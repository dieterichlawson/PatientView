package net.frontlinesms.plugins.patientview.ui.thinletformfields.fieldgroups;

import java.util.ArrayList;
import java.util.List;

import net.frontlinesms.plugins.patientview.data.domain.framework.DataType;
import net.frontlinesms.plugins.patientview.ui.thinletformfields.FormFieldDelegate;
import net.frontlinesms.plugins.patientview.ui.thinletformfields.ThinletFormField;
import net.frontlinesms.ui.UiGeneratorController;

import org.hibernate.classic.ValidationFailure;
import org.springframework.context.ApplicationContext;

public class FieldGroup implements FormFieldDelegate {
	
	protected UiGeneratorController ui;
	protected ApplicationContext appCon;
	protected Object mainPanel;
	protected List<ThinletFormField<?>> formFields;
	protected FormFieldDelegate delegate;
	
	public FieldGroup(UiGeneratorController ui, ApplicationContext appCon, FormFieldDelegate delegate){
		this.ui = ui;
		this.appCon = appCon;
		mainPanel = ui.createPanel("fieldGroupPanel");
		ui.setColumns(mainPanel, 1);
		ui.setWeight(mainPanel, 1, 1);
		ui.setGap(mainPanel, 5);
		this.formFields = new ArrayList<ThinletFormField<?>>();
	}
	
	public Object getMainPanel(){
		return mainPanel;
	}
	
	public void addField(String label, DataType datatype){
		addField(ThinletFormField.getThinletFormFieldForDataType(datatype, ui, appCon, label, this));
		
	}
	
	public void addField(ThinletFormField<?> field){
		getFormFields().add(field);
		field.setDelegate(this);
		ui.add(mainPanel,field.getThinletPanel());
	}
	
	public boolean validate(boolean alert){
		for(ThinletFormField<?> field: getFormFields()){
			try{
				field.validate();
			}catch(ValidationFailure vf){
				if(alert) ui.alert(vf.getMessage());
				return false;
			}
		}
		return true;
	}
	
	public void removeAll(){
		ui.removeAll(mainPanel);
		this.formFields =new ArrayList<ThinletFormField<?>>();
	}

	/**
	 * The pass-through method for form field delegation
	 * 
	 * @see net.frontlinesms.plugins.patientview.ui.thinletformfields.FormFieldDelegate#formFieldChanged(net.frontlinesms.plugins.patientview.ui.thinletformfields.ThinletFormField, java.lang.String)
	 */
	public void formFieldChanged(ThinletFormField changedField, String newValue) {
		if(delegate != null)
			delegate.formFieldChanged(changedField, newValue);
	}
	
	public List<ThinletFormField<?>> getFormFields() {
		return formFields;
	}
	
}
