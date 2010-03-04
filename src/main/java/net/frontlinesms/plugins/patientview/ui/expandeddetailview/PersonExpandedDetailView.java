package net.frontlinesms.plugins.patientview.ui.expandeddetailview;

import net.frontlinesms.plugins.patientview.data.domain.people.Patient;
import net.frontlinesms.plugins.patientview.data.domain.people.Person;
import net.frontlinesms.plugins.patientview.data.domain.response.MedicFormResponse;
import net.frontlinesms.plugins.patientview.data.repository.hibernate.HibernateHistoryEntryDao;
import net.frontlinesms.plugins.patientview.data.repository.hibernate.HibernateMedicFormResponseDao;
import net.frontlinesms.plugins.patientview.ui.AdvancedTableActionDelegate;
import net.frontlinesms.plugins.patientview.ui.AdvancedTableController;
import net.frontlinesms.plugins.patientview.ui.detailview.FormResponseDetailViewPanelController;
import net.frontlinesms.plugins.patientview.ui.personpanel.CommunityHealthWorkerPanel;
import net.frontlinesms.plugins.patientview.ui.personpanel.PatientPanel;
import net.frontlinesms.plugins.patientview.ui.personpanel.PersonAttributePanel;
import net.frontlinesms.ui.UiGeneratorController;
import net.frontlinesms.ui.i18n.InternationalisationUtils;

import org.springframework.context.ApplicationContext;

import thinlet.Thinlet;

public class PersonExpandedDetailView extends ExpandedDetailView implements AdvancedTableActionDelegate {

	protected Patient patient;
	protected AdvancedTableController formResponses;
	protected HibernateMedicFormResponseDao formDao;
	protected HibernateHistoryEntryDao historyEntryDao;
	protected FormResponseDetailViewPanelController formResponsePanel;
	
	protected Person person;
	protected AdvancedTableController formTable;
	
	private static final String GO_BACK_BUTTON = "patientrecord.buttons.go.back";
	private static final String TITLE_LABLE = "patientrecord.labels.patient.record";
	private static final String FORM_RESPONSES_LABEL = "patientrecord.labels.form.responses";
	private static final String FORM_NAME_COLUMN = "medic.common.labels.form.name";
	private static final String FORM_SENDER_COLUMN = "medic.common.labels.form.sender";
	private static final String FORM_SUBJECT_COLUMN = "medic.common.labels.subject";
	private static final String DATE_SUBMITTED_COLUMN = "medic.common.labels.date.submitted";
	
	public PersonExpandedDetailView(UiGeneratorController uiController, ApplicationContext appCon, Patient p) {
		super(uiController, appCon);
		this.patient = p;
		this.formDao = (HibernateMedicFormResponseDao) appCon.getBean("MedicFormResponseDao");
		this.historyEntryDao = (HibernateHistoryEntryDao) appCon.getBean("HistoryEntryDao");
		init();
	}

	public void init(){
		//add all the proper left panel stuff
		uiController.add(leftPanel,new PatientPanel(uiController,appCon,patient).getMainPanel());
		uiController.add(leftPanel,new CommunityHealthWorkerPanel(uiController,appCon,patient.getChw()).getMainPanel());
		uiController.add(leftPanel,new PersonAttributePanel(uiController,appCon,patient).getMainPanel());
		Object button = uiController.createButton(InternationalisationUtils.getI18NString(GO_BACK_BUTTON));
		uiController.setAction(button, "goBack()", null, this);
		uiController.setHAlign(button, Thinlet.LEFT);
		uiController.setVAlign(button, Thinlet.BOTTOM);
		uiController.setWeight(button, 1, 1);
		uiController.add(leftPanel,button);
		//configure the form response panel
		formResponsePanel = new FormResponseDetailViewPanelController(uiController,appCon);
		uiController.add(rightPanel,formResponsePanel.getPanel());
		//add the form response table
		formResponses = new AdvancedTableController(this,uiController,false);
		formResponses.putHeader(MedicFormResponse.class, new String[]{InternationalisationUtils.getI18NString(FORM_NAME_COLUMN), 
			InternationalisationUtils.getI18NString(FORM_SENDER_COLUMN), InternationalisationUtils.getI18NString(FORM_SUBJECT_COLUMN),
			InternationalisationUtils.getI18NString(DATE_SUBMITTED_COLUMN)}, new String[]{"getFormName","getSubmitterName","getSubjectName","getStringDateSubmitted"});
		formResponses.setResults(formDao.getFormResponsesForSubject(patient));	
		Object fresponses = uiController.createLabel(InternationalisationUtils.getI18NString(FORM_RESPONSES_LABEL)+" "+patient.getName());
		uiController.setIcon(fresponses, "/icons/form.png");
		uiController.add(middlePanel,fresponses);
		uiController.add(middlePanel,formResponses.getTable());
		formResponses.setSelected(0);
		expandDetailView();
	}

	public void doubleClickAction(Object selectedObject) {
		//do nothing
	}

	public void resultsChanged() {
		// TODO Auto-generated method stub
	}
	
	public Object getTable() {
		return null;
	}

	public void selectionChanged(Object selectedObject) {
		if(selectedObject instanceof MedicFormResponse){
			formResponsePanel.viewWillAppear((MedicFormResponse) selectedObject);
		}
	}
	
	public void goBack(){
		collapseDetailView();
	}
}
