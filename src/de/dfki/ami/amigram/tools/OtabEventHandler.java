/******************************************************************
 *                 OtabEventHandler.java  -  description
 *                    -----------------------
 * @author 		  Christoph Lauer
 * @version 		  0.1  
 * @see			  www.amiproject.de
 * copyright		: (C) 2003 @ christoph lauer
 * begin 		: Thu Okt 30 17:00:00 CET 2003
 * last save   	        : Time-stamp: <04/11/18 20:40:04 clauer> 
 * compiler    	        : java version 1.4.2 
 * operating system     : Linux 
 * editor		: xemacs 21.4
 * description          : Eventhandling for the OTAB.
 ******************************************************************/

package de.dfki.ami.amigram.tools;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.dfki.ami.amigram.gui.DesktopFrame;

public class OtabEventHandler 
    implements ActionListener,ChangeListener
{
    public DesktopFrame root;

    public OtabEventHandler(DesktopFrame r)
    {
	root = r;
    }
    public void actionPerformed(ActionEvent e) 
    {
	root.printDebugMessage("ACTION PERFORMED IN EVENT HANDLER OTABWINDOW");
// 	if (e.getSource() == root.getOtabWindow().toolbartwo.newbutton)
// 	    {
// 		int activelayer = root.layermanager.getActiveLayer();
// 		if (activelayer == -1) {
// 		    JOptionPane.showMessageDialog(root,"Please select a layer first.","Info",1);
// 		}
// 		else 
// 		    root.layermanager.getLayerByID(activelayer).addAnnotationLayerElement();
// 	    }

// 	if (e.getSource() == root.getOtabWindow().toolbartwo.removebutton)
// 	    {
// 		int activelayer = root.layermanager.getActiveLayer();
// 		if (activelayer == -1) {
// 		    JOptionPane.showMessageDialog(root,"Please select a layer first.","Info",1);
// 		}
// 		else root.layermanager.getLayerByID(activelayer).removeTrackElement();
// 	    }

// 	if (e.getSource() == mainframe.getOtabWindow().toolbartwo.printbutton) {
// 	    de.dfki.sonogram.PrintableComponent pc = new de.dfki.sonogram.PrintableComponent(mainframe.getOtabWindow().hierarchytreepanel);
// 	    mainframe.statusbar.updateStatusText("Print Otab board.");
// 	    try {
// 		pc.print();
// 		mainframe.statusbar.updateStatusText("Otab board printed.");
// 	    } catch(java.awt.print.PrinterException pe){
// 		mainframe.statusbar.updateStatusText("Error while printing.");
// 		System.out.println(pe);
// 	    };
// 	}


    }
    public void stateChanged(ChangeEvent e)
	{
	    root.printDebugMessage("STATE CHANGED IN EVENT HANDLER OTABWINDOW");
	}

}
