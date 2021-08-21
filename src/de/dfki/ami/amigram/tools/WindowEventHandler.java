/******************************************************************
 *                     WindowEventHandler.java  -  description
 *                    -----------------------
 * @author 		  Christoph Lauer
 * @version 		  0.1  
 * @see			  www.amiproject.de
 * copyright		: (C) 2003 @ christoph lauer
 * begin 		: Thu Okt 30 17:00:00 CET 2003
 * last save   	        : Time-stamp: <2005-03-09 18:01:18 christoph> 
 * compiler    	        : java version 1.4.2 
 * operating system     : Linux 
 * editor		: xemacs 21.4
 * description          : The window closing adapter exclusively 
 *                        for the desktop frame.
 ******************************************************************/

package de.dfki.ami.amigram.tools;

import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import de.dfki.ami.amigram.gui.DesktopFrame;

public class WindowEventHandler extends WindowAdapter implements ComponentListener
{
    private DesktopFrame root;

    public WindowEventHandler(DesktopFrame r){root = r;}

    // implementation of the window adapter functions
    public void windowClosing(WindowEvent event)
    {
	root.printDebugMessage("Window closing message");
	root.quit();
    }
    public void windowActivated(WindowEvent e){}
    public void windowClosed(WindowEvent e){}
    public void windowDeactivated(WindowEvent e){}
    public void windowDeiconified(WindowEvent e){}
    public void windowGainedFocus(WindowEvent e){}
    public void windowIconified(WindowEvent e){}
    public void windowLostFocus(WindowEvent e){}
    public void windowOpened(WindowEvent e){}
    public void windowStateChanged(WindowEvent e) {}
    // as follows the implementation of the componentlistener
    public void componentHidden(ComponentEvent e){}
    public void componentMoved(ComponentEvent e){}
    public void componentResized(ComponentEvent e)
    {
	if(root.config.automaticalOrderWindows)
	    root.placeSubwindows();
    }
    public void componentShown(ComponentEvent e){}
    
}
