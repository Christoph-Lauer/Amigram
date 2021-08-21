/******************************************************************
 *                     OtabFrame.java  -  description
 *                    -----------------------
 * @author 		  Christoph Lauer
 * @version 		  0.1  
 * @see			  www.amiproject.de
 * copyright		: (C) 2003 @ christoph lauer
 * begin 		: Thu May 18 16:00:00 CET 2004
 * last save   	        : Time-stamp: <05/08/19 14:04:31 clauer> 
 * compiler    	        : java version 1.4.2 
 * operating system     : Linux 
 * editor		: xemacs 21.4
 * description          : The program control dialog window
 ******************************************************************/

package de.dfki.ami.amigram.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JViewport;
import javax.swing.RepaintManager;

import de.dfki.ami.amigram.tools.AnnotationElement;
import de.dfki.ami.amigram.tools.AnnotationLayer;
import de.dfki.ami.amigram.tools.LayerCollectionPanel;
import de.dfki.ami.amigram.tools.MediaTimeSynchronizeListener;
import de.dfki.ami.amigram.tools.OtabEventHandler;


public class OtabFrame extends JInternalFrame implements ComponentListener,MediaTimeSynchronizeListener,AdjustmentListener
{
    public    OtabDefaultPanel     defaultpanel         = new OtabDefaultPanel();
    public    LayerCollectionPanel layercollectionpanel;
    public    JScrollPane          scrollpane,hiddenscrollpane;
    private   JSplitPane           splitpane;
    public    HierarchyTreePanel   hierarchytreepanel;
    public    OtabToolBar          toolbar;
    public    boolean              defaultimageispainted = true;
    public    RepaintManager       repaintmanager       = RepaintManager.currentManager(this);
    protected OtabEventHandler     eventhandler;
    private   DesktopFrame         root;
    private   int                  lastactiveannotationelement=-1;
    private   boolean first = true;

    public OtabFrame(DesktopFrame r)
    {
	super("Observable Track Annotation Board",true,false,true);
	root = r;
	//setTitle();
 	//setClosable(false);
	addComponentListener(this);
	getContentPane().add(defaultpanel);
	// Enable DoubleBuffering for all components
	repaintmanager.setDoubleBufferingEnabled(true); 
	// register the OTAB by the MediaTimeSynchronizeListener
	root.synchronizer.registerListener(this,"OTAB");
	toolbar = new OtabToolBar(root);
    }

    public void finalize() 
    {
	root.synchronizer.unregisterListener(this);
    }
    
    class OtabDefaultPanel extends JPanel
    {
//  	java.net.URL imageurl = this.getClass().getResource("otab.gif");
//  	Image defaultimage = new ImageIcon(imageurl).getImage();
    	//Image defaultimage = Toolkit.getDefaultToolkit().getImage( "otab.gif" );
    	
    	IconManager iconManager = IconManager.getInstance();
    	Icon icon = iconManager.getIcon(IconManager.OTAB);
    	Image defaultimage = ((ImageIcon)icon).getImage();

    	
    	public void paint(Graphics g1)
    	{
    		Graphics2D g = (Graphics2D) g1;
    		Dimension size = getSize();
    		short x = (short)size.getWidth();
    		short y = (short)size.getHeight();
    		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
    		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BICUBIC);
    		g.drawImage(defaultimage,0,0,x,y,new Color(0,0,0),this);
    	}
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
    /**
     * Builds the AnnotationBoard-Panel and his sub instances. Called from Zooom........
     */
    public void buildAnnotationBoardPanel() {
	// First remove the old ones and instanciate if needed.
	if (first == true)
	    {
		first = false;
		getContentPane().add(toolbar,BorderLayout.EAST);
	    }
	if (defaultimageispainted == true)
	    {
		remove(defaultpanel);
		defaultimageispainted = false;
	    }
	if (splitpane != null)
	    {
		remove(splitpane);
	    }
	if (toolbar == null)
	    {
		toolbar = new OtabToolBar(root);
		getContentPane().add(toolbar,BorderLayout.EAST);
	    }
	// instaciate the panels
	layercollectionpanel = new LayerCollectionPanel(root);
	hierarchytreepanel = new HierarchyTreePanel(root);
	
	// scrollpane for the layers
 	scrollpane = new JScrollPane(layercollectionpanel);
	layercollectionpanel.setPreferredSize(new Dimension(layercollectionpanel.getWidth(),layercollectionpanel.getHeight()));
	scrollpane.getViewport().setScrollMode(JViewport.BLIT_SCROLL_MODE); 
	scrollpane.getVerticalScrollBar().addAdjustmentListener(this);
	
	//scrollpane for the hierarchytreepanel
	hiddenscrollpane = new JScrollPane(hierarchytreepanel);
	hiddenscrollpane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
	hiddenscrollpane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
	hierarchytreepanel.setPreferredSize(new Dimension(hierarchytreepanel.getWidth(),hierarchytreepanel.getHeight()));
	
	// the splitpane
	splitpane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,hiddenscrollpane,scrollpane);
	splitpane.setOneTouchExpandable(true);
	splitpane.setContinuousLayout(true);
	splitpane.setDividerLocation(hierarchytreepanel.getWidth());

	// And finaly add the scrollpane and the toolbar to the otab window.
	getContentPane().add(splitpane,BorderLayout.CENTER);
	validate();
	
    }
    public void receiveMediaTimeChange(javax.media.Time time)
    {
	if (root.config.synchronizeTimeline==false)
	    return;
	int x = root.layermanager.getLayerByID(0).convertTimeToPixel(time.getSeconds());
	//root.printDebugMessage("OTAB receives media time change");
	int viewportsize  = (int)scrollpane.getSize().getWidth();
	int viewpositionX  = (int)scrollpane.getViewport().getViewPosition().getX();
	int viewpositionY  = (int)scrollpane.getViewport().getViewPosition().getY();
	
	int leftborder  = viewpositionX;
	int rightborder = leftborder+viewportsize;
	if (root.config.synchronizeTimelineContinuously==true)
	    {
		x-=150;
		int xa = 0;
		if (x<xa) x=xa;
		int xe = root.layermanager.getLayerByID(0).getWidth()-viewportsize;
		if (x>xe) x=xe; 
		scrollpane.getViewport().setViewPosition(new Point(x,viewpositionY));
	    }
	else
	    {
		if (x<leftborder+30)
		    {
			x+= -viewportsize+50;
			int xa=0;
			if (x<xa) x=0;
			scrollpane.getViewport().setViewPosition(new Point(x,viewpositionY));
		    }
		if (x>rightborder-30)
		    {  
			x+= -50;
			int xe = root.layermanager.getLayerByID(0).getWidth()-viewportsize;
			if (x>xe)x=xe; 
			scrollpane.getViewport().setViewPosition(new Point(x,viewpositionY));
		    }
	    }
	root.layermanager.timelinealcurrenttime = time.getSeconds();
	// if anno element tracking is enabled, search for selected layers
	if (root.config.recognizeElementsWhilePlaying==true)
	    {
		int activelayer = root.layermanager.getActiveLayer();
		if (activelayer != -1)
		    { 
			AnnotationLayer al = root.layermanager.getLayerByID(activelayer);
			if (al != null)
			    {			    
				int active = al.getAnnoElementForTime(time.getSeconds(),-1);
				// only update if element has changed
				if (active != -1 && active!=lastactiveannotationelement) 
				    {
					root.printDebugMessage("Annotation Element recognized while playing.");
					al.activeannotationelement = active;
					al.repaint();
					// Call to update the Annotation Table in the control dialog.
					root.getControlWindow().annotable.updateAnnotable();
					// The timelineal too
					AnnotationElement ae = (AnnotationElement) al.annotationelements.get(al.activeannotationelement);
					root.layermanager.timelinealstarttime = ae.begintime;
					root.layermanager.timelinealendtime   = ae.endtime;
					root.getControlWindow().tabbedpane.setSelectedComponent(root.getControlWindow().annotable);
					lastactiveannotationelement = active;
					// redraw the condition net only if the panel is moved (prevents shadows)
					if (root.config.recognizeElementsWhilePlaying == true)
					    layercollectionpanel.drawConditionNet(layercollectionpanel.getGraphics(),ae);
				    }
			    }
		    }
	    }
	root.layermanager.getLayerByID(0).repaint();
    }
    /**
     * For the Scrollbar Events
     */
    public void adjustmentValueChanged(AdjustmentEvent e)
    {
	hiddenscrollpane.getVerticalScrollBar().setValue(e.getValue());
    }
}
