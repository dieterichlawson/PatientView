package net.frontlinesms.plugins.patientview.search.simplesearch;

import java.util.ArrayList;

import net.frontlinesms.plugins.patientview.data.domain.framework.MedicForm;
import net.frontlinesms.plugins.patientview.data.domain.framework.MedicFormField;
import net.frontlinesms.plugins.patientview.data.domain.people.CommunityHealthWorker;
import net.frontlinesms.plugins.patientview.data.domain.people.Patient;
import net.frontlinesms.plugins.patientview.data.domain.response.MedicFormFieldResponse;
import net.frontlinesms.plugins.patientview.data.domain.response.MedicFormResponse;
import net.frontlinesms.plugins.patientview.data.domain.response.MedicMessageResponse;
import net.frontlinesms.plugins.patientview.data.domain.response.PersonAttributeResponse;
import net.frontlinesms.plugins.patientview.search.FieldDescriptor;
import net.frontlinesms.ui.i18n.InternationalisationUtils;

/**
 * This class sets out all the input possibilities and database information needed for the Simple search
 * Each entry in this enum is an Entity that can be searchedfor in the system.
 * Each FieldDescriptor supplied with that entry is a field that can be filtered upon
 * @author Dieterich
 *
 */
public enum SimpleSearchEntity {

	PATIENT("medic.common.patients",  //Display name
			"pat",   //entity table alias
			"Patient", //database name
			 Patient.class,   //entity class
			 new FieldDescriptor[] {new FieldDescriptor(null,"simplesearch.fields.name","name",SimpleSearchDataType.STRING),
									new FieldDescriptor(null,"simplesearch.fields.chw.name","chw.name",SimpleSearchDataType.STRING),
									new FieldDescriptor(null,"simplesearch.fields.birthdate","birthdate",SimpleSearchDataType.DATE),
									new FieldDescriptor(null,"simplesearch.fields.id","pid",SimpleSearchDataType.NUMBER)}),
	
	CHW("medic.common.chws",
		"chw",
		"CommunityHealthWorker",
		 CommunityHealthWorker.class,
		 new FieldDescriptor[] {new FieldDescriptor(null,"simplesearch.fields.name","name",SimpleSearchDataType.STRING),
								new FieldDescriptor(null,"simplesearch.fields.phone.number","contactInfo.phoneNumber",SimpleSearchDataType.STRING),
								new FieldDescriptor(null,"simplesearch.fields.birthdate","birthdate",SimpleSearchDataType.DATE),
		 						new FieldDescriptor(null,"simplesearch.fields.id","pid",SimpleSearchDataType.NUMBER)}),
	MESSAGE("medic.common.text.messages",
			"sms",
			"MedicMessageResponse",
			 MedicMessageResponse.class,
			 new FieldDescriptor[] {new FieldDescriptor(null,"simplesearch.fields.submitter.name","submitter.name",SimpleSearchDataType.STRING),
									new FieldDescriptor(null,"simplesearch.fields.content","messageContent",SimpleSearchDataType.STRING),
									new FieldDescriptor(null,"simplesearch.fields.date.submitted","dateSubmitted",SimpleSearchDataType.DATE)}),
	FORM("medic.common.forms",
		 "form",
		 "MedicForm",
		 MedicForm.class,
		 new FieldDescriptor[] {new FieldDescriptor(null,"simplesearch.fields.name","name",SimpleSearchDataType.STRING)}),
	     
	FIELD("medic.common.fields",
		  "field",
		  "MedicFormField",
		   MedicFormField.class,
		   new FieldDescriptor[] {new FieldDescriptor(null,"simplesearch.fields.field.label","label",SimpleSearchDataType.STRING),
		   						  new FieldDescriptor(null,"simplesearch.fields.fields.form.name","parentForm.name",SimpleSearchDataType.STRING)}),
		   
	FORM_RESPONSE("medic.common.form.responses",
				   "formr",
				   "MedicFormResponse",
				   MedicFormResponse.class,
				   new FieldDescriptor[] {new FieldDescriptor(null,"simplesearch.fields.form.name","form.name",SimpleSearchDataType.STRING),
										  new FieldDescriptor(null,"simplesearch.fields.submitter.name","submitter.name",SimpleSearchDataType.STRING),
										  new FieldDescriptor(null,"simplesearch.fields.subject.name","subject.name",SimpleSearchDataType.STRING),
										  new FieldDescriptor(null,"simplesearch.fields.date.submitted","dateSubmitted",SimpleSearchDataType.DATE)}),
					   
    FIELD_RESPONSE("medic.common.field.responses",
				   "fieldr",
				   "MedicFormFieldResponse",
				   MedicFormFieldResponse.class,
				   new FieldDescriptor[] {new FieldDescriptor(null,"simplesearch.fields.field.label","field.label",SimpleSearchDataType.STRING),
    			   new FieldDescriptor(null,"simplesearch.fields.submitter.name","submitter.name",SimpleSearchDataType.STRING),
    			   new FieldDescriptor(null,"simplesearch.fields.subject.name","subject.name",SimpleSearchDataType.STRING),
    			   new FieldDescriptor(null,"simplesearch.fields.date.submitted","dateSubmitted",SimpleSearchDataType.DATE),
				   new FieldDescriptor(null,"simplesearch.fields.response","value",SimpleSearchDataType.STRING)}),
				   
	ATTRIBUTE_RESPONSE("medic.common.attribute.responses",
					   "attributeResponse",
				       "PersonAttributeResponse",
					   PersonAttributeResponse.class,
					   new FieldDescriptor[] {new FieldDescriptor(null,"simplesearch.fields.attribute.name","attribute.label",SimpleSearchDataType.STRING),
											  new FieldDescriptor(null,"simplesearch.fields.submitter.name","submitter.name",SimpleSearchDataType.STRING),
											  new FieldDescriptor(null,"simplesearch.fields.subject.name","subject.name",SimpleSearchDataType.STRING),
											  new FieldDescriptor(null,"simplesearch.fields.date.submitted","dateSubmitted",SimpleSearchDataType.DATE),
											  new FieldDescriptor(null,"simplesearch.fields.response","value",SimpleSearchDataType.STRING)});
	
	private String displayName;
	private String databaseName;
	private String tableAlias;
	private Class entityClass;
	private ArrayList<FieldDescriptor> fields;
	
	private SimpleSearchEntity(String name,  String tableAlias, String databaseName, Class entityClass, FieldDescriptor[] fields){
		this.displayName = name;
		this.databaseName = databaseName;
		this.entityClass = entityClass;
		this.tableAlias = tableAlias;
		this.fields = new ArrayList<FieldDescriptor>();
		for(int i = 0; i < fields.length; i++ ){
			this.fields.add(fields[i]);
		}
	}
	
	public String getDatabaseName(){
		return databaseName;
	}
	
	public Class getEntityClass() {
		return entityClass;
	}

	public String getEntityDisplayName() {
		return InternationalisationUtils.getI18NString(displayName);
	}

	public String getTableAlias() {
		return tableAlias;
	}
	
	public ArrayList<String> getFieldDisplayNames() {
		ArrayList<String> results = new ArrayList<String>();
		for(FieldDescriptor f: fields){
			results.add(f.getDisplayName());
		}
		return results;
	}
	
	public ArrayList<FieldDescriptor> getFields(){
		return fields;
	}
}
