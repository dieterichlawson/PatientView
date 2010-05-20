package net.frontlinesms.plugins.patientview.ui.detailview;

import static net.frontlinesms.ui.i18n.InternationalisationUtils.getI18NString;

import java.util.HashMap;

import net.frontlinesms.plugins.patientview.data.domain.people.Patient;
import net.frontlinesms.plugins.patientview.data.domain.people.User.Role;
import net.frontlinesms.plugins.patientview.ui.dashboard.PatientDashboard;
import net.frontlinesms.plugins.patientview.ui.dashboard.PersonDashboard;
import net.frontlinesms.plugins.patientview.ui.dialogs.SubmitFormDialog;
import net.frontlinesms.plugins.patientview.ui.personpanel.CommunityHealthWorkerPanel;
import net.frontlinesms.plugins.patientview.ui.personpanel.PatientPanel;
import net.frontlinesms.plugins.patientview.ui.personpanel.PersonAttributePanel;
import net.frontlinesms.plugins.patientview.userlogin.UserSessionManager;
import net.frontlinesms.ui.UiGeneratorController;

import org.springframework.context.ApplicationContext;

import thinlet.Thinlet;

public class PatientDetailViewPanelController implements DetailViewPanelController<Patient> {

	private static final String EDIT_PATIENT_ATTRIBUTES = "detailview.buttons.edit.patient.attributes";
	private static final String SAVE_PATIENT_ATTRIBUTES = "detailview.buttons.save";
	private static final String CANCEL = "detailview.buttons.cancel";
	private static final String SEE_MORE = "detailview.buttons.see.more";
	private static final String SUBMIT_FORM_FOR_PATIENT = "detailview.buttons.submit.form.patient";
	private static final String EDIT_ATTRIBUTE_ICON = "/icons/user_edit.png";
	private static final String SAVE_ICON = "/icons/tick.png";
	private static final String CANCEL_ICON = "/icons/cross.png";
	private static final String EXPAND_DETAIL_VIEW_ICON = "/icons/folder_user.png";
	private Object mainPanel;
	private Patient currentPatient;
	private UiGeneratorController uiController;
	private ApplicationContext appCon;
	private boolean inEditingMode;
	
	private PatientPanel currentPatientPanel;
	private CommunityHealthWorkerPanel currentCHWPanel;
	private PersonAttributePanel currentAttributePanel;
	
	public PatientDetailViewPanelController(UiGeneratorController uiController,ApplicationContext appCon){
		this.uiController = uiController;
		this.appCon = appCon;
		inEditingMode=false;
	}
	public Class getEntityClass() {
		return Patient.class;
	}

	public HashMap<String, String> getFurtherOptions() {
		HashMap<String,String> furtherOptions = new HashMap<String,String>();
		furtherOptions.put(getI18NString(SUBMIT_FORM_FOR_PATIENT), "submitForm()");
		return furtherOptions;
	}

	public Object getPanel() {
		return mainPanel;
	}
	
	public void submitForm(){
		SubmitFormDialog sfd = new SubmitFormDialog(uiController,appCon,null,currentPatient);
	}

	public void viewWillAppear(Patient p) {
		inEditingMode=false;
		currentPatient= p;
		mainPanel = uiController.create("panel");
		uiController.setWeight(mainPanel, 1, 1);
		uiController.setColumns(mainPanel, 1);
		currentPatientPanel = new PatientPanel(uiController,appCon,p);
		currentCHWPanel = new CommunityHealthWorkerPanel(uiController,appCon,p.getChw());
		currentAttributePanel = new PersonAttributePanel(uiController,appCon,p);
		uiController.add(mainPanel, currentPatientPanel.getMainPanel());
		uiController.add(mainPanel, currentCHWPanel.getMainPanel());
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
			uiController.setIcon(leftButton, EDIT_ATTRIBUTE_ICON);
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
		inEditingMode=false;
		currentAttributePanel.stopEditingWithSave();
		uiController.remove(uiController.find(mainPanel,"buttonPanel"));
		uiController.add(mainPanel,getBottomButtons());
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
