package net.frontlinesms.plugins.medic.data.domain.framework;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.IndexColumn;

@Entity
@Table(name="medic_forms")
public class MedicForm {

	/** Unique id for this entity.  This is for hibernate usage. */
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(unique=true,nullable=false,updatable=false)
	private long fid;
	
	@IndexColumn(name="form_name_index")
	private String name;
	
	@OneToMany(fetch = FetchType.EAGER,cascade=CascadeType.ALL, mappedBy="form")
	private List<MedicFormField> fields;
	
	public MedicForm(){}
	
	public MedicForm(String name){
		this.name = name;
		fields = new ArrayList<MedicFormField>();
	}
	
	public MedicForm(String name, List<MedicFormField> fields){
		this.name = name;
		setFormFields(fields);
	}
	
	public void setFormFields(List<MedicFormField> fields){
		this.fields = fields;
		int i = 0;
		for(MedicFormField mff: this.fields){
			mff.setForm(this);
			mff.setPosition(i);
			i++;
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getFid() {
		return fid;
	}

	public List<MedicFormField> getFields() {
		return fields;
	}
	
	public void addField(MedicFormField field){
		field.setForm(this);
		field.setPosition(fields.size());
		fields.add(field);
	}
	
	public void removedField(MedicFormField field){
		fields.remove(field);
	}
	
	
}
