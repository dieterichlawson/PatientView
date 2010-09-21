package net.frontlinesms.plugins.patientview.ui.detailview.panels;

import static net.frontlinesms.ui.i18n.InternationalisationUtils.getI18NString;
import net.frontlinesms.plugins.patientview.data.domain.people.Patient;
import net.frontlinesms.plugins.patientview.data.domain.people.Person.Gender;
import net.frontlinesms.plugins.patientview.data.domain.people.User.Role;
import net.frontlinesms.plugins.patientview.data.repository.MedicFormFieldDao;
import net.frontlinesms.plugins.patientview.data.repository.PersonAttributeDao;
import net.frontlinesms.plugins.patientview.security.UserSessionManager;
import net.frontlinesms.plugins.patientview.ui.dashboard.PatientDashboard;
import net.frontlinesms.plugins.patientview.ui.detailview.DetailViewPanelController;
import net.frontlinesms.plugins.patientview.ui.personpanel.PatientPanel;
import net.frontlinesms.plugins.patientview.ui.personpanel.PersonAttributePanel;
import net.frontlinesms.ui.UiGeneratorController;

import org.springframework.context.ApplicationContext;

import thinlet.Thinlet;

public class PatientDetailViewPanelController implements DetailViewPanelController<Patient> {

	private static final String EDIT_PATIENT_ATTRIBUTES = "detailview.buttons.edit.attributes";
	private static final String SAVE_PATIENT_ATTRIBUTES = "detailview.buttons.save";
	private static final String CANCEL = "detailview.buttons.cancel";
	private static final String SEE_MORE = "detailview.buttons.see.more";
	private static final String EDIT_ATTRIBUTE_ICON = "/icons/patient_edit_";
	private static final String SAVE_ICON = "/icons/tick.png";
	private static final String CANCEL_ICON = "/icons/cross.png";
	private static final String EXPAND_DETAIL_VIEW_ICON = "/icons/patient_file.png";
	private Object mainPanel;
	private Patient currentPatient;
	private UiGeneratorController uiController;
	private ApplicationContext appCon;
	private boolean inEditingMode;
	
	private PatientPanel currentPatientPanel;
	private PersonAttributePanel currentAttributePanel;
	
	public PatientDetailViewPanelController(UiGeneratorController uiController,ApplicationContext appCon){
		this.uiController = uiController;
		this.appCon = appCon;
		inEditingMode=false;
	}
	public Class getEntityClass() {
		return Patient.class;
	}

	public Object getPanel() {
		return mainPanel;
	}

	public void viewWillAppear(Patient p) {
		inEditingMode=false;
		currentPatient= p;
		mainPanel = uiController.create("panel");
		uiController.setWeight(mainPanel, 1, 1);
		uiController.setColumns(mainPanel, 1);
		currentPatientPanel = new PatientPanel(uiController,appCon,p);
		currentAttributePanel = new PersonAttributePanel(uiController,appCon,p);
		uiController.add(mainPanel, currentPatientPanel.getMainPanel());
		uiController.add(mainPanel, currentAttributePanel.getMainPanel());
		uiController.add(mainPanel,getBottomButtons());
	}
	
	private Object getBottomButtons(){
		Object buttonPanel = uiController.create("panel");
		uiController.setName(buttonPanel, "buttonPanel");
		uiController.setColumns(buttonPanel, 3);
		Object leftButton = uiController.createButton(!inEditingMode?getI18NString(EDIT_PATIENT_ATTRIBUTES):getI18NString(SAVE_PATIENT_ATTRIBUTES));
		Object rightButton = uiController.createButton(!inEditingMode?getI18NString(SEE_MORE):getI18NString(CANCEL));
		if(inEditingMode){
			uiController.setAction(leftButton, "saveButtonClicked", null, this);
			uiController.setAction(rightButton, "cancelButtonClicked", null, this);
			uiController.setIcon(leftButton, SAVE_ICON);
			uiController.setIcon(rightButton, CANCEL_ICON);
			
		}else{
			uiController.setAction(leftButton, "editButtonClicked", null, this);
			uiController.setAction(rightButton, "showPatientDashboard", null, this);
			uiController.setIcon(leftButton, EDIT_ATTRIBUTE_ICON + (currentPatient.getGender() == Gender.MALE?"male.png":"female.png"));
			if(((PersonAttributeDao) appCon.getBean("PersonAttributeDao")).getAllAttributesForPerson(currentPatient).size() == 0 && ((MedicFormFieldDao) appCon.getBean("MedicFormFieldDao")).getAttributePanelFields().size() == 0 ){
				uiController.setEnabled(leftButton,false);
			}
			uiController.setIcon(rightButton, EXPAND_DETAIL_VIEW_ICON);
		}
		uiController.setHAlign(leftButton, Thinlet.LEFT);
		uiController.setVAlign(leftButton, Thinlet.BOTTOM);
		if(UserSessionManager.getUserSessionManager().getCurrentUserRole() == Role.READWRITE||
		   UserSessionManager.getUserSessionManager().getCurrentUserRole() == Role.ADMIN){
			uiController.add(buttonPanel,leftButton);
		}
		Object spacerLabel = uiController.createLabel("");
		uiController.setWeight(spacerLabel, 1, 0);
		uiController.add(buttonPanel,spacerLabel);
		uiController.setHAlign(rightButton, Thinlet.RIGHT);
		uiController.setVAlign(rightButton, Thinlet.BOTTOM);
		uiController.add(buttonPanel, rightButton);
		uiController.setWeight(buttonPanel, 1, 1);
		uiController.setVAlign(buttonPanel, Thinlet.BOTTOM);
		return buttonPanel;
	}

	public void editButtonClicked(){
		inEditingMode=true;
		currentAttributePanel.switchToEditingPanel();
		uiController.remove(uiController.find(mainPanel,"buttonPanel"));
		uiController.add(mainPanel,getBottomButtons());
	}
	
	public void saveButtonClicked(){
		if(currentAttributePanel.stopEditingWithSave()){
			inEditingMode=false;
			uiController.remove(uiController.find(mainPanel,"buttonPanel"));
			uiController.add(mainPanel,getBottomButtons());
		}
	}
	
	public void cancelButtonClicked(){
		inEditingMode=false;
		currentAttributePanel.stopEditingWithoutSave();
		uiController.remove(uiController.find(mainPanel,"buttonPanel"));
		uiController.add(mainPanel,getBottomButtons());
	}
	
	public void showPatientDashboard(){
		PatientDashboard patientDashboard = new PatientDashboard(uiController,appCon,currentPatient);
		patientDashboard.expandDashboard();
	}
	public void viewWillDisappear() {/* do nothing */}

}
