/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package microbot;

import helpers.Methods;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import helpers.Variables;
import helpers.WebDocument;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.Vector;

/**
 *
 * @author Mir Saman Tajbakhsh
 */
public class Microbot {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        Variables.state = Variables.microbotState.Initializing;
        readConfiguration();
        fillConfiguration();

        //TODO Add error checker
        //Add a boolean and check if any exception occured in configuration?
        Variables.state = Variables.microbotState.Starting;
        //Start threads
        Variables.threadController.Start();

        //Set shut down hook
        setShutDownHook();
    }

    /**
     * This function will read the configuration file and set the
     * {@link Variables}.
     */
    private static void readConfiguration() {
        //TODO Exception handle while reading and setting.
        //Specially integer and enumration values.

        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream("config.properties"));

            //
            // Anonymizer
            //
            if (properties.containsKey("Anonymizer")) {
                Variables.anonymizerNetwork = Variables.Anonymizer.valueOf(properties.getProperty("Anonymizer", Variables.Anonymizer.TOR.name()));
            }
            if (properties.containsKey("AnonymizerProxyType")) {
                Variables.anonymizerProxyType = Variables.AnonymizerProxy.valueOf(properties.getProperty("AnonymizerProxyType", "SOCKS"));
            }
            if (properties.containsKey("AnonymizerIP")) {
                Variables.anonymizerIP = properties.getProperty("AnonymizerIP", "localhost");
            }
            if (properties.containsKey("AnonymizerPort")) {
                Variables.anonymizerPort = Integer.valueOf(properties.getProperty("AnonymizerPort", "9150"));
            }

            //
            // Web Requests
            //
            if (properties.containsKey("AcceptSelfSignedCertificates")) {
                Variables.acceptAllCerts = Boolean.getBoolean(properties.getProperty("AcceptSelfSignedCertificates", "true"));
            }
            if (properties.containsKey("ThreadCount")) {
                Variables.threadCount = Integer.parseInt(properties.getProperty("ThreadCount", "5"));
            }
            if (properties.containsKey("MaxSleep")) {
                Variables.maxSleep = Integer.parseInt(properties.getProperty("MaxSleep", "10"));
            }
            if (properties.containsKey("MinSleep")) {
                Variables.minSleep = Integer.parseInt(properties.getProperty("MinSleep", "5"));
            }
            if (properties.containsKey("RandomUserAgent")) {
                Variables.randomUA = Boolean.valueOf(properties.getProperty("RandomUserAgent", "true"));
            }
            if (properties.containsKey("UserAgentListFile")) {
                Variables.UAFile = properties.getProperty("UserAgentListFile", "UA.txt");
            }
            if (properties.containsKey("Cookie")) {
                Variables.Cookie = properties.getProperty("Cookie", "");
            }

            //
            // Load and Store
            //
            //Load
            if (properties.containsKey("InputFile")) {
                Variables.inputFile = properties.getProperty("InputFile", "profiles.csv");
            }
            if (properties.containsKey("MainURLColumnName")) {
                Variables.inputFileLinksColumnName = properties.getProperty("MainURLColumnName", "");
            }
            if (properties.containsKey("OutputFileColumnName")) {
                Variables.inputFileOutputFileName = properties.getProperty("OutputFileColumnName", "");
            }
            if (properties.containsKey("LinksSeparator")) {
                Variables.inputFileLinksSeparator = properties.getProperty("LinksSeparator", "");
            }
            //Store
            if (properties.containsKey("OutputDirectory")) {
                Variables.outputDirectory = properties.getProperty("OutputDirectory", "." + java.io.File.pathSeparator);
                Variables.outputDirectory = Methods.checkDirectory(Variables.outputDirectory);
            }
            //Compress
            if (properties.containsKey("OutputLimit")) {
                Variables.outputSizeLimit = Methods.filesizeToBytes(properties.getProperty("OutputLimit", "500MB"));
            }
            if (properties.containsKey("DeleteAfterCompress")) {
                Variables.deleteAfterCompress = Boolean.valueOf(properties.getProperty("DeleteAfterCompress", "true"));
            }
            if (properties.containsKey("CompressorType")) {
                Variables.compressType = Variables.CompressType.valueOf(properties.getProperty("CompressorType", Variables.CompressType.ZIP.name()));
            }

            //
            // Debug
            //
            if (properties.containsKey("Debug")) {
                Variables.debug = Boolean.valueOf(properties.getProperty("Debug", "false"));
            }
            if (properties.containsKey("VeryVerbos")) {
                Variables.vv = Boolean.valueOf(properties.getProperty("VeryVerbos", "false"));
            }
            //Done reading ...
            Variables.logger.Log(Microbot.class, Variables.LogType.Info, "Config file read " + Methods.Colorize("successfully", Methods.Color.Green) + ".");

            if (Variables.vv) {
                //TODO print value of each variable.
            }

            //Post Configuration Checker:
            //Web Requests
            //Check min time and max time
            if (Variables.maxSleep <= Variables.minSleep) {
                if (Variables.debug) {
                    Variables.logger.Log(Microbot.class, Variables.LogType.Warning, "Max time is greater than min time. Setting both to min time ...");
                }

                Variables.maxSleep = Variables.minSleep;

            }

        } catch (IOException e) {
            if (e instanceof FileNotFoundException) {
                //Create config file and try again
                Variables.logger.Log(Microbot.class, Variables.LogType.Warning, "Config file not found. " + Methods.Colorize("Create a new one.", Methods.Color.White));
                writeConfiguration();
                Variables.logger.Log(Microbot.class, Variables.LogType.Info, "Config file written successfully. Retrying ...");
                readConfiguration();
            }
        }
    }

    /**
     * This method will create default configuration file
     * {@code properties.config}.
     */
    private static void writeConfiguration() {
        Properties prop = new Properties();
        OutputStream output = null;

        try {

            output = new FileOutputStream("config.properties");

            //Anonymizer
            prop.setProperty("Anonymizer", Variables.anonymizerNetwork.name());
            prop.setProperty("AnonymizerProxyType", Variables.anonymizerProxyType.name());
            prop.setProperty("AnonymizerIP", Variables.anonymizerIP);
            prop.setProperty("AnonymizerPort", String.valueOf(Variables.anonymizerPort));

            //Web Requests
            prop.setProperty("AcceptSelfSignedCertificates", String.valueOf(Variables.acceptAllCerts));
            prop.setProperty("ThreadCount", String.valueOf(Variables.threadCount));
            prop.setProperty("MaxSleep", String.valueOf(Variables.maxSleep));
            prop.setProperty("MinSleep", String.valueOf(Variables.minSleep));
            prop.setProperty("RandomUserAgent", String.valueOf(Variables.randomUA));
            prop.setProperty("UserAgentListFile", Variables.UAFile);
            prop.setProperty("Cookie", Variables.Cookie);

            //Load and Store
            prop.setProperty("InputFile", Variables.inputFile);
            prop.setProperty("MainURLColumnName", Variables.inputFileLinksColumnName);
            prop.setProperty("LinksSeparator", Variables.inputFileLinksSeparator);
            prop.setProperty("OutputFileColumnName", Variables.inputFileOutputFileName);
            prop.setProperty("OutputDirectory", Variables.outputDirectory);
            prop.setProperty("OutputLimit", Methods.filesizeToHumanReadable(Variables.outputSizeLimit, true));
            prop.setProperty("DeleteAfterCompress", String.valueOf(Variables.deleteAfterCompress));

            //Debug
            prop.setProperty("Debug", String.valueOf(Variables.debug));
            prop.setProperty("VeryVerbos", String.valueOf(Variables.vv));

            // save properties to project root folder
            prop.store(output, "Microbot Configuration");
        } catch (IOException e) {
            Variables.logger.Log(Microbot.class, Variables.LogType.Error, "Error in writing configuration file. Detail:\r\n" + Methods.Colorize(e.getMessage(), Methods.Color.Red));
            System.exit(-1);
        }
    }

    /**
     * This method will fill necessary information into RAM. Variables are
     * stored in {@link Variables}
     */
    private static void fillConfiguration() {
        try {
            String line = "";
            String[] items = null;
            int URLIndex = 0;
            int OutputIndex = 0;
            BufferedReader br = null;
            Set<WebDocument> dummySet = new LinkedHashSet<WebDocument>();

            //Read URLs (and outputs):
            br = new BufferedReader(new FileReader(Variables.inputFile));

            if (Variables.debug) {
                Variables.logger.Log(Microbot.class, Variables.LogType.Info, "[+] Start reading input file (profiles) ...");
            }

            line = br.readLine(); //First line has column names
            items = line.split(",");
            URLIndex = Arrays.asList(items).indexOf(Variables.inputFileLinksColumnName);
            OutputIndex = Arrays.asList(items).indexOf(Variables.inputFileOutputFileName);

            if (URLIndex == -1) {
                Variables.logger.Log(Microbot.class, Variables.LogType.Error, Methods.Colorize(Variables.inputFileLinksColumnName, Methods.Color.Red) + " not found in the input file [" + Methods.Colorize(Variables.inputFile, Methods.Color.Green));
                System.exit(-2);
            } else if (OutputIndex == -1) {
                Variables.logger.Log(Microbot.class, Variables.LogType.Error, Methods.Colorize(Variables.inputFileLinksColumnName, Methods.Color.Red) + " not found in the input file [" + Methods.Colorize(Variables.inputFile, Methods.Color.Green));
                System.exit(-3);
            } else {
                line = br.readLine();

                while (line != null) {
                    WebDocument doc = new WebDocument(line.split(",")[URLIndex], line.split(",")[OutputIndex]);
                    if (doc.getOutputName().endsWith("html") || doc.getOutputName().endsWith("htm")) {
                        //Variables.links.add(doc);
                        dummySet.add(doc);
                    }
                    line = br.readLine();
                }

                //Fill the main Vector:
                /*
                List<WebDocument> dummyList = new ArrayList();
                 dummyList.addAll(dummySet);
            
                 dummySet.clear();
            
                 Collections.reverse(dummyList);
                 */
                Variables.links = new Vector<WebDocument>(dummySet);

                //Clear RAM:
                dummySet.clear();
                dummySet = null;
                System.gc();

                if (Variables.links.isEmpty()) {
                    Variables.logger.Log(Microbot.class, Variables.LogType.Warning, "No web document added. Please check CSV file structure and check if the output file names ends with " + Methods.Colorize("html or htm", Methods.Color.Cyan) + ".");
                }

                if (Variables.debug) {
                    Variables.logger.Log(Microbot.class, Variables.LogType.Info, "[+] Done reading input file (profiles)");
                }
            }

            //Read UAs:
            if (Variables.debug) {
                Variables.logger.Log(Microbot.class, Variables.LogType.Info, "[+] Start reading user agents list ...");
            }

            br = new BufferedReader(new FileReader(Variables.UAFile));
            line = br.readLine();

            while (line != null) {
                Variables.UAs.add(line);
                line = br.readLine();
            }

            if (Variables.debug) {
                Variables.logger.Log(Microbot.class, Variables.LogType.Info, "[+] Done reading user agents list.");
            }

            //Logger
            Variables.successLogsFile.mkdir(); //Create directories
            Variables.errorLogsFile.mkdir();

            Variables.successLogsFile = new File("LOGS" + File.separator + "success.log"); //Create files
            Variables.errorLogsFile = new File("LOGS" + File.separator + "error.log");

            Variables.successFileWriter = new FileWriter(Variables.successLogsFile, true); //Access files
            Variables.errorFileWriter = new FileWriter(Variables.errorLogsFile, true);

            System.gc();

        } catch (FileNotFoundException ex) {
            Variables.logger.Log(Microbot.class, Variables.LogType.Error, "Input file not found.\r\nDetails:\r\n" + Methods.Colorize(ex.getMessage(), Methods.Color.Red) + "\r\n");
        } catch (IOException ex) {
            Variables.logger.Log(Microbot.class, Variables.LogType.Error, "Error in reading file.\r\nDetails:\r\n" + Methods.Colorize(ex.getMessage(), Methods.Color.Red) + "\r\n");
        }
    }

    /**
     * This method will handle when user presses {@code CTRL + C}
     */
    private static void setShutDownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                System.out.println("Cleaning up ...");
                Variables.threadController.Stop();
                Variables.threadController.changeActiveThreads(false, null, Variables.microbotState.CleanUp);
            }
        });
    }

}

/*
 Errors:
 -1      Error in writing configuration
 -2      Error in finding column name of input CSV file.
 -3      Error in finding output name of input CSV file.
 */
