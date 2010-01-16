package net.frontlinesms.plugins.medic.ui.helpers.thinletformfields;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.frontlinesms.ui.ExtendedThinlet;

public class PhoneNumberField extends TextBox{

	
	public static final String NAME = "phoneNumberField";
	
	public PhoneNumberField(ExtendedThinlet thinlet, String label){
		super(thinlet, label, NAME);
		thinlet.setAttachedObject(mainPanel, this);
	}
	
	protected PhoneNumberField(ExtendedThinlet thinlet, String label, String name){
		super(thinlet, label, name);
	}

	
	public boolean isValid() {
		//if there are any letters in the input, it will find them and fail validation
		//TODO: right now, we don't know enough about the possible phone number
		//formats to really validate this. Should fix this.
		Pattern pattern = Pattern.compile("[a-z]+",Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(getResponse());
		if(matcher.matches()){
			return false;
		}else{
			return true;
		}
	}
}
