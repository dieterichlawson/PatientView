package net.frontlinesms.plugins.patientview.data.domain.framework;

import net.frontlinesms.ui.i18n.InternationalisationUtils;

/**
 * Enumerates the different data types in the medic system.
 * These are used for normal form fields as well as attribute fields
 * Should figure out a way to do this other than replicating data
 * that is already in core frontline, as many of these are already in
 * the FormFieldType enum
 *
 */
public enum DataType { 
		TRUNCATED_TEXT("plugins.forms.field.truncatedtext", false, null,null,"/icons/components/small/truncatedtext.png"),
		WRAPPED_TEXT("plugins.forms.field.wrappedtext", false, null, null,"/icons/components/small/wrappedtext.png"),
		CURRENCY_FIELD("plugins.forms.field.currency", true, null, null,"/icons/components/small/currencyfield.png"),
		EMAIL_FIELD("common.email",true, null,null,"/icons/components/small/emailfield.png"),
		CHECK_BOX("plugins.forms.field.checkbox", true,"action.yes","action.no","/icons/components/small/checkbox.png"),
		DATE_FIELD("common.date",true,null,null,"/icons/components/small/datefield.png"),
		PASSWORD_FIELD("plugins.forms.field.password",true,null,null,"/icons/components/small/passwordfield.png"),
		PHONE_NUMBER_FIELD("plugins.forms.field.phonenumber",true,null,null,"/icons/components/small/phonenumberfield.png"),
		TIME_FIELD("common.time",true,null,null,"/icons/components/small/timefield.png"),
		NUMERIC_TEXT_FIELD("plugins.forms.field.number",true,null,null,"/icons/components/small/numerictextfield.png"),
		TEXT_FIELD("plugins.forms.field.textfield",true,null,null,"/icons/components/small/textfield.png"), 
		TEXT_AREA("plugins.forms.field.textarea",true,null,null,"/icons/components/small/textarea.png"), 
		TRUEFALSE("datatype.true.false",true,"datatype.true","datatype.false","/icons/components/small/checkbox.png"),
		POSITIVENEGATIVE("datatype.positive.negative",true,"datatype.positive","datatype.negative","/icons/components/small/checkbox.png"),
		YESNO("datatype.yes.no",true,"action.yes","action.no","/icons/components/small/checkbox.png");
		
		private String description;
		private boolean isBoolean;
		private boolean respondable;
		private String trueLabel;
		private String falseLabel;
		
		private String iconPath;
		
		DataType(String desc, boolean respondable,String trueLabel,String falseLabel, String iconPath){
			this.description = desc;
			this.respondable = respondable;
			this.iconPath = iconPath;
			if(trueLabel != null){
				this.isBoolean = true;
				this.trueLabel = trueLabel;
				this.falseLabel = falseLabel;
			}else{
				this.isBoolean=false;
			}
		}
		
		public String toString(){
			return InternationalisationUtils.getI18NString(description);
		}
		
		public boolean isBoolean(){
			return isBoolean;
		}
		
		public String getTrueLabel(){
			return InternationalisationUtils.getI18NString(trueLabel);
		}
		
		public String getFalseLabel(){
			return InternationalisationUtils.getI18NString(falseLabel);
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

		public String getIconPath() {
			return iconPath;
		}

	}