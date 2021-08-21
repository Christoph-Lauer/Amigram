/******************************************************************
 *                  MediaTimeSynchronizeListener.java  -  description
 *                    -----------------------
 * @author 		  Christoph Lauer
 * @version 		  0.1  
 * @see			  www.amiproject.de
 * copyright		: (C) 2003 @ christoph lauer
 * begin 		: Thu Okt 30 17:00:00 CET 2003
 * last save   	        : Time-stamp: <2004-08-18 05:13:17 christoph> 
 * compiler    	        : java version 1.4.2 
 * operating system     : Linux 
 * editor		: xemacs 21.4
 * description          : This imterface must be implemented from
 *                        any class thah nedds to be informed about
 *                        media time changes.
 ******************************************************************/

package de.dfki.ami.amigram.tools;


public interface MediaTimeSynchronizeListener
{
    /** this is the only message that a listener can receive. **/
    public void receiveMediaTimeChange(javax.media.Time time);
}
