package net.frontlinesms.plugins.medic.search.drilldownsearch;

import net.frontlinesms.plugins.medic.search.drilldownsearch.breadcrumbs.EntityType;


public class QueryConstants {
	private static final String chwNameField= "name";
	private static final String chwTableName="CommunityHealthWorker";
	private static final String chwTablAlias="chw";
	
	private static final String patientNameField= "name";
	private static final String patientTableName="Patient";
	private static final String patientTableAlias="p";
	
	private static final String formNameField= "name";
	private static final String formTableName="MedicForm";
	private static final String formTableAlias="f";
	
	private static final String fieldNameField= "label";
	private static final String fieldTableName="MedicField";
	private static final String fieldTableAlias="ff";
	
	private static final String messageNameField = "messageContent";
	private static final String messageTableName = "MedicMessageResponse";
	private static final String messageTableAlias = "mmr";
	
	private static final String fieldResponseTableName = "MedicFieldResponse";
	private static final String fieldResponseTableAlias = "mfieldr";
	
	private static final String formResponseTableName = "MedicFormResponse";
	private static final String formResponseTableAlias = "mformr";
	
	private static String [] chwColumns = new String[]{"name", "birthdate" , "contactInfo.phoneNumber"};
	private static String [] patientColumns = new String[]{"name", "birthdate","chw.name"};
	private static String [] fieldColumns = new String[]{"label", "form.name"};
	private static String [] formColumns = new String[]{"name"};
	
	//response columns
	private static String [] formResponseColumns = new String[]{"form.name","submitter.name","subject.name","dateSubmitted"};
	private static String [] messageColumns = new String[]{"submitter.name","dateSubmitted","messageContent"};
	private static String [] fieldResponseColumns = new String[]{"field.label","value","submitter.name","subject.name","dateSubmitted"};
	
	public static String getNameField(EntityType e){
		if(e ==EntityType.CHW){
			return chwNameField;
		}else if(e==EntityType.PATIENT){
			return patientNameField;
		}else if(e==EntityType.FORM){
			return formNameField;
		}else if(e==EntityType.FIELD){
			return fieldNameField;
		}else if(e==EntityType.MESSAGE){
			return messageNameField;
		}
		return null;
	}
	
	public static String getTableName(EntityType e, boolean searchingForResponse){
		if(!searchingForResponse){
			if(e ==EntityType.CHW){
				return chwTableName;
			}else if(e==EntityType.PATIENT){
				return patientTableName;
			}else if(e==EntityType.FORM){
				return formTableName;
			}else if(e==EntityType.FIELD){
				return fieldTableName;
			}else if(e==EntityType.MESSAGE){
				return messageTableName;
			}
		}else{
			if(e==EntityType.FORM){
				return formResponseTableName;
			}else if(e==EntityType.FIELD){
				return fieldResponseTableName;
			}
		}
		return null;
	}
	
	public static String getTableAlias(EntityType e,boolean searchingForResponse){
		if(!searchingForResponse){
			if(e ==EntityType.CHW){
				return chwTablAlias;
			}else if(e==EntityType.PATIENT){
				return patientTableAlias;
			}else if(e==EntityType.FORM){
				return formTableAlias;
			}else if(e==EntityType.FIELD){
				return fieldTableAlias;
			}else if(e==EntityType.MESSAGE){
				return messageTableAlias;
			}
		}else {
			if(e==EntityType.FORM){
				return formResponseTableAlias;
			}else if(e==EntityType.FIELD){
				return fieldResponseTableAlias;
			}
		}
		return null;
	}
	
	public static String getSortColumn(int column, EntityType e, boolean searchingForResponse){
		if(!searchingForResponse){
			if(e ==EntityType.CHW){
				return chwColumns[column];
			}else if(e==EntityType.PATIENT){
				return patientColumns[column];
			}else if(e==EntityType.FORM){
				return formColumns[column];
			}else if(e==EntityType.FIELD){
				return fieldColumns[column];
			}else if(e==EntityType.MESSAGE){
				return messageColumns[column];
			}
		}else {
			if(e == EntityType.FORM){
				return formResponseColumns[column];
			}else if(e==EntityType.FIELD){
				return fieldResponseColumns[column];
			}
		}
		return null;
	}
	
}
