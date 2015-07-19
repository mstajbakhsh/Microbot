/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package helpers;

import Fetcher.Controller;
import java.io.File;
import java.io.FileWriter;
import java.util.Vector;
import java.util.concurrent.Semaphore;

/**
 *
 * @author Mir Saman Tajbakhsh
 */
public class Variables {

    //<editor-fold desc="Anonimizer Settings" defaultstate="expanded">
    //
    public enum Anonymizer {

        TOR, I2P
    }

    public enum AnonymizerProxy {

        SOCKS, HTTP, DIRECT, NONE
    }

    public static Anonymizer anonymizerNetwork = Anonymizer.TOR;
    public static AnonymizerProxy anonymizerProxyType = AnonymizerProxy.SOCKS;
    public static String anonymizerIP = "localhost";
    public static int anonymizerPort = 9150;
    //</editor-fold>

    //<editor-fold defaultstate="expanded" desc="Web Requests">
    //
    // Requests
    public static boolean acceptAllCerts = true;
    // Thread
    public static int threadCount = 5;
    public static Controller threadController = new Controller();

    public enum microbotState {

        Fetching, Compressing, Initializing, Starting, Stopping, Finished, Dummy
    }
    
    public static microbotState state = microbotState.Dummy;

    // Sleep
    public static int maxSleep = 10;
    public static int minSleep = 5;

    // User Agent
    public static boolean randomUA = true; //If false, it will use the first UA of UAPath.
    public static String UAFile = "UA.txt";
    public static Vector<String> UAs = new Vector<String>();

    // Cookie
    public static String Cookie = "";

    //</editor-fold>

    //<editor-fold defaultstate="expanded" desc="Load and Store Content">
    // Load and Store
    //
    //Load
    public static String inputFile = "profiles.csv";
    public static String inputFileLinksColumnName = "url";
    public static String inputFileOutputFileName = "profile";
    public static String inputFileLinksSeparator = "###"; //Separator of links
    public static Vector<WebDocument> links = new Vector<>();

    //Store
    public static String outputDirectory = "./";
    public static Semaphore startMakeLogs = null; // It will initialize in fillConfiguration.    

    //Compress
    public enum CompressType {

        TAR, ZIP, RAR, GZIP, NONE
    };
    public static Compressor compressor = new Compressor();

    public static long outputSizeLimit = Methods.filesizeToBytes("500MB");
    public static boolean deleteAfterCompress = true;
    public static CompressType compressType = CompressType.ZIP;
    
    //public static Semaphore startCompress = null; // It will initialize in fillConfiguration.

    //</editor-fold>
    //<editor-fold defaultstate="expanded" desc="Logging">
    // Logging
    //
    public enum LogType {

        Debug, Error, Warning, Trace, Info
    };

    public static Logger logger = new Logger();
    public static File successLogsFile = new File("LOGS" + File.separator); //Initialize in fillConfiguration
    public static File errorLogsFile = new File("LOGS" + File.separator); //Initialize in fillConfiguration
    public static FileWriter successFileWriter = null; //Initialize in fillConfiguration
    public static FileWriter errorFileWriter = null; //Initialize in fillConfiguration

    public static boolean debug = false; //prints debug information
    public static boolean vv = false; //prints debug information very verbos
    //</editor-fold>

}
