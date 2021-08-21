/******************************************************************
 *                 AnnotationLayerManagement.java  -  description
 *                    -----------------------
 * @author 		  Christoph Lauer
 * @version 		  0.1  
 * @see			  www.amiproject.de
 * copyright		: (C) 2003 @ christoph lauer
 * begin 		: Thu Okt 30 17:00:00 CET 2003
 * last save   	        : Time-stamp: <05/08/19 14:01:48 clauer> 
 * compiler    	        : java version 1.4.2 
 * operating system     : Linux 
 * editor		: xemacs 21.4
 * description          : Admisters the Layers.
 ******************************************************************/

package de.dfki.ami.amigram.tools;

import java.util.Iterator;
import java.util.Vector;

import javax.swing.JOptionPane;

import de.dfki.ami.amigram.gui.DesktopFrame;

public class AnnotationLayerManagement{

    public  Vector layervector = new Vector();
    public  double timelinealstarttime   = 0.0;
    public  double timelinealendtime     = 0.0;
    public  double timelinealcurrenttime = 0.0;
    private DesktopFrame root;
    private int activelayer = -1;
    private double videoduration = 100.0;
    private double framespersecond = 10;
 
    /**
     * The default constructor.
     */
    public AnnotationLayerManagement(DesktopFrame r) {
	root = r;
    }
    /**
     * Adds a time aligned layer to the layer list.
     * @param String name - The name of the layer.
     */
    public int addTimeAlignedLayer(String name) {
	AnnotationLayer annolayer = new AnnotationLayer(root,name,layervector.size(),-1,AnnotationLayer.TIMEALIGNED);
	layervector.add(annolayer);
	return layervector.size()-1;
    }
    /**
     * Adds a structural layer to the layer list.
     * @param String name - The name of the layer.
     * @param int parent - The ID of the parent layer.
     */
    public int addStructuralLayer(String name, int parent) {
	AnnotationLayer annolayer = new AnnotationLayer(root,name,layervector.size(),parent,AnnotationLayer.STRUCTURAL);
	layervector.add(annolayer);
	return layervector.size()-1;
    }
    /**
     * Adds the time lineal layer to the begin of the layer list.
     * @return int ID - the id of the time lineal layer
     */
    public int addTimeLinealLayer() {
	AnnotationLayer timelineallayer = new AnnotationLayer(root,"Tine Lineal",layervector.size(),-1,AnnotationLayer.TIMELINEAL);
	layervector.add(timelineallayer);
	return layervector.size()-1;
    }
    /**
     * Gets the layer with the ID.
     * @param int ID - the ID of the searched Layer.
     * @return AnnotationLayer - The layer for this ID. Is null if the ID of the layer is out of the borders.
     */
    public AnnotationLayer getLayerByID(int ID) {
	if (0 <= ID && layervector.size() > ID)
	    return (AnnotationLayer)layervector.get(ID);
	else 
	    return null;
    }
    /**
     * Gets the layer with the name.
     * @param String name - The name of the layer to search.
     * @return int ID - The ID of the layer with the name. If no layer with this name is found -1 will be give back.
     */
    public int findLayerByName(String name){
	for (int i=0;i<layervector.size();i++) {
	    AnnotationLayer actuallayer = (AnnotationLayer)layervector.get(i);
	    if (name.compareTo(actuallayer.getName()) == 0) {
		return actuallayer.getID();
	    }
	}
	return -1;
    }

    /**
     * @param int - The number of layers in the manager. Attention: The layer counter begins with 0 and not
     * with 1. This means if the number of layers is for example 5 the last layer has the index 4 !
     */
    public int getNumberOffLayers() {
	return layervector.size()+1;
    }

    /**
     * returns the hierarchy depth of this layer.
     * @param  int ID - the layer for which the hierarchy is needed.
     * @return int - the layer hierarchy depth. Is -1 when the layer ID does not exist.
     */
    public int getHierarchyDeepthForID(int ID) {
	if (0 <= ID && layervector.size() > ID) {
	    if (getLayerByID(ID).getLayerType() == AnnotationLayer.TIMEALIGNED)
		return 0;
	    int deepth = -1;
	    int parent;
	    AnnotationLayer layer;
	    do {
		deepth ++;
		layer = getLayerByID(ID); 
		ID = layer.getParentID();
	    }
	    while (ID != -1);
	    return deepth;
	}
	else 
	    return-1;
    }
    /**
     * This is an DEBUG function with traces the layer list informations on the Console.
     */
    public void traceLayerList() {
	for (int i=0;i<layervector.size();i++) {
	    AnnotationLayer actuallayer = (AnnotationLayer)layervector.get(i);
	    String layertype = actuallayer.getLayerTypeAsString();
	    root.printDebugMessage(i + "  " + actuallayer.getName() + layertype + " " + " PARENT " + actuallayer.getParentID() + " " + getHierarchyDeepthForID(i));
	}
    }
    /**
     * This function removes all the layers and reinitializes the LayerManager.
     */
    public void removeAllLayers() {
	layervector.removeAllElements();
    }
    /**
     * Returns the active layer.
     * @return int - active layer.
     */
    public int getActiveLayer() {
	if (activelayer == 0) return -1;
	return activelayer;
    }

    /**
     * Sets the active layer.
     * param int al - the active layer.
     */
    public void setActiveLayer(int al) {
	activelayer = al;
    }
    /**
     * Set the video duration.
     * @param double duration - The duration of the video.
     */
    public void setVideoDuration(double d) {
	videoduration = d;
    }
    /**
     * Returns the duration of the video.
     * @ return double - the duration.
     */
    public double getVideoDuration() {
	return videoduration;
    }
    /**
     * Set the video fps.
     * @param double fps - The frames per second of the video.
     */
    public void setFramesPerSecond(double fps) {
	framespersecond = fps;
    }
    /**
     * Returns the frames per second.
     * @return double - the frames per second.
     */
    public double getFramesPerSecond() {
	return framespersecond;
    }
    /** 
     * Updates the AnnotationLayer id's. Useful if there are new Layers inserted or removed.
     */
    public void updateLayerIds() {
	// The mapper is used to mapp the old id's to the new one.
	int[] mapper = new int[root.layermanager.layervector.size()+1];
	for (int i=0;i<root.layermanager.layervector.size();i++) {
	    AnnotationLayer  al = (AnnotationLayer)root.layermanager.layervector.get(i);
	    // Store tho old comnstellation
	    mapper[al.getID()] = i;
	    al.setID(i);
	}
	// And now update the parents.
 	for (int i=0;i<root.layermanager.layervector.size();i++) {
 	    AnnotationLayer  al = (AnnotationLayer)root.layermanager.layervector.get(i);
 	    if (al.getParentID() != -1)
 		al.setParentID(mapper[al.getParentID()]);
 	}
    }
    public void MoveActiveLayerUp(boolean rebuild)
    {
	if (root.otabwin.defaultimageispainted == true)
	    return;
	if (activelayer==-1 || activelayer==0)
	    {
		JOptionPane.showMessageDialog(root,"Please select a Layer.","Move Layer Up",1);
		return;
	    }
	AnnotationLayer al = (AnnotationLayer)layervector.get(activelayer);
	if (al.paintOrderNumber==1)
	    {
		JOptionPane.showMessageDialog(root,"This Layer is already at top.","Move Layer Up",1);
	    }
	else 
	    {
		AnnotationLayer alswitch = getLayerByPaintOrder(al.paintOrderNumber-1);
		al.paintOrderNumber--;
		alswitch.paintOrderNumber++;
		if (rebuild == true)
		    root.getOtabWindow().buildAnnotationBoardPanel();
	    } 
	return;
    }

    public void MoveActiveLayerDown(boolean rebuild)
    {
	if (root.otabwin.defaultimageispainted == true)
	    return;
	if (activelayer==-1 || activelayer==0)
	    {
		JOptionPane.showMessageDialog(root,"Please select a Layer.","Move Layer Down",1);
		return;
	    }
	AnnotationLayer al = (AnnotationLayer)layervector.get(activelayer);
	if (al.paintOrderNumber==layervector.size()-1)
	    {
		JOptionPane.showMessageDialog(root,"This Layer is already on the bottom.","Move Layer Down",1);
	    }
	else
	    {
		AnnotationLayer alswitch = getLayerByPaintOrder(al.paintOrderNumber+1);
		al.paintOrderNumber++;
		alswitch.paintOrderNumber--;
		if (rebuild == true)
		    root.getOtabWindow().buildAnnotationBoardPanel();
	    }
	return;
    }
  
    public void MoveActiveAnnoElementLeft(boolean rebuild)
    {
	if (root.otabwin.defaultimageispainted == true)
	    return;
	if (activelayer==-1 || activelayer==0)
	    {
		JOptionPane.showMessageDialog(root,"Please select a Layer.","Move Element.",1);
		return;
	    }
	AnnotationLayer al = (AnnotationLayer)layervector.get(activelayer);
	if (al.activeannotationelement==-1)
	    {
		JOptionPane.showMessageDialog(root,"Please select an Annotation Element.","Move Element.",1);
		return;
	    }
	AnnotationElement ae = (AnnotationElement)al.annotationelements.get(al.activeannotationelement);
	if (al.activeannotationelement > 0)
	    {
		al.activeannotationelement--;
		activateElement(((AnnotationElement)al.annotationelements.get(al.activeannotationelement)).nomwriteelement);
		root.getControlWindow().annotable.updateAnnotable();
		if (rebuild == true)
		    al.repaint();
		root.getControlWindow().annotable.updateAnnotable();
// 		java.awt.Graphics g = root.otabwin.layercollectionpanel.getGraphics();
// 		root.otabwin.layercollectionpanel.drawConditionNet(g,ae);
	    }
    }

    public void MoveActiveAnnoElementRight(boolean rebuild)
    {
	if (root.otabwin.defaultimageispainted == true)
	    return;
	if (activelayer==-1 || activelayer==0)
	    {
		JOptionPane.showMessageDialog(root,"Please select a Layer.","Move Element.",1);
		return;
	    }
	AnnotationLayer al = (AnnotationLayer)layervector.get(activelayer);
	if (al.activeannotationelement==-1)
	    {
		JOptionPane.showMessageDialog(root,"Please select an Annotation Element.","Move Element.",1);
		return;
	    }
	/*sort the Annotation Elements, see AnnotationLayer for implementation*/
	al.sortAnnotationElemets();
	AnnotationElement ae = (AnnotationElement)al.annotationelements.get(al.activeannotationelement);
	if (al.activeannotationelement < al.annotationelements.size())
	    {
		al.activeannotationelement++;
		activateElement(((AnnotationElement)al.annotationelements.get(al.activeannotationelement)).nomwriteelement);
		if (rebuild == true)
		    al.repaint();
		root.getControlWindow().annotable.updateAnnotable();
// 		java.awt.Graphics g = root.otabwin.layercollectionpanel.getGraphics();
// 		root.otabwin.layercollectionpanel.drawConditionNet(g,ae);
	    }
    }
    ////////////////////////////////////////////////////////////////////////
    public AnnotationLayer getLayerByPaintOrder(int pon)
    {
	AnnotationLayer al;
	for (int i=0;i<layervector.size();i++)
	    {
		al = (AnnotationLayer)layervector.get(i);
		if (al.paintOrderNumber == pon)
		    return al;
		
	    }
	    return null;
    }

    public int getLayerIdByPaintOrder(int pon)
    {
	AnnotationLayer al;
	for (int i=0;i<layervector.size();i++)
	    {
		al = (AnnotationLayer)layervector.get(i);
		if (al.paintOrderNumber == pon)
		    return i;
		
	    }
	return -1;
    }
    ////////////////////////////////////////////////////////////////////////
    public void addSignalPanel(AnnotationLayer al)
    {
	for (int i=1;i<layervector.size();i++)
	    {
		AnnotationLayer tmpAl = (AnnotationLayer)layervector.get(i);
		tmpAl.paintOrderNumber++;
	    }
	layervector.add(al);
	al.paintOrderNumber = 1;
	root.getOtabWindow().buildAnnotationBoardPanel();
    }
    public void removeActiveLayer()
    {
	if (activelayer==-1 || activelayer==0)
	    {
		JOptionPane.showMessageDialog(root,"Please select a Signal Layer.","Move Layer Down",1);
		return;
	    }
	traceLayerList();
	root.printDebugMessage("Active Layer: "+activelayer);
	AnnotationLayer al = (AnnotationLayer)layervector.get(activelayer);
	root.printDebugMessage("Try to remove Layer with name "+al.getName());
	if (al.getLayerType() == AnnotationLayer.SPECTRAL || al.getLayerType() == AnnotationLayer.OSZILLOSKOP)
	    {
		int pod = al.paintOrderNumber;
		layervector.removeElement(al);
		for (int i=1;i<layervector.size();i++)
		    {
 			al = (AnnotationLayer)layervector.get(i);
			if (al.paintOrderNumber > pod)
			    al.paintOrderNumber--;
		    }
		traceLayerList();
		activelayer = -1;
		updateLayerIds();
		root.getOtabWindow().buildAnnotationBoardPanel();
	    }
	else
	    {
		JOptionPane.showMessageDialog(root,"It is only possible to remove Signal Layers.","Error",2);
	    }
	return;
    }
    ////////////////////////////////////////////////////////////////////////////////////
    /**
     * Order the Layers by the paint order and calls the annotation 
     * layer elements sort routine.
     */
    public void orderLayers()
    {
	// first look for the root layers
	Vector roots = new Vector();
	for (int i=1;i<layervector.size();i++)
	    {
		AnnotationLayer al = getLayerByPaintOrder(i);
		if (al.getParentID() == -1)
		    roots.add(al);
	    }
	// then call recursive the layer order function
	int offset = 0;
	for (Iterator iter = roots.iterator(); iter.hasNext();) {
	    AnnotationLayer al = (AnnotationLayer)iter.next();
	    offset = recursiveOrder(al,offset+1);
	}
    }
    // this is the recursive function for the layer ordering
    private int recursiveOrder(AnnotationLayer al, int offset)
    {
	al.paintOrderNumber = offset;
	int[] indices = al.getChildIDs();
	for (int i=0; i<indices.length; i++) {
	    AnnotationLayer child = root.layermanager.getLayerByID(indices[i]);
	    if (child != null) {
		offset = recursiveOrder(child, offset + 1);
	    }
	}
	return offset;
    }
    //////////////////////////////////////////////////////////////////////////////////    
    public void SelectLowerActiveLayer()
    {
	if (root.otabwin.defaultimageispainted == true)
	    return;
	if (activelayer==-1 || activelayer==0)
	    {
		JOptionPane.showMessageDialog(root,"Please select a Layer first.","Select Layer.",1);
		return;
	    }
	AnnotationLayer al = (AnnotationLayer)layervector.get(activelayer);
	if (al.paintOrderNumber==layervector.size()-1)
	    return;
	else
	    {
		activelayer = getLayerIdByPaintOrder(al.paintOrderNumber+1);
		if (al.activeannotationelement != -1) {
		    AnnotationElement ae  = (AnnotationElement)al.annotationelements.get(al.activeannotationelement);
		    float time = (ae.begintime+ae.endtime)/2.0f;
		    AnnotationLayer nal = getLayerByID(activelayer);
		    int nae = nal.getAnnoElementForTime(time,0);
		    if (nae != -1)
			nal.activeannotationelement = nae;
		}
		root.getControlWindow().annotable.updateAnnotable();
		root.getOtabWindow().repaint();
	    }
	return;
    }
    public void SelectUpperActiveLayer()
    {
	if (root.otabwin.defaultimageispainted == true)
	    return;
	if (activelayer==-1 || activelayer==0)
	    {
		JOptionPane.showMessageDialog(root,"Please select a Layer first.","Select Layer.",1);
		return;
	    }
	AnnotationLayer al = (AnnotationLayer)layervector.get(activelayer);
	if (al.paintOrderNumber==1)
	    return;
	else
	    {
		    activelayer = getLayerIdByPaintOrder(al.paintOrderNumber-1);
		    if (al.activeannotationelement != -1) {
			AnnotationElement ae = (AnnotationElement)al.annotationelements.get(al.activeannotationelement);
			float time = (ae.begintime+ae.endtime)/2.0f;
			AnnotationLayer nal = getLayerByID(activelayer);
			int nae = nal.getAnnoElementForTime(time,0);
			if (nae != -1)
			    nal.activeannotationelement = nae;
		    }
		    root.getControlWindow().annotable.updateAnnotable();
		    root.getOtabWindow().repaint();
	    }
	return;
    }
    //////////////////////////////////////////////////////////////////////////////////    

    AnnotationElement getActiveAnnotationElement() {
	AnnotationLayer al = (AnnotationLayer)layervector.get(activelayer);
	if (al==null || al.activeannotationelement==0) return null;
	AnnotationElement ae = (AnnotationElement)al.annotationelements.get(al.activeannotationelement);
	if (ae == null) return null;
	return ae;
    }

    /** function gets a list of query results, all elements must be from type NOMElement */
    public void highlightElements(java.util.List list, java.awt.Color color) {
	Iterator it = list.iterator();
	while (it.hasNext()) {
	    net.sourceforge.nite.nom.nomwrite.NOMElement ne = (net.sourceforge.nite.nom.nomwrite.NOMElement)it.next();
	    AnnotationElement ae = (AnnotationElement)root.nomcommunicator.annohashmap.get(ne);
	    if (ae != null) 
		ae.searchResult = color;
	}
	root.getOtabWindow().layercollectionpanel.repaint();
    }
    /** clears all the query results */
    public void clearHighlightElements() {
	Iterator it = root.nomcommunicator.annohashmap.values().iterator();
	while (it.hasNext()) {
	    AnnotationElement ae = (AnnotationElement)it.next();
	    if (ae != null) 
		ae.searchResult = null;
	}
	root.getOtabWindow().layercollectionpanel.repaint();
    }



    /** function gets one query result and selects the element active and spring to the position where it is placed*/
    public void activateElement(net.sourceforge.nite.nom.nomwrite.NOMElement ne) {
	AnnotationElement ae = (AnnotationElement)root.nomcommunicator.annohashmap.get(ne);
	if (ae != null) {
	    AnnotationLayer al = ae.al;
	    if (al != null) {
		// look for the layer and set them active
		for (int i=0;i<layervector.size();i++) {
		    if (((AnnotationLayer)layervector.get(i)).getID() == al.getID())
			activelayer = i;
		}
		// look for the element and set them active
		for (int i=0;i<al.annotationelements.size();i++) {
		    if  (((AnnotationElement)al.annotationelements.get(i)).nomwriteelement.getID().equals(ne.getID()))
			al.activeannotationelement = i;
		}
		root.getOtabWindow().layercollectionpanel.repaint();
		// place the AnnoElement in the middle of the layercollection panel
		java.awt.Point point = root.otabwin.scrollpane.getViewport().getViewPosition();
		int posX = al.convertTimeToPixel((ae.begintime + ae.endtime)/2.0);
		int posY = (int)point.getY();
		java.awt.Rectangle rect = root.otabwin.scrollpane.getViewport().getViewRect();
		int width = (int)rect.getWidth();
		System.out.println(width);
		int widthHalf = width/2;
		
		root.otabwin.scrollpane.getViewport().setViewPosition(new java.awt.Point(posX-widthHalf,posY));
		System.out.println("SetElementActive");
	    }
	}	
    }
}