/******************************************************************
 *                 InfoTable.java  -  description
 *                    -----------------------
 * @author 		  Christoph Lauer
 * @version 		  0.1  
 * @see			  www.amiproject.de
 * copyright		: (C) 2003 @ christoph lauer
 * begin 		: Thu Okt 30 17:00:00 CET 2003
 * last save   	        : Time-stamp: <05/08/03 15:28:24 clauer> 
 * compiler    	        : java version 1.4.2 
 * operating system     : Linux 
 * editor		: xemacs 21.4
 * description          : Tab for the Control window.
 ******************************************************************/

package de.dfki.ami.amigram.tools;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import de.dfki.ami.amigram.gui.DesktopFrame;

public class InfoTable
    extends JPanel
{
    private DesktopFrame root;
    private Object[][] data = new Object[25][2];
    public InfoTable(DesktopFrame r)
    {
	root = r;
	data[0][0] = "Number of SignalS:";
	data[1][0] = "Accessible Signals";
	data[2][0] = "Number of Layers:";
	data[3][0] = "Number of Elements:";
	data[4][0] = "Number of Observations:";
	data[5][0] = "Number of Agents:";
	data[6][0] = "Number of Codings:";
	data[7][0] = "Open Time:";
	data[8][0] = "Last Save:";
	data[9][0] = "Audio Samplerate:";
	data[10][0] = "Audio Format:";
	data[11][0] = "Audio Bits per Sample";
	data[12][0] = "Audio Channels";
	data[13][0] = "Corpus Filename:";
	data[14][0] = "Corpus Path:";
	data[15][0] = "Annotations Loaded:";
	data[16][0] = "Corpus Duration";
	data[17][0] = "";
	data[18][0] = "";
	data[19][0] = "";
	data[20][0] = "";
	data[21][0] = "";
	data[22][0] = "";
	data[23][0] = "";
	data[24][0] = "";
        String[] columnNames = {"Name","Value"};
	JTable table = new JTable(data, columnNames);
        //table.setPreferredScrollableViewportSize(new Dimension(270,230));
	table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
	table.setDefaultRenderer(Object.class,new ColoredTableCellRenderer());
	TableColumn column = null;
	for (int i = 0; i < 2; i++) {
	    column = table.getColumnModel().getColumn(i);
	    if (i == 0) 
		column.setPreferredWidth(100); 
	    if (i == 1) 
		column.setPreferredWidth(130);
	}
	setLayout(new GridLayout(1,1));
	JScrollPane sp = new JScrollPane(table);
add(sp);
    }
    /**
     * updateData is called from any class that changed the values that displayed here.
     */
    public void updateData() {
	if (root.nomcommunicator.metadata.getSignals() == null) data[0][1] = "----";
	else data[0][1] = root.nomcommunicator.metadata.getSignals().size()+" Signals";
	data[1][1] = root.nomcommunicator.existingSignals+" Signals";
	data[3][1] = root.nomcommunicator.annohashmap.size()+" Elements";
	data[2][1] = root.nomcommunicator.layercount+" Layers";
	if (root.nomcommunicator.observations == null) data[4][1] = "----";
	else data[4][1] = root.nomcommunicator.observations.size()+" Observations";
	if (root.nomcommunicator.agents == null) data[5][1] = "----";
	else data[5][1] = root.nomcommunicator.agents.size()+" Agents";
	if (root.nomcommunicator.codings == null) data[6][1] = "----";
	else data[6][1] = root.nomcommunicator.codings.size()+" Codings";
	data[7][1] = root.nomcommunicator.openTime;
	data[8][1] = root.nomcommunicator.saveTime;

	if (root.nomcommunicator.getCorpusFilePath() == null) 
	    data[13][1] = data[14][1] = "  -----";
	else {
	    data[13][1] = root.nomcommunicator.getCorpusFileName();
	    data[14][1] = root.nomcommunicator.getCorpusFilePath();
	    data[15][1] = root.nomcommunicator.sumoffoundedannotationelements+" Elements";
	    data[16][1] = root.nomcommunicator.nomwritecorpus.getCorpusDuration() + " sec.";
	}
    }
}

class ColoredTableCellRenderer
    implements TableCellRenderer
{
    private Color lightBlue = new Color(170, 170, 255);
    private Color middleBlue = new Color(100, 100, 200);
    private Color darkBlue  = new Color( 64,  64, 128);
    public Component getTableCellRendererComponent(
						   JTable table,
						   Object value,
						   boolean isSelected,
						   boolean hasFocus,
						   int row,
						   int column
						   )
    {   
	JLabel label = new JLabel((String)value);
	{
	    label.setOpaque(true);
	    Border b = BorderFactory.createEmptyBorder(1,1,1,1);
	    label.setFont(table.getFont());
	    if (column == 1) {
		label.setBackground(lightBlue);
		label.setForeground(Color.black);
	    }
	    if (column == 0) {
		label.setBackground(middleBlue);
		label.setForeground(Color.black);
	    }
	}
	return label;
    }
}
