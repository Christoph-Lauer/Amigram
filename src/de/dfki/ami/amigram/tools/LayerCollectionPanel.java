/******************************************************************
 *          LayerCollectionPanel.java  -  description
 *                    -----------------------
 * @author 		  Christoph Lauer
 * @version 		  0.1  
 * @see			  www.amiproject.de
 * copyright		: (C) 2003 @ christoph lauer
 * begin 		: Fri may 21 17:00:00 CET 2003
 * last save   	        : Time-stamp: <05/04/20 12:00:47 clauer> 
 * compiler    	        : java version 1.4.2 
 * operating system     : Linux 
 * editor		: xemacs 21.4
 * description          : Handles the communication with the NOM.
 ******************************************************************/

package de.dfki.ami.amigram.tools;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.JPanel;

import net.sourceforge.nite.nom.nomwrite.impl.NOMWriteElement;
import de.dfki.ami.amigram.gui.DesktopFrame;

public class LayerCollectionPanel extends JPanel {

    public static final int LAYERHEIGHT         = 30;
    public static final int LAYERHEIGHT_RECUR   = 20;
    public static final int LAYERHEIGHT_SIGNALS = 60;
    public static final int LAYERWIDTHFACTOR    = 1;

    private DesktopFrame root;
    private HashMap      layers = new HashMap(); // stores the heights for the layers
    public  int          maxDepth = 0;
    
    public LayerCollectionPanel(DesktopFrame r) {
	layers.clear();
	setBackground(new Color(190,190,225));
	root = r;
	setDoubleBuffered(true);
	setLayout(null);
	int panelheight = 0;
	// First add the layers to the panel.
	for (int i=0;i<root.layermanager.getNumberOffLayers()-1;i++) {
	    AnnotationLayer al = root.layermanager.getLayerByPaintOrder(i);
	    add(al);
	    if (al.getLayerType()==AnnotationLayer.OSZILLOSKOP || al.getLayerType()==AnnotationLayer.SPECTRAL) 
		{
		    layers.put(al,new Integer(panelheight));
		    al.setBoundaries(0,panelheight,root.getControlWindow().zoomslider.getValue()*LAYERWIDTHFACTOR,LAYERHEIGHT_SIGNALS);
		    panelheight+=LAYERHEIGHT_SIGNALS;
		}
	    else if (al.recursive == false)
		{
		    layers.put(al,new Integer (panelheight));
		    al.setBoundaries(0,panelheight,root.getControlWindow().zoomslider.getValue()*LAYERWIDTHFACTOR,LAYERHEIGHT);
		    panelheight+=LAYERHEIGHT;
		}	    
	    else
		{
		    maxDepth = root.nomcommunicator.nomwritecorpus.getMaxDepth(al.nitelayer);
		    if (maxDepth == 0) maxDepth = 1;
		    layers.put(al,new Integer (panelheight));
		    al.setBoundaries(0,panelheight,root.getControlWindow().zoomslider.getValue()*LAYERWIDTHFACTOR,LAYERHEIGHT_RECUR*maxDepth);
		    panelheight+=LAYERHEIGHT_RECUR*maxDepth;
		}	    
	    root.printDebugMessage("LAYER ADDED TO CONTAINER "+al.getName());
	}
	// Callculate the sizes of the panel.
	int panelwidth  = root.getControlWindow().zoomslider.getValue()*LAYERWIDTHFACTOR;
	setSize(panelwidth,panelheight);
    }


    /**
     * Overwritten paint function
     */
    public void paint(Graphics g) {
	super.paint(g);
	// look for an selected element and call the net draw routine 
	if (root.layermanager.getActiveLayer() != -1) {
	    AnnotationLayer al = root.layermanager.getLayerByID(root.layermanager.getActiveLayer());
	    if (al != null) // can occur when a new corpus is load
		if (al.activeannotationelement != -1) {
		    AnnotationElement ae = (AnnotationElement)al.annotationelements.get(al.activeannotationelement);
		    drawConditionNet(g,ae);
		}
	}
    }
    
    /**
     * draws a conditions net for the Annotation Element
     */
    public void drawConditionNet(Graphics g1,AnnotationElement ae) {
	// first set the pen
 	Graphics2D g = (Graphics2D)g1;
 	g.setRenderingHint(RenderingHints.KEY_RENDERING,RenderingHints.VALUE_RENDER_SPEED);
  	if (root.getControlWindow().antialias.isSelected() == true)	    
 	    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
 	else
	    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_OFF);	
	AlphaComposite compositealpha  = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f);
	g.setComposite(compositealpha);
	g.setStroke(new BasicStroke(1.8f));

	// get the boundaries of the viewport
	int viewportsize = (int)root.getOtabWindow().scrollpane.getSize().getWidth();
	int viewposition = (int)root.getOtabWindow().scrollpane.getViewport().getViewPosition().getX();
	int leftborder  = viewposition-1;
	int rightborder = leftborder+viewportsize+2;

	// get the values for the active element
	java.awt.geom.Point2D ap = getMiddleCoordinatesForAnnoElement(ae);
	if (ap == null) 
	    return;
	NOMWriteElement nwe = ae.nomwriteelement;
	if (nwe == null)
	    return;
	int xa = (int)ap.getX();
	int ya = (int)ap.getY();
	g.drawOval(xa-2,ya-2,4,4);

	// first lines for the parent
	java.util.List parents = nwe.getParents();
	int nonvisibleparents = 0;
	if (parents != null) {
	    root.printDebugMessage("draw "+parents.size()+" parent lines");
	    Iterator it = parents.iterator();
	    while (it.hasNext()) {
		NOMWriteElement  nwep = (NOMWriteElement)it.next();
		AnnotationElement aep = (AnnotationElement)root.nomcommunicator.annohashmap.get(nwep);
		if (aep==null) {
		    root.printDebugMessage(" parent not referenced with visible layer !!!");
		    nonvisibleparents++;
		    continue;
		}
		java.awt.geom.Point2D pp = getMiddleCoordinatesForAnnoElement(aep);
		if (pp == null) 
		    return;
		int x1 = (int)ap.getX();
		int y1 = (int)ap.getY();
		int x2 = (int)pp.getX();
		int y2 = (int)pp.getY();
		if (x1>rightborder && x2>rightborder || x1<leftborder && x2<leftborder) 
		    continue;
		g.setColor(new Color(0,100,0));
		g.drawLine(x1,y1,x2,y2);
		g.drawOval(x2-2,y2-2,4,4);
	    }
	}
	// then the cildren 
	java.util.List childs = nwe.getChildren();
	int nonvisiblechilds = 0;
	if (childs != null) {
	    root.printDebugMessage("draw "+childs.size()+" children lines");
	    Iterator it = childs.iterator();
	    while (it.hasNext()) {
		NOMWriteElement  nwec = (NOMWriteElement)it.next();
		AnnotationElement aec = (AnnotationElement)root.nomcommunicator.annohashmap.get(nwec);
		if (aec==null) {
		    root.printDebugMessage(" child not referenced with visible layer !!!");
		    nonvisiblechilds++;
		    continue;
		}
		java.awt.geom.Point2D pp = getMiddleCoordinatesForAnnoElement(aec);
		if (pp == null) 
		    return;
		int x1 = (int)ap.getX();
		int y1 = (int)ap.getY();
		int x2 = (int)pp.getX();
		int y2 = (int)pp.getY();
		if (x1>rightborder && x2>rightborder || x1<leftborder && x2<leftborder) 
		    continue;
		g.setColor(new Color(0,0,100));
		g.drawLine(x1,y1,x2,y2);
		g.drawOval(x2-2,y2-2,4,4);
	    }
	}
	// draw non visible elements
	if (nonvisiblechilds != 0 || nonvisibleparents !=0)
	    {
		compositealpha  = AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.5f);
		g.setComposite(compositealpha);
		g.setStroke(new BasicStroke(2.0f));
		g.setColor(new Color(0,0,0));
		String text = "NON VISIBLE: ";
		if (nonvisibleparents != 0) text += "["+nonvisibleparents+" Parents]";
		if (nonvisiblechilds != 0) text += "["+nonvisiblechilds+" Childs]";
		g.drawString(text,xa+4,ya+5);
	    }
	// the pointers
// 	java.util.List pointers = ae.nomwriteelement.getPointers();
// 	if (pointers!=null) {
// 	    for (int i=0;i<pointers.size();i++) {
// 		// resolve annoelement
// 		NOMElement nwep = ((net.sourceforge.nite.nom.nomwrite.NOMPointer)pointers.get(i)).getToElement();
// 		if (nwep == null) 
// 		    continue;
// 		AnnotationElement aep = (AnnotationElement)root.nomcommunicator.annohashmap.get(nwep);
// 		java.awt.geom.Point2D.Float pp = getMiddleCoordinatesForAnnoElement(aep);
// 		if (pp==null) {
// 		    continue;
// 		}
// 		int x1 = (int)ap.getX();
// 		int y1 = (int)ap.getY();
// 		int x2 = (int)pp.getX();
// 		int y2 = (int)pp.getY();
// 		if (x1>rightborder && x2>rightborder || x1<leftborder && x2<leftborder) 
// 		    continue;
// 		g.setColor(new Color(1,0,0));
// 		g.drawLine(x1,y1,x2,y2);
// 		g.drawOval(x2-2,y2-2,4,4);
// 	    }
// 	}
    }

    /**
     * resolve the coordinates
     */
    public java.awt.geom.Point2D.Float getMiddleCoordinatesForAnnoElement(AnnotationElement ae) {
	if (ae.al==null)
	    return null;
	AnnotationLayer al = ae.al;
	if (al == null) 
	    return null;
	float x = (al.convertTimeToPixel(ae.begintime)+al.convertTimeToPixel(ae.endtime))/2;
	float y = 0;
	try {
	    y = ((Integer)layers.get(al)).floatValue();
	}
	catch (Exception e) {
	   root.printDebugMessage("ERROR No layer reference for "+al.getName());
	}
	// differ between recursive and normal layers
	if (al.recursive == false)
	    y+=(float)LAYERHEIGHT/2.0f;
	else {
	    int depth=0;
	    if (ae.nomwriteelement == null) 
		depth = al.activedepth;
	    else
		depth = ae.nomwriteelement.getRecursiveDepth();
	    y+=depth*(float)LAYERHEIGHT_RECUR+(float)LAYERHEIGHT_RECUR/2.0f;
	}
	return (new java.awt.geom.Point2D.Float(x,y));
    }
}
