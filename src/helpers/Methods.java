/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package helpers;

import static helpers.Variables.UAs;
import static helpers.Variables.links;
import java.io.File;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author Mir Saman Tajbakhsh
 */
public class Methods {

    private static Integer finishedCounter = 0;
    private static Integer profileCounter = 0;
    private static Random r = new Random();

    //<editor-fold defaultstate="collapsed" desc="Web Requests">
    //Web requests
    public static synchronized WebDocument getNextProfileLink() {
        return links.get(profileCounter++);
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
     * This method will run after each thread finished its work. If size limit
     * enabled in configuration, it will zip and delete the main files due to configuration.
     */
    public static synchronized void checkFinished() {
        //TODO Check semaphor or mutex
        //Check size limit of output folder
        long size = getFolderSize(Variables.outputDirectory);
        if (size >= Variables.outputSizeLimit) {
            
            //Change state
            Variables.state = Variables.microbotState.Compressing;
            
            if (Variables.debug) {
                Variables.logger.Log(Methods.class, Variables.LogType.Info, "File threshold reached [" + filesizeToHumanReadable(size, false) + "]");
            }
            
            Variables.compressor.Compress(Variables.outputDirectory, Variables.outputDirectory + ".." + File.separator + "Compressed" + File.separator, Variables.CompressType.ZIP);
        }
        
        //report progress
        finishedCounter++;
        double progress = ((double) finishedCounter / (double) Variables.links.size());
        Variables.logger.logProgress(progress);
        
        //Resume downloading
        Variables.state = Variables.microbotState.Fetching;
    }

    /**
     * This method will return a random integer between minTime and maxTime
     * (Configuration file - > Variables)
     *
     * @return The random time. If any error occur, -1 will be returned.
     */
    public static synchronized Integer getNextRandom() {
        try {
            return (r.nextInt(Variables.maxSleep - Variables.minSleep) + Variables.minSleep);
        } catch (IllegalArgumentException ex) {
            return -1;
        }
    }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Load and Store">
    //Load and Store
    
    /**
     * This method converts human readable file size to bytes (1024).
     * @param humanReadableFileSize The human readable file size (Ex. 500MB <b>without space between size and unit</b>)
     * @return the bytes of human readable size in long format.
     */
    public static long filesizeToBytes(String humanReadableFileSize) {
        long returnValue = -1;
        java.util.regex.Pattern patt = java.util.regex.Pattern.compile("([\\d.]+)([GMK]B)", java.util.regex.Pattern.CASE_INSENSITIVE);
        Matcher matcher = patt.matcher(humanReadableFileSize);
        Map<String, Integer> powerMap = new HashMap<>();
        powerMap.put("TB", 4);
        powerMap.put("GB", 3);
        powerMap.put("MB", 2);
        powerMap.put("KB", 1);
        powerMap.put("B", 0);
        if (matcher.find()) {
            String number = matcher.group(1);
            int pow = powerMap.get(matcher.group(2).toUpperCase());
            BigDecimal bytes = new BigDecimal(number);
            bytes = bytes.multiply(BigDecimal.valueOf(1024).pow(pow));
            returnValue = bytes.longValue();
        }
        return returnValue;
    }

    /**
     * This method will convert bytes to human readable size (Ex. 500MB).
     * @param bytes The number of bytes.
     * @param si The base of conversion ({@code si = true} -> 1000 , {@code si = false} -> 1024)
     * @return The string format of human readable size (Ex. 500MB).
     */
    public static String filesizeToHumanReadable(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit) {
            return bytes + "B";
        }
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + ""; // + (si ? "" : "i");
        return String.format("%.1f%sB", bytes / Math.pow(unit, exp), pre);
    }

    /**
     * This method will calculate size of files inside a folder. It will not
     * calculate recursively.
     *
     * @param Directory The path of directory.
     * @return Size of all elements in long or 0 if security restrictions occur
     * or -1 if the size is longer than {@link Long#MAX_VALUE}.
     */
    public static synchronized long getFolderSize(String Directory) {
        return FileUtils.sizeOfDirectory(new File(Directory));
    }
    
    /**
     * This method will check if path has a separator at end. If not it will add.
     * @param Directory The path of directory to be checked.
     * @return The path with separator at end.
     */
    public static String checkDirectory(String Directory) {
        if (Directory.endsWith(java.io.File.separator)) {
            return Directory;
        } else {
            return Directory + java.io.File.separator;
        }
    }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Logger">
    //Logger
    public static String Colorize(String msg, Color color) {
        switch (color) {
            case Black:
                return ANSI_BLACK + msg + ANSI_RESET;
            case Blue:
                return ANSI_BLUE + msg + ANSI_RESET;

            case Cyan:
                return ANSI_CYAN + msg + ANSI_RESET;

            case Green:
                return ANSI_GREEN + msg + ANSI_RESET;

            case Purple:
                return ANSI_PURPLE + msg + ANSI_RESET;

            case Red:
                return ANSI_RED + msg + ANSI_RESET;

            case White:
                return ANSI_WHITE + msg + ANSI_RESET;

            case Yellow:
                return ANSI_YELLOW + msg + ANSI_RESET;
        }
        return msg;
    }

    // Color
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_BLACK = "\u001B[30m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_BLUE = "\u001B[34m";
    private static final String ANSI_PURPLE = "\u001B[35m";
    private static final String ANSI_CYAN = "\u001B[36m";
    private static final String ANSI_WHITE = "\u001B[37m";

    /**
     * Color for the output text.
     */
    public enum Color {

        Black, Red, Green, Yellow, Blue, Purple, Cyan, White
    };
//</editor-fold>
}
