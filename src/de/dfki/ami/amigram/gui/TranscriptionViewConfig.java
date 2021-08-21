
package de.dfki.ami.amigram.gui;

import net.sourceforge.nite.gui.transcriptionviewer.NTranscriptionView;
import net.sourceforge.nite.gui.util.AbstractCallableToolConfig;


public class TranscriptionViewConfig extends AbstractCallableToolConfig {
    /**
     * 
     */
    public String getNXTConfigRootName() {
        return "DACoderConfig";
    }
    /**
     * daelementname attribute in corpussettings
     */
    public String getDAElementName() {
        return getNXTConfig().getCorpusSettingValue("daelementname");
    }
    /**
     * daontology#daroot attributes in corpussettings
     */
    public String getDATypeRoot() {
        return getNXTConfig().getCorpusSettingValue("daontology")+ "#" + getNXTConfig().getCorpusSettingValue("daroot");
    }
    /**
     * datyperole attributes in corpussettings
     */
    public String getDATypeRole() {
        return getNXTConfig().getCorpusSettingValue("datyperole");

    }
    /**
     * dagloss attributes in corpussettings
     */
    public String getDAAGloss() {
        return getNXTConfig().getCorpusSettingValue("dagloss");
    }
    /**
     * apelementname attributes in corpussettings
     */
    public String getAPElementName() {
        return getNXTConfig().getCorpusSettingValue("apelementname");
    }
    /**
     * apontology#aproot attributes in corpussettings
     */
    public String getAPTypeRoot() {
        return getNXTConfig().getCorpusSettingValue("apontology")+ "#" + getNXTConfig().getCorpusSettingValue("aproot");
    }
    /**
     * apgloss attribute in corpussettings
     */
    public String getAPGloss() {
        return getNXTConfig().getCorpusSettingValue("apgloss");
    }
    /**
     * apontology#defaultaptype attributes in corpussettings
     */
    public String getDefaultAPType() {
        return getNXTConfig().getCorpusSettingValue("apontology")+ "#" + getNXTConfig().getCorpusSettingValue("defaultaptype");
    }

    /**
     * neelementname attributes in corpussettings
     */
    public String getNEElementName() {
        return getNXTConfig().getCorpusSettingValue("neelementname");
    }    
    /**
     * neontology#neroot attributes in corpussettings
     */
    public String getNETypeRoot() {
        return getNXTConfig().getCorpusSettingValue("neontology")+ "#" + getNXTConfig().getCorpusSettingValue("neroot");
    }
    /**
     * nenameattribute attributes in corpussettings
     */
    public String getNEDisplayAttribute() {
        return getNXTConfig().getCorpusSettingValue("nenameattribute");
    }    
    /**
     * netyperole attributes in corpussettings
     */
    public String getNETypePointerRole() {
        return getNXTConfig().getCorpusSettingValue("netyperole");
    }    
    /**
     * abbrevattribute attributes in corpussettings
     */
    public String getNEAbbrevAttrib() {
        return getNXTConfig().getCorpusSettingValue("abbrevattribute");
    }    



    /**
     * addresseeignoreattribute:
     * that is the attribute that, if present on a dialogue act type label, will force the tool
     * to grey out the addressee checkboxes so the user is forced to NOT code addressee.
     */
    public String getAddresseeIgnoreAttribute() {
        String result =  getNXTConfig().getCorpusSettingValue("addresseeignoreattribute");
        
        if (result == null) {
            result =  "";
        }
        //System.out.println("addria:"+result);
        return result;
    }    
    
    
    /**
     * Override for your application! Determines which annotation elements should be displayed.
     * Set of strings... maybe a config setting, later
     */
    public void initDisplayedAnnotationNames() {
        super.initDisplayedAnnotationNames();
        displayedAnnotationNames.add(getDAElementName());
    }

    public int getWordlevelSelectionType() {
        return NTranscriptionView.CROSS_SEGMENT_PHRASE;
    }
    public boolean getAllowTranscriptSelect() {
        return true;
    }
    public boolean getAllowAnnotationSelect() {
        return true;
    }
    public boolean getAllowMultiAgentSelect() {
        return false;
    }
    public String getHelpSetName() {
        return "dacoder.hs";
    }

    /**
     * showapwindow attribute in guisettings
     */
    public boolean showAdjacencyPairWindows() {
        return Boolean.valueOf(getNXTConfig().getGuiSettingValue("showapwindow")).booleanValue();
    }



}