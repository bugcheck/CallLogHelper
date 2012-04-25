package com.onmoso.entrypoint;

import net.rim.device.api.ui.UiApplication;

import com.onmoso.screen.HelperScreen;

/**
 * 
 * @author xiangguang
 * @version 1.0
 * http://www.onmoso.com
 */
public class EntryPoint extends UiApplication{
    /**
     * Entry point for application
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args)
    {  
        // Create a new instance of the application and make the currently
    	EntryPoint theApp = new EntryPoint();       
    	// running thread the application's event dispatch thread.
        theApp.enterEventDispatcher();
    } 
    
    /**
     * Creates a new MyApp object
     */
    public EntryPoint()
    {        
    	pushScreen(new HelperScreen());
    } 
}
