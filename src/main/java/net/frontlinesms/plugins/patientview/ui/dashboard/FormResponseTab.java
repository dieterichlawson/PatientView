package net.frontlinesms.plugins.patientview.ui.dashboard;

import java.util.List;

import net.frontlinesms.data.events.DatabaseNotification;
import net.frontlinesms.events.EventBus;
import net.frontlinesms.events.EventObserver;
import net.frontlinesms.events.FrontlineEventNotification;
import net.frontlinesms.plugins.patientview.data.domain.people.CommunityHealthWorker;
import net.frontlinesms.plugins.patientview.data.domain.people.Person;
import net.frontlinesms.plugins.patientview.data.domain.response.MedicFormResponse;
import net.frontlinesms.plugins.patientview.data.repository.hibernate.HibernateMedicFormResponseDao;
import net.frontlinesms.plugins.patientview.ui.AdvancedTableActionDelegate;
import net.frontlinesms.plugins.patientview.ui.AdvancedTableController;
import net.frontlinesms.plugins.patientview.ui.detailview.panels.FormResponseDetailViewPanelController;
import net.frontlinesms.ui.UiGeneratorController;
import net.frontlinesms.ui.i18n.InternationalisationUtils;

import org.springframework.context.ApplicationContext;

public class FormResponseTab<P extends Person> extends TabController implements AdvancedTableActionDelegate, EventObserver {

	protected AdvancedTableController formResponseTable;
	protected HibernateMedicFormResponseDao formResponseDao;
	protected FormResponseDetailViewPanelController formResponsePanel;
	protected P person;
	

	//private static final String FORM_RESPONSES_LABEL = "patientrecord.labels.form.responses";
	private static final String FORM_NAME_COLUMN = "medic.common.labels.form.name";
	private static final String FORM_SENDER_COLUMN = "medic.common.labels.form.sender";
	private static final String FORM_SUBJECT_COLUMN = "medic.common.labels.subject";
	private static final String DATE_SUBMITTED_COLUMN = "medic.common.labels.date.submitted";

	public FormResponseTab(UiGeneratorController uiController, ApplicationContext appCon, P person) {
		super(uiController, appCon);
		this.person = person;
		this.formResponseDao = (HibernateMedicFormResponseDao) appCon.getBean("MedicFormResponseDao");
		init();
	}

	protected void init() {
		super.setTitle(InternationalisationUtils.getI18NString("medic.common.form.responses"));
		super.setIconPath("/icons/big_form.png");
		//register this object as an event observer
		((EventBus) appCon.getBean("eventBus")).registerObserver(this);
		
		formResponsePanel = new FormResponseDetailViewPanelController(uiController, appCon);
		// create the two panels, left for the table containing form responses
		// right for the form response display
		Object rightPanel = uiController.createPanel("");
		Object leftPanel = uiController.createPanel("");
		uiController.setWeight(leftPanel, 3, 1);
		uiController.setWeight(rightPanel, 1, 1);
		uiController.setColumns(super.getMainPanel(), 2);
		// add the form response table
		formResponseTable = new AdvancedTableController(this, uiController);
		formResponseTable.putHeader(MedicFormResponse.class, new String[] { InternationalisationUtils.getI18NString(FORM_NAME_COLUMN), InternationalisationUtils.getI18NString(FORM_SENDER_COLUMN), InternationalisationUtils.getI18NString(FORM_SUBJECT_COLUMN), InternationalisationUtils.getI18NString(DATE_SUBMITTED_COLUMN) }, new String[] { "getFormName", "getSubmitterName", "getSubjectName", "getStringDateSubmitted" });
//		Object fresponses = uiController.createLabel(InternationalisationUtils.getI18NString(FORM_RESPONSES_LABEL) + " " + person.getName());
//		super.uiController.setIcon(fresponses, "/icons/form.png");
		uiController.add(rightPanel, formResponsePanel.getPanel());
		uiController.add(leftPanel,formResponseTable.getTable());
		uiController.add(super.getMainPanel(), leftPanel);
		uiController.add(super.getMainPanel(), rightPanel);
		uiController.setGap(super.getMainPanel(), 5);
		uiController.setInteger(super.getMainPanel(), "top", 5);
		uiController.setInteger(super.getMainPanel(), "left", 5);
		uiController.setInteger(super.getMainPanel(), "right", 5);
		uiController.setInteger(super.getMainPanel(), "bottom", 5);
		
		updateTable();
	}
	
	protected void updateTable(){
		List<MedicFormResponse> mfrs ;
		if(!isCHW()){
			mfrs = formResponseDao.getFormResponsesForSubject(person);
		}else{
			mfrs = formResponseDao.getFormResponsesForSubmitter(person);
		}
		formResponseTable.setResults(mfrs);
		formResponseTable.setSelected(0);
	}
	
	protected boolean isCHW(){
		return (person instanceof CommunityHealthWorker);
	}

	public void doubleClickAction(Object selectedObject) {}

	public void resultsChanged() {}

	public void selectionChanged(Object selectedObject) {
			formResponsePanel.viewWillAppear((MedicFormResponse) selectedObject);
	}

	public void notify(FrontlineEventNotification event) {
		if(event instanceof DatabaseNotification<?>){
			if(((DatabaseNotification<?>) event).getDatabaseEntity() instanceof MedicFormResponse){
				updateTable();
			}
		}
	}

}
