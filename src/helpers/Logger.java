/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package helpers;

import org.slf4j.LoggerFactory;

/**
 *
 * @author Mir Saman Tajbakhsh
 */
public class Logger {

    private org.slf4j.impl.SimpleLogger logger;
    private double progress = 0.0d;

    public Logger() {
        System.setProperty(org.slf4j.impl.SimpleLogger.SHOW_DATE_TIME_KEY, "true");
        System.setProperty(org.slf4j.impl.SimpleLogger.DATE_TIME_FORMAT_KEY, "yyyy-MM-dd HH:mm:ss:SSS Z");
        System.setProperty(org.slf4j.impl.SimpleLogger.SHOW_THREAD_NAME_KEY, "true");
        System.setProperty(org.slf4j.impl.SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "TRACE");
        
        //TODO Make the loggger more customizable.
        //enable file logging, user can configure ehat to be logged.
    }

    public void Log(Class logClass, Variables.LogType logType, String message) {
        logger = (org.slf4j.impl.SimpleLogger) LoggerFactory.getLogger(logClass);

        switch (logType) {
            case Debug:
                logger.debug(message);
                break;
            case Error:
                logger.error(message);
                break;
            case Info:
                logger.info(message);
                break;
            case Trace:
                logger.trace(message);
                break;
            case Warning:
                logger.warn(message);
                break;
        }
    }
    
    /**
     * This method will declare the progress of the links completed successfully.
     * @param progress The progress value between 0 and 100.
     */
    public void logProgress(double progress) {
        if (progress - this.progress == 5) { //Display with 5 percents progresses.
            this.progress = progress;
            Variables.logger.Log(Logger.class, Variables.LogType.Info, "Completed links progress:\t" + Methods.Colorize(String.valueOf(progress), Methods.Color.Green) + "%");
        }
    }

}
