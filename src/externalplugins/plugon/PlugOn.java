/******************************************************************
 *                 PlugOn.java  -  description
 *                    -----------------------
 * @author 		  Benjamin Lang, Jochen Frey
 * @version 		  0.1  
 * @see			  www.amiproject.de
 * copyright		: (C) 2005 @ dfki
 * begin 		: Thu Jan 20 15:01:00 CET 2004
 * last save   	        : Time-stamp: <05/08/09 12:08:11 clauer> 
 * compiler    	        : java version 1.4.2 
 * operating system     : Win 
 * editor		: xemacs 21.4
 * description          : The plugin for the Protege Ontology Plugin
 ******************************************************************/
package externalplugins.plugon;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Label;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import net.sourceforge.nite.meta.impl.NiteAttribute;
import net.sourceforge.nite.meta.impl.NiteObservation;
import net.sourceforge.nite.nom.NOMException;
import net.sourceforge.nite.nom.nomwrite.impl.NOMWriteAttribute;
import net.sourceforge.nite.nom.nomwrite.impl.NOMWriteElement;

public class PlugOn implements de.dfki.ami.amigram.plugin.AnnotationListener 

{
    /* RootFrame of Amigram */
    private de.dfki.ami.amigram.gui.DesktopFrame mainframe;

    /* Root_Instance of PlugOn*/
    private PlugOn root = this;
    
    /* NOMWriteElement that is selected, included in the AnnotationEvent */
    private  NOMWriteElement nomwriteelement;
    
    /* NOMWriteElement that is currently used in Protege for assigning */
    private NOMWriteElement actnwe;
    
    /* NOMWriteAttribute for the ontology URL */
    private NOMWriteAttribute nomwriteattribute;
       
    /* Message from Protege */
    private String message;
    
    /* Flag if Amigram is waiting for response from Protege */
    private Boolean waiting = false;
    
    /* AnnoMode Check Box */
     JCheckBox annoMode;

    /* TextArea that shows the textContent of the Selected NOMWriteElement*/
     JTextArea  textArea = new JTextArea(3,35);

    /* ID of the selected Element, shown in the PluginGUI */ 
    Label selectedElementID = new Label();

    /* ID of the selected Element, shown in the PluginGUI */ 
    Label status = new Label();
    
    /* Assigned Ontology-URL of the selected Element, shown in the PluginGUI */    
    Label selectedElementOntologyURL = new Label();
    
    /* Object of type Server (Listener) */
    private Server server;

    /* flag indicates of the pluginGUI is already started */
    private Boolean guistarted = false;

    /* the Plugin-GUI*/
    JFrame win; // = new JFrame("Plugin Settings");
    
    /* file chooser dialog */
    public JFileChooser chooser = new JFileChooser();

        
    /* */
    private java.util.List existLst = new ArrayList();
    private java.util.List notExistLst = new ArrayList();
    private java.util.List notAssignedLst = new ArrayList();
    /* */
    private Iterator it;
    
    /* Constructor of Class Plugon */   
    public PlugOn() { 
    }
    
    /* method which is called if an empty annotation element is selected by the user */
    public void emptyAnnoElementSelected(de.dfki.ami.amigram.plugin.AnnotationEvent ev) {
	System.out.println("SELECT EMPTY");
    }
    
    /* method which is called if any not emtpy annotation element is selected by the user */
    public void annoElementSelected(de.dfki.ami.amigram.plugin.AnnotationEvent ev) {
	System.out.println("SELECT");
	showGui();
	nomwriteelement = ev.annoElement;
	updatePluginGUI(nomwriteelement);
    }
    
    /* method which is called if an emtpy annotation element is double clicked  by the user */
    public void emptyAnnoElementDoubleClicked(de.dfki.ami.amigram.plugin.AnnotationEvent ev) {
	System.out.println("DOUBLECLICKED EMPTY");
        annoElementDoubleClicked(ev);
    }
    
    /* method which is called if any not emtpy annotation element is double clicked by the user */
    public void annoElementDoubleClicked(de.dfki.ami.amigram.plugin.AnnotationEvent ev) {
	System.out.println("DOUBLECLICKED");
	
	//String[] args = null;
	//TranscriptionView tv = new TranscriptionView(args);
	
	
	showGui();
	
	/* returns the current status of the thread for client connection */

	ServerThread svthread = server.getThread();
	
	try{
	    /* sends the ID of the selected element only if waiting == false */
	    if ( waiting == false && svthread != null)
		{
		    /*the selected element, only changed if amigram is not waiting for response*/
		    nomwriteelement = ev.annoElement;
		    String elementID = nomwriteelement.getID();
		    
		    /*updates the PluginGUI*/
		    updatePluginGUI(nomwriteelement);
		    svthread.sendTo(elementID + "+" + getOntCont(nomwriteelement));
		    actnwe = nomwriteelement;
		    
		    waitswitch();
		    updatePluginGUI (nomwriteelement);
		       
		}
                                    
	} catch (IOException e) {
	    System.err.println("Couldn't get I/O for the connection to client.");
	}
		 
		 
    }
    
    /* method which is called if the plugon is started by amigram */
    public void initialize(de.dfki.ami.amigram.gui.DesktopFrame amiroot) {
           
	System.out.println("INIT");
	
	mainframe = amiroot;
	
	/* Creates the listening Server */
	server = new Server(root);
    
	/* start the thread */
	server.start();
	
	/* shows the Plugin - GUI*/
	//showGui();
	
    }
    
    /* Function is called everytime the plugon is closed by amigram */
    public void terminate() {
	System.out.println("TERM");
	
	/* stop the listening server thread (deprectiated) */
	server.stopMe();
    }
    
    /* returns a string with the name of the plugin */
    public String getName(){return("PlugOn (the ontology plugin)");}

    
    /* stores the received URL in NXT */
    public void setData(String str){
	
	message = str;
	System.out.println("From Client:" + message);
	
	if (str.equals("abort"))
	    {
                if (waiting == true)
		    {
			//mainframe.toFront();
			//clearPluginGUI();
			waitswitch();
			updatePluginGUI(nomwriteelement);
			
			if(annoMode.isSelected())
			    {
				sendNext();
			    }
		    }
	    }
	

	
	else if (str.equals("exists"))
	    {
		//System.out.println("From Client:" + message);
		//System.out.println(nomwriteelement);
		
		existLst.add(nomwriteelement);
		consistCheck();
	    }
	else if (str.equals("notexists"))
	    {
		//System.out.println("From Client:" + message);
		notExistLst.add(nomwriteelement);
		consistCheck();
	    }   
        else
	    {
		//mainframe.toFront();  
		
	
		nomwriteattribute  = (NOMWriteAttribute) actnwe.getAttribute("ont-url");
		if (nomwriteattribute != null){
		    try {
			nomwriteattribute.setStringValue(message);
		    
		    } catch (NOMException ignore ){
			/*ignore*/
		    }
		}
		else {
		    NOMWriteAttribute newna = new NOMWriteAttribute("ont-url",message);

		    try
			{
			    actnwe.addAttribute(newna);
			}
		    catch (net.sourceforge.nite.nom.NOMException exc)
			{
			    mainframe.printDebugMessage("ERROR NOMEXCEPTION NOMCommunicator");
			}
		}
		
		/* Amigram  isn´t waiting for response anymore */
		if (waiting == true)
		    waitswitch();
		   
		updatePluginGUI(nomwriteelement);
        
		/* If AnnotationMode is selected, Amigram sends the next element to Protege */
		if(annoMode.isSelected())
		    {
			sendNext();
		    }
	    }
    }
    
  
    public void waitswitch () {
	if (waiting == false)
	    waiting = true;
	else
	    waiting = false;
    }
  
    public Boolean getStatus() {
	return waiting;
    }
    
    public void sendNext(){
	
	/* returns the current status of the thread for client connection */
	ServerThread svthread = server.getThread();
	
	mainframe.layermanager.activateElement((net.sourceforge.nite.nom.nomwrite.NOMElement) actnwe);
	mainframe.layermanager.MoveActiveAnnoElementRight(true);
	mainframe.statusbar.updateStatusText("Select Next Anno Element.");
	
	int activeLayerID = mainframe.layermanager.getActiveLayer();
	de.dfki.ami.amigram.tools.AnnotationLayer activeLayer = mainframe.layermanager.getLayerByID(activeLayerID);
	int activeElementID = activeLayer.activeannotationelement;
	de.dfki.ami.amigram.tools.AnnotationElement ae = (de.dfki.ami.amigram.tools.AnnotationElement) activeLayer.annotationelements.get(activeElementID);
	nomwriteelement = ae.nomwriteelement;
		
	try {
	         
	    if ( waiting == false & svthread != null )
		{
		    updatePluginGUI(nomwriteelement);
		    String annotationID = nomwriteelement.getID();
		    svthread.sendTo(annotationID + "+" + getOntCont(nomwriteelement));
		    actnwe = nomwriteelement;
		    waitswitch();
		    updatePluginGUI (nomwriteelement);
		}
	}  catch (IOException e) {
	    System.err.println("Couldn't get I/O for the connection to client.");
	}
    }
    
    public void sendPrev(){
	
	/* returns the current status of the thread for client connection */
	ServerThread svthread = server.getThread();
	
	mainframe.layermanager.activateElement((net.sourceforge.nite.nom.nomwrite.NOMElement) actnwe);
	mainframe.layermanager.MoveActiveAnnoElementLeft(true);
	mainframe.statusbar.updateStatusText("Select Previous Anno Element.");
	
	int activeLayerID = mainframe.layermanager.getActiveLayer();
	de.dfki.ami.amigram.tools.AnnotationLayer activeLayer = mainframe.layermanager.getLayerByID(activeLayerID);
	int activeElementID = activeLayer.activeannotationelement;
	de.dfki.ami.amigram.tools.AnnotationElement ae = (de.dfki.ami.amigram.tools.AnnotationElement) activeLayer.annotationelements.get(activeElementID);
	nomwriteelement = ae.nomwriteelement;
		
	try {
	         
	    if ( waiting == false & svthread != null)
		{
		    updatePluginGUI(nomwriteelement);
		    String annotationID = nomwriteelement.getID();
		    svthread.sendTo(annotationID + "+" + getOntCont(nomwriteelement));
		    actnwe = nomwriteelement;
		    waitswitch();
		    updatePluginGUI (nomwriteelement);
		}
	}  catch (IOException e) {
	    System.err.println("Couldn't get I/O for the connection to client.");
	}
    }


    
    /*shows a GUI with the plugin-settings*/
    public void showGui() {
           
	if (guistarted == false)
	    {
		win = new JFrame("PlugOn GUI");
		//win.setResizable(false);
		Box subwin = new Box(BoxLayout.Y_AXIS);
		JPanel line1 = new JPanel(new FlowLayout(0));
		JPanel line2 = new JPanel(new FlowLayout(0));
		JPanel line3 = new JPanel(new FlowLayout(0));
		JPanel line4 = new JPanel(new FlowLayout(0));
		JPanel line5 = new JPanel(new FlowLayout(0));
		JPanel line6 = new JPanel(new FlowLayout(0));
		

		win.addWindowListener(new java.awt.event.WindowAdapter(){
			public void windowClosing(WindowEvent we){
			    guistarted = false;
			    win.removeAll();
			}});
		/*this button starts Protege*/
		JButton startProtege = new JButton("Start Protege");
		startProtege.addActionListener(new ActionListener() {
            
             
			// called when the button is pressed
			public void actionPerformed(ActionEvent event) {
			    System.out.println("start protege");
			    try {

				int returnVal = chooser.showOpenDialog(mainframe);
				if(returnVal == JFileChooser.APPROVE_OPTION) {
                                   
				    String url = chooser.getSelectedFile().toURL().toString();
				    String name = chooser.getSelectedFile().getName();
				    Runtime.getRuntime().exec("cmd.exe /c start c:/programme/protege_3.1/protege.exe " + url);
				}

			    }
			    catch (Exception ignore){}
			}});
	
		/*this button performs an Annotation Check with Protege*/
		JButton checkConsistence = new JButton("Check");
		checkConsistence.addActionListener(new ActionListener() {
            
			// called when the button is pressed
			public void actionPerformed(ActionEvent event) {
			    System.out.println("consistence check");
			    int activeLayerID = mainframe.layermanager.getActiveLayer();
			    de.dfki.ami.amigram.tools.AnnotationLayer activeLayer = mainframe.layermanager.getLayerByID(activeLayerID);
			    Vector elementsToCheck = new Vector();
			    elementsToCheck = activeLayer.annotationelements;
			    //NiteLayer nitelayer = activeLayer.nitelayer;
			    //java.util.List elementsToCheck = nitelayer.getContentElements();
			    
			    it = elementsToCheck.iterator();
			    consistCheck();
           	
	
			}});

		/*this button plays the audio of the selected element*/
		JButton playAudio = new JButton("Play");
		playAudio.addActionListener(new ActionListener() {
            
			// called when the button is pressed
			public void actionPerformed(ActionEvent event) {
			    System.out.println("play audio");
			    javax.media.Time starttime = new javax.media.Time(nomwriteelement.getStartTime());
			    javax.media.Time endtime = new javax.media.Time (nomwriteelement.getEndTime());
			    mainframe.synchronizer.playTimeSpan(starttime,endtime);
	
			}});

		/* if the user hit this button the next annoelement will be send instantly to protege*/
		JButton sendNext = new JButton("SendNext");
		sendNext.addActionListener(new ActionListener() {
            
			// called when the button is pressed
			public void actionPerformed(ActionEvent event) {
			    if( waiting == false ){
				System.out.println("send next element");
				sendNext();
			    }
			    else
				System.out.println("still waiting for protege ...");
			}});

			/* if the user hit this button the previous annoelement will be send instantly to protege*/
		JButton sendPrev = new JButton("SendPrev");
		sendPrev.addActionListener(new ActionListener() {
            
			// called when the button is pressed
			public void actionPerformed(ActionEvent event) {
			    if( waiting == false ){
				System.out.println("send previous element");
				sendPrev();
			    }
			    else
				System.out.println("still waiting for protege ...");
			}});

		
		
		JButton showTranscription = new JButton("ShowTranscription");
		showTranscription.addActionListener(new ActionListener() {
            
			// called when the button is pressed
			public void actionPerformed(ActionEvent event) {
			    
				System.out.println("show transcription");
				String[] arglist = new String[10];
				arglist[0]="java";
				arglist[1]="ViewPanel";
				arglist[2]="-corpus";
				//arglist[3]="C:/Dokumente und Einstellungen/Jochen/Eigene Dateien/Uni/hiwi/src-codes/Amigram/AmigramData/AMI-metadata.xml";
				arglist[3]=mainframe.getNOMCommunicator().getCorpusFilePath();
				arglist[4]="-observation";
				//arglist[5]="IS1006a";
				for (Iterator iter = mainframe.getNOMCommunicator().getLoadedObservations().iterator(); iter.hasNext();) {
				    arglist[5] = ((NiteObservation)iter.next()).getShortName();
				}
				arglist[6]="-config";
				arglist[7]="configuration/amiConfig.xml";
				arglist[8]="-annotator";
				/*TODO: different annotators possible*/
				arglist[9]="Sandra";
				TranscriptionView tv = new TranscriptionView(arglist);
			   
			    
			}});
		
		
        
		/*switch to annotion mode that automatically sends the next element to Protege*/
		annoMode = new JCheckBox("Auto Mode", false);
	
		/*the textArea that shows the textContent of the Selected NOMWriteElement, defined above*/
		textArea.setWrapStyleWord(true);
		textArea.setLineWrap(true);
		textArea.setEditable(false);
		
		if ( getStatus() == true )
		    status.setText("waiting");
		else
		    status.setText("ready");
		
		
		line1.add( new Label("Name: " + getName()));
		line1.add( new Label("Status: "));
		line1.add( status );
		line2.add( new Label("Selected Element ID: "));
		line2.add(selectedElementID);
		line3.add( new Label("Ontology-URL: "));
		line3.add(selectedElementOntologyURL);
		line4.add( new Label("Content: "));
		line4.add(textArea);
		line5.add(startProtege);
		line5.add(checkConsistence);
		line5.add(playAudio);
		line5.add(sendPrev);
		line5.add(sendNext);
		line5.add(annoMode);
		/* TEST Transcription View */

		
		line6.add(showTranscription);

		/* TEST Transcription View */
		subwin.add(line1);
		subwin.add(line2);
		subwin.add(line3);
		subwin.add(line4);
		subwin.add(line5);
		subwin.add(line6);
		win.add(subwin);
		
		win.pack();

		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		win.setLocation( (d.width  - win.getSize().width ) / 2,
				 (d.height - win.getSize().height) / 5 );
                     
		win.setVisible(true);
		win.setAlwaysOnTop(true);
	        
		guistarted = true;
	    }
    }
    
	
    /* updates the PluginGUI */
    public void updatePluginGUI (NOMWriteElement nomwriteelement){
	if (waiting == false){
	    status.setText("ready");
	    /* updates the selectedElementID */
	    String elementID = nomwriteelement.getID();
	    selectedElementID.setText(elementID);
	
	    /* updates the shown textContent of the selected element*/
	    java.util.List children = nomwriteelement.getChildren();
	    textArea.setText("");
	    for (int i=0; i<children.size(); i++) 
		{
		    NOMWriteElement child = (NOMWriteElement) children.get(i);
		    String textContent = child.getText();
		    if (child.getText() == null)
			{textArea.append("[disfmarker] ");}
		    else textArea.append(textContent + " ");
		}

	    /* updates the selectedElementOntologyURL */
	    selectedElementOntologyURL.setText(getOntCont(nomwriteelement));
	
	    win.pack();
	}
	else{
	    status.setText("waiting");
	}
    
    }

    /* clears the GUI */
    public void clearPluginGUI () {
	selectedElementID.setText("");
	selectedElementOntologyURL.setText("");
	textArea.setText("");

	win.pack();
    }


    
    /**
     * asks whether the plugin should be invoked
     */
    public boolean invokePlugin(java.util.List attributeList){
	boolean flag = false;
	System.out.println("invokePlugin");
	for(int i = 0; i < attributeList.size(); i++)
	    {
		NiteAttribute att = (NiteAttribute) attributeList.get(i);
			    
		if (att.getName().equals("ont-url"))
		    {
			flag = true;
			break;
		    }
		else flag = false;
	    }
	return flag;
    }
    
    public String getOntCont(NOMWriteElement nwe){
	nomwriteattribute = (NOMWriteAttribute) nwe.getAttribute("ont-url");
           
	String content = "";
	if (nomwriteattribute != null)
	    content = nomwriteattribute.getStringValue();
	else content = "NOT ASSIGNED";
	return content;
    }

    public void consistCheck(){
	ServerThread svthread = server.getThread();
	if (svthread != null)
	    {
		if (it.hasNext()) {
		    //de.dfki.ami.amigram.tools.AnnotationElement ae = (de.dfki.ami.amigram.tools.AnnotationElement)it.next();
		    //if (ae != null ) {
		    de.dfki.ami.amigram.tools.AnnotationElement ae = (de.dfki.ami.amigram.tools.AnnotationElement) (it.next());
		    //nomwriteelement = ae.nomwriteelement;
		    nomwriteelement = ae.nomwriteelement;
		    //NOMWriteElement nomwriteelement = it.next().nomwritelement;
		    try
			{
			    String annotationID = nomwriteelement.getID();
			    if (getOntCont(nomwriteelement).equals("NOT ASSIGNED"))
				{
				    notAssignedLst.add(nomwriteelement);
				    consistCheck();
				}
					
			    else svthread.sendTo(annotationID + "+" + getOntCont(nomwriteelement) + "#" + "CheckItOut");
			    /*try
			      {
			      Thread.sleep(50);
			      }
			      catch (InterruptedException e){}*/
		            
		            
			} catch (IOException e) {
			System.err.println("Couldn't get I/O for the connection to client.");
		    }
                
		    // }
                
		}
	        
		else if (existLst != null && notExistLst != null && notAssignedLst != null)
		    {
			mainframe.layermanager.highlightElements(existLst, java.awt.Color.GREEN);
			mainframe.layermanager.highlightElements(notExistLst, java.awt.Color.RED);
			mainframe.layermanager.highlightElements(notAssignedLst, java.awt.Color.YELLOW);
			mainframe.getOtabWindow().layercollectionpanel.repaint(); 
			existLst.clear();
			notExistLst.clear();
			notAssignedLst.clear();
		    }
		  
	    }
        
    }
    
}
