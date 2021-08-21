/******************************************************************
 *                 AnnotationLayer.java  -  description
 *                    -----------------------
 * @author 		  Christoph Lauer
 * @version 		  0.1  
 * @see			  www.amiproject.de
 * copyright		: (C) 2003 @ christoph lauer
 * begin 		: Thu Okt 30 17:00:00 CET 2003
 * last save   	        : Time-stamp: <05/08/09 10:26:11 clauer> 
 * compiler    	        : java version 1.4.2 
 * operating system     : Linux 
 * editor		: xemacs 21.4
 * description          : The Layer.
 ******************************************************************/

package de.dfki.ami.amigram.tools;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.BitSet;
import java.util.Vector;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;

import net.sourceforge.nite.meta.NCoding;
import net.sourceforge.nite.meta.NElement;
import net.sourceforge.nite.meta.impl.NiteElement;
import net.sourceforge.nite.meta.impl.NiteLayer;
import net.sourceforge.nite.nom.nomwrite.NOMElement;
import net.sourceforge.nite.nom.nomwrite.impl.NOMWriteAnnotation;
import net.sourceforge.nite.nom.nomwrite.impl.NOMWriteAttribute;
import net.sourceforge.nite.nom.nomwrite.impl.NOMWriteElement;
import de.dfki.ami.amigram.gui.DesktopFrame;
import de.dfki.ami.amigram.gui.MenuBar;

public class AnnotationLayer extends JPanel 
    implements MouseListener , MouseMotionListener, ActionListener {

    public static final int TIMEALIGNED = 1;
    public static final int STRUCTURAL  = 2;
    public static final int FEATURAL    = 3;
    public static final int TIMELINEAL  = 4;
    public static final int SPECTRAL    = 5;
    public static final int OSZILLOSKOP = 6;
    public static final int MAX_CHILDS  = 100;
    
    private String   layername       = "";
    private int      ID              = -1;
    private int      parentID        = -1;
    private int      layertype       = -1;
    public  int      childcounter    = 0;
    private int[]    childlayers;
    private int      width           = 0;
    private int      height          = 0;
    private int      posX            = 0;
    private int      posY            = 0;
    public  boolean  recursive       = false;
    public  int      activedepth     = 0; // only needed for recursive layers 
    private int      hierarchydeepth = 0;
    private DesktopFrame root;
    private MenuBar menubar;
    private Color    colorblack = new Color(0,0,0);
    private Color    colorwhite = new Color(255,255,255);
    private Color    colorred   = new Color(255,0,0);
    private AlphaComposite compositealpha2  = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.2f);
    private AlphaComposite compositealpha3  = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f);
    private AlphaComposite compositealpha4  = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f);
    private AlphaComposite compositealpha5  = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f);
    private AlphaComposite compositealpha6  = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f);
    private AlphaComposite compositealpha7  = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f);
    private AlphaComposite compositealpha8  = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.8f);
    private AlphaComposite compositealpha10 = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f);
    private boolean   paintElements = true;
    public  Vector    annotationelements = new Vector();
    public  int       activeannotationelement = -1;
    public  NiteLayer nitelayer;
    public  int       paintOrderNumber;    // this is the order in which the layers are placed on the layercollectionpanel
    public  float     duration;
    public  float[]   samples;
    public  float[][] spectrum;
    public  boolean   otfstate = false;
    protected BufferedImage specimage;
    protected BufferedImage oscaimage;
    final JPopupMenu menu = new JPopupMenu();
    private boolean lastMouseDownOverAnnoElement = false; 
    private Point toolTipPoint = new Point(0,0);
    private int lastElement=-123; // used in the tooltip routine 
    private double lastTime = -1.0; // select multiple elements
    private BitSet lastActiveElements = new BitSet();; // select multiple elements
    private int clickcount;
    private NiteLayer nlayer;

    /**
     * The constructor initializes the parameters.
     * @param String name - The layer name.
     * @param int id - The internal ID of this layer.
     * @param imt parent - the ID of the parent layer is only needed by structural layers.
     * @param int lt - The layer type. (See the final constants for details)
     */
    public AnnotationLayer (DesktopFrame r,String name, int id, int parent, int lt) {
	root = r;
	menubar = r.getAmiGramMenuBar();
	paintOrderNumber = id;
	layername =name;
	ID = id;
	parentID = parent;
	layertype = lt;
	childlayers = new int[MAX_CHILDS];
	for (int i=0;i<MAX_CHILDS;i++) {
	    childlayers[i] = -1;
	}
	addMouseListener(this);
	addMouseMotionListener(this);
	if (lt==TIMEALIGNED || lt==STRUCTURAL)
	    setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
	// Create and add a menu item
	JMenuItem item = new JMenuItem("remove element");
	item.addActionListener(this);
	item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R,ActionEvent.CTRL_MASK));
	menu.add(item);
	menu.addSeparator();
	item = new JMenuItem("add child");
	item.addActionListener(this);
	item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C,ActionEvent.CTRL_MASK));
	menu.add(item);
	item = new JMenuItem("remove child");
	item.addActionListener(this);
	item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C,ActionEvent.ALT_MASK));
	menu.add(item);
	item = new JMenuItem("remove all children");
	item.addActionListener(this);
	item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C,ActionEvent.CTRL_MASK | ActionEvent.ALT_MASK));
	menu.add(item);
	menu.addSeparator();
	item = new JMenuItem("add parent");
	item.addActionListener(this);
	item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P,ActionEvent.CTRL_MASK));
	menu.add(item);
	item = new JMenuItem("remove parent");
	item.addActionListener(this);
	item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P,ActionEvent.ALT_MASK));
	menu.add(item);
	item = new JMenuItem("remove all parents");
	item.addActionListener(this);
	item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P,ActionEvent.CTRL_MASK | ActionEvent.ALT_MASK));
	menu.add(item);
    }
    
    /**
     * enables/disables the layer painting
     */
    public void setPaintEnabled(boolean p)
    {
	paintElements = p;
    }
    public boolean getPaintEnabled(){return paintElements;}
    /**
     * Adds a child to the layer. A Layer can have several childs.
     * @param int childID - The ID of the child to add.
     */
    public void addChild(int childID) {
	childlayers[childcounter] = childID;
	childcounter ++;
    }
    /**
     * Gets the child array return. No childs have the number -1.
     * @return int[] childlayers. 
     */
    public int[] getChildIDs() {return childlayers;}
    /**
     * Gets the parent ID.
     * @return int parentID
     */
    public int getParentID(){return parentID;}
    /**
     * Sets the parent ID of the layer.
     * @param id - The ID of the Layer.
     */
    public void setParentID(int id) {
	parentID = id;
    }
    /**
     * Returns the layername.
     * @param String layername
     */
    public String getName() {return layername;}
    /**
     * Returns the internal ID of this Layer.
     * @return int ID
     */
    public int getID() {return ID;}
    /**
     * Sets the ID of the layer.
     * @param id - The ID of the Layer.
     */
    public void setID(int id) {ID = id;}
    /**
    * Returns the layertype similar to the defined static constants.
    * @return int layertype;
    */
    public int getLayerType() {return layertype;}
    /**
     * Playes the panel on the parent panel.
     */
    public void setBoundaries(int x,int y,int w,int h) {
	width=w; height=h; posX=x; posY=y;
	setBounds(x,y,w,h);
    }
    /**
     * @return boolean - recursive flag.
     */
    public boolean getRecursive() {return recursive;}
    /**
     * @param boolean recursive.
     */
    public void setRecursive(boolean r) {recursive = r;}
    /**
     * This Function sets the hierarchydeepth.
     * @param int hierarchydeepth
     */
    public void setHierarchyDeepth(int h) {hierarchydeepth = h;}
    /**
     * This Function gives back the Layer Types as String
     * @return String - Layer Type:
     */
    public String getLayerTypeAsString() {
	if (layertype == 1) return "Time Aligned";
	if (layertype == 2) return "Structural";
	if (layertype == 3) return "Featural";
	if (layertype == 4) return "Time Lineal";
	return null;
    }
    /**
     * Draws the default elements of the Layer.
     * @param Graphics2D g - The pen
     */
    private void drawDefaultLayerElements(Graphics2D g) {
	// The color scemes.
	if (layertype == TIMEALIGNED){
	    g.setColor(new Color(75,59,165));
	}
	if (layertype == STRUCTURAL)  {
	    int red   = 78-hierarchydeepth*15;
	    int green = 87-hierarchydeepth*25;
	    int blue  = 139-hierarchydeepth*10;
		if (red<0) red =0;
		if (green<0) green =0;
		if (blue<0) blue =0;
		g.setColor(new Color(red,green,blue));   
	}
	if (recursive == true) {
	    GradientPaint gp = new GradientPaint(0,0,new Color(60,60,120),0,10,new Color(120,120,200),true);
	    g.setPaint(gp);
	}
	// get the boundaries of the viewport
	int viewportsize = (int)root.getOtabWindow().scrollpane.getSize().getWidth();
	int viewposition = (int)root.getOtabWindow().scrollpane.getViewport().getViewPosition().getX();
	int leftborder  = viewposition-1;
	int rightborder = leftborder+viewportsize+2;
	double lefttime = convertPixelToTime(leftborder);
	double righttime = convertPixelToTime(rightborder);

	g.fillRect(leftborder,0,viewportsize,height);	    
	g.setColor(colorblack);
	g.drawRect(leftborder,0,viewportsize,height);	    
	if (recursive == true) 
	    for (int i=0;i<=root.otabwin.layercollectionpanel.maxDepth;i++) {
		int h = i*root.otabwin.layercollectionpanel.LAYERHEIGHT_RECUR;
		g.drawLine(0,h,rightborder,h);
	    }
	g.setComposite(compositealpha4);
	g.setColor(colorwhite);
	g.setFont(new Font("Tahoma",Font.PLAIN,8));
	String text = "LAYER NAME: "+getName()+" LAYER TYPE: "+getLayerTypeAsString();
	if (parentID != -1) 
	    text += " PARENT: "+root.layermanager.getLayerByID(parentID).getName();
	if (childcounter != 0) {
	    text += " CHILDS: ";
	    for (int i=0;i<childcounter;i++) {
		text += i+" "+root.layermanager.getLayerByID(childlayers[i]).getName();
	    }
	}
	if (recursive == true)
	    text += " RECURSIVE: Is Recursive Layer ";
	g.drawString(text,3,height-4);
	// check if there are any annotation elements on the layer
	if (annotationelements.size()==0)
	    return;
	// differ element height between recursive and normal
	int eheight;
	if (recursive == false)
	    eheight = getHeight()-4;
	else
	    eheight = root.otabwin.layercollectionpanel.LAYERHEIGHT_RECUR-4;
	// And now draw the Annotation Elements       
	g.setStroke(new BasicStroke(2.0f));
	// differ the radius and text positions between recursive and normal elements
	int radius = 10;
	int textpos1=13;
	int textpos2=24;
	if (recursive) {
	    radius = 5;
	    textpos1=9;
	    textpos2=17;
	}
	for (int i=0;i<annotationelements.size();i++) {
   	    AnnotationElement ae = (AnnotationElement)annotationelements.get(i);
	    // get the offset for the recursive layers
	    int offset = 0;
	    if (recursive == true) {
		if (ae.nomwriteelement != null)
		    offset = ae.nomwriteelement.getRecursiveDepth()*root.otabwin.layercollectionpanel.LAYERHEIGHT_RECUR;
		else 
		    offset = activedepth*root.otabwin.layercollectionpanel.LAYERHEIGHT_RECUR;
	    }
	    // check for empty elemets
 	    boolean empty = false;
	    if (ae.nomwriteelement == null) empty=true;
	    // Check in the annotation Element is in the viewport of the current ScrollPane
  	    if (ae.begintime>righttime || ae.endtime<lefttime)
  		    continue;
	    // if yes paint the element
	    int x1=convertTimeToPixel(ae.begintime);
  	    int x2=convertTimeToPixel(ae.endtime);
	    if (ae.searchResult != null)
		g.setColor(ae.searchResult);
	    else
		g.setColor(colorwhite);
  	    g.setComposite(compositealpha6);
	    if (empty == true){GradientPaint gp = new GradientPaint(0,0,Color.white,3,3,Color.gray,true);g.setPaint(gp);}
	    g.fillRoundRect(x1,2+offset,x2-x1,eheight,radius,radius);
	    g.setComposite(compositealpha10);
	    if (empty == true){GradientPaint gp = new GradientPaint(0,0,Color.gray,3,3,Color.white,true);g.setPaint(gp);}
	    g.drawRoundRect(x1,2+offset,x2-x1,eheight,radius,radius);
  	    g.setColor(new Color(100,10,40));
	    // look for the nom element and its content
	    if (ae.nomwriteelement != null) {
		if (ae.nomwriteelement.getName() != null)
		    if(empty == false) {
			String name = ae.nomwriteelement.getName();
			if (ae.nomwriteelement.getText() != null)
			    name += " ["+ae.nomwriteelement.getText()+"]";
			java.util.List p = ae.nomwriteelement.getParents();
			if (p != null) name += "["+p.size()+"P]";
			java.util.List c = ae.nomwriteelement.getChildren();
			if (c != null) name += "["+c.size()+"C]";
			g.setComposite(compositealpha10);
			g.drawString(name,x1+5,textpos1+offset);
		    } 
		java.util.List atts = ae.nomwriteelement.getAttributes();
		// go throught the attributes
		if (atts!=null && atts.size()!=0) {
		    String content = new String(); 
 		    for (int itt=0;itt<atts.size();itt++) {
 			NOMWriteAttribute att = (NOMWriteAttribute)atts.get(itt);
 			content += "<"+att.getName()+">"+att.getStringValue();
 		    }
		    g.setComposite(compositealpha10);
	 	    g.setColor(new Color(0,10,40));
		    if(empty == false) g.drawString(content,x1+5,textpos2+offset); 
		}
	    }
	}
	// and finaly the active element
	if (root.layermanager.getActiveLayer()==getID() && activeannotationelement != -1) {
	    int i = activeannotationelement;
	    AnnotationElement ae = (AnnotationElement)annotationelements.get(i);
	    // get the offset for the recursive layers
	    int offset = 0;
	    if (recursive == true) {
		if (ae.nomwriteelement != null)
		    offset = ae.nomwriteelement.getRecursiveDepth()*root.otabwin.layercollectionpanel.LAYERHEIGHT_RECUR;
		else 
		    offset = activedepth*root.otabwin.layercollectionpanel.LAYERHEIGHT_RECUR;
	    }
	    // Check in the annotation Element is in the viewport of the current ScrollPane
 	    if (ae.begintime<righttime && ae.endtime>lefttime)
		{
		    // check for empty elemets
		    boolean empty = false;
		    if (ae.nomwriteelement == null) empty=true;
		    // if yes paint the element
		    int x1=convertTimeToPixel(ae.begintime);
		    int x2=convertTimeToPixel(ae.endtime);
		    g.setComposite(compositealpha10);
		    if (empty == true){GradientPaint gp = new GradientPaint(0,0,Color.red,3,3,Color.black,true);g.setPaint(gp);}
		    if (empty == false){GradientPaint gp = new GradientPaint(0,0,Color.red,3,3,Color.orange,true);g.setPaint(gp);}
		    g.drawRoundRect(x1,2+offset,x2-x1,eheight,radius,radius);
		    // look for the nom element and its content
		    if (ae.nomwriteelement != null) {
			g.setComposite(compositealpha10);
			g.setColor(new Color(100,10,40));
			if (ae.nomwriteelement.getName() != null)
			    if(empty == false) {
				String name = ae.nomwriteelement.getName();
				if (ae.nomwriteelement.getText() != null)
				    name += " ["+ae.nomwriteelement.getText()+"]";
				java.util.List p = ae.nomwriteelement.getParents();
				if (p != null) name += "["+p.size()+"P]";
				java.util.List c = ae.nomwriteelement.getChildren();
				if (c != null) name += "["+c.size()+"C]";
				g.drawString(name,x1+5,textpos1+offset);
			    } 
			// go throught the attributes
			if (ae.nomwriteelement.getAttributes() != null); {
			    java.util.List atts = ae.nomwriteelement.getAttributes();
			    // go throught the attributes
			    if (atts!=null && atts.size()!=0) {
				String content = new String(); 
				for (int itt=0;itt<atts.size();itt++) {
				    NOMWriteAttribute att = (NOMWriteAttribute)atts.get(itt);
				    content += "<"+att.getName()+">"+att.getStringValue();
				}
				g.setComposite(compositealpha10);
				g.setColor(new Color(0,10,40));
				if(empty == false) g.drawString(content,x1+5,textpos2+offset); 
			    }
			}
		    }
		}
	}
    }
    /**
     * Overwritten paint function.
     * @param Graphics g - the pen.
     */
    public void paint(Graphics g1) {
	Graphics2D g = (Graphics2D)g1;
	g.setRenderingHint(RenderingHints.KEY_RENDERING,RenderingHints.VALUE_RENDER_SPEED);
 	if (root.getControlWindow().antialias.isSelected() == true)	    
	    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
	else
	    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_OFF);	
	// first calc the times
	int viewportsize  = (int)root.getOtabWindow().scrollpane.getSize().getWidth();
	int viewposition  = (int)root.getOtabWindow().scrollpane.getViewport().getViewPosition().getX();
	int leftborder    = viewposition-1;
 	int rightborder = leftborder+viewportsize+2;
 	double lefttime  = convertPixelToTime(leftborder);
 	double righttime = convertPixelToTime(rightborder);
 	if (paintElements == false)
 	    {
 		g.setColor(new Color(100,100,100));
 		g.fillRect(leftborder,0,viewportsize,height);	    
 		g.setColor(new Color(120,120,120));
 		g.setFont(new Font("Tahoma",Font.PLAIN,20));
 		for (int i=100;i<rightborder;i+=200)
 		    g.drawString("Disabled",i,24);
 		return;
 	    }
 	switch(layertype) {
 	case TIMEALIGNED:
	    drawDefaultLayerElements(g);
 	    break;
 	case STRUCTURAL:
 	    drawDefaultLayerElements(g);
 	    break;
 	case FEATURAL:
 	    break;
 	case TIMELINEAL:	    
 	    // the draw
  	    double durationinseconds = root.layermanager.getVideoDuration();
	    GradientPaint  gp = new GradientPaint(0.0f,0.0f,new Color(230,163,4),(float)((float)width/durationinseconds/2.0f),0.0f,new Color(190,123,30),true);
	    g.setPaint(gp);
	    //g.setColor(new Color(230,163,4));
  	    g.fillRect(leftborder,0,viewportsize,height);	    
 	    double framespersecond   = 30.0;
 	    g.setColor(colorblack);
 	    g.setFont(new Font("Tahoma", Font.PLAIN, 11));
	    // Fist the time StampsMain
	    for (double t=0.0;t<durationinseconds;t=t+1.0) {
		if (lefttime>t || righttime<t)
		    continue;
 		// Draw the second lines
 		int x = (int)((double)width/durationinseconds*t);
 		g.drawLine(x,height/2,x,height);
 		// Draw the time text
 		if ((double)width/durationinseconds > 30.0) {
		    if (root.config.secoundsInTimelineal == false)
			{
			    String time = new String("");
			    int minutes  = (int)(t/60.0);		
			    if (minutes < 10)
				time += "0" + minutes + ":"; 
			    else 
				time += minutes + ":"; 
			    int secounds = (int)(t%60);
			    if (secounds < 10)
				time += "0" + secounds; 
			    else 
				time += secounds; 
			    g.drawString(time,x-16,height/2-4);
			}
		    else
			{
			    String time = new String(""+t+"sec");
			    g.drawString(time,x-16,height/2-4);
			}
		}
	    }		
	    // And the the frame stamps
 	    if ((double)width/durationinseconds/framespersecond > 4) 
 		for (double t=0.0;t<durationinseconds;t=t+(1.0/framespersecond)) {
		    if (lefttime>t || righttime<t)
			continue;
		    int x = (int)((double)width/durationinseconds*t);
		    g.drawLine(x,height*3/4,x,height);
		}
	    // The selection area
 	    if (root.layermanager.timelinealstarttime != 0.0) {
 		double start = 0.0;
 		double end=0.0;
 		if (root.layermanager.timelinealstarttime < root.layermanager.timelinealendtime) {
 		    start = root.layermanager.timelinealstarttime;
 		    end   = root.layermanager.timelinealendtime;
 		}
 		else {
 		    start = root.layermanager.timelinealendtime;
 		    end   = root.layermanager.timelinealstarttime;
 		}
 		int x1 = convertTimeToPixel(start);
 		int x2 = convertTimeToPixel(end);
 		g.setColor(new Color(0,0,255));
 		g.drawLine(x1,0,x1,getHeight());
 		g.drawLine(x2,0,x2,getHeight());
  		g.setComposite(compositealpha3);
  		g.fillRect(x1,0,x2-x1,getHeight());;
 		// And the numbers
  		g.setComposite(compositealpha5);
 		g.setFont(new Font("Tahoma",0,7));
 		g.drawString(convertTimeToString(start),x1-29,18);
 		g.drawString(convertTimeToString(end),x2+4,18);
 		g.drawString("(SPAN: "+convertTimeToString(end-start)+")",x2+38,18);

  	    }
 	    // The current time line
 	    int x = convertTimeToPixel(root.layermanager.timelinealcurrenttime);
  	    g.setComposite(compositealpha4);
  	    g.setStroke(new BasicStroke(20.0f));
  	    g.setColor(colorred);
  	    g.drawLine(x,0,x,getHeight());
  	    g.setComposite(compositealpha10);
  	    g.setStroke(new BasicStroke(1.0f));
  	    g.setColor(colorblack);
  	    g.drawLine(x,0,x,getHeight());
	    // on the fly annotation box
	    if (root.otf.getOtfState()==true && root.otf.getPlayerState())
		{
		    g.setComposite(compositealpha6);
		    int xotf = convertTimeToPixel(root.otf.getOtfTime());
		    gp = new GradientPaint(0,0,Color.black,3,3,Color.gray,true);
		    g.setPaint(gp);
		    g.fillRect(xotf,0,x-xotf,getHeight());
		}
  	    // The time in numbers
  	    g.setFont(new Font("Tahoma",0,7));
  	    g.drawString(convertTimeToString(root.layermanager.timelinealcurrenttime),x+5,7);
	    break;
	case OSZILLOSKOP:
	    width = convertTimeToPixel(duration);
	    // Need to allocate and paint an new image 
	    if (oscaimage == null || oscaimage.getWidth()!=width) {
		root.printDebugMessage("Recalculate OSZILLOSKOP Image.");
		Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
		root.statusbar.updateStatusText("Recalculate oscilloscope Image.");
		oscaimage = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
		Graphics2D gi = (Graphics2D)oscaimage.getGraphics();
		if (root.getControlWindow().antialias.isSelected() == true)	    
		    gi.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
		else
		    gi.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_OFF);	
		// The Background
		gi.setColor(new Color(0,30,0));
		gi.fillRect(0,0,width,height);	    
		// The Grids
		gi.setColor(new Color(55,70,40));
		for (int xl=0;xl<(int)duration;xl++) 
		    gi.drawLine(convertTimeToPixel(xl),0,convertTimeToPixel(xl),height);
		for (int yl=0;yl<height;yl+=5)
		    gi.drawLine(0,yl,width,yl);
		gi.setColor(colorred);
		gi.drawLine(0,height/2,width,height/2);
		// No paint the oszilloskop
		gi.setColor(new Color(215,255,30));
		int ya,xo=0,yo=height/2,arraypos;
		double arraysize = (double)samples.length;
		int y1,y2,y1o=0,y2o=0;
		for (int xa=0;xa<width;xa++) {
		    arraypos = (int)((double)xa/width*arraysize);
		    y1 = (int)((double)(Math.abs(samples[arraypos])+127.0f)/128.0*height/2.0);
		    y2 = height-y1;
		    gi.setComposite(compositealpha5);
		    gi.drawLine(xa,y1,xa,y2);
		    gi.setComposite(compositealpha7);
		    gi.drawLine(xa-1,y1o,xa,y1);
		    gi.drawLine(xa-1,y2o,xa,y2);
		    y1o=y1;y2o=y2;
 		    root.statusbar.updateStatusValue(100);
 		    root.statusbar.updateStatusText("Oszillocope image calculated.");
		}

		// the information string
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
		String text = "NAME: "+getName()+"   DURATION: "+time+"   SAMPLES: "+samples.length;
		gi.setComposite(compositealpha7);
		gi.setFont(new Font("Tahoma",Font.PLAIN,7));
		gi.drawString(text,width-350,10);
		Thread.currentThread().setPriority(Thread.NORM_PRIORITY);
	    }
	    // paint the image
	    g.drawImage(oscaimage,0,0,this);
	    break;
	case SPECTRAL:
	    width = convertTimeToPixel(duration);
	    // Need to allocate and paint an new image 
	    if (specimage == null || specimage.getWidth()!=width) {
		root.printDebugMessage("Recalculate SPECTOGRAM Image.");
		Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
 		root.statusbar.updateStatusValue(100);
 		root.statusbar.updateStatusText("Recalculate spectrum image.");
		specimage = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
		Graphics2D gi = (Graphics2D)specimage.getGraphics();
		if (root.getControlWindow().antialias.isSelected() == true)	    
		    gi.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
		else
		    gi.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_OFF);	
		double stepx = (double)width/(double)spectrum.length;
		double stepy = (double)height/64.0;
		int color,posx,posy,lenx,leny;
		lenx = (int)(stepx+1.0);
		leny = (int)(stepy+1.0);
		// Get the Color array.
		de.dfki.ami.amigram.multimedia.GreenColorArray colorgenerator = new de.dfki.ami.amigram.multimedia.GreenColorArray();
		Color[] colors = colorgenerator.green;
		gi.setComposite(compositealpha10);
		for (int xr=0;xr<spectrum.length;xr++) {
		    for (int yr=0;yr<64;yr++) {
			color = (int)(spectrum[xr][yr]*255.0);
			if (color>255) color=255;
			if (color<0) color=0;
			gi.setColor(colors[color]);
			posx = (int)((double)stepx*(double)xr);
			posy = height-(int)((double)stepy*(double)yr);
			gi.fillRect(posx,posy,lenx,leny);
		    }
		}
		// The Grids
		gi.setComposite(compositealpha3);
		gi.setColor(colors[180]);
		for (int xl=0;xl<(int)duration;xl++) 
		    gi.drawLine(convertTimeToPixel(xl),0,convertTimeToPixel(xl),height);
		for (int yl=0;yl<height;yl+=8)
		    gi.drawLine(0,yl,width,yl);
 		root.statusbar.updateStatusValue(100);
 		root.statusbar.updateStatusText("Spectrum Image calculated.");
		// the information string
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
		time += "  (min/sec/millisec)";
		String text = "NAME: "+getName()+"   DURATION: "+time+"   Windows: "+spectrum.length;
		gi.setComposite(compositealpha7);
		gi.setFont(new Font("Tahoma",Font.PLAIN,7));
		gi.setColor(new Color(215,255,30));
		gi.drawString(text,width-350,10);
		Thread.currentThread().setPriority(Thread.NORM_PRIORITY);
	    }
	    // Draw the image on the layer
	    g.drawImage(specimage,0,0,this);
	    break;
	case MAX_CHILDS:
	    break;
	}
	// draw the shaddow over active layer
	if (root.layermanager.getActiveLayer() == getID())
	    {
		g.setColor(new Color (255,60,0));
		g.setComposite(compositealpha2);
 		if (recursive == false) {
		    g.fillRect(leftborder,0,viewportsize,height);
		}
 		else {
		    int height = root.otabwin.layercollectionpanel.LAYERHEIGHT_RECUR;
		    g.fillRect(leftborder,height*activedepth,viewportsize,height);
 		}
	    }	    
    }
    /**
     * Converts double times to time Strings
     * @param double time in seconds.
     * @return String timestring.
     */
    private String convertTimeToString (double time) {
	String timestring = new String("");
	if (root.config.secoundsInTimelineal == false)
	    {
		int intminutes = (int)time/60;
		if (intminutes<10) timestring += "0"+intminutes+":";
		else timestring += intminutes+":";
		int intseconds = (int)time%60;
		if (intseconds<10) timestring += "0"+intseconds+":";
		else timestring += intseconds+":";
		double centiseconds = time - (int)time;
		int intcentiseconds = (int)(centiseconds*100.0);
		if (intcentiseconds<10) timestring += "0"+intcentiseconds;
		else timestring += intcentiseconds;
	    }
	else
	    {
		int intseconds = (int)time;
		timestring += intseconds+":";
		double centiseconds = time - (int)time;
		int intcentiseconds = (int)(centiseconds*100.0);
		if (intcentiseconds<10) timestring += "0"+intcentiseconds;
		else timestring += intcentiseconds;
	    }
	return timestring;
    }

    /**
     * Implements the MouseListener.
     * When mouse is double clicked.
     * @param MouseEvent e
     */
    public void mouseClicked(MouseEvent e) {
// 	int count = e.getClickCount();
// 	System.out.println(count);
// 	root.layermanager.setActiveLayer(getID());
// 	root.getOtabWindow().repaint();
// 	root.statusbar.updateStatusText("Layer selected. Name:" + layername);
// 	//Check if there is clicked on an track element. When yes then play it the player.
// 	double time = convertPixelToTime(e.getX());
// 	int active = getAnnoElementForTime(time);
//  	int ex = e.getX();
//  	int ey = e.getY();
// 	//Play the track element
// 	AnnotationElement ae = (AnnotationElement) annotationelements.get(active);
//  	double begintime  = ae.begintime;
//  	double endtime = ae.endtime;
//  	int beginpixel = convertTimeToPixel(begintime);
//    // TODO TODO TODO TODO TODO TODO TODO TODO TODO TODO TODO TODO 
//    // TODO TODO TODO TODO TODO TODO TODO TODO TODO TODO TODO TODO 
//  	if (root.getVideoWindow().player != null) {
//  	    root.statusbar.updateStatusText("Play GVM track element.");	
//  	    root.getVideoWindow().player.playTimeSpan(new javax.media.Time(begintime),new javax.media.Time(endtime));
// 	}
    }
    /**
     * Implements the MouseListener.
     * When mouse entered this panel
     * @param MouseEvent e
     */
    public void mouseEntered(MouseEvent e) {
    }
    /**
     * Implements the MouseListener.
     * When mouse exited this panel.
     * @param MouseEvent e
     */
    public void mouseExited(MouseEvent e) {
    }
    /**
     * Implements the MouseListener.
     * When one click is done. (press + release)
     * @param MouseEvent e
     */
    public void mousePressed(MouseEvent e) {
	// counts the MouseClicks
	clickcount = e.getClickCount();
     	root.printDebugMessage("MOUSE PRESSED");
	double time = convertPixelToTime(e.getX());
	root.layermanager.timelinealcurrenttime = time;
	// Nothing else if it is an time lineal layer
	if (getLayerType() == TIMELINEAL) {
	    root.layermanager.timelinealcurrenttime = time;
	    if (root.getControlWindow().synctimeline.isSelected()==true)
		root.synchronizer.sendTimeChange("OTAB",new javax.media.Time(time));
	    root.layermanager.getLayerByID(0).repaint();
	    return;
	}
	root.layermanager.setActiveLayer(getID());
	root.getOtabWindow().repaint();
	root.layermanager.timelinealstarttime = time;
	// get the depth for recursive layers
	if (recursive == true)
	    activedepth = e.getY()/root.otabwin.layercollectionpanel.LAYERHEIGHT_RECUR;
	// If the mouse is pressed on an annotation element
	int active = getAnnoElementForTime(time,activedepth);
	if (active == -1)
	    lastMouseDownOverAnnoElement = false;
	else {
	    // check for multiple underlaying elements
	    if (time==lastTime) {
		int alternateActive = getAlternateAnnoElementForTime(time,activedepth);
		if (alternateActive != -1 && e.getButton()!=MouseEvent.BUTTON3)
		    active = alternateActive;
	    }
	    lastTime = time;
	    root.statusbar.updateStatusText("Track element selected.");	     
	    lastMouseDownOverAnnoElement = true;
	    root.printDebugMessage("AnnoElement selected");
	    activeannotationelement = active;
	    // The timelineal too
	    AnnotationElement ae = (AnnotationElement) annotationelements.get(activeannotationelement);
	    ////////////////////////////////////////////////////////////////////////////////////////////////
	    if (ae.nomwriteelement == null)
		{
		    java.util.List layerelements = nitelayer.getContentElements(); // all possible metadata elements
		    NOMWriteAnnotation ne = null;
		    try{
			NElement nel = (NElement)layerelements.get(0); // set as default to the first possible element
			String observation = (String)root.ctrlwin.observations.getSelectedItem();
			String agent = (String)root.ctrlwin.agents.getSelectedItem();
			ne = new NOMWriteAnnotation(root.nomcommunicator.nomwritecorpus,nel.getName(),observation,agent);
			// Differ between the layer types
			if (layertype == TIMEALIGNED) {
			    root.printDebugMessage(" add element to time aligned layer.");
			    ne.setStartTime(ae.begintime);
			    ne.setEndTime(ae.endtime);
			}
			else if (layertype == STRUCTURAL) {
			    root.printDebugMessage(" add element to structural layer.");
			    AnnotationLayer parentLayer = root.layermanager.getLayerByID(getParentID());
			    Vector aes = parentLayer.getAnnoElementsForTimeSpan(ae.begintime,ae.endtime);
			    root.printDebugMessage("Found "+aes.size()+" Parents");
  			    for (int i=0;i<aes.size();i++) {
   				AnnotationElement ane = (AnnotationElement)aes.get(i);
  				if (ane.nomwriteelement == null) {
  				    root.printDebugMessage("ERROR Unannotated Segment in parent.");
  				    JOptionPane.showMessageDialog(root,"Can not build stuructural element for segmented\n(unannotated) childs. Annotate the child first.","ERROR",3);
  				    return;
				}
				ne.addChild(ane.nomwriteelement);
  			    }
			    // add parend links to recursive for all layers with a smaler depth
			    // i will add to the first element i found in the upper depth
			    boolean rebuildPanel = false;
			    if (recursive == true) {
				// go reverse throught the depth
				//for (int de=root.otabwin.layercollectionpanel.maxDepth;d<=0;d--) {
				    aes = getAnnoElementsForTimeSpan(ae.begintime,ae.endtime);
				    for (int i=0;i<aes.size();i++) {
					AnnotationElement ane = (AnnotationElement)aes.get(i);
					if (ane.nomwriteelement != null) {
					    //if (ane.nomwriteelement.getRecursiveDepth() < activedepth) 
						{
						    ane.nomwriteelement.addChild(ne);
						    // chek for the max depth -> rebuild and restore position
						    if (root.otabwin.layercollectionpanel.maxDepth <= ne.getRecursiveDepth()) {
							rebuildPanel = true;
						    }
						}
					}
				    }
				    //}
			    }
			    if (rebuildPanel == true)
				rebuidOtabWithPosition();
			}
			else {
			    root.printDebugMessage(" Can't add element on unknown layer type.");
			    JOptionPane.showMessageDialog(root,"Can't add element on unknown Layer type.","ERROR",3);
			}
			ne.setText("New Amigram Element");
			ne.addToCorpus(); 
		    }
		    catch(Exception exception) {
			root.printDebugMessage("--- ERROR --- while instantiate the NOM element\n");
			exception.printStackTrace();
			JOptionPane.showMessageDialog(root,"ERROR WHILE CREATE THE NOM ELEMENT","ERROR",3);
		    }
		    ae.nomwriteelement = ne;
		    
		    
		    // CALL THE PLUGIN
		    
		    if (clickcount == 2){
			root.pluginManager.emptyElementDoubleClicked(ae);
		    }
		     else {
 			root.pluginManager.emptyElementSelected(ae);
 		    }
		    // add it to the hashmap
		    root.nomcommunicator.annohashmap.put(ne,ae);
		}
	    else 
		// CALL THE PLUGIN

		
		
		if (clickcount == 2) {
		    
		    java.util.List contentelementlist = nitelayer.getContentElements();
  		    NiteElement nelement = (NiteElement)contentelementlist.get(0);
 		    java.util.List attributeList = nelement.getAttributes();
		    
		    if(root.pluginManager.invokePlugin(attributeList))
			{
			    root.pluginManager.elementDoubleClicked(ae);
			}
			
		}
		 else {
		     java.util.List contentelementlist = nitelayer.getContentElements();
		     NiteElement nelement = (NiteElement)contentelementlist.get(0);
		     java.util.List attributeList = nelement.getAttributes();
		    
		     if(root.pluginManager.invokePlugin(attributeList))
			{
			    root.pluginManager.elementSelected(ae);
			}

		     
		 }
		
	    if (root.addChild == false && root.removeChild == false && root.addParent == false && root.removeParent == true && ae != null)
 		    root.pluginManager.elementSelected(ae);
	    //////////////////////////////////////////////////////////////////////////////////////////////
	    if (root.addChild == true)
		{
		    // check if there was an parent/child assigment event
		    boolean parentFound = checkParentRelation(root.lastae.nomwriteelement,ae.nomwriteelement);
		    // add the child
		    if (parentFound == false)
			{
			    try {
				root.lastae.nomwriteelement.addChild(ae.nomwriteelement);
			    } catch(Exception ex) {
				JOptionPane.showMessageDialog(root,"ERROR while add the child..","ERROR",2);
				root.statusbar.updateStatusText("ERROR add child !!!");
				ex.printStackTrace();
			    }
			    root.statusbar.updateStatusText("Child added !");
			    if (recursive==true)
				validateLayerDepth(ae.nomwriteelement);
			}
		    // can not add child to child
		    else
			{
			    JOptionPane.showMessageDialog(root,"There is a dominance relation between this\nparent and the child. Can not add a child to \na child from the parent.","Dominance Relation !",2);
			    root.statusbar.updateStatusText("Can not add child to child !");
				
			}
		    // reactivate the element
		    root.layermanager.setActiveLayer(root.lastActiveLayer);
		    if (root.lastActiveLayer == ID)
			activeannotationelement = root.lastActiveAnnoElement;
		    root.setCursorForAllWidgets(0);
		}
	    if (root.removeChild == true)
		{
		    try {
			root.lastae.nomwriteelement.removeChild(ae.nomwriteelement);
			root.statusbar.updateStatusText("child removed !");
		    } catch (Exception ex) {
			root.statusbar.updateStatusText("ERROR while remove child !");
			JOptionPane.showMessageDialog(root,"Can't remove child.","WARNING",2);
			ex.printStackTrace();
		    }
		    // reactivate the element
		    root.layermanager.setActiveLayer(root.lastActiveLayer);
		    if (root.lastActiveLayer == ID)
			activeannotationelement = root.lastActiveAnnoElement;
		    root.setCursorForAllWidgets(0);
		}
	    if (root.addParent == true)
		{
		    // check if there was an parent/child assigment event
		    boolean childFound = checkParentRelation(ae.nomwriteelement,root.lastae.nomwriteelement);
		    // add the child
		    if (childFound == false)
			{
			    try {
				ae.nomwriteelement.addChild(root.lastae.nomwriteelement);
			    } catch(Exception ex) {
				JOptionPane.showMessageDialog(root,"ERROR while add the parent..","ERROR",2);
				root.statusbar.updateStatusText("ERROR add parent !!!");
				ex.printStackTrace();
			    }
			    root.statusbar.updateStatusText("parent added !");
			    if (recursive==true)
				validateLayerDepth(ae.nomwriteelement);
			}
		    // can not add child to child
		    else
			{
			    JOptionPane.showMessageDialog(root,"There is a dominance relation between this\nchild and the parent. Can not add a parent to \na parent from the child.","Dominance Relation !",2);
			    root.statusbar.updateStatusText("Can not add parent to parent !");
				
			}
		    // reactivate the element
		    root.layermanager.setActiveLayer(root.lastActiveLayer);
		    if (root.lastActiveLayer == ID)
			activeannotationelement = root.lastActiveAnnoElement;
		    root.setCursorForAllWidgets(0);
		}
	    if (root.removeParent == true)
		{
		    try {
			ae.nomwriteelement.removeChild(root.lastae.nomwriteelement);
			root.statusbar.updateStatusText("Parent removed !");
		    } catch (Exception ex) {
			root.statusbar.updateStatusText("ERROR while remove parent !");
			JOptionPane.showMessageDialog(root,"Can't remove parent.","WARNING",2);
			ex.printStackTrace();
		    }
		    // reactivate the element
		    root.layermanager.setActiveLayer(root.lastActiveLayer);
		    if (root.lastActiveLayer == ID)
			activeannotationelement = root.lastActiveAnnoElement;
		    root.setCursorForAllWidgets(0);
		}
	    root.lastae = ae;
	    root.lastActiveLayer = ID;
	    root.lastActiveAnnoElement = active;
	    root.removeChild = false; root.addChild = false;
	    root.removeParent = false; root.addParent = false;
	    this.repaint();
	    // update calls for the Annotation Table in the control dialog.
	    root.getControlWindow().annotable.updateAnnotable();
	    root.layermanager.timelinealstarttime = ae.begintime;
	    root.layermanager.timelinealendtime   = ae.endtime;
	    root.layermanager.getLayerByID(0).repaint();
	    // Bring the tab in the focus
	    root.getControlWindow().tabbedpane.setSelectedComponent(root.getControlWindow().annotable);
	}
    }

    /**
     * Check recursively if any child from the element is deeper than the max depth
     * and rebuild the LayerCollection.
     * @param NOMWriteElement element
     */
    private boolean  validateLayerDepth(NOMElement element) {
	int maxDepth = root.otabwin.layercollectionpanel.maxDepth;
	// first check the element itself
	if (maxDepth <= element.getRecursiveDepth())
	    {
		rebuidOtabWithPosition();
		return true;
	    }
	// then check the children from this element
	java.util.List children = element.getChildren();
	if (children == null)
	    return false;
	for (int i=0;i<children.size();i++) {
	    NOMElement child = (NOMElement)children.get(i);
	    if (maxDepth <= child.getRecursiveDepth())
		{
		    rebuidOtabWithPosition();
		    return true;
		}
	    // check recursive for the children from the child
	    if (validateLayerDepth(child)==true)
		return true;
	}
	return false;
    }

    private void rebuidOtabWithPosition() {
	int viewposition  = (int)root.getOtabWindow().scrollpane.getViewport().getViewPosition().getX();
	double viewtime = convertPixelToTime(viewposition);
	root.getOtabWindow().buildAnnotationBoardPanel();
	int newviewposition = convertTimeToPixel(viewtime);
	root.getOtabWindow().scrollpane.getViewport().setViewPosition(new Point(newviewposition,0));
	root.config.zoom = root.ctrlwin.zoomslider.getValue();
    }

    /**
     * Check recursively for child relations between the parent and the new child
     * @param NOMWriteElement parent 
     * @child NOMWriteElemention parent 
     * @return boolean parentFound
     */
    private boolean checkParentRelation(NOMElement parent, NOMElement child) {
	String id0 = child.getID();
	String id1 = parent.getID();
	java.util.List parents = parent.getParents();
	if (parents == null) 
	    return false;
	for (int i=0;i<parents.size();i++) {
	    String id2 = ((NOMElement)parents.get(i)).getID();
	    // check for self parents
	    if (id1.equals(id2))
		continue;
	    // the recursive call
	    if (id0.equals(id2) || checkChildRelation((NOMElement)parents.get(i),child)==true)
		return true;
	}
	return false;
    }
    /**
     * Check recursively for parent relations between the parent and the new child
     * @param NOMWriteElement parent 
     * @child NOMWriteElemention parent 
     * @return boolean parentFound
     */
    private boolean checkChildRelation(NOMElement parent, NOMElement child) {
	String id0 = child.getID();
	String id1 = parent.getID();
	java.util.List parents = parent.getParents();
	if (parents == null) 
	    return false;
	for (int i=0;i<parents.size();i++) {
	    String id2 = ((NOMElement)parents.get(i)).getID();
	    // check for self parents
	    if (id1.equals(id2))
		continue;
	    // the recursive call
	    if (id0.equals(id2) || checkChildRelation((NOMElement)parents.get(i),child)==true)
		return true;
	}
	return false;
    }

    /**
     * Implements the MouseListener.
     * When button is released.
     * @param MouseEvent e
     */
    public void mouseReleased(MouseEvent e) {
	if (e.getButton() == MouseEvent.BUTTON3)
	    if (lastMouseDownOverAnnoElement == true)
		menu.show(e.getComponent(),e.getX(),e.getY());
    }
    /**
     * Implements the MouseMotionListener.
     * Moving with pressed mouse button
     * @param MouseEvent e
     */
    public void mouseDragged (MouseEvent e) {
	if (getLayerType() == TIMELINEAL) {
	    double time = convertPixelToTime(e.getX());
	    root.layermanager.timelinealcurrenttime = time;
	    if (root.getControlWindow().synctimeline.isSelected()==true)
		root.synchronizer.sendTimeChange("OTAB",new javax.media.Time(time));
	    root.layermanager.getLayerByID(0).repaint();

	}
	if (getLayerType()==STRUCTURAL || getLayerType()==TIMEALIGNED) {
	    double time = convertPixelToTime(e.getX());
	    root.layermanager.timelinealendtime = time;
	    root.layermanager.timelinealcurrenttime = time;
	    if (root.getControlWindow().synctimeline.isSelected()==true)
		root.synchronizer.sendTimeChange("OTAB",new javax.media.Time(time));
	    root.layermanager.getLayerByID(0).repaint();
	}
    }
    /**
     * Implements the MouseMotionListener. Moving without pressed mouse button.
     * In this function the toolTipText is set.
     * @param MouseEvent e
     */
    public void mouseMoved(MouseEvent e) {
	// get the depth for recursive layers
	int depth = 0;
	if (recursive == true)
	    depth = e.getY()/root.otabwin.layercollectionpanel.LAYERHEIGHT_RECUR;
	int element = getAnnoElementForTime(convertPixelToTime(e.getX()),depth);
	if (element == -1)
	    {

		setToolTipText(null);
			
		
		return;
	    }
	if (element==lastElement) {
	    return;
	}
	lastElement = element;
	AnnotationElement ae = (AnnotationElement)annotationelements.get(element);
	if (ae==null || root.addChild==true || root.removeChild==true)
	    {
			
		setToolTipText(null);
		
		return;
	    }
	NOMWriteElement nwe = ae.nomwriteelement;
	if (nwe == null)
	    {
			if (menubar.isToolTipSelected()){
				setToolTipText("Empty Segment");
			} else {
				setToolTipText(null);
			}
		return;
	    }
	String text = "<html>";
	text += "Element Name: <b><font color=blue size=+1>"+nwe.getName()+"</font></b><br>";
	if (nwe.getText() != null) {
	    text += "Content: <b><i><u><font color=blue size=+1>"+nwe.getText()+"</u></i></b></font><br>";
	}
	text += "<font color=\"666666\" size=-3>";
	text += "Start Time: <b>"+ae.begintime+"</b> secs. , ";
	text += "End Time: <b>"+ae.endtime+"</b> secs.<br>";
	text += "</font>";
	if (recursive == true) {
	    text += "Depth: "+"<b>"+ae.nomwriteelement.getRecursiveDepth()+"</b><br>";
	}
	text += "Attributes: ";
	java.util.List atts = ae.nomwriteelement.getAttributes();
	if (atts!=null && atts.size()!=0) {
	    for (int itt=0;itt<atts.size();itt++) {
		NOMWriteAttribute att = (NOMWriteAttribute)atts.get(itt);
		text += "<font color=green>&lt;"+att.getName()+"&gt;</font><b><font color=blue>"+att.getStringValue()+"</font></b>";
	    }
	}
	text += "<br>";
	text += "<font size=-2>";
	if (nwe.getChildren() != null) {
	    text += "Childs: <b>"+nwe.getChildren().size()+"</b> , ";
	}
	if (nwe.getParents() != null) {
	    text += "Parents: <b>"+nwe.getParents().size()+"</b><br>";
	}
	if (nwe.getObservation() != null) {
	    text += "Observation: <b>"+nwe.getObservation()+"</b><br>";
	}
	if (nwe.getAgentName() != null) {
	    text += "Agent: <b>"+nwe.getAgentName()+"</b><br>";
	}
	if (nitelayer != null) {
	    NCoding coding = (NCoding)nitelayer.getContainer();
	    if (coding != null) {
		if (nwe.getParents() != null) {
		    text += "Coding: <b>"+coding.getName()+"</b>";
		}
	    }
	}
	text += "</font>";
	text += "</html>";
	// finaly set the position of the tooltiptext
	if (menubar.isToolTipSelected()){
		setToolTipText(text);
	} else {
		setToolTipText(null);
	}
	
	java.awt.geom.Point2D.Float point = root.otabwin.layercollectionpanel.getMiddleCoordinatesForAnnoElement(ae);
	int tx = (int)point.getX();
	int ty = 0;
	if (recursive == false)
	    ty+=root.otabwin.layercollectionpanel.LAYERHEIGHT/2.0f;
	else {
	    int rheigth = root.otabwin.layercollectionpanel.LAYERHEIGHT_RECUR;
	    ty+=(int)(depth*rheigth+rheigth/2);
	}
	toolTipPoint = new Point(tx,ty);
    }
    /**
     * Overwritten function for the tool tip location
     /*/
    public Point getToolTipLocation(MouseEvent event) {
            return toolTipPoint;
        }

    /**
     * Helper function for unit conversion.
     * @param double time - The time.
     * @return int - The pixels.
     */
    public int convertTimeToPixel(double time) {
	return (int)(time/root.layermanager.getVideoDuration()*(double)(getWidth()));
    }
    /**
     * Helper function for unit conversion.
     * @param int pixel.
     * @return double time.
     */
    private double convertPixelToTime(int pixel){
	return (double)pixel/(double)getWidth()*root.layermanager.getVideoDuration();
    }

    /********************************************************
     * This adds an annotation layer to the layer vector list
     ********************************************************/
    public void addAnnotationLayerElement() {
	double begin = root.layermanager.timelinealstarttime;
	double end   = root.layermanager.timelinealendtime;
	if (begin>end) {
	    double tmp = begin;
	    begin = end;
	    end = tmp;
	}
	if (end == 0.0) {
	    JOptionPane.showMessageDialog(root,"Please select a time span first","Info",1);
	    return;
	}
	// Check if the end time is not longer than the video
	if (end > root.layermanager.getVideoDuration())
	    end = root.layermanager.getVideoDuration();
	// If SRUCTURAL layer check for overlaying times
	if (getLayerType() == STRUCTURAL) {
	    startAndEndTimes saetime = getStartAndEntTimeForOverlayingLayers(new startAndEndTimes(begin,end));
	    if (saetime == null) {
		JOptionPane.showMessageDialog(root,"No other elements over this structural layer.","Not Possible",0);
		return;
	    }
	    else {
		begin = saetime.begin;
		end = saetime.end;
	    }
	    root.statusbar.updateStatusText("Element added.");
        }    
	// For the TIMEALIGNED layers
	else {
	    // Check if there is no other annotation element in the selected time span
	// get the depth for recursive layers
	    int beginelement = getAnnoElementForTime(begin,0);
	    int endelement = getAnnoElementForTime(end,0);
	    if (beginelement != -1 && endelement != -1) {
		AnnotationElement beginae = (AnnotationElement)annotationelements.get(beginelement);
		AnnotationElement endae = (AnnotationElement)annotationelements.get(endelement);
		if (beginae.endtime==endae.begintime) {
		    JOptionPane.showMessageDialog(root,"This new track element is between two\nother connected track elements.","Not Possible",0);
		    return;
		}
		else {
		    Object[] options = {"Yes, adapt","Remove Element"};
		    int n = JOptionPane.showOptionDialog(root,"The begin and end time of this new track element\nintersects two other track elements. Should we adapt\nthe begin and end time of this two elements to\nthe begin and end times of the other track elements or do you want to\nremove the new track element ?","Stretch Track Element Begin/End Times ?",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE,null,options,options[0]); // Yes = 0, No = 1
		    if (n==1)
			return;
		    begin = beginae.endtime;
		    end   = endae.begintime;
		    root.layermanager.timelinealendtime = end;
		    root.layermanager.timelinealstarttime = begin;
		}
	    }
	    else {
		if (beginelement != -1) {
		    Object[] options = {"Yes, adapt","Remove Element"};
		    int n = JOptionPane.showOptionDialog(root,"The begin time of this new track element\nintersects an other track element. Should we adapt\nthe begin time of this element to the end\ntime of the other track element or do you want to\nremove the new track element ?","Stretch Track Element Begin Time ?",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE,null,options,options[0]); // Yes = 0, No = 1
		    if (n==1)
			return;
		    AnnotationElement beginae = (AnnotationElement)annotationelements.get(beginelement);
		    begin = beginae.endtime;
		    root.layermanager.timelinealendtime = end;
		    root.layermanager.timelinealstarttime = begin;
		}
		if (endelement != -1) {
		    Object[] options = {"Yes, adapt","Remove Element"};
		    int n = JOptionPane.showOptionDialog(root,"The end time of this new track element\nintersects an other track element. Should we adapt\nthe end time of this element to the begin\ntime of the other track element or do you want to\nremove the new track element ?","Stretch Track Element End Time ?",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE,null,options,options[0]); // Yes = 0, No = 1
		    if (n==1)
			return;
		    AnnotationElement endae = (AnnotationElement)annotationelements.get(endelement);
		    end = endae.begintime;
		    root.layermanager.timelinealendtime = end;
		    root.layermanager.timelinealstarttime = begin;
		}
	    }
	    root.statusbar.updateStatusText("Element added.");
	} 
	// End of the routine for the time aligned layers
	AnnotationElement ae = new AnnotationElement((float)begin,(float)end,this);
	annotationelements.add(ae);
	java.util.List layerelements = nitelayer.getContentElements();
//	layerelements.add(ne);
	activeannotationelement = annotationelements.size()-1;
	sortAnnotationElemets();
	repaint();
    }
    /**
     * This function looks for all elements between the two times.
     * @param double start
     * @param double end
     * @return Vector aes - all found elements.
     */
    public Vector getAnnoElementsForTimeSpan(double start, double end) {
	Vector aes = new Vector();
	for (int i=0;i<annotationelements.size();i++) {
	    AnnotationElement ae = (AnnotationElement)annotationelements.get(i);
	    if (ae.isAnnoElementBetweenThisTimes(start,end) == true) aes.add(ae);; 
	}
	return aes;
    }
  
    /**
     * This function checks if any annoelement is in the given time.
     * @param double time
     * @param int depth - only nedded for recursive layers. -1 looks on the activedepth
     * @return int index - the index of the annotation element that is in the time, otherwise -1 if no one is found.
     */
    public int getAnnoElementForTime(double time, int depth) {
	if (depth == -1) depth = activedepth;
	for (int i=0;i<annotationelements.size();i++) {
	    AnnotationElement ae = (AnnotationElement)annotationelements.get(i);
	    if (ae.isAnnoElementInThisTime(time) == true) {
		if (recursive == false) {
		    lastActiveElements.set(i);
		    return i; 
		}
		else {
		    if (ae.nomwriteelement == null) {
			lastActiveElements.set(i);
			return i; 
		    }
		    if (ae.nomwriteelement.getRecursiveDepth() == depth) {
			lastActiveElements.set(i);
			return i; 
		    }
		}
	    }
	}
	lastActiveElements.clear();
	return -1;
    }
    
    public int getAlternateAnnoElementForTime(double time, int depth) {
	if (depth == -1) depth = activedepth;
	for (int i=0;i<annotationelements.size();i++) {
	    AnnotationElement ae = (AnnotationElement)annotationelements.get(i);
	    if (ae.isAnnoElementInThisTime(time)==true && lastActiveElements.get(i)==false) {
		if (recursive == false){
		    lastActiveElements.set(i);
		    return i; 
		}
		else {
		    if (ae.nomwriteelement == null) {
			lastActiveElements.set(i);
			return i;
		    }
		    if (ae.nomwriteelement.getRecursiveDepth() == depth) {
			lastActiveElements.set(i);
			return i; 
		    }
		}
	    }
	}
	lastActiveElements.clear();
	return -1;
    }

    /**
     * Removes the selected track element,
     */
    public void removeTrackElement() {
	if (activeannotationelement==-1)
	    return;
	int n = JOptionPane.showConfirmDialog(root,"Are you sure you want remove this\ntrack element from the layer ?","Question",JOptionPane.YES_NO_OPTION);
	if (n==0) {
	    AnnotationElement ae = (AnnotationElement)annotationelements.get(activeannotationelement);
	    net.sourceforge.nite.nom.nomwrite.impl.NOMWriteElement nwa = (net.sourceforge.nite.nom.nomwrite.impl.NOMWriteElement)ae.nomwriteelement;
	    annotationelements.remove(activeannotationelement);
	    activeannotationelement = -1;
	    // remove nom element fom the hashtable
	    root.nomcommunicator.annohashmap.remove(nwa);
	    // delete the nom element from the corpus 
	    net.sourceforge.nite.nom.nomwrite.NOMElement parent = nwa.getParentInFile();
	    try {
		parent.deleteChild(nwa);
		root.statusbar.updateStatusText("Element removed.");
	    }
	    catch(Exception e) {
		root.statusbar.updateStatusText("Error while remove element.");
		root.printDebugMessage(" ERROE while remove the element from nom");
		JOptionPane.showMessageDialog(root,"Error while remove the eleemnt from NOM","ERROR",3);
	    }
	    repaint();
	}
    }
    /**
     * Searches recursively for parent layers and activates the selected elements
     * @param startAndEndTime - The given start and end times
     * @return startAndEndTimes - The returned start and end times
     */
    public startAndEndTimes getStartAndEntTimeForOverlayingLayers(startAndEndTimes times) {
	if (getLayerType() != STRUCTURAL)
	    return null;
	else {
	    AnnotationLayer parentLayer = root.layermanager.getLayerByID(getParentID());
	    int beginelementindex = parentLayer.getAnnoElementForTime(times.begin,activedepth);
	    int endelementindex   = parentLayer.getAnnoElementForTime(times.end,activedepth);
	    if (beginelementindex==-1 || endelementindex==-1)
		return null;
	    AnnotationElement beginae = (AnnotationElement)parentLayer.annotationelements.get(beginelementindex);
	    AnnotationElement endae   = (AnnotationElement)parentLayer.annotationelements.get(endelementindex);
	    startAndEndTimes newtimes = new startAndEndTimes(beginae.begintime,endae.endtime);
	    return newtimes;
	}
    }
    /**
     * Searches for the start and end times for the on the fly annotation
     * @param startAndEndTime - The given start and end times
     * @return startAndEndTimes - The returned start and end times
     */
    public startAndEndTimes getStartAndEndTimesForOnTheFlyAnnotation(startAndEndTimes times) {
	if (getLayerType() != STRUCTURAL)
	    return times;
	AnnotationLayer parentLayer = root.layermanager.getLayerByID(getParentID());
	// first look for the starttime of the new element
	int beginelementindex = parentLayer.getAnnoElementForTime(times.begin-((Integer)root.ctrlwin.reaktiontime.getValue()).doubleValue()/1000,0);
	int endelementindex   = parentLayer.getAnnoElementForTime(times.end+((Integer)root.ctrlwin.reaktiontime.getValue()).doubleValue()/1000,0);
	if (beginelementindex==-1 || endelementindex==-1)
	    return null;
	AnnotationElement beginae = (AnnotationElement)parentLayer.annotationelements.get(beginelementindex);
	AnnotationElement endae   = (AnnotationElement)parentLayer.annotationelements.get(endelementindex);
	startAndEndTimes newtimes = new startAndEndTimes(beginae.begintime,endae.endtime);
	return newtimes;
    }
    /**
     * Extract Samples
     */
    public boolean generateSamples(String url)
    {
	root.setCursorForAllWidgets(1);
	try {
	    root.statusbar.updateStatusText("Read Out Samples.");
	    int compress = 30; // corresponds a samplerate from 8000/x
	    SamplesReader samplesreader = new SamplesReader(root);
	    // Start to read out the samples
	    if (samplesreader.generateSamplesFromURL(url)==false)
		{
		    root.setCursorForAllWidgets(0);
		    return false;
		}
	    // Wait for the thread
	    root.printDebugMessage("START READ OUT THE SAMPLES");
	    while (samplesreader.finished == false) {
		Thread.sleep(100);
		int progress = (int)(double)(samplesreader.audioStream.size()/(samplesreader.duration*8000.0)*100.0);
		root.statusbar.updateStatusValue(progress);
	    }
	    root.printDebugMessage("FINISHED READ OUT THE SAMPLES");
	    // Copy it into the array and normalize it
	    int size = samplesreader.audioStream.size();
	    duration = (float)samplesreader.duration;
	    float[] tmp = new float[size];
	    Byte b;
	    float number;
	    for (int j=0;j<size;j++) {
		b = (Byte)samplesreader.audioStream.get(j);	    
		number = b.floatValue();
		tmp[j] = number;
	    }
	    // Resample the whole vector
	    samples = new float[size/compress+1];
	    for (int j=0;j<size;j++)
		samples[j/compress] += tmp[j]/(float)compress;
	    
	    // Normalize the resampled vector
	    double median=0.0,maximum=0.0,ratio;
	    for (int j=0;j<(size/compress);j++) 
		{
		    median += (double)Math.abs(samples[j]);
		    if (Math.abs(samples[j])>maximum)
			maximum = Math.abs(samples[j]);
		}
	    median /= (size/compress);
	    ratio = maximum/median;
	    if (ratio>10.0) ratio=10.0;
	    root.printDebugMessage("Peak/Median Ration = "+ratio);
	    for (int j=0;j<(size/compress);j++) 
		samples[j] *= 128.0f/median/ratio;
	    
	    root.printDebugMessage("FINISHED BUILD THE SAMPLES");
	    root.statusbar.updateStatusValue(100);
	    root.statusbar.updateStatusText("Samples read.");
	}
	catch(Exception e)
	    {
		root.printDebugMessage("Error while extract the Samples");
		e.printStackTrace();
		root.setCursorForAllWidgets(0);
		return false;
	    }
	    root.setCursorForAllWidgets(0);
	    return true;
    }
    public boolean generateSpectrum(String url)
    {
	root.setCursorForAllWidgets(1);
	try {
	    root.statusbar.updateStatusText("Calculate Spectrum.");
	    SamplesReader samplesreader = new SamplesReader(root);
	    // Start to read out the samples
	    if (samplesreader.generateSamplesFromURL(url)==false)
		{
		    root.setCursorForAllWidgets(0);
		    return false;
		}
	    // Wait for the thread
	    root.printDebugMessage("START READ OUT THE SAMPLES");
	    while (samplesreader.finished == false) {
		Thread.sleep(100);
		int progress = (int)(double)(samplesreader.audioStream.size()/(samplesreader.duration*8000.0)*100.0);
		root.statusbar.updateStatusValue(progress);
	    }
	    root.printDebugMessage("START SPECTRUM GENERATION");
	    // Copy it into the array and normalize it
	    int size = samplesreader.audioStream.size();
	    duration = (float)samplesreader.duration;
	    samples = new float[size];
	    Byte b;
	    float number;
	    for (int j=0;j<size;j++) {
		b = (Byte)samplesreader.audioStream.get(j);	    
		number = b.floatValue();
		samples[j] = number;
	    }
	    
	    // Normalize the resampled vector
	    double median=0.0,maximum=0.0,ratio;
	    for (int j=0;j<(size);j++) 
		{
		    median += (double)Math.abs(samples[j]);
		    if (Math.abs(samples[j])>maximum)
			maximum = Math.abs(samples[j]);
		}
	    median /= (size);
	    ratio = maximum/median;
	    if (ratio>10.0) ratio=10.0;
	    root.printDebugMessage("Peak/Median Ration = "+ratio);
	    for (int j=0;j<(size);j++) 
		samples[j] *= 128.0f/median/ratio;

	    // Initialize the transformation
	    de.dfki.maths.FastFourierTransform fft = new de.dfki.maths.FastFourierTransform();
	    int samplelength    = samples.length;
	    int numberofwindows = samplelength/128;
	    spectrum = new float[numberofwindows][64];
	    float[] timetmp = new float[128];
	    float[] frequtmp;
	    float max   = 0.0f;
	    float min   = 1000000.0f;
	    int counter = 0;
	    // Build the spectrum
	    for (int i=0;i<(samplelength-128);i+=128) {
 		root.statusbar.updateStatusValue(i*100/(samplelength-128));
		// Generate the buffer
		for (int j=0;j<128;j++) {
		    timetmp[j] = samples[j+i];
		}
		// Transformate the buffer
		frequtmp = fft.doFFT(timetmp);
		// And copy it into the spectrum
		for (int j=0;j<64;j++) {
		    spectrum[counter][j] = (float)Math.log(frequtmp[j]+1.0);
		    if (spectrum[counter][j]>max) 
			max=spectrum[counter][j];
		    if (spectrum[counter][j]<min) 
			min=spectrum[counter][j];
		}
		counter ++;
	    }
	    float span = max-min;
	    // Normalize the spectrum from 0..1
	    for (int i=0;i<numberofwindows;i++)
		for (int j=0;j<64;j++) {
		    spectrum[i][j] = (spectrum[i][j]-min)/span;
		}
	    // delete the samples
	    samples = null;
	    root.printDebugMessage("FINISHED BUILD THE SPECTRUM, MAX: "+max+" MIN: "+min);
	    root.statusbar.updateStatusValue(100);
	    root.statusbar.updateStatusText("Spectrum calculated.");
	}
	catch(Exception e)
	    {
		root.printDebugMessage("Error while extract the Spectrum");
		e.printStackTrace();
		root.setCursorForAllWidgets(0);
		return false;
	    }
	root.setCursorForAllWidgets(0);
	return true;
    }
    /**
     * This class sorts all annoatation elements for there start time. 
     */
    public void sortAnnotationElemets (){
	AnnotationElementComparator comparator = new AnnotationElementComparator();
	java.util.Collections.sort(annotationelements,comparator);
    }

    /**
     * Inner class for the comparison of annotation elements.
     */
    private class AnnotationElementComparator implements java.util.Comparator {
	public int compare(Object o1, Object o2)
	{
	    AnnotationElement a1 = (AnnotationElement)o1;
	    AnnotationElement a2 = (AnnotationElement)o2;
	    if (a1.begintime < a2.begintime) return (-1);
	    if (a1.begintime > a2.begintime) return (+1);
	    return (0);
	}
	public boolean equals(Object obj)
	{
	    return false;
	}
    }
    /**
     * The action listener interface
     */
    public void actionPerformed(ActionEvent e) {
	if (e.getActionCommand() == "remove element") {
	    removeTrackElement();
	}
	if (e.getActionCommand() == "add child") {
	    root.addChild = true;
	    root.statusbar.updateStatusText("Select Child to add");
	    root.setCursorForAllWidgets(2);
	}
	if (e.getActionCommand() == "remove child") {
	    root.removeChild = true;
	    root.statusbar.updateStatusText("Select child to remove");
	    root.setCursorForAllWidgets(2);
	}
	if (e.getActionCommand() == "remove all children") {
	    AnnotationElement ae = (AnnotationElement)annotationelements.get(activeannotationelement);
	    if (ae == null){
		JOptionPane.showMessageDialog(root,"No Element selected.","WARNING",2);
		root.statusbar.updateStatusText("No element selected.");
	    }
	    Object[] options = {"Remove All","Cancel"};
	    int n = JOptionPane.showOptionDialog(root,"Are you really sure to remove all\nchildren from this Element.","Really remove all ?",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE,null,options,options[0]); // Yes = 0, No = 1
	    if (n==1)
		return;
	    else {
		NOMWriteElement nwe = ae.nomwriteelement;
		java.util.List children = nwe.getChildren();
		if (children != null) {
		    while (children.size() != 0) {
			try {
			    nwe.removeChild((NOMWriteElement)children.get(0));
			    children = nwe.getChildren();
			}
			catch(Exception ex) {
			    JOptionPane.showMessageDialog(root,"ERROR while remove Child.","ERROR",3);
			}
		    } 
		}
	    }
	}
	if (e.getActionCommand() == "add parent") {
	    root.addParent = true;
	    root.statusbar.updateStatusText("Select parent to add");
	    root.setCursorForAllWidgets(2);
	}
	if (e.getActionCommand() == "remove parent") {
	    root.removeParent = true;
	    root.statusbar.updateStatusText("Select child to remove");
	    root.setCursorForAllWidgets(2);
	}
	if (e.getActionCommand() == "remove all parents") {
	    AnnotationElement ae = (AnnotationElement)annotationelements.get(activeannotationelement);
	    if (ae == null){
		JOptionPane.showMessageDialog(root,"No Element selected.","WARNING",2);
		root.statusbar.updateStatusText("No element selected.");
	    }
	    Object[] options = {"Remove All","Cancel"};
	    int n = JOptionPane.showOptionDialog(root,"Are you really sure to remove all\nparents from this Element.","Really remove all ?",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE,null,options,options[0]); // Yes = 0, No = 1
	    if (n==1)
		return;
	    else {
		NOMWriteElement nwe = ae.nomwriteelement;
		java.util.List parents = nwe.getParents();
		if (parents != null) {
		    while (parents.size() != 0) {
			try {
			    ((NOMWriteElement)parents.get(0)).removeChild(nwe);
			    parents = nwe.getParents();
			}
			catch(Exception ex) {
			    JOptionPane.showMessageDialog(root,"ERROR while remove Parents.","ERROR",3);
			}
		    } 
		}
	    }
	}
    }
}
