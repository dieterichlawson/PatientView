package net.frontlinesms.plugins.patientview.ui.helpers.thinletformfields;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.frontlinesms.ui.ExtendedThinlet;

public class NumericTextField extends TextBox<Number>{
	
	public NumericTextField(ExtendedThinlet thinlet, String label, FormFieldDelegate delegate){
		super(thinlet,label, delegate);
		
	}

	public boolean isValid() {
		//this checks for letters
		Pattern pattern = Pattern.compile("\\D+");
		Matcher matcher = pattern.matcher(getStringResponse());
		if(matcher.matches()){
			return false;
		}else{
			return true;
		}
	}

	@Override
	public Number getRawResponse() {
		try{
			return Integer.parseInt(getStringResponse());
		}catch(NumberFormatException e){
			try{
				return Double.parseDouble(getStringResponse());
			}catch(NumberFormatException f){
				return null;
			}
		}
	}

	@Override
	public void setRawResponse(Number response) {
		setStringResponse(response.toString());
	}
}
