package de.dfki.ami.amigram.tools;
    /**
     * This is the comparator class for the sort algorithm.
     */
public class DataTimePointsComparator implements java.util.Comparator {
    public int compare(Object o1, Object o2) {
	DataTimePoint dtp1 = (DataTimePoint)o1;
	DataTimePoint dtp2 = (DataTimePoint)o2;
	    if (dtp1.time.getSeconds() <  dtp2.time.getSeconds()) return -1;
	    if (dtp1.time.getSeconds() == dtp2.time.getSeconds()) return 0;
	    if (dtp1.time.getSeconds() >  dtp2.time.getSeconds()) return  1;
	    return 1;
    }
    public boolean equals(Object o) {
	return false;
    }
}