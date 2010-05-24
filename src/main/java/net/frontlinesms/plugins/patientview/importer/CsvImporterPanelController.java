package net.frontlinesms.plugins.patientview.importer;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JFileChooser;

import net.frontlinesms.plugins.patientview.ui.administration.AdministrationTabPanel;
import net.frontlinesms.ui.ThinletUiEventHandler;
import net.frontlinesms.ui.UiGeneratorController;
import net.frontlinesms.ui.i18n.InternationalisationUtils;

import org.springframework.context.ApplicationContext;


public class CsvImporterPanelController implements AdministrationTabPanel, ThinletUiEventHandler{

	private Object mainPanel;
	private Object messageList;
	private Object dataTypeComboBox;
	private Object fileTextField;
	private Object additionalOptionsPanel;
	
	private List<CsvDataImporter> importers;
	
	private ApplicationContext appContext;
	
	private UiGeneratorController uiController;
	private Object infoPanel;
	
	private static final String UI_FILE_XML = "/ui/plugins/patientview/administration/dataimport/dataImportAdministrationPanel.xml";
	
	
	public CsvImporterPanelController(UiGeneratorController uiController, ApplicationContext appCon){
		this.uiController = uiController;
		this.appContext = appCon;
		init();
	}
	
	private void init(){
		mainPanel = uiController.loadComponentFromFile(UI_FILE_XML, this);
		messageList = uiController.find(mainPanel,"messageArea");
		fileTextField = uiController.find(mainPanel,"pathField");
		additionalOptionsPanel= uiController.find(mainPanel,"additionalOptionsPanel");
		infoPanel = uiController.find(mainPanel,"infoPanel");
		dataTypeComboBox = uiController.find(mainPanel,"dataTypeComboBox");

		//initialize the importers
		importers = new ArrayList<CsvDataImporter>();
		importers.add(new CommunityHealthWorkerDataImporter(messageList, uiController, appContext));
		importers.add(new PatientDataImporter(messageList,uiController,appContext));
		importers.add(new FormResponseDataImporter(messageList, uiController, appContext));
		for(CsvDataImporter di: importers){
			uiController.add(dataTypeComboBox,uiController.createComboboxChoice(di.getTypeLabel(), di));
		}
		uiController.setSelectedIndex(dataTypeComboBox, 0);
		uiController.setText(dataTypeComboBox, importers.get(0).getTypeLabel());
		datatypeChanged();
	}
	
	public String getListItemTitle() {
		return InternationalisationUtils.getI18NString("medic.importer.tab.title");
	}

	public Object getPanel() {
		return mainPanel;
	}
	
	public void datatypeChanged(){
		uiController.removeAll(additionalOptionsPanel);
		uiController.removeAll(infoPanel);
		CsvDataImporter importer = uiController.getAttachedObject(uiController.getSelectedItem(dataTypeComboBox),CsvDataImporter.class);
		uiController.add(additionalOptionsPanel,importer.getAdditionalOptionsPanel());
		uiController.add(infoPanel,importer.getInformationPanel());
	}
	
	public void browseButtonClicked(){
		JFileChooser fc = new JFileChooser();
		int returnVal = fc.showDialog(null, InternationalisationUtils.getI18NString("medic.common.label.open"));
		if(returnVal == JFileChooser.APPROVE_OPTION){
			uiController.setText(fileTextField, fc.getSelectedFile().getAbsolutePath());
		}

	}
	
	public void importButtonClicked(){
		CsvDataImporter importer = uiController.getAttachedObject(uiController.getSelectedItem(dataTypeComboBox), CsvDataImporter.class);
		importer.importFile(uiController.getText(fileTextField));
	}
	
	public void clearLog(){
		uiController.setText(messageList, "");
	}

	public String getIconPath() {
		return "/icons/import_data.png";
	}

}
