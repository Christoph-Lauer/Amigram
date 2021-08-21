/******************************************************************
 *                     MenuBar.java  -  description
 *                    -----------------------
 * @author 		  Christoph Lauer
 * @version 		  0.1  
 * @see			  www.amiproject.de
 * copyright		: (C) 2003 @ christoph lauer
 * begin 		: Thu Okt 30 17:00:00 CET 2003
 * last save   	        : Time-stamp: <05/08/23 09:59:24 clauer> 
 * compiler    	        : java version 1.4.2 
 * operating system     : Linux 
 * editor		: xemacs 21.4
 * description          : Menue bar for the main window including 
 *                        the action handler for the menue items.
 ******************************************************************/

package de.dfki.ami.amigram.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import de.dfki.ami.amigram.multimedia.MediaPlayer;
import de.dfki.ami.amigram.tools.AnnotationElement;
import de.dfki.ami.amigram.tools.AnnotationLayer;

public class MenuBar extends JMenuBar implements ActionListener
{
    DesktopFrame root;
    JMenu menu;                   // temporary menu
    JMenuItem menuItem;           // temporary menu item
    public JCheckBoxMenuItem tooltip,fullscreen,autoorder,syncevents,syncstartstop,autoosca,autospec,autoopen,debug;// temporary check box menue item
    public JFileChooser chooser = new JFileChooser();
    private boolean reload = false;
    
    public MenuBar(DesktopFrame r){
	super();
	root = r;
       	menu = new JMenu("File");
	add(menu);
	menuItem = new JMenuItem("open annotation");
	menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
	menuItem.addActionListener(this);
	menu.add(menuItem);
	menuItem = new JMenuItem("reload annotation");
	menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, ActionEvent.CTRL_MASK | ActionEvent.ALT_MASK));
	menuItem.addActionListener(this);
	menu.add(menuItem);
	menuItem = new JMenuItem("close annotation");
	menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK | ActionEvent.ALT_MASK));
	menuItem.addActionListener(this);
	menu.add(menuItem);
	menu.addSeparator();
	menuItem = new JMenuItem("open corpus signal");
	menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK ));
	menuItem.addActionListener(this);
	menu.add(menuItem);
	menuItem = new JMenuItem("open other signal");
	menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK | ActionEvent.ALT_MASK));
	menuItem.addActionListener(this);
	menu.add(menuItem);
	menuItem = new JMenuItem("close signal");
	menuItem.addActionListener(this);
	menu.add(menuItem);
	menu.addSeparator();
	menuItem = new JMenuItem("generate time waveform");
	menuItem.addActionListener(root.otabwin.toolbar);
	menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, ActionEvent.CTRL_MASK | ActionEvent.ALT_MASK));
	menu.add(menuItem);
	menuItem = new JMenuItem("generate fourier spectrum");
	menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, ActionEvent.CTRL_MASK | ActionEvent.ALT_MASK));
	menuItem.addActionListener(root.otabwin.toolbar);
	menu.add(menuItem);
	menu.addSeparator();
	menuItem = new JMenuItem("quit");
	menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.CTRL_MASK));
	menuItem.addActionListener(this);
	menu.add(menuItem);

       	menu = new JMenu("Options");
	add(menu);
	menuItem = new JMenuItem("restore initial window positions");
	menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F10,0));
	menuItem.addActionListener(this);
	menu.add(menuItem);
	fullscreen = new JCheckBoxMenuItem("fullscreen",root.config.fullScreen);
	fullscreen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F11,0));
	fullscreen.addActionListener(this);
	menu.add(fullscreen);
	autoorder = new JCheckBoxMenuItem("automatical order windows",root.config.automaticalOrderWindows);
	autoorder.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F12,0));
	autoorder.addActionListener(this);
	menu.add(autoorder);
	menu.addSeparator();
	syncevents = new JCheckBoxMenuItem("synchronize media time changes",root.config.synchronizeMediaTimeChanges);
	syncevents.addActionListener(this);
	menu.add(syncevents);
	syncstartstop = new JCheckBoxMenuItem("synchronize media start/stop events",root.config.synchronizeMediaStartStopEvents);
	syncstartstop.addActionListener(this);
	menu.add(syncstartstop);

	menu.addSeparator();
	menuItem = new JMenuItem("plugin manager");
	menuItem.addActionListener(this);
	menu.add(menuItem);

	menu.addSeparator();
	autoosca = new JCheckBoxMenuItem("auto generate oszillogram for opened media",root.config.autoGenerateOszilloskop);
	autoosca.addActionListener(this);
	menu.add(autoosca);
	autospec = new JCheckBoxMenuItem("auto generate spectogram for opened media",root.config.autoGenerateSpectrum);
	autospec.addActionListener(this);
	menu.add(autospec);
	menu.addSeparator();
	autoopen = new JCheckBoxMenuItem("show open dialog when program starts",root.config.openDialog);
	autoopen.addActionListener(this);
	menu.add(autoopen);	
	menu.addSeparator();
	tooltip = new JCheckBoxMenuItem("show tool tip");
	tooltip.addActionListener(this);
	menu.add(tooltip);
	menu.addSeparator();
	debug = new JCheckBoxMenuItem("switch console debug trace",root.DEBUG);
	debug.addActionListener(this);
	menu.add(debug);
	menu = new JMenu("Annotation");
	menuItem = new JMenuItem("select upper layer");
	menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_UP,ActionEvent.CTRL_MASK));
	menuItem.addActionListener(this);
	menu.add(menuItem);
	menuItem = new JMenuItem("select lower layer");
	menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN,ActionEvent.CTRL_MASK));
	menuItem.addActionListener(this);
	menu.add(menuItem);
	menuItem = new JMenuItem("select next annotation element");
	menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT,ActionEvent.CTRL_MASK));
	menuItem.addActionListener(this);
	menu.add(menuItem);
	menuItem = new JMenuItem("select previous annotation element");
	menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT,ActionEvent.CTRL_MASK));
	menuItem.addActionListener(this);
	menu.add(menuItem);
	menu.addSeparator();
	menuItem = new JMenuItem("move layer up");
	menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_UP,ActionEvent.CTRL_MASK | ActionEvent.SHIFT_MASK));
	menuItem.addActionListener(this);
	menu.add(menuItem);
	menuItem = new JMenuItem("move layer down");
	menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN,ActionEvent.CTRL_MASK | ActionEvent.SHIFT_MASK));
	menuItem.addActionListener(this);
	menu.add(menuItem);
	menu.addSeparator();
	menuItem = new JMenuItem("add annotation element");
	menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A,ActionEvent.CTRL_MASK));
	menuItem.addActionListener(this);
	menu.add(menuItem);
	menuItem = new JMenuItem("remove annotation element");
	menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R,ActionEvent.CTRL_MASK));
	menuItem.addActionListener(this);
	menu.add(menuItem);
	menu.addSeparator();
	menuItem = new JMenuItem("add child to element");
	menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C,ActionEvent.CTRL_MASK));
	menuItem.addActionListener(this);
	menu.add(menuItem);
	menuItem = new JMenuItem("remove child from element");
	menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D,ActionEvent.CTRL_MASK));
	menuItem.addActionListener(this);
	menu.add(menuItem);
	menu.addSeparator();
	menuItem = new JMenuItem("new time segment");
	menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,ActionEvent.CTRL_MASK));
	menuItem.addActionListener(this);
	menu.add(menuItem);
	menuItem = new JMenuItem("end time segment");
	menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_M,ActionEvent.CTRL_MASK));
	menuItem.addActionListener(this);
	menu.add(menuItem);
	menu.addSeparator();
	menuItem = new JMenuItem("save annotation");
	menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X,ActionEvent.CTRL_MASK));
	menuItem.addActionListener(this);
	menu.add(menuItem);
	add(menu);
	menu.addSeparator();
	menuItem = new JMenuItem("NQL search");
	menuItem.addActionListener(this);
	menu.add(menuItem);
	add(menu);

    menu = new JMenu("Help");
	menuItem = new JMenuItem("launch help");
	menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1,0));
	
	//TODO//
	//menuItem.addActionListener(new javax.help.CSH.DisplayHelpFromSource(root.help.hb));
	menu.add(menuItem);
	menu.addSeparator();
	menuItem = new JMenuItem("about amigram");
	menuItem.addActionListener(this);
	menu.add(menuItem);
	add(menu);
    }
    public void actionPerformed(ActionEvent e) {
	
	root.printDebugMessage("Menue Event: "+e.getActionCommand());
	
	if (e.getActionCommand().equals("add annotation element"))
	    {
		root.printDebugMessage("ADD an annotation elements");
		int activelayer = root.layermanager.getActiveLayer();
 		if (activelayer == -1) {
 		    JOptionPane.showMessageDialog(root,"Please select a layer first.","Info",1);
 		}
 		else 
 		    root.layermanager.getLayerByID(activelayer).addAnnotationLayerElement();
	    }
	if (e.getActionCommand().equals("remove annotation element"))
	    {
		root.printDebugMessage("REMOVE an annotation elements");
		int activelayer = root.layermanager.getActiveLayer();
		if (activelayer == -1) {
		    JOptionPane.showMessageDialog(root,"Please select a layer first.","Info",1);
		}
		else root.layermanager.getLayerByID(activelayer).removeTrackElement();
	    }
	if (e.getActionCommand().equals("about amigram"))
	    {
		JOptionPane.showMessageDialog(root,"AMI Graphical Represntation and Annotation Module\n(c) DFKI - christoph lauer\nwww.amiproject.org","Info",1);
	    }
	if (e.getActionCommand().equals("close annotation"))
	    {
		JOptionPane.showMessageDialog(root,"to be implemented","Info",1);
	    }

	if (e.getActionCommand().equals("quit"))
	    {
		root.printDebugMessage("Window closing message");
		root.quit();
	    }

	if (e.getActionCommand().equals("open annotation"))
	    {
		root.synchronizer.stopAllPlayers(new String("--- open signal ---"));
		initializeDataBank();
	    }
	if (e.getActionCommand().equals("reload annotation"))
	    {
		root.synchronizer.stopAllPlayers(new String("--- open signal ---"));
		reload = true;
		initializeDataBank();
	    }
	if (e.getActionCommand().equals("open corpus signal"))
	    {
		root.synchronizer.stopAllPlayers(new String("--- open signal ---"));
	   	root.nomcommunicator.openMediaSignals();
	    }
	if (e.getActionCommand().equals("open other signal"))
	    {
		root.synchronizer.stopAllPlayers(new String("--- open signal ---"));
		int returnVal = chooser.showOpenDialog(root);
		if(returnVal == JFileChooser.APPROVE_OPTION) {
		    MediaPlayer player = new MediaPlayer(root,chooser.getSelectedFile());
		    if (player.playerRealized == true)
			{
			    if (root.config.automaticalOrderWindows==false)
				{
				    root.addSubWindow(player,200,200,player.getWidth(),player.getHeight());
				    player.pack();
				}
			    else
				{ 
				    root.addSubWindow(player,301,0,346,301);
				    player.pack();
				    root.placeSubwindows();
				}
			    root.synchronizer.registerListener(player,player.getTitle());
			    if (root.config.autoGenerateOszilloskop==true)
				try
				    {
					String url = chooser.getSelectedFile().toURL().toString();
					String name = chooser.getSelectedFile().getName();
					root.getOtabWindow().toolbar.addOszillogramLayerForUrl(url,name);
				    }
				catch (Exception exc) {exc.printStackTrace();}
			    if (root.config.autoGenerateSpectrum==true)
				try
				    {
					String url = chooser.getSelectedFile().toURL().toString();
					String name = chooser.getSelectedFile().getName();
					root.getOtabWindow().toolbar.addSpectralLayerForUrl(url,name);
				    }
				catch (Exception exc) {exc.printStackTrace();}
			}
		    else
			{
			    player.dispose();
			    JOptionPane.showMessageDialog(root,"Error while create the meda player for this file.\nPerhaps the format is not a supported.","Error",0);
			}
		}
	    }
	if (e.getActionCommand().equals("restore initial window positions"))
	    {
		root.restoreWindowPositions();
	    }	
	if (e.getActionCommand().equals("fullscreen"))
	    {
		root.config.fullScreen = !(root.config.fullScreen);
		root.updateFullscreenMode();
	    }
	if (e.getActionCommand().equals("automatical order windows"))
	    {
		root.config.automaticalOrderWindows = !(root.config.automaticalOrderWindows);
		if(root.config.automaticalOrderWindows==true) root.placeSubwindows();
	    }
	if (e.getActionCommand().equals("synchronize media time changes"))
	    {
		root.config.synchronizeMediaTimeChanges = !(root.config.synchronizeMediaTimeChanges);
		root.getControlWindow().syncevents.setSelected(root.config.synchronizeMediaTimeChanges);
	    }
	if (e.getActionCommand().equals("synchronize media start/stop events"))
	    {
		root.config.synchronizeMediaStartStopEvents = !(root.config.synchronizeMediaStartStopEvents);
		root.getControlWindow().syncstart.setSelected(root.config.synchronizeMediaStartStopEvents);
	    }
	if (e.getActionCommand().equals("close signal"))
	    {
		root.closeMultimediaWindow(null);
	    }
	if (e.getActionCommand().equals("NQL search"))
	    {
		    root.nomcommunicator.gui.popupSearchWindow();
	    }
	if (e.getActionCommand().equals("auto generate oszillogram for opened media"))
	    {
		root.config.autoGenerateOszilloskop = !(root.config.autoGenerateOszilloskop);
		root.getControlWindow().genosca.setSelected(root.config.autoGenerateOszilloskop);
	    }
	if (e.getActionCommand().equals("auto generate spectogram for opened media"))
	    {
		root.config.autoGenerateSpectrum = !(root.config.autoGenerateSpectrum);
		root.getControlWindow().genspec.setSelected(root.config.autoGenerateSpectrum);
	    }
	if (e.getActionCommand().equals("show open dialog when program starts"))
	    {
		root.config.openDialog = !(root.config.openDialog);
		root.getControlWindow().showopendialog.setSelected(root.config.openDialog);
	    }
	if (e.getActionCommand().equals("move layer up"))
	    {
		root.layermanager.MoveActiveLayerUp(true);
		root.statusbar.updateStatusText("Layer moved up.");
	    }
	if (e.getActionCommand().equals("move layer down"))
	    {
		root.layermanager.MoveActiveLayerDown(true);
		root.statusbar.updateStatusText("Layer moved down.");
	    }                            
	if (e.getActionCommand().equals("select previous annotation element"))
	    {
		root.layermanager.MoveActiveAnnoElementLeft(true);
		root.statusbar.updateStatusText("Select Previous Anno Element.");
	    }
	if (e.getActionCommand().equals("select next annotation element"))
	    {
		root.layermanager.MoveActiveAnnoElementRight(true);
		root.statusbar.updateStatusText("Select Next Anno Element.");
	    }
	if (e.getActionCommand().equals("select lower layer"))
	    {
		root.layermanager.SelectLowerActiveLayer();
	    }
	if (e.getActionCommand().equals("select upper layer"))
	    {
		root.layermanager.SelectUpperActiveLayer();
	    }
	if (e.getActionCommand().equals("switch console debug trace"))
	    {
		root.DEBUG = !root.DEBUG;
		System.out.println("DEBUG: enable/disable debug trace to:"+root.DEBUG);
	    }	
	if (e.getActionCommand().equals("new time segment"))
	    {
		root.otf.beginOrNewBeginEvent();
	    }
	if (e.getActionCommand().equals("end time segment"))
	    {
		root.otf.beginOrEndEvent();
	    }
	if (e.getActionCommand().equals("save annotation"))
	    {
		root.nomcommunicator.saveAnnotationToNom();
	    }
	if (e.getActionCommand().equals("add child to element"))
	    {
		int activeL = root.layermanager.getActiveLayer();
		if (activeL == -1){
		    root.statusbar.updateStatusText("Select Element first.");
		    JOptionPane.showMessageDialog(root,"First select element.","No Element",1);
		}
		AnnotationLayer al = root.layermanager.getLayerByID(activeL);
		int activeE = al.activeannotationelement;
		if (activeE == -1) {
		    root.statusbar.updateStatusText("Select Element first.");
		    JOptionPane.showMessageDialog(root,"First select element.","No Element",1);
		}
		AnnotationElement ae = (AnnotationElement)al.annotationelements.get(activeE);
		root.lastae = ae;
		root.lastActiveLayer = activeL;
		root.lastActiveAnnoElement = activeE;
		root.addChild = true;
		root.statusbar.updateStatusText("Select CHILD to ADD");
		root.setCursorForAllWidgets(2);
	    }
	if (e.getActionCommand().equals("remove child from element"))
	    {
		int activeL = root.layermanager.getActiveLayer();
		if (activeL == -1){
		    root.statusbar.updateStatusText("Select Element first.");
		    JOptionPane.showMessageDialog(root,"First select element.","No Element",1);
		}
		AnnotationLayer al = root.layermanager.getLayerByID(activeL);
		int activeE = al.activeannotationelement;
		if (activeE == -1) {
		    root.statusbar.updateStatusText("Select Element first.");
		    JOptionPane.showMessageDialog(root,"First select element.","No Element",1);
		}
		AnnotationElement ae = (AnnotationElement)al.annotationelements.get(activeE);
		root.lastae = ae;
		root.lastActiveLayer = activeL;
		root.lastActiveAnnoElement = activeE;
		root.removeChild = true;
		root.statusbar.updateStatusText("Select CHILD to REMOVE");
		root.setCursorForAllWidgets(2);
	    }
	if (e.getActionCommand().equals("plugin manager"))
	    {
		root.pluginManager.showGui();
	    }
    }
   
    /**
     * Open corpus from the NOM and build the annotation board.
     * Sequential: 1.)Select a corpus file  2.)Check if the corpus exist 3.)Instanciate the NiteMetaData class
     * 4.)Check if the corpus type is an stand off corpus. 5.)Instanciate the NiteWrite corpus 
     * 6.)Buld the annotation Bar 7.)Build the Annotation Bar and read out the track Elements
     * 7.)VideoPlayer and some other stuff
     */
    public void initializeDataBank()
    {
	root.setCursorForAllWidgets(1);
	root.statusbar.updateStatusText("Open the corpus from the NOM");
	Thread.yield();
	boolean success;
	root.printDebugMessage("INITIALIZE DATABANK FROM THE NOM API");
	// Open the file in the filechooser.
	root.statusbar.updateStatusText("Select a corpus file.");

	// load same file while reload
	if (reload == true)
	    reload = false;
	else 
	    {
		// select a file with the filechooser or uder the default file for DURCHSTART
		if (root.DURCHSTART == false)
		    root.nomcommunicator.chooseMetaDataFile();
		else 
		    root.DURCHSTART = false;
	    }
	
	// Check if the corpus exist
	if (root.nomcommunicator.getCorpusFilePath() == null) {
	    root.statusbar.updateStatusText("No file selected in the filechooser.");
 	    root.statusbar.updateStatusValue(100);
	    root.setCursorForAllWidgets(0);
	    return;
	}
 	root.statusbar.updateStatusValue(10);
 	root.statusbar.updateStatusText("Opening the corpus: " + root.nomcommunicator.getCorpusFileName());
	// Instanciate the NiteMetaData class
	if (root.nomcommunicator.access() == false) {
	    JOptionPane.showMessageDialog(root,"The NiteMetaData could not instantiated !","Error",0);
	    JOptionPane.showMessageDialog(root,"The NiteMetaData could not instantiated !","Error",0);
 	    root.statusbar.updateStatusText("Metadata could not instantiated.");
 	    root.statusbar.updateStatusValue(100);
	    root.setCursorForAllWidgets(0);
	    return;
	}
	else
	    root.printDebugMessage("CORPUS INSTANCIATED");
 	root.statusbar.updateStatusValue(20);
	root.statusbar.updateStatusText("Check corpus type.");
	Thread.yield();  // Give the status a chance to paint
	// Check the corpus Type
	if (root.nomcommunicator.checkCorpusType() == false) {
	    JOptionPane.showMessageDialog(root,"This Corpus is a SIMPLE or a STANDALONE corpus\nand no STANDOFF corpus. No NOM has been loaded !","Error",0);
 	    root.statusbar.updateStatusText("This file is no standoff corpus.");
 	    root.statusbar.updateStatusValue(100);
	    root.setCursorForAllWidgets(0);
	    return;
	}	
	else 
	    root.printDebugMessage("CORPUS CHECKED");
 	root.statusbar.updateStatusValue(40);
 	root.statusbar.updateStatusText("Open the corpus for writing.");
	// Instanciate the NiteWrite corpus
	if (root.nomcommunicator.loadForWriting()==false) {
	    JOptionPane.showMessageDialog(root,"Could not open the corpus for writing !","Error",0);
 	    root.statusbar.updateStatusText("Could not open corpus for writing.");
 	    root.statusbar.updateStatusValue(100);
	    root.setCursorForAllWidgets(0);
	    return;
	}
	else 
	    root.printDebugMessage("CORPUS OPENED FOR WRITING");
 	root.statusbar.updateStatusValue(60);
 	root.statusbar.updateStatusText("Build the OTAB.");
	// Raed the video duration and set it to the OTAB
	double duration = root.nomcommunicator.nomwritecorpus.getCorpusDuration();
	root.layermanager.setVideoDuration(duration);
	// Build the annotation Bar
 	if (root.nomcommunicator.buildAnnotationTree()==false) {
	    JOptionPane.showMessageDialog(root,"Could not build the internal\nannotation tree from the corpus !","Error",0);
 	    root.statusbar.updateStatusText("Failed to build the internal annotation.");
 	    root.statusbar.updateStatusValue(100);
	    root.setCursorForAllWidgets(0);
	    return;
	}
	// Read out the track elements from the Observation.
	if (root.nomcommunicator.readTheAnnotaionsFromTheCorups()==false) {
	    JOptionPane.showMessageDialog(root,"Could not read the annotation track\nelements from the corpus !","Error",0);
 	    root.statusbar.updateStatusText("Failed to read the track elements !.");
 	    root.statusbar.updateStatusValue(100);
	    root.setCursorForAllWidgets(0);
	    return;
	}
	root.printDebugMessage("ANNOTATION TREE SUCCESSFUL BUILT");
 	root.statusbar.updateStatusValue(80);
 	root.statusbar.updateStatusText("Opening the video file");
	// AND now lets paint the OTAB window.
	root.getOtabWindow().buildAnnotationBoardPanel();
	// checks for autozoom
	if (root.config.autoZoom == true) {
	    double value = root.layermanager.getVideoDuration()/10.0*(double)root.otabwin.scrollpane.getWidth();
	    root.getControlWindow().zoomslider.setValue((int)value);
	}
	// Order the layers
	if (root.getControlWindow().order.isSelected()==true)
	    {
		root.layermanager.orderLayers();
		root.getOtabWindow().buildAnnotationBoardPanel();
	    }
	// Access the video file name
 	root.statusbar.updateStatusText("Select Media Files");
 	root.nomcommunicator.openMediaSignals();
	// Upadtes the information view on the control window tabbed pane.
	root.getControlWindow().infotable.updateData();
	root.setTitle("AMIGRAM - AMI Graphical Representation and Annotation Module  -  "+root.nomcommunicator.getCorpusFileName());
	// Sucess Message
 	root.statusbar.updateStatusText("Corpus loaded:"+"  "+root.nomcommunicator.sumoffoundedannotationelements+" Elements Parsed");
 	root.statusbar.updateStatusValue(100);
	JOptionPane.showMessageDialog(root,"Corpus is INITIALIZED.\nParsed "+root.nomcommunicator.sumoffoundedannotationelements+" annotation elemets.","Success",1);
	root.setCursorForAllWidgets(0);
 	root.nomcommunicator.gui = new net.sourceforge.nite.search.GUI(root.nomcommunicator.nomwritecorpus);
	root.nomcommunicator.gui.registerResultHandler(root.nomcommunicator.queryresulthandler);
    }
    
    public boolean isToolTipSelected(){
    	return tooltip.isSelected();
    }
}