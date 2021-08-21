/******************************************************************
 *                HierarchyTreePanel.java  -  description
 *                    -----------------------
 * @author 		  Christoph Lauer
 * @version 		  0.1  
 * @see			  www.amiproject.de
 * copyright		: (C) 2003 @ christoph lauer
 * begin 		: Thu Okt 30 17:00:00 CET 2003
 * last save   	        : Time-stamp: <05/04/20 12:20:40 clauer> 
 * compiler    	        : java version 1.4.2 
 * operating system     : Linux 
 * editor		: xemacs 21.4
 * description          : Draws based on the 
 ******************************************************************/

package de.dfki.ami.amigram.gui;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

import de.dfki.ami.amigram.tools.AnnotationLayer;
import de.dfki.ami.amigram.tools.LayerCollectionPanel;

public class HierarchyTreePanel extends JPanel implements ActionListener{

    static final int TREEDISTANCEWIDTH    = 70;

    private DesktopFrame   root;
    private Color          colorblack       = new Color(0,0,0);
    private Color          colorwhite       = new Color(255,255,255);
    private Color          colorlines       = new Color(50,50,90);
    private Color          colordarkviolet  = new Color(100,100,130);
    private Color          colortext        = new Color(80,20,20);
    private Color          colororange      = new Color(230,163,4);
    private JCheckBox[]    checkboxes;
    private Image          defaultimage;
    
    private AlphaComposite compositealpha09 = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.09f);
    private AlphaComposite compositealpha1  = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.18f);
    private AlphaComposite compositealpha3  = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f);
    private AlphaComposite compositealpha5  = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f);
    private AlphaComposite compositealpha6  = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f);
    private AlphaComposite compositealpha7  = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f);
    private AlphaComposite compositealpha10 = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f);
    
    public  int            panelwidth;     
    public  int            panelheight;     

    /**
     * Constructor.
     * @param DesktopFrame - gets the reference to the main class.
     */
    public HierarchyTreePanel (DesktopFrame r) {
	r.printDebugMessage("Rebuild Hierarchy Panel");
	root = r;
	setLayout(null);
//	java.net.URL imageurl = this.getClass().getResource("brushed_metal.jpg");
//	defaultimage = new ImageIcon(imageurl).getImage();
	IconManager iconManager = IconManager.getInstance();
	Icon icon = iconManager.getIcon(IconManager.TREE);
	defaultimage = ((ImageIcon)icon).getImage();
	
	// Fist callculate the max hierarchy deepth for the dimensions of the hierarchytree.
	int maxhierarchydeepth = 1;
	for (int i=0;i<root.layermanager.getNumberOffLayers()-1;i++) {
	    if (maxhierarchydeepth < root.layermanager.getHierarchyDeepthForID(i))
		maxhierarchydeepth = root.layermanager.getHierarchyDeepthForID(i);
	}
	int panelwidth = 48+(maxhierarchydeepth+1)*TREEDISTANCEWIDTH;
	int panelheight = getHeightForPosition(root.layermanager.getNumberOffLayers()-1);
	//int panelheight = 500+(root.layermanager.getNumberOffLayers()-1)*LayerCollectionPanel.LAYERHEIGHT+20;
	//      Not to small because the time information for the time lineal
	// 	if (panelwidth < 170)
	// 	    panelwidth = 170;
	setPreferredSize(new Dimension(panelwidth,panelheight));
	setMaximumSize(new Dimension(panelwidth,panelheight));
	setSize(panelwidth,panelheight);
	addCheckBoxes();
    }

    /** add the checkboxes to the panel */
    private void addCheckBoxes()
    {
	Vector layers = new Vector();
	Vector al = root.layermanager.layervector;
	checkboxes = new JCheckBox[al.size()-1];
	for (int i=1;i<al.size();i++)
	    {		
		checkboxes[i-1] = new JCheckBox( String.valueOf(((AnnotationLayer)al.get(i)).getID()),((AnnotationLayer)al.get(i)).getPaintEnabled());
		checkboxes[i-1].setBounds(26,getHeightForPosition(((AnnotationLayer)al.get(i)).paintOrderNumber)+6,20,15);
		checkboxes[i-1].addActionListener(this);
		add(checkboxes[i-1]);
	    }
    }

    /**
     * Overwritten paint methode.
     * @param Graphics - The Pen to paint.
     */
    public void paint(Graphics g1) {
	root.printDebugMessage("Repaint Hierarchy Panel");
	Graphics2D g = (Graphics2D)g1;
 	if (root.getControlWindow().antialias.isSelected() == true)	    
	    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
	else
	    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_OFF);	
	g.setColor(colordarkviolet);
	g.fillRect(0,0,getWidth(),getHeight());
	g.drawImage(defaultimage,0,0,1024,768,new Color(0,0,0),this);
	// callculate the values for the node ovals and call the draw function
	for (int i=0;i<root.layermanager.getNumberOffLayers()-1;i++) {
	    // First check the type of the layer
	    int layertype = root.layermanager.getLayerByPaintOrder(i).getLayerType();
	    if (layertype == AnnotationLayer.TIMELINEAL) 
		continue;
	    String text = root.layermanager.getLayerByPaintOrder(i).getName();
	    int    x    = root.layermanager.getHierarchyDeepthForID(root.layermanager.getLayerByPaintOrder(i).getID());
	    int    y    = i;
	    boolean recursive = root.layermanager.getLayerByPaintOrder(i).getRecursive();
	    boolean active = false;
	    if (root.layermanager.getActiveLayer() == root.layermanager.getLayerByPaintOrder(i).getID())
		active = true;
	    boolean signal = false;
	    if (layertype == AnnotationLayer.OSZILLOSKOP || layertype == AnnotationLayer.SPECTRAL)
		signal = true;
	    drawNodeOval(g,text,x,y,recursive,active,signal,root.layermanager.getLayerByPaintOrder(i));
	}
	// callculate the numbers for the conecting line
	for (int i=0;i<root.layermanager.getNumberOffLayers()-1;i++) {
	    int parentID = root.layermanager.getLayerByID(i).getParentID();
	    if (parentID != -1) {
		int hierarchydeepth = root.layermanager.getHierarchyDeepthForID(i);
		int parenthierarchydeepth = root.layermanager.getHierarchyDeepthForID(parentID);
		connectTwoNodes(g,parenthierarchydeepth,root.layermanager.getLayerByID(parentID).paintOrderNumber,hierarchydeepth,root.layermanager.getLayerByID(i).paintOrderNumber);
	    }
	}
	// finaly draw the time to pixel ratio and the duration
	double duration = root.layermanager.getVideoDuration();
	double width = root.layermanager.getLayerByID(1).getWidth();
	int ratio = (int)(width/duration);
	g.setComposite(compositealpha10);	
	g.setFont(new Font("Tahoma",0,9));
	g.setColor(new Color(0,0,0));
	g.drawString("Zoom: "+ratio+" Pixel per Second",54,15);
	String time = new String("");
	int minutes  = (int)(duration/60.0);		
	if (minutes < 10)
	    time += "0" + minutes + ":"; 
	else 
	    time += minutes + ":"; 
	int seconds = (int)(duration%60);
	if (seconds < 10)
	    time += "0" + seconds + ":"; 
	else 
	    time += seconds + ":";
	int milliseconds = (int)(1000*(duration-Math.floor(duration)));
	if (milliseconds < 10)
	    time += "00"; 
	else if (milliseconds < 100)
	    time += "0"; 
	time += milliseconds;
	time += " (min/sec/millisec)";
	g.drawString(time,54,25);
	g.setComposite(compositealpha09);
	g.drawLine(48,0,48,getHeight());
	g.drawLine(0,getHeightForPosition(1)+1,getWidth(),getHeightForPosition(1)+1);
	g.setComposite(compositealpha10);
	paintComponents(g);
    }
    /**
     * Draws the node oval
     * @param Graphics2D g - The pen.
     * @param String text - The test of the node
     * @param int x - The x position of the Node in block units.
     * @param int y - The y position of the Node in block units.
     * @param boolean recursive - Draws a gringel on the corner if the layer is a recursve layer.
     * @param boolean active - Draws the background of the Node red.
     */
    public void drawNodeOval (Graphics2D g, String text, int x, int y, boolean recursive, boolean active, boolean signal, AnnotationLayer al) {
 	if (root.getControlWindow().antialias.isSelected() == true)	    
	    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
	else
	    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_OFF);	
	// First callculate the postions of the parts
	int rectw   = TREEDISTANCEWIDTH;
	int recth;
	if (signal == true) 
	    recth = LayerCollectionPanel.LAYERHEIGHT_SIGNALS;
	else
	    recth = LayerCollectionPanel.LAYERHEIGHT;
	int posx    = x*rectw+44;
	int posy    = getHeightForPosition(y);
	int space   = 7;
	int arc1x   = posx+space;
	int arc1y   = posy+space;
	int arc1w   = space*2;
	int arc1h   = recth-2*space;
	int arc2x   = posx+rectw-3*space;
	int arc2y   = arc1y;
	int arc2w   = arc1w;
	int arc2h   = arc1h;
	int recx    = posx+2*space;
	int recy    = posy+space;
	int recw    = rectw-4*space;
	int rech    = recth-2*space;
	int textx   = posx+2*space-3;
	int texty   = posy+recth-space-5;
	int recurx  = posx+rectw-2*space;
	int recury  = posy;
	
	if (signal == true)
	    {
		if (active==false)
		    g.setColor(new Color(0,80,0));
		else 
		    g.setColor(new Color(200,0,0));
		g.setComposite(compositealpha10);	
		g.setFont(new Font("Tahoma",0,9));
		g.drawString(text,textx-3,texty+5);
		if (al.getLayerType() == AnnotationLayer.OSZILLOSKOP)
		    {
			g.drawString("OSZILLOSKOP:",textx-3,texty-25);
			g.drawString(al.samples.length+" Samples",textx-3,texty-15);
		    }
		if (al.getLayerType() == AnnotationLayer.SPECTRAL)
		    {
			g.drawString("SPECTRAL:",textx-3,texty-25);
			g.drawString(al.spectrum.length+" Windows",textx-3,texty-15);
		    }
		String time = new String("");
		int minutes  = (int)(al.duration/60.0);		
		if (minutes < 10)
		    time += "0" + minutes + ":"; 
		else 
		    time += minutes + ":"; 
		int seconds = (int)(al.duration%60);
		if (seconds < 10)
		    time += "0" + seconds + ":"; 
		else 
		    time += seconds + ":";
		int milliseconds = (int)(1000*(al.duration-Math.floor(al.duration)));
		if (milliseconds < 10)
		    time += "00"; 
		else if (milliseconds < 100)
		    time += "0"; 
		time += milliseconds;
		time += " (min/sec/millisec)";
		g.drawString(time,textx-3,texty-5);
		g.setColor(new Color(0,0,0));
		g.setComposite(compositealpha09);
		g.drawLine(0,getHeightForPosition(y+1)+1,getWidth(),getHeightForPosition(y+1)+1);
		return;
	    }
	// And draw the parts, first the inner fills
	g.setColor(colorlines);
	g.setComposite(compositealpha3);	
	if (al.getLayerType() == AnnotationLayer.TIMEALIGNED)
	    {
		GradientPaint gp = new GradientPaint(0,0,new Color(90,90,90),5,0,new Color(155,155,155),true);
		g.setPaint(gp);
	    }
	else
	    {
		GradientPaint gp = new GradientPaint(0,0,new Color(100,149,250),5,0,new Color(50,40,120),true);
		g.setPaint(gp);
	    }
	g.fillArc(arc1x,arc1y,arc1w,arc1h,90,180);
	g.fillArc(arc2x,arc2y,arc2w,arc2h,270,180);
	g.fillRect(recx,recy,recw,rech);
	// And the the border
	g.setComposite(compositealpha10);	
	g.setColor(colorlines);
	if (active == true) {
	    GradientPaint gp = new GradientPaint(0,0,Color.red,5,0,Color.orange,true);
	    g.setPaint(gp);
	    g.setComposite(compositealpha7);	
	}
	g.setStroke(new BasicStroke(2.0f));
	g.drawArc(arc1x,arc1y,arc1w,arc1h,90,180);
	g.drawArc(arc2x,arc2y,arc2w,arc2h,270,180);
	g.drawLine(recx+1,recy,recx+recw-2,recy);
	g.drawLine(recx+1,recy+rech,recx+recw-2,recy+rech);
	if (recursive == true)
	    g.drawArc(recurx,recury,space*2,space*2,270,270);
	// And finaly the text
	g.setColor(colortext);
	g.setComposite(compositealpha10);	
	g.setFont(new Font("Tahoma",0,9));
	g.drawString(text,textx,texty);
	g.setColor(new Color(0,0,0));
	g.setComposite(compositealpha09);
	g.drawLine(0,getHeightForPosition(y+1)+1,getWidth(),getHeightForPosition(y+1)+1);
	g.setComposite(compositealpha6);
	g.setFont(new Font("Tahoma",0,10));
	text = ""+al.annotationelements.size();
	g.drawString(text,2,texty+3);
    }
    /**
     * Connects two Nodes with a line.
     * The coordinate system values are in block units
     * @param Graphics2D g - Pen
     * @param x1 - source node x position
     * @param y2 - source node y position
     * @param x2 - destination node x position
     * @param y3 - destination node y position
     */
    public void connectTwoNodes(Graphics2D g,int x1, int y1, int x2, int y2) {
	// First callculate the positions
	int rectw   = TREEDISTANCEWIDTH;
	int recth   = LayerCollectionPanel.LAYERHEIGHT;
	int posx1    = 44+x1*rectw+rectw-6;
	int posy1    = getHeightForPosition(y1)+14;
	int posx2    = 44+x2*rectw+7;
	int posy2    = getHeightForPosition(y2)+14;

	// And the paint the Line
 	if (root.getControlWindow().antialias.isSelected() == true)	    
	    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
	else
	    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_OFF);		
	g.setColor(colorlines);
	g.setComposite(compositealpha7);	
	g.setStroke(new BasicStroke(2.0f));
	g.drawLine(posx1,posy1,posx2,posy2);	 

    }
    public int getHeightForPosition(int pos)
    {
	int height = 0;
	for(int i=0;i<pos;i++)
	    {
		height += root.layermanager.getLayerByPaintOrder(i).getHeight();
	    }
	return (height);
    }
    public void actionPerformed(ActionEvent e)
    {
	int i = (Integer.decode(e.getActionCommand())).intValue();
	AnnotationLayer al = root.layermanager.getLayerByID(i);
	al.setPaintEnabled(checkboxes[i-1].isSelected());
	al.repaint();
    }
}
