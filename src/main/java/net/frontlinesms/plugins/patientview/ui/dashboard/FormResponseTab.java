package net.frontlinesms.plugins.patientview.ui.dashboard;

import static net.frontlinesms.ui.i18n.InternationalisationUtils.getI18NString;

import java.util.Date;
import java.util.List;

import net.frontlinesms.data.events.DatabaseEntityNotification;
import net.frontlinesms.events.EventBus;
import net.frontlinesms.events.EventObserver;
import net.frontlinesms.events.FrontlineEventNotification;
import net.frontlinesms.plugins.patientview.data.domain.framework.MedicForm;
import net.frontlinesms.plugins.patientview.data.domain.people.CommunityHealthWorker;
import net.frontlinesms.plugins.patientview.data.domain.people.Person;
import net.frontlinesms.plugins.patientview.data.domain.response.MedicFormResponse;
import net.frontlinesms.plugins.patientview.data.repository.MedicFormDao;
import net.frontlinesms.plugins.patientview.search.impl.FormResponseResultSet;
import net.frontlinesms.plugins.patientview.ui.advancedtable.AdvancedTableActionDelegate;
import net.frontlinesms.plugins.patientview.ui.advancedtable.PagedAdvancedTableController;
import net.frontlinesms.plugins.patientview.ui.detailview.panels.FormResponseDetailViewPanelController;
import net.frontlinesms.plugins.patientview.ui.helpers.thinletformfields.DateField;
import net.frontlinesms.plugins.patientview.ui.helpers.thinletformfields.FormFieldDelegate;
import net.frontlinesms.plugins.patientview.ui.helpers.thinletformfields.ThinletFormField;
import net.frontlinesms.ui.UiGeneratorController;

import org.springframework.context.ApplicationContext;
public class FormResponseTab<P extends Person> extends TabController implements AdvancedTableActionDelegate, EventObserver, FormFieldDelegate {

	protected PagedAdvancedTableController formResponseTable;
	protected FormResponseDetailViewPanelController formResponsePanel;
	protected FormResponseResultSet resultSet;
	protected Object comboBox;
	protected P person;
	

	//private static final String FORM_RESPONSES_LABEL = "patientrecord.labels.form.responses";
	private static final String FORM_NAME_COLUMN = "medic.common.labels.form.name";
	private static final String FORM_SENDER_COLUMN = "medic.common.labels.form.sender";
	private static final String FORM_SUBJECT_COLUMN = "medic.common.labels.subject";
	private static final String DATE_SUBMITTED_COLUMN = "medic.common.labels.date.submitted";
	private static final String TAB_TITLE = "medic.common.form.responses";
	private static final String FORM_COMBOBOX_LABEL = "medic.common.form";
	
	private static final String UI_FILE ="/ui/plugins/patientview/dashboard/tabs/formResponseTab.xml";

	public FormResponseTab(UiGeneratorController uiController, ApplicationContext appCon, P person) {
		super(uiController, appCon);
		this.person = person;
		init();
	}

	protected void init() {
		super.setTitle(getI18NString(TAB_TITLE));
		super.setIconPath("/icons/big_form.png");
		//register this object as an event observer
		((EventBus) appCon.getBean("eventBus")).registerObserver(this);
		//set up skeleton
		uiController.add(super.getMainPanel(),uiController.loadComponentFromFile(UI_FILE));
		//set up right panel, the form response panel
		formResponsePanel = new FormResponseDetailViewPanelController(uiController, appCon);
		uiController.add(uiController.find(super.getMainPanel(),"formPanel"),formResponsePanel.getPanel());
		//set up the table, starting with the result set
		resultSet = new FormResponseResultSet(appCon);
		if(isCHW()){
			resultSet.setSubmitter(person);
		}else{
			resultSet.setSubject(person);
		}
		resultSet.setAroundDate(new Date());
		// add the form response table
		formResponseTable = new PagedAdvancedTableController(this, uiController,uiController.find(getMainPanel(),"tablePanel"));
		if(isCHW()){
			formResponseTable.putHeader(MedicFormResponse.class, new String[] { getI18NString(FORM_NAME_COLUMN), getI18NString(FORM_SUBJECT_COLUMN), getI18NString(DATE_SUBMITTED_COLUMN) }, new String[] { "getFormName", "getSubjectName", "getStringDateSubmitted" });
		}else{
			formResponseTable.putHeader(MedicFormResponse.class, new String[] { getI18NString(FORM_NAME_COLUMN), getI18NString(FORM_SENDER_COLUMN), getI18NString(DATE_SUBMITTED_COLUMN) }, new String[] { "getFormName", "getSubmitterName", "getStringDateSubmitted" });
		}
		formResponseTable.setResultsSet(resultSet);
		formResponseTable.updateTable();
		//set up controls
		//create the date controls
		DateField dateField = new DateField(uiController,getI18NString(DATE_SUBMITTED_COLUMN),this);
		dateField.setLabelIcon("/icons/date.png");
		uiController.add(uiController.find(getMainPanel(),"controlPanel"),dateField.getThinletPanel());
		uiController.add(uiController.find(getMainPanel(),"controlPanel"),uiController.createLabel("   "));
		//create the form combo box
		List<MedicForm> forms = ((MedicFormDao) appCon.getBean("MedicFormDao")).getAllMedicForms();
		comboBox = uiController.create("combobox");
		uiController.add(comboBox,uiController.createComboboxChoice("All Forms", null));
		for(MedicForm mf: forms){
			uiController.add(comboBox,uiController.createComboboxChoice(mf.getName(), mf));
		}
		uiController.setAction(comboBox, "formChanged(this.selected)", null, this);
		uiController.setWeight(comboBox,1,0);
		Object label = uiController.createLabel(getI18NString(FORM_COMBOBOX_LABEL));
		uiController.setIcon(label, "/icons/form.png");
		uiController.add(uiController.find(getMainPanel(),"controlPanel"),label);
		uiController.add(uiController.find(getMainPanel(),"controlPanel"),comboBox);
		//add the spacer
		Object spacer = uiController.createLabel("");
		uiController.setWeight(spacer, 1, 0);
		uiController.add(uiController.find(getMainPanel(),"controlPanel"),spacer);
	}
	

	
	protected boolean isCHW(){
		return (person instanceof CommunityHealthWorker);
	}


	public void selectionChanged(Object selectedObject) {
			formResponsePanel.viewWillAppear((MedicFormResponse) selectedObject);
	}

	public void notify(FrontlineEventNotification event) {
		if(event instanceof DatabaseEntityNotification<?>){
			if(((DatabaseEntityNotification<?>) event).getDatabaseEntity() instanceof MedicFormResponse){
				formResponseTable.updateTable();
			}
		}
	}

	public void doubleClickAction(Object selectedObject) {}
	public void resultsChanged() {}

	public void formFieldChanged(ThinletFormField changedField, String newValue) {
		resultSet.setAroundDate(((DateField) changedField).getRawResponse());
		formResponseTable.updateTable();
	}
	
	public void formChanged(int selectedIndex){
		MedicForm mf = (MedicForm) uiController.getAttachedObject(uiController.getItem(comboBox, selectedIndex));
		resultSet.setForm(mf);
		formResponseTable.updateTable();
	}
}
