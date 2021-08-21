/******************************************************************
 *                 ControlEventHandler.java  -  description
 *                    -----------------------
 * @author 		  Christoph Lauer
 * @version 		  0.1  
 * @see			  www.amiproject.de
 * copyright		: (C) 2003 @ christoph lauer
 * begin 		: Thu Okt 30 17:00:00 CET 2003
 * last save   	        : Time-stamp: <2005-03-11 20:30:58 christoph> 
 * compiler    	        : java version 1.4.2 
 * operating system     : Linux 
 * editor		: xemacs 21.4
 * description          : Eventhandling for the Control Window.
 ******************************************************************/

package de.dfki.ami.amigram.tools;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.dfki.ami.amigram.gui.DesktopFrame;

public class ControlEventHandler 
    implements ActionListener,ChangeListener
{
    public DesktopFrame root;
    public JFileChooser chooser = new JFileChooser();

    public ControlEventHandler(DesktopFrame r)
    {
	root = r;
    }
    
    /**
     * This function opens a file chooser dialog and asks the user for a multimedia file.
     */
    private void openevent()
    {
	chooser.setDialogTitle("Select Video or Audiofile");
	int returnVal = chooser.showOpenDialog(root);
	if(returnVal == JFileChooser.APPROVE_OPTION) 
	    {
		closeevent();
		String url = new String("file:/" + chooser.getSelectedFile().getPath());
		root.printDebugMessage( "FILECHOOSER " + url);
		javax.media.MediaLocator ml = new javax.media.MediaLocator(url);
		//mainframe.getVideoWindow().player.open(ml);
	    }
    }

    /**
     * The function closes the video file.
     */
    private void closeevent()
    {
// 	if (mainframe.getVideoWindow().player.p != null){
// 	    if (mainframe.getVideoWindow().player.p.getState() == javax.media.Processor.Started)
// 		mainframe.getVideoWindow().player.close();
// 	    System.gc();
// 	}
// 	if (mainframe.getVideoWindow().player.vc != null)
// 	    mainframe.getVideoWindow().remove(mainframe.getVideoWindow().player.vc);
// 	if (mainframe.getVideoWindow().player.cc != null)
// 	    mainframe.getVideoWindow().remove(mainframe.getVideoWindow().player.cc);
// 	mainframe.getVideoWindow().player = new de.dfki.nite.gram.video.Player(mainframe.getVideoWindow());
// 	mainframe.getVideoWindow().paintDefaultImage();
//     	mainframe.getVideoWindow().repaint();
// 	mainframe.controlwindow.infotable.updateData();
// 	System.gc();
    }
    
    /**
     * Open corpus from the NOM and build the annotation board.
     * Sequential: 1.)Select a corpus file  2.)Check if the corpus exists 3.)Instantiate the NiteMetaData class
     * 4.)Check if the corpus type is an stand off corpus. 5.)Instantiate the NiteWrite corpus 
     * 6.)Create the annotation Bar 7.)Build the Annotation Bar and read out the track Elements
     * 7.)Add VideoPlayer and some other stuff
     */
    private void initializeDataBank()
    {
	//mainframe.statusbar.updateStatusText("Open the corpus from the NOM");
	Thread.yield();
	boolean success;
	root.printDebugMessage("INITIALIZE DATABANK FROM THE NOM API");
	// Open the file in the filechooser.
	root.statusbar.updateStatusText("Select a corpus file.");
	root.nomcommunicator.chooseMetaDataFile();
	// Check if the corpus exist
	if (root.nomcommunicator.getCorpusFilePath() == null) {
	    root.statusbar.updateStatusText("No file selected in the filechooser.");
	    root.statusbar.updateStatusValue(100);
	    return;
	 }
	root.statusbar.updateStatusValue(10);
	root.statusbar.updateStatusText("Opening the corpus: " + root.nomcommunicator.getCorpusFileName());
	// Instanciate the NiteMetaData class
	if (root.nomcommunicator.access() == false) {
	    JOptionPane.showMessageDialog(root,"The NiteMetaData could not instantiated !","Error",0);
	    root.statusbar.updateStatusText("Metadata couldn't be instantiated.");
	    root.statusbar.updateStatusValue(100);
	    return;
	}
	else
	    root.printDebugMessage("CORPUS INSTANTIATED");
	root.statusbar.updateStatusValue(20);
	root.statusbar.updateStatusText("Check corpus type.");
	Thread.yield();  // Give the status a chance to paint
	// Check the corpus Type
	if (root.nomcommunicator.checkCorpusType() == false) {
	    JOptionPane.showMessageDialog(root,"This Corpus is a SIMPLE or a STANDALONE corpus\nand no STANDOFF corpus. No NOM has been loaded !","Error",0);
	    root.statusbar.updateStatusText("This file is no standoff corpus.");
	    root.statusbar.updateStatusValue(100);
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
	    return;
	}
	// Read out the track elements from the Observation.
	if (root.nomcommunicator.readTheAnnotaionsFromTheCorups()==false) {
	    JOptionPane.showMessageDialog(root,"Could not read the annotation track\nelements from the corpus !","Error",0);
	    root.statusbar.updateStatusText("Failed to read the track elemets !.");
	    root.statusbar.updateStatusValue(100);
	    return;
	}
	root.printDebugMessage("ANNOTATION TREE SUCCESSFUL BUILT");
	root.statusbar.updateStatusValue(80);
	root.statusbar.updateStatusText("Opening the video file");
	// Upadtes the information view on the control window tabbed pane.
	root.getControlWindow().infotable.updateData();
	// AND now lets paint the OTAB window.
	root.getOtabWindow().buildAnnotationBoardPanel();
	// Access the video file name
// 	root.nomcommunicator.selectmediafile = true;
// 	String file = root.nomcommunicator.getMediaFilePath();
// 	String curDir = System.getProperty("user.dir");
// 	file = curDir+"/"+file;
// 	root.printDebugMessage("MEDIA FILE PATH:"+file);
	root.nomcommunicator.openMediaSignals();
	// Start the video player
// 	String url = new String("file:/" + file);
// 	javax.media.MediaLocator ml = new javax.media.MediaLocator(url);
// 	mainframe.getVideoWindow().player.open(ml);
	// And repaint the otab window
      //root.getOtabWindow().repaint();
	// Sucess Message
	root.statusbar.updateStatusText("Corpus loaded:"+"  "+root.nomcommunicator.sumoffoundedannotationelements+" Elements Parsed");
	root.statusbar.updateStatusValue(100);
	JOptionPane.showMessageDialog(root,"Corpus is INITIALIZED.\nParsed "+root.nomcommunicator.sumoffoundedannotationelements+" annotation elemets.","Success",1);
    }
    
    /**
     * This function is for the ActionListener
     */
    public void actionPerformed(ActionEvent e) 
    {
 	root.printDebugMessage("ACTION PERFORMED IN EVENT HANDLER CONTROLWINDOW");
 	if (e.getSource() == root.getControlWindow().antialias) {
	    smooth.util.SmoothUtilities.setAntialias(root.getControlWindow().antialias.isSelected());
	    smooth.util.SmoothUtilities.setFractionalMetrics(root.getControlWindow().antialias.isSelected());
	    root.repaint();
	}
 	if (e.getSource() == root.getControlWindow().order) {
	    if(root.getControlWindow().order.isSelected() == true)
		{
		    root.layermanager.orderLayers();
		    root.layermanager.orderLayers();
		    root.layermanager.orderLayers();
		    if (root.otabwin.defaultimageispainted == false)
			root.getOtabWindow().buildAnnotationBoardPanel();
		}
	}
	if (e.getSource() == root.getControlWindow().orderbutton) {
	    root.layermanager.orderLayers();
	    root.layermanager.orderLayers();
	    root.layermanager.orderLayers();
	    if (root.otabwin.defaultimageispainted == false)
		root.getOtabWindow().buildAnnotationBoardPanel();
	}
 	if (e.getSource() == root.getControlWindow().synctimeline) {
	    root.config.synchronizeTimeline = !(root.config.synchronizeTimeline);
	    root.getControlWindow().synctimelinecont.setEnabled(root.config.synchronizeTimeline);
	    root.getControlWindow().synctimelineplaying.setEnabled(root.config.synchronizeTimeline);
	}
 	if (e.getSource() == root.getControlWindow().synctimelinecont) {
	    root.config.synchronizeTimelineContinuously = !(root.config.synchronizeTimelineContinuously);
	}
 	if (e.getSource() == root.getControlWindow().synctimelineplaying) {
	    root.config.synchronizeTimelinePlaying = !(root.config.synchronizeTimelinePlaying);
	}
 	if (e.getSource() == root.getControlWindow().track) {
	    root.config.recognizeElementsWhilePlaying = !(root.config.recognizeElementsWhilePlaying);
	}
 	if (e.getSource() == root.getControlWindow().secs) {
	    root.config.secoundsInTimelineal = !(root.config.secoundsInTimelineal);
	    if (root.layermanager.getLayerByID(0) != null)
		root.layermanager.getLayerByID(0).repaint();
	}
 	if (e.getSource() == root.getControlWindow().syncevents) {
	    root.menubar.syncevents.doClick();
	}
 	if (e.getSource() == root.getControlWindow().syncstart) {
	    root.menubar.syncstartstop.doClick();
	}
 	if (e.getSource() == root.getControlWindow().genosca) {
	    root.menubar.autoosca.doClick();
	}
 	if (e.getSource() == root.getControlWindow().genspec) {
	    root.menubar.autospec.doClick();
	}
 	if (e.getSource() == root.getControlWindow().autoorderwins) {
	    root.menubar.autoorder.doClick();
	}
 	if (e.getSource() == root.getControlWindow().showopendialog) {
	    root.menubar.autoopen.doClick();
	}
  	if (e.getSource() == root.getControlWindow().fullscreen) {
	    root.menubar.fullscreen.doClick();
	}
  	if (e.getSource() == root.getControlWindow().order) {
	    root.config.automaticalOrderLayers = !(root.config.automaticalOrderLayers);
	}
  	if (e.getSource() == root.getControlWindow().spider) {
	    java.util.Iterator it = root.nomcommunicator.annohashmap.values().iterator();
	    while (it.hasNext()){
		AnnotationElement ae = (AnnotationElement)it.next();
		Graphics g = root.otabwin.layercollectionpanel.getGraphics();
		root.otabwin.layercollectionpanel.drawConditionNet(g,ae);
	    }
	}
  	if (e.getSource() == root.getControlWindow().autozoom) {
	    root.config.autoZoom = !(root.config.autoZoom);
	}
// 	if (e.getSource() == root.getControlWindow().toolbar.savebutton || e.getSource() == mainframe.getControlWindow().menubar.saveitem) {
// 	    root.printDebugMessage("SAVE THE CORPUS");
// 	    // Save the corpus and check if it could be saved sucessfull
// 	    if (mainframe.nomcommunicator.saveAnnotationToNom() == false) 
// 		JOptionPane.showMessageDialog(mainframe,"The corpus could not be saved sucessfully !!!","ERROR",0);
// 	    else {
// 		JOptionPane.showMessageDialog(mainframe,"The corpus was sucessfully saved !!!","INFO",JOptionPane.INFORMATION_MESSAGE);
// 		mainframe.statusbar.updateStatusText("Corpus stored.");
// 	    }
//	}
// 	if (e.getSource()== mainframe.getControlWindow().toolbar.querybutton  || e.getSource() == mainframe.getControlWindow().menubar.corpitem)
// 	    {initializeDataBank();}
// 	if (e.getSource() == mainframe.getControlWindow().menubar.openitem)
// 	    {openevent();}
// 	if (e.getSource() == mainframe.getControlWindow().menubar.closeitem)
// 	    {
// 		closeevent();
// 		mainframe.getVideoWindow().reshape  (3,3,490,340);
// 	    }
// 	if (e.getSource() == mainframe.getControlWindow().menubar.quititem)
// 	    {System.exit(0);}
// 	if (e.getSource() == mainframe.getControlWindow().menubar.expitem)
// 	    {
// 		root.printDebugMessage("Ei jo, action.....");
// 	    }
// 	if (e.getSource() == mainframe.getControlWindow().menubar.upditem)
// 	    {
// 		root.printDebugMessage("Ei jo, action.....");
// 	    }
// 	if (e.getSource() == mainframe.getControlWindow().menubar.infoitem)
// 	    {infoevent();}
// 	if (e.getSource() == mainframe.getControlWindow().menubar.sysitem)
// 	    {SystemProperties systemproperties = new SystemProperties(mainframe);}
// 	if (e.getSource() == mainframe.getControlWindow().menubar.performanceitem || e.getSource() == mainframe.getControlWindow().toolbar.rambutton)
// 	    {
// 		final PerformanceMonitor demo = new PerformanceMonitor();
// 		final JFrame f = new JFrame("Performance Monitor");
//  		demo.surf.start();
//  		WindowListener l = new WindowAdapter() {
//  			public void windowClosing(WindowEvent e) {f.dispose();}
//  			public void windowDeiconified(WindowEvent e) { demo.surf.start(); }
//  			public void windowIconified(WindowEvent e) { demo.surf.stop(); }
//  		    };
//  		f.addWindowListener(l);
// 		f.getContentPane().add("Center", demo);
// 		f.pack();
// 		f.setSize(new Dimension(150,140));
// 		demo.putClientProperty("JInternalFrame.isPalette", Boolean.TRUE); 
// 		f.setVisible(true);
// 	    }
// 	if (e.getSource() == mainframe.getControlWindow().lafcombobox)
// 	    {mainframe.changeLookAndFeel(mainframe.getControlWindow().lafcombobox.getSelectedIndex());}
// 	if (e.getSource() == mainframe.getControlWindow().colorscemecombobox)
// 	    {mainframe.getOtabWindow().repaint();}
// 	if (e.getSource() == mainframe.getControlWindow().movedcheckbox)
// 	    {
// 		mainframe.getControlWindow().interpolatedcheckbox.setEnabled(mainframe.getControlWindow().movedcheckbox.isSelected());
// 	    }
// 	if (e.getSource() == mainframe.getControlWindow().gvmpcheckbox)
// 	    {
// 		mainframe.getControlWindow().p1.setEnabled(mainframe.getControlWindow().gvmpcheckbox.isSelected());
// 		mainframe.getControlWindow().gvmshapecombobox.setEnabled(mainframe.getControlWindow().gvmpcheckbox.isSelected());
// 		mainframe.getControlWindow().gvmcolorcombobox.setEnabled(mainframe.getControlWindow().gvmpcheckbox.isSelected());
// 		mainframe.getControlWindow().markupnowbutton.setEnabled(mainframe.getControlWindow().gvmpcheckbox.isSelected());
// 		mainframe.getControlWindow().gvmcheckbox.setEnabled(mainframe.getControlWindow().gvmpcheckbox.isSelected());
// 		mainframe.getControlWindow().deletemarkupbutton.setEnabled(mainframe.getControlWindow().gvmpcheckbox.isSelected());
// 		mainframe.getControlWindow().movedcheckbox.setEnabled(mainframe.getControlWindow().gvmpcheckbox.isSelected());
// 		mainframe.getControlWindow().annotable.markupnowbutton.setEnabled(mainframe.getControlWindow().gvmpcheckbox.isSelected());
// 		mainframe.getControlWindow().annotable.deletemarkupbutton.setEnabled(mainframe.getControlWindow().gvmpcheckbox.isSelected());
// 		if (mainframe.getControlWindow().movedcheckbox.isSelected())
// 		    mainframe.getControlWindow().interpolatedcheckbox.setEnabled(mainframe.getControlWindow().gvmpcheckbox.isSelected());
// 		if (mainframe.getControlWindow().gvmpcheckbox.isSelected() == false)
// 		    closeevent();
// 	    }
// 	// Add and remove GVM's event handler.
// 	if (e.getSource() == mainframe.getControlWindow().markupnowbutton || e.getSource() == mainframe.getControlWindow().annotable.markupnowbutton) {
// 	    // First check if there is a track element selected
// 	    int activelayer = mainframe.layermanager.getActiveLayer();
// 	    if (activelayer == -1) {
// 		mainframe.statusbar.updateStatusText("Please select a track element first.");
// 		JOptionPane.showMessageDialog(mainframe,"Please select a track element first.","Information",1);
// 		return;
// 	    }
// 	    int activetrack = mainframe.layermanager.getLayerByID(activelayer).activeannotationelement;
// 	    if (activetrack == -1) {
// 		mainframe.statusbar.updateStatusText("Please select a track element first.");
// 		JOptionPane.showMessageDialog(mainframe,"Please Select a track element first.","Information",1);
// 		return;
// 	    }			
// 	    AnnotationElement ae  = (AnnotationElement)mainframe.layermanager.getLayerByID(activelayer).annotationelements.get(activetrack);
// 	    // Check if there is no other GVM connected with the track element
// 	    if (ae.getGvmData()!=null) {
// 		mainframe.statusbar.updateStatusText("There is an other GVM linked to this track element.");
// 		JOptionPane.showMessageDialog(mainframe,"There is an other GVM linked to\nthis track element. Please remove\nthe old one first.","Remove Old One First.",0);
// 		return;
// 	    }			
// 	    // This Flag is used from the callbackFromTheVideoPlayerWhenMousePressed method anf the time lineal painter.
// 	    videomouseeventishot = true;
//  	    mainframe.layermanager.getLayerByID(0).repaint();
// 	    // Static or Dynamic
// 	    if (mainframe.getControlWindow().movedcheckbox.isSelected() == false) {
// 		mainframe.statusbar.updateStatusText("Select the position on the video player.");
// 		JOptionPane.showMessageDialog(null,"Please select the position for the GVM on\nthe video player","Select Position",1);
// 	    }
// 	    else {
// 		mainframe.statusbar.updateStatusText("Select times and positions. Finish with right mouse.");
// 		JOptionPane.showMessageDialog(null,"Please select the multiple positions for\nthe moved GVM on the video player for\ndiffrerent time points. Press the right\nbutton to end the visual markup.","Select Position",1);
// 	    }
// 	}
// 	// This is for the removing of the GVM's
// 	if (e.getSource() == mainframe.getControlWindow().deletemarkupbutton || e.getSource() == mainframe.getControlWindow().annotable.deletemarkupbutton) {
// 	    // First check if there is a track element selected
// 	    int activelayer = mainframe.layermanager.getActiveLayer();
// 	    if (activelayer == -1) {
// 		mainframe.statusbar.updateStatusText("Please select an track element first.");
// 		JOptionPane.showMessageDialog(mainframe,"Please select a track element first.","Information",1);
// 		return;
// 	    }
// 	    int activetrack = mainframe.layermanager.getLayerByID(activelayer).activeannotationelement;
// 	    if (activetrack == -1) {
// 		mainframe.statusbar.updateStatusText("Please select an track element first.");
// 		JOptionPane.showMessageDialog(mainframe,"Please select an track element first.","Information",1);
// 		return;
// 	    }			
// 	    AnnotationElement ae  = (AnnotationElement)mainframe.layermanager.getLayerByID(activelayer).annotationelements.get(activetrack);
// 	    if (ae.getGvmData() == null) {
// 		mainframe.statusbar.updateStatusText("This track element contains no GVM data.");
// 		JOptionPane.showMessageDialog(mainframe,"This track element contains no GVM data\nthat can be removed !","Not Possible",0);
// 		return;
// 	    }
// 	    int answer = JOptionPane.showConfirmDialog(mainframe,"Do you want to remove the GVM\nfrom the selected track element ?","Are you shure ?",JOptionPane.YES_NO_OPTION);
// 	    if (answer == JOptionPane.YES_OPTION) {
// 		mainframe.statusbar.updateStatusText("GVM from the track element removed.");
// 		ae.setGvmData(null);
// 		mainframe.getVideoWindow().player.updatePlayerView();
// 	    }
// 	}
// 	// Change the spectrum color
// 	if (e.getSource() == mainframe.getControlWindow().firespeccheckbox) {
// 	    AnnotationLayerManagement alm = mainframe.layermanager;
// 	    // Search for the spectral layer
// 	    for (int i=0;i<alm.layervector.size();i++) {
// 		AnnotationLayer al = (AnnotationLayer) alm.layervector.get(i);
// 		if (al.getLayerType()==AnnotationLayer.SPECTRAL) {
// 		    al.repaintspectrum = true;
// 		    al.repaint();
// 		}
// 	    }
    }

    
   /**
     * Implementation for the Changelistener Event
     */
    public void stateChanged(ChangeEvent e)
    {
 	root.printDebugMessage("STATE CHANGED IN EVENT HANDLER CONTROLLWINDOW");
 	if (e.getSource() == root.getControlWindow().zoomslider)
 	    if (root.getOtabWindow().defaultimageispainted == false)
		{
		    int viewposition  = (int)root.getOtabWindow().scrollpane.getViewport().getViewPosition().getX();
		    root.printDebugMessage("VP"+viewposition);
		    double viewtime = convertPixelToTime(viewposition);
		    root.printDebugMessage("VT"+viewtime);
		    root.getOtabWindow().buildAnnotationBoardPanel();
		    int newviewposition = convertTimeToPixel(viewtime);
		    root.getOtabWindow().scrollpane.getViewport().setViewPosition(new Point(newviewposition,0));
		    root.printDebugMessage("NVT"+newviewposition);
		    root.config.zoom = root.ctrlwin.zoomslider.getValue();
		}
//     /**
//      * This Field is for the callbackFromTheVideoPlayerWhenMousePressed function.
//      * It is used for the multimple annotation points in the moved gvm.
//      */
//     de.dfki.nite.gram.video.GVMData gvmmoveddata = null;
//     /**
//      * This function is called back from the video Player mouse adapter and gives the 
//      * relative positions back from the mouse events. If -1 and -1 is give back means 
//      * that the right mouse button is pressed.
//      * @param double posX - The relative x position from the video window.
//      * @param double posY - The relative y position from the video window.
//      */
//     public void callbackFromTheVideoPlayerWhenMousePressed(double posX, double posY) {
// 	 if (videomouseeventishot == false) 
// 	     return;
// 	 videomouseeventishot = false;
// 	 root.printDebugMessage("CONTROLLEVENTHANDLER: VIDEO EVENT IS HOT: "+ posX + " " +posY);
// 	 // STATIC GVM
// 	 if (mainframe.getControlWindow().movedcheckbox.isSelected() == false) {
// 	     // First an instance of the gvm data class.
// 	     de.dfki.nite.gram.video.GVMData gvmdata = new de.dfki.nite.gram.video.GVMData();
// 	     // When the right mouse button is pressed make this function hot again and return.
// 	     if (posX == -1.0 && posY == -1.0) {
// 		 videomouseeventishot = true;
// 		 return; 
// 	     }
// 	     gvmdata.moved = false;
// 	     gvmdata.interpolated = false;
// 	     gvmdata.color   = mainframe.getControlWindow().gvmcolorcombobox.getSelectedIndex();
// 	     gvmdata.gvmtype = mainframe.getControlWindow().gvmshapecombobox.getSelectedIndex();
// 	     java.awt.geom.Point2D.Double point = new java.awt.geom.Point2D.Double(posX,posY);
// 	     gvmdata.points.add(point);
// 	     AnnotationLayer al = mainframe.layermanager.getLayerByID(mainframe.layermanager.getActiveLayer());
// 	     AnnotationElement ae = (AnnotationElement) al.annotationelements.get(al.activeannotationelement);
// 	     ae.setGvmData(gvmdata);
// 	     mainframe.getVideoWindow().player.updatePlayerView();
// 	     // Reapaint the time lineal and the active layer.
// 	     mainframe.layermanager.getLayerByID(0).repaint();
// 	     al.repaint();
// 	     mainframe.statusbar.updateStatusText("Static GVM added to the track element.");
// 	      
// 	     root.printDebugMessage("STATIC POINT ADDED: Color"+gvmdata.color+" Type:"+gvmdata.gvmtype);
// 	     // Store the Information in the NomWriteElement
// 	     ae.nomwriteelement.setGVM(gvmdata.createStringFromThisGVMDataObject());
// 	 }
// 	 // DYNAMIC GVM 
// 	 if (mainframe.getControlWindow().movedcheckbox.isSelected() == true) {
// 	     // First for the dynamic gvm this function must be maked hot again.
// 	     videomouseeventishot = true;
// 	     // If the left mouse button is pressed disable the hot flag and store the gvm to the annotation track
// 	     if (posX == -1.0 && posY == -1.0) {
// 		 videomouseeventishot = false;
// 		 if (gvmmoveddata == null) {
// 		     mainframe.statusbar.updateStatusText("No point selected. No GVM add.");
// 		     JOptionPane.showMessageDialog(mainframe,"You have no points selected with the\nleft mouse button. The GVM will not\nbe added to the track element.","No Points Selected",0);
// 		     return;
// 		 }
// 		 // Store the point in the track element.
// 		 AnnotationLayer al = mainframe.layermanager.getLayerByID(mainframe.layermanager.getActiveLayer());
// 		 if (al == null) {
// 		     mainframe.statusbar.updateStatusText("You deselected the annotation layer.");
// 		     JOptionPane.showMessageDialog(mainframe,"You have deselected the annotation layer !","ERROR",0);
// 		     return;
// 		 }
// 		 AnnotationElement ae = (AnnotationElement) al.annotationelements.get(al.activeannotationelement);
// 		 if (ae == null) {
// 		     mainframe.statusbar.updateStatusText("You deselected the annotation track element.");
// 		     JOptionPane.showMessageDialog(mainframe,"You have deselected the annotation track element !","ERROR",0);
// 		     return;
// 		 }
// 		 ae.setGvmData(gvmmoveddata);
// 		 mainframe.statusbar.updateStatusText("Dynamical annotation point added to the track elenemt.");
// 		 if (mainframe.DEBUG == true) {
// 		     root.printDebugMessage("DYNAMICAL ANNOTATION POINT ADDED TO THE TRACK ELEMENT:");
//  		     for (int i=0;i<gvmmoveddata.points.size();i+=2) {
// 			 java.awt.geom.Point2D.Double point = (java.awt.geom.Point2D.Double)gvmmoveddata.points.get(i);
// 			 javax.media.Time time = (javax.media.Time)gvmmoveddata.points.get(i+1);
// 			 root.printDebugMessage("POINT "+i+" AT POINT X: "+point.getX()+" POINT X: "+point.getY()+" AT TIME: "+time.getSeconds());
// 		     }
// 		 }
// 		 // Save it in the NOM
// 		 ae.nomwriteelement.setGVM(gvmmoveddata.createStringFromThisGVMDataObject());
// 		 // Reapaint the time lineal and the active layer.
// 		 al.repaint();
// 		 mainframe.layermanager.getLayerByID(0).repaint();
// 		 // And release the gmvdata point from the class
// 		 gvmmoveddata = null;
// 		 return;
// 	     }
// 	     // For the left mouse button.
// 	     // If no GVM is instanciated please create one.
// 	     if (gvmmoveddata == null) {
// 		 gvmmoveddata = new de.dfki.nite.gram.video.GVMData();
// 		 gvmmoveddata.moved = true;
// 		 gvmmoveddata.interpolated = mainframe.getControlWindow().interpolatedcheckbox.isSelected();
// 		 gvmmoveddata.color   = mainframe.getControlWindow().gvmcolorcombobox.getSelectedIndex();
// 		 gvmmoveddata.gvmtype = mainframe.getControlWindow().gvmshapecombobox.getSelectedIndex();
// 	     }
// 	     java.awt.geom.Point2D.Double point = new java.awt.geom.Point2D.Double(posX,posY);
// 	     javax.media.Time time = new javax.media.Time(mainframe.layermanager.timelinealcurrenttime);
// 	     gvmmoveddata.points.add(point);
// 	     gvmmoveddata.points.add(time);
// 	     mainframe.statusbar.updateStatusText("Timepoint annotated. Number: "+gvmmoveddata.points.size()/2);
// 	 }
    }
    /**
     * Helper function for unit conversion.
     * @param double time - The time.
     * @return int - The pixels.
     */
    public int convertTimeToPixel(double time) {
	return (int)(time/root.layermanager.getVideoDuration()*(double)(root.layermanager.getLayerByID(0).getWidth()));
    }
    /**
     * Helper function for unit conversion.
     * @param int pixel.
     * @return double time.
     */
    private double convertPixelToTime(int pixel){
	return (double)pixel/(double)root.layermanager.getLayerByID(0).getWidth()*root.layermanager.getVideoDuration();
    }
}
