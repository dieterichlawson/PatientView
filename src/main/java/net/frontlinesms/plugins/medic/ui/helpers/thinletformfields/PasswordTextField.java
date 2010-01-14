package net.frontlinesms.plugins.medic.ui.helpers.thinletformfields;

import net.frontlinesms.ui.ExtendedThinlet;

public class PasswordTextField extends ThinletFormField<String>{

	private Object textBox;
	public static final String NAME = "passwordField";
	
	public PasswordTextField(ExtendedThinlet thinlet, String label){
		super(thinlet,label, NAME);
		response = "";
		textBox =thinlet.createTextfield(null, null);
		thinlet.setAction(textBox, "textBoxKeyPressed(this.text)", null, this);
		thinlet.add(mainPanel,textBox);
		thinlet.setInteger(textBox, "weightx", 1);
		thinlet.setInteger(mainPanel, "colspan", 1);
		thinlet.setAttachedObject(mainPanel, this);
	}
	
	protected PasswordTextField(ExtendedThinlet thinlet, String label, String name){
		super(thinlet,label, name);
		response = "";
		textBox =thinlet.createTextfield(null, null);
		thinlet.setAction(textBox, "textBoxKeyPressed(this.text)", null, this);
		thinlet.add(mainPanel,textBox);
		thinlet.setInteger(textBox, "weightx", 1);
		thinlet.setInteger(mainPanel, "colspan", 1);
	}

	public Object getThinletPanel() {
		return mainPanel;
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

	public boolean hasResponse() {
		return (getResponse() != "");
	}

	public boolean isValid() {
		return true;
	}

	@Override
	public void setResponse(String s) {
		textBoxKeyPressed(s);
	}

}
