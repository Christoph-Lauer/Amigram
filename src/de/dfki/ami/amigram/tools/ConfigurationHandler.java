/******************************************************************
 *               ConfigurationHandler.java  -  description
 *                    -----------------------
 * @author 		  Christoph Lauer
 * @version 		  0.1  
 * @see			  www.amiproject.de
 * copyright		: (C) 2003 @ christoph lauer
 * begin 		: Thu Okt 30 17:00:00 CET 2003
 * last save   	        : Time-stamp: <05/06/15 15:43:46 clauer> 
 * compiler    	        : java version 1.4.2 
 * operating system     : Linux 
 * editor		: xemacs 21.4
 * description          : Handles the global configuration varables
 *                        and the storing and reading to/from the 
 *                        configuration file. Supported Typs are
 *                        boolean and integer.
 ******************************************************************/

package de.dfki.ami.amigram.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import de.dfki.ami.amigram.gui.DesktopFrame;

public class ConfigurationHandler 
{
    private String configFilePath = new String("Amigram.config"); // The name of the configration
    private File   configFile;
    private DesktopFrame root;

    /**********************************************************/
    /** as follows the managed parameters and default values **/
    /**********************************************************/
    public boolean fullScreen                       = false;
    public boolean automaticalOrderWindows          = true;
    public boolean synchronizeMediaTimeChanges      = true;
    public boolean synchronizeMediaStartStopEvents  = true;
    public boolean synchronizeTimeline              = true;
    public boolean synchronizeTimelineContinuously  = false;
    public boolean synchronizeTimelinePlaying       = true;
    public boolean recognizeElementsWhilePlaying    = true;
    public boolean autoGenerateOszilloskop          = false;
    public boolean autoGenerateSpectrum             = false;
    public boolean openDialog                       = true;
    public boolean secoundsInTimelineal             = false;
    public boolean automaticalOrderLayers           = false;
    public boolean onTheFlyAnnotation               = true;
    public int     reaktionTimeOTF                  = 600;
    public int     zoom                             = 400;
    public boolean autoZoom                         = true;
    public String  plugin                           = "SimplePlugin (the default plugin)";

    public ConfigurationHandler(DesktopFrame r) 
    {
	root = r;
	// instantiate the file
	try {configFile = new File(configFilePath);}
	catch (Exception e){root.printDebugMessage("Error while get the Config File.");System.exit(0);}
	if (configFile.exists() == false)
	    {
		root.printDebugMessage("Can't find the configuration file: "+configFile+"  Use the default values and create an new one.");
		writeConfigurationFile();
	    }
	else 
	    readConfigurationFile();
    }
    
    /** reads the configuration from the configuration file **/
    public void readConfigurationFile() 
    {
	root.printDebugMessage("Read configuration variables from config file: "+configFilePath);
	try
	    {
		BufferedReader br = new BufferedReader(new FileReader(configFile));
		String line="";
		while((line=br.readLine())!=null)
		    {   
			// go throught the configuration file lines
			if (line.matches(".*automaticalOrderWindows.*=.*")==true)
			    automaticalOrderWindows=getBooleanValue(line);
			else if (line.matches(".*reaktionTimeOTF.*=.*")==true)
			    reaktionTimeOTF=getIntValue(line);
			else if (line.matches(".*fullScreen.*=.*")==true)
			    fullScreen=getBooleanValue(line);
			else if (line.matches(".*synchronizeMediaTimeChanges.*=.*")==true)
			    synchronizeMediaTimeChanges=getBooleanValue(line);
			else if (line.matches(".*synchronizeMediaStartStopEvents.*=.*")==true)
			    synchronizeMediaStartStopEvents=getBooleanValue(line);
			else if (line.matches(".*synchronizeTimelineContinuously.*=.*")==true)
			    synchronizeTimelineContinuously=getBooleanValue(line);
			else if (line.matches(".*synchronizeTimelinePlaying.*=.*")==true)
			    synchronizeTimelineContinuously=getBooleanValue(line);
			else if (line.matches(".*synchronizeTimeline.*=.*")==true)
			    synchronizeTimeline=getBooleanValue(line);
			else if (line.matches(".*recognizeElementsWhilePlaying.*=.*")==true)
			    recognizeElementsWhilePlaying=getBooleanValue(line);
			else if (line.matches(".*autoGenerateOszilloskop.*=.*")==true)
			    autoGenerateOszilloskop=getBooleanValue(line);
			else if (line.matches(".*autoGenerateSpectrum.*=.*")==true)
			    autoGenerateSpectrum=getBooleanValue(line);
			else if (line.matches(".*autoOpenDialog.*=.*")==true)
			    openDialog=getBooleanValue(line);
			else if (line.matches(".*secoundsInTImelineal.*=.*")==true)
			    secoundsInTimelineal=getBooleanValue(line);
			else if (line.matches(".*automaticalOrderLayers.*=.*")==true)
			    automaticalOrderLayers=getBooleanValue(line);
			else if (line.matches(".*onTheFlyAnnotation.*=.*")==true)
			    onTheFlyAnnotation=getBooleanValue(line);
			else if (line.matches(".*zoom.*=.*")==true)
			    zoom=getIntValue(line);
			else if (line.matches(".*autoZoom.*=.*")==true)
			    autoZoom=getBooleanValue(line);
			else if (line.matches(".*plugIn.*=.*")==true)
			    plugin=getStringValue(line);
			
			else 
			    root.printDebugMessage("Unknown configuration file entry: "+line);
		    }
	    }catch(Exception e){root.printDebugMessage("Error while read the configuration file.");e.printStackTrace();}
    }
    /** extracts the integer value from an line of the configuration file **/
    private int getIntValue(String string)
    {
	root.printDebugMessage("Parse configuration file line: "+string);
	try 
	    {
		String valuestring = string.substring(string.indexOf("=")+1).trim();
		Integer integer = Integer.valueOf(valuestring);
		return integer.intValue();
	    }
	catch (Exception e)
	    {
		root.printDebugMessage("Error while parse the integer string: "+string);
		e.printStackTrace();
		return 0;
	    }
    }
    /** extracts the boolean value from an line of the configuration file **/
    private boolean getBooleanValue(String string)
    {
	root.printDebugMessage("Parse configuration file line: "+string);
	if (string.matches(".*true.*")==true) return true;
	else if (string.matches(".*false.*")==true) return false;
	else {root.printDebugMessage("Error while parse an boolean string: "+string);return false;}
    }
    /** extracts the String value from an line of the configuration file **/
    private String getStringValue(String string)
    {
	root.printDebugMessage("Parse configuration file line: "+string);
	return (string.substring(2+string.indexOf("=")));
    }
    /** writes the actual configuration into the configuration file **/
    public void writeConfigurationFile() 
    {
	// synchronize values
	if (root.ctrlwin != null)
	    reaktionTimeOTF = ((Integer)root.ctrlwin.reaktiontime.getValue()).intValue();
	// write the configuration
	root.printDebugMessage("Write configuration variables to config file: "+configFilePath);
	printConfiguration();
	try {configFile.createNewFile();}
	catch(Exception e){root.printDebugMessage("Error while create new configuration file.");}
	try 
	    {
		String fileContent = new String();
		fileContent += "fullScreen = "+fullScreen;
		fileContent += "\nautomaticalOrderWindows = "+automaticalOrderWindows;
		fileContent += "\nsynchronizeMediaTimeChanges = "+synchronizeMediaTimeChanges;
		fileContent += "\nsynchronizeMediaStartStopEvents = "+synchronizeMediaStartStopEvents;
		fileContent += "\nsynchronizeTimelinePlaying = "+synchronizeTimelineContinuously;
		fileContent += "\nsynchronizeTimelineContinuously = "+synchronizeTimelineContinuously;
		fileContent += "\nsynchronizeTimeline = "+synchronizeTimeline;
		fileContent += "\nrecognizeElementsWhilePlaying = "+recognizeElementsWhilePlaying;
		fileContent += "\nautoGenerateOszilloskop = "+autoGenerateOszilloskop;
		fileContent += "\nautoGenerateSpectrum = "+autoGenerateSpectrum;
		fileContent += "\nautoOpenDialog = "+openDialog;
		fileContent += "\nsecoundsInTImelineal = "+secoundsInTimelineal;
		fileContent += "\nautomaticalOrderLayers = "+automaticalOrderLayers;
		fileContent += "\nreaktionTimeOTF = "+reaktionTimeOTF;
		fileContent += "\nOnTheFlyAnnotation = "+onTheFlyAnnotation;
		fileContent += "\nzoom = "+zoom;
		fileContent += "\nautoZoom = "+autoZoom;
		fileContent += "\nplugIn = "+plugin;
		FileWriter fw = new FileWriter(configFile);
		fw.write(fileContent);
		fw.flush();
		fw.close();            
            }
	catch(Exception e){root.printDebugMessage("Can't write content to the configuration file.");}
    }

    /** prints out the current configuration to the console **/
    public void printConfiguration()
    {
	root.printDebugMessage("--------- begin trace configuration variables ---------");
	root.printDebugMessage("(Variable) fullScreen                      : (Value)"+fullScreen);
	root.printDebugMessage("(Variable) automaticalOrderWindows         : (Value)"+automaticalOrderWindows);
	root.printDebugMessage("(Variable) synchronizeMediaTimeChanges     : (Value)"+synchronizeMediaTimeChanges);
	root.printDebugMessage("(Variable) synchronizeMediaStartStopEvents : (Value)"+synchronizeMediaStartStopEvents);
	root.printDebugMessage("(Variable) synchronizeTimeline             : (Value)"+synchronizeTimeline);
	root.printDebugMessage("(Variable) synchronizeTimelineContinuously : (Value)"+synchronizeTimelineContinuously);
	root.printDebugMessage("(Variable) synchronizeTimelinePlaying      : (Value)"+synchronizeTimelinePlaying);
	root.printDebugMessage("(Variable) recognizeElementsWhilePlaying   : (Value)"+recognizeElementsWhilePlaying);
	root.printDebugMessage("(Variable) autoGenerateOszilloskop         : (Value)"+autoGenerateOszilloskop);
	root.printDebugMessage("(Variable) autoGenerateSpectrum            : (Value)"+autoGenerateSpectrum);
	root.printDebugMessage("(Variable) autoOpenDialog                  : (Value)"+openDialog);
	root.printDebugMessage("(Variable) secoundsInTimelineal            : (Value)"+secoundsInTimelineal);
	root.printDebugMessage("(Variable) automaticalOrderLayers          : (Value)"+automaticalOrderLayers);
	root.printDebugMessage("(Variable) onTheFlyAnnotation              : (Value)"+onTheFlyAnnotation);
	root.printDebugMessage("(Variable) reaktionTimeOTF                 : (Value)"+reaktionTimeOTF);
	root.printDebugMessage("(Variable) zoom                            : (Value)"+zoom);
	root.printDebugMessage("(Variable) autoZoom                        : (Value)"+autoZoom);
	root.printDebugMessage("(Variable) plugIn                          : (Value)"+plugin);
	root.printDebugMessage("--------- end trace configuration variables   ---------");
    }
}
