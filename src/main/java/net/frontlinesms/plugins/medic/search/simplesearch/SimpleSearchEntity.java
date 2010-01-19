package net.frontlinesms.plugins.medic.search.simplesearch;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import net.frontlinesms.plugins.medic.data.domain.framework.MedicField;
import net.frontlinesms.plugins.medic.data.domain.framework.MedicForm;
import net.frontlinesms.plugins.medic.data.domain.people.CommunityHealthWorker;
import net.frontlinesms.plugins.medic.data.domain.people.Patient;
import net.frontlinesms.plugins.medic.data.domain.people.User;
import net.frontlinesms.plugins.medic.data.domain.response.MedicFieldResponse;
import net.frontlinesms.plugins.medic.data.domain.response.MedicFormResponse;
import net.frontlinesms.plugins.medic.data.domain.response.MedicMessageResponse;
import net.frontlinesms.plugins.medic.search.FieldDescriptor;

public enum SimpleSearchEntity {

	PATIENT("Patients",  //Entity name
			"pat",   //entity table alias
			"Patient",
			 Patient.class,   //entity class
			 new FieldDescriptor[] {new FieldDescriptor(null,"name","name",SimpleSearchDataType.STRING),
									new FieldDescriptor(null,"CHW name","chw.name",SimpleSearchDataType.STRING),
									new FieldDescriptor(null,"birthdate","birthdate",SimpleSearchDataType.DATE),
									new FieldDescriptor(null,"gender","gender",SimpleSearchDataType.ENUM)}),
	
	CHW("CHWs",
		"chw",
		"CommunityHealthWorker",
		 CommunityHealthWorker.class,
		 new FieldDescriptor[] {new FieldDescriptor(null,"name","name",SimpleSearchDataType.STRING),
								new FieldDescriptor(null,"phone number","contactInfo.phoneNumber",SimpleSearchDataType.STRING),
								new FieldDescriptor(null,"birthdate","birthdate",SimpleSearchDataType.DATE),
								new FieldDescriptor(null,"gender","gender",SimpleSearchDataType.ENUM)}),
	MESSAGE("Text Messages",
			"sms",
			"MedicMessageResponse",
			 MedicMessageResponse.class,
			 new FieldDescriptor[] {new FieldDescriptor(null,"submitter","submitter.name",SimpleSearchDataType.STRING),
									new FieldDescriptor(null,"content","messageContent",SimpleSearchDataType.STRING),
									new FieldDescriptor(null,"date submitted","dateSubmitted",SimpleSearchDataType.DATE)}),
	FORM("Forms",
		 "form",
		 "MedicForm",
		 MedicForm.class,
		 new FieldDescriptor[] {new FieldDescriptor(null,"name","name",SimpleSearchDataType.STRING)}),
	     
	FIELD("Fields",
		  "field",
		  "MedicField",
		   MedicField.class,
		   new FieldDescriptor[] {new FieldDescriptor(null,"label","label",SimpleSearchDataType.STRING)}),
		   
	FORM_RESPONSE("Form Responses",
				   "formr",
				   "MedicFormResponse",
				   MedicFormResponse.class,
				   new FieldDescriptor[] {new FieldDescriptor(null,"form name","form.name",SimpleSearchDataType.STRING),
										  new FieldDescriptor(null,"submitter name","submitter.name",SimpleSearchDataType.STRING),
										  new FieldDescriptor(null,"subject name","subject.name",SimpleSearchDataType.STRING),
										  new FieldDescriptor(null,"date submitted","dateSubmited",SimpleSearchDataType.DATE)}),
					   
    FIELD_RESPONSE("Field Responses",
				   "fieldr",
				   "MedicFieldResponse",
				   MedicFieldResponse.class,
				   new FieldDescriptor[] {new FieldDescriptor(null,"field label","field.label",SimpleSearchDataType.STRING),
										  new FieldDescriptor(null,"submitter name","submitter.name",SimpleSearchDataType.STRING),
										  new FieldDescriptor(null,"subject name","subject.name",SimpleSearchDataType.STRING),
										  new FieldDescriptor(null,"date submitted","dateSubmited",SimpleSearchDataType.DATE),
				   						  new FieldDescriptor(null,"response","value",SimpleSearchDataType.STRING)});
	
	
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
		return displayName;
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
