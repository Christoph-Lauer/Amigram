package de.dfki.ami.amigram.tools;

import java.awt.Color;
import java.util.Collection;
import java.util.Iterator;

import de.dfki.ami.amigram.gui.DesktopFrame;
import de.dfki.ami.amigram.gui.OtabFrame;
import de.dfki.ami.amigram.multimedia.MediaPlayer;

/******************************************************************
 *          MediaTimeSynchronizer.java  -  description
 *                    -----------------------
 * @author 		  Christoph Lauer
 * @version 		  0.1  
 * @see			  www.amiproject.de
 * copyright		: (C) 2003 @ christoph lauer
 * begin 		: Fri may 21 17:00:00 CET 2003
 * last save   	        : Time-stamp: <05/08/09 12:21:19 clauer> 
 * compiler    	        : java version 1.4.2 
 * operating system     : Linux 
 * editor		: xemacs 21.4
 * description          : This class manages the clients which 
 *                        needs to be synchronized with media 
 *                        time information.
 ******************************************************************/
public class MediaTimeSynchronizer extends Thread
{
	private DesktopFrame root;
	
	private javax.media.Time currenttime;
	
	private int playSynchInterval = 40;   // How often the Timeline is Synchronizen (time)
	
	private boolean playerStarted = false;
	
	private String  playerStartedID = new String("");
	
	public MediaTimeSynchronizer(DesktopFrame r)
	{
		root = r;
		setPriority(Thread.MAX_PRIORITY);
		start();
	}
	
	/** Use a weak Hash map here. WeakHashMaps check autonomous if the elements
	 in the map exist and remove them if necessary **/
	private java.util.WeakHashMap hashmap = new java.util.WeakHashMap();
	
	/** this class informs all other listeners (exclude the sender) that the media time has changed **/
	public void sendTimeChange(String senderId,javax.media.Time time)
	{
		if (root.config.synchronizeMediaTimeChanges == false || hashmap.size() < 2)
			return;
		
		currenttime = time;
		
		root.printDebugMessage(senderId + " sends media time change to " + time.getSeconds() + " seconds");
		
		Collection alllisteners = hashmap.values();
		
		Iterator i = alllisteners.iterator();
		
		while(i.hasNext())
		{
			Object object = i.next();
			if (object.toString().equals(root.getOtabWindow().toString()))
			{
				if (senderId.equals("OTAB"))
					continue;
				OtabFrame otab = (OtabFrame)object;
				otab.receiveMediaTimeChange(time);
				continue;
			}
			
			MediaPlayer mp = (MediaPlayer)object;
			
			if (mp.getPlayerState() == javax.media.Controller.Started)
				mp.prefetchPlayer();
			
			// Start not the player which has invoked the sync start event
			if (!mp.getTitle().equals(senderId))
			{
				mp.receiveMediaTimeChange(time);
			}
		}
	}
	
	/** how the name says, this procedure stops all players **/
	public void stopAllPlayers(String senderId)
	{
		root.statusbar.updateStatusText("Player Stopped.");
		
		if (root.config.synchronizeMediaStartStopEvents == false || hashmap.size() < 2)
			return;
		
		playerStarted = false;
		
		root.printDebugMessage("STOP all players event send from " + senderId);
		
		Collection alllisteners = hashmap.values();
		
		Iterator i = alllisteners.iterator();
		
		while(i.hasNext())
		{
			Object object = i.next();
			if (object.toString().substring(0,30).equals(root.getOtabWindow().toString().substring(0,30)))
				continue;
			
			MediaPlayer mp = (MediaPlayer)object;
			
			// Stop not the player which has invoked the stop event
			if (mp.getTitle().equals(senderId))
				continue;
			
			mp.stopPlayer();
		}
	}
	
	/** This function starts all players with the given timebase. **/
	public void syncStartAllPlayers(String senderId,javax.media.Time time)
	{
		root.statusbar.updateStatusText("Player Started.");
		
		if (root.config.synchronizeMediaStartStopEvents == false || hashmap.size() < 2)
			return;
		
		playerStarted = true;
		
		playerStartedID = senderId;
		
		root.printDebugMessage("START all players event send from " + senderId);
		
		Collection alllisteners = hashmap.values();
		
		Iterator i = alllisteners.iterator();
		
		while(i.hasNext())
		{
			Object object = i.next();
			
			if (object.toString().substring(0,30).equals(root.getOtabWindow().toString().substring(0,30)))
				continue;
			
			MediaPlayer mp = (MediaPlayer)object;
			
			if (!mp.getTitle().equals(senderId))
			{
				
				if (mp.getPlayerState() == javax.media.Controller.Started)
					mp.stopPlayer();
				
				mp.syncStartPlayer(time);
			}
		}
	}
	
	/** plays a time with all players **/
	public void playTimeSpan(javax.media.Time starttime,javax.media.Time endtime)
	{
		root.statusbar.updateStatusText("Player Started.");
		
		stopAllPlayers(null);
		
		playerStarted = true;
		
		Collection alllisteners = hashmap.values();
		
		Iterator i = alllisteners.iterator();
		
		while(i.hasNext())
		{
			Object object = i.next();
			
			if (object.toString().substring(0,30).equals(root.getOtabWindow().toString().substring(0,30)))
				continue;
			
			MediaPlayer mp = (MediaPlayer)object;
			
			mp.stopPlayer();
			
			mp.syncStartPlayerTimespan(starttime,endtime);
		}
	} 
	
	/** Resgisters a listener. He will be informed if any other listener or other tread 
	 calls the sendTimeChange function. **/
	public void registerListener(MediaTimeSynchronizeListener listener,String id)
	{
		hashmap.put(id,listener);
		root.printDebugMessage(id + " ADDED to the media time synchronizer.");
		if (currenttime != null)
			listener.receiveMediaTimeChange(currenttime);
	}
	
	public void unregisterListener(MediaTimeSynchronizeListener listener)
	{
		hashmap.remove(((MediaPlayer)listener).getTitle());
		root.printDebugMessage(((MediaPlayer)listener).getTitle()+" REMOVED from the media time synchronizer.");
	}
	
	// needed to find out if the player who has started is now stopped for the timeline synchronisation.
	public void playerHasStopped(String senderID)
	{
		if (senderID.equals(playerStartedID))
			playerStarted = false;
	}
	
public void run()
    {
	for (;;) {  // The endless loop for the thread
	    if (root.config != null)
		if (root.config.synchronizeTimelinePlaying==true && playerStarted==true)
		    {
			OtabFrame otab=null;
			MediaPlayer mp=null;
			MediaPlayer mptmp=null;
			Collection alllisteners = hashmap.values();
			Iterator i = alllisteners.iterator();
			while(i.hasNext()) // search for the current playing instance
			    {
				Object object = i.next();
				if (!object.toString().substring(0,30).equals(root.getOtabWindow().toString().substring(0,30)))
				    {
					mptmp = (MediaPlayer)object;
					if (playerStartedID.equals(mptmp.getTitle()))
					    {
						mp=mptmp;
						mp.setForeground(new Color(255,0,0));
						mp.setBackground(new Color(255,0,0));
					    }
				    }
				else
				{
					otab = (OtabFrame)object;
					continue;
				}
			    }
			
			if (mp!=null) 
			    {
				root.getOtabWindow().receiveMediaTimeChange(mp.getMediaTime());

				try 
				    {
					root.getOtabWindow().setSelected(false);
				    }
				catch(Exception e)
				    {
				    }
			    }
		    }
	    try {Thread.sleep(playSynchInterval);}
	    catch (InterruptedException e) {}
	}
    }

	
	/** returns the current playing player, else null */
	public MediaPlayer getCurrentPlayer()
	{
		if (playerStarted == false)
			return null;
		
		Collection alllisteners = hashmap.values();
		
		Iterator i = alllisteners.iterator();
		
		while(i.hasNext())
		{
			Object object = i.next();
			
			if (object.toString().substring(0,30).equals(root.getOtabWindow().toString().substring(0,30)))
				continue;
			
			MediaPlayer mp = (MediaPlayer)object;
			
			if (mp.getTitle().equals(playerStartedID))
				return mp;
		}
		return null;
	}
}
