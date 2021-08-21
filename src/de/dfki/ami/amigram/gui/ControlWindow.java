/******************************************************************
 *                     ControlWindow.java  -  description
 *                    -----------------------
 * @author 		  Christoph Lauer
 * @version 		  0.1  
 * @see			  www.amiproject.de
 * copyright		: (C) 2003 @ christoph lauer
 * begin 		: Thu May 18 16:00:00 CET 2004
 * last save   	        : Time-stamp: <05/08/09 10:13:00 clauer> 
 * compiler    	        : java version 1.4.2 
 * operating system     : Linux 
 * editor		: xemacs 21.4
 * description          : The program control dialog window
 ******************************************************************/

package de.dfki.ami.amigram.gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import de.dfki.ami.amigram.tools.AnnoTable;
import de.dfki.ami.amigram.tools.ControlEventHandler;
import de.dfki.ami.amigram.tools.InfoTable;

public class ControlWindow extends JInternalFrame implements ComponentListener
{   
    public    InfoTable           infotable;
    public    AnnoTable           annotable;
    public    JButton             markupnowbutton,deletemarkupbutton,orderbutton,spider;
    public    ControlEventHandler eventhandler;
    public    JCheckBox           antialias,order,autoscroll,syncevents,syncstart,genspec,genosca,fullscreen,autoorderwins,showopendialog,synctimeline,synctimelinecont,synctimelineplaying,track,secs,otf,autozoom;
    public    JSlider             zoomslider;
    public    JPanel              p1,p2,p3,p4;
    public    JTabbedPane         tabbedpane = new JTabbedPane();
    public    JSpinner            reaktiontime;
    public    JComboBox           observations,agents;

    private   DesktopFrame        root;
    private   JPanel              p41 = new JPanel();
    private   JPanel              p42 = new JPanel(); 

    public ControlWindow(String titel, DesktopFrame r)
    {
	super(titel);
	root = r;
	eventhandler = new ControlEventHandler(root);
 	setClosable(false);
 	setResizable(true);
	putClientProperty("JInternalFrame.isPalette", Boolean.TRUE);
	addComponentListener(this);
	infotable = new InfoTable(root);
	infotable.setToolTipText("<html><b>INFORMATION VIEW</b><br>Information about the<br>video and the annotation is<br>displayed here.</html>");
	tabbedpane.addTab("Info",infotable);

	p1 = new JPanel();
	p1.setLayout(new GridLayout(2,1));
	JPanel p11 = new JPanel();
	p11.setLayout(new GridLayout(5,1));
	p11.setBorder(new CompoundBorder(new smooth.basic.SmoothTitledBorder(null,"Media Synchronize Settings",TitledBorder.LEFT, TitledBorder.TOP),new EmptyBorder(5,5,5,5))); 
	syncstart = new JCheckBox("Synchronize Media Start/Stop",root.config.synchronizeMediaStartStopEvents);
	syncstart.addActionListener(eventhandler);
	syncstart.setToolTipText("<html><b>Synchronize Media Start/Stop</b><br>Synchronizes all players start/stop events.</html>");
	p11.add(syncstart);
	syncevents = new JCheckBox("Synchronize Media Move Events",root.config.synchronizeMediaTimeChanges);
	syncevents.addActionListener(eventhandler);
	syncevents.setToolTipText("<html><b>Synchronize Media Move Events</b><br>Synchronizes all Time Change Events with all Players.</html>");
	p11.add(syncevents);
	synctimeline = new JCheckBox("Synchronize Otab Timeline",root.config.synchronizeTimeline);
	synctimeline.addActionListener(eventhandler);
	synctimeline.setToolTipText("<html><b>Synchronize Otab Timeline</b><br>Synchronizes all Time Change Events with<br>the Timeline on the OTAB.</html>");
	p11.add(synctimeline);
	synctimelineplaying = new JCheckBox("Synchronize Otab Timeline while Playing",root.config.synchronizeTimelinePlaying);
	synctimelineplaying.addActionListener(eventhandler);
	synctimelineplaying.setToolTipText("<html><b>Synchronize Otab while Playing</b><br>If enabled the Timeline will be synchronized while playing.</html>");
	p11.add(synctimelineplaying);
	synctimelinecont = new JCheckBox("Synchronize Otab Timeline Continuously",root.config.synchronizeTimelineContinuously);
	synctimelinecont.addActionListener(eventhandler);
	synctimelinecont.setToolTipText("<html><b>Synchronize Otab Timeline</b><br>If enabled the Timeline will be scrolled continuous.<br>Else the layers will be scolled to the right position stepwise.</html>");
	p11.add(synctimelinecont);
	synctimelinecont.setEnabled(root.config.synchronizeTimeline);
	JPanel p12 = new JPanel();
	p12.setLayout(new GridLayout(5,1));
	genosca = new JCheckBox("Auto Generate Oszillogram for opened Media",root.config.autoGenerateOszilloskop);
	genosca.addActionListener(eventhandler);
	genosca.setToolTipText("<html><b>Auto Generate Oszillogram</b><br>Generates automatical a Oszillogram for opened Media Files.</html>");
	p12.add(genosca);
	genspec = new JCheckBox("Auto Generate Spectrum for opened Media",root.config.autoGenerateSpectrum);
	genspec.addActionListener(eventhandler);
	genspec.setToolTipText("<html><b>Auto Generate Spectrum</b><br>Generates automatical a Spectrum for opened Media Files.</html>");
	p12.add(genspec);
	p12.setBorder(new CompoundBorder(new smooth.basic.SmoothTitledBorder(null,"Other Media Settings",TitledBorder.LEFT, TitledBorder.TOP),new EmptyBorder(5,5,5,5))); 
	p1.add(p11);
	p1.add(p12);
	p1.setToolTipText("Media Player Settings");
	tabbedpane.addTab("Media",p1);

	p2  = new JPanel();
	p2.setToolTipText("Observable Track Annotation Board Card");
	p2.setLayout(new GridLayout(3,1));
	JPanel p21 = new JPanel();
	p21.setBorder(new CompoundBorder(new smooth.basic.SmoothTitledBorder(null,"General Otab Settings",TitledBorder.LEFT, TitledBorder.TOP),new EmptyBorder(5,5,5,5))); 
	p21.setLayout(new GridLayout(3,1));
	order = new JCheckBox("Automatical Order Layers",root.config.automaticalOrderLayers);
	order.addActionListener(eventhandler);
	order.setToolTipText("<html><b>Automatical Order Layers</b><br>Orders the Annotation Tree.<br>Now and while opening.</html>");
	autoscroll = new JCheckBox("Autoscroll",true);
	autoscroll.addActionListener(eventhandler);
	autoscroll.setToolTipText("<html><b>Autoscroll</b><br>Scrolls automaticly when the mouse.<br>reaches an border of Annotation Layer.</html>");
	p21.add(order);
	track = new JCheckBox("Recognize Elements while Playing",root.config.recognizeElementsWhilePlaying);
	track.addActionListener(eventhandler);
	track.setToolTipText("<html><b>Recognize Elements while Playing</b><br>Recognizes automaticly the Annotation Elements<br>while playing audio selects them..</html>");
	p21.add(track);
	secs = new JCheckBox("seconds instead of mm.ss",root.config.secoundsInTimelineal);
	root.printDebugMessage(""+root.config.secoundsInTimelineal);
	secs.setToolTipText("Sounds instead of minutes/seconds\nin Timelineal.");
	secs.addActionListener(eventhandler);
	p21.add(secs);
	p2.add(p21);
	JPanel p22 = new JPanel();
	zoomslider = new JSlider(100,600000,root.config.zoom);
	zoomslider.setMajorTickSpacing(12000);
	zoomslider.setPaintTicks(true);
	zoomslider.addChangeListener(eventhandler);
	p22.setBorder(new CompoundBorder(new smooth.basic.SmoothTitledBorder(null,"OTAB Timeline Zoom Factor",TitledBorder.LEFT, TitledBorder.TOP),new EmptyBorder(5,5,5,5))); 
	p22.setToolTipText("<html><p><b><font size=3>Timeline Zoom Factor</font><b></p><p><font size=2>Zooms the annotation board in and out.</html>");
	p22.add(zoomslider);
	p2.add(p22);
	JPanel p23 = new JPanel();
	orderbutton = new JButton("Order Layers");
	orderbutton.addActionListener(eventhandler);
	spider = new JButton("Spider");
	spider.setToolTipText("Shows all relation between the elements !");
	orderbutton.addActionListener(eventhandler);
	spider.addActionListener(eventhandler);
	p23.add(orderbutton);
	p23.add(spider);
	p23.setBorder(new CompoundBorder(new smooth.basic.SmoothTitledBorder(null,"Other OTAB functions",TitledBorder.LEFT, TitledBorder.TOP),new EmptyBorder(5,5,5,5))); 
	p2.add(p23);
	p2.setToolTipText("<html><b>OTAB SETTINGS</b><br>Change settings for the OTAB<br>(Obversable Track Annotation Board)<br>in there tab..</html>");
	tabbedpane.addTab("OTAB",p2);

	annotable = new AnnoTable(root);
	annotable.setToolTipText("<html><b>ANNOTATION VIEW</b><br>Information about the<br>annotation track elements<br>displayed here.</html>");
	tabbedpane.addTab("AnnoContent",annotable);

	p4  = new JPanel();
	p4.setToolTipText("<html><b>NXT SETTINGS</b><br>Here are the settings for<br>the NXT module chaged.</html>");
	p4.setLayout(new GridLayout(3,1));
	observations = new JComboBox();
	agents = new JComboBox();
	p41.setBorder(new CompoundBorder(new smooth.basic.SmoothTitledBorder(null,"Agents for new Elements",TitledBorder.LEFT, TitledBorder.TOP),new EmptyBorder(5,5,5,5))); 
	p41.add(observations);
	p42.setBorder(new CompoundBorder(new smooth.basic.SmoothTitledBorder(null,"Observations for new Elements",TitledBorder.LEFT, TitledBorder.TOP),new EmptyBorder(5,5,5,5))); 
	p42.add(agents);
	p4.add(p41);
	p4.add(p42);
	tabbedpane.addTab("NXT",p4); 

	PerformanceMonitor p51 = new PerformanceMonitor();
	p51.start();
	JPanel p5  = new JPanel();
	p5.setBorder(new CompoundBorder(new smooth.basic.SmoothTitledBorder(null,"Memory Performance Monitor",TitledBorder.LEFT, TitledBorder.TOP),new EmptyBorder(5,5,5,5))); 
	p5.add(p51);
	p5.setLayout(new GridLayout(1,1));
	p5.setToolTipText("Performance Monitor");
	tabbedpane.addTab("Memory",p5); 

	JPanel p6 = new JPanel();
	p6.setLayout(new GridLayout(2,1));
	JPanel p61 = new JPanel();
	p61.setLayout(new GridLayout(3,1));
	p61.setBorder(new CompoundBorder(new smooth.basic.SmoothTitledBorder(null,"Window Settings",TitledBorder.LEFT, TitledBorder.TOP),new EmptyBorder(5,5,5,5))); 
	fullscreen = new JCheckBox("Fullscreen",root.config.synchronizeMediaStartStopEvents);
	fullscreen.addActionListener(eventhandler);
	fullscreen.setToolTipText("<html><b>Fullscreen</b><br>Switch Fullscreen on/off.</html>");
	p61.add(fullscreen);
	autoorderwins = new JCheckBox("Automatical Order Windows",root.config.automaticalOrderWindows);
	autoorderwins.addActionListener(eventhandler);
	autoorderwins.setToolTipText("<html><b>Automatical Order Windows</b><br>If this Function is enabled, all internal<br>Windows are placed and resized automaicely.</html>");
	p61.add(autoorderwins);
	p6.add(p61);
	JPanel p62 = new JPanel();
	showopendialog = new JCheckBox("Show Open Dialog when Program starts",root.config.synchronizeMediaStartStopEvents);
	showopendialog.addActionListener(eventhandler);
	showopendialog.setToolTipText("<html><b>Show Open Dialog when Program starts</b><br>Shows automatically the open Dialog.</html>");
	p62.add(showopendialog);
	autozoom = new JCheckBox("Autozoom to 10 seconds",root.config.autoZoom);
	autozoom.addActionListener(eventhandler);
	autozoom.setToolTipText("<html><b>Autozoom to 10 seconds</b><br>Zooms the OTAB automatical to 10 sec. wen the corpus is opened.</html>");
	p62.add(autozoom);
	antialias = new JCheckBox("Antialiasing",true);
	showopendialog.setToolTipText("<html><b>Antialias</b><br>Enables/Disables the Antialiasing for all Elements.</html>");
	antialias.addActionListener(eventhandler);
	p62.add(antialias);
	p62.setLayout(new GridLayout(3,1));
	p62.setBorder(new CompoundBorder(new smooth.basic.SmoothTitledBorder(null,"Other Settings",TitledBorder.LEFT, TitledBorder.TOP),new EmptyBorder(5,5,5,5))); 
	p6.add(p62);
	tabbedpane.addTab("General",p6); 

	QueryPanel p7 = new QueryPanel(root);
	tabbedpane.addTab("NQL",p7); 

	JPanel p8 = new JPanel();
	p8.setLayout(new GridLayout(3,1));
	JPanel p81 = new JPanel();
	p81.setBorder(new CompoundBorder(new smooth.basic.SmoothTitledBorder(null,"\"On The Fly\" Segmentation",TitledBorder.LEFT, TitledBorder.TOP),new EmptyBorder(5,5,5,5))); 
	p8.setBorder(new CompoundBorder(new smooth.basic.SmoothTitledBorder(null,"\"On The Fly\" Segmentation",TitledBorder.LEFT, TitledBorder.TOP),new EmptyBorder(5,5,5,5))); 
	otf = new JCheckBox("Enable On The Fly Annotation",root.config.onTheFlyAnnotation);
	otf.setToolTipText("<html><b>On The Fly Annotation</b><br>Enables/Disables the on the fly annotation<br>with the N and M keys. See the help (F1)<br>for an dexcription.</html>");
	p81.add(otf);
	p8.add(p81);

	reaktiontime = new JSpinner();
	reaktiontime.setValue(new Integer(root.config.reaktionTimeOTF));
	reaktiontime.setToolTipText("<html><b>Reaktion Time</b><br>The Reaktion Time for the \"On The Fly\" Annotaion.<br>Decides how many Seconds the user can press an<br>Annotion Button after the Parent Segment is passed in time<br>while the \"On The Fly\" Annotation. This has only an effect<br>in structural layers.</html>");
 	//reaktiontime.setPreferredSize(new Dimension(130,22));
	JPanel p82 = new JPanel();
	p82.setBorder(new CompoundBorder(new smooth.basic.SmoothTitledBorder(null,"Reaktion Time for OTF Segmentation",TitledBorder.LEFT, TitledBorder.TOP),new EmptyBorder(5,5,5,5))); 
	p82.add(reaktiontime);
	p82.setToolTipText("<html><b>Reaktion Time</b><br>The Reaktion Time for the \"On The Fly\" Annotaion.<br>Decides how many milliseconds the user can press an<br>Annotion Button after the Parent Segment is passed in time<br>while the \"On The Fly\" Annotation. This has only an effect<br>in structural layers.</html>");
	p8.add(p82);
	tabbedpane.addTab("Segmentation",p8); 

	getContentPane().setLayout(new BorderLayout());
	getContentPane().add(tabbedpane,BorderLayout.CENTER);
	getContentPane().add(root.statusbar.panel,BorderLayout.SOUTH);
    }

    /**
     * This Function was called from the NOMCommunicator to set the
     * comboboxes for the observations and the agents.
     */
    public void setNxtBoxes(JComboBox ob, JComboBox ag) {
	agents = ag;
	observations = ob;
	p41.removeAll();
	p42.removeAll();
	p41.add(agents);
	p42.add(observations);
 }
    
    // as follows the implementation of the componentlistener
    //
    //
    public void componentHidden(ComponentEvent e){}
    public void componentMoved(ComponentEvent e)
    {
	if (root.config.automaticalOrderWindows == true)
	    {
		root.printDebugMessage("Otab Window Moved");
		root.placeSubwindows();
	    }
    }
    public void componentResized(ComponentEvent e)
    {
 	if (root.config.automaticalOrderWindows == true)
	    {
		root.printDebugMessage("Otab Window Resized");
		root.placeSubwindows();
	    }
    }
    public void componentShown(ComponentEvent e){}
}
