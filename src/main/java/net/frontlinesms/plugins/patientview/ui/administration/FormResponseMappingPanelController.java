package net.frontlinesms.plugins.patientview.ui.administration;

import static net.frontlinesms.ui.i18n.InternationalisationUtils.getI18NString;

import java.awt.Font;

import net.frontlinesms.events.EventBus;
import net.frontlinesms.events.EventObserver;
import net.frontlinesms.events.FrontlineEventNotification;
import net.frontlinesms.plugins.patientview.PatientViewPluginController;
import net.frontlinesms.plugins.patientview.analysis.FormMatcher;
import net.frontlinesms.plugins.patientview.data.domain.people.Patient;
import net.frontlinesms.plugins.patientview.data.domain.response.MedicFormResponse;
import net.frontlinesms.plugins.patientview.data.repository.MedicFormResponseDao;
import net.frontlinesms.plugins.patientview.search.impl.FormMappingResultSet;
import net.frontlinesms.plugins.patientview.ui.AdvancedTableActionDelegate;
import net.frontlinesms.plugins.patientview.ui.PagedAdvancedTableController;
import net.frontlinesms.plugins.patientview.ui.components.CandidateSearchPanel;
import net.frontlinesms.plugins.patientview.ui.components.FlexibleFormResponsePanel;
import net.frontlinesms.plugins.patientview.ui.personpanel.PatientPanel;
import net.frontlinesms.ui.ThinletUiEventHandler;
import net.frontlinesms.ui.UiGeneratorController;

import org.hibernate.Hibernate;
import org.springframework.context.ApplicationContext;
public class FormResponseMappingPanelController implements AdministrationTabPanel, ThinletUiEventHandler, AdvancedTableActionDelegate, EventObserver{

	private UiGeneratorController uiController;
	private ApplicationContext appCon;
	
	private Object mainPanel;
	private Object actionPanel;
	private Object bottomPanel;
	private PagedAdvancedTableController tableController;
	private FormMappingResultSet resultSet;
	private FormMatcher matcher;
	private MedicFormResponse currentResponse;
	private MedicFormResponseDao responseDao;
	private CandidateSearchPanel currentSearchPanel;
	
	private static final String UI_FILE ="/ui/plugins/patientview/administration/responsemapping/formResponseMappingAdministrationPanel.xml";
	
	public FormResponseMappingPanelController(UiGeneratorController uiController, ApplicationContext appCon) {
		this.uiController = uiController;
		this.appCon = appCon;
		this.matcher = PatientViewPluginController.getFormMatcher();
		((EventBus) appCon.getBean("eventBus")).registerObserver(this);
		this.responseDao = (MedicFormResponseDao) appCon.getBean("MedicFormResponseDao");
		init();
	}

	private void init(){
		this.mainPanel = uiController.loadComponentFromFile(UI_FILE,this);
		actionPanel = uiController.find(mainPanel,"actionPanel");
		
		tableController = new PagedAdvancedTableController(this,uiController, uiController.find(mainPanel,"tablePanel"));
		tableController.putHeader(MedicFormResponse.class, new String[]{getI18NString("medic.common.labels.form.name"),getI18NString("medic.common.labels.date.submitted"),getI18NString("medic.common.labels.submitter")}, new String[]{"getFormName","getStringDateSubmitted","getSubmitterName"});
		tableController.setNoResultsMessage(getI18NString("medic.form.response.mapping.panel.no.responses.yet"));
		resultSet = new FormMappingResultSet(appCon);
		resultSet.setSearchingMapped(false);
		tableController.setResultsManager(resultSet);
		bottomPanel=uiController.find(mainPanel,"bottomPanel");
		tableController.updateTable();
	}
	
	public String getListItemTitle() {
		return getI18NString("admin.actionlist.map.form.responses");
	}

	public Object getPanel() {
		return mainPanel;
	}


	public void selectionChanged(Object selectedObject) {
		if(selectedObject == null){
			uiController.removeAll(bottomPanel);
			uiController.removeAll(actionPanel);
			return;
		}
		currentResponse = (MedicFormResponse) selectedObject;
		uiController.removeAll(actionPanel);
		Object label = uiController.createLabel(getI18NString("medic.common.form.response"));
		uiController.setFont(label, new Font("Sans Serif",Font.BOLD,14));
		uiController.setWeight(label,1,0);
		uiController.add(actionPanel,label);
		Object separator = uiController.create("separator");
		uiController.setWeight(separator, 1, 0);
		uiController.add(actionPanel,separator);
		uiController.add(actionPanel,new FlexibleFormResponsePanel(uiController, appCon,currentResponse).getMainPanel());
		if(currentResponse.isMapped()){
			Object label2 = uiController.createLabel(getI18NString("medic.form.response.mapping.panel.response.mapped.to"));
			uiController.setFont(label2, new Font("Sans Serif",Font.BOLD,14));
			uiController.setWeight(label2,1,0);
			uiController.add(actionPanel,label2);
			Object separator2 = uiController.create("separator");
			uiController.setWeight(separator2, 1, 0);
			uiController.add(actionPanel,separator2);
			PatientPanel panel = new PatientPanel(uiController,appCon,(Patient) currentResponse.getSubject());
			panel.setPanelTitle("");
			uiController.add(actionPanel,panel.getMainPanel());
			Object confidenceLabel = uiController.createLabel(getI18NString("medic.form.response.mapping.panel.with.confidence")+" " + matcher.getConfidence((Patient) currentResponse.getSubject(), currentResponse)+"%");
			uiController.setWeight(confidenceLabel,1,0);
			uiController.setHAlign(confidenceLabel, "center");
			uiController.add(actionPanel,confidenceLabel);
		}else{
			//create the title label: "Top Candidate"
			Object label2 = uiController.createLabel(getI18NString("medic.candidate.search.panel.top.candidate"));
			uiController.setFont(label2, new Font("Sans Serif",Font.BOLD,14));
			uiController.setWeight(label2,1,0);
			uiController.add(actionPanel,label2);
			//create separator
			Object separator2 = uiController.create("separator");
			uiController.setWeight(separator2, 1, 0);
			uiController.add(actionPanel,separator2);
			//get the top candidate for the form response
			Patient topCandidate = matcher.getCandidatesForResponse(currentResponse).get(0).getPatient();
			PatientPanel panel = new PatientPanel(uiController,appCon,topCandidate);
			panel.setPanelTitle("");
			//add the person panel for the top candidate
			uiController.add(actionPanel,panel.getMainPanel());
			//create the confidence label: "Confidence is XX%"
			Object confidenceLabel = uiController.createLabel(getI18NString("medic.form.response.mapping.panel.confidence.is")+" " + matcher.getConfidence(topCandidate, currentResponse)+"%");
			uiController.setWeight(confidenceLabel,1,0);
			uiController.setHAlign(confidenceLabel, "center");
			uiController.add(actionPanel,confidenceLabel);
			//create the map button
			Object mapButton = uiController.createButton(getI18NString("medic.candidate.search.panel.map.this.person"));
			uiController.setWeight(mapButton,1,0);
			uiController.setHAlign(mapButton, "center");
			uiController.setAction(mapButton, "mapResponse(this)", null,this);
			uiController.setAttachedObject(mapButton, topCandidate);
			uiController.add(actionPanel,mapButton);
		}
		uiController.removeAll(bottomPanel);
		boolean expanded = currentSearchPanel == null? false: currentSearchPanel.isExpanded();
		currentSearchPanel = new CandidateSearchPanel(uiController, appCon, currentResponse, expanded);
		uiController.add(bottomPanel,currentSearchPanel.getMainPanel());
	}
	
	public void mapResponse(Object button){
		currentResponse = responseDao.reattach(currentResponse);
		Hibernate.initialize(currentResponse);
		Hibernate.initialize(currentResponse.getResponses());
		currentResponse.setSubject((Patient) uiController.getAttachedObject(button));
		//((SessionFactory) appCon.getBean("sessionFactory")).getCurrentSession().merge(response);
		responseDao.updateMedicFormResponse(currentResponse);
		tableController.updateTable();
		tableController.setSelected(0);
	}

	
	public void toggleChanged(Object button){
		if(uiController.getName(button).equals("mappedToggle")){
			resultSet.setSearchingMapped(true);
		}else if(uiController.getName(button).equals("unmappedToggle")){
			resultSet.setSearchingMapped(false);
		}
		tableController.updateTable();
		tableController.updatePagingControls();
	}

	public void doubleClickAction(Object selectedObject) {/*do nothing*/}
	public void resultsChanged() {/*do nothing*/}

	public void notify(FrontlineEventNotification notification) {
		tableController.updateTable();
		tableController.setSelected(0);
	}

	public String getIconPath() {
		return "/icons/map_form.png";
	}
}
