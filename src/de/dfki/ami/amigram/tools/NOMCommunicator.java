/******************************************************************
 *          NOMCommunicator.java  -  description
 *                    -----------------------
 * @author 		  Christoph Lauer
 * @version 		  0.1  
 * @see			  www.amiproject.de
 * copyright		: (C) 2003 @ christoph lauer
 * begin 		: Fri may 21 17:00:00 CET 2003
 * last save   	        : Time-stamp: <05/08/03 15:27:42 clauer> 
 * compiler    	        : java version 1.4.2 
 * operating system     : Linux 
 * editor		: xemacs 21.4
 * description          : Handles the communication with the NOM.
 ******************************************************************/

package de.dfki.ami.amigram.tools;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.WeakHashMap;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import net.sourceforge.nite.meta.NObservation;
import net.sourceforge.nite.meta.impl.NiteAgent;
import net.sourceforge.nite.meta.impl.NiteLayer;
import net.sourceforge.nite.meta.impl.NiteMetaData;
import net.sourceforge.nite.meta.impl.NiteMetaException;
import net.sourceforge.nite.meta.impl.NiteObservation;
import net.sourceforge.nite.meta.impl.NiteSignal;
import net.sourceforge.nite.nom.nomwrite.impl.NOMWriteCorpus;
import net.sourceforge.nite.nom.nomwrite.impl.NOMWriteElement;
import de.dfki.ami.amigram.gui.DesktopFrame;
import de.dfki.ami.amigram.multimedia.MediaPlayer;

public class NOMCommunicator 
{
    private DesktopFrame   root;
    private String         corpusfilepath = new String(".."+File.separator+"AmigramData"+File.separator+"meta"+File.separator+"dagmar-metadata.xml");
    private JFileChooser   metadatafilechooser = new JFileChooser(".."+File.separator+"AmigramData"+File.separator+"meta");
    public  NiteMetaData   metadata;
    private String         mediafilepath;
    public  NOMWriteCorpus nomwritecorpus;
    public  boolean        selectmediafile = false;        // If this flag is true the file will be new selected
    public  int            sumoffoundedannotationelements; // The sum of founded annotation elements while read out the corpus
    public  JCheckBox[]    checkboxes;
    public  String[]       signalpath;
    public  java.util.List signallist;

    private JLabel         sigWarnLabel;                   // warn while opening a medi file if signal analyse is enabled
    private JPanel         sigWarnPanel;                   // warn while opening a medi file if signal analyse is enabled
    private JButton        sigWarnButton;                  // warn while opening a medi file if signal analyse is enabled
    private boolean        disableSigAnal = false;   
    public  java.util.List observations;                   // observations for the selected corpus
    public  java.util.List observationsLoaded=new ArrayList();// observations loaded into the NOM
    private boolean[]      observationsSelected;           // for the checkbos
    private JCheckBox[]    observationCheckBoxes;
    public  java.util.List agents;                         // agents for the selected corpus
    public  java.util.List codings;
    public  boolean[]      fileExists;
    public  WeakHashMap    annohashmap = new WeakHashMap(); // references for the nom elements to anno elements
    public  int existingSignals = 0;
    public  int layercount = 0;
    public  String openTime = "not opened";
    public  String saveTime = " not saved !";
    public  net.sourceforge.nite.search.GUI gui;
    public  QueryResultHandlerImpl queryresulthandler;

    /** The default constructor for the nom-communicator.*/
    public NOMCommunicator(DesktopFrame r)
    {
	root = r;
    }
    /**
     * Opens a file chooser in the folder where the corpora are stored.
     * The selected filepath is stored in the private corpusfilepath field.
     */
    public void chooseMetaDataFile() {
	int returnVal;
	do {
	    returnVal = metadatafilechooser.showOpenDialog(root);
	    if (returnVal == JFileChooser.CANCEL_OPTION) {
		corpusfilepath = null;
		return;		
	    }
	} while 
	      (returnVal != JFileChooser.APPROVE_OPTION);
	root.printDebugMessage("SELECTED FILE" + metadatafilechooser.getSelectedFile());
	corpusfilepath = metadatafilechooser.getSelectedFile().getPath();
    }
    /**
     * This function tries to access the corpus and instantiate the
     * NiteMetaData class from the corpus file path.
     * @return boolean - true if the corpus could be instantiated. Otherwise false.
     */
    public boolean access() {
	openTime = getDateString();
	saveTime = " not saved !";
        try {
	    root.printDebugMessage("TRY TO INSTANTIATE THE CORPUS");
	    metadata = new NiteMetaData(corpusfilepath);
        } catch (NiteMetaException nme) 
	    {
		root.printDebugMessage("NiteMetaException WHILE INSTANTIATE THE CORPUS");
		return false;
	    }
	// look for codings
	codings = metadata.getCodings();
	// look for agents
	agents = metadata.getAgents();
	// look for the observations and instanciate the corresponding elements for the check box
	observations = metadata.getObservations();
	// crate and set the comboboxes
	JComboBox ob = new JComboBox();
	for (Iterator iter = observations.iterator(); iter.hasNext();) {
	    ob.addItem(((NiteObservation)iter.next()).getShortName());
	}
	JComboBox ag = new JComboBox();
	for (Iterator iter = agents.iterator(); iter.hasNext();) {
	    ag.addItem(((NiteAgent)iter.next()).getShortName());
	}
	root.ctrlwin.setNxtBoxes(ob,ag);
	
	root.printDebugMessage("Found " + observations.size() + " Observations.");
	observationCheckBoxes = new JCheckBox[observations.size()];
	int j=0;
	for (Iterator iter = observations.iterator(); iter.hasNext();j++) {
	    
	    //	for (Iterator<NiteObservation> iter=(Iterator<NiteObservation>)observations.iterator();iter.hasNext();j++)
	    //{
		String name = ((NiteObservation)iter.next()).getShortName();
		observationCheckBoxes[j] = new JCheckBox(name);
		root.printDebugMessage(j+". Observation: "+name );
	    }

	// build the dialog box
	if (observations.size() > 1)
	    {
		observationsSelected = new boolean[observations.size()];
		final JDialog dialog = new JDialog(root,"Select Observations",true);
		dialog.getContentPane().setLayout(new BorderLayout());
		JPanel panel = new JPanel();
		panel.setBorder(new CompoundBorder(new smooth.basic.SmoothTitledBorder(null,observations.size()+" Observations available Corpus",TitledBorder.LEFT, TitledBorder.TOP),new EmptyBorder(5,5,5,5))); 
		panel.setLayout(new GridLayout(observations.size(),1));
		for (int i=0;i<observationCheckBoxes.length;i++)
		    panel.add(observationCheckBoxes[i]);
		JScrollPane sp = new JScrollPane(panel);
		sp.setPreferredSize(new Dimension(400,400));
		dialog.getContentPane().add(sp,BorderLayout.NORTH);
		JPanel buttonPanel = new JPanel();
		JButton openButton = new JButton("Open Selected Observations");
		JButton allButton = new JButton("Open All Observations");
		openButton.setPreferredSize(new Dimension((int)openButton.getPreferredSize().getWidth()+5,(int)openButton.getPreferredSize().getHeight()));
		allButton.setPreferredSize(new Dimension((int)allButton.getPreferredSize().getWidth()+5,(int)allButton.getPreferredSize().getHeight()));
		buttonPanel.add(allButton);
		buttonPanel.add(openButton);
		dialog.getContentPane().add(buttonPanel,BorderLayout.SOUTH);
		dialog.pack();
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension window = dialog.getSize();
		dialog.setLocation((screen.width - window.width) / 2, (screen.height - window.height) / 2);
		openButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){dialog.setVisible(false);}});
		allButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
			    dialog.setVisible(false);
			    for (int i=0;i<observationsSelected.length;i++)
				    observationsSelected[i] = true;
			    dialog.setVisible(false);
			}});
		dialog.setVisible(true);
		for (int i=0;i<observationCheckBoxes.length;i++)
		    if (observationCheckBoxes[i].isSelected()==true) 
			observationsSelected[i] = true;
	    }
	return true;
    }
    /**
     * Checks if the Corpus is an Standoff Corpus.
     * @return boolean - true is the corpus is a standoff corpus. If it is a simple or a standalone corpus false will be given back.
     */
    public boolean checkCorpusType() {
	if (metadata.getCorpusType()==NiteMetaData.STANDOFF_CORPUS) 
	    return true;
	else 
	    return false;
    }
    /**
     * Tries to instantiate the NomWriteCorpus.
     * If the corpus is loaded the corpus will be serialized.
     * @return boolean - true if no error occoured, otherwise false.
     */
    public boolean loadForWriting() {
	try {
	    observationsLoaded.clear(); // remove possible old obersations from previous calls

	    java.io.PrintStream ps = new java.io.PrintStream(new java.io.FileOutputStream(new File("nx.log")));
	    nomwritecorpus = new NOMWriteCorpus(metadata, ps);
	    nomwritecorpus.setLazyLoading(false);
	    nomwritecorpus.setErrorStream(ps);
	    // either load the complete corpus or the selected corpuses
	    if (observations.size() < 2) {
		nomwritecorpus.loadData();
		observationsLoaded.add(observations.get(0));
	    } else 
		{
		    int j=0;
		    for (Iterator iter = observations.iterator(); iter.hasNext();j++) 
			//for (Iterator<NiteObservation> iter=(Iterator<NiteObservation>)observations.iterator();iter.hasNext();j++)
			{
			    if (observationsSelected[j] == false)
				{
				    iter.next();
				    continue;
				}
			    NiteObservation no = (NiteObservation)iter.next();
			    nomwritecorpus.loadData(no);
			    observationsLoaded.add(no);
			}
		}
	    // NOM is loaded!! 
	    // Available to edit / search now.
	    // Now serialize any files that have changed.
	    // won't do anything unless edits have been made
	    nomwritecorpus.serializeCorpusChanged();
	} catch (Exception e) 
	    {
		root.printDebugMessage("NOM EXCEPTION WHILE TRY TO OPEN THE CORPUS FOR WRITING");
		e.printStackTrace();
		return false;
	    }
	return true;
    }

    // Accessor functions for the info tab-pane
    /**
     * Gets back the file path of the selected corpus file.
     * @return String - the file path. If nothing is choosen, null will be returned.
     */
    public String getCorpusFilePath() {
	return corpusfilepath;
    }
    /** 
     * Gets back the file name of the selected corpus file.
     * @return String - the file name. If no file is selected, null will be returned.
     */
    public String getCorpusFileName() {
	if (corpusfilepath == null) 
	    return null;
	String corpusfilename;
	int pos = corpusfilepath.lastIndexOf(File.separator);
	corpusfilename = corpusfilepath.substring(pos+1);
	return corpusfilename;
    }

    /**
     * Builds the Annotation Board from the NOMWriteCorpus.
     * @return boolean - true if the annotation board could be build successful, otherwise false.
     */
    public boolean buildAnnotationTree() {
	// First remove all layers from the list
	root.layermanager.removeAllLayers();
	sumoffoundedannotationelements = 0;
	// Then add the time lineal layer
	root.layermanager.addTimeLinealLayer();
	java.util.List layerlist = metadata.getLayersByType(NiteLayer.TIMED_LAYER);
	for (int i=0;i<layerlist.size();i++) {
	    layercount++;
	    NiteLayer timelayer = (NiteLayer)layerlist.get(i);
	    // Add an new time aligned layer to the Annotation Manager
	    int timelayerID = root.layermanager.addTimeAlignedLayer(timelayer.getName());
	    // Add the NiteLayer to the timealigned Layer
	    root.layermanager.getLayerByID(timelayerID).nitelayer = timelayer;
	    // Trace the List of Elements for this Layer
	    java.util.List contentelementlist = timelayer.getContentElements();
	    root.printDebugMessage("Elements for this TimeLayer: "+contentelementlist.size());
	    // Search for underlaying structural layers
	    buildStructuralLayersRecursively(timelayer);
	}
	// Layer trace to the console
	if (root.DEBUG == true) root.layermanager.traceLayerList();
	return true;
    }

    /**
     * This Class searches recursively for inner layers and add them to the 
     * GRAM layer management. It only can add structural layers. 
     * The layer searches recursively for other structural layers.
     * The Metadata uses inversely hierarchy structure. Parents are childs vise versa.
     * @param NiteLayer - The layer which is scanned for parents.
     */
    public void buildStructuralLayersRecursively(NiteLayer layer) {
	int parentID = root.layermanager.findLayerByName(layer.getName());
	java.util.List parentlayerlist = layer.getParentLayers();
	if (parentlayerlist != null) {
	    layercount ++;
	    for (int j=0;j<parentlayerlist.size();j++) { 
		// get an instance of the layer
		NiteLayer parentlayer = (NiteLayer)parentlayerlist.get(j);
		// interrupt recursive calls
		if (parentlayer.getName().compareTo(layer.getName()) == 0) 
		    continue;
		// add the layer to the list
		int childID = root.layermanager.addStructuralLayer(parentlayer.getName(),parentID);
		root.layermanager.getLayerByID(parentID).addChild(childID);
		if (parentlayer.getRecursive() == true)
		    root.layermanager.getLayerByID(childID).setRecursive(true);
		root.layermanager.getLayerByID(childID).setHierarchyDeepth(root.layermanager.getHierarchyDeepthForID(childID));
		// Add the NiteLayer to the Structural Layer
		root.layermanager.getLayerByID(childID).nitelayer = parentlayer;
		// Trace the List of Elements for this Layer
		if (root.DEBUG == true) {
		    java.util.List contentelementlist = parentlayer.getContentElements();
		    root.printDebugMessage("Elements for this StructuralLayer: "+contentelementlist.size());
		}
		// recursive call
		buildStructuralLayersRecursively(parentlayer);
	    }
	}
    }

    /** 
     * @param String - Opens the video and audio files referenced in the metadata.
     */
    public void openMediaSignals() {
	root.printDebugMessage("NOMCommunicator:OPEN MEDIA SIGNALS:START");
	// First get the signals from the corpus
	if (metadata == null)
	    return;
	if (metadata.getSignals() == null)
	    return;
	signallist = metadata.getSignals();
	if (signallist.size() == 0)
	    return;
	// Get the path to signals from the metadata
	String path = metadata.getSignalPath()+File.separator;
	String   signalformat;
	String   signalname;
	String   signalextension;
	String[] signaltype = new String[signallist.size() * observationsLoaded.size()];
	signalpath = new String[signallist.size() * observationsLoaded.size()];
	String[] signalstringfordialog = new String[signallist.size() * observationsLoaded.size()]; // For the selection dialog
	fileExists = new boolean[signallist.size() * observationsLoaded.size()]; 
	checkboxes = new JCheckBox[signallist.size() * observationsLoaded.size()];  // For the selection dialog
	existingSignals = 0;
	// Check if there are any signals
	if (signallist.size() == 0) {
	    JOptionPane.showMessageDialog(root,"No multimedia signals in the corpus","Message",1);
 	    return;
	}

	int signals=0;

	// iterate over the loaded observations
	for (Iterator oit=observationsLoaded.iterator(); oit.hasNext(); ) {
	    String obs = ((NObservation)oit.next()).getShortName(); 

	    // Check for Signals.
	    for (int i=0;i<signallist.size();i++) {
		NiteSignal signal = (NiteSignal) signallist.get(i);
		signalname = signal.getName();
		signalextension = signal.getExtension();
		signalpath[signals] = path + obs + "." + signalname + "." + signalextension;
		signalformat = signal.getFormat();
		// Check the signal typees
		if (signal.getMediaType() == net.sourceforge.nite.meta.impl.NiteSignal.VIDEO_SIGNAL)
		    signaltype[signals] = "VIDEO"; 
		if (signal.getMediaType() == net.sourceforge.nite.meta.impl.NiteSignal.AUDIO_SIGNAL)
		    signaltype[signals] = "AUDIO"; 
		File testFile = new File(signalpath[signals]);
		if (testFile.exists() == true) {
		    signalstringfordialog[signals] = "(Y) ";
		    fileExists[signals] = true;
		    existingSignals++;
		}
		else {
		    signalstringfordialog[signals] = "(N) ";
		    fileExists[signals] = false;
		}
		signalstringfordialog[signals] += signaltype[signals]+":  "+signalformat+":  ["+obs+"]."+signalname+"."+signalextension;
		root.printDebugMessage("SIGNAL FOUND: "+signals+" "+signalstringfordialog[signals]);
		root.printDebugMessage(signalpath[signals]);
		// set the text for the checkboxes
		checkboxes[signals] = new JCheckBox(signalstringfordialog[signals]);
		signals++;
	    }
	}

	// generate the check box dialog
	final JDialog dialog = new JDialog(root,"Select Signals",true);
	dialog.getContentPane().setLayout(new BorderLayout());
	JPanel checkBoxPanel = new JPanel();
	checkBoxPanel.setBorder(new CompoundBorder(new smooth.basic.SmoothTitledBorder(null,"Found " + signallist.size()  + " Signals in Corpus",TitledBorder.LEFT, TitledBorder.TOP),new EmptyBorder(5,5,5,5))); 
	checkBoxPanel.setLayout(new GridLayout(signallist.size(),1));
	for (int i=0;i<signallist.size() * observationsLoaded.size();i++)
	    checkBoxPanel.add(checkboxes[i]);
	dialog.getContentPane().add(checkBoxPanel,BorderLayout.NORTH);
	JPanel buttonPanel = new JPanel();
	JButton okButton = new JButton("Open Selected");
	okButton.setPreferredSize(new Dimension((int)okButton.getPreferredSize().getWidth()+5,(int)okButton.getPreferredSize().getHeight()));
	JButton allButton = new JButton("Open All Existing");
	allButton.setPreferredSize(new Dimension((int)okButton.getPreferredSize().getWidth()+5,(int)okButton.getPreferredSize().getHeight()));
	JButton otherButton = new JButton("Open Other");
	otherButton.setPreferredSize(new Dimension((int)okButton.getPreferredSize().getWidth()+5,(int)okButton.getPreferredSize().getHeight()));
	JButton cancelButton = new JButton("Open Nothing");
	cancelButton.setPreferredSize(new Dimension((int)okButton.getPreferredSize().getWidth()+5,(int)okButton.getPreferredSize().getHeight()));
	buttonPanel.add(allButton);
	buttonPanel.add(okButton);
	buttonPanel.add(otherButton);
	buttonPanel.add(cancelButton);
	sigWarnLabel = new JLabel("Automatical signal generation disabled.");
	sigWarnLabel.setForeground(new Color(0,140,0));
	if (root.config.autoGenerateOszilloskop== true && root.config.autoGenerateSpectrum==true)
	    {
		sigWarnLabel = new JLabel("Attention: Automatical Oszilloskop and Spectogram Layer Generation enabled.");
		sigWarnLabel.setForeground(new Color(140,0,0));
	    }
	else 
	    {
		if (root.config.autoGenerateOszilloskop== true)
		    {
			sigWarnLabel = new JLabel("Attention: Automatical Oszilloskop Layer Generation enabled.");
			sigWarnLabel.setForeground(new Color(140,0,0));
		    }
		if (root.config.autoGenerateSpectrum== true)

		    {
			sigWarnLabel = new JLabel("Attention: Automatical Spectrum Layer Generation enabled.");
			sigWarnLabel.setForeground(new Color(140,0,0));
	   
		    }
	    }
	sigWarnPanel = new JPanel();
	sigWarnPanel.setLayout(new BorderLayout());
	sigWarnButton = new JButton("Disable Signal Analyse");
	sigWarnButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) 
		{
		    sigWarnPanel.removeAll();
		    sigWarnLabel = new JLabel("Automatical signal generation disabled for current opened Media Files.");
		    sigWarnLabel.setForeground(new Color(0,140,0));
		    sigWarnPanel.add(sigWarnLabel,BorderLayout.WEST);
		    dialog.pack();
		    disableSigAnal=true;
		}
	    }
				       );
	sigWarnPanel.setBorder(new CompoundBorder(new smooth.basic.SmoothTitledBorder(null,"Automatical Signal Generation",TitledBorder.LEFT, TitledBorder.TOP),new EmptyBorder(5,5,5,5))); 
	sigWarnPanel.add(sigWarnLabel,BorderLayout.WEST);
	if (root.config.autoGenerateOszilloskop== true || root.config.autoGenerateSpectrum==true)
	    sigWarnPanel.add(sigWarnButton,BorderLayout.EAST);
	dialog.getContentPane().add(sigWarnPanel,BorderLayout.CENTER);
	dialog.getContentPane().add(buttonPanel,BorderLayout.SOUTH);
	// action for the ok button
       	okButton.addActionListener(new ActionListener(){
		public void actionPerformed(ActionEvent e) 
		{
		    root.printDebugMessage("OK Button Pressed.");
		    dialog.setVisible(false);
		    for (int i=0;i<signallist.size();i++) {
			if(fileExists[i] == false)
			    continue;
			if (checkboxes[i].isSelected()==true) 
			    {
				mediafilepath = signalpath[i];
				selectmediafile = false;
				File mediaFile = new File(mediafilepath);
				if (mediaFile.isFile() && mediaFile.canRead()) {
				    MediaPlayer player = new MediaPlayer(root, mediaFile);
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
					    if (root.config.autoGenerateOszilloskop==true && disableSigAnal==false)
						try
						    {
							disableSigAnal = false;
							String url = (new File(mediafilepath)).toURL().toString();
							String name = (new File(mediafilepath)).getName();
							root.getOtabWindow().toolbar.addOszillogramLayerForUrl(url,name);
						    }
						catch (Exception exc) {exc.printStackTrace();}
					    if (root.config.autoGenerateSpectrum==true && disableSigAnal==false)
						try
						    {
							disableSigAnal = false;
							String url = (new File(mediafilepath).toURL()).toString();
							String name = (new File(mediafilepath)).getName();
							root.getOtabWindow().toolbar.addSpectralLayerForUrl(url,name);
						    }
						catch (Exception exc) {exc.printStackTrace();}
					}
				    else
					{
					    player.dispose();
					    JOptionPane.showMessageDialog(root,"Error while create the meda player for this file.\nPerhaps the format is not a supported.\n File: "+ mediafilepath,"Error",0);
					}
				} else {
				    JOptionPane.showMessageDialog(root,"Media file '" + mediafilepath + "' does not exist or is not readable." ,"Error",0);
				}
			    }			 
		    }
		}
	    }
				   );
        // action for the open all button
	allButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) 
		{
		    root.printDebugMessage("ALL Button Pressed.");
		    dialog.setVisible(false);
		    for (int i=0;i<signallist.size();i++) {
			if(fileExists[i] == false)
			    continue;
			mediafilepath = signalpath[i];
			selectmediafile = false;
			MediaPlayer player = new MediaPlayer(root,new File(mediafilepath));
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
				if (root.config.autoGenerateOszilloskop==true && disableSigAnal==false)
				    try
					{
					    disableSigAnal = false;
					    String url = (new File(mediafilepath)).toURL().toString();
					    String name = (new File(mediafilepath)).getName();
					    root.getOtabWindow().toolbar.addOszillogramLayerForUrl(url,name);
					}
				    catch (Exception exc) {exc.printStackTrace();}
				if (root.config.autoGenerateSpectrum==true && disableSigAnal==false)
				    try
					{
					    disableSigAnal = false;
					    String url = (new File(mediafilepath)).toURL().toString();
					    String name = (new File(mediafilepath)).getName();
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
		    selectmediafile = false;
		}
	    }
				       );
	// action for the cancel button
	cancelButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) 
		{
		    root.printDebugMessage("CANCEL Button Pressed.");
		    dialog.setVisible(false);
		    selectmediafile = false;
		}
	    }
				       );
	// action for the other button
	otherButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) 
		{
		    root.printDebugMessage("OTHER Button Pressed.");
		    dialog.setVisible(false);

		    root.getControlWindow().eventhandler.chooser.setDialogTitle("Select Video or Audiofile for Signal Analyze");
		    int returnVal = root.getControlWindow().eventhandler.chooser.showOpenDialog(root);
		    if(returnVal == JFileChooser.APPROVE_OPTION) 
			{
			    File file = root.getControlWindow().eventhandler.chooser.getSelectedFile();
			    MediaPlayer player = new MediaPlayer(root,file);
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
				    if (root.config.autoGenerateOszilloskop==true  && disableSigAnal==false)
					try
					    {
						disableSigAnal = false;
						String url = file.toURL().toString();
						String name = file.getName();
						root.getOtabWindow().toolbar.addOszillogramLayerForUrl(url,name);
					    }
					catch (Exception exc) {exc.printStackTrace();}
				    if (root.config.autoGenerateSpectrum==true && disableSigAnal==false)
					try
					    {
						disableSigAnal = false;
						String url = file.toURL().toString();
						String name = file.getName();
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
		    selectmediafile = false;
		}
	    });
				      
	// place and show the dialog
	dialog.pack();
	Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
	Dimension window = dialog.getSize();
	dialog.setLocation((screen.width - window.width) / 2, (screen.height - window.height) / 2);
	dialog.setVisible(true);
	root.printDebugMessage("NOMCommunicator:OPEN MEDIA SIGNALS:END");
    }
    /**
     * Stores the annotation into the NOM and notifies all other viewers of 
     * this nom Corpus that the corpus has changed.
     * @return boolean - true if the corpus was saves sucessfull, otherwise false.
     */
    public boolean saveAnnotationToNom() {
	try {
	    nomwritecorpus.serializeCorpus();
	}
	catch (Throwable t) {
	    root.printDebugMessage("ERROR WHILE SAVE THE CORPUS");
	}
	// Tell all other NOMViewers that the corpus has chaged now.
	if (nomwritecorpus != null) {
	    nomwritecorpus.notifyChange();
	}
	else {
	    root.printDebugMessage("No Corpus avaliable for writing !!!");
	    return false;
	}
	saveTime = getDateString();
	root.statusbar.updateStatusText("Corpus Saved !");
	root.ctrlwin.infotable.updateData();
	root.printDebugMessage("CORPUS SAVED");
	return true;
    }

    /** 
     * Read out the annotation track elements from the corpus and add them to the
     * anotation layers.
     * @return boolean - true if all stored elements are added correctly
     */
    public boolean readTheAnnotaionsFromTheCorups() {
	annohashmap.clear();
  	root.printDebugMessage("Read the Annotation from the Corpus.");
  	// First get all NOMElements as itterator
	java.util.Iterator elementiterator = nomwritecorpus.getElements();

	
  	// Go throught the elements
  	while  (elementiterator.hasNext() == true) 
	    {
		sumoffoundedannotationelements++;
		NOMWriteElement ne = (NOMWriteElement) elementiterator.next();
		try {
			
		    root.printDebugMessage("Found Element with Name: "+ne.getName()+" StartTime: "+ne.getStartTime()+" EndTime: "+ne.getEndTime()+" Layer: "+ne.getLayer().getName());
		    if (java.lang.Double.isNaN(ne.getEndTime()) == true || java.lang.Double.isNaN(ne.getStartTime()))
			{
			    root.printDebugMessage("Start or Endtime of an Annotationelement was NAN");
			    continue;
			}
		    // Get the Layer where this track element is placed
		    String layername = ne.getLayer().getName();
		    AnnotationLayer al = root.layermanager.getLayerByID(root.layermanager.findLayerByName(layername));
		    // Crate an new annotaion element and add it to the layer
		    AnnotationElement ae = new AnnotationElement(ne,al);
		    al.annotationelements.add(ae);  
		    // Add the new reference to the hashtable
		    annohashmap.put(ne,ae);
		} catch (Throwable t) {}
	    }
	root.printDebugMessage("FOUND ALLTOGETHER "+sumoffoundedannotationelements+" ANNOTATION TRACK ELEMETS");
	return true;
    }
    /**return the date and time as string */
    public String getDateString() {
	java.util.Date dt = new java.util.Date();
	java.text.SimpleDateFormat df = new java.text.SimpleDateFormat( "yyyy-MM-dd HH:mm:ss.S" );
	return(df.format(dt));
    }

    public java.util.List getLoadedObservations() {
	return observationsLoaded;
    }
    
}
