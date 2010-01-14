package net.frontlinesms.plugins.medic.data.domain.response;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import net.frontlinesms.plugins.medic.data.domain.framework.MedicField;
import net.frontlinesms.plugins.medic.data.domain.people.Person;

@Entity
@DiscriminatorValue("fieldresponse")
public class MedicFieldResponse extends Response {
	
	@OneToOne(targetEntity=MedicField.class,cascade ={})
	@JoinColumn(name="field")
	private MedicField field;

	@ManyToOne(fetch = FetchType.EAGER, optional = true, cascade = {})
	@JoinColumn(name="form_response", nullable=true)
	private MedicFormResponse formResponse;

	private String value;

	public MedicFieldResponse(){}
	
	public MedicFieldResponse(String value, MedicField field, Person subject, Person submitter) {
		super(submitter, subject);
		this.value = value;
		this.field = field;
	}

	public MedicFieldResponse(String value, MedicField field, MedicFormResponse formResponse,Person subject, Person submitter) {
		super(submitter, subject);
		this.value = value;
		this.field = field;
		this.formResponse = formResponse;
	}

	public MedicField getField() {
		return field;
	}

	public MedicFormResponse getFormResponse() {
		return formResponse;
	}

	public String getValue() {
		return value;
	}

	public void setField(MedicField field) {
		this.field = field;
	}

	public void setFormResponse(MedicFormResponse formResponse) {
		this.formResponse = formResponse;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
