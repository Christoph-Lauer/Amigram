package de.dfki.ami.amigram.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;

/**
 * This class implements an status bar that is updated by an thread with a high
 * priority periodical. So it guarantees that the display is realy updated.
 * The class extends an Thread.
 */

public class StatusBar extends Thread {

    private DesktopFrame root;
    private boolean      statuschangedtext  = false;
    private boolean      statuschangedvalue = false;
    private String       statustext  = "";
    private int          statusvalue = 0;
    private JTextField   textfield;
    private JProgressBar progressbar;
    public  JPanel       panel;
    
    /**
     * The Constructor gets the reference to the main frame and initalizes some the gui.
     * @param DesktopFrame mf - the reference to the main frame.
     */
    public StatusBar(DesktopFrame r) {
	root = r;
	// First Build the Progress Bar
	progressbar = new JProgressBar(0,100);
	progressbar.setToolTipText("<html><b>Progress Bar</b><br>Shows the progress of the activities<br>in the program.</html>");
	progressbar.setStringPainted(false);
	progressbar.setBorderPainted(false);
 	progressbar.setBackground(new Color(60,60,120));
 	progressbar.setForeground(new Color(160,160,210));
	progressbar.setValue(statusvalue);
	ProgressBarUI spui = new ProgressBarUI(new Color(193,193,230),new Color(98,98,149),true);
	progressbar.setUI(spui);
	progressbar.setPreferredSize(new Dimension(80,14));
	// Then the text field
	textfield = new JTextField();
	textfield.setToolTipText("<html><b>Status Text</b><br>Shows the Programm status information.</html>");
	textfield.setEditable(false);
	textfield.setFont(new Font("Tahoma",0,11));
 	textfield.setForeground(new Color(40,40,100));
 	textfield.setBackground(new Color(160,160,210));
	textfield.setCaretColor(new Color(60,60,120));
	textfield.setText("Welcome to AMIGRAM");
	textfield.setPreferredSize(new Dimension(230,14));
	textfield.setBorder(BorderFactory.createLineBorder(new Color(60,60,120)));
	// The main panel for the status bar.
	panel = new JPanel();
	panel.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));
	panel.add(textfield);
	panel.add(progressbar);
	panel.setPreferredSize(new Dimension(215,22));
	// Start the thread.
	setPriority(Thread.MAX_PRIORITY);
	start();
    }
    /**
     * The thread run function is defined in the runnable interface.
     */
    public void run() {
	for (;;) {  // The endless loop for the thread
	    if (statuschangedtext == true) {
		statuschangedtext = false;
		textfield.setText(statustext);
 		Graphics g = textfield.getGraphics();
 		textfield.update(g);
	    }
 	    if (statuschangedvalue == true) {
  		statuschangedvalue = false;
  		progressbar.setValue(statusvalue);
		progressbar.setIndeterminate(true);
 		Graphics g = progressbar.getGraphics();
 		progressbar.update(g);
	    }
	    try {Thread.sleep(100);}
	    catch (InterruptedException e) {}
	}
    }
    /**
     * Updates the status text.
     * @param String text - The text to display in the status bar.
     */
    public void updateStatusText(String text) {
  	if (text.compareTo(statustext)==0) 
  	    return;
	statuschangedtext = true;	
	statustext = text;
    }
    /** 
     * Updates the Progress monitor value. The value range reaches from 0..100.
     * @param int value - a value from 0..100
     */
    public void updateStatusValue(int value) {
	if (value==statusvalue)
	    return;
	statuschangedvalue = true;
	statusvalue = value;
    }
}
