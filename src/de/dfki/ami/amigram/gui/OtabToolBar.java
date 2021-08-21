/**
 * @(#)nite.gram.OtabToolBarOne.java
 * @author Christoph Lauer
 * @version 0.1,  14/08/2002
 *
 * Copyright (c) 2001 Christoph Lauer @ DFKI, All Rights Reserved.
 * clauer@dfki.de - www.dfki.de
 *
 * The toolbar for <i>GRAM</i>
 */

package de.dfki.ami.amigram.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import de.dfki.ami.amigram.tools.AnnotationLayer;

public class OtabToolBar extends JToolBar implements ActionListener
{
    private DesktopFrame root;
    
    public JButton zoomin;
    public JButton zoomout;
    public JButton up;
    public JButton down;
    public JButton add;
    public JButton del;
    public JButton spec;
    public JButton osca;
    public JButton remo;
    
   
    public OtabToolBar(DesktopFrame r)
    {
	root = r;
	setOrientation(JToolBar.VERTICAL);

	IconManager iconManager = IconManager.getInstance();
	zoomin = new JButton((ImageIcon)iconManager.getIcon(IconManager.ZOOMIN));
	zoomout = new JButton((ImageIcon)iconManager.getIcon(IconManager.ZOOMOUT));
	up = new JButton((ImageIcon)iconManager.getIcon(IconManager.UP));
	down = new JButton((ImageIcon)iconManager.getIcon(IconManager.DOWN));
	add = new JButton((ImageIcon)iconManager.getIcon(IconManager.ADD));
	del = new JButton((ImageIcon)iconManager.getIcon(IconManager.DEL));
	spec = new JButton((ImageIcon)iconManager.getIcon(IconManager.SPEC));
	osca = new JButton((ImageIcon)iconManager.getIcon(IconManager.OSCA));
	remo = new JButton((ImageIcon)iconManager.getIcon(IconManager.REMO));
	
	this.addButtons();
    }
    private void addButtons()
    {
	this.add(zoomin);
	zoomin.setToolTipText("Zoom In");
	zoomin.setMnemonic(KeyEvent.VK_PLUS);
	zoomin.addActionListener(this);
	this.add(zoomout);
	zoomout.setToolTipText("Zoom In");
	zoomout.addActionListener(this);
	zoomout.setMnemonic(KeyEvent.VK_MINUS);
	this.add(up);
	up.setToolTipText("Move Layer Up");
	up.addActionListener(this);
	up.setMnemonic(KeyStroke.getKeyStroke(KeyEvent.VK_UP,ActionEvent.CTRL_MASK | ActionEvent.SHIFT_MASK).getKeyCode());
	this.add(down);
	down.setToolTipText("Move Layer Down");
	down.addActionListener(this);
	down.setMnemonic(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN,ActionEvent.CTRL_MASK | ActionEvent.SHIFT_MASK).getKeyCode());
	this.add(add);
	add.setToolTipText("Add Annotation Element");
	add.addActionListener(this);
	add.setMnemonic(KeyStroke.getKeyStroke(KeyEvent.VK_A,ActionEvent.CTRL_MASK).getKeyCode());
	this.add(del);
	del.setToolTipText("Delete Annotaion Element");
	del.addActionListener(this);
	del.setMnemonic(KeyStroke.getKeyStroke(KeyEvent.VK_R,ActionEvent.CTRL_MASK).getKeyCode());
	this.add(osca);
	osca.setToolTipText("Add a Signal Oscilloskop Waveform Layer");
	osca.addActionListener(this);
	this.add(spec);
	spec.setToolTipText("Add a Signal Sonogram Spectrum Layer");
	spec.addActionListener(this);
	this.add(remo);
	remo.setToolTipText("Remove selected Signal Layer");
	remo.addActionListener(this);
    }

    public void actionPerformed(ActionEvent e)
    {
	if (e.getSource()==zoomin)
	    {
		root.getControlWindow().zoomslider.setValue(root.getControlWindow().zoomslider.getValue()*2);
		root.statusbar.updateStatusText("Zoom In.");
	    }
	if (e.getSource()==zoomout)
	    {
		root.getControlWindow().zoomslider.setValue(root.getControlWindow().zoomslider.getValue()/2);		
		root.statusbar.updateStatusText("Zoom Out.");
	    }
	if (e.getSource()==up)
	    {
		root.layermanager.MoveActiveLayerUp(true);
		root.statusbar.updateStatusText("Layer moved up.");
	    }
	if (e.getSource()==down)
	    {
		root.layermanager.MoveActiveLayerDown(true);
		root.statusbar.updateStatusText("Layer moved down.");
	    }
	if (e.getSource()==osca || e.getActionCommand().equals("generate time waveform"))
	    {
		// generate the check box dialog
		final JDialog dialog = new JDialog(root,"Select Signals for Signal Analyze",true);
		dialog.getContentPane().setLayout(new BorderLayout());
		JPanel checkBoxPanel = new JPanel();
		checkBoxPanel.setBorder(new CompoundBorder(new smooth.basic.SmoothTitledBorder(null,"Found " + root.nomcommunicator.signallist.size()  + " Signals in Corpus",TitledBorder.LEFT, TitledBorder.TOP),new EmptyBorder(5,5,5,5))); 
		checkBoxPanel.setLayout(new GridLayout(root.nomcommunicator.signallist.size(),1));
		for (int i=0;i<root.nomcommunicator.signallist.size();i++)
		    checkBoxPanel.add(root.nomcommunicator.checkboxes[i]);
		dialog.getContentPane().add(checkBoxPanel,BorderLayout.CENTER);
		JPanel buttonPanel = new JPanel();
		JButton okButton = new JButton("Generate Selected");
		JButton otherButton = new JButton("Other File");
		JButton cancelButton = new JButton("Cancel");
		buttonPanel.add(okButton);
		okButton.setPreferredSize(new Dimension((int)okButton.getPreferredSize().getWidth()+5,(int)okButton.getPreferredSize().getHeight()));
		buttonPanel.add(otherButton);
		otherButton.setPreferredSize(new Dimension((int)okButton.getPreferredSize().getWidth()+5,(int)okButton.getPreferredSize().getHeight()));
		buttonPanel.add(cancelButton);
		cancelButton.setPreferredSize(new Dimension((int)okButton.getPreferredSize().getWidth()+5,(int)okButton.getPreferredSize().getHeight()));
		dialog.getContentPane().add(buttonPanel,BorderLayout.SOUTH);
		// action for the ok button
		okButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
			    dialog.setVisible(false);
			    for (int i=0;i<root.nomcommunicator.signallist.size();i++)
				{
				    if (root.nomcommunicator.checkboxes[i].isSelected()==true)
					{
					    try {
						String url = ((new File(root.nomcommunicator.signalpath[i])).toURL()).toString();
						addOszillogramLayerForUrl(url,root.nomcommunicator.signalpath[i]);
					    } catch(Exception ex) {
						root.printDebugMessage("ERROR while convert signalpath to url string");
						ex.printStackTrace();
					    }
					}
				}
			}
		    });
		otherButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) 
			{
			    dialog.setVisible(false);
			    root.getControlWindow().eventhandler.chooser.setDialogTitle("Select Video or Audiofile for Signal Analyze");
			    int returnVal = root.getControlWindow().eventhandler.chooser.showOpenDialog(root);
			    if(returnVal == JFileChooser.APPROVE_OPTION) 
				{
				    String url = new String("file:/" + root.getControlWindow().eventhandler.chooser.getSelectedFile().getPath());
				    addOszillogramLayerForUrl(url,root.getControlWindow().eventhandler.chooser.getSelectedFile().getName());
				}
			}
		    });
		cancelButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) 
			{
			    dialog.setVisible(false);
			}
		    });
		
		dialog.pack();
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension window = dialog.getSize();
		dialog.setLocation((screen.width - window.width) / 2, (screen.height - window.height) / 2);
		dialog.setVisible(true);
	    }
	if (e.getSource()==spec || e.getActionCommand().equals("generate fourier spectrum"))
	    {
		// generate the check box dialog
		final JDialog dialog = new JDialog(root,"Select Signals for Signal Analyze",true);
		dialog.getContentPane().setLayout(new BorderLayout());
		JPanel checkBoxPanel = new JPanel();
		checkBoxPanel.setBorder(new CompoundBorder(new smooth.basic.SmoothTitledBorder(null,"Found " + root.nomcommunicator.signallist.size()  + " Signals in Corpus",TitledBorder.LEFT, TitledBorder.TOP),new EmptyBorder(5,5,5,5))); 
		checkBoxPanel.setLayout(new GridLayout(root.nomcommunicator.signallist.size(),1));
		for (int i=0;i<root.nomcommunicator.signallist.size();i++)
		    checkBoxPanel.add(root.nomcommunicator.checkboxes[i]);
		dialog.getContentPane().add(checkBoxPanel,BorderLayout.CENTER);
		JPanel buttonPanel = new JPanel();
		JButton okButton = new JButton("Generate Selected");
		okButton.setPreferredSize(new Dimension((int)okButton.getPreferredSize().getWidth()+5,(int)okButton.getPreferredSize().getHeight()));
		JButton otherButton = new JButton("Other File");
		otherButton.setPreferredSize(new Dimension((int)okButton.getPreferredSize().getWidth()+5,(int)okButton.getPreferredSize().getHeight()));
		JButton cancelButton = new JButton("Cancel");
		cancelButton.setPreferredSize(new Dimension((int)okButton.getPreferredSize().getWidth()+5,(int)okButton.getPreferredSize().getHeight()));
		buttonPanel.add(okButton);
		buttonPanel.add(otherButton);
		buttonPanel.add(cancelButton);
		dialog.getContentPane().add(buttonPanel,BorderLayout.SOUTH);
		// action for the ok button
		okButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) 
			{
			    dialog.setVisible(false);
			    for (int i=0;i<root.nomcommunicator.signallist.size();i++) 
				{
				    if (root.nomcommunicator.checkboxes[i].isSelected()==true) 
					{
					    try {
						String url = ((new File(root.nomcommunicator.signalpath[i])).toURL()).toString();
						addSpectralLayerForUrl(url,root.nomcommunicator.signalpath[i]);
					    } catch(Exception ex)
						{root.printDebugMessage("ERROR while convert signalpath to url string");ex.printStackTrace();}
					}   
				}
			}
		    });
		otherButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) 
			{
			    dialog.setVisible(false);
			    root.getControlWindow().eventhandler.chooser.setDialogTitle("Select Video or Audiofile for Signal Analyze");
			    int returnVal = root.getControlWindow().eventhandler.chooser.showOpenDialog(root);
			    if(returnVal == JFileChooser.APPROVE_OPTION) 
				{
				    String url = new String("file:/" + root.getControlWindow().eventhandler.chooser.getSelectedFile().getPath());
				    addSpectralLayerForUrl(url,root.getControlWindow().eventhandler.chooser.getSelectedFile().getName());
				}
			}
		    });
		cancelButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) 
			{
			    dialog.setVisible(false);
			}
		    });
		
		dialog.pack();
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension window = dialog.getSize();
		dialog.setLocation((screen.width - window.width) / 2, (screen.height - window.height) / 2);
		dialog.setVisible(true);
		
	    }
	if (e.getSource()==add)
	    {
		root.printDebugMessage("ADD an annotation elements");
		int activelayer = root.layermanager.getActiveLayer();
 		if (activelayer == -1) {
 		    JOptionPane.showMessageDialog(root,"Please select a layer first.","Info",1);
 		}
 		else 
 		    root.layermanager.getLayerByID(activelayer).addAnnotationLayerElement();
	    }
	if (e.getSource()==del)
	    {
		root.printDebugMessage("REMOVE annotation element");
		int activelayer = root.layermanager.getActiveLayer();
		if (activelayer == -1) {
		    JOptionPane.showMessageDialog(root,"Please select a layer first.","Info",1);
		}
		else root.layermanager.getLayerByID(activelayer).removeTrackElement();
	    }
	if (e.getSource()==remo)
	    {
		root.layermanager.removeActiveLayer();
		root.statusbar.updateStatusText("Signal Layer removed.");
	    }

    }
    
    public void addOszillogramLayerForUrl(String url,String name)
    {
	root.printDebugMessage("Try to build the Waveform.");
	AnnotationLayer al = new AnnotationLayer(root,name,root.layermanager.layervector.size(),-1,AnnotationLayer.OSZILLOSKOP);
	if (al.generateSamples(url)==false)
	    {
		JOptionPane.showMessageDialog(root,"Can not generate Signal Analysis\nfor file: "+name,"Error",2);
		root.statusbar.updateStatusText("Error while generate Waveform.");
		return;
	    }
	root.layermanager.addSignalPanel(al);
    }

    public void addSpectralLayerForUrl(String url,String name)
    {
	root.printDebugMessage("Try to build the Spectrum.");
	AnnotationLayer al = new AnnotationLayer(root,name,root.layermanager.layervector.size(),-1,AnnotationLayer.SPECTRAL);
	if (al.generateSpectrum(url)==false)
	    {
		JOptionPane.showMessageDialog(root,"Can not generate Signal Analysis\nfor file: "+name,"Error",2);
		root.statusbar.updateStatusText("Error while generate Spectrum.");
		return;
	    }
	root.layermanager.addSignalPanel(al);
    }
}


