/******************************************************************
 *                 AnnotationEvent.java  -  description
 *                    -----------------------
 * @author 		  Christoph Lauer
 * @version 		  0.1  
 * @see			  www.amiproject.de
 * copyright		: (C) 2004 @ christoph lauer
 * begin 		: Thu Jan 20 15:01:00 CET 2004
 * last save   	        : Time-stamp: <05/01/21 12:33:23 clauer> 
 * compiler    	        : java version 1.4.2 
 * operating system     : Linux 
 * editor		: xemacs 21.4
 * description          : This Event is thrown 
 ******************************************************************/

package de.dfki.ami.amigram.plugin;


/**
 * This is a core data class that holds information about the annotation element 
 * which should be filled with content.
 */
public class AnnotationEvent
{
    /**
     * single field for the element to annotate.
     */
    public net.sourceforge.nite.nom.nomwrite.impl.NOMWriteElement annoElement;
    /** 
     * The constructor gets the element to annotate 
     * @param ae - the annotation element to fill with content 
     */
    public AnnotationEvent(net.sourceforge.nite.nom.nomwrite.impl.NOMWriteElement ae) {
	annoElement = ae;
    }
}
