package de.dfki.ami.amigram.gui;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;

import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicProgressBarUI;

/**
 * Copyright (c) 2001 Christoph Lauer @ DFKI, All Rights Reserved.
 * clauer@dfki.de - www.dfki.de
 *
 * This is my own splash progress bar user interface implementation.
 * @author Christoph Lauer
 * @version 1.0, Current 26/09/2002
 */
public class ProgressBarUI extends BasicProgressBarUI
{
    private Color start;
    private Color end;
    private boolean smallborders;
    /**
     * Construktor for my own implementation of the ProgressBarUI
     * @param Color startCol - Begin color of the gradient of the gradiented color progress.
     * @param Color startCol - End color of the gradient of the gradiented color progress.
     * @param boolean smallerborders - true for smaller borders clled for the SonoProgressBar, Splash Screen uses the larger version.
     */
    public ProgressBarUI(Color startCol,Color endCol,boolean smallerborders){
	super();
	start = startCol;
	end   = endCol;
	smallborders = smallerborders;
    }
    /**
     * Updates the prgressbar
     * @param Graphics g - The Graphics for painting.
     * @param JComponent jcomponent - the component to paint (no use in this function).
     */
    public void paint(Graphics g, JComponent jcomponent)
    {
        Insets insets = super.progressBar.getInsets();
        int i = insets.left;
        int j = insets.top;
        int k = super.progressBar.getWidth()  - (insets.right + i);
        int l = super.progressBar.getHeight() - (insets.bottom + j);
        int i1 = getAmountFull(insets,k,l);
        if(i1>0)
	    {
		GradientPaint gradientpaint = new GradientPaint(i, j,start,i+k,j+l,end);
		Graphics2D graphics2d = (Graphics2D)g;
		graphics2d.setPaint(gradientpaint);
		graphics2d.fill(new Rectangle(i,j,i1,l));
	    }
    }
}
