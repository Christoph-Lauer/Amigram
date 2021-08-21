/******************************************************************
 *                 AnnotationListener.java  -  description
 *                    -----------------------
 * @author 		  Christoph Lauer
 * @version 		  0.1  
 * @see			  www.amiproject.de
 * copyright		: (C) 2004 @ christoph lauer
 * begin 		: Thu Jan 20 15:01:00 CET 2004
 * last save   	        : Time-stamp: <05/07/19 11:18:12 clauer> 
 * compiler    	        : java version 1.4.2 
 * operating system     : Linux 
 * editor		: xemacs 21.4
 * description          : This interface listens for new annotated 
 *                        elements. It should handle the content of
 *                        the new created elements.
 ******************************************************************/

package de.dfki.ami.amigram.plugin;

import java.util.EventListener;

/**
 * This interface listens for new annotated elements. They should 
 * handle the content of the new created annotation elements.
 */
public interface AnnotationListener extends EventListener
{
    /**
     * This function is called if an annotation element on the OTAB was
     * selected. This function should handle the content of the nxt element.
     * Call the root.pluginManager.contentCreated(NOMWriteAnnotation ne) function 
     * with the NOM element from the AnnotationEvent.
     */
    public void emptyAnnoElementSelected(AnnotationEvent e);
    /**
     * This function is called from the plugin manager when an element is selected
     */
    public void annoElementSelected(AnnotationEvent e);
    /**
     * This function is called from the plugin manager when an empty element is double clicked
     */
    public void emptyAnnoElementDoubleClicked(AnnotationEvent e);
    /**
     * This function is called from the plugin manager when an element is double clicked
     */
    public void annoElementDoubleClicked(AnnotationEvent e);
    /**
     * This function was called from Amigram when the plugin was instantiated.
     * The argument from type <DesktopFrame> grants full access to Amigram 
     * including nxt-layers, nxt-corpus, otab .... be careful
     */
    public void initialize(de.dfki.ami.amigram.gui.DesktopFrame root);
    /**
     * This function is called when the plugin was finished.
     */
    public void terminate();
    /** 
     * Name listed in menu bar (the returned name should be one String without spaces)
     */
    public String getName();
    /** 
     * Starts the Plugin - Gui
     */
    public void showGui();
    /**
     * Asks whether the plugin should be invoked
     */
    public boolean invokePlugin(java.util.List attributeList);
}
