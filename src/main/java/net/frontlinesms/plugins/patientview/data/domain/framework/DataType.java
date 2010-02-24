package net.frontlinesms.plugins.patientview.data.domain.framework;

/**
 * The different types of data types in the medic system.
 * These are used for normal form fields as well as notes fields
 * Should figure out a way to do this other than replicating data
 * that is already in core frontline, as many of these are already in
 * the FormFieldType enum
 * @author Dieterich
 *
 */
public enum DataType { 
	
		TRUNCATED_TEXT("Truncated Text", false, null,null),
		WRAPPED_TEXT("Wrapped Text", false, null, null),
		CURRENCY_FIELD("Currency", false, null, null),
		EMAIL_FIELD("Email",false, null,null),
		CHECK_BOX("Check Box", true,"Yes","No"),
		DATE_FIELD("Date",false,null,null),
		PASSWORD_FIELD("Password",false,null,null),
		PHONE_NUMBER_FIELD("Phone Number",false,null,null),
		TIME_FIELD("Time",false,null,null),
		NUMERIC_TEXT_FIELD("Number",false,null,null),
		TEXT_FIELD("Plain Text",false,null,null), 
		TEXT_AREA("Text Area",false,null,null), 
		TRUEFALSE("True/False",true,"True","False"),
		POSITIVENEGATIVE("Positive/Negative",true,"Positive","Negative"),
		YESNO("Yes/No",true,"Yes","No");
		
		private String description;
		private boolean isBoolean;
		private String trueLabel;
		private String falseLabel;
		
		DataType(String desc, boolean isBoolean,String trueLabel,String falseLabel){
			description = desc;
			this.isBoolean = isBoolean;
			if(trueLabel != null){
				this.trueLabel = trueLabel;
				this.falseLabel = falseLabel;
			}
		}
		
		public String toString(){
			return description;
		}
		
		public boolean isBoolean(){
			return isBoolean;
		}
		
		public String getTrueLabel(){
			return trueLabel;
		}
		
		public String getFalseLabel(){
			return falseLabel;
		}
		
		public static DataType getDataTypeForString(String s){
			for(DataType dt : DataType.values()){
				if(dt.toString().equalsIgnoreCase(s)){
					return dt;
				}
			}
			return null;
		}
	}