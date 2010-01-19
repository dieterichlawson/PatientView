package net.frontlinesms.plugins.medic.search.drilldownsearch.breadcrumbs;

import java.util.ArrayList;

import net.frontlinesms.plugins.medic.data.domain.framework.MedicField;
import net.frontlinesms.plugins.medic.data.domain.framework.MedicFormField;


public class FieldBreadCrumb extends BreadCrumb {

	private MedicField field;

	private static final String patientQuery = "select distinct p from Patient p, MedicFieldResponse mfieldr where p = mfieldr.subject and mfieldr.field.fid=";

	private static final String chwQuery = "select distinct chw from CommunityHealthWorker chw, MedicFieldResponse mfieldr where chw = mfieldr.submitter and mfieldr.field.fid=";
	
	private static final String fieldResponseQuery = "select mfieldr from MedicFieldResponse mfieldr where mfieldr.field.fid =";
	
	public FieldBreadCrumb(MedicField field) {
		restrictedEntities = new ArrayList<EntityType>();
		restrictedEntities.add(EntityType.FORM);
		restrictedEntities.add(EntityType.MESSAGE);
		type = EntityType.FIELD;
		this.field = field;
	}

	public MedicField getField() {
		return field;
	}
	@Override
	public String getName() {
		return field.getLabel();
	}

	@Override
	public String getQueryForEntityType(EntityType e) {
		if (e == EntityType.PATIENT) {
			return patientQuery + field.getFid();
		} else if (e == EntityType.CHW) {
			return chwQuery + field.getFid();
		}
		return null;
	}
	
	@Override
	public String getResponseQueryForEntityType(EntityType e) {
		if(e == EntityType.FIELD){
			return fieldResponseQuery + field.getFid();
		}
		return null;
	}
}