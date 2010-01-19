package net.frontlinesms.plugins.medic.search.drilldownsearch.breadcrumbs;

import java.util.ArrayList;

import net.frontlinesms.plugins.medic.data.domain.people.CommunityHealthWorker;

public class CommunityHealthWorkerBreadCrumb extends BreadCrumb {

	private CommunityHealthWorker chw;
	
	private static final String formResponseQuery = "select mformr from MedicFormResponse mformr where mformr.submitter.pid =";
	
	private static final String fieldResponseQuery = "select mfieldr from MedicFieldResponse mfieldr where mfieldr.submitter.pid =";
	
	private static final String patientQuery = "select p from Patient p, CommunityHealthWorker chw where p.chw = chw and chw.pid =";
	
	private static final String fieldQuery = "select distinct ff from MedicField ff, MedicFieldResponse mfieldr where mfieldr.field = ff and mfieldr.submitter.pid =";
	
	private static final String formQuery = "select distinct ff from MedicForm ff, MedicFormResponse mformr where mformr.form = ff and mformr.submitter.pid =";
	
	private static final String messageQuery = "select mmr from MedicMessageResponse mmr where mmr.submitter.pid =";
	
	public CommunityHealthWorkerBreadCrumb(CommunityHealthWorker chw){
		restrictedEntities = new ArrayList<EntityType>();
		type = EntityType.CHW;
		this.chw = chw;
	}
	
	@Override
	public String getQueryForEntityType(EntityType e) {
		if(e == EntityType.PATIENT){
			return patientQuery + chw.getPid();
		}else if(e == EntityType.FIELD){
			return fieldQuery + chw.getPid();
		}else if(e== EntityType.FORM){
			return formQuery + chw.getPid();
		}else if(e==EntityType.MESSAGE){
			return messageQuery + chw.getPid();
		}
		return null;
	}

	@Override
	public String getResponseQueryForEntityType(EntityType e) {
		if(e == EntityType.FORM){
			return formResponseQuery + chw.getPid();
		}else if(e == EntityType.FIELD){
			return fieldResponseQuery + chw.getPid();
		}
		return null;
	}


	@Override
	public String getName() {
		return chw.getName();
	}

}
