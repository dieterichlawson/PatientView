package net.frontlinesms.plugins.patientview.search.drilldownsearch.breadcrumbs;

import java.util.ArrayList;

import net.frontlinesms.plugins.patientview.data.domain.framework.MedicField;
import net.frontlinesms.plugins.patientview.data.domain.framework.MedicFormField;


public class FieldBreadCrumb extends BreadCrumb {

	private MedicField field;

	private static final String patientQuery = "select distinct p from Patient p, MedicFormFieldResponse mfieldr where p = mfieldr.subject and mfieldr.field.fid=";

	private static final String chwQuery = "select distinct chw from CommunityHealthWorker chw, MedicFormFieldResponse mfieldr where chw = mfieldr.submitter and mfieldr.field.fid=";
	
	private static final String fieldResponseQuery = "select mfieldr from MedicFormFieldResponse mfieldr where mfieldr.field.fid =";
	
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
			return patientQuery + field.getAid();
		} else if (e == EntityType.CHW) {
			return chwQuery + field.getAid();
		}
		return null;
	}
	
	@Override
	public String getResponseQueryForEntityType(EntityType e) {
		if(e == EntityType.FIELD){
			return fieldResponseQuery + field.getAid();
		}
		return null;
	}
}