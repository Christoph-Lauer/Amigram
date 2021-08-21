package de.dfki.ami.amigram.multimedia;

import java.awt.Color;
/**
 * @(#)de.dfki.gram.nite.gui.AnnotationLayer.java
 * Copyright (c) 2003 Christoph Lauer @ DFKI, All Rights Reserved.
 * clauer@dfki.de - www.dfki.de
 *
 * This Class generates a green color array, which is later used from the spectrum.
 * @author Christoph Lauer
 * @version 0.1,  Begin 10/09/2002, Current 11/11/2002
 */
public class GreenColorArray {

    /**This is an color array.*/
    public Color[] green = new Color[256];

    /**The constructor generates the colors.*/
    public GreenColorArray() {
	int r,g,b;
	for (int i=0;i<256;i++) {
	    r = i-10;
	    g = i+20;
	    b = i/6;
	    if (r<0) r=0;
	    if (r>255) r=255;
	    if (g>255) g=255;
	    if (b>255) b=255;
	    Color col = new Color(r,g,b);
	    green[i] = col;
	}
    }
}
