 /******************************************************************
 *                HelpHandler.java  -  description
 *                    -----------------------
 * @author 		  Christoph Lauer
 * @version 		  0.1  
 * @see			  www.amiproject.de
 * copyright		: (C) 2004 @ christoph lauer
 * begin 		: Thu Sep 22 17:00:00 CET 2003
 * last save   	        : Time-stamp: <2004-09-23 14:01:50 christoph>  
 * compiler    	        : java version 1.4.2 
 * operating system     : Linux 
 * editor		: xemacs 21.4
 * description          : Handles the help menues.
 ******************************************************************/

package de.dfki.ami.amigram.help;

import java.net.URL;

import javax.help.HelpBroker;
import javax.help.HelpSet;

import de.dfki.ami.amigram.gui.DesktopFrame;

public class HelpHandler extends Object
{
    public  HelpSet              hs;
    public  HelpBroker           hb;
    private DesktopFrame         root;
    
    public HelpHandler(DesktopFrame r)
    {
	root = r;
	try 
	    {
		URL hsURL = getClass().getResource("helpset.hs");
		hs = new HelpSet(null,hsURL);
	    }
	catch (Exception e) 
	    {
		System.out.println("Exception" + e.getMessage());
		return;
	    }
	hb = hs.createHelpBroker();
    }
}

