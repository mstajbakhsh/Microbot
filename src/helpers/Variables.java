/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package helpers;

import Fetcher.Controller;
import java.util.HashSet;
import java.util.Random;
import java.util.Vector;

/**
 *
 * @author Mir Saman Tajbakhsh
 */
public class Variables {

    //<editor-fold desc="Anonimizer Settings" defaultstate="expanded">
    //
    public enum Anonymizer {

        TOR, I2P
    };

    public enum AnonymizerProxy {

        SOCKS, HTTP, DIRECT, NONE
    }

    public static Anonymizer anonymizerNetwork = Anonymizer.TOR;
    public static AnonymizerProxy anonymizerProxyType = AnonymizerProxy.SOCKS;
    public static String anonymizerIP = "localhost";
    public static int anonymizerPort = 9150;
    //</editor-fold>

    //<editor-fold defaultstate="expanded" desc="Web Requests">
    // Requests
    //
    // Thread
    public static int threadCount = 5;
    public static Controller threadController = new Controller();
    
    // Sleep
    public static int maxSleep = 10;
    public static int minSleep = 5;

    // User Agent
    public static boolean randomUA = true; //If false, it will use the first UA of UAPath.
    public static String UAFile = "UA.txt";
    public static Vector<String> UAs = new Vector<String>();

    // Cookie
    public static String Cookie = "";

    // Randomize Date
    public static boolean randomizeDate = true;
    //</editor-fold>

    //<editor-fold defaultstate="expanded" desc="Load and Store Content">
    // Load and Store
    //
    //Load
    public static String inputFile = "profiles.csv";
    public static String inputFileLinksColumnName = "url";
    public static String inputFileOutputFileName = "profile";
    public static HashSet<WebDocument> links = new HashSet<WebDocument>();

    //Store
    public static String outputDirector = "./";
    
    //Compress
    //TODO Add compress configuration and size limit
    //</editor-fold>

    //<editor-fold defaultstate="expanded" desc="Logging">
    // Logging
    //
    public enum LogType {

        Debug, Error, Warning, Trace, Info
    };

    public static Logger logger = new Logger();

    public static boolean debug = false; //prints debug information
    public static boolean vv = false; //prints debug information very verbos

    // Color
    //TODO Make a colorized method.
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";
    //</editor-fold>

    private static int profileCounter = 0;
    private static Random r = new Random();

    public static synchronized WebDocument getNextProfileLink() {
        //TODO Check if it's correctly working or not.
        if (links.iterator().hasNext()) {
            return (links.iterator().next());
        } else {
            return null;
        }
    }

    public static String getRandomUserAgent() {
        if (Variables.randomUA) {
            //TODO Check if its return true or not.
            //if UAs.size() correct or +1?
            return (UAs.elementAt(r.nextInt(UAs.size())));
        } else {
            return (UAs.elementAt(0));
        }
    }
    
    /**
     * This method will run after each thread finished its work.
     * If size limit enabled in configuration, it will tar and delete the main files.
     */
    public static synchronized void checkFinished() {
        
    }
}
