
/******************************************************************
 *                 QueryResultHandlerImpl.java  -  description
 *                    -----------------------
 * @author 		  Christoph Lauer
 * @version 		  0.1  
 * @see			  www.amiproject.de
 * copyright		: (C) 2003 @ christoph lauer
 * begin 		: Thu Okt 30 17:00:00 CET 2003
 * last save   	        : Time-stamp: <05/08/09 11:00:13 clauer> 
 * compiler    	        : java version 1.5 
 * operating system     : Linux 
 * editor		: xemacs 21.4
 * description          : Handles the result of query GUI.
 ******************************************************************/

package de.dfki.ami.amigram.tools;

import net.sourceforge.nite.nom.nomwrite.NOMElement;
import net.sourceforge.nite.query.QueryResultHandler;
import de.dfki.ami.amigram.gui.DesktopFrame;

public class QueryResultHandlerImpl implements QueryResultHandler 
{
    private DesktopFrame root;
    private java.awt.Color highlightColor = new java.awt.Color(0,255,0);
    public QueryResultHandlerImpl(DesktopFrame r) {
	root = r;
    }
    public void acceptQueryResult(NOMElement result) {
	root.layermanager.activateElement(result);
    }
    public void acceptQueryResults(java.util.List results) {
	root.layermanager.highlightElements(results,highlightColor);
    }
    public void setQueryHighlightColor(java.awt.Color color) {
	highlightColor = color;
    }
}