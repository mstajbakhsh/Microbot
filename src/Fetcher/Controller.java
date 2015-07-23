/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Fetcher;

import helpers.Methods;
import helpers.Variables;
import java.io.File;
import java.io.IOException;

/**
 * Class for storing and managing threads.
 *
 * @author Mir Saman Tajbakhsh
 */
public class Controller {

    private Fetcher[] threads;
    private int activeThreads = 0;

    /**
     * Dummy constructor
     */
    public Controller() {

    }

    public void Start() {

        //Add SSL trust here before each thread starts.
        if (Variables.acceptAllCerts) {
            Methods.trustAllCertificates();
        }

        int ID = 0;
        Variables.state = Variables.microbotState.Fetching;

        threads = new Fetcher[Variables.threadCount];

        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Fetcher(ID++);
            threads[i].startFetching();
            changeActiveThreads(true, threads[i].getThread(), Variables.microbotState.Fetching);
        }
    }

    public void notifyAllThreads() {
        for (int i = 0; i < threads.length; i++) {
            if (threads[i] != null) {
                synchronized (threads[i].getThread()) {
                    threads[i].getThread().notify();
                    changeActiveThreads(true, threads[i].getThread(), Variables.microbotState.Fetching);
                }
            }
        }
    }

    /**
     * This method will stops the threads.
     */
    public void Stop() {
        //Change program state.
        for (Fetcher t : threads) {
            t.Stop();
        }
    }

    /**
     * This method will control number of active threads.
     *
     * @param increase If true will increase number of threads. It will
     * decrease.
     * @param t The thread which changes active threads count. otherwise.
     */
    public synchronized void changeActiveThreads(boolean increase, Thread t, Variables.microbotState currentState) {

        if (t == null) {
            //Comes from shutdown hook. There is no other thread running.
            cleanUp();
            
            //Rename old files
            (new File(Variables.inputFile)).renameTo(new File(Variables.inputFile + ".old"));
            
            
            return;
        }

        if (increase) {
            activeThreads++;
        } else {
            if (activeThreads > 0) {
                activeThreads--;

                //Lock the thread by means of other thread.
                Runnable r = new Runnable() {

                    @Override
                    public void run() {
                        try {
                        //This case only happens when the thread detects that size limit is reached or user exits the program.
                            //So the thread should freeze.
                            synchronized (t) {
                                t.wait();
                            }
                        } catch (InterruptedException ex) {
                            if (Variables.debug) {
                                Variables.logger.Log(Controller.class, Variables.LogType.Error, "Error in waiting thread [" + t.getName() + "]. Details:\r\n" + ex.getMessage());
                            }
                        }
                    }
                };

                new Thread(r).start();
            }
        }

        if (activeThreads == 0) { //Start compressing
            switch (currentState) {
                case Compressing:
                    Variables.state = Variables.microbotState.Compressing;
                    Variables.compressor.Compress(Variables.outputDirectory, Variables.outputDirectory + ".." + File.separator + "Compressed" + File.separator, Variables.compressType);

                    //Notify all waited threads
                    Variables.state = Variables.microbotState.Fetching;
                    Variables.threadController.notifyAllThreads();
                    break;

                case Stopping:
                    cleanUp();
            }
        }
    }

    /**
     * This method will clean up after {@code CTRL + C} or normal finish.
     */
    private void cleanUp() {
        //Wait for all threads to finish
        Methods.makeLinksLogs();

        //Stop the log writers
        try {
            Variables.successFileWriter.close();
            Variables.errorFileWriter.close();
        } catch (IOException ex) {
            Variables.logger.Log(Controller.class, Variables.LogType.Error, "Error in closing success and error logs. Details:\r\n" + ex.getMessage());
        }
    }
}
