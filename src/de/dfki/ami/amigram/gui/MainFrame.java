/******************************************************************
 *                  MainFrame.java  -  description
 *                    -----------------------
 * @author 		  Christoph Lauer
 * @version 		  0.1  
 * @see			  www.amiproject.de
 * copyright		: (C) 2003 @ christoph lauer
 * begin 		: Thu Okt 30 17:00:00 CET 2003
 * last save   	        : Time-stamp: <05/08/09 11:50:14 clauer> 
 * compiler    	        : java version 1.4.2 
 * operating system     : Linux 
 * editor		: xemacs 21.4
 * description          : This class has the main function which
 *                        initialises the main window, parses the
 *                        command line parameters and set the GUI
 *                        to the metal look and feel.
 ******************************************************************/
 
package de.dfki.ami.amigram.gui;

import java.awt.Toolkit;
import java.util.Locale;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class MainFrame
{
    public static final float version = 0.88f;
    public static SplashWindow splash;

    public static void main(String[] args)
    {
    Locale.setDefault(Locale.ENGLISH);
	splash = new SplashWindow("splash.png",10,true,version);
	// look and feel disable here.
	JDialog.setDefaultLookAndFeelDecorated(true);
	JFrame.setDefaultLookAndFeelDecorated(true);
	Toolkit.getDefaultToolkit().setDynamicLayout(true);
 	try {
	    javax.swing.plaf.metal.MetalLookAndFeel.setCurrentTheme( new javax.swing.plaf.metal.DefaultMetalTheme());
	    UIManager.setLookAndFeel("smooth.metal.SmoothLookAndFeel");
         }  
	catch ( UnsupportedLookAndFeelException e ) {
	    System.out.println ("Metal Look & Feel not supported on this platform. \nProgram Terminated");
	    System.exit(0);
	}
	catch ( IllegalAccessException e ) {
	    System.out.println ("Metal Look & Feel could not be accessed. \nProgram Terminated");
	    System.exit(0);
	}
	catch ( ClassNotFoundException e ) {
	    System.out.println ("Metal Look & Feel could not found. \nProgram Terminated");
	    System.exit(0);
	} 
	catch ( InstantiationException e ) {
	    System.out.println ("Metal Look & Feel could not be instantiated. \nProgram Terminated");
	    System.exit(0);
	}
	catch ( Exception e ) {
	    System.out.println ("Unexpected error. \nProgram Terminated");
	    e.printStackTrace();
             System.exit(0);
	}
        try {Class.forName("javax.media.Player");} 
	catch (Throwable throwable)
	    {
		JOptionPane.showMessageDialog(null,"Java Media Framework is not installed.","JMF not found",2);
		System.out.println("Java Media Framework is not installed.");
	    }
	boolean debug = false;
	if (java.lang.reflect.Array.getLength(args)!=0)
	    if (args[0].compareTo("DEBUG") == 0) {
		debug = true;
		System.out.println("\nDEBUG: ###########################################");
		System.out.println("DEBUG: #     Welcome to amigram version: "+version+"    #");
		System.out.println("DEBUG: ###########################################");
		System.out.println("DEBUG: Debug messages are enabled.");
	    }
	    else {
		debug = false;
		System.out.println("Unknown command line parameter.");
	    }
	// the "durchstart" parameter opens automatical a file while start
	boolean durchstart = false;
	if (java.lang.reflect.Array.getLength(args)==2 && args[1].compareTo("AUTOOPEN") == 0) {
	    durchstart = true;
	    System.out.println("DEBUG: Automaicly open a corpus file.");
	}
	// create the main window
	DesktopFrame desktop = new DesktopFrame(debug,durchstart);
    }
}
