package net.frontlinesms.plugins.patientview.data.domain.framework;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import net.frontlinesms.plugins.patientview.data.domain.response.MedicFormFieldResponse;
import net.frontlinesms.ui.i18n.InternationalisationUtils;


/**
 * MedicFields are fields on medic forms. Like most fields they have a datatype
 * and a label, as well as other functionality for displaying them in the attribute
 * panel, etc...
 * @author Dieterich
 *
 */
@Entity
@DiscriminatorValue(value="formfield")
public class MedicFormField extends Field{
	
	/**
	 * boolean indicating whether or not this field should show up
	 * in the person attributes panel
	 */
	private boolean isAttributePanelField;
	
	/**
	 * The form that contains this field
	 */
	@ManyToOne(fetch=FetchType.EAGER,cascade= {})
	@JoinColumn(name="parentForm")
	private MedicForm parentForm;
	
	@OneToMany(cascade=CascadeType.REMOVE, fetch=FetchType.LAZY, mappedBy="field",targetEntity=MedicFormFieldResponse.class)
	private Set<MedicFormFieldResponse> responses;
	 
	
	/**
	 * The field's position on the form
	 */
	private int position;
	
	/**
	 * The mapping of this field to certain patient fields, to allow
	 * for the matching of forms with their subjects
	 */
	@Enumerated(EnumType.STRING)
	private PatientFieldMapping mapping;
	
	public enum PatientFieldMapping{
		IDFIELD("medic.field.mapping.id","/icons/id_card.png"), 
		NAMEFIELD("medic.field.mapping.name","/icons/user.png"),
		BIRTHDATEFIELD("medic.field.mapping.birthdate","/icons/cake.png");
		
		private String displayName;
		
		private String iconPath;
		
		private PatientFieldMapping(String displayName, String iconPath){
			this.displayName = displayName;
			this.iconPath = (iconPath);
		}
		
		public String toString(){
			return InternationalisationUtils.getI18NString(displayName);
		}

		public String getIconPath() {
			return iconPath;
		}
	}
	
	public MedicFormField(){}
	
	
	public MedicFormField(MedicForm form, DataType datatype, String label, PatientFieldMapping mapping){
		super(label,datatype);
		this.isAttributePanelField = false;
		if(form != null)
			this.parentForm=form;
		if(mapping != null)
			this.mapping = mapping;
	}
	
	public MedicFormField(MedicForm form, DataType datatype, String label){
		super(label,datatype);
		this.isAttributePanelField = false;
		if(form != null)
			this.parentForm=form;
	}
	
	/**
	 * @return the parent form
	 */
	public MedicForm getForm() {
		return parentForm;
	}
	
	/**
	 * @return the name of the parent form
	 */
	public String getParentFormName(){
		return parentForm.getName();
	}
	
	/**
	 * @param form The form that contains this field
	 */
	public void setForm(MedicForm form) {
		this.parentForm = form;
	}
	
	/**
	 * @return This field's position on the form
	 */
	public int getPosition() {
		return position;
	}
	
	/**
	 * @param position This field's position on it's parent form
	 */
	public void setPosition(int position) {
		this.position = position;
	}
	
	/**
	 * @return The mapping from this field to a patient field, if it has one
	 */
	public PatientFieldMapping getMapping(){
		return mapping;
	}
	 
	/**
	 * @param mapping the new mapping
	 */
	public void setMapping(PatientFieldMapping mapping){
		this.mapping = mapping;
	}


	/**
	 * @param isAttributePanelField whether or not this field should be displayed in the detail view
	 */
	public void setIsAttributePanelField(boolean isAttributePanelField) {
		this.isAttributePanelField = isAttributePanelField;
	}


	/**
	 * @return whether or not this field is also in the detail view
	 */
	public boolean isAttributePanelField() {
		return isAttributePanelField;
	}
	
	/**
	 * @return All responses to this field
	 */
	public Set<MedicFormFieldResponse> getResponses(){
		return responses;
	}
	
	public boolean isRespondable(){
		return this.datatype != DataType.WRAPPED_TEXT && this.datatype != DataType.TRUNCATED_TEXT;
	}
	
	
}
