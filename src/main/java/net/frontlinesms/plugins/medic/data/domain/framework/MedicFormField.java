package net.frontlinesms.plugins.medic.data.domain.framework;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;


@Entity
@DiscriminatorValue("formfield")
public class MedicFormField extends MedicField{

	@ManyToOne(fetch=FetchType.EAGER,cascade= {})
	@JoinColumn(name="form")
	private MedicForm form;
	
	private int position;
	
	public MedicFormField(){}
	
	
	public MedicFormField(MedicForm form, DataType datatype, String label){
		super(label,datatype);
		if(form != null)
			this.form=form;
	}
	
	public MedicForm getForm() {
		return form;
	}
	
	public String getParentFormName(){
		return form.getName();
	}
	
	public void setForm(MedicForm form) {
		this.form = form;
	}
	
	public int getPosition() {
		return position;
	}
	
	public void setPosition(int position) {
		this.position = position;
	}
	
	
	
}
