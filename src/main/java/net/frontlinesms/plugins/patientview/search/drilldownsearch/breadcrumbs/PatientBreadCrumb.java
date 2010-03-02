package net.frontlinesms.plugins.patientview.search.drilldownsearch.breadcrumbs;

import java.util.ArrayList;

import net.frontlinesms.plugins.patientview.data.domain.people.Patient;

public class PatientBreadCrumb extends BreadCrumb {

	private Patient patient;
	
	//get forms submitted on a specific patient
	private String formQuery = "select distinct f from MedicForm f, Patient p, MedicFormResponse mformr where f = mformr.form and mformr.subject.pid =";
	
	private String fieldQuery = "select distinct ff from MedicField ff,  MedicFormFieldResponse mfieldr, Patient p where ff = mfieldr.field and mfieldr.subject.pid =";

	private String formResponseQuery = "select mformr from MedicFormResponse mformr,  Patient p where mformr.subject = p and p.pid =";
	
	private String fieldResponseQuery = "select mfieldr from MedicFormFieldResponse mfieldr,  Patient p where mfieldr.subject = p and p.pid =";
	
	public PatientBreadCrumb(Patient patient){
		this.patient = patient;
		restrictedEntities = new ArrayList<EntityType>();
		restrictedEntities.add(EntityType.CHW);
		restrictedEntities.add(EntityType.MESSAGE);
		type = EntityType.PATIENT;
	}
	
	@Override
	public String getName() {
		return patient.getName();
	}
	
	@Override
	public String getQueryForEntityType(EntityType e) {
		if(e == EntityType.FIELD){
			return getQueryForField();
		}else if(e == EntityType.FORM){
			return getQueryForForm();
		}
		return null;
	}
	
	private String getQueryForField(){
		return fieldQuery + patient.getPid();
	}
	
	private String getQueryForForm(){
		return formQuery + patient.getPid();
	}
	
	@Override
	public String getResponseQueryForEntityType(EntityType e) {
		if(e == EntityType.FORM){
			return formResponseQuery + patient.getPid();
		}else if(e==EntityType.FIELD){
			return fieldResponseQuery + patient.getPid();
		}
		return null;
	}

}
