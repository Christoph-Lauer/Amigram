/******************************************************************
 *                 AnnoTable.java  -  description
 *                    -----------------------
 * @author 		  Christoph Lauer
 * @version 		  0.1  
 * @see			  www.amiproject.de
 * copyright		: (C) 2003 @ christoph lauer
 * begin 		: Thu Okt 30 17:00:00 CET 2003
 * last save   	        : Time-stamp: <2005-03-14 05:04:11 christoph> 
 * compiler    	        : java version 1.4.2 
 * operating system     : Linux 
 * editor		: xemacs 21.4
 * description          : Tab for the Control window.
 ******************************************************************/

package de.dfki.ami.amigram.tools;

import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.sourceforge.nite.meta.impl.NiteAttribute;
import net.sourceforge.nite.meta.impl.NiteElement;
import net.sourceforge.nite.meta.impl.NiteLayer;
import net.sourceforge.nite.nom.nomwrite.impl.NOMWriteAttribute;
import net.sourceforge.nite.nom.nomwrite.impl.NOMWriteElement;
import de.dfki.ami.amigram.gui.DesktopFrame;

public class AnnoTable extends JPanel implements ItemListener, ChangeListener, ActionListener, CaretListener
{
    private DesktopFrame       root;
    private JLabel             noanno = new JLabel("<html><font size=+2>No Element Selected</font><br><center>Select layer and element.</center>");
    private JComboBox          elementscombobox,attributecombobox;
    private SpinnerNumberModel startmodel = new SpinnerNumberModel(0.0, 0.0, 100000.0, 0.05); 
    private SpinnerNumberModel endmodel = new SpinnerNumberModel(0.0, 0.0, 100000.0, 0.05); 
    private JSpinner           startspinner = new JSpinner(startmodel);
    private JSpinner           endspinner = new JSpinner(endmodel);
    public  JTextArea textarea = new JTextArea();
    private EditPanel          editpanel = new EditPanel();
    public  JTextArea textareaelementname = new JTextArea();
    private ElementTextContentPanel elementtextpanel = new ElementTextContentPanel();
    private NOMWriteElement    nomwriteelement;
    private NiteAttribute      niteattribute;
    private NiteLayer          nitelayer;
    private JButton            addbutton = new JButton("Add");
    private JButton            removebutton = new JButton("Remove");
    private AnnotationLayer    al = null;
    private AnnotationElement  ae = null;
    public  JButton            markupnowbutton, deletemarkupbutton;
    public  AnnoTable          thisref = this; // Access this class from the inner class
    private Double lastEndTime = new Double(0.0);
    private Double lastStartTime = new Double(0.0);
    
    /**
     * This is the standard constructor.
     */
    public AnnoTable(DesktopFrame r) {
	root = r;
	updateAnnotable();
	addbutton.addActionListener(this);
	removebutton.addActionListener(this);
	startspinner.setPreferredSize(new Dimension(130,22));
	endspinner.setPreferredSize(new Dimension(130,22));
    }
    
    /**
     * This creates dynamicly the annotation bar from the MetaData.
     */
    public void updateAnnotable() {
	if (root.DEBUG == true) root.printDebugMessage("Rebuild Annotation Table");
	boolean activeanootrackelementexists = setActiveLayerAndAnnoElement();
	// strore the values for other methods
	// Paint the Panel
	removeAll();
	repaint();
	if (activeanootrackelementexists == true) {
	    nomwriteelement = ae.nomwriteelement;
	    if (nomwriteelement == null)
		return;
	    setLayout(new GridLayout(5,1));
// THE ATTRIBUTE BUTTONS
	    JPanel abovepanel = new JPanel();
	    abovepanel.setLayout(new GridLayout(1,2));
	    JPanel p2 = new JPanel();
	    p2.setLayout(new GridLayout(1,2));
	    p2.setBorder(new CompoundBorder(new TitledBorder(null,"Add/Remove Attributes",TitledBorder.LEFT, TitledBorder.TOP),new EmptyBorder(0,0,0,0))); 		    
	    p2.add(addbutton);
	    p2.add(removebutton);
	    abovepanel.add(p2);
	    add(abovepanel);
// THE SPINNERS
	    JPanel spinnerpanel = new JPanel();
	    JPanel spinnerpanel_1 = new JPanel();
	    JPanel spinnerpanel_2 = new JPanel();
	    spinnerpanel_1.setBorder(new CompoundBorder(new smooth.basic.SmoothTitledBorder(null,"Start Time",TitledBorder.LEFT, TitledBorder.TOP),new EmptyBorder(0,0,0,0))); 
	    spinnerpanel_2.setBorder(new CompoundBorder(new smooth.basic.SmoothTitledBorder(null,"End Time",TitledBorder.LEFT, TitledBorder.TOP),new EmptyBorder(0,0,0,0))); 
	    startspinner.addChangeListener(this);
	    endspinner.addChangeListener(this);
	    spinnerpanel_1.add(startspinner);
	    spinnerpanel_2.add(endspinner);
	    spinnerpanel.setLayout(new GridLayout(1,2));
	    spinnerpanel.add(spinnerpanel_1);
	    spinnerpanel.add(spinnerpanel_2);
	    lastStartTime = new Double(ae.begintime);
	    lastEndTime = new Double(ae.endtime);
	    startspinner.setValue(new Double(ae.begintime));
	    endspinner.setValue(new Double(ae.endtime));
	    add(spinnerpanel);
// THE ELEMENT CONTENT PANNEL
	    elementtextpanel.updateEdit();
	    add(elementtextpanel);
// THE ELEMENT AND ATTRIBUTE SELECTORS
	    // First get the possible elements from the list
	    nitelayer = al.nitelayer;
	    java.util.List contentelementlist = al.nitelayer.getContentElements();
	    Object[] elementitems = new Object[contentelementlist.size()];
	    for (int i=0;i<contentelementlist.size();i++) {
		NiteElement ne = (NiteElement)contentelementlist.get(i);
		    elementitems[i]=ne.getName();
	    }
	    // Then create the combobox
	    JPanel combopanel = new JPanel();
	    combopanel.setLayout(new GridLayout(1,2));
	    JPanel combo1 = new JPanel();
	    combo1.setBorder(new CompoundBorder(new TitledBorder(null,"Elements (OR)",TitledBorder.LEFT, TitledBorder.TOP),new EmptyBorder(0,0,0,0))); 
	    combo1.setToolTipText("list of possible elements for this annotation");
	    elementscombobox = new JComboBox(elementitems);
	    combo1.add(elementscombobox);
	    combopanel.add(combo1);
	    // Access the corresponding Attributes if there is any 
	    NiteElement niteelement = (NiteElement)contentelementlist.get(0);
	    if (niteelement == null)
		{
		    root.printDebugMessage("Empty Segment selected");
		    return;
		}
	    // possible Attributes
	    java.util.List attributlist = niteelement.getAttributes();
	    Object[] attributeitems = new Object[attributlist.size()];
	    // Attributes from the real corpus, not from the metadada
	    java.util.List nomarrlist = nomwriteelement.getAttributes();
	    // store the place where attribute found to select them in combo box
	    String realAtt=null;
	    // Go throught the MetaData Attributes
	    for (int i=0;i<attributlist.size();i++) {
		NiteAttribute na = (NiteAttribute)attributlist.get(i);
		String itemname = na.getName();
		if (na.getType() == NiteAttribute.ENUMERATED_ATTRIBUTE) itemname += " <ENUMERATED>";
		if (na.getType() == NiteAttribute.NUMBER_ATTRIBUTE) itemname += " <NUMBER>";
		if (na.getType() == NiteAttribute.STRING_ATTRIBUTE) itemname += " <STRING>";
		// Check if there is an equal element in the current element NOM corpus
 		if (nomarrlist!=null)
		    for (int j=0;j<nomarrlist.size();j++) {
			NOMWriteAttribute nwa = (NOMWriteAttribute)nomarrlist.get(j);
			if (nwa.getName().equals(na.getName())==true) {
			    root.printDebugMessage(nwa.getName() + " " + na.getName());
			    itemname = "\u00d7 "+itemname;
			    realAtt = itemname;
			}
		    }
		// Write the name to the ComboBox Items 
		attributeitems[i]=itemname;
	    }
	    
	    // Store the curren state
	    if (attributlist.size() != 0)
		niteattribute = (NiteAttribute)attributlist.get(0);
	    else 
		niteattribute = null;
	    // Create the combo box
	    JPanel combo2 = new JPanel();
	    combo2.setBorder(new CompoundBorder(new TitledBorder(null,"Attributes (AND)",TitledBorder.LEFT, TitledBorder.TOP),new EmptyBorder(0,0,0,0))); 
	    combo2.setToolTipText("list of attributes for this element");
	    attributecombobox = new JComboBox(attributeitems);
	    attributecombobox.setSelectedItem(realAtt);
	    combo2.add(attributecombobox);
	    combopanel.add(combo2);
	    attributecombobox.setPreferredSize(new Dimension(130,22));
	    elementscombobox.setPreferredSize(new Dimension(130,22));
	    add(combopanel);
	    elementscombobox.addItemListener(this);
	    attributecombobox.addItemListener(this);
// THE ATTRIBUTE CONTENT PANEL 
	    // Now the edit panel is generated
	    editpanel.updateEdit();
	    add(editpanel);
	    // And finaly read out the values from the refereced NomWriteElement
	    readValuesFromNomWriteElement();
	}
	// Default Text
	else {
	    setLayout(new FlowLayout());
	    add(noanno);
	}
	revalidate();
    }
    ////////////////////////////////////////
    // LISTENER IMPLEMENTATIONS FOLLOW HERE
    ////////////////////////////////////////
    /**
     * For the actionlistener
     */
    public void actionPerformed(ActionEvent e) {

	if (e.getSource() == removebutton) {
	    if (root.DEBUG == true) root.printDebugMessage("REMOVE ATTRIBUTE BUTTON PRESSED");
	    // Check if there is an attribute selected
	    int selectedatt = attributecombobox.getSelectedIndex();
	    if (selectedatt == -1) {
		JOptionPane.showMessageDialog(root,"There is no attribute selected !!!","ERROR",JOptionPane.ERROR_MESSAGE);
		return;
	    }
	    //Get the META attribute name
	    NiteAttribute na = niteattribute;
	    // Check if there is an instance of the attribute in the nom
	    boolean attfound = false;
	    java.util.List nomattributelist = nomwriteelement.getAttributes();
	    if (nomattributelist.size() != 0)
		for (int i=0;i<nomattributelist.size();i++) {
		    NOMWriteAttribute nwa = (NOMWriteAttribute)nomattributelist.get(i);
		    if (nwa.getName().equals(na.getName()))
			attfound = true;
		}
	    // If no show message edialog
	    if (attfound == false) {
		JOptionPane.showMessageDialog(root,"There is no attribute instantiated\nin the current element.","Not Possible",JOptionPane.ERROR_MESSAGE);
		return;
	    }
	    // Ask the user 
	    int answer = JOptionPane.showConfirmDialog(root,"Are you sure you want remove\nthis attribute","Question",JOptionPane.YES_NO_OPTION);
	    if (answer == 1) 
		return;
	    try {
		nomwriteelement.removeAttribute(na.getName());
	    }
	    catch (Throwable t) {
		JOptionPane.showMessageDialog(root,"There was an unknown Error\nwhile removing the attribute.","ERROR",JOptionPane.ERROR_MESSAGE);
	    }
	    root.printDebugMessage("ATTRIBUTE REMOVED");
	    updateAnnotable();
	    attributecombobox.setSelectedIndex(selectedatt);
	}
	if (e.getSource() == addbutton) {
	    root.printDebugMessage("ADD ATTRIBUTE BUTTON PRESSED");
	    // Check if there is an attribute selected
	    int selectedatt = attributecombobox.getSelectedIndex();
	    if (selectedatt == -1) {
		JOptionPane.showMessageDialog(root,"There is no attribute selected, or\nthis element has no attribute !!!","ERROR",JOptionPane.ERROR_MESSAGE);
		return;
	    }
	    // Get the corresponding nite element and his attributes list
	    java.util.List contentelementlist = nitelayer.getContentElements();
	    NiteElement niteelement = (NiteElement)contentelementlist.get(elementscombobox.getSelectedIndex());
	    java.util.List attributlist = niteelement.getAttributes();
	    // get the nom attributes list;
	    java.util.List nomattributelist = nomwriteelement.getAttributes();
	    boolean thereisalredadsanattributeinstance = false;
	    // Check if the metadata refers to attributes
	    if (contentelementlist.size() > 0)
		{
		    // go throught the NOM attribute list and check if there is already an attribute with this name
		    if (nomattributelist != null)
			{
			    for (int i=0;i<nomattributelist.size();i++) { 
				NOMWriteAttribute nwa = (NOMWriteAttribute)nomattributelist.get(i);
				if (nwa.getName().equals(niteattribute.getName())) {
				    JOptionPane.showMessageDialog(root,"There is already an attribute\nfrom this type in the element!","Not Possible",JOptionPane.ERROR_MESSAGE);
				    return;
				}
			    }
			}
		}
	    else {
		JOptionPane.showMessageDialog(root,"This element has no attributes !!!","Not Possible",JOptionPane.ERROR_MESSAGE);
		return;
	    }
	    // Create and initialize the new attribute
	    NOMWriteAttribute newna = null;
	    switch (niteattribute.getType()) {
	    case NiteAttribute.ENUMERATED_ATTRIBUTE: {
		// get the value
		java.util.List enumvalues = niteattribute.getEnumeratedValues();
		String attvalue;
		if (enumvalues.size() == 0)
		    attvalue = "NULL";
		else 
		    attvalue = (String)enumvalues.get(0);
		newna = new NOMWriteAttribute(NOMWriteAttribute.NOMATTR_STRING,niteattribute.getName(),attvalue,new Double(0.0));
	    }
	    case NiteAttribute.NUMBER_ATTRIBUTE: {
		newna = new NOMWriteAttribute(niteattribute.getName(),new Double(0.0));
	    }
	    case NiteAttribute.STRING_ATTRIBUTE: {
		newna = new NOMWriteAttribute(niteattribute.getName(),"PLACE TEXT HERE.");
	    }
	    }
	    //  add the attribute to the NomWriteCorpus
	    try
		{
		    nomwriteelement.addAttribute(newna);
		}
	    catch (net.sourceforge.nite.nom.NOMException exc)
		{
		    root.printDebugMessage("ERROR NOMEXCEPTION NOMCommunicator");
		}
	    updateAnnotable();
	    // and set the combobox as selected item
	    attributecombobox.setSelectedIndex(selectedatt);
	}
	if (e.getSource() == editpanel.combobox) {
	    root.printDebugMessage("EDITPANEL COMBOBOX EVENT");
	    // first get the selected item name
	    int selitem = editpanel.combobox.getSelectedIndex();
	    java.util.List metaattparamlist = niteattribute.getEnumeratedValues();
	    String text = (String)metaattparamlist.get(selitem);
	    // Then get the corresponding attribute
	    NOMWriteAttribute nwa = null;
	    java.util.List nomattributelist = nomwriteelement.getAttributes();
	    // Then search the attribute
	    for (int i=0;i<nomattributelist.size();i++) {
		nwa = (NOMWriteAttribute)nomattributelist.get(i);
		if (nwa.getName().equals(niteattribute.getName()))
		    break;
	    }
	    // And set the String to as parameter
	    try {
		nwa.setStringValue(text);
 		root.statusbar.updateStatusText("Parameter enum stored: "+text);
		root.printDebugMessage("SAVE THE STRING IN THE NITE ELEMENT:"+text+" IN ATTRIBUTE: "+nwa.getName());
	    }
	    catch (Throwable t) {
		root.printDebugMessage("ERROR WHILE SAVE THE STRING");
	    }
	}
	
    }
    
    /** 
     * For the ChangeListener
     */
    public void stateChanged(ChangeEvent e) {
	root.printDebugMessage("ITEM LISTENER");
	if (e.getSource() == startspinner) {
	    Double sv = (Double) startspinner.getValue();
	    if (lastStartTime.doubleValue() == sv.doubleValue()) 
		return;
	    if (al.getLayerType() != 1) {
		return;
	    }
	    ae.begintime = sv.floatValue();
	    try {
		ae.nomwriteelement.setStartTime(sv.doubleValue());
		al.repaint();
	    }
	    catch (Throwable t) {}
	    lastStartTime = sv;
	}
	if (e.getSource() == endspinner) {
	    Double ev = (Double) endspinner.getValue();
	    if (lastEndTime.doubleValue() == ev.doubleValue()) {
		return;
	    }
	    if (al.getLayerType() != 1) {
		return;
	    }
	    ae.endtime = ev.floatValue();
	    try {
		ae.nomwriteelement.setEndTime(ev.doubleValue());
		al.repaint();
	    }
	    catch (Throwable t) {}
	    lastEndTime = ev;
	}
	if (e.getSource() == editpanel.spinner) {
	    // Search the corresponding attribute
	    java.util.List nomattributelist = nomwriteelement.getAttributes();
	    NOMWriteAttribute nwa = null;
	    // Then search the attribute
	    for (int i=0;i<nomattributelist.size();i++) {
		nwa = (NOMWriteAttribute)nomattributelist.get(i);
		if (nwa.getName().equals(niteattribute.getName()))
		    break;
	    }
	    // And set the Number as parameter
	    Double val = (Double) editpanel.spinner.getValue();
	    try {
		nwa.setDoubleValue(val);
 		root.statusbar.updateStatusText("Parameter value stored: "+val);
		root.printDebugMessage("SAVE THE VALUE IN THE NITE ELEMENT: "+val+" IN ATTRIBUTE: "+nwa.getName());
	    }
	    catch (Throwable t) {
		root.printDebugMessage("ERROR WHILE SAVE THE STRING");
	    }
	}
    }
    
    /** 
     * The impementation of the ItemListener
     */
    public void itemStateChanged(ItemEvent e) {
	// First check if there is any nite-element.
	if (nitelayer == null)
	    return;
	// get the selected Item
	int selecteditem = elementscombobox.getSelectedIndex();
	// Get the corresponding nite element
	java.util.List contentelementlist = nitelayer.getContentElements();
	NiteElement niteelement = (NiteElement)contentelementlist.get(selecteditem);
	// get the attributes
	java.util.List attributlist = niteelement.getAttributes();
	if (e.getSource() == elementscombobox) {
	    root.printDebugMessage(e.paramString());
	    if (e.getStateChange() == ItemEvent.DESELECTED) 
		return;
	    root.printDebugMessage("ITEM CHANGE EVENT IN ELEMENTSCOMBOBOX");
	    int answer = JOptionPane.showConfirmDialog(root,"Are you sure you want change\nthe element. You will lost all\n attributes in the current element.","Question",JOptionPane.YES_NO_OPTION);
	    if (answer == 1) 
		return;
	    // Change the Name
	    nomwriteelement.setName(niteelement.getName());
	    // Remove all attributes
	    java.util.List nomattributes = nomwriteelement.getAttributes();
	    if (nomattributes != null)
		{
		    for (int i=0;i<nomattributes.size();i++) {
			NOMWriteAttribute na = (NOMWriteAttribute)nomattributes.get(i);
			try {
			    nomwriteelement.removeAttribute(na.getName());
			}
			catch (Throwable t) {
			    root.printDebugMessage("ERROR WHILE REMOVE AN NOMWRITEATTRIBUTE");
			}
		    }
		}
	    // remove al items
	    attributecombobox.removeAllItems();
	    // Attributes from the real corpus, not from the metadada
	    java.util.List nomarrlist = nomwriteelement.getAttributes();
	    // and the new ones
	    for (int i=0;i<attributlist.size();i++) {
		NiteAttribute na = (NiteAttribute)attributlist.get(i);
		String itemname = na.getName();
		if (na.getType() == NiteAttribute.ENUMERATED_ATTRIBUTE) itemname += " <ENUMERATED>";
		if (na.getType() == NiteAttribute.NUMBER_ATTRIBUTE) itemname += " <NUMBER>";
		if (na.getType() == NiteAttribute.STRING_ATTRIBUTE) itemname += " <STRING>";
		// Check if there is an equal element in the current element NOM corpus
		for (int j=0;j<nomarrlist.size();j++) {
		    NOMWriteAttribute nwa = (NOMWriteAttribute)nomarrlist.get(j);
		    if (nwa.getName().equals(na.getName())==true) {
			root.printDebugMessage(nwa.getName() + " " + na.getName());
			    itemname = "\u00d7 "+itemname;
		    }
		}
		attributecombobox.addItem(itemname);
	    }
	    updateAttributeCombobox();
	}
	if (e.getSource() == attributecombobox) {
	    root.printDebugMessage("ITEM CHANGE EVENT IN ATTRIBUTECOMBOBOX");
	    updateAttributeCombobox();
	}
    }
    /**
     * The implementation of the Caret interface
     */
    public void caretUpdate(CaretEvent e) {
	root.printDebugMessage("CARET EVENT");
	// The text area from the edit panel
	if (e.getSource() == editpanel.textarea) {
	    String text = editpanel.textarea.getText();
	    NOMWriteAttribute nwa = null;
	    // search the attribute in the list
	    java.util.List nomattributelist = nomwriteelement.getAttributes();
	    if (nomattributelist != null && nomattributelist.size() != 0)
		{
		    for (int i=0;i<nomattributelist.size();i++) {
			nwa = (NOMWriteAttribute)nomattributelist.get(i);
			if (nwa.getName().equals(niteattribute.getName()))
			    break;
		    }
		    try {
			nwa.setStringValue(text);
			root.statusbar.updateStatusText("Parameter text stored: "+text);
		    }
		    catch (Throwable t) {
			root.printDebugMessage("ERROR WHILE SAVE THE STRING");
		    }
		}
	}
	if (e.getSource() == elementtextpanel.textarea) {
	    try {
		nomwriteelement.setText(elementtextpanel.textarea.getText());
	    }
	    catch (Throwable t) {
		root.printDebugMessage("ERROR WHILE SAVE THE STRING");
	    }
	}
    }
	
    ////////////////////////////////////////
    // END OF THE LISTENER IMPLEMENTATIONS
    ////////////////////////////////////////
    /**
     * This function reads out the values from the NOMWriteElement and updates the AnnoTable values
     * for the Attribute selector.
     */
    public void readValuesFromNomWriteElement() {
	// METADATA
	if (nitelayer == null)
	    return;
	// Get the corresponding nite element
	java.util.List contentelementlist = nitelayer.getContentElements();
	
	// NOM
	String elementname = nomwriteelement.getName();
	// Search the element in the list.
	root.printDebugMessage("SEARCH ELEMENT IN THE LIST");
	for (int i=0;i<contentelementlist.size();i++) {
	    NiteElement ne = (NiteElement) contentelementlist.get(i);
	    if (elementname.equals(ne.getName()) == true) 
		{
		    // And set the combobox element
		    elementscombobox.removeItemListener(this);
		    elementscombobox.setSelectedIndex(i);
		    elementscombobox.addItemListener(this);
	    }
	}
    }
    
    /**
     * This function accesses the currently selected Layer and Element,
     * and stores them in the class variables al and ae.
     * @return boolean - true if there is an active element.
     */
    public boolean setActiveLayerAndAnnoElement() {
	boolean activeanootrackelementexists = false; 	// Decide if there is anything to paint
       	// First get the active AnnotationTrackElement
	int activelayer = root.layermanager.getActiveLayer();
	if (activelayer != -1) {
	    al = root.layermanager.getLayerByID(activelayer);
	    int activeelement = al.activeannotationelement;
	    if (activeelement != -1) {		
		ae = (AnnotationElement)root.layermanager.getLayerByID(activelayer).annotationelements.get(activeelement);
		activeanootrackelementexists = true;
	    }
	    else {
		activeanootrackelementexists = false;
	    }
	}
	else {
	    activeanootrackelementexists = false;
	}
	root.printDebugMessage("ActiveAnnotTrackElements"+activeanootrackelementexists);
	
	return activeanootrackelementexists;
    }
    
    /**
     * This function updates the Attribute combobox and calls the edit panel.
     * It is often called from the elementcombobox event and the attribute combobox event.
     */
    private void updateAttributeCombobox() {
	// First check if there is any nite-element.
	if (nitelayer == null)
	    return;
	// get the selected Item
	int selecteditem = elementscombobox.getSelectedIndex();
	// Get the corresponding nite element
	java.util.List contentelementlist = nitelayer.getContentElements();
	NiteElement niteelement = (NiteElement)contentelementlist.get(selecteditem);
	// get the attributes
	java.util.List attributlist = niteelement.getAttributes();
	int selectedattribute = attributecombobox.getSelectedIndex();
	if (selectedattribute == -1) 
	    niteattribute = null;
	else 
	    niteattribute = (NiteAttribute)attributlist.get(selectedattribute);
	editpanel.updateEdit();
	revalidate();
    }

    /**
     * This nested class implements an editor panel which is has the possibility to change the 
     * the outfit from ENUM over NUMBER to TEXT. It displays an default label when no NiteAttribute
     * is referenced. 
     */
    private class EditPanel extends JPanel {
	
	final static String  ENUM = "ENUM";
	final static String  TEXT = "TEXT";
	final static String  NUMBER = "NUMBER";
	final static String  NOATT = "NOATT";
	
	public JTextArea textarea = new JTextArea();
	public SpinnerNumberModel spinnermodel = new SpinnerNumberModel(0.0,-1000000000000000000000.0,1000000000000000000000.0, 0.01); 
	public JSpinner spinner = new JSpinner(spinnermodel);
	public JComboBox combobox = new JComboBox(); 
	public JScrollPane textpane = new JScrollPane(textarea);
	public JLabel noatt = new JLabel("<html><center><font size=2 color=aa3333>This Element has no Attribute</font></center>");

	private CardLayout cardlayout;
	private JPanel p1 = new JPanel(new GridLayout(1,1));
	private JPanel p2 = new JPanel(new GridLayout(1,1));
	private JPanel p3 = new JPanel(new GridLayout(1,1));
	private JPanel p4 = new JPanel(new GridLayout(1,1));
	
	/**
	 * The default constructor.
	 */
	public EditPanel(){
	    p1.setBorder(new CompoundBorder(new TitledBorder(null,"Parameter (Number Edit)",TitledBorder.LEFT, TitledBorder.TOP),new EmptyBorder(0,0,0,0))); 
	    p2.setBorder(new CompoundBorder(new TitledBorder(null,"Parameter (Enumerated Edit)",TitledBorder.LEFT, TitledBorder.TOP),new EmptyBorder(0,0,0,0)));
	    p3.setBorder(new CompoundBorder(new TitledBorder(null,"Parameter (Text Edit)",TitledBorder.LEFT, TitledBorder.TOP),new EmptyBorder(0,0,0,0))); 
	    p4.setBorder(new CompoundBorder(new TitledBorder(null,"No Attribute",TitledBorder.LEFT, TitledBorder.TOP),new EmptyBorder(0,0,0,0))); 
	    
	    p1.add(spinner);
	    p2.add(combobox);
	    p3.add(textpane);
	    p4.add(noatt);
	    
	    setLayout(new CardLayout());
	    add(p1,NUMBER); 
	    add(p2,ENUM);
	    add(p3,TEXT);
	    add(p4,NOATT); 
	    cardlayout = (CardLayout)getLayout();
	}
	
	/**
	 * This class changes the outfit of the Panel automaticly and updates the values.
	 */
	public void updateEdit() {
	    if (niteattribute == null) {
		cardlayout.show(this,NOATT);
		revalidate();
		return;
	    }
	    // Search if there is an attribute with this name in the NOMElement
	    NOMWriteAttribute nomattribute = null;
	    java.util.List attributelist = nomwriteelement.getAttributes();
	    if (attributelist == null)
		return;
	    for (int i=0;i<attributelist.size();i++) {
		nomattribute = (NOMWriteAttribute)attributelist.get(i);
		if (nomattribute.getName().equals(niteattribute.getName()) == true)
		    break;
		else 
		    nomattribute = null;
	    }
	    switch (niteattribute.getType()) {
	    case NiteAttribute.ENUMERATED_ATTRIBUTE: {
		combobox.removeActionListener(thisref);
		combobox.removeAllItems();
		java.util.List items = niteattribute.getEnumeratedValues();
		for (int i=0;i<items.size();i++) 
		    combobox.addItem(items.get(i));
		cardlayout.show(this,ENUM);
		if (nomattribute == null) {
		    combobox.setEnabled(false);
		    p2.setEnabled(false);
		}
		else {
		    combobox.setEnabled(true);
		    p2.setEnabled(true);
		}
		revalidate();
		if (nomattribute != null)
		    combobox.setSelectedItem(nomattribute.getStringValue());
		combobox.addActionListener(thisref);
		break;
	    }
	    case NiteAttribute.NUMBER_ATTRIBUTE: {
		cardlayout.show(this,NUMBER);
		spinner.removeChangeListener(thisref);
		if (nomattribute == null)
		    {
			spinner.setValue(new Double(0));
			spinner.setEnabled(false);
			setEnabled(false);
			p1.setEnabled(false);
		    }
		else 
		    {
			System.out.println(nomattribute.getStringValue());
			System.out.println(nomattribute.getDoubleValue());
			spinner.setValue(nomattribute.getDoubleValue());
			setEnabled(true);
			spinner.setEnabled(true);
			p1.setEnabled(true);
		    }
		revalidate();
		spinner.addChangeListener(thisref);
		break;
	    }
	    case NiteAttribute.STRING_ATTRIBUTE: {
		cardlayout.show(this,TEXT);
		textarea.removeCaretListener(thisref);
		if (nomattribute == null)
		    {
			textarea.setText("");
			textarea.setEnabled(false);
			p3.setEnabled(false);
		    }
		else 
		    {
			textarea.setEnabled(true);
			p3.setEnabled(true);
			textarea.setText(nomattribute.getStringValue());
		    }
		revalidate();
		textarea.addCaretListener(thisref);
		break;
	    }
	    }
	}
    }
    /**
     * This 
     */
    private class ElementTextContentPanel extends JPanel {
	private JPanel p1 = new JPanel(new GridLayout(1,1));
	private JTextArea textarea = new JTextArea();
	private JScrollPane textpane = new JScrollPane(textarea);
	
	public ElementTextContentPanel() {
	    setLayout(new GridLayout(1,1));
	    setBorder(new CompoundBorder(new TitledBorder(null,"Element Text Content",TitledBorder.LEFT, TitledBorder.TOP),new EmptyBorder(0,0,0,0))); 
	    add(textpane);
	}
	
	public void updateEdit() {
	    textarea.removeCaretListener(thisref);
	    if (nomwriteelement.getMetadataElement().textContentPermitted() == true) {
		String text = nomwriteelement.getText();
		textarea.setText(text);
		textarea.setEnabled(true);
	    }
	    else {
		textarea.setText("      --- NO TEXT CONTENT ---");
		textarea.setEnabled(false);
	    }
	    textarea.addCaretListener(thisref);
	}
    }
}
