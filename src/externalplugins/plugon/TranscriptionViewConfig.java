/**
 * Natural Interactivity Tools Engineering
 * Copyright (c) 2004, 
 * See the README file in this distribution for licence.
 */
package externalplugins.plugon;
import net.sourceforge.nite.gui.transcriptionviewer.NTranscriptionView;
import net.sourceforge.nite.gui.util.AbstractCallableToolConfig;

/**
 * See also superclass and NXTConfig class.
 *
 * name of root element of DACoder settings:
 * "DACoderConfig"
 * Extensive documentation in the tool help of the dialogue act coder, under the heading 'Customization'.
 
 annotator specific codings in dacoder:
 * @author Dennis Reidsma, UTwente
 */
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