/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Fetcher;

import helpers.Methods;
import helpers.Variables;
import helpers.WebDocument;
import java.io.FileWriter;
import java.io.IOException;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;
import javax.net.ssl.HttpsURLConnection;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author Mir Saman Tajbakhsh
 */
public class Fetcher implements Runnable {

    private Thread t;
    private String name;
    private boolean isWorking = false;
    
    private CookieManager cookies = new CookieManager();

    /**
     * Creates a new instance of fetcher without identifier.
     */
    public Fetcher() {
        //Do nothing
        t = new Thread(this);
        name = "null";
    }

    /**
     * Creates a new instance of fetcher.
     *
     * @param ID The identifier (and name) for this thread.
     */
    public Fetcher(int ID) {
        name = String.valueOf(ID);
        t = new Thread(this, name);
    }

    public void startFetching() {
        if (t != null && !t.isAlive() && !isWorking) {
            isWorking = true;
            t.start();
            if (Variables.debug) {
                Variables.logger.Log(Fetcher.class, Variables.LogType.Info, "Fetcher (" + Methods.Colorize(name, Methods.Color.Blue) + ")");
            }
        } else {
            Variables.logger.Log(Fetcher.class, Variables.LogType.Error, "Failed to start fetcher (" + Methods.Colorize(name, Methods.Color.Blue) + ")");
        }
    }

    @Deprecated
    @Override
    /**
     * run() is deprecated. Use startFetching() instead.
     */
    public void run() {
        WebDocument link = null;
        HttpURLConnection connection;
        Proxy p;

        //PreConnfiguration
        //Configure proxy
        //TODO Anonymizer is deprecated. Use in following for warning generation.
        switch (Variables.anonymizerProxyType) {
            case DIRECT:
                p = new Proxy(Proxy.Type.DIRECT, new InetSocketAddress(Variables.anonymizerIP, Variables.anonymizerPort));
                break;
            case HTTP:
                p = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(Variables.anonymizerIP, Variables.anonymizerPort));
                break;
            case SOCKS:
                p = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(Variables.anonymizerIP, Variables.anonymizerPort));
                break;
            case NONE:
            default:
                p = null;
                break;
        }

        link = Methods.getNextProfileLink();
        while (link != null && isWorking) {
            //Start fetching ...

            String URL = link.getNextUrl();
            String UA = Methods.getRandomUserAgent(); //Use this UA for refererd or single links.

            //loop for referer
            for (int i = 0; i <= link.getRefererCount(); URL = link.getNextUrl(), i++) {

                if (Variables.debug && Variables.vv) {
                    Variables.logger.Log(Fetcher.class, Variables.LogType.Trace, "Fetcher (" + Methods.Colorize(name, Methods.Color.Green) + ") start getting " + URL);
                }

                try {

                    //Anonymizer
                    if (Variables.anonymizerProxyType == Variables.AnonymizerProxy.NONE) {
                        connection = (HttpURLConnection) new URL(URL).openConnection();
                    } else {
                        connection = (HttpURLConnection) new URL(URL).openConnection(p);
                    }

                    connection.setDoOutput(true);
                    connection.setDoInput(true);
                    connection.setRequestProperty("User-Agent", UA);
                    connection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
                    connection.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
                    connection.setRequestProperty("Accept-Encoding", "gzip, deflated");

                    String referer = link.getNextReferrer();
                    if (referer != null) {
                        connection.setRequestProperty("Referer", referer);
                        referer = null;
                        System.gc();
                    }

                    //Send Cookie using user input
                    if (!(Variables.Cookie == null || Variables.Cookie.equalsIgnoreCase(""))) {
                        connection.setRequestProperty("Cookie", Variables.Cookie);
                    } else if (cookies.getCookieStore().getCookies().size() > 0) { //From referer, there are some cookies
                        connection.setRequestProperty("Cookie", Join(",", cookies.getCookieStore().getCookies()));
                    }

                    connection.setRequestMethod("GET");

                    connection.connect();

                    //Get Cookie from response
                    getCookies(connection);

                    if (connection.getResponseCode() == 200) {
                        //Write to file
                        String outputName = Variables.outputDirectory + link.getOutputName().substring(0, link.getOutputName().lastIndexOf(".")) + i + link.getOutputName().substring(link.getOutputName().lastIndexOf("."));

                        //Check extension
                        if (!(outputName.endsWith("html") || outputName.endsWith("htm"))) {
                            outputName += "html";
                        }

                        //get content
                        String html = "";
                        if (connection.getContentEncoding().equalsIgnoreCase("gzip")) {
                            html = IOUtils.toString(new GZIPInputStream(connection.getInputStream()));
                        } else if (connection.getContentEncoding().equalsIgnoreCase("deflate")) {
                            html = IOUtils.toString(new InflaterInputStream(connection.getInputStream()));
                        }

                        FileWriter fw = new FileWriter(outputName);
                        fw.write(html);
                        fw.flush();
                        fw.close();
                    } else { //The returned code is not 200.
                        if (Variables.debug) {
                            Variables.logger.Log(Fetcher.class, Variables.LogType.Error, "Fetcher could not download (" + Methods.Colorize(URL, Methods.Color.Red) + ") in " + name);
                            if (Variables.vv) {
                                Variables.logger.Log(Fetcher.class, Variables.LogType.Error, "Server responded (" + Methods.Colorize(connection.getResponseCode() + " - " + connection.getResponseMessage(), Methods.Color.Red) + ") for " + URL);
                            }
                        }
                    }

                    //Close the connection
                    connection.disconnect();

                    //Report progress
                    Variables.logger.logResult(connection, link);
                    Methods.oneFinished();

                    if (Variables.debug && Variables.vv) {
                        Variables.logger.Log(Fetcher.class, Variables.LogType.Info, "[+] Done fetching (" + Methods.Colorize(URL, Methods.Color.Red) + "]");
                    }

                    try {
                        synchronized (t) {
                            t.wait(Methods.getNextRandom() * 1000);
                        }
                    } catch (InterruptedException ex) {
                        if (Variables.debug) {
                            Variables.logger.Log(Fetcher.class, Variables.LogType.Error, "Cannot interrupt thread [" + Methods.Colorize(name, Methods.Color.Red) + "]. Interrupted before!");
                        }
                    } catch (IllegalArgumentException ex) {
                        if (Variables.debug) {
                            Variables.logger.Log(Fetcher.class, Variables.LogType.Error, "-1 is returned as random number for thread [" + Methods.Colorize(name, Methods.Color.Red) + "].");
                        }
                    }
                } catch (IOException ex) {
                    if (Variables.debug) {
                        if (Variables.vv) {
                            Variables.logger.Log(Fetcher.class, Variables.LogType.Error, "Error in fetching [" + Methods.Colorize(URL, Methods.Color.Red) + "] in fetcher (" + Methods.Colorize(name, Methods.Color.Yellow) + ") for writing in (" + Methods.Colorize(link.getOutputName(), Methods.Color.White) + "). Detail:\r\n" + ex.getMessage());
                        } else {
                            Variables.logger.Log(Fetcher.class, Variables.LogType.Error, "Error in fetching [" + Methods.Colorize(URL, Methods.Color.Red) + "]");
                        }
                    }
                }
            }

            //Check size limit and compress ...
            long size = Methods.getFolderSize(Variables.outputDirectory);
            if (size >= Variables.outputSizeLimit) {
                //Deactivate itself by waiting ...
                Variables.state = Variables.microbotState.Compressing;
                Variables.threadController.changeActiveThreads(false, t, Variables.microbotState.Compressing);
            }

            //Check if user terminated program or not
            if (isWorking) {
                link = Methods.getNextProfileLink();
            }
        }
        
        //Thread finished. (Normally or by force)
        Variables.state = Variables.microbotState.Stopping;
        Variables.threadController.changeActiveThreads(false, t, Variables.microbotState.Stopping);

        //URLs done. This thread finishes its work.
        if (Variables.debug) {
            Variables.logger.Log(Fetcher.class, Variables.LogType.Info, "Fetcher (" + Methods.Colorize(name, Methods.Color.Green) + ") finished its work.");
        }

    }

    public Thread getThread() {
        return t;
    }

    /**
     * This method will extract cookies from {@code connection}
     * ({@link HttpURLConnection}).
     *
     * @param connection The instance of {@link HttpsURLConnection} which is
     * connected and contains cookie field.
     */
    private void getCookies(HttpURLConnection connection) {
        Map<String, List<String>> headerFields = connection.getHeaderFields();
        List<String> cookiesHeader = headerFields.get("Set-Cookie");

        if (cookiesHeader != null) {
            for (String cookie : cookiesHeader) {
                cookies.getCookieStore().add(null, HttpCookie.parse(cookie).get(0));
            }
        }
    }
    
    /**
     * This method will ask the thread to destroy itself
     */
    public void Stop() {
        isWorking = false;
    }

    /**
     * This method is very similar to {@code android.text.TextUtils}. Joins with
     * the delimeter.
     *
     * @param delimmeter The character between elements. Usually is ,
     * @param cookies The list of cookies to be join.
     * @return The joined string of cookies.
     */
    private String Join(String delimmeter, List<HttpCookie> cookies) {
        String output = "";

        for (HttpCookie cookie : cookies) {
            output += cookie.toString() + delimmeter;
        }

        if (output.endsWith(delimmeter)) {
            output = output.substring(0, output.lastIndexOf(delimmeter));
        }

        return output;
    }

}
