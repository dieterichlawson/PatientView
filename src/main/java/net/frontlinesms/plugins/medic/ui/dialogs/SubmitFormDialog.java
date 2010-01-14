package net.frontlinesms.plugins.medic.ui.dialogs;

import java.awt.event.WindowEvent;
import java.text.DateFormat;
import java.util.ArrayList;

import net.frontlinesms.plugins.medic.data.domain.framework.DataType;
import net.frontlinesms.plugins.medic.data.domain.framework.MedicForm;
import net.frontlinesms.plugins.medic.data.domain.framework.MedicFormField;
import net.frontlinesms.plugins.medic.data.domain.people.Patient;
import net.frontlinesms.plugins.medic.data.domain.response.MedicFieldResponse;
import net.frontlinesms.plugins.medic.data.domain.response.MedicFormResponse;
import net.frontlinesms.plugins.medic.data.repository.hibernate.HibernateMedicFormResponseDao;
import net.frontlinesms.plugins.medic.ui.dialogs.searchareas.FormSearchArea;
import net.frontlinesms.plugins.medic.ui.dialogs.searchareas.PatientSearchArea;
import net.frontlinesms.plugins.medic.ui.helpers.thinletformfields.CheckBox;
import net.frontlinesms.plugins.medic.ui.helpers.thinletformfields.DateField;
import net.frontlinesms.plugins.medic.ui.helpers.thinletformfields.NumericTextField;
import net.frontlinesms.plugins.medic.ui.helpers.thinletformfields.PasswordTextField;
import net.frontlinesms.plugins.medic.ui.helpers.thinletformfields.PhoneNumberField;
import net.frontlinesms.plugins.medic.ui.helpers.thinletformfields.TextArea;
import net.frontlinesms.plugins.medic.ui.helpers.thinletformfields.TextBox;
import net.frontlinesms.plugins.medic.ui.helpers.thinletformfields.ThinletFormField;
import net.frontlinesms.plugins.medic.ui.helpers.thinletformfields.TimeField;
import net.frontlinesms.plugins.medic.userlogin.UserSessionManager;
import net.frontlinesms.ui.ExtendedThinlet;
import net.frontlinesms.ui.ThinletUiEventHandler;
import net.frontlinesms.ui.UiGeneratorController;

import org.springframework.context.ApplicationContext;

import thinlet.FrameLauncher;

public class SubmitFormDialog implements ThinletUiEventHandler{
	
	private static final String UI__FILE_SUBMIT_FORM_DIALOG = "/ui/plugins/medic/submit_form_dialog.xml";
	
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
	ArrayList<ThinletFormField> fields;
	
	/**daos**/
	private HibernateMedicFormResponseDao responseDao;
	
	private ApplicationContext appContext;
	
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
		Object separator = thinlet.create("separator");
		thinlet.setInteger(separator, "weightx", 1);
		Object separator1 = thinlet.create("separator");
		thinlet.setInteger(separator1, "weightx", 1);
		thinlet.add(searchPanel,formSearch.getThinletPanel());
		thinlet.add(searchPanel,separator);
		thinlet.add(searchPanel, separator1);
		thinlet.add(searchPanel,patientSearch.getThinletPanel());
		//initialize the form panel
		updateFormPanel();
		//display the dialog
		FrameLauncher f = new FrameLauncher("Edit the Detail View",thinlet,800,600,null)
		{ public void windowClosing(WindowEvent e){  dispose(); }};
	}
	
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
			}else if(ff.getDatatype() == DataType.NUMBER){
				tff = new NumericTextField(thinlet,label);
			}else if(ff.getDatatype() == DataType.PASSWORD_FIELD){
				tff = new PasswordTextField(thinlet,label);
			}else if(ff.getDatatype() == DataType.PHONE_NUMBER_FIELD){
				tff = new PhoneNumberField(thinlet,label);
			}else if(ff.getDatatype() == DataType.TIME_FIELD){
				tff = new TimeField(thinlet,label);
			}else if(ff.getDatatype() == DataType.TEXT_AREA){
				tff = new TextArea(thinlet,label);
			}else if(ff.getDatatype() == DataType.TEXT){
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
		if(currentPatient!= null && currentForm !=null){
			fields.get(0).setResponse(currentPatient.getName());
			DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT);
			fields.get(1).setResponse(df.format(currentPatient.getBirthdate()));	
		}
	}
	
	public void submit(){
		if(currentForm == null){
			setWarningLabel("You must select a Form and fill it out before submitting");
			return;
		}
		if(currentPatient == null){
			setWarningLabel("You must select a Patient that the form is about before submitting");
			return;
		}
		//The form response that is going to contain all the field responses
		MedicFormResponse response = new MedicFormResponse(currentForm,UserSessionManager.getUserSessionManager().getCurrentUser(),currentPatient);
		for(ThinletFormField f:fields){
			MedicFieldResponse rv = null;
			if(!f.isValid()){
				setWarningLabel("The field labelled \"" + f.getLabel() +"\" is incorrectly formatted");
				return;
			}else{	
				//create the field response
				rv = new MedicFieldResponse(f.getResponse(), f.getField(),response,currentPatient,UserSessionManager.getUserSessionManager().getCurrentUser());
				//and add it to the form response
				response.addFieldResponse(rv);
			}
		}
		responseDao.saveMedicFormResponse(response);
		setWarningLabel("Form Submitted!");
		updateFormPanel();
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
