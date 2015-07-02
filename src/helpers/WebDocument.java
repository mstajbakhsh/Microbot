/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package helpers;

/**
 *
 * @author Mir Saman Tajbakhsh
 */
public class WebDocument {
    
    String url = "";
    String outputName = "";

    /**
     * Dummy constructor. You may use setURL() or setOutputName() after constructor,
     */
    public WebDocument() {
    }
    
    /**
     * This constructor sets the URL and the output filename of HTTP response.
     * @param URL The URL of the web resource to be downloaded. (Ex. http://mstajbakhsh.ir)
     * @param outputName The output file name of HTTP response (Ex. mstajbakhsh.ir.html)
     */
    public WebDocument(String URL, String outputName) {
        setUrl(URL);
        setOutputName(outputName);
    }
    
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getOutputName() {
        return outputName;
    }

    public void setOutputName(String outputName) {
        this.outputName = outputName;
    }
    
}
