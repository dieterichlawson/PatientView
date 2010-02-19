package net.frontlinesms.plugins.patientview.ui.expandeddetailview;

import net.frontlinesms.plugins.patientview.data.domain.framework.HistoryEntry;
import net.frontlinesms.plugins.patientview.data.domain.people.Patient;
import net.frontlinesms.plugins.patientview.data.domain.people.Person;
import net.frontlinesms.plugins.patientview.data.domain.response.MedicFormResponse;
import net.frontlinesms.plugins.patientview.data.repository.hibernate.HibernateHistoryEntryDao;
import net.frontlinesms.plugins.patientview.data.repository.hibernate.HibernateMedicFormResponseDao;
import net.frontlinesms.plugins.patientview.ui.AdvancedTable;
import net.frontlinesms.plugins.patientview.ui.DetailedViewController;
import net.frontlinesms.plugins.patientview.ui.TableActionDelegate;
import net.frontlinesms.ui.UiGeneratorController;
import net.frontlinesms.ui.i18n.InternationalisationUtils;

import org.springframework.context.ApplicationContext;

public class PersonExpandedDetailView extends ExpandedDetailView implements TableActionDelegate {

	protected DetailedViewController dvController;
	protected Patient patient;
	protected AdvancedTable formResponses;
	protected AdvancedTable historyMessages;
	protected HibernateMedicFormResponseDao formDao;
	protected HibernateHistoryEntryDao historyEntryDao;
	
	private static final String GO_BACK_BUTTON = "patientrecord.buttons.go.back";
	private static final String TITLE_LABLE = "patientrecord.labels.patient.record";
	private static final String FORM_RESPONSES_LABEL = "patientrecord.labels.form.responses";
	private static final String RECENT_HISTORY_LABEL = "patientrecord.labels.recent.history";
	private static final String FORM_NAME_COLUMN = "medic.common.labels.form.name";
	private static final String FORM_SENDER_COLUMN = "medic.common.labels.form.sender";
	private static final String FORM_SUBJECT_COLUMN = "medic.common.labels.subject";
	private static final String DATE_SUBMITTED_COLUMN = "medic.common.labels.date.submitted";
	private static final String HISTORY_SUBJECT_COLUMN = "medic.common.labels.subject";
	private static final String HISTORY_DATE_COLUMN = "medic.common.labels.date";
	private static final String HISTORY_ACTOR_COLUMN = "medic.common.labels.actor";
	private static final String HISTORY_MESSAGE_COLUMN = "medic.common.labels.message";
	
	public PersonExpandedDetailView(UiGeneratorController uiController, ApplicationContext appCon, Patient p, DetailedViewController dvController) {
		super(uiController, appCon);
		this.patient = p;
		this.dvController = dvController;
		this.formDao = (HibernateMedicFormResponseDao) appCon.getBean("MedicFormResponseDao");
		this.historyEntryDao = (HibernateHistoryEntryDao) appCon.getBean("HistoryEntryDao");
		init();
	}

	public void init(){
		uiController.add(leftPanel,dvController.getPersonPanel(patient, this));
		formResponses = new AdvancedTable(this,uiController,false);
		formResponses.putHeader(MedicFormResponse.class, new String[]{InternationalisationUtils.getI18NString(FORM_NAME_COLUMN), 
			InternationalisationUtils.getI18NString(FORM_SENDER_COLUMN), InternationalisationUtils.getI18NString(FORM_SUBJECT_COLUMN),
			InternationalisationUtils.getI18NString(DATE_SUBMITTED_COLUMN)}, new String[]{"getFormName","getSubmitterName","getSubjectName","getStringDateSubmitted"});
		formResponses.setResults(formDao.getFormResponsesForSubject(patient));	
		historyMessages = new AdvancedTable(this,uiController,false);
		historyMessages.putHeader(HistoryEntry.class, new String[]{InternationalisationUtils.getI18NString(HISTORY_SUBJECT_COLUMN), 
			InternationalisationUtils.getI18NString(HISTORY_DATE_COLUMN), InternationalisationUtils.getI18NString(HISTORY_ACTOR_COLUMN),
			InternationalisationUtils.getI18NString(HISTORY_MESSAGE_COLUMN)}, new String[]{"getSubjectName","getStringDateSubmitted","getActorName","getMessage"});
		historyMessages.setResults(historyEntryDao.getHistoryEntriesForSubject(patient));
		Object fresponses = uiController.createLabel(InternationalisationUtils.getI18NString(FORM_RESPONSES_LABEL)+" "+patient.getName());
		uiController.setIcon(fresponses, "/icons/form.png");
		uiController.add(middlePanel,fresponses);
		uiController.add(middlePanel,formResponses.getTable());
		Object hItems = uiController.createLabel(InternationalisationUtils.getI18NString(RECENT_HISTORY_LABEL)+" "+patient.getName());
		uiController.setIcon(hItems, "/icons/note.png");
		uiController.add(middlePanel,hItems);
		uiController.add(middlePanel,historyMessages.getTable());
		formResponses.setSelected(0);
	}

	protected Person person;
	protected AdvancedTable historyTable;
	protected AdvancedTable formTable;
	
	
	public void formTableSelectionChanged(){
		
	}

	public void doubleClickAction(Object selectedObject) {
		//do nothing
	}

	public Object getTable() {
		return null;
	}

	public void selectionChanged(Object selectedObject) {
		if(selectedObject.getClass() == MedicFormResponse.class){
			uiController.removeAll(rightPanel);
			uiController.add(rightPanel,dvController.switchToFormResponsePanel((MedicFormResponse) selectedObject,this));
		}
	}
	
	public void goBack(){
		dvController.getParent().collapseDetailView();
	}
	public void loadImage(){
		
	}

	public void resultsChanged() {
		// TODO Auto-generated method stub
		
	}
	
}
