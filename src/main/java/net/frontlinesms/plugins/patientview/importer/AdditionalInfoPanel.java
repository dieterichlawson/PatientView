package net.frontlinesms.plugins.patientview.importer;

import net.frontlinesms.ui.UiGeneratorController;
import net.frontlinesms.ui.i18n.InternationalisationUtils;


public class AdditionalInfoPanel {

	private Object mainPanel;
	
	private UiGeneratorController uiController;
	
	private int currentColumn =1;
	
	private static final String UI_FILE_XML = "/ui/plugins/patientview/additionalInfoPanel.xml";
	
	public AdditionalInfoPanel(UiGeneratorController uiController){
		this.uiController = uiController;
		mainPanel = uiController.loadComponentFromFile(UI_FILE_XML);
	}
	
	public void addLine(String line){
		Object firstLabel = uiController.createLabel(InternationalisationUtils.getI18NString("medic.importer.labels.column") + " "+currentColumn+":");
		uiController.add(mainPanel,firstLabel);
		uiController.add(mainPanel,uiController.createLabel(line));
		currentColumn++;
	}
	
	public Object getMainPanel(){
		return mainPanel;
	}
	
}
