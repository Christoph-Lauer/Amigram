/**
 * 
 */
package externalplugins.exampleplugin;

import java.util.Iterator;
import java.util.List;

import javax.swing.JInternalFrame;
import javax.swing.JScrollPane;

import net.sourceforge.nite.gui.transcriptionviewer.NTranscriptionView;
import net.sourceforge.nite.gui.transcriptionviewer.StringInsertDisplayStrategy;
import net.sourceforge.nite.meta.impl.NiteMetaData;
import net.sourceforge.nite.meta.impl.NiteObservation;
import net.sourceforge.nite.nom.nomwrite.NOMElement;
import net.sourceforge.nite.nom.nomwrite.impl.NOMWriteElement;
import de.dfki.ami.amigram.gui.DesktopFrame;
import de.dfki.ami.amigram.gui.TranscriptionView;
import de.dfki.ami.amigram.plugin.AnnotationEvent;
import de.dfki.ami.amigram.plugin.AnnotationListener;

/**
 * @author Jochen
 * 
 */
public class ExamplePlugin implements AnnotationListener {

	private DesktopFrame mainframe;

	/* NOMWriteElement that is selected, included in the AnnotationEvent */
	private NOMWriteElement nomwriteelement;

	private NTranscriptionView ntv;

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.dfki.ami.amigram.plugin.AnnotationListener#emptyAnnoElementSelected(de.dfki.ami.amigram.plugin.AnnotationEvent)
	 */
	public void emptyAnnoElementSelected(AnnotationEvent e) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.dfki.ami.amigram.plugin.AnnotationListener#annoElementSelected(de.dfki.ami.amigram.plugin.AnnotationEvent)
	 */
	public void annoElementSelected(AnnotationEvent e) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.dfki.ami.amigram.plugin.AnnotationListener#emptyAnnoElementDoubleClicked(de.dfki.ami.amigram.plugin.AnnotationEvent)
	 */
	public void emptyAnnoElementDoubleClicked(AnnotationEvent e) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.dfki.ami.amigram.plugin.AnnotationListener#annoElementDoubleClicked(de.dfki.ami.amigram.plugin.AnnotationEvent)
	 */
	public void annoElementDoubleClicked(AnnotationEvent e) {
		// TODO Auto-generated method stub
		

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.dfki.ami.amigram.plugin.AnnotationListener#initialize(de.dfki.ami.amigram.gui.DesktopFrame)
	 */
	public void initialize(DesktopFrame root) {
		System.out.println("INIT EXAMPLEPLUGIN");
		mainframe = root;
		

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.dfki.ami.amigram.plugin.AnnotationListener#terminate()
	 */
	public void terminate() {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.dfki.ami.amigram.plugin.AnnotationListener#getName()
	 */
	public String getName() {
		return ("ExamplePlugin (the example plugin)");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.dfki.ami.amigram.plugin.AnnotationListener#showGui()
	 */
	public void showGui() {
		
		String[] arglist = new String[14];
		arglist[0]="java";
		arglist[1]="ViewPanel";
		arglist[2]="-corpus";
		arglist[3]=mainframe.getNOMCommunicator().getCorpusFilePath();
		arglist[4]="-observation";
		
		for (Iterator iter = mainframe.getNOMCommunicator().getLoadedObservations().iterator(); iter.hasNext();) {
		    arglist[5] = ((NiteObservation)iter.next()).getShortName();
		}
		arglist[6]="-config";
		arglist[7]="configuration/amiConfig.xml";
		arglist[8]="-gui-settings";
		arglist[9]="dac-gs-ami";
		arglist[10]="-corpus-settings";
		arglist[11]="-dac-cs-ami";
		arglist[12]="-annotator";
		/*TODO: different annotators possible*/
		arglist[13]="";
		
		TranscriptionView tv = new TranscriptionView(arglist);
		
		JInternalFrame ntvFrame = tv.getTranscriptionView();
		ntvFrame.setClosable(true);
		ntvFrame.setResizable(true);
		ntvFrame.setMaximizable(true);
		
		mainframe.addSubWindow(ntvFrame, 10, 10, 500, 600);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.dfki.ami.amigram.plugin.AnnotationListener#invokePlugin(java.util.List)
	 */
	public boolean invokePlugin(List attributeList) {
		// TODO Auto-generated method stub
		return true;
	}

	


	

}
