package net.frontlinesms.plugins.medic.ui.helpers.thinletformfields;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.frontlinesms.ui.ExtendedThinlet;

public class NumericTextField extends ThinletFormField<String>{

	private Object textBox;
	public static final String NAME = "numericTextField";
	
	public NumericTextField(ExtendedThinlet thinlet, String label){
		super(thinlet,label, NAME);
		textBox =thinlet.createTextfield(null, null);
		thinlet.add(mainPanel,textBox);
		thinlet.setInteger(textBox, "weightx", 1);
		thinlet.setInteger(mainPanel, "colspan", 1);
		thinlet.setAttachedObject(mainPanel, this);
	}

	protected NumericTextField(ExtendedThinlet thinlet, String label, String name){
		super(thinlet,label, name);
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
		//this checks for letters
		Pattern pattern = Pattern.compile("\\D+");
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
