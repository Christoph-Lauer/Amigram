package de.dfki.ami.amigram.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;

public class SplashWindow extends JWindow {
	ImageIcon image;

	boolean done = false;

	float version;

	public SplashWindow(String imageName, int theWaitTime, boolean allowClick,
			float ver) {
		super();
		version = ver;
		final int waitTime = 1000 * theWaitTime;
		// URL imageURL = getClass().getResource(imageName);
		// if (imageURL != null)
		// image = new ImageIcon(imageURL);
		// else//try and load it from a local drive
		// image = new ImageIcon(imageName);
		IconManager iconManager = IconManager.getInstance();
		Icon icon = iconManager.getIcon(IconManager.SPLASH);
		image = ((ImageIcon) icon);
		JLabel l = new JLabel(image);
		
		getContentPane().add(l, BorderLayout.CENTER);
		pack();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension labelSize = l.getPreferredSize();
		setLocation((screenSize.width / 2) - (labelSize.width / 2),
				(screenSize.height / 2) - (labelSize.height / 2));
		if (allowClick) {
			addMouseListener(new MouseAdapter() {
				public void mousePressed(MouseEvent e) {
					done = true;
					setVisible(false);
					dispose();
				}
			});
		}
		final Runnable closerRunner = new Runnable() {
			public void run() {
				setVisible(false);
				dispose();
			}
		};
		Runnable waitRunner = new Runnable() {
			public void run() {
				try {
					Thread.sleep(waitTime);
					done = true;
					SwingUtilities.invokeAndWait(closerRunner);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		setVisible(true);
		Thread splashThread = new Thread(waitRunner, "SplashThread");
		splashThread.start();
	}

	public void paint(Graphics g) {
		g.drawImage(image.getImage(), 0, 0, new Color(0, 0, 0), this);
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setFont(new Font("Tahoma", Font.BOLD, 18));
		g2.setColor(new Color(40, 40, 60));
		g2.drawString("(c) DFKI-Christoph Lauer-Version:" + version, 20,
				getHeight() - 15);
	}
}
