package net.frontlinesms.plugins.patientview.ui.thinletformfields.fieldgroups;

import net.frontlinesms.plugins.patientview.data.domain.framework.DataType;
import net.frontlinesms.plugins.patientview.data.domain.framework.MedicForm;
import net.frontlinesms.plugins.patientview.data.domain.framework.MedicFormField;
import net.frontlinesms.plugins.patientview.data.domain.framework.MedicFormField.PatientFieldMapping;
import net.frontlinesms.plugins.patientview.data.domain.people.Patient;
import net.frontlinesms.plugins.patientview.ui.thinletformfields.DateField;
import net.frontlinesms.plugins.patientview.ui.thinletformfields.FormFieldDelegate;
import net.frontlinesms.ui.UiGeneratorController;

import org.springframework.context.ApplicationContext;

public class FormFieldGroup extends FieldGroup {
	
	private MedicForm form;
	
	public FormFieldGroup(UiGeneratorController ui, ApplicationContext appCon, MedicForm form, FormFieldDelegate delegate) {
		super(ui, appCon, delegate);
		this.form = form;
		initialize();
	}
	
	private void initialize(){
		for(MedicFormField mff: form.getFields()){
			// if its a label field, throw in a label
			if(mff.getDatatype() == DataType.TRUNCATED_TEXT || mff.getDatatype() == DataType.WRAPPED_TEXT){
				Object field = ui.createLabel(mff.getLabel());
				ui.add(super.getMainPanel(),field);
				ui.setChoice(field, "halign", "center");
				ui.setInteger(field, "weightx", 1);
			}else{ //otherwise, put in a normal field
				addField(mff.getLabel(), mff.getDatatype());
			}
		}
	}
	
	public void autoFillWithPatient(Patient p){
		//int containing the number of non-respondable (label) fields we've passed
		int nonRespondable=0;
		for(int i = 0; i < form.getFields().size(); i++){
			MedicFormField mff = form.getFields().get(i);
			if(!mff.getDatatype().isRespondable()){
				nonRespondable++;
				continue;
			}
			//check for the different mapping types
			if(mff.getMapping() == PatientFieldMapping.NAMEFIELD){
				formFields.get(i-nonRespondable).setStringResponse(p.getName());
			}else if(mff.getMapping() == PatientFieldMapping.BIRTHDATEFIELD){
				((DateField) formFields.get(i-nonRespondable)).setRawResponse(p.getBirthdate());
			}else if(mff.getMapping() == PatientFieldMapping.IDFIELD){
				formFields.get(i-nonRespondable).setStringResponse(String.valueOf(p.getPid()));
			}
			//if it was a mapped field, disable it after autofilling it
			if(mff.getMapping() != null){
				formFields.get(i-nonRespondable).setEnabled(false);
			}
		}
	}
	
}
