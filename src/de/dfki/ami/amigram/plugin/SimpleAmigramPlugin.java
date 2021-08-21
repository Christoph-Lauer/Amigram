/******************************************************************
 *                 SimpleAmigramPlugin.java  -  description
 *                    -----------------------
 * @author 		  Christoph Lauer
 * @version 		  0.1  
 * @see			  www.amiproject.de
 * copyright		: (C) 2004 @ christoph lauer
 * begin 		: Thu Jan 20 15:01:00 CET 2004
 * last save   	        : Time-stamp: <05/08/19 13:17:57 clauer> 
 * compiler    	        : java version 1.4.2 
 * operating system     : Linux 
 * editor		: xemacs 21.4
 * description          : simple implementation of an Amigram plugin
 ******************************************************************/

package de.dfki.ami.amigram.plugin;

import de.dfki.ami.amigram.gui.DesktopFrame;

public class SimpleAmigramPlugin implements de.dfki.ami.amigram.plugin.AnnotationListener
{
    private DesktopFrame amigramRoot;
    
    public SimpleAmigramPlugin(DesktopFrame r)
    {amigramRoot = r;}

    public void annoElementSelected(AnnotationEvent e) {
	amigramRoot.printDebugMessage("PLUGIN: ANNOTATION ELEMENT SELECTED");
    }
    public void emptyAnnoElementSelected(AnnotationEvent e) {
	amigramRoot.printDebugMessage("PLUGIN: EMPTY ANNOTATION ELEMENT SELECTED");
    }
    public void annoElementDoubleClicked(AnnotationEvent e) {
	amigramRoot.printDebugMessage("PLUGIN: ANNOTATION ELEMENT DOUBLECLICKED");
    }

    public void emptyAnnoElementDoubleClicked(AnnotationEvent e) {
	amigramRoot.printDebugMessage("PLUGIN: EMPTY ANNOTATION ELEMENT DOUBLECLICKED");
    }
    
    public void initialize(de.dfki.ami.amigram.gui.DesktopFrame root) {
    System.out.println("Simple Plugin Initialized !");
	amigramRoot = root;
	amigramRoot.printDebugMessage("PLUGIN: AMIGRAM REFERENCE RECEIVED");
    }
    public void finalize(de.dfki.ami.amigram.gui.DesktopFrame root) {}
    public void terminate(){};
    public String getName(){return("SimplePlugin (the default plugin)");}
    public void showGui(){};
    public boolean invokePlugin(java.util.List attributeList){return true;};
}
