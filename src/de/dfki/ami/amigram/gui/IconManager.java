package de.dfki.ami.amigram.gui;

import java.util.Hashtable;

import javax.swing.Icon;
import javax.swing.ImageIcon;

public class IconManager {
	
    /* the subdirectory where the icon images are located */
    public static final String DEFAULT_DIRECTORY = "/images";

    /* the keys */
    public static final String OTAB            = "otab.gif";
    public static final String ZOOMIN          = "ZoomIn.gif";
    public static final String ZOOMOUT         = "ZoomOut.gif";
    public static final String UP              = "Up.gif";
    public static final String DOWN            = "Down.gif";
    public static final String ADD             = "Add.gif";
    public static final String DEL             = "Del.gif";
    public static final String SPEC            = "Spec.png";
    public static final String OSCA            = "Osca.png";
    public static final String REMO            = "Remove.gif";
    public static final String SPLASH		   = "splash.png";
    public static final String TREE	     	   = "brushed_metal.jpg";

    /** the singleton instance */
    private static final IconManager THE_INSTANCE = new IconManager();

    /** the cache */
    private Hashtable table;
    
    private IconManager() {
	table = new Hashtable();
    }
    
    public static IconManager getInstance() {
	return THE_INSTANCE;
    }

    public Icon getIcon(String key) {
	Icon ret = (Icon)table.get(key);
	
	if (ret == null) {
	    ret = new ImageIcon(getClass().getResource(DEFAULT_DIRECTORY+"/"+key));
	    if (ret != null)
	    	table.put(key, ret);
	    else
	    	System.out.println("Error: Picture " + key + " not found!");
	}

	return ret;
    }
}
