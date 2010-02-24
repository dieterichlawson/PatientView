package net.frontlinesms.plugins.patientview.data.domain.framework;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;


@Entity
@DiscriminatorValue("formfield")
public class MedicFormField extends MedicField{

	@ManyToOne(fetch=FetchType.EAGER,cascade= {})
	@JoinColumn(name="parentForm")
	private MedicForm parentForm;
	
	private int position;
	
	@Enumerated(EnumType.STRING)
	private PatientAttributeMapping mapping;
	
	public enum PatientAttributeMapping{IDFIELD(), NAMEFIELD(),BIRTHDATEFIELD()}
	
	public MedicFormField(){}
	
	
	public MedicFormField(MedicForm form, DataType datatype, String label, PatientAttributeMapping mapping){
		super(label,datatype);
		if(form != null)
			this.parentForm=form;
		if(mapping != null)
			this.mapping = mapping;
	}
	
	public MedicFormField(MedicForm form, DataType datatype, String label){
		super(label,datatype);
		if(form != null)
			this.parentForm=form;
	}
	
	public MedicForm getForm() {
		return parentForm;
	}
	
	public String getParentFormName(){
		return parentForm.getName();
	}
	
	public void setForm(MedicForm form) {
		this.parentForm = form;
	}
	
	public int getPosition() {
		return position;
	}
	
	public void setPosition(int position) {
		this.position = position;
	}
	
	public PatientAttributeMapping getMapping(){
		return mapping;
	}
	public void setMapping(PatientAttributeMapping mapping){
		this.mapping = mapping;
	}
	
	
}
