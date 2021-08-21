package de.dfki.ami.amigram.tools;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Vector;

import javax.media.Buffer;
import javax.media.ConfigureCompleteEvent;
import javax.media.ControllerEvent;
import javax.media.ControllerListener;
import javax.media.DataSink;
import javax.media.EndOfMediaEvent;
import javax.media.IncompatibleSourceException;
import javax.media.Manager;
import javax.media.MediaLocator;
import javax.media.PlugInManager;
import javax.media.PrefetchCompleteEvent;
import javax.media.Processor;
import javax.media.RealizeCompleteEvent;
import javax.media.ResourceUnavailableEvent;
import javax.media.SizeChangeEvent;
import javax.media.datasink.DataSinkErrorEvent;
import javax.media.datasink.DataSinkEvent;
import javax.media.datasink.DataSinkListener;
import javax.media.datasink.EndOfStreamEvent;
import javax.media.format.AudioFormat;
import javax.media.protocol.BufferTransferHandler;
import javax.media.protocol.ContentDescriptor;
import javax.media.protocol.DataSource;
import javax.media.protocol.PullBufferDataSource;
import javax.media.protocol.PullBufferStream;
import javax.media.protocol.PushBufferDataSource;
import javax.media.protocol.PushBufferStream;
import javax.media.protocol.SourceStream;

import de.dfki.ami.amigram.gui.DesktopFrame;

/**
 * Copyright (c) 2001 Christoph Lauer @ DFKI, All Rights Reserved.
 * clauer@dfki.de - www.dfki.de
 * <p>
 * This Class open a URL (i.e. file:c:\...) and reads ths sapmle informations out 
 * of the file.
 *
 * @author Christoph Lauer
 * @version 1.0, Current 05/12/2002
 */
public class SamplesReader implements ControllerListener, DataSinkListener 
{
    Processor            p;
    Object               waitSync          = new Object();
    boolean              stateTransitionOK = true;
    boolean              cancelIsPressed   = false;
    boolean              error             = false;
    public  String       dstype;
    public  Vector       audioStream       = new Vector();
    public  boolean      openAllRightFlag  = false;
    public  boolean      finished          = false; // This flag is setted by the Thread when he has finished the reading
    public  double       duration;
    public  int          samplerate        = 8000;
    public  DesktopFrame root;

    public  SamplesReader(DesktopFrame r)
    {
	root = r;
    }

    /**
     * Given a DataSource, create a processor and hook up the output
     * DataSource from the processor to a customed DataSink.
     */
    public boolean open(DataSource ds) {
//  	try{
// remove poss. old Elements
	audioStream.removeAllElements();
	dstype = ds.getContentType();
	root.printDebugMessage("SAMPLES READER --> Create JMF-PROCESSOR; Data type = " + dstype);
// Create PROCESSOR
	    try {
		p = Manager.createProcessor(ds);
	    } catch (Exception e) {
		root.printDebugMessage("SAMPLES READER --> EyRROR: create PROCESSOR: " + e);
		return false;
	    }
	    p.addControllerListener(this);	    
// Put the Processor into configured state.
	    p.configure();
	    if (!waitForState(p.Configured)) {
		root.printDebugMessage("SAMPLES REDER --> Error: configure the JMF-PROCESSORS");
		return false;
	    }
//Hier wird das Ausgabeformat des PROCESSORS Spezifiziert
	    javax.media.control.TrackControl traCont[] = p.getTrackControls();
	    for (int i=0;i<traCont.length;i++)
		{
		    if (traCont[i].getFormat() instanceof AudioFormat) {
			AudioFormat af = (AudioFormat) traCont[i].getFormat();
			// Set selected Samplingrate
			root.printDebugMessage("SAMPLES READER --> Files orginal audio format:" + af);
			traCont[i].setFormat(new AudioFormat("LINEAR",samplerate,8,1,0,1));
			AudioFormat newaf = (AudioFormat) traCont[i].getFormat();
			root.printDebugMessage("SAMPLES READER --> Format processed in:" + newaf);
		    }
		}
// Get the raw output from the processor.
	    p.setContentDescriptor(new ContentDescriptor(ContentDescriptor.RAW));
	    p.realize();
	    if (!waitForState(p.Realized)) {
		root.printDebugMessage("SAMPLES READER --> ERROR: the JMF-PROCESSOR cant convert this file");
		return false;
	    }
// Get the output DataSource from the processor and
// hook it up to the DataSourceHandler.
	    DataSource ods = p.getDataOutput();
	    DataSourceHandler handler = new DataSourceHandler();
	    try {
		handler.setSource(ods);
	    } catch (IncompatibleSourceException e) {
		root.printDebugMessage("SAMPLES READER --> ERROR: the JMF-PROCESSOR cant handel this datasource: " + ods);
		return false;
	    }
	    handler.addDataSinkListener(this);
	    handler.start();
// Prefetch the processor.
	    p.prefetch();
	    if (!waitForState(p.Prefetched)) {
		root.printDebugMessage("SAMPLES READER --> ERROR: prefetch audio data with JMF-PROCESSOR");
		return false;
	    }
// Start the processor.
	    root.printDebugMessage("SAMPLES READER --> Moment Please, reading Stream");
	    duration = p.getDuration().getSeconds();
	    root.printDebugMessage("SAMPLES READER --> Duration:" + duration + "secs.");
	    p.start();
	return true;
	    
    }
//---------------------------------------------------------------------------------------------------
    public void addNotify() {
    }
//---------------------------------------------------------------------------------------------------
    /**
     * Change the plugin list to disable the default RawBufferMux
     * thus allowing the RawSyncBufferMux to be used.
     * This is a handy trick.  You wouldn't know this, would you? :)
     */
    void enableSyncMux() {
	Vector muxes = PlugInManager.getPlugInList(null, null,
					PlugInManager.MULTIPLEXER);
	for (int i = 0; i < muxes.size(); i++) {
	    String cname = (String)muxes.elementAt(i);
	    if (cname.equals("com.sun.media.multiplexer.RawBufferMux")) {
		muxes.removeElementAt(i);
		break;
	    }
	}
	PlugInManager.setPlugInList(muxes, PlugInManager.MULTIPLEXER);
    }
//---------------------------------------------------------------------------------------------------
    /**
     * Block until the processor has transitioned to the given state.
     * Return false if the transition failed.
     */
    boolean waitForState(int state) {
	synchronized (waitSync) {
	    try {
		while (p.getState() < state && stateTransitionOK)
		    waitSync.wait();
	    } catch (Exception e) {
		root.printDebugMessage("SAMPLES READER --> ERROR while JMF transition");
	    }
	}
	return stateTransitionOK;
    }
//---------------------------------------------------------------------------------------------------
    /**
     * Controller Listener.
     */
    public void controllerUpdate(ControllerEvent evt) {

	if (evt instanceof ConfigureCompleteEvent ||
	    evt instanceof RealizeCompleteEvent ||
	    evt instanceof PrefetchCompleteEvent) {
	    synchronized (waitSync) {
		stateTransitionOK = true;
		waitSync.notifyAll();
	    }
	} else if (evt instanceof ResourceUnavailableEvent) {
	    synchronized (waitSync) {
		stateTransitionOK = false;
		waitSync.notifyAll();
	    }
	} else if (evt instanceof EndOfMediaEvent) {
	    p.close();
	} else if (evt instanceof SizeChangeEvent) {
	}
    }
//---------------------------------------------------------------------------------------------------
    /**
     * DataSink Listener
     */
    public void dataSinkUpdate(DataSinkEvent evt) {

	if (evt instanceof EndOfStreamEvent) {

	    if (cancelIsPressed == true) {
		openAllRightFlag = false;
		return;
	    }
	    openAllRightFlag = true;
	    root.printDebugMessage("SAMPLES READER --> Stream ausgelesen: " + audioStream.size() + " Samples");
	    evt.getSourceDataSink().close();
	    if (audioStream.size() == 0){
		return;
	    }
	    try {
		Thread.sleep(100);
	    } catch (Throwable t){}
	    // ALL SAMPLES ARE READED OUT
	    finished = true;
	}
    }
//---------------------------------------------------------------------------------------------------
//Konvertiert eine Byte Variable bitweise in eine Integer Variable (Vorzeichen,bits....)
    private synchronized int copyByteToIntBitwise(byte b){
	int i=0;
	if (b > 0) i = b;
	if (b < 0) {
	    i = ~b;
	    i += 128;
	}
	return i;
    }
//====================================================================================================
/***************************************************
 * Inner class
 ***************************************************/
/**
  * This DataSourceHandler class reads from a DataSource and display
  * information of each frame of data received.
  */
//---------------------------------------------------------------------------------------------------
    class DataSourceHandler implements DataSink, BufferTransferHandler {
	DataSource source;
	PullBufferStream pullStrms[] = null;
	PushBufferStream pushStrms[] = null;
	// Data sink listeners.
	private Vector listeners = new Vector(1);
	// Stored all the streams that are not yet finished (i.e. EOM
	// has not been received.
	SourceStream unfinishedStrms[] = null;
	// Loop threads to pull data from a PullBufferDataSource.
	// There is one thread per each PullSourceStream.
	Loop loops[] = null;
	Buffer readBuffer;
//---------------------------------------------------------------------------------------------------
	/**
	 * Sets the media source this <code>MediaHandler</code>
	 * should use to obtain content.
	 */
	public void setSource(DataSource source) throws IncompatibleSourceException {

	    // Different types of DataSources need to handled differently.
	    if (source instanceof PushBufferDataSource) {
		pushStrms = ((PushBufferDataSource)source).getStreams();
		unfinishedStrms = new SourceStream[pushStrms.length];
		// Set the transfer handler to receive pushed data from
		// the push DataSource.
		for (int i = 0; i < pushStrms.length; i++) {
		    pushStrms[i].setTransferHandler(this);
		    unfinishedStrms[i] = pushStrms[i];
		}
	    } else if (source instanceof PullBufferDataSource) {
		pullStrms = ((PullBufferDataSource)source).getStreams();
		unfinishedStrms = new SourceStream[pullStrms.length];
		// For pull data sources, we'll start a thread per
		// stream to pull data from the source.
		loops = new Loop[pullStrms.length];
		for (int i = 0; i < pullStrms.length; i++) {
		    loops[i] = new Loop(this, pullStrms[i]);
		    unfinishedStrms[i] = pullStrms[i];
		}
	    } else {
		// This handler only handles push or pull buffer datasource.
		throw new IncompatibleSourceException();
	    }
	    this.source = source;
	    readBuffer = new Buffer();
	}
//---------------------------------------------------------------------------------------------------
	/**
	 * For completeness, DataSink's require this method.
	 * But we don't need it.
	 */
	public void setOutputLocator(MediaLocator ml) {
	}
//---------------------------------------------------------------------------------------------------
	public MediaLocator getOutputLocator() {
	    return null;
	}
//---------------------------------------------------------------------------------------------------
	public String getContentType() {
	    return source.getContentType();
	}
//---------------------------------------------------------------------------------------------------
	/**
	 * Our DataSink does not need to be opened.
	 */
	public void open() {
	}
//---------------------------------------------------------------------------------------------------
	public void start() {
	    try {
		source.start();
	    } catch (IOException e) {
		root.printDebugMessage("SAMPLES READER --> ERROR while start source in JMF-PROCESSOR");
	    }
	    // Start the processing loop if we are dealing with a
	    // PullBufferDataSource.
	    if (loops != null) {
		for (int i = 0; i < loops.length; i++){
		    loops[i].restart();
		}
	    }
	}
//---------------------------------------------------------------------------------------------------
	public void stop() {
	    try {
		source.stop();
	    } catch (IOException e) {		
		root.printDebugMessage("SAMPLES READER --> ERROR while stop source in JMF-PROCESSOR");
	    }
	    // Start the processing loop if we are dealing with a
	    // PullBufferDataSource.
	    if (loops != null) {
		for (int i = 0; i < loops.length; i++)
		    loops[i].pause();
	    }
	}
//---------------------------------------------------------------------------------------------------
	public void close() {
	    stop();
	    if (loops != null) {
		for (int i = 0; i < loops.length; i++)
		    loops[i].kill();
	    }
	}
//---------------------------------------------------------------------------------------------------
       	public void addDataSinkListener(DataSinkListener dsl) {
	    if (dsl != null)
		if (!listeners.contains(dsl))
		    listeners.addElement(dsl);
	}
//---------------------------------------------------------------------------------------------------
	public void removeDataSinkListener(DataSinkListener dsl) {
	    if (dsl != null)
		listeners.removeElement(dsl);
	}
//---------------------------------------------------------------------------------------------------
	protected void sendEvent(DataSinkEvent event) {
	    if (!listeners.isEmpty()) {
		synchronized (listeners) {
		    Enumeration list = listeners.elements();
		    while (list.hasMoreElements()) {
			DataSinkListener listener =
				(DataSinkListener)list.nextElement();
			listener.dataSinkUpdate(event);
		    }
		}
	    }
	}
//---------------------------------------------------------------------------------------------------
	/**
	 * This will get called when there's data pushed from the
	 * PushBufferDataSource.
	 */
	public synchronized void transferData(PushBufferStream stream) {
	    try {
		stream.read(readBuffer);
	    } catch (IOException e) {
		sendEvent(new DataSinkErrorEvent(this, e.getMessage()));
		root.printDebugMessage("SAMPLES READER --> ERROR while read buffer by JMF-PROCESSOR");
		return;
	    }
	    printDataInfo(readBuffer);
	    // Check to see if we are done with all the streams.
	    if (readBuffer.isEOM() && checkDone(stream)) {
		sendEvent(new EndOfStreamEvent(this));
	    }
	}
//---------------------------------------------------------------------------------------------------
	/**
	 * This is called from the Loop thread to pull data from
	 * the PullBufferStream.
	 */
	public synchronized boolean readPullData(PullBufferStream stream) {
	    try {
		stream.read(readBuffer);
	    } catch (IOException e) {
		root.printDebugMessage("SAMPLES READER --> ERROR while read buffer by JMF-PROCESSOR");
		return true;
	    }
	    printDataInfo(readBuffer);
	    if (readBuffer.isEOM()) {
	        // Check to see if we are done with all the streams.
		if (checkDone(stream)) {
		    root.printDebugMessage("SAMPLES READER: All done!");
		    close();
		}
		return true;
	    }
	    return false;
	}
//---------------------------------------------------------------------------------------------------
	/**
	 * Check to see if all the streams are processed.
	 */
	public boolean checkDone(SourceStream strm) {
	    boolean done = true;

	    for (int i = 0; i < unfinishedStrms.length; i++) {
		if (strm == unfinishedStrms[i])
		    unfinishedStrms[i] = null;
		else if (unfinishedStrms[i] != null) {
		    // There's at least one stream that's not done.
		    done = false;
		}
	    }
	    return done;
	}
//---------------------------------------------------------------------------------------------------
	/**
	 * Writes Data in on Vektor
	 */
	public synchronized void printDataInfo(Buffer buffer) {
	    try {
		if (buffer.getFormat() instanceof AudioFormat) {
		    byte[] buf = (byte[]) buffer.getData();
		    for (int i=0;i<(buffer.getLength());i++) {             // Copy Data To Buffer
			try {
			    audioStream.add(new Byte(buf[i]));
			}
			catch (Throwable t) {
			    root.printDebugMessage("SAMPLES READER: --> Error while reading Samples" + t);
			    if (error == true)
				return;
			    error = true;
			}
		    }
		}
	    } catch (Exception e) {
		root.printDebugMessage("SAMPLES READER: --> Error while reading Samples out." + e);
		e.printStackTrace();
		return;
	    }
	}
//---------------------------------------------------------------------------------------------------
	public Object [] getControls() {
	    return new Object[0];
	}
//---------------------------------------------------------------------------------------------------
	public Object getControl(String name) {
	    return null;
	}
//---------------------------------------------------------------------------------------------------
    }
//====================================================================================================
 //====================================================================================================
    /**
     * A thread class to implement a processing loop.
     * This loop reads data from a PullBufferDataSource.
     */
//---------------------------------------------------------------------------------------------------
    class Loop extends Thread {

	DataSourceHandler handler;
	PullBufferStream stream;
	boolean paused = true;
	boolean killed = false;
//---------------------------------------------------------------------------------------------------
	public Loop(DataSourceHandler handler, PullBufferStream stream) {
	    this.handler = handler;
	    this.stream = stream;
	    start();
	}
//---------------------------------------------------------------------------------------------------
	public synchronized void restart() {
	    paused = false;
	    notify();
	}
//---------------------------------------------------------------------------------------------------
	/**
	 * This is the correct way to pause a thread; unlike suspend.
	 */
	public synchronized void pause() {
	    paused = true;
	}
//---------------------------------------------------------------------------------------------------
	/**
	 * This is the correct way to kill a thread; unlike stop.
	 */
	public synchronized void kill() {
	    killed = true;
	    notify();
	}
//---------------------------------------------------------------------------------------------------
	/**
	 * This is the processing loop to pull data from a
	 * PullBufferDataSource.
	 */
	public void run() {
	    while (!killed) {
		try {
		    while (paused && !killed) {
			wait();
		    }
		} catch (InterruptedException e) {}

		if (!killed) {
		    boolean done = handler.readPullData(stream);
		    if (done)
			pause();
		}
	    }
	}
//---------------------------------------------------------------------------------------------------
    }
//====================================================================================================

    public boolean generateSamplesFromURL(String url) {
	String filename = "";
	try {
	    filename = (new java.io.File ((new URL(url)).getFile()).getName());
	    
	} catch (Throwable t) {}
// Medialocator from URL
	MediaLocator ml;
	if ((ml = new MediaLocator(url)) == null) {
	    root.printDebugMessage("SAMPLES READER--> ERROR: Cant Build MediaLocator: " + url);
	    return false;
	}
	DataSource ds = null;
// Create a DataSource given the media locator.
	try {
	    ds = Manager.createDataSource(ml);
	} catch (Exception e) {
	    root.printDebugMessage("SAMPLES READER: --> URL does not exist: " + ml);
	    return false;
	}
// Call to open in this class
	return (open(ds));
    }
//---------------------------------------------------------------------------------------------------
}

