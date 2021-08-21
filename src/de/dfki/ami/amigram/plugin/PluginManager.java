/******************************************************************
 *                 PluginManager.java  -  description
 *                    -----------------------
 * @author 		  Christoph Lauer
 * @version 		  0.1  
 * @see			  www.amiproject.de
 * copyright		: (C) 2004 @ christoph lauer
 * begin 		: Thu Jan 20 15:01:00 CET 2004
 * last save   	        : Time-stamp: <05/08/19 13:10:56 clauer> 
 * compiler    	        : java version 1.4.2 
 * operating system     : Linux 
 * editor		: xemacs 21.4
 * description          : handles the plugin
 ******************************************************************/

package de.dfki.ami.amigram.plugin;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import net.sourceforge.nite.nom.nomwrite.impl.NOMWriteAnnotation;
import de.dfki.ami.amigram.gui.DesktopFrame;
import de.dfki.ami.amigram.tools.AnnotationElement;
import de.dfki.ami.amigram.tools.ClassPathLoader;

public class PluginManager {
	private DesktopFrame root;

	private Vector<AnnotationListener> plugins = new Vector<AnnotationListener>();

	private AnnotationListener plugin;

	private PluginManagerGui gui;

	public PluginManager(DesktopFrame r) {
		root = r;
		lookForPlugins();
		// init the gui and preselect the plugin from config
		gui = new PluginManagerGui();
		String preselected = root.config.plugin;
		boolean flag = false;
		for (int i = 0; i < plugins.size(); i++) {
			root.printDebugMessage("COMPARE [" + plugins.get(i).getName()
					+ "] WITH [" + preselected + "]");

			if (plugins.get(i).getName().equals(preselected)) {
				root.printDebugMessage("PluginMatches MATCH");
				gui.combobox.setSelectedIndex(i);
				plugin = plugins.get(i);
				plugin.initialize(root);
				flag = true;
			}
		}
		if (flag == false) {
			JOptionPane
					.showMessageDialog(
							root,
							"Can't find the preconfigured PlugIn\nSwitch to the default PlugIn",
							"PlugIn Error", JOptionPane.ERROR_MESSAGE);
			plugin = plugins.get(0);
		}
	}

	/**
	 * Looks for plugins in the known folders
	 */
	public Vector lookForPlugins() {
		// first the default plugin
		AnnotationListener sim = new SimpleAmigramPlugin(root);
		plugins.add(sim);

		File dir = new File("../plugins/");
		if (dir.exists() == true) {
			File[] files = dir.listFiles();
			
			for (int i = 0; i < files.length; i++) {
				String plugin = files[i].getPath();
				try {
					JarFile jarfile = new JarFile(plugin);
					try {
						ClassPathLoader.addFile(plugin);
					} catch (java.io.IOException e) {
						System.out.println("ERROR while add the plugin to the CLASSPATH");
						e.printStackTrace();
					}

					Enumeration entries = jarfile.entries();

					while (entries.hasMoreElements()) {
						JarEntry jarentry = (JarEntry) entries.nextElement();
						String pluginPath;
						if (jarentry.getName().endsWith(".class")) {
							pluginPath = jarentry.getName().replace("/", ".")
									.replace(".class", "");
							Class plugClass;
							try {
								plugClass = Class.forName(pluginPath);
							} catch (Exception e) {
								System.out.println("Can't get the CLASS:"
										+ "externalplugins." + pluginPath);
								e.printStackTrace();
								continue;
							}
							AnnotationListener plug;
							try {
								plug = (AnnotationListener) plugClass
										.newInstance();
							} catch (Exception e) {
								System.out
										.println("It seems that there is a class in "
												+ plugin
												+ " that implements not the AnnotationListener Interface. "
												+ "The Class ist called: "
												+ pluginPath);
								continue;
							}
							plugins.add(plug);

						}

					}
				} catch (IOException e) {
					
				}

			}

		} else {
			System.out.println("no plugin folder found !!!");
			return plugins;
		}

		
		return plugins;
	}

	/**
	 * Called from the plugin when content is created.
	 */
	public void contentCreated(NOMWriteAnnotation ne) {
	}

	/**
	 * Called from otab and handles the plugin calls.
	 */
	public void elementSelected(AnnotationElement ae) {
		de.dfki.ami.amigram.plugin.AnnotationEvent event = new de.dfki.ami.amigram.plugin.AnnotationEvent(
				ae.nomwriteelement);
		plugin.annoElementSelected(event);
	}

	/**
	 * Called from otab and handles the plugin calls.
	 */
	public void emptyElementSelected(AnnotationElement ae) {
		de.dfki.ami.amigram.plugin.AnnotationEvent event = new de.dfki.ami.amigram.plugin.AnnotationEvent(
				ae.nomwriteelement);
		plugin.emptyAnnoElementSelected(event);
		System.out.println("Manager Select Empty");
	}

	/**
	 * Called from otab and handles the plugin calls.
	 */
	public void elementDoubleClicked(AnnotationElement ae) {
		de.dfki.ami.amigram.plugin.AnnotationEvent event = new de.dfki.ami.amigram.plugin.AnnotationEvent(
				ae.nomwriteelement);
		plugin.annoElementDoubleClicked(event);
		System.out.println("Manager DoubleClicked");
	}

	/**
	 * Called from otab and handles the plugin calls.
	 */
	public void emptyElementDoubleClicked(AnnotationElement ae) {
		de.dfki.ami.amigram.plugin.AnnotationEvent event = new de.dfki.ami.amigram.plugin.AnnotationEvent(
				ae.nomwriteelement);
		plugin.emptyAnnoElementDoubleClicked(event);
		System.out.println("Manager DoubleClicked Empty");
	}

	/**
	 * Opens the gui for the pluginmanager
	 */
	public void showGui() {
		// select the pluin to the
		String preselected = root.config.plugin;
		for (int i = 0; i < plugins.size(); i++) {
			if (plugins.get(i).getName().equals(preselected)) {
				gui.combobox.setSelectedIndex(i);
			}
		}
		// place it in the middle of the screen
		// int screenX =
		// java.awt.Toolkit.getDefaultToolkit().getScreenSize().width;
		// int screenY =
		// java.awt.Toolkit.getDefaultToolkit().getScreenSize().height;
		// gui.setLocation(screenX/2-gui.getWidth()/2,screenY/2-gui.getHeight()/2);
		// gui.setVisible(true);
		gui.showGui();
	}

	/**
	 * Asks whether the plugin should be invoked
	 */
	public boolean invokePlugin(java.util.List attributeList) {
		return plugin.invokePlugin(attributeList);
	}

	private class PluginManagerGui extends JFrame {
		public JComboBox combobox;

		public PluginManagerGui() {
			super("PluginManager");
			// build the combobox
			Vector<String> names = new Vector<String>();
			for (int i = 0; i < plugins.size(); i++) {
				names.add(plugins.get(i).getName());
			}
			combobox = new JComboBox(names);
		}

		public void showGui() {
			String[] options = { "switch to selected PlugIn",
					"leave unchanged", "PluginGui" };
			int result = JOptionPane.showOptionDialog(root, // the parent that
					// the
					// dialog blocks
					combobox, // the dialog message
					// array
					"Plugin Manager", // the title of the
					// dialog window
					JOptionPane.YES_NO_CANCEL_OPTION, // option type
					JOptionPane.INFORMATION_MESSAGE, // message type
					null, // optional icon, use
					// null to use the
					// default icon
					options, // options string array,
					// will be made into
					// buttons
					options[1] // option that should be
					// made into a default
					// button
					);
			switch (result) {
			case 0: // yes
				System.out.println("YES");

				AnnotationListener currentplugin = plugin;
				AnnotationListener selectedplugin = plugins.get(combobox
						.getSelectedIndex());

				if (currentplugin.getName().equals(selectedplugin.getName()) == false) {
					plugin.terminate();
					plugin = plugins.get(combobox.getSelectedIndex());
					plugin.initialize(root);
					root.config.plugin = plugin.getName();
					setVisible(false);
				}
				break;

			case 1: // no
				System.out.println("NO");

				setVisible(false);
				break;
			case 2: // cancel
				System.out.println("CANCEL");
				plugin.showGui();
			default:
				break;
			}
		}
	}
}
