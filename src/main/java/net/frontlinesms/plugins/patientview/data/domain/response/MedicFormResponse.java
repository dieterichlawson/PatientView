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

import org.apache.log4j.Logger;
import org.hibernate.annotations.OrderBy;

import net.frontlinesms.FrontlineSMS;
import net.frontlinesms.Utils;
import net.frontlinesms.plugins.forms.data.domain.FormResponse;
import net.frontlinesms.plugins.patientview.data.domain.framework.MedicForm;
import net.frontlinesms.plugins.patientview.data.domain.people.Person;
import net.frontlinesms.ui.i18n.InternationalisationUtils;

@Entity
@DiscriminatorValue(value="form")
public class MedicFormResponse extends Response{

	private static Logger LOG = Utils.getLogger(MedicFormResponse.class);
	
	@ManyToOne(fetch=FetchType.EAGER,cascade={})
	@JoinColumn(name="form" )
	private MedicForm form;
	
	@OneToMany(fetch=FetchType.LAZY,mappedBy="formResponse",cascade=CascadeType.ALL)
	@OrderBy(clause = "position asc")
	private List<MedicFormFieldResponse> responses;
	
	public MedicFormResponse(){}
	
	
	public MedicFormResponse(FormResponse fr, MedicForm mForm, Person submitter, Person subject){
		this.form = mForm;
		this.subject = subject;
		this.submitter = submitter;
		responses = new ArrayList<MedicFormFieldResponse>();
		LOG.info("Beginning conversion from FSMS form response to PV form fesponse");
		LOG.info("Form: " + mForm.getName());
		for(int i = 0; i < mForm.getFields().size(); i++){
			LOG.info("Creating field #"+i+" . Label: "+ mForm.getFields().get(i).getLabel()+ " Value: "+fr.getResults().get(i).toString());
			MedicFormFieldResponse mffr = new MedicFormFieldResponse(fr.getResults().get(i).toString(),mForm.getFields().get(i),subject,submitter);
			mffr.setFormResponse(this);
			mffr.setPosition(responses.size());
			responses.add(mffr);
		}
		LOG.info("Done creating PV form response");
	}
	
	public MedicFormResponse(MedicForm form, List<MedicFormFieldResponse> responses,Person submitter, Person subject) {
		super(submitter,subject);
		this.form = form;
		for(MedicFormFieldResponse mfr : responses){
			mfr.setFormResponse(this);
		}
		this.responses = responses;
		updateFieldPositions();
	}

	public MedicFormResponse(MedicForm form, Person submitter, Person subject) {
		super(submitter, subject);
		this.form = form;
		responses = new ArrayList<MedicFormFieldResponse>();
	}
	
	public List<MedicFormFieldResponse> getResponses() {
		return responses;
	}

	public void setResponses(List<MedicFormFieldResponse> responses) {
		this.responses = responses;
		for(MedicFormFieldResponse mfr : responses){
			mfr.setFormResponse(this);
		}
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
		response.setPosition(responses.size());
	}
	
	public void removeFieldResponse(MedicFormFieldResponse response){
		responses.remove(response);
		updateFieldPositions();
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
	
	public boolean isMapped(){
		return subject !=null;
	}
	
	public String isMappedString(){
		return InternationalisationUtils.getI18NString(isMapped()?"datatype.true":"datatype.false");
	}
	
	private void updateFieldPositions(){
		for(int i = 0; i < responses.size();i++){
			responses.get(i).setPosition(i);
		}
	}
	
}
