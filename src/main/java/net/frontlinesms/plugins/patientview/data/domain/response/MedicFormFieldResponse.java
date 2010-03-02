package net.frontlinesms.plugins.patientview.data.domain.response;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import net.frontlinesms.plugins.patientview.data.domain.framework.MedicFormField;
import net.frontlinesms.plugins.patientview.data.domain.people.Person;

@Entity
@DiscriminatorValue("fieldresponse")
public class MedicFormFieldResponse extends Response {
	
	@ManyToOne(targetEntity=MedicFormField.class,cascade ={},fetch=FetchType.EAGER)
	@JoinColumn(name="field")
	private MedicFormField field;

	@ManyToOne(fetch = FetchType.EAGER, optional = true, cascade = {})
	@JoinColumn(name="form_response", nullable=true)
	private MedicFormResponse formResponse;

	private String value;

	public MedicFormFieldResponse(){}
	
	public MedicFormFieldResponse(String value, MedicFormField field, Person subject, Person submitter) {
		super(submitter, subject);
		this.value = value;
		this.field = field;
	}

	public MedicFormFieldResponse(String value, MedicFormField field, MedicFormResponse formResponse,Person subject, Person submitter) {
		super(submitter, subject);
		this.value = value;
		this.field = field;
		this.formResponse = formResponse;
	}

	public MedicFormField getField() {
		return field;
	}
	
	public String getFieldLabel(){
		return field.getLabel();
	}

	public MedicFormResponse getFormResponse() {
		return formResponse;
	}

	public String getValue() {
		return value;
	}

	public void setField(MedicFormField field) {
		this.field = field;
	}

	public void setFormResponse(MedicFormResponse formResponse) {
		this.formResponse = formResponse;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
