 /******************************************************************
 *                DesktopFrame.java  -  description
 *                    -----------------------
 * @author 		  Christoph Lauer
 * @version 		  0.1  
 * @see			  www.amiproject.de
 * copyright		: (C) 2003 @ christoph lauer
 * begin 		: Thu Okt 30 17:00:00 CET 2003
 * last save   	        : Time-stamp: <05/06/09 16:01:50 clauer> 
 * compiler    	        : java version 1.4.2 
 * operating system     : Linux 
 * editor		: xemacs 21.4
 * description          : The main window and the subwindow 
 *                        handling. Subwindows are registered and
 *                        unregistered here. (called root)
 ******************************************************************/

package de.dfki.ami.amigram.gui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.DefaultDesktopManager;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.RepaintManager;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;

import de.dfki.ami.amigram.multimedia.MediaPlayer;
import de.dfki.ami.amigram.plugin.PluginManager;
import de.dfki.ami.amigram.tools.AnnotationElement;
import de.dfki.ami.amigram.tools.AnnotationLayer;
import de.dfki.ami.amigram.tools.AnnotationLayerManagement;
import de.dfki.ami.amigram.tools.ConfigurationHandler;
import de.dfki.ami.amigram.tools.MediaTimeSynchronizer;
import de.dfki.ami.amigram.tools.NOMCommunicator;
import de.dfki.ami.amigram.tools.OnTheFlyAnnotation;
import de.dfki.ami.amigram.tools.WindowEventHandler;

public class DesktopFrame extends JFrame
{
    public  boolean              DEBUG   = false;// set from MainFrame while parsing the ARGS
    public  boolean              DURCHSTART = false;
    public  ConfigurationHandler config=null;        // the configuration file handling class. Instanciated after the DEBUG variable is set.    
    public  ControlWindow        ctrlwin;
    public  OtabFrame            otabwin;
    public  MediaTimeSynchronizer synchronizer = new MediaTimeSynchronizer(this);
    public  StatusBar            statusbar = new StatusBar(this);
    public  AnnotationLayerManagement layermanager = new AnnotationLayerManagement(this);
    public  NOMCommunicator      nomcommunicator = new NOMCommunicator(this);
    public  RepaintManager       repaintmanager = RepaintManager.currentManager(this);
    public  MenuBar              menubar;
    public  de.dfki.ami.amigram.help.HelpHandler          help = new de.dfki.ami.amigram.help.HelpHandler(this);
    public  OnTheFlyAnnotation   otf = new OnTheFlyAnnotation(this);
    public  PluginManager        pluginManager;

    private JDesktopPane         desk;
    private Hashtable            windows = new Hashtable();
    private WindowEventHandler   windoweventhandler; // implements the functions fro the window and the component listener
    
    // for the context menu
    public AnnotationElement lastae;
    public boolean addChild = false;
    public boolean removeChild = false;
    public boolean addParent = false;
    public boolean removeParent = false;
    public int lastActiveLayer = -1;
    public int lastActiveAnnoElement = -1;
    
    private int defaultWidth = 1200;
    private int defaultHight = 760;
    private int ctrlWidth    = 325;
    private int ctrlHeight   = 353;

    /** constructor **/
    public DesktopFrame(boolean debug,boolean durchstart)
    {
	super("AMIGRAM - AMI Graphical Representation and Annotation Module");
	DEBUG = debug;
	config = new ConfigurationHandler(this);
	if (DEBUG == true) config.printConfiguration();
	pluginManager = new PluginManager(this);
	desk = new JDesktopPane();
	desk.setDesktopManager(new DefaultDesktopManager());
	setContentPane(desk);
	windoweventhandler = new WindowEventHandler(this);
	addWindowListener(windoweventhandler);
	addComponentListener(windoweventhandler);
	// window setup
	int screenX = java.awt.Toolkit.getDefaultToolkit().getScreenSize().width;
	int screenY = java.awt.Toolkit.getDefaultToolkit().getScreenSize().height;
	// check for small screens
	if (screenX <= 1024){
	    defaultWidth = 1000;
	    defaultHight = 670;
	    printDebugMessage(" SMALL SCREEN DETECTED");
	}
	setLocation(screenX/2-defaultWidth/2,screenY/2-defaultHight/2);
	setSize(defaultWidth,defaultHight);
	setVisible(true);
	// add the child frames
	otabwin = new OtabFrame(this);
	otabwin.setResizable(true);
	menubar = new MenuBar(this);
	setJMenuBar(menubar);
	ctrlwin = new ControlWindow("Control Window",this);
	addSubWindow(ctrlwin,0,0,ctrlWidth,ctrlHeight);
	addSubWindow(otabwin,0,354,getWidth()-10,getHeight()-ctrlwin.getHeight()-50);
	// Change the tooltip Colors
 	UIManager.put("ToolTip.foreground",
 		      new javax.swing.plaf.ColorUIResource(new Color(200,0,0)));
 	UIManager.put("ToolTip.background",
 		      new javax.swing.plaf.ColorUIResource(new Color(194,251,0)));
	ToolTipManager.sharedInstance().setDismissDelay(30000);
	ToolTipManager.sharedInstance().setInitialDelay(200);
	// fullscreen in config
	if (config.fullScreen==true)
	    updateFullscreenMode();
	else
	    restoreWindowPositions();
	// Enable DoubleBuffering for all components
	repaintmanager.setDoubleBufferingEnabled(true); 
	// "durchstart" opens automaticly an anno file
	if (durchstart == true)
	    {
		DURCHSTART = true;
		menubar.initializeDataBank();
	    }
	// auto open dialog
	else if (config.openDialog==true)
	    menubar.initializeDataBank();
    }
    
    /** window management. Adds a aub window to the root window. The Name should be the Title of the Window. **/
    public void addSubWindow(JInternalFrame child, int x, int y, int w, int h)
    {
	// first check if there is an object with this key name
	while (windows.containsKey(child.getTitle())==true)
	    {
		child.setTitle(child.getTitle()+" COPY");
	    }

	// add the window to the hashtable and the root window
	child.setLocation(x,y);
	child.setSize(w,h);
 	child.setDefaultCloseOperation(JInternalFrame.DISPOSE_ON_CLOSE);
	desk.add(child);
	child.setVisible(true);
	windows.put(child.getTitle(),child);
    }
    /** window management. Removes a sub window from the root window **/
    public void removeSubWindow(String name) {
	windows.remove(name);
    }
    /** window management **/
    public JInternalFrame getSubWindow(String name){return (JInternalFrame)windows.get(name);}
    /** get all media player sub-windows. Returns a Vector from Type MediaPlayer. **/
    public Vector getAllMediaPlayerWindows()
    {
	Vector playerWindows = new Vector();
	for (Enumeration e = windows.elements() ; e.hasMoreElements() ;) 
	    {
		JInternalFrame win = (JInternalFrame)e.nextElement();
		if (win.getTitle().matches(".*Video Player.*") || win.getTitle().matches(".*Audio Player.*"))
		    playerWindows.add((MediaPlayer)win);
	    }
	// the allocate the array
	return playerWindows;
    }
    /** enables/diables the fullscreen mode **/
    public void updateFullscreenMode()
    {
	GraphicsEnvironment  ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
	GraphicsDevice       gs = ge.getDefaultScreenDevice();
// 	if (gs.isFullScreenSupported()==false)
// 	    {
// 		JOptionPane.showMessageDialog(this,"Fullscreen is not Supported on this Graphic Device.","Warning",2);
// 		config.fullScreen = false;
// 		menubar.fullscreen.setSelected(false);
// 		getControlWindow().fullscreen.setSelected(false);
// 		gs.setFullScreenWindow(null);
//              return;
// 	    }
	if (config.fullScreen == true)
		gs.setFullScreenWindow(this);
	else 
	    gs.setFullScreenWindow(null);
	placeSubwindows();
    }
    /** set the size of the root window back to the initial size  **/
    public void restoreWindowPositions()
    {
	if (config.fullScreen==false)
	    {
		setSize(defaultWidth,defaultHight);
		if (ctrlwin != null)
		    ctrlwin.setSize(ctrlWidth,ctrlHeight);
		int screenX = java.awt.Toolkit.getDefaultToolkit().getScreenSize().width;
		int screenY = java.awt.Toolkit.getDefaultToolkit().getScreenSize().height;
		setLocation(screenX/2-defaultWidth/2,screenY/2-defaultHight/2);
	    }
	placeSubwindows();
    }
    /** automatical order all subwindows **/ 
    public void placeSubwindows()
    {
	if (ctrlwin==null || otabwin==null)return;
	printDebugMessage("Place sub windows");
	int rootWinX=getWidth();
	int rootWinY=getHeight();
	// place the otab and the control window
	ctrlwin.setLocation(0,0);
	otabwin.setLocation(0,ctrlwin.getHeight()+1);
	otabwin.setSize(rootWinX-10,rootWinY-ctrlwin.getHeight()-50);  
	// place the media player windows
	Vector playerWindows = getAllMediaPlayerWindows();
	int xLocation = ctrlwin.getWidth()+1;  // is counted during the loop
	// place the audio windows before the video windows
	int audioPlayerCounter = 0;
	for (int i=0;i<playerWindows.size();i++)
	    {
		MediaPlayer presentPlayerWin = ((MediaPlayer)playerWindows.get(i));
		if (presentPlayerWin.aspectRatio == -1.0f)
		    {
			int xSize = ctrlwin.getHeight();
			presentPlayerWin.pack();
			int ySize = presentPlayerWin.getHeight(); // vorher = 50
			presentPlayerWin.setLocation(xLocation,audioPlayerCounter*50); 
			presentPlayerWin.setPreferredSize(new Dimension(xSize,ySize));
			presentPlayerWin.pack();
			audioPlayerCounter++;
		    }
		if (audioPlayerCounter==6)
		    {
			audioPlayerCounter=0;
			xLocation += ctrlwin.getHeight()+1;
		    }
	    }
	if (audioPlayerCounter != 0)
	    xLocation += ctrlwin.getHeight()+1;
	// place the video windows
	for (int i=0;i<playerWindows.size();i++)
	    {
		MediaPlayer presentPlayerWin = ((MediaPlayer)playerWindows.get(i));
		if (presentPlayerWin.aspectRatio == -1.0f)
		    continue;
		int xSize = (int)(((float)ctrlwin.getHeight()-40)*presentPlayerWin.aspectRatio); // perhaps the borders are not 40
		int ySize = ctrlwin.getHeight();
		presentPlayerWin.setLocation(xLocation,0);
		presentPlayerWin.setPreferredSize(new Dimension(xSize,ySize));
		xLocation += xSize+1;
		presentPlayerWin.pack();
	    }
    }
    /** debug handling. Prints out a debug message if the DEBUG variable ist set. Otherwise a dot ist printed out. **/
    public void printDebugMessage(String message)
    {
	if (DEBUG == false) 
	    return;
	System.out.println("DEBUG: "+message);
    }
    /** quits the whole program **/
    public void quit()
    {
	if (nomcommunicator.metadata != null) {
	    int n = JOptionPane.showConfirmDialog(this,"QUIT: Save the Corpus to the NOM ?","Last Save: "+nomcommunicator.saveTime,JOptionPane.YES_NO_OPTION);
	    System.out.println(n);
	    if (n==0) {
		nomcommunicator.saveAnnotationToNom();
	    }
	}
	config.writeConfigurationFile();
	printDebugMessage("Amigram quits now.\n");
	System.exit(0);
    }
    /** shows an select-window and closes the selected multimedia window **/
    public void closeMultimediaWindow(String preferedWindow)	
    {
	// first collect all player windows into a String
	Vector playerWindows = getAllMediaPlayerWindows();
	if (playerWindows.size() == 0)
	    {
		JOptionPane.showMessageDialog(this,"There are no open media players.","No Players",1);
		printDebugMessage(" no open players.");
		return;
	    }
	String[] wins = new String[playerWindows.size()];
	int pref = 0;    // the id of the prefered window
	for (int i=0;i<playerWindows.size();i++)
	    {
		wins[i] = ((MediaPlayer)playerWindows.get(i)).getTitle();
		if (((MediaPlayer)playerWindows.get(i)).getTitle().equals(preferedWindow)) 
		    pref = i;
	    }
	// Show the debug message
	printDebugMessage("show player list to select which player to close. found "+playerWindows.size()+" players.");
	String answer = (String)JOptionPane.showInputDialog(this,"Select player to close. ( "+playerWindows.size()+" players )","Close Media Player",JOptionPane.PLAIN_MESSAGE,null,wins,wins[pref]);
	// resolve the selected Window and closes them
	for (int i=0;i<playerWindows.size();i++)
	    {
		if (((MediaPlayer)playerWindows.get(i)).getTitle().equals(answer)) 
		    {
			printDebugMessage("Decide to close "+answer+".");
			removeSubWindow(((MediaPlayer)playerWindows.get(i)).getTitle());
			synchronizer.unregisterListener((MediaPlayer)playerWindows.get(i));
			((MediaPlayer)playerWindows.get(i)).stopPlayer();	
			((MediaPlayer)playerWindows.get(i)).dispose();	
			if (config.automaticalOrderWindows==true)
			    placeSubwindows();
		    }
	    }
    }
    // Classes for the OTAB migration
    public ControlWindow getControlWindow()
    {
	return ctrlwin;
    }
    public OtabFrame getOtabWindow()
    {
	return otabwin;
    }
    /**
     * Sets the cursor for the whole application to the wait cursor if "C" == 1, else to the default cursor.
     * @param int = 0: (DEFAULT) sets all ti wait cursor and layers to crosshair
     * @param int = 1: (WAIT) sets all to wait cursor
     * @param int = 2: (HAND while add/remove childs) sets sets to defaut, ald layers to hand  
     */
    public void setCursorForAllWidgets(int c)
    {
	java.awt.Cursor dcursor = new Cursor(Cursor.DEFAULT_CURSOR);
	java.awt.Cursor wcursor = new Cursor(Cursor.WAIT_CURSOR);
	java.awt.Cursor hcursor = new Cursor(Cursor.HAND_CURSOR);
	java.awt.Cursor ccursor = new Cursor(Cursor.CROSSHAIR_CURSOR);
	
	if (c==1) {
	    setCursor(wcursor);
	    otabwin.setCursor(wcursor);
	    ctrlwin.setCursor(wcursor);
	    menubar.setCursor(wcursor);
	    otabwin.defaultpanel.setCursor(wcursor);
	    setlayerCursors(wcursor);
	}
	if (c==2) {
	    setCursor(dcursor);
	    otabwin.setCursor(dcursor);
	    ctrlwin.setCursor(dcursor);
	    menubar.setCursor(dcursor);
	    otabwin.defaultpanel.setCursor(dcursor);
	    setlayerCursors(hcursor);
	}
	else{
	    setCursor(dcursor);
	    otabwin.setCursor(dcursor);
	    ctrlwin.setCursor(dcursor);
	    menubar.setCursor(dcursor);
	    otabwin.defaultpanel.setCursor(dcursor);
	    setlayerCursors(ccursor);
	}
    }
    private void setlayerCursors(java.awt.Cursor c) {
	for (int i=0;i<layermanager.getNumberOffLayers()-1;i++) {
	    AnnotationLayer al = layermanager.getLayerByPaintOrder(i);
	    if (al.getLayerType()==AnnotationLayer.TIMEALIGNED || al.getLayerType()==AnnotationLayer.STRUCTURAL) 
		    al.setCursor(c);
	}
    }
    public NOMCommunicator getNOMCommunicator() {
	return nomcommunicator;
    }
    
    public MenuBar getAmiGramMenuBar(){
    	return menubar;
    }
}
