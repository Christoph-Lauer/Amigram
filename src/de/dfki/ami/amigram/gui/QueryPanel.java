/******************************************************************
 *                 QueryPanel.java  -  description
 *                    -----------------------
 * @author 		  Christoph Lauer
 * @version 		  0.1  
 * @see			  www.amiproject.de
 * copyright		: (C) 2003 @ christoph lauer
 * begin 		: Fri mar 03 15:00:00 CET 2005
 * last save   	        : Time-stamp: <05/08/19 14:36:32 clauer> 
 * compiler    	        : java version 1.4.2 
 * operating system     : Linux 
 * editor		: xemacs 21.4
 * description          : Handles all the routine for the nxt qery
 ******************************************************************/

package de.dfki.ami.amigram.gui;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

class QueryPanel extends JPanel {
    private DesktopFrame root;
    public QueryPanel(DesktopFrame r){
	root = r;
	setLayout(new GridLayout(5,2));
	JButton gui = new JButton("Show Query Window");
	JButton clear = new JButton("Clear Results");
	this.add(gui);
	this.add(clear);
	gui.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent event)
		{
		    root.nomcommunicator.gui.popupSearchWindow();
		}
	    });
	clear.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent event)
		{
		    root.layermanager.clearHighlightElements();
		}
	    });
    }
}
