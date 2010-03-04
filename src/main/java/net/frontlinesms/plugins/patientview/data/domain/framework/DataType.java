package net.frontlinesms.plugins.patientview.data.domain.framework;

/**
 * Enumerates the different data types in the medic system.
 * These are used for normal form fields as well as attribute fields
 * Should figure out a way to do this other than replicating data
 * that is already in core frontline, as many of these are already in
 * the FormFieldType enum
 *
 */
public enum DataType { 
	
		TRUNCATED_TEXT("Truncated Text", false, null,null),
		WRAPPED_TEXT("Wrapped Text", false, null, null),
		CURRENCY_FIELD("Currency", true, null, null),
		EMAIL_FIELD("Email",true, null,null),
		CHECK_BOX("Check Box", true,"Yes","No"),
		DATE_FIELD("Date",true,null,null),
		PASSWORD_FIELD("Password",true,null,null),
		PHONE_NUMBER_FIELD("Phone Number",true,null,null),
		TIME_FIELD("Time",true,null,null),
		NUMERIC_TEXT_FIELD("Number",true,null,null),
		TEXT_FIELD("Plain Text",true,null,null), 
		TEXT_AREA("Text Area",true,null,null), 
		TRUEFALSE("True/False",true,"True","False"),
		POSITIVENEGATIVE("Positive/Negative",true,"Positive","Negative"),
		YESNO("Yes/No",true,"Yes","No");
		
		private String description;
		private boolean isBoolean;
		private boolean respondable;
		private String trueLabel;
		private String falseLabel;
		
		DataType(String desc, boolean respondable,String trueLabel,String falseLabel){
			this.description = desc;
			this.respondable = respondable;
			if(trueLabel != null){
				this.isBoolean = true;
				this.trueLabel = trueLabel;
				this.falseLabel = falseLabel;
			}else{
				this.isBoolean=false;
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
		
		/**
		 * Returns the data type that has a name
		 * most closely matching the string passed in
		 * @param s
		 * @return
		 */
		public static DataType getDataTypeForString(String s){
			for(DataType dt : DataType.values()){
				if(dt.name().equalsIgnoreCase(s)){
					return dt;
				}
			}
			return null;
		}

		/**
		 * @return whether or not the field can have a response to it
		 */
		public boolean isRespondable() {
			return respondable;
		}

	}