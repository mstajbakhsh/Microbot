/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package helpers;

import Fetcher.Controller;
import java.util.HashSet;
import java.util.Set;
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
    public static Vector<WebDocument> links = new Vector<WebDocument>();

    //Store
    public static String outputDirectory = "./";

    //Compress
    public enum CompressType {TAR, ZIP, RAR, GZIP};
    public static Compressor compressor = new Compressor();
    
    public static long outputSizeLimit = Methods.filesizeToBytes("500MB");
    public static boolean deleteAfterCompress = true;
    public static CompressType compressType = CompressType.ZIP;
    
    
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
    //</editor-fold>

}
