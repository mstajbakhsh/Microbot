/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package microbot;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import helpers.Variables;
import helpers.WebDocument;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStream;
import java.util.Arrays;

/**
 *
 * @author Mir Saman Tajbakhsh
 */
public class Microbot {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        readConfiguration();
        fillConfiguration();

        //TODO Add error checker
        //Add a boolean and check if any exception occured in configuration?
        
        //Start threads
        Variables.threadController.Start();
    }

    /**
     * This function will read the configuration file and set the
     * <b>Variables</b>.
     */
    private static void readConfiguration() {
        //TODO Exception handle while reading and setting.
        //Specially integer and enumration values.

        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream("config.properties"));

            //Anonymizer
            if (properties.containsKey("Anonymizer")) {
                Variables.anonymizerNetwork = Variables.Anonymizer.valueOf(properties.getProperty("Anonymizer", "TOR"));
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

            //Web Requests
            if (properties.containsKey("threadCount")) {
                Variables.threadCount = Integer.parseInt(properties.getProperty("threadCount", "5"));
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

            //Load and Store
            if (properties.containsKey("InputFile")) {
                Variables.inputFile = properties.getProperty("InputFile", "profiles.csv");
            }
            if (properties.containsKey("MainURLColumnName")) {
                Variables.inputFileLinksColumnName = properties.getProperty("MainURLColumnName", "");
            }
            if (properties.containsKey("OutputFileName")) {
                Variables.inputFileOutputFileName = properties.getProperty("OutputFileName", "");
            }
            if (properties.containsKey("OutputDirectory")) {
                Variables.outputDirector = properties.getProperty("OutputDirectory", "." + java.io.File.pathSeparator);

                if (!Variables.outputDirector.endsWith(java.io.File.pathSeparator)) {
                    Variables.outputDirector += java.io.File.pathSeparator;
                }
            }

            //Debug
            if (properties.containsKey("Debug")) {
                Variables.debug = Boolean.valueOf(properties.getProperty("Debug", "false"));
            }
            if (properties.containsKey("VeryVerbos")) {
                Variables.vv = Boolean.valueOf(properties.getProperty("VeryVerbos", "false"));
            }
            //Done reading ...
            Variables.logger.Log(Microbot.class, Variables.LogType.Info, "Config file read " + Variables.ANSI_GREEN + "successfully" + Variables.ANSI_RESET + ".");

            if (Variables.vv) {
                //TODO print value of each variable.
            }

        } catch (IOException e) {
            if (e instanceof FileNotFoundException) {
                //Create config file and try again
                Variables.logger.Log(Microbot.class, Variables.LogType.Warning, "Config file not found. " + Variables.ANSI_WHITE + "Create a new one." + Variables.ANSI_RESET);
                writeConfiguration();
                Variables.logger.Log(Microbot.class, Variables.LogType.Info, "Config file written successfully. Retrying ...");
                readConfiguration();
            }
        }
    }

    /**
     * This method will create default configuration file
     * (<b>properties.config</b>).
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
            prop.setProperty("ThreadCount", String.valueOf(Variables.threadCount));
            prop.setProperty("MaxSleep", String.valueOf(Variables.maxSleep));
            prop.setProperty("MinSleep", String.valueOf(Variables.minSleep));
            prop.setProperty("RandomUserAgent", String.valueOf(Variables.randomUA));
            prop.setProperty("UserAgentListFile", Variables.UAFile);
            prop.setProperty("Cookie", Variables.Cookie);

            //Load and Store
            prop.setProperty("InputFile", Variables.inputFile);
            prop.setProperty("MainURLColumnName", Variables.inputFileLinksColumnName);
            prop.setProperty("OutputFileName", Variables.inputFileOutputFileName);
            prop.setProperty("OutputDirectory", Variables.outputDirector);

            //Debug
            prop.setProperty("Debug", String.valueOf(Variables.debug));
            prop.setProperty("VeryVerbos", String.valueOf(Variables.vv));

            // save properties to project root folder
            prop.store(output, "Microbot Configuration");
        } catch (IOException e) {
            Variables.logger.Log(Microbot.class, Variables.LogType.Error, "Error in writing configuration file. Detail:\r\n" + Variables.ANSI_RED + e.getMessage() + Variables.ANSI_RESET);
            System.exit(-1);
        }
    }

    /**
     * This method will fill necessary information into RAM.
     */
    private static void fillConfiguration() {
        try {
            String line = "";
            String[] items = null;
            int URLIndex = 0;
            int OutputIndex = 0;
            BufferedReader br = null;

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
                Variables.logger.Log(Microbot.class, Variables.LogType.Error, Variables.ANSI_RED + Variables.inputFileLinksColumnName + Variables.ANSI_RESET + " not found in the input file [" + Variables.ANSI_GREEN + Variables.inputFile + Variables.ANSI_RESET);
                System.exit(-2);
            } else if (OutputIndex == -1) {
                Variables.logger.Log(Microbot.class, Variables.LogType.Error, Variables.ANSI_RED + Variables.inputFileLinksColumnName + Variables.ANSI_RESET + " not found in the input file [" + Variables.ANSI_GREEN + Variables.inputFile + Variables.ANSI_RESET);
                System.exit(-3);
            } else {
                line = br.readLine();

                while (line != null) {
                    Variables.links.add(new WebDocument(line.split(",")[URLIndex], line.split(",")[OutputIndex]));
                    line = br.readLine();
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

            System.gc();

        } catch (FileNotFoundException ex) {
            Variables.logger.Log(Microbot.class, Variables.LogType.Error, "Input file not found.\r\nDetails:\r\n" + Variables.ANSI_RED + ex.getMessage() + Variables.ANSI_RESET + "\r\n");
        } catch (IOException ex) {
            Variables.logger.Log(Microbot.class, Variables.LogType.Error, "Error in reading file.\r\nDetails:\r\n" + Variables.ANSI_RED + ex.getMessage() + Variables.ANSI_RESET + "\r\n");
        }
    }

}

/*
 Errors:
 -1      Error in writing configuration
 -2      Error in finding column name of input CSV file.
 -3      Error in finding output name of input CSV file.
 */
