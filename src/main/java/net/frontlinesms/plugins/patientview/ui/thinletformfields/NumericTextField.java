package net.frontlinesms.plugins.patientview.ui.thinletformfields;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hibernate.classic.ValidationFailure;

import net.frontlinesms.ui.ExtendedThinlet;

public class NumericTextField extends TextBox<Number>{
	
	public NumericTextField(ExtendedThinlet thinlet, String label, FormFieldDelegate delegate){
		super(thinlet,label, delegate);
		
	}

	/**
	 * Is valid if there are not letters in the box.
	 * TODO: Make this more stringent
	 */
	public void validate() throws ValidationFailure {
		//this checks for letters
		Pattern pattern = Pattern.compile("\\D+");
		Matcher matcher = pattern.matcher(getStringResponse());
		if(matcher.matches()){
			throw new ValidationFailure("\"" + getLabel().replace(":", "")+ "\" does not contain a valid number");
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
