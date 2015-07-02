/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Fetcher;

import helpers.Variables;
import helpers.WebDocument;
import java.io.FileWriter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;

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
            t.run();
            if (Variables.debug) {
                Variables.logger.Log(Fetcher.class, Variables.LogType.Error, "Fetcher (" + Variables.ANSI_BLUE + name + Variables.ANSI_RESET + ")");
            }
        } else {
            Variables.logger.Log(Fetcher.class, Variables.LogType.Error, "Failed to start fetcher (" + Variables.ANSI_BLUE + name + Variables.ANSI_RESET + ")");
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

        while (!t.isInterrupted()) {
            link = Variables.getNextProfileLink();

            if (link == null) { //The links are finished!
                t.interrupt();
            } else {
                //Start fetching ...

                if (Variables.debug && Variables.vv) {
                    Variables.logger.Log(Fetcher.class, Variables.LogType.Trace, "Fetcher (" + Variables.ANSI_GREEN + name + Variables.ANSI_RESET + ") start getting " + link.getUrl());
                }

                try {
                    if (Variables.anonymizerProxyType != Variables.AnonymizerProxy.NONE) {
                        connection = (HttpURLConnection) new URL(link.getUrl()).openConnection();
                    } else {
                        connection = (HttpURLConnection) new URL(link.getUrl()).openConnection(p);
                    }
                    connection.setDoOutput(true);
                    connection.setDoInput(true);
                    connection.setRequestProperty("User-Agent", Variables.getRandomUserAgent());
                    connection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
                    connection.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
                    connection.setRequestProperty("Accept-Encoding", "gzip, deflated");

                    if (Variables.Cookie != "") {
                        connection.setRequestProperty("Cookie", Variables.Cookie);
                    }

                    connection.setRequestMethod("GET");

                    connection.connect();

                    if (connection.getResponseCode() == 200) {
                        //Write to file
                        String outputName = Variables.outputDirector + link.getOutputName();

                        //Check extension
                        if (!outputName.endsWith("html") || !outputName.endsWith("htm")) {
                            outputName += "html";
                        }

                        FileWriter fw = new FileWriter(outputName);
                        fw.write(String.valueOf(connection.getContent()));
                        fw.flush();
                        fw.close();
                    }
                    
                    if (Variables.debug && Variables.vv) {
                        Variables.logger.Log(Fetcher.class, Variables.LogType.Info, "[+] Done fetching (" + Variables.ANSI_RED + link.getUrl() + Variables.ANSI_RESET + "]");
                    }

                    //Close the connection
                    connection.disconnect();
                    
                    
                    
                } catch (IOException ex) {
                    if (Variables.debug) {
                        if (Variables.vv) {
                            Variables.logger.Log(Fetcher.class, Variables.LogType.Error, "Error in fetching [" + Variables.ANSI_RED + link.getUrl() + Variables.ANSI_RESET + "] in fetcher (" + Variables.ANSI_YELLOW + name + Variables.ANSI_RESET + ") for writing in (" + Variables.ANSI_WHITE + link.getOutputName() + Variables.ANSI_RESET + "). Detail:\r\n" + ex.getMessage());
                        } else {
                            Variables.logger.Log(Fetcher.class, Variables.LogType.Error, "Error in fetching [" + Variables.ANSI_RED + link.getUrl() + Variables.ANSI_RESET + "]");
                        }
                    }
                }
            }
        }

        //URLs done. This thread finishes its work.
        if (Variables.debug) {
            Variables.logger.Log(Fetcher.class, Variables.LogType.Info, "Fetcher (" + Variables.ANSI_GREEN + name + Variables.ANSI_RESET + ") finished its work.");
        }

    }

}
