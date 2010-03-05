package net.frontlinesms.plugins.patientview.ui.dialogs;

import java.awt.event.WindowEvent;
import java.text.DateFormat;
import java.util.ArrayList;

import net.frontlinesms.plugins.patientview.data.domain.framework.DataType;
import net.frontlinesms.plugins.patientview.data.domain.framework.MedicForm;
import net.frontlinesms.plugins.patientview.data.domain.framework.MedicFormField;
import net.frontlinesms.plugins.patientview.data.domain.framework.MedicFormField.PatientFieldMapping;
import net.frontlinesms.plugins.patientview.data.domain.people.Patient;
import net.frontlinesms.plugins.patientview.data.domain.response.MedicFormFieldResponse;
import net.frontlinesms.plugins.patientview.data.domain.response.MedicFormResponse;
import net.frontlinesms.plugins.patientview.data.repository.hibernate.HibernateMedicFormResponseDao;
import net.frontlinesms.plugins.patientview.ui.dialogs.searchareas.FormSearchArea;
import net.frontlinesms.plugins.patientview.ui.dialogs.searchareas.PatientSearchArea;
import net.frontlinesms.plugins.patientview.ui.helpers.thinletformfields.CheckBox;
import net.frontlinesms.plugins.patientview.ui.helpers.thinletformfields.DateField;
import net.frontlinesms.plugins.patientview.ui.helpers.thinletformfields.NumericTextField;
import net.frontlinesms.plugins.patientview.ui.helpers.thinletformfields.TextArea;
import net.frontlinesms.plugins.patientview.ui.helpers.thinletformfields.TextBox;
import net.frontlinesms.plugins.patientview.ui.helpers.thinletformfields.ThinletFormField;
import net.frontlinesms.plugins.patientview.ui.helpers.thinletformfields.TimeField;
import net.frontlinesms.plugins.patientview.ui.helpers.thinletformfields.personalformfields.PasswordTextField;
import net.frontlinesms.plugins.patientview.ui.helpers.thinletformfields.personalformfields.PhoneNumberField;
import net.frontlinesms.plugins.patientview.userlogin.UserSessionManager;
import net.frontlinesms.ui.ExtendedThinlet;
import net.frontlinesms.ui.ThinletUiEventHandler;
import net.frontlinesms.ui.UiGeneratorController;
import net.frontlinesms.ui.i18n.InternationalisationUtils;

import org.springframework.context.ApplicationContext;

import thinlet.FrameLauncher;
import thinlet.Thinlet;

public class SubmitFormDialog implements ThinletUiEventHandler{
	
	private static final String UI__FILE_SUBMIT_FORM_DIALOG = "/ui/plugins/patientview/submit_form_dialog.xml";
	
	/**UI controller**/
	private ExtendedThinlet  thinlet;
	
	/**Thinlet Components**/
	private Object mainPanel;
	private Object formPanel;
	private Object searchPanel;
	private Object warningLabel;
	/**
	 * the search areas where the user selects the form to submit, the patient
	 * to submit about and the chw to submit for
	 */
	private FormSearchArea formSearch;
	private PatientSearchArea patientSearch;
	
	/**The currently selected entities**/
	private Patient currentPatient;
	private MedicForm currentForm;
	
	/**ArrayList of fields on the forms**/
	@SuppressWarnings("unchecked")
	ArrayList<ThinletFormField> fields;
	
	/**daos**/
	private HibernateMedicFormResponseDao responseDao;
	
	private ApplicationContext appContext;
	
	//i18n
	private static final String TITLE ="submitform.title";
	private static final String MUST_FILL_OUT_FORM_MESSAGE ="submitform.messages.must.fill.out";
	private static final String MUST_SELECT_PATIENT_MESSAGE ="submitform.messages.select.patient";
	private static final String BAD_FORMATTING_MESSAGE_PREFIX ="submitform.messages.bad.form.prefix";
	private static final String BAD_FORMATTING_MESSAGE_SUFFIX ="submitform.messages.bad.form.suffix";
	private static final String SUCCESSFUL_SUBMIT_MESSAGE ="submitform.messages.successful.submit";
	
	@SuppressWarnings("serial")
	public SubmitFormDialog(UiGeneratorController uiController, ApplicationContext appContext, MedicForm form, Patient patient){
		//load the main panel from the file
		mainPanel = uiController.loadComponentFromFile(UI__FILE_SUBMIT_FORM_DIALOG, this);
		//create the new thinlet instance, and add the main panel
		thinlet = new ExtendedThinlet();
		thinlet.add(mainPanel);
		//initialize the two panels
		formPanel = thinlet.find("formPanel");
		searchPanel = thinlet.find("searchPanel");
		warningLabel = thinlet.find("warningLabel");
		//if this dialog was passed entities, set them
		currentForm = form;
		currentPatient = patient;
		//create the search areas
		formSearch = new FormSearchArea(form,thinlet,this,appContext);
		patientSearch = new PatientSearchArea(patient,thinlet,this,appContext);
		//initialize the daos
		responseDao = (HibernateMedicFormResponseDao) appContext.getBean("MedicFormResponseDao");
		//add the search areas, with separators inbetween
		thinlet.add(searchPanel,formSearch.getThinletPanel());
		Object separator = Thinlet.create("separator");
		thinlet.setInteger(separator, "weightx", 1);
		Object separator1 = Thinlet.create("separator");
		thinlet.setInteger(separator1, "weightx", 1);
		thinlet.add(searchPanel,formSearch.getThinletPanel());
		thinlet.add(searchPanel,separator);
		thinlet.add(searchPanel, separator1);
		thinlet.add(searchPanel,patientSearch.getThinletPanel());
		//initialize the form panel
		updateFormPanel();
		//display the dialog
		@SuppressWarnings("unused")
		FrameLauncher f = new FrameLauncher(InternationalisationUtils.getI18NString(TITLE),thinlet,800,600,null)
		{ public void windowClosing(WindowEvent e){  dispose(); }};
	}
	
	@SuppressWarnings("unchecked")
	public void updateFormPanel(){
			//clear the panel
		thinlet.removeAll(formPanel);
		if(currentForm == null)
			return;

		fields = new ArrayList<ThinletFormField>();
		for(MedicFormField ff: currentForm.getFields()){
			ThinletFormField tff = null;
			String label = ff.getLabel() +":";
			if(ff.getDatatype() == DataType.CHECK_BOX){ 
				tff = new CheckBox(thinlet,label);
			}else if(ff.getDatatype() == DataType.DATE_FIELD){
				tff = new DateField(thinlet,label);
			}else if(ff.getDatatype() == DataType.NUMERIC_TEXT_FIELD){
				tff = new NumericTextField(thinlet,label);
			}else if(ff.getDatatype() == DataType.PASSWORD_FIELD){
				tff = new PasswordTextField(thinlet,label);
			}else if(ff.getDatatype() == DataType.PHONE_NUMBER_FIELD){
				tff = new PhoneNumberField(thinlet,label);
			}else if(ff.getDatatype() == DataType.TIME_FIELD){
				tff = new TimeField(thinlet,label);
			}else if(ff.getDatatype() == DataType.TEXT_AREA){
				tff = new TextArea(thinlet,label);
			}else if(ff.getDatatype() == DataType.TEXT_FIELD){
				tff = new TextBox(thinlet,label);
			}else if(ff.getDatatype() == DataType.TRUNCATED_TEXT ||
					ff.getDatatype() == DataType.WRAPPED_TEXT){
				Object field = thinlet.createLabel(label);
				thinlet.add(formPanel,field);
				thinlet.setChoice(field, "halign", "center");
				thinlet.setInteger(field, "weightx", 1);
			}

			if(tff != null){
				tff.setField(ff);
				fields.add(tff);
				thinlet.add(formPanel,tff.getThinletPanel());
				thinlet.setInteger(tff.getThinletPanel(), "weightx", 1);
				thinlet.setChoice(tff.getThinletPanel(), "halign", "fill");
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
				fields.get(i-nonRespondable).setResponse(currentPatient.getName());
			}else if(mff.getMapping()== PatientFieldMapping.BIRTHDATEFIELD){
				((DateField) fields.get(i-nonRespondable)).setRawResponse(currentPatient.getBirthdate());
			}else if(mff.getMapping()== PatientFieldMapping.IDFIELD){
				fields.get(i-nonRespondable).setResponse(String.valueOf(currentPatient.getPid()));
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
				rv = new MedicFormFieldResponse(f.getResponse(), (MedicFormField) f.getField(),response,currentPatient,UserSessionManager.getUserSessionManager().getCurrentUser());
				//and add it to the form response
				response.addFieldResponse(rv);
			}
		}
		responseDao.saveMedicFormResponse(response);
		setWarningLabel(InternationalisationUtils.getI18NString(SUCCESSFUL_SUBMIT_MESSAGE));
		updateFormPanel();
	}
	
	@SuppressWarnings("unused")
	private void setAllEditable(Object container, boolean editable){
		try{
		thinlet.setEditable(container,editable);
		}catch(Throwable t){}
		for(Object b : thinlet.getItems(container)){
			thinlet.setEnabled(b, editable);
			try{
				setAllEditable(b, editable);
			}catch(Throwable t){}
		}
	}
	
	public void setForm(MedicForm f){
		currentForm = f;
		updateFormPanel();
	}
	
	public void setPatient(Patient p){
		currentPatient = p;
		autoFillPatient();
	}
	
	public void setWarningLabel(String s){
		thinlet.setText(warningLabel, s);
	}
	
	public void clearWarningLabel(){
		thinlet.setText(warningLabel, "");
	}
}
