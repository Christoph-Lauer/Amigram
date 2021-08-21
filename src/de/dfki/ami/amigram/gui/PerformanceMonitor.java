/*
 * @(#)MemoryMonitor.java	1.30 01/12/03
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package de.dfki.ami.amigram.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;


/**
 * Tracks Memory allocated & used, displayed in graph form.
 */
public class PerformanceMonitor extends JPanel implements Runnable {
    public long sleepAmount = 1000;
    private Thread thread;
    private int w, h;
    private BufferedImage bimg;
    private Graphics2D big;
    private Font font = new Font("Thaoma", Font.PLAIN, 11);
    private Runtime r = Runtime.getRuntime();
    private int columnInc;
    private int pts[];
    private int ptNum;
    private int ascent, descent;
    private float freeMemory, totalMemory;
    private Rectangle graphOutlineRect = new Rectangle();
    private Rectangle2D mfRect = new Rectangle2D.Float();
    private Rectangle2D muRect = new Rectangle2D.Float();
    private Line2D graphLine = new Line2D.Float();
    private Color graphColor = new Color(46, 139, 87);
    private Color mfColor = new Color(0, 100, 0);
    private String usedStr;
    public PerformanceMonitor() {
	super();
	setBackground(Color.black);
    }
    public void paint(Graphics g1) {
	if (big == null) return;
	Graphics2D g = (Graphics2D)g1;
	g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
	big.setBackground(getBackground());
	big.clearRect(0,0,w,h);
	float freeMemory = (float) r.freeMemory();
	float totalMemory = (float) r.totalMemory();
	// .. Draw allocated and used strings ..
	big.setColor(Color.green);
	big.drawString(String.valueOf((int) totalMemory/1024/1024) + "MB allocated",  4.0f, (float) ascent+0.5f);
	usedStr = String.valueOf(((int) (totalMemory - freeMemory))/1024/1024) 
	    + "MB used";
	big.drawString(usedStr, 4, h-descent);
	// Calculate remaining size
	float ssH = ascent + descent;
	float remainingHeight = (float) (h - (ssH*2) - 0.5f);
	float blockHeight = remainingHeight/10;
	float blockWidth = 20.0f;
	float remainingWidth = (float) (w - blockWidth - 10);
	// .. Memory Free ..
	big.setColor(mfColor);
	int MemUsage = (int) ((freeMemory / totalMemory) * 10);
	int i = 0;
	for ( ; i < MemUsage ; i++) { 
	    mfRect.setRect(5,(float) ssH+i*blockHeight,
			   blockWidth,(float) blockHeight-1);
	    big.fill(mfRect);
	}
	// .. Memory Used ..
	big.setColor(Color.green);
	for ( ; i < 10; i++)  {
	    muRect.setRect(5,(float) ssH+i*blockHeight,
			   blockWidth,(float) blockHeight-1);
	    big.fill(muRect);
	}
	// .. Draw History Graph ..
	big.setColor(graphColor);
	int graphX = 30;
	int graphY = (int) ssH;
	int graphW = w - graphX - 5;
	int graphH = (int) remainingHeight;
	graphOutlineRect.setRect(graphX, graphY, graphW, graphH);
	big.draw(graphOutlineRect);
	
	int graphRow = graphH/10;
	
	// .. Draw row ..
	for (int j = graphY; j <= graphH+graphY; j += graphRow) {
	    graphLine.setLine(graphX,j,graphX+graphW,j);
	    big.draw(graphLine);
	}
	// .. Draw animated column movement ..
	int graphColumn = graphW/15;
	if (columnInc == 0) {
	    columnInc = graphColumn;
	}
	for (int j = graphX+columnInc; j < graphW+graphX; j+=graphColumn) {
	    graphLine.setLine(j,graphY,j,graphY+graphH);
	    big.draw(graphLine);
	}
	--columnInc;
	if (pts == null) {
	    pts = new int[graphW];
	    ptNum = 0;
	} else if (pts.length != graphW) {
	    int tmp[] = null;
	    if (ptNum < graphW) {     
		tmp = new int[ptNum];
		System.arraycopy(pts, 0, tmp, 0, tmp.length);
	    } else {        
		tmp = new int[graphW];
		System.arraycopy(pts, pts.length-tmp.length, tmp, 0, tmp.length);
		ptNum = tmp.length - 2;
	    }
	    pts = new int[graphW];
	    System.arraycopy(tmp, 0, pts, 0, tmp.length);
	} else {
	    big.setColor(Color.yellow);
	    pts[ptNum] = (int)(graphY+graphH*(freeMemory/totalMemory));
	    for (int j=graphX+graphW-ptNum, k=0;k < ptNum; k++, j++) {
                    if (k != 0) {
                        if (pts[k] != pts[k-1]) {
                            big.drawLine(j-1, pts[k-1], j, pts[k]);
                        } else {
                            big.fillRect(j, pts[k], 1, 1);
                        }
                    }
	    }
	    if (ptNum+2 == pts.length) {
		// throw out oldest point
		for (int j = 1;j < ptNum; j++) {
		    pts[j-1] = pts[j];
		}
                    --ptNum;
	    } else {
		ptNum++;
	    }
	}
	g.drawImage(bimg, 0, 0, this);
    }
    
    public void start() {
	thread = new Thread(this);
	thread.setPriority(Thread.MAX_PRIORITY);
	thread.setName("MemoryMonitor");
	thread.start();
    }
    
    public synchronized void stop() {
	thread = null;
	notify();
    }
    
    public void run() 
    {	
	while (true)
	    {
		Dimension d = getSize();
		if (d.width != w || d.height != h) 
		    {
			w = d.width;
			h = d.height;
			bimg = (BufferedImage) createImage(w,h);
			big = bimg.createGraphics();
			big.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
			big.setFont(font);
			FontMetrics fm = big.getFontMetrics(font);
			ascent = (int) fm.getAscent();
			descent = (int) fm.getDescent();
		    }
		repaint();
		try 
		    {
			thread.sleep(sleepAmount);
		    } catch (InterruptedException e) { break; }
	    }
    }
}


