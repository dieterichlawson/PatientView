package net.frontlinesms.plugins.patientview.data.domain.framework;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import net.frontlinesms.plugins.forms.data.domain.Form;
import net.frontlinesms.plugins.forms.data.domain.FormField;

import org.hibernate.annotations.IndexColumn;
import org.hibernate.annotations.OrderBy;

@Entity
@Table(name = "medic_forms")
public class MedicForm {

	/** Unique id for this entity. This is for hibernate usage. */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(unique = true, nullable = false, updatable = false)
	private long fid;

	@IndexColumn(name = "form_name_index")
	private String name;

	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "parentForm")
	@OrderBy(clause = "position asc")
	private List<MedicFormField> fields;

	@OneToOne(fetch = FetchType.LAZY, cascade = {})
	@JoinColumn(name = "vanilla_form_id", nullable = true)
	private Form vanillaForm;

	public MedicForm() {
	}

	public MedicForm(String name) {
		this.name = name;
		fields = new ArrayList<MedicFormField>();
	}

	/**
	 * Gets the vanilla frontline form that this medic form is linked to
	 * 
	 * @return the form
	 */
	public Form getForm() {
		return vanillaForm;
	}

	/**
	 * Sets the vanilla frontline form that this medic form is linked to
	 * 
	 * @param form
	 */
	public void setForm(Form form) {
		this.vanillaForm = form;
	}

	public MedicForm(String name, List<MedicFormField> fields) {
		this.name = name;
		setFormFields(fields);
	}

	public void setFormFields(List<MedicFormField> fields) {
		this.fields = fields;
		updateFieldPositions();
	}

	/**
	 * creates a MedicForm from a vanilla frontline form
	 * 
	 * @param f
	 */
	public MedicForm(Form f) {
		this.vanillaForm = f;
		this.name = f.getName();
		fields = new ArrayList<MedicFormField>();
		for (FormField field : f.getFields()) {
			MedicFormField mff = new MedicFormField(this, DataType.getDataTypeForString(field.getType().name()), field.getLabel());
			fields.add(mff);
		}
		updateFieldPositions();
	}

	/**
	 * @return the name of the form
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name of the form
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the ID of the form
	 */
	public long getFid() {
		return fid;
	}

	/**
	 * @return a list of the fields on the form
	 */
	public List<MedicFormField> getFields() {
		return fields;
	}

	/**
	 * Adds a field to the form at the end
	 * 
	 * @param field
	 */
	public void addField(MedicFormField field) {
		field.setForm(this);
		fields.add(field);
		field.setPosition(fields.size());
	}

	/**
	 * removes a field from the form
	 * 
	 * @param field
	 */
	public void removedField(MedicFormField field) {
		fields.remove(field);
		updateFieldPositions();
	}
	
	private void updateFieldPositions(){
		for(int i = 0; i < fields.size();i++){
			fields.get(i).setPosition(i);
		}
	}

}
