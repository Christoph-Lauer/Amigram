/******************************************************************
 *                 AnnotationElement.java  -  description
 *                    -----------------------
 * @author 		  Christoph Lauer
 * @version 		  0.1  
 * @see			  www.amiproject.de
 * copyright		: (C) 2003 @ christoph lauer
 * begin 		: Thu Okt 30 17:00:00 CET 2003
 * last save   	        : Time-stamp: <05/08/09 10:29:08 clauer> 
 * compiler    	        : java version 1.4.2 
 * operating system     : Linux 
 * editor		: xemacs 21.4
 * description          : The atomar element.
 ******************************************************************/

package de.dfki.ami.amigram.tools;
import java.awt.Color;

public class AnnotationElement extends Object
{
    public float begintime = 0.0f;
    public float endtime   = 0.0f;
    public net.sourceforge.nite.nom.nomwrite.impl.NOMWriteElement nomwriteelement;
    public AnnotationLayer al;
    public Color searchResult = null;
    
    public AnnotationElement(net.sourceforge.nite.nom.nomwrite.impl.NOMWriteElement nwe,AnnotationLayer alay) {
	al = alay;
	nomwriteelement = nwe;
	begintime = (float)nwe.getStartTime();
	endtime   = (float)nwe.getEndTime();
    }
    public AnnotationElement(float start, float end, AnnotationLayer alay) {
	al = alay;
	begintime = start;
	endtime = end;
    }
    public AnnotationElement(startAndEndTimes times, AnnotationLayer alay) {
	al = alay;
	begintime = (float)times.begin;
	endtime   = (float)times.end;
    }
    /**
     * This function checks if the annotation element is in the time.
     * @param double time
     * @return boolean - true if it is in the time.
     */
    public boolean isAnnoElementInThisTime(double time) {
	if (time>=begintime && time<=endtime) return true;
	else return false;
    }

    /**
     * This function checks if the whole annotation element is between this two times
     * @param double start
     + @param double end
     * @return boolean - true if the element is between
     */
    public boolean isAnnoElementBetweenThisTimes(double start,double end) {
	if (start<=begintime && end>=endtime) return true;
	else return false;
    }
    /** 
     * This is the sort algorithm for the dataTimePoint's in a list
     * @param List - The list that contains the dataTimePoints
     */
    private void sortDataTimePoints(java.util.List l) {
	DataTimePointsComparator c = new DataTimePointsComparator();
	java.util.Collections.sort(l,c);
    }
}
