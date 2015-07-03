/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Fetcher;

import helpers.Variables;
import java.util.Vector;

/**
 * Class for storing and managing threads.
 * @author Mir Saman Tajbakhsh
 */
public class Controller {
    
    Fetcher[] threads;
    
    /**
     * Dummy constructor
     */
    public Controller() {
        
    }
    
    public void Start() {
        int ID = 0;
        
        threads = new Fetcher[Variables.threadCount];
        
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Fetcher(ID++);
            threads[i].startFetching();
        }
    }
    
}
