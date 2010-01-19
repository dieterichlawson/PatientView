package net.frontlinesms.plugins.medic.search.drilldownsearch;

import java.util.ArrayList;
import java.util.HashSet;

import net.frontlinesms.plugins.medic.search.drilldownsearch.breadcrumbs.BreadCrumb;
import net.frontlinesms.plugins.medic.search.drilldownsearch.breadcrumbs.EntityType;

public class SearchDescriptorGenerator {

	public static String CHW_NAME = "CHWs";
	public static String FORM_NAME = "Forms";
	public static String FIELD_NAME = "Fields";
	public static String PATIENT_NAME = "Patients";
	
// Currently Searching Patients:
	public static String PATIENTS_CHW = " of CHW @name";
	public static String PATIENTS_FORM = " that have form \"@name\" submitted on them";
	public static String PATIENTS_FIELD = " that have field \"@name\" submitted on them";
	
// Currently Searching Fields:
	public static String FIELDS_CHW = " submitted by CHW @name";
	public static String FIELDS_FORM = " on form \"@name\"";
	public static String FIELDS_PATIENT = " that have been submitted about patient @name";
	
// Currently Searching Forms
	public static String FORMS_CHW = " submitted by CHW @name";
	public static String FORMS_PATIENT = " submitted about patient @name";
	
// Currently Searching CHWs
	public static String CHWS_FORM= " that have submitted form \"@name\"";
	public static String CHWS_FIELD= " that have submitted field \"@name\"";
	
	public static String getSearchDescriptor(ArrayList<BreadCrumb> breadcrumbs, EntityType currentlySearchingEntity, boolean searchingForResponses){
		String result = "";
		if(breadcrumbs.size() != 0){
			if(searchingForResponses){
				for(BreadCrumb bc: breadcrumbs){
					if(bc.getEntityType() ==EntityType.FORM){
						result = "Form Responses";
						currentlySearchingEntity = EntityType.FORM;
					}else if(bc.getEntityType() ==EntityType.FIELD){
						result = "Field Responses";
						currentlySearchingEntity = EntityType.FIELD;
					}
				}
				if(result == ""){
					result = "Form Responses";
					currentlySearchingEntity = EntityType.FORM;
				}
			}else{
				result = currentlySearchingEntity.getName() + "s";
			}
			//create a list of all the superentities
			HashSet<EntityType> superEntities = new HashSet<EntityType>();
			for(BreadCrumb bc: breadcrumbs){
				superEntities.addAll(bc.getRestrictedEntityTypes());
			}
			//next, go through the breadcrumb list, getting the salient breadcrumbs
			ArrayList<BreadCrumb> salientBreadCrumbs = new ArrayList<BreadCrumb>();
			for(BreadCrumb bcrumb: breadcrumbs){
				if(!superEntities.contains(bcrumb.getEntityType())){
						salientBreadCrumbs.add(bcrumb);
				}
			}
			
			for(BreadCrumb b : salientBreadCrumbs){
				result += getStringForBreadcrumbAndEntity(b, currentlySearchingEntity);
			}
		}else{
			result = "All " + currentlySearchingEntity.getName() + "s";
		}
		result = result.replaceAll("null", "");
		return result;
	}
	
	private static String getStringForBreadcrumbAndEntity(BreadCrumb breadcrumb, EntityType entity){
		if(entity.getName() == "Patient"){
			if(breadcrumb.getEntityTypeName() == "CHW"){
				return putNameInString(breadcrumb,PATIENTS_CHW);
			}else if(breadcrumb.getEntityTypeName() == "Form"){
				return putNameInString(breadcrumb,PATIENTS_FORM);	
			}else if(breadcrumb.getEntityTypeName() == "Field"){
				return putNameInString(breadcrumb,PATIENTS_FIELD);
			}
		}else if(entity.getName() == "Field"){
			if(breadcrumb.getEntityTypeName() == "CHW"){
				return putNameInString(breadcrumb,FIELDS_CHW);
			}else if(breadcrumb.getEntityTypeName() == "Form"){
				return putNameInString(breadcrumb,FIELDS_FORM);
			}else if(breadcrumb.getEntityTypeName() == "Patient"){
				return putNameInString(breadcrumb,FIELDS_PATIENT);
			}
		}else if(entity.getName() == "Form"){
			if(breadcrumb.getEntityTypeName() == "CHW"){
				return putNameInString(breadcrumb,FORMS_CHW);
			}else if(breadcrumb.getEntityTypeName() == "Patient"){
				return putNameInString(breadcrumb,FORMS_PATIENT);
			}
		}else if(entity.getName() == "CHW"){
			if(breadcrumb.getEntityTypeName() == "Form"){
				return putNameInString(breadcrumb,CHWS_FORM);
			}else if(breadcrumb.getEntityTypeName() == "Field"){
				return putNameInString(breadcrumb,CHWS_FIELD);
			}
		}else if(entity.getName() == "Freeform Text"){	
			return putNameInString(breadcrumb,FORMS_CHW);
		}
		return null;
	}
	
	private static String putNameInString(BreadCrumb breadcrumb, String string){
		return string.replaceAll("@name", breadcrumb.getName());
	}
}
