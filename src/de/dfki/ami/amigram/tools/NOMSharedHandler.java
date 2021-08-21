/******************************************************************
 *                NOMSharedHandler.java  -  description
 *                    -----------------------
 * @author 		  Christoph Lauer
 * @version 		  0.1
 * @see			  www.amiproject.de
 * copyright		: (C) 2003 @ christoph lauer
 * begin 		: Thu Okt 30 17:00:00 CET 2003
 * last save   	        : Time-stamp: <05/04/29 13:34:57 clauer>
 * compiler    	        : java version 1.5
 * operating system     : Linux
 * editor		: xemacs 21.4
 * description          : Receives changes from the other NOMEditors.
 ******************************************************************/

package de.dfki.ami.amigram.tools;

import net.sourceforge.nite.nom.link.NOMEdit;
import net.sourceforge.nite.nom.nomwrite.NOMElement;
import net.sourceforge.nite.nom.nomwrite.impl.NOMWriteCorpus;
import net.sourceforge.nite.nom.nomwrite.impl.NOMWriteElement;
import de.dfki.ami.amigram.gui.DesktopFrame;

public class NOMSharedHandler implements net.sourceforge.nite.nom.link.NOMView {
    private DesktopFrame root;
    public NOMSharedHandler(DesktopFrame r) {
	root = r;
	registerViewerToCorpus();
    }
    
    public void registerViewerToCorpus() {
	NOMWriteCorpus nwc = root.nomcommunicator.nomwritecorpus;
	if (nwc != null) {
	    nwc.registerViewer(this);
	    root.printDebugMessage("NOMView registered on NOMWriteCorpus.");
	}
	else {
	    root.printDebugMessage("No Corpus to register NOMViewer.");
	}
    }
    
    /** 
     * This function is called from the external editor.
     * Amigram reacts of state change events.
     */
    public void handleChange(NOMEdit edit) {
	// get the element
	NOMElement ne = edit.getElement();
	if (ne == null) return;
	// get the type
	int type = edit.getType();
	// get the active element
	AnnotationElement ae = root.layermanager.getActiveAnnotationElement();

        switch (type) {
	case NOMEdit.ADD_CHILD: 
	    {
		// check if the corresponding element was selected
		if (ae != null)
		    if (ae.nomwriteelement.getID().equals(ne.getID()))
			{
			    root.otabwin.layercollectionpanel.drawConditionNet(root.otabwin.layercollectionpanel.getGraphics(),ae);
			    return;
			}
	    }
	case NOMEdit.ADD_ELEMENT: 
	    {
		try 
		    {
			NOMWriteElement nwe = (NOMWriteElement)ne;
			String layername = ne.getLayer().getName();  
			AnnotationLayer al = root.layermanager.getLayerByID(root.layermanager.findLayerByName(layername));
			AnnotationElement newae = new AnnotationElement(nwe,al);
			al.annotationelements.add(newae);  
			root.nomcommunicator.annohashmap.put(ne,newae);
			al.repaint();
		    }
		catch (Exception e) 
		    {
			e.printStackTrace();
		    }
	    }
	case NOMEdit.ADD_POINTER:return; // not handled yes
	case NOMEdit.DELETE_ELEMENT: 
	    {
		AnnotationElement delae = (AnnotationElement)(root.nomcommunicator.annohashmap).get(ne);
		// remove element from the layer
		// check if elemet was visible
		// repaint if necessary
	    }
	case NOMEdit.DELETE_POINTER:return; // not handled yes
	case NOMEdit.EDIT_POINTER:return; // not handled yes
	case NOMEdit.REMOVE_CHILD: 
	    {
		// check if the corresponding element was selected
		if (ae != null)
		    if (ae.nomwriteelement.getID().equals(ne.getID()))
			{
			    root.otabwin.layercollectionpanel.drawConditionNet(root.otabwin.layercollectionpanel.getGraphics(),ae);
			    return;
			}
	    }
	case NOMEdit.SET_ATTRIBUTE: 
	    {
		// check is selected
		if (ae != null)
		    if (ae.nomwriteelement.getID().equals(ne.getID()))
			{
     	        // rebuild annotable
			    root.getControlWindow().annotable.updateAnnotable();
			}
		// repaint layer
	    }
	case NOMEdit.SET_END_TIME: 
	    {
		// change the correspondig annoelement
		// set the new time
		// repaint layer
	    }
	case NOMEdit.SET_START_TIME: 
	    {
		// change the correspondig annoelement
		// set the new time
		// repaint layer
	    }
	case NOMEdit.SET_TEXT:
	    {
		// check is selected
		// rebuild annotable
	    }
        }

//  	// get the layer for the element
//  	AnnotationElement ae = root.nomcommunicator.annohashmap.get(ne);
//  	if (ae == null) return;
//  	// get the layer
//  	AnnotationLayer al = ae.al;
//  	if (al == null) return;
//  	// repaint the layer
//  	al.repaint();

    }
}
