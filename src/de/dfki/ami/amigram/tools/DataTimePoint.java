package de.dfki.ami.amigram.tools;
    /**
     * This class is a raw data class that implements only a point for an time.
     * It is used by the sort algorithm for the time points.
     */
public class DataTimePoint {
    public java.awt.geom.Point2D.Double point;
    public javax.media.Time time;
    public DataTimePoint(java.awt.geom.Point2D.Double p,javax.media.Time t) {time=t;point=p;}
}
