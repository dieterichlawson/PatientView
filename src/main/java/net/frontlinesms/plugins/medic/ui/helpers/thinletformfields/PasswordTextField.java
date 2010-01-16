package net.frontlinesms.plugins.medic.ui.helpers.thinletformfields;

import net.frontlinesms.ui.ExtendedThinlet;

public class PasswordTextField extends TextBox{

	public static final String NAME = "passwordField";
	private String response;
	
	public PasswordTextField(ExtendedThinlet thinlet, String label){
		super(thinlet,label, NAME);
		response = "";
		thinlet.setInteger(mainPanel, "colspan", 1);
		thinlet.setAttachedObject(mainPanel, this);
	}
	
	protected PasswordTextField(ExtendedThinlet thinlet, String label, String name){
		super(thinlet,label, name);
		response = "";
		thinlet.setInteger(mainPanel, "colspan", 1);
	}
	
	public void textBoxKeyPressed(String typed){
		String newText = typed.substring(typed.lastIndexOf("*") + 1);
		if(typed.lastIndexOf("*") <  response.length() -1){
			response = response.substring(0, typed.lastIndexOf("*") + 1) + newText;
		}else{
			response = response + newText;
		}
		String mask = "";
		for(int i = 0; i < response.length();i++){
			mask = mask + "*";
		}
		thinlet.setText(textBox, mask);	
	}

	public boolean isValid() {
		return true;
	}
	
	@Override
	public String getResponse() {
		return response;
	}

	@Override
	public void setResponse(String s) {
		textBoxKeyPressed(s);
	}

}
