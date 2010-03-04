package net.frontlinesms.plugins.patientview.data.domain.response;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import net.frontlinesms.plugins.patientview.data.domain.framework.MedicForm;
import net.frontlinesms.plugins.patientview.data.domain.people.Person;
import net.frontlinesms.plugins.patientview.history.HistoryManager;

@Entity
@DiscriminatorValue(value="formresponse")
public class MedicFormResponse extends Response{

	@ManyToOne(fetch=FetchType.EAGER,cascade={})
	@JoinColumn(name="form" )
	private MedicForm form;
	
	@OneToMany(fetch=FetchType.LAZY,mappedBy="formResponse",cascade=CascadeType.ALL)
	private List<MedicFormFieldResponse> responses;
	
	public MedicFormResponse(){}
	
	public MedicFormResponse(MedicForm form, List<MedicFormFieldResponse> responses,Person submitter, Person subject) {
		super(submitter,subject);
		this.form = form;
		for(MedicFormFieldResponse mfr : responses){
			mfr.setFormResponse(this);
		}
		this.responses = responses;
		HistoryManager.logFormSubmssion(subject, form);
	}

	public List<MedicFormFieldResponse> getResponses() {
		return responses;
	}

	public void setResponses(List<MedicFormFieldResponse> responses) {
		this.responses = responses;
		for(MedicFormFieldResponse mfr : responses){
			mfr.setFormResponse(this);
		}
	}

	public MedicFormResponse(MedicForm form, Person submitter, Person subject) {
		super(submitter, subject);
		this.form = form;
		responses = new ArrayList<MedicFormFieldResponse>();
		HistoryManager.logFormSubmssion(subject, form);
	}

	public MedicForm getForm(){
		return form;
	}
	
	public String getFormName(){
		return form.getName();
	}
	
	public void setForm(MedicForm form){
		this.form = form;
	}
	
	public void addFieldResponse(MedicFormFieldResponse response){
		responses.add(response);
		response.setFormResponse(this);
	}
	
	public void removeFieldResponse(MedicFormFieldResponse response){
		responses.remove(response);
	}
	
	@Override
	public void setSubject(Person p){
		super.setSubject(p);
		for(MedicFormFieldResponse mfr: responses){
			mfr.setSubject(p);
		}
	}
	
	@Override
	public void setSubmitter(Person p){
		super.setSubmitter(p);
		for(MedicFormFieldResponse mfr: responses){
			mfr.setSubmitter(p);
		}
	}
	
}
