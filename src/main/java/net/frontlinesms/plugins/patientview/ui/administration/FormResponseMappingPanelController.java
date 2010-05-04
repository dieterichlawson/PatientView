package net.frontlinesms.plugins.patientview.ui.administration;

import java.awt.Font;

import net.frontlinesms.plugins.patientview.analysis.FormMatcher;
import net.frontlinesms.plugins.patientview.data.domain.people.Patient;
import net.frontlinesms.plugins.patientview.data.domain.response.MedicFormResponse;
import net.frontlinesms.plugins.patientview.ui.AdvancedTableActionDelegate;
import net.frontlinesms.plugins.patientview.ui.PagedAdvancedTableController;
import net.frontlinesms.plugins.patientview.ui.administration.FormResponseMappingQueryGenerator.SearchState;
import net.frontlinesms.plugins.patientview.ui.components.CandidateSearchPanel;
import net.frontlinesms.plugins.patientview.ui.components.FlexibleFormResponsePanel;
import net.frontlinesms.plugins.patientview.ui.personpanel.PatientPanel;
import net.frontlinesms.ui.ThinletUiEventHandler;
import net.frontlinesms.ui.UiGeneratorController;

import org.springframework.context.ApplicationContext;

public class FormResponseMappingPanelController implements AdministrationTabPanel, ThinletUiEventHandler, AdvancedTableActionDelegate{

	private UiGeneratorController uiController;
	private ApplicationContext appCon;
	
	private Object mainPanel;
	private Object actionPanel;
	private Object bottomPanel;
	private PagedAdvancedTableController tableController;
	private FormResponseMappingQueryGenerator queryGenerator;
	private FormMatcher matcher;
	
	private static final String UI_FILE ="/ui/plugins/patientview/admintab/manageFormResponsesPanel.xml";
	
	public FormResponseMappingPanelController(UiGeneratorController uiController, ApplicationContext appCon) {
		this.uiController = uiController;
		this.appCon = appCon;
		this.matcher = new FormMatcher(appCon);
		init();
	}

	private void init(){
		this.mainPanel = uiController.loadComponentFromFile(UI_FILE,this);
		actionPanel = uiController.find(mainPanel,"actionPanel");
		
		tableController = new PagedAdvancedTableController(this,uiController, uiController.find(mainPanel,"tablePanel"));
		tableController.putHeader(MedicFormResponse.class, new String[]{"Form Name","Date Submitted","Submitted By","Decided"}, new String[]{"getFormName","getStringDateSubmitted","getSubmitterName","isMappedString"});
		tableController.setNoResultsMessage("You have not received any form responses yet");
		queryGenerator = new FormResponseMappingQueryGenerator(appCon,tableController);
		tableController.setQueryGenerator(queryGenerator);
		bottomPanel=uiController.find(mainPanel,"bottomPanel");
		queryGenerator.setSearchState(SearchState.UNMAPPED);
		queryGenerator.startSearch();
	}
	
	public String getListItemTitle() {
		return "Map Form Responses";
	}

	public Object getPanel() {
		return mainPanel;
	}


	public void selectionChanged(Object selectedObject) {
		MedicFormResponse response = (MedicFormResponse) selectedObject;
		uiController.removeAll(actionPanel);
		Object label = uiController.createLabel("Form Response");
		uiController.setFont(label, new Font("Sans Serif",Font.BOLD,14));
		uiController.setWeight(label,1,0);
		uiController.add(actionPanel,label);
		Object separator = uiController.create("separator");
		uiController.setWeight(separator, 1, 0);
		uiController.add(actionPanel,separator);
		uiController.add(actionPanel,new FlexibleFormResponsePanel(uiController, appCon,response).getMainPanel());
		if(response.isMapped()){
			Object label2 = uiController.createLabel("Response Was Mapped To");
			uiController.setFont(label2, new Font("Sans Serif",Font.BOLD,14));
			uiController.setWeight(label2,1,0);
			uiController.add(actionPanel,label2);
			Object separator2 = uiController.create("separator");
			uiController.setWeight(separator2, 1, 0);
			uiController.add(actionPanel,separator2);
			PatientPanel panel = new PatientPanel(uiController,appCon,(Patient) response.getSubject());
			panel.setPanelTitle("");
			uiController.add(actionPanel,panel.getMainPanel());
			Object confidenceLabel = uiController.createLabel("With a confidence of " + matcher.getConfidence((Patient) response.getSubject(), response)+"%");
			uiController.setWeight(confidenceLabel,1,0);
			uiController.setHAlign(confidenceLabel, "center");
			uiController.add(actionPanel,confidenceLabel);

		}
		uiController.removeAll(bottomPanel);
		uiController.add(bottomPanel,new CandidateSearchPanel(uiController, appCon, response).getMainPanel());
	}

	
	public void toggleChanged(Object button){
		if(uiController.getName(button).equals("mappedToggle")){
			queryGenerator.setSearchState(SearchState.MAPPED);
		}else if(uiController.getName(button).equals("unmappedToggle")){
			queryGenerator.setSearchState(SearchState.UNMAPPED);
		}else{
			queryGenerator.setSearchState(SearchState.ALL);
		}
		queryGenerator.startSearch();
	}

	public void doubleClickAction(Object selectedObject) {/*do nothing*/}
	public void resultsChanged() {/*do nothing*/}
}
