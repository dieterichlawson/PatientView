package net.frontlinesms.plugins.medic.search.drilldownsearch.breadcrumbs;

import java.util.ArrayList;

import net.frontlinesms.plugins.medic.data.domain.framework.MedicForm;

public class FormBreadCrumb extends BreadCrumb {

	private MedicForm form;

	private static final String formResponseQuery = "select mformr from MedicFormResponse mformr where mformr.form.fid =";
	
	private static final String fieldQuery = "select ff from MedicFormField ff where ff.datatype != 'TRUNCATED_TEXT' and ff.datatype != 'WRAPPED_TEXT' and ff.form.fid =";
	
	private static final String chwQuery = "select distinct chw from CommunityHealthWorker chw, MedicFormResponse mformr where chw = mformr.submitter and mformr.form.fid=";
	
	private static final String patientQuery = "select distinct p from Patient p, MedicFormResponse mformr where p = mformr.subject and mformr.form.fid=";
	
	public FormBreadCrumb(MedicForm form){
		this.form = form;
		restrictedEntities = new ArrayList<EntityType>();
		restrictedEntities.add(EntityType.MESSAGE);
		type = EntityType.FORM;
	}
	
	@Override
	public String getName() {
		return form.getName();
	}
	
	@Override
	public String getQueryForEntityType(EntityType e) {
		if(e == EntityType.CHW){
			return chwQuery + form.getFid();
		}else if(e == EntityType.PATIENT){
			return patientQuery + form.getFid();
		}else if(e == EntityType.FIELD){
			return fieldQuery + form.getFid();
		}
		return null;
	}
	
	@Override
	public String getResponseQueryForEntityType(EntityType e) {
		if(e == EntityType.FORM){
			return formResponseQuery + form.getFid();
		}
		return null;
	}

}
