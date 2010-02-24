package net.frontlinesms.plugins.patientview.data.domain.response;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import net.frontlinesms.plugins.forms.data.domain.FormResponse;
import net.frontlinesms.plugins.forms.data.domain.ResponseValue;
import net.frontlinesms.plugins.patientview.data.domain.framework.MedicForm;
import net.frontlinesms.plugins.patientview.data.domain.people.Person;
import net.frontlinesms.plugins.patientview.history.HistoryManager;

@Entity
@DiscriminatorValue("formresponse")
public class MedicFormResponse extends Response{

	@OneToOne(fetch=FetchType.EAGER,cascade={})
	@JoinColumn(name="form" )
	private MedicForm form;
	
	@OneToMany(fetch=FetchType.LAZY,mappedBy="formResponse",cascade=CascadeType.ALL)
	private List<MedicFieldResponse> responses;
	
	public MedicFormResponse(){}
	
	public MedicFormResponse(MedicForm form, List<MedicFieldResponse> responses,Person submitter, Person subject) {
		super(submitter,subject);
		this.form = form;
		for(MedicFieldResponse mfr : responses){
			mfr.setFormResponse(this);
		}
		this.responses = responses;
		HistoryManager.logFormSubmssion(subject, form);
	}

	public List<MedicFieldResponse> getResponses() {
		return responses;
	}

	public void setResponses(List<MedicFieldResponse> responses) {
		this.responses = responses;
		for(MedicFieldResponse mfr : responses){
			mfr.setFormResponse(this);
		}
	}

	public MedicFormResponse(MedicForm form, Person submitter, Person subject) {
		super(submitter, subject);
		this.form = form;
		responses = new ArrayList<MedicFieldResponse>();
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
	
	public void addFieldResponse(MedicFieldResponse response){
		responses.add(response);
		response.setFormResponse(this);
	}
	
	public void removeFieldResponse(MedicFieldResponse response){
		responses.remove(response);
	}
	
	@Override
	public void setSubject(Person p){
		super.setSubject(p);
		for(MedicFieldResponse mfr: responses){
			mfr.setSubject(p);
		}
	}
	
	@Override
	public void setSubmitter(Person p){
		super.setSubmitter(p);
		for(MedicFieldResponse mfr: responses){
			mfr.setSubmitter(p);
		}
	}
	
}
