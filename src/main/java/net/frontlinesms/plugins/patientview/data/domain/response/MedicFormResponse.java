package net.frontlinesms.plugins.patientview.data.domain.response;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import net.frontlinesms.plugins.forms.data.domain.FormResponse;
import net.frontlinesms.plugins.forms.data.domain.ResponseValue;
import net.frontlinesms.plugins.patientview.data.domain.framework.MedicForm;
import net.frontlinesms.plugins.patientview.data.domain.framework.MedicFormField;
import net.frontlinesms.plugins.patientview.data.domain.people.Person;
import net.frontlinesms.ui.i18n.InternationalisationUtils;

import org.hibernate.annotations.OrderBy;

@Entity
@DiscriminatorValue(value="form")
public class MedicFormResponse extends Response{
	
	@ManyToOne(fetch=FetchType.EAGER,cascade={})
	@JoinColumn(name="form" )
	private MedicForm form;
	
	@OneToMany(fetch=FetchType.LAZY,mappedBy="formResponse",cascade=CascadeType.ALL)
	@OrderBy(clause = "responsePosition asc")
	private List<MedicFormFieldResponse> responses;
	
	public MedicFormResponse(){}
	
	public MedicFormResponse(FormResponse fr, MedicForm mForm, Person submitter, Person subject){
		this.form = mForm;
		this.subject = subject;
		this.submitter = submitter;
		this.dateSubmitted = new Date().getTime();
		responses = new ArrayList<MedicFormFieldResponse>();
		Iterator<ResponseValue> responseValues = fr.getResults().iterator();
		for(MedicFormField mff: mForm.getFields()){
			if(mff.isRespondable()){
				String response = responseValues.next().toString();
				MedicFormFieldResponse mffr = new MedicFormFieldResponse(response, mff, subject, submitter);
				mffr.setFormResponse(this);
				mffr.setResponsePosition(responses.size());
				responses.add(mffr);
			}
		}
	}
	
	public MedicFormResponse(MedicForm form, List<MedicFormFieldResponse> responses,Person submitter, Person subject) {
		super(submitter,subject);
		this.form = form;
		this.responses = responses;
		setFieldSubjects();
		updateFieldPositions();
	}

	public MedicFormResponse(MedicForm form, Person submitter, Person subject) {
		super(submitter, subject);
		this.form = form;
		this.dateSubmitted = new Date().getTime();
		responses = new ArrayList<MedicFormFieldResponse>();
	}
	
	public List<MedicFormFieldResponse> getResponses() {
		return responses;
	}

	public void setResponses(List<MedicFormFieldResponse> responses) {
		this.responses = responses;
		setFieldSubjects();
		updateFieldPositions();
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
		response.setResponsePosition(responses.size()-1);
	}
	
	public void removeFieldResponse(MedicFormFieldResponse response){
		responses.remove(response);
		updateFieldPositions();
	}
	
	public boolean isMapped(){
		return subject !=null;
	}
	
	public String isMappedString(){
		return InternationalisationUtils.getI18NString(isMapped()?"datatype.true":"datatype.false");
	}
	
	private void updateFieldPositions(){
		for(int i = 0; i < responses.size();i++){
			responses.get(i).setResponsePosition(i);
		}
	}
	
	private void setFieldSubjects(){
		for(MedicFormFieldResponse mfr : responses){
			mfr.setFormResponse(this);
		}
	}
}
