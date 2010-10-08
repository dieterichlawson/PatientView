package net.frontlinesms.plugins.patientview.ui.dashboard.tabs;

import net.frontlinesms.plugins.patientview.data.domain.framework.MedicForm;
import net.frontlinesms.plugins.patientview.data.domain.framework.MedicFormField;
import net.frontlinesms.plugins.patientview.data.domain.people.Patient;
import net.frontlinesms.plugins.patientview.data.domain.response.MedicFormFieldResponse;
import net.frontlinesms.plugins.patientview.data.domain.response.MedicFormResponse;
import net.frontlinesms.plugins.patientview.data.repository.MedicFormResponseDao;
import net.frontlinesms.plugins.patientview.data.repository.hibernate.HibernateMedicFormResponseDao;
import net.frontlinesms.plugins.patientview.security.UserSessionManager;
import net.frontlinesms.plugins.patientview.ui.components.FormSearchArea;
import net.frontlinesms.plugins.patientview.ui.components.SearchAreaDelegate;
import net.frontlinesms.plugins.patientview.ui.thinletformfields.ThinletFormField;
import net.frontlinesms.plugins.patientview.ui.thinletformfields.fieldgroups.FormFieldGroup;
import net.frontlinesms.ui.ThinletUiEventHandler;
import net.frontlinesms.ui.UiGeneratorController;
import net.frontlinesms.ui.i18n.InternationalisationUtils;

import org.springframework.context.ApplicationContext;

import thinlet.Thinlet;

public class SubmitFormTab extends TabController implements ThinletUiEventHandler, SearchAreaDelegate<MedicForm>{
	
	private static final String SUBMIT_FORM_DIALOG = "/ui/plugins/patientview/submitFormPanel.xml";
	
	/**Thinlet Components**/
	private Object formPanel;
	private Object searchPanel;
	private Object warningLabel;
	/**
	 * the search areas where the user selects the form to submit, the patient
	 * to submit about, and the chw to submit for
	 */
	private FormSearchArea formSearch;
	
	/**The currently selected entities**/
	private Patient currentPatient;
	private MedicForm currentForm;
	
	private FormFieldGroup fieldGroup;
	
	/**daos**/
	private MedicFormResponseDao responseDao;
	
	//i18n
	private static final String TITLE ="submitform.title";
	private static final String MUST_FILL_OUT_FORM_MESSAGE ="submitform.messages.must.fill.out";
	private static final String MUST_SELECT_PATIENT_MESSAGE ="submitform.messages.select.patient";
	private static final String SUCCESSFUL_SUBMIT_MESSAGE ="submitform.messages.successful.submit";
	
	public SubmitFormTab(UiGeneratorController uiController, ApplicationContext appContext, Patient patient){
		super(uiController, appContext);
		super.setIconPath("/icons/big_form_edit.png");
		super.setTitle(InternationalisationUtils.getI18NString("submitform.title"));
		//load the main panel from the file
		uiController.add(mainPanel,uiController.loadComponentFromFile(SUBMIT_FORM_DIALOG, this));
		//create the new thinlet instance, and add the main panel
		//initialize the two panels
		formPanel = uiController.find(mainPanel,"formPanel");
		searchPanel = uiController.find(mainPanel,"searchPanel");
		warningLabel = uiController.find(mainPanel,"warningLabel");
		//if this dialog was passed entities, set them
		currentPatient = patient;
		//create the search areas
		//Object searchPanel = uiController.loadComponentFromFile(filename, thinletEventHandler)
		formSearch = new FormSearchArea(uiController,appCon,this);
		//initialize the daos
		responseDao = (HibernateMedicFormResponseDao) appContext.getBean("MedicFormResponseDao");
		//add the search areas, with separators inbetween
		uiController.add(searchPanel,formSearch.getMainPanel());
		Object separator = Thinlet.create("separator");
		uiController.setInteger(separator, "weightx", 1);
		Object separator1 = Thinlet.create("separator");
		uiController.setInteger(separator1, "weightx", 1);
		uiController.add(searchPanel,formSearch.getMainPanel());
		//initialize the form panel
		updateFormPanel();
	}
	
	public void updateFormPanel(){
			//clear the panel
		uiController.removeAll(formPanel);
		fieldGroup = new FormFieldGroup(uiController, appCon, currentForm, null);
		uiController.add(formPanel,fieldGroup.getMainPanel());
		fieldGroup.autoFillWithPatient(currentPatient);
		clearWarningLabel();
	}
	
	@SuppressWarnings("unchecked")
	public void submit(){
		if(currentForm == null){
			setWarningLabel(InternationalisationUtils.getI18NString(MUST_FILL_OUT_FORM_MESSAGE));
			return;
		}
		if(currentPatient == null){
			setWarningLabel(InternationalisationUtils.getI18NString(MUST_SELECT_PATIENT_MESSAGE));
			return;
		}
		if(fieldGroup.validate(true)){
			MedicFormResponse response = new MedicFormResponse(currentForm,UserSessionManager.getUserSessionManager().getCurrentUser(),currentPatient);
			for(ThinletFormField f:fieldGroup.getFormFields()){
				MedicFormFieldResponse rv = null;
				//create the field response
				rv = new MedicFormFieldResponse(f.getStringResponse(), (MedicFormField) f.getField(),response,currentPatient,UserSessionManager.getUserSessionManager().getCurrentUser());
				//and add it to the form response
				response.addFieldResponse(rv);
			}
			responseDao.saveMedicFormResponse(response);
			updateFormPanel();
			setSuccessLabel(InternationalisationUtils.getI18NString(SUCCESSFUL_SUBMIT_MESSAGE));
		}
	}
	
	public void setSuccessLabel(String s){
		uiController.setIcon(warningLabel, "/icons/thumb_up.png");
		setLabel(s);
	}
	
	public void setWarningLabel(String s){
		uiController.setIcon(warningLabel, "/icons/cross.png");
		setLabel(s);
	}
	
	public void setLabel(String s){
		uiController.setText(warningLabel, s);
		uiController.setVisible(warningLabel, true);
	}
	
	public void clearWarningLabel(){
		uiController.setText(warningLabel, "");
		uiController.setVisible(warningLabel, false);
	}

	public void selectionChanged(MedicForm selectedObject) {
		currentForm = selectedObject;
		updateFormPanel();
	}

}
