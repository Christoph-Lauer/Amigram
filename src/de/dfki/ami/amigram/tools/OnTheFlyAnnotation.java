/******************************************************************
 *                  OnTheFlyAnnotation.java -  description
 *                    ----------------------
 * @author 		  Christoph Lauer
 * @version 		  0.1  
 * @see			  www.amiproject.de
 * copyright		: (C) 2003 @ christoph lauer
 * begin 		: Thu Okt 30 17:00:00 CET 2003
 * last save   	        : Time-stamp: <05/03/08 19:35:01 clauer> 
 * compiler    	        : java version 1.4.2 
 * operating system     : Linux 
 * editor		: xemacs 21.4
 * description          : Handles the On the Fly Annotation.
 ******************************************************************/
package de.dfki.ami.amigram.tools;
import de.dfki.ami.amigram.gui.DesktopFrame;
import de.dfki.ami.amigram.multimedia.MediaPlayer;

public class OnTheFlyAnnotation {
    private DesktopFrame root;
    private boolean otfstate = false;
    private double  otftime = 0.0;
    private boolean lastPointFromAutoNew = false;  // is set to true if the last start time was set from the endAndNew "button"

    /** The constructor */
    public OnTheFlyAnnotation(DesktopFrame r){
	root = r;
    }
    /** sets the key pressed time to now */
    private double getMediaTime(){
	MediaPlayer mp = root.synchronizer.getCurrentPlayer();
	if (mp == null) return -1.0;
	double time = root.synchronizer.getCurrentPlayer().getMediaTime().getSeconds();
	return time;
    }
    /** Returns the state of the player, true if playing*/
    public boolean getPlayerState() 
    {
	MediaPlayer mp = root.synchronizer.getCurrentPlayer();
	if (mp == null) return false;
	if (mp.getPlayerState() == javax.media.Controller.Started)
	    return true;
	else 
	    return false;
	
    }
    /** Event for key two "STRG N" key */
    public void beginOrNewBeginEvent(){
	double time = getMediaTime(); // now pressed media time
	AnnotationLayer al = (AnnotationLayer)root.layermanager.getLayerByID(root.layermanager.getActiveLayer());
	if (al == null) return;
	if (otfstate == false){ // begin new segment
	    otfstate = true;
	    al.otfstate = true;
	    otftime = time;
	}
	else{           	// end old and begin new segment
	    startAndEndTimes times = new startAndEndTimes(otftime,time);
	    times = al.getStartAndEndTimesForOnTheFlyAnnotation(times);
	    if (times == null)
		{
		    root.statusbar.updateStatusText("No time for OTF !!!.");
		    java.awt.Toolkit.getDefaultToolkit().beep();
		    otfstate = true;
		    al.otfstate = true;
		    otftime = time;
		    return;
		}		
	    AnnotationElement ae;
	    if (time>otftime) 
	        ae = new AnnotationElement((float)otftime,(float)times.end,al);
 	    else
		ae = new AnnotationElement(times,al);
	    al.annotationelements.add(ae);
	    root.statusbar.updateStatusText("OTF segment created.");
	    al.sortAnnotationElemets();
	    al.repaint();
	    otftime = times.end;
	    lastPointFromAutoNew = true;
	}
    }
    /** Event for key one, "STRG M" key */
    public void beginOrEndEvent(){
	double time = getMediaTime(); // now presed media time
	// first get the current active layer
	AnnotationLayer al = (AnnotationLayer)root.layermanager.getLayerByID(root.layermanager.getActiveLayer());
	if (al == null) return;
	if (otfstate == false){ // begin new segment
	    otfstate = true;
	    al.otfstate = true;
	    otftime = time;
	}
	else {          	// end current segment and store them in the layer
	    startAndEndTimes times = new startAndEndTimes(otftime,time);
	    times = al.getStartAndEndTimesForOnTheFlyAnnotation(times);
	    if (times == null)
		{
		    root.statusbar.updateStatusText("No time for OTF !!!.");
		    java.awt.Toolkit.getDefaultToolkit().beep();
		    otfstate = false;
		    al.otfstate = false;
		    lastPointFromAutoNew = false;
		    return;
		}		
	    AnnotationElement ae;
	    if (time>otftime)
	        ae = new AnnotationElement((float)otftime,(float)times.end,al);
 	    else
		ae = new AnnotationElement(times,al);
	    al.annotationelements.add(ae);
	    root.statusbar.updateStatusText("OTF segment created.");
	    al.sortAnnotationElemets();
	    al.repaint();
	    otfstate = false;
	    al.otfstate = false;
	    lastPointFromAutoNew = false;
	}
    }
    /** returns the state of the OTFA */
    public boolean getOtfState(){return otfstate;}
    /** returns the current OTF time */
    public double getOtfTime(){return otftime;}
}
