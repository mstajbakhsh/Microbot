/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package helpers;

import java.util.Vector;

/**
 *
 * @author Mir Saman Tajbakhsh
 */
public class WebDocument {

    private String url = "";
    private String outputName = "";

    private Vector<String> links = new Vector<>();
    private int Counter = -1; //Used for indexing of links

    /**
     * Dummy constructor. You may use setURL() or setOutputName() after
     * constructor,
     */
    public WebDocument() {
    }

    /**
     * This constructor sets the URL and the output filename of HTTP response.
     *
     * @param URL The URL of the web resource to be downloaded. (Ex.
     * http://mstajbakhsh.ir or
     * http://mstajbakhsh.ir<b>Separator</b>http://mstajbakhsh.ir/CV.pdf [{@link Variables#inputFileLinksSeparator])
     * @param outputName The output file name of HTTP response (Ex.
     * mstajbakhsh.ir.html)
     */
    public WebDocument(String URL, String outputName) {
        setUrl(URL);
        setOutputName(outputName);
    }

    /**
     * This method will return the next URL. This method should call before
     * {@link WebDocument#getNextReferrer()}.
     *
     * @return The next URL in the list or null if finished. (null value will
     * reset counter and again will return the first element).
     */
    public String getNextUrl() {
        try {
            return links.get(++Counter);
        } catch (IndexOutOfBoundsException ex) {
            Counter = -1;
            return null;
        }
    }

    /**
     * This method will use former link (in the list) as the referrer of current
     * link. So the first URL does not have any referrer but others, have. This
     * method should call after {@link WebDocument#getNextUrl()}.
     *
     * @return The referrer for current URL or null for first URL.
     */
    public String getNextReferrer() {
        if (Counter == 0) {
            return null;
        } else if (Counter - 1 < links.size()) {
            return links.get(Counter - 1);
        } else { //This case should not happen.
            Variables.logger.Log(WebDocument.class, Variables.LogType.Error, "[!] The code should not reach here. Please debug the code.");
            return null;
        }
    }

    /**
     * This method will reset counter and makes the URLs retrieved from first
     * item. This is semi dummy method and is used only in
     * {@link Methods#makeLinksLogs()}
     */
    public void resetCounter() {
        Counter = 0;
    }

    /**
     * This method will clear the list of links and set it to new URLs separated
     * by {@link Variables#inputFileLinksSeparator}
     *
     * @param url The list of URLs in the format of (Ex. http://mstajbakhsh.ir
     * or http://mstajbakhsh.ir<b>Separator</b>http://mstajbakhsh.ir/CV.pdf [{@link Variables#inputFileLinksSeparator])
     */
    public void setUrl(String url) {

        this.links.clear();

        String[] links = url.split(Variables.inputFileLinksSeparator);

        for (String link : links) {
            this.links.add(link);
        }
    }

    public String getOutputName() {
        return outputName;
    }

    public void setOutputName(String outputName) {
        this.outputName = outputName;
    }

    /**
     * This method will return the count of referers'.
     * @return The count of referes'.
     */
    public int getRefererCount() {
        return links.size() - 1; //Referer is total linnks minus one because the first link does not have referer.
    }

}
