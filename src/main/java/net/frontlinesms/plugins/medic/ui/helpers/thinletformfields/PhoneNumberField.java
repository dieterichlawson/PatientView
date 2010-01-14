package net.frontlinesms.plugins.medic.ui.helpers.thinletformfields;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.frontlinesms.ui.ExtendedThinlet;

public class PhoneNumberField extends ThinletFormField<String> {

	private Object textBox;
	public static final String NAME = "phoneNumberField";
	
	public PhoneNumberField(ExtendedThinlet thinlet, String label){
		super(thinlet, label, NAME);
		textBox =thinlet.createTextfield(null, null);
		thinlet.add(mainPanel,textBox);
		thinlet.setInteger(textBox, "weightx", 1);
		thinlet.setInteger(mainPanel, "colspan", 1);
		thinlet.setAttachedObject(mainPanel, this);
	}
	
	protected PhoneNumberField(ExtendedThinlet thinlet, String label, String name){
		super(thinlet, label, name);
		textBox =thinlet.createTextfield(null, null);
		thinlet.add(mainPanel,textBox);
		thinlet.setInteger(textBox, "weightx", 1);
		thinlet.setInteger(mainPanel, "colspan", 1);
	}
	
	public String getResponse() {
		return thinlet.getText(textBox);
	}
	
	public boolean hasResponse() {
		return !getResponse().equals("");
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

	@Override
	public void setResponse(String s) {
		thinlet.setText(textBox, s);
	}

}
