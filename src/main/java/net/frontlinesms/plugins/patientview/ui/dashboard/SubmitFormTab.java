package net.frontlinesms.plugins.patientview.ui.dashboard;

import java.util.ArrayList;

import net.frontlinesms.plugins.patientview.data.domain.framework.DataType;
import net.frontlinesms.plugins.patientview.data.domain.framework.MedicForm;
import net.frontlinesms.plugins.patientview.data.domain.framework.MedicFormField;
import net.frontlinesms.plugins.patientview.data.domain.framework.MedicFormField.PatientFieldMapping;
import net.frontlinesms.plugins.patientview.data.domain.people.Patient;
import net.frontlinesms.plugins.patientview.data.domain.response.MedicFormFieldResponse;
import net.frontlinesms.plugins.patientview.data.domain.response.MedicFormResponse;
import net.frontlinesms.plugins.patientview.data.repository.hibernate.HibernateMedicFormResponseDao;
import net.frontlinesms.plugins.patientview.ui.components.FormSearchArea;
import net.frontlinesms.plugins.patientview.ui.dialogs.searchareas.SearchAreaDelegate;
import net.frontlinesms.plugins.patientview.ui.helpers.thinletformfields.DateField;
import net.frontlinesms.plugins.patientview.ui.helpers.thinletformfields.ThinletFormField;
import net.frontlinesms.plugins.patientview.userlogin.UserSessionManager;
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
	 * to submit about and the chw to submit for
	 */
	private FormSearchArea formSearch;
	
	/**The currently selected entities**/
	private Patient currentPatient;
	private MedicForm currentForm;
	
	/**ArrayList of fields on the forms**/
	@SuppressWarnings("unchecked")
	ArrayList<ThinletFormField> fields;
	
	/**daos**/
	private HibernateMedicFormResponseDao responseDao;
	
	//i18n
	private static final String TITLE ="submitform.title";
	private static final String MUST_FILL_OUT_FORM_MESSAGE ="submitform.messages.must.fill.out";
	private static final String MUST_SELECT_PATIENT_MESSAGE ="submitform.messages.select.patient";
	private static final String BAD_FORMATTING_MESSAGE_PREFIX ="submitform.messages.bad.form.prefix";
	private static final String BAD_FORMATTING_MESSAGE_SUFFIX ="submitform.messages.bad.form.suffix";
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
	
	@SuppressWarnings("unchecked")
	public void updateFormPanel(){
			//clear the panel
		uiController.removeAll(formPanel);
		if(currentForm == null)
			return;

		fields = new ArrayList<ThinletFormField>();
		for(MedicFormField ff: currentForm.getFields()){
			ThinletFormField tff = null;
			String label = ff.getLabel() +":";
			if(ff.getDatatype() == DataType.TRUNCATED_TEXT || ff.getDatatype() == DataType.WRAPPED_TEXT){
				Object field = uiController.createLabel(label);
				uiController.add(formPanel,field);
				uiController.setChoice(field, "halign", "center");
				uiController.setInteger(field, "weightx", 1);
			}else{
				tff = ThinletFormField.getThinletFormFieldForDataType(ff.getDatatype(), uiController, label, null);
				tff.setField(ff);
				fields.add(tff);
				uiController.add(formPanel,tff.getThinletPanel());
				uiController.setWeight(tff.getThinletPanel(), 1,0);
				uiController.setHAlign(tff.getThinletPanel(), "fill");
			}
		}
		autoFillPatient();
	}
	
	private void autoFillPatient(){
		int nonRespondable=0;
		for(int i = 0; i < currentForm.getFields().size(); i++){
			MedicFormField mff = currentForm.getFields().get(i);
			if(!mff.getDatatype().isRespondable()){
				nonRespondable++;
			}
			if(mff.getMapping()== PatientFieldMapping.NAMEFIELD){
				fields.get(i-nonRespondable).setStringResponse(currentPatient.getName());
			}else if(mff.getMapping()== PatientFieldMapping.BIRTHDATEFIELD){
				((DateField) fields.get(i-nonRespondable)).setRawResponse(currentPatient.getBirthdate());
			}else if(mff.getMapping()== PatientFieldMapping.IDFIELD){
				fields.get(i-nonRespondable).setStringResponse(String.valueOf(currentPatient.getPid()));
			}
			if(mff.getMapping() !=null){
				fields.get(i-nonRespondable).setEnabled(false);
			}
		}
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
		//The form response that is going to contain all the field responses
		MedicFormResponse response = new MedicFormResponse(currentForm,UserSessionManager.getUserSessionManager().getCurrentUser(),currentPatient);
		for(ThinletFormField f:fields){
			MedicFormFieldResponse rv = null;
			if(!f.isValid()){
				setWarningLabel(InternationalisationUtils.getI18NString(BAD_FORMATTING_MESSAGE_PREFIX)+" \"" + f.getLabel() +"\" "+InternationalisationUtils.getI18NString(BAD_FORMATTING_MESSAGE_SUFFIX));
				return;
			}else{	
				//create the field response
				rv = new MedicFormFieldResponse(f.getStringResponse(), (MedicFormField) f.getField(),response,currentPatient,UserSessionManager.getUserSessionManager().getCurrentUser());
				//and add it to the form response
				response.addFieldResponse(rv);
			}
		}
		responseDao.saveMedicFormResponse(response);
		setWarningLabel(InternationalisationUtils.getI18NString(SUCCESSFUL_SUBMIT_MESSAGE));
		updateFormPanel();
	}
	
	public void setWarningLabel(String s){
		uiController.setText(warningLabel, s);
	}
	
	public void clearWarningLabel(){
		uiController.setText(warningLabel, "");
	}

	public void selectionChanged(MedicForm selectedObject) {
		currentForm = selectedObject;
		updateFormPanel();
	}

}
