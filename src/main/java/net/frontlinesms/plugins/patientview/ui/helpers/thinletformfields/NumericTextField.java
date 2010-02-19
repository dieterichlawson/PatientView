package net.frontlinesms.plugins.patientview.ui.helpers.thinletformfields;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.frontlinesms.ui.ExtendedThinlet;

public class NumericTextField extends TextBox{

	private Object textBox;
	public static final String NAME = "numericTextField";
	
	public NumericTextField(ExtendedThinlet thinlet, String label){
		super(thinlet,label, NAME);
		thinlet.setAttachedObject(mainPanel, this);
	}

	protected NumericTextField(ExtendedThinlet thinlet, String label, String name){
		super(thinlet,label, name);
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
}
