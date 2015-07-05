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
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author Mir Saman Tajbakhsh
 */
public class Fetcher implements Runnable {

    private Thread t;
    private String name;

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
        if (t != null && !t.isAlive()) {
            t.start();
            if (Variables.debug) {
                Variables.logger.Log(Fetcher.class, Variables.LogType.Error, "Fetcher (" + Methods.Colorize(name, Methods.Color.Blue) + ")");
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
        while (link != null) {
            //Start fetching ...

            if (Variables.debug && Variables.vv) {
                Variables.logger.Log(Fetcher.class, Variables.LogType.Trace, "Fetcher (" + Methods.Colorize(name, Methods.Color.Green) + ") start getting " + link.getUrl());
            }

            try {
                if (Variables.anonymizerProxyType == Variables.AnonymizerProxy.NONE) {
                    connection = (HttpURLConnection) new URL(link.getUrl()).openConnection();
                } else {
                    connection = (HttpURLConnection) new URL(link.getUrl()).openConnection(p);
                }
                connection.setDoOutput(true);
                connection.setDoInput(true);
                connection.setRequestProperty("User-Agent", Methods.getRandomUserAgent());
                connection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
                connection.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
                connection.setRequestProperty("Accept-Encoding", "gzip, deflated");

                if (!(Variables.Cookie == null || Variables.Cookie.equalsIgnoreCase(""))) {
                    connection.setRequestProperty("Cookie", Variables.Cookie);
                }

                connection.setRequestMethod("GET");

                connection.connect();

                if (connection.getResponseCode() == 200) {
                    //Write to file
                    String outputName = Variables.outputDirectory + link.getOutputName();

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
                    //TODO Add errors in a separate list.
                    if (Variables.debug) {
                        Variables.logger.Log(Fetcher.class, Variables.LogType.Error, "Server responded (" + Methods.Colorize(connection.getResponseCode() + " - " + connection.getResponseMessage(), Methods.Color.Red) + ") for " + link.getUrl());
                    }
                }

                if (Variables.debug && Variables.vv) {
                    Variables.logger.Log(Fetcher.class, Variables.LogType.Info, "[+] Done fetching (" + Methods.Colorize(link.getUrl(), Methods.Color.Red) + "]");
                }

                //Close the connection
                connection.disconnect();

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

                //Check size limit and report progress ...
                
                Methods.checkFinished();
                
                long size = Methods.getFolderSize(Variables.outputDirectory);
                if (size >= Variables.outputSizeLimit) {
                    //Release for starting compressor ...
                    Variables.startCompress.release();

                    //Block itself
                    try {
                        Thread.currentThread().wait();
                    } catch (InterruptedException ex) {
                        if (Variables.debug) {
                            Variables.logger.Log(Fetcher.class, Variables.LogType.Error, "Error in waiting thread [" + name + "]. Details:\r\n" + ex.getMessage());
                        }
                    }
                }

            } catch (IOException ex) {
                if (Variables.debug) {
                    if (Variables.vv) {
                        Variables.logger.Log(Fetcher.class, Variables.LogType.Error, "Error in fetching [" + Methods.Colorize(link.getUrl(), Methods.Color.Red) + "] in fetcher (" + Methods.Colorize(name, Methods.Color.Yellow) + ") for writing in (" + Methods.Colorize(link.getOutputName(), Methods.Color.White) + "). Detail:\r\n" + ex.getMessage());
                    } else {
                        Variables.logger.Log(Fetcher.class, Variables.LogType.Error, "Error in fetching [" + Methods.Colorize(link.getUrl(), Methods.Color.Red) + "]");
                    }
                }
            }

            link = Methods.getNextProfileLink();
        }

        //URLs done. This thread finishes its work.
        if (Variables.debug) {
            Variables.logger.Log(Fetcher.class, Variables.LogType.Info, "Fetcher (" + Methods.Colorize(name, Methods.Color.Green) + ") finished its work.");
        }

    }
    
    public Thread getThread() {
        return t;
    }

}
