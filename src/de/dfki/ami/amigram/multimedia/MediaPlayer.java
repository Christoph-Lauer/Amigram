package de.dfki.ami.amigram.multimedia;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Panel;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.File;
import java.net.URL;

import javax.media.CachingControl;
import javax.media.CachingControlEvent;
import javax.media.Clock;
import javax.media.Controller;
import javax.media.ControllerClosedEvent;
import javax.media.ControllerErrorEvent;
import javax.media.ControllerEvent;
import javax.media.ControllerListener;
import javax.media.EndOfMediaEvent;
import javax.media.Manager;
import javax.media.MediaLocator;
import javax.media.MediaTimeSetEvent;
import javax.media.Player;
import javax.media.RealizeCompleteEvent;
import javax.media.StartEvent;
import javax.media.StopEvent;
import javax.media.RestartingEvent;
import javax.media.Time;
import javax.swing.JInternalFrame;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

import de.dfki.ami.amigram.gui.DesktopFrame;
import de.dfki.ami.amigram.tools.MediaTimeSynchronizeListener;

/******************************************************************
 *                     MediaPlayer.java  -  description
 *                    -----------------------
 * @author 		  Christoph Lauer
 * @version 		  0.1  
 * @see			  www.amiproject.de
 * copyright		: (C) 2003 @ christoph lauer
 * begin 		: Thu Okt 30 17:00:00 CET 2003
 * last save   	        : Time-stamp: <05/08/09 12:10:28 clauer> 
 * compiler    	        : java version 1.4.2 
 * operating system     : Linux 
 * editor		: xemacs 21.4
 * description          : The Media player window and the core JMF
 *                        media player including the 
 *                        ControlerListener. The window is an 
 *                        JInternalFrame sub window for the main 
 *                        frame which implememnts the window 
 *                        listener for the closing handling.
 ******************************************************************/

public class MediaPlayer extends JInternalFrame implements InternalFrameListener, ComponentListener,MediaTimeSynchronizeListener
{
	
	File mediafile;
	Panel panel = new Panel();
	
	public  boolean playerRealized = false;
	
	public  float aspectRatio;
	
	public  CorePlayer player;
	
	private int timeJustNowSet = 0;
	
	private int startedJustFromOutside = 0;
	
	private int stopedJustFromOutside = 0;
	
	private boolean playerInitFinished = false;
	
	private DesktopFrame root;
	
	public MediaPlayer(DesktopFrame r,File file) 
	{
		root = r;
		
		mediafile = file;
		
		panel.setLayout(new BorderLayout());
		
		getContentPane().add(panel);
		
		player = new CorePlayer();
		
		setMaximizable(true);
		
		setClosable(true);
		
		setResizable(true);
		
		addInternalFrameListener(this);
		
		addComponentListener(this);
	}
	
	/** nested class for the JMF media handling **/
	class CorePlayer implements ControllerListener {
		
		public Player player = null;                // media Player
		
		Component visualComponent = null;    // component in which video is playing
		
		Component controlComponent = null;   // controls gain, position, start, stop
		
		Component progressBar = null;        // displays progress during download
		
		boolean firstTime = true;
		
		long CachingSize = 0L;  
		
		int controlPanelHeight = 0;
		
		int videoWidth = 0;
		
		int videoHeight = 0;
		
		MediaLocator mrl = null;
		
		URL url;
		
		public CorePlayer()
		{
			root.printDebugMessage("Open a video player for the media file: " + mediafile.getName());
			
			// get the media locator for the file
			try 
			{
				if ((mrl = new MediaLocator(mediafile.toURL())) == null)
					root.printDebugMessage("Can't build the MediaLocator.");
			} catch (Exception e)
			{
				root.printDebugMessage("Can't build the MediaLocator.");
				return;
			}
			
			
			// create an instance of a player for this media
			try 
			{
				player = Manager.createPlayer(mrl);
			} catch (Exception e) 
			{
				root.printDebugMessage("Could not create player for " + mrl);
				return;
			}
			
			// Add ourselves as a listener for a player's events
			player.addControllerListener(this);
			
			root.printDebugMessage("player created.");
			
			// set the parent window title and closing adapter
			player.realize();
			
			while (playerInitFinished == false) 
			{
				try
				{
					Thread.sleep(10);
				}catch(Exception e){}
			}
			if (aspectRatio == -1.0f)
				MediaPlayer.this.setTitle("Audio Player: " + mediafile.getName());
			else
				MediaPlayer.this.setTitle("Video Player: " + mediafile.getName());
			player.prefetch();
		}
		
		public void finalize()
		{
			root.synchronizer.unregisterListener(MediaPlayer.this);
		}
		
		/** ControllerListener functions **/
		public synchronized void controllerUpdate(ControllerEvent event) 
		{
			// If we're getting messages from a dead player, just leave
			if (player == null)
				return;
			
			// When the player is Realized, get the visual 
			// and control components and add them to the Applet
			if (event instanceof RealizeCompleteEvent) {
				
				if (progressBar != null) 
				{
					MediaPlayer.this.panel.remove(progressBar);
					progressBar = null;
				}
				
				int width=320;
				
				int height=240;
				
				if (controlComponent == null)
				{
					if (( controlComponent = player.getControlPanelComponent()) != null) 
					{
						controlPanelHeight = controlComponent.getPreferredSize().height;
						
						MediaPlayer.this.panel.add("South",controlComponent);
						
						height += controlPanelHeight;
					}
				}
				if (visualComponent == null)
				{
					if (( visualComponent = player.getVisualComponent())!= null) 
					{				
						MediaPlayer.this.panel.add("Center",visualComponent);
						
						Dimension videoSize = visualComponent.getPreferredSize();
						
						videoWidth = videoSize.width;
						
						videoHeight = videoSize.height;
						
						width = videoWidth;
						
						height += videoHeight;
						
						visualComponent.setBounds(0, 0, videoWidth, videoHeight);
					}
				}
				
				MediaPlayer.this.panel.setBounds(0, 0, width, height);
				
				if (player.getVisualComponent()==null)
					MediaPlayer.this.aspectRatio = -1.0f; // indicates the the player is an audio player
				else 
					MediaPlayer.this.aspectRatio = (float)player.getVisualComponent().getWidth()/(float)player.getVisualComponent().getHeight();
				
				root.printDebugMessage("Aspect ratio for media window: " + MediaPlayer.this.aspectRatio);
				
				if (root.config.automaticalOrderWindows==true)
					root.placeSubwindows();
				
				if (controlComponent != null) 
					controlComponent.setBounds(0,videoHeight,width,controlPanelHeight);
				
				MediaPlayer.this.playerRealized = true;
				
				MediaPlayer.this.playerInitFinished = true;
				
			} 
			else if (event instanceof CachingControlEvent) 
			{
				if (player.getState() > Controller.Realizing)
					return;
				
				
				// Put a progress bar up when downloading starts, 
				// take it down when downloading ends.
				CachingControlEvent e = (CachingControlEvent) event;
				CachingControl cc = e.getCachingControl();
				
				// Add the bar if not already there ...
				if (progressBar == null) 
				{
					if ((progressBar = cc.getControlComponent()) != null) 
					{
						MediaPlayer.this.panel.add(progressBar);
						MediaPlayer.this.panel.setSize(progressBar.getPreferredSize());
					}
				}
			} 
			else if (event instanceof EndOfMediaEvent)
			{
				// We've reached the end of the media; rewind and start over
				player.setMediaTime(new Time(0));
				
				timeJustNowSet++;
				
				root.synchronizer.playerHasStopped(MediaPlayer.this.getTitle());
				
				//player.start();
			} 
			
			else if (event instanceof ControllerErrorEvent) 
			{
				root.printDebugMessage("No Supported Media Format. Remove and kill player window");
				
				root.removeSubWindow(getTitle());
				
				root.synchronizer.unregisterListener(MediaPlayer.this);
				
				MediaPlayer.this.playerRealized = false;
				
				MediaPlayer.this.playerInitFinished = true;
			}
			
			else if (event instanceof ControllerClosedEvent) 
			{
				MediaPlayer.this.panel.removeAll();
			} 
			
			else if (event instanceof StartEvent) 
			{
				if (startedJustFromOutside != 0)
					MediaPlayer.this.startedJustFromOutside--;
				else {
					root.printDebugMessage("Player START.");
					root.synchronizer.syncStartAllPlayers(MediaPlayer.this.getTitle(),player.getMediaTime());
				}
			} 
			
			else if (event instanceof StopEvent) 
			{
				if (!(event instanceof RestartingEvent))
				{
					if (stopedJustFromOutside != 0)
						MediaPlayer.this.stopedJustFromOutside--;
					else {
						root.printDebugMessage("Player STOP.");
						root.synchronizer.stopAllPlayers(MediaPlayer.this.getTitle());
					}
				}
				else
					startedJustFromOutside++;
			} 
			
			else if (event instanceof MediaTimeSetEvent) 
			{
				if (timeJustNowSet != 0)
					MediaPlayer.this.timeJustNowSet--;
				else
					root.synchronizer.sendTimeChange(MediaPlayer.this.getTitle(),player.getMediaTime());
			}
		}
	}
	
	// as follows the interface implementation of the InternalFrameListener
	public void internalFrameClosing(InternalFrameEvent e)
	{
		setDefaultCloseOperation(JInternalFrame.DO_NOTHING_ON_CLOSE);
		
		String title = e.getInternalFrame().getTitle();
		
		root.closeMultimediaWindow(title);
	}
	
	public void internalFrameClosed(InternalFrameEvent e){}
	
	public void internalFrameOpened(InternalFrameEvent e){}
	
	public void internalFrameIconified(InternalFrameEvent e){}
	
	public void internalFrameDeiconified(InternalFrameEvent e){}
	
	public void internalFrameActivated(InternalFrameEvent e){}
	
	public void internalFrameDeactivated(InternalFrameEvent e) {}
	
	// as follows the interface implementation of the componentlistener
	public void componentHidden(ComponentEvent e){}
	
	public void componentShown(ComponentEvent e){}
	
	public void componentMoved(ComponentEvent e)
	{
		if (root.config.automaticalOrderWindows == true)
		{
			root.printDebugMessage("Media Player Moved");
			root.placeSubwindows();
		}
	}
	
	public void componentResized(ComponentEvent e)
	{
		if (root.config.automaticalOrderWindows == true)
		{
			root.printDebugMessage("Media Player Resized");
			root.placeSubwindows();
		}
	}
	
	
	// function for the MediaTimeSynchronizeListener
	public void receiveMediaTimeChange(javax.media.Time time)
	{
		timeJustNowSet++;
		player.player.setMediaTime(time);
		
	}
	
	// jmf wrappers
	public int getPlayerState()
	{
		return player.player.getState();
	}
	
	public void prefetchPlayer()
	{
		player.player.prefetch();
	}
	
	public void stopPlayer()
	{
		stopedJustFromOutside++;
		
		player.player.stop();
		
		player.player.setStopTime(Clock.RESET);
	}
	
	public void syncStartPlayer(Time time) 
	{
		startedJustFromOutside++;
		
		receiveMediaTimeChange(time);
		player.player.start();
	}
	
	public void syncStartPlayerTimespan(Time starttime, Time endtime) 
	{
		
		startedJustFromOutside++;
		
		receiveMediaTimeChange(starttime);
		
		player.player.syncStart(starttime);
		
		player.player.setStopTime(endtime);
	}
	
	public Time getMediaTime()
	{
		return player.player.getMediaTime();
	}
}
