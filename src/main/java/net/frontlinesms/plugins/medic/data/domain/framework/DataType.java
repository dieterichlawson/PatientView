package net.frontlinesms.plugins.medic.data.domain.framework;

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
		NUMBER("Number",false,null,null),
		TEXT("Plain Text",false,null,null), 
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
	}