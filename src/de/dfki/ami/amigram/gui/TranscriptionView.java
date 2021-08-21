package de.dfki.ami.amigram.gui;

import java.awt.Color;
import java.awt.event.WindowEvent;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JScrollPane;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;


import net.sourceforge.nite.gui.transcriptionviewer.NTranscriptionView;
import net.sourceforge.nite.gui.transcriptionviewer.StringInsertDisplayStrategy;
import net.sourceforge.nite.gui.util.AbstractCallableTool;
import net.sourceforge.nite.nom.NOMException;
import net.sourceforge.nite.nom.nomwrite.NOMElement;
import net.sourceforge.nite.nom.nomwrite.NOMPointer;
import net.sourceforge.nite.nom.nomwrite.impl.NOMWriteCorpus;
import net.sourceforge.nite.util.IteratorTransform;
import net.sourceforge.nite.util.Transform;

public class TranscriptionView extends AbstractCallableTool {
	private NTranscriptionView ntv = null;

	JInternalFrame ntvFrame;

	protected void initConfig() {
		config = new TranscriptionViewConfig();
	}

	public NTranscriptionView getNTV() {
		return ntv;
	}

	public TranscriptionView() {

	}

	public TranscriptionView(String[] args) {

		parseArguments(args);
		initConfig();
		initializeCorpus(getCorpusName(), getObservationName());

		setupTranscriptionView(15, 15, 500, 600);
	}

	protected void initNomAnnotatorSpecificLoads(NOMWriteCorpus nom)
			throws NOMException {
		/* ignore */

	}

	protected void setupTranscriptionView(int x, int y, int width, int height) {
		ntv = new NTranscriptionView();
		getNTV().setClock(getClock());
		ntvFrame = new JInternalFrame("Transcription");
		
		JScrollPane scroller = new JScrollPane(getNTV());
		ntvFrame.getContentPane().add(scroller);
		ntvFrame.setSize(width, height);
		ntvFrame.setLocation(x, y);
		//ntvFrame.setVisible(true);

		initTranscriptionViewSettings();
		refreshTranscriptionView();

	}

	public void initTranscriptionViewSettings() {
		// //display: word layer properties

		System.out.println("TranscriptionLayerName: "
				+ config.getTranscriptionLayerName());
		getNTV().setTransLayerName(config.getTranscriptionLayerName());
		getNTV().setTranscriptionToTextDelegate(
				config.getTranscriptionToTextDelegate());
		getNTV().setTranscriptionAttribute(config.getTranscriptionAttribute());

		// display: segment layer properties
		getNTV()
				.setSegmentationElementName(config.getSegmentationElementName());

		// selection: selection strategies and properties
		getNTV().setWordlevelSelectionType(config.getWordlevelSelectionType());
		getNTV().setAllowTranscriptSelect(config.getAllowTranscriptSelect());
		getNTV().setAllowAnnotationSelect(config.getAllowAnnotationSelect());
		getNTV().setAllowMultiAgentSelect(config.getAllowMultiAgentSelect());

		// super.initTranscriptionViewSettings();
		StringInsertDisplayStrategy ds = new StringInsertDisplayStrategy(
				getNTV()) {

			protected String formStartString(NOMElement element) {

				String spaces = "";
				String agentName = element.getAgentName();
				if (agentName == null) {
					return "";
				}
				if (agentName.equals("p1")) {
					spaces = " ";
				} else if (agentName.equals("p2")) {
					spaces = "  ";
				} else if (agentName.equals("p3")) {
					spaces = "   ";
				}
				if (getPersonForAgentName(agentName) != null) {
					String name = (String) getPersonForAgentName(agentName)
							.getAttributeComparableValue("name");
					if (name == null) {
						name = agentName;
					}
					return spaces + spaces + spaces + spaces + spaces + spaces
							+ name + ": ";
				} else {
					return spaces + spaces + spaces + spaces + spaces + spaces
							+ agentName + ": ";
				}
			}
		};
		ds.setEndString("");
		getNTV()
				.setDisplayStrategy(
						((TranscriptionViewConfig) config)
								.getSegmentationElementName(), ds);
		// display: how to visualize dialogue acts:
		// blue letters, some extra spacing, slightly larger font and dialog act
		// type
		Style style = getNTV().addStyle("dact-style", null);
		StyleConstants.setForeground(style, Color.blue);
		StringInsertDisplayStrategy ds2 = new StringInsertDisplayStrategy(
				getNTV(), style) {
			protected String formStartString(NOMElement element) {
				// show type of da...
				String text = "Dialogue-act";
				List tl = element.getPointers();
				if (tl != null) {
					Iterator tlIt = tl.iterator();
					while (tlIt.hasNext()) {
						NOMPointer p2 = (NOMPointer) tlIt.next();
						if (p2.getRole().equals(
								((TranscriptionViewConfig) getConfig())
										.getDATypeRole())) {
							text = ((String) p2
									.getToElement()
									.getAttributeComparableValue(
											((TranscriptionViewConfig) getConfig())
													.getDAAGloss()));
						}
					}
				}
				String comm = element.getComment();
				if (comm == null) {
					comm = "";
				} else if (!comm.equals("")) {
					comm = "***";
				}

				return " " + comm + " " + text + ": <";
			}
		};
		ds2.setEndString(">  ");
		getNTV()
				.setDisplayStrategy(
						((TranscriptionViewConfig) getConfig())
								.getDAElementName(), ds2);

		// selection: set of annotation element types that can be selected
		Set s = new HashSet();
		s.add(((TranscriptionViewConfig) getConfig()).getDAElementName());
		getNTV().setSelectableAnnotationTypes(s);

	}

	public void refreshTranscriptionView() {
		Transform t = new Transform() {
			public Object transform(Object o) {
				return ((List) o).get(0);
			}
		};
		// display segments
		System.out.println("SegmentationElementName: "
				+ config.getSegmentationElementName());

		Iterator elemIt = search(
				"($a " + config.getSegmentationElementName() + ")").iterator();
		if (elemIt.hasNext()) {
			elemIt.next();
			Iterator transformedIt = new IteratorTransform(elemIt, t);
			getNTV().setDisplayedSegments(transformedIt);
			System.out.println("*");

		}
		// display annotations on the transcription
		Iterator it = config.getDisplayedAnnotationNames().iterator();
		String elements = "";
		boolean first = true;
		while (it.hasNext()) {
			System.out.println("it.hasNext()");

			if (first) {
				first = false;
			} else {
				elements += "|";
			}
			elements += (String) it.next();
		}
		System.out.println("Elements: " + elements);

		if (!elements.equals("")) {
			elemIt = search("($a " + elements + ")").iterator();
			System.out.println("**");

			if (elemIt.hasNext()) {
				System.out.println("elemIt.hasNext()");

				elemIt.next(); // first element is a list of some general
								// search result variables
				Iterator transformedIt = new IteratorTransform(elemIt, t);
				System.out.println("***");
				getNTV().displayAnnotationElements(transformedIt);
				System.out.println("****");

			}
		}
	}

	protected NOMElement getPersonForAgentName(String agentName) {
		Iterator i = search(
				"($h person)(exists $p participant):($p@"
						+ getMetaData().getObservationAttributeName() + "==\""
						+ getObservationName() + "\") && ($p>$h) && ($p@"
						+ getMetaData().getAgentAttributeName() + "==\""
						+ agentName + "\")").iterator();
		if (i.hasNext()) {
			i.next();
		}
		NOMElement result = null;
		if (i.hasNext()) {
			result = (NOMElement) ((List) i.next()).get(0);
		}
		return result;
	}

	public JInternalFrame getTranscriptionView(){
		return ntvFrame;
	}
}
