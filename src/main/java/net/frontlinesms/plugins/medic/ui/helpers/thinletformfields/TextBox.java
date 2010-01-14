package net.frontlinesms.plugins.medic.ui.helpers.thinletformfields;

import net.frontlinesms.ui.ExtendedThinlet;

public class TextBox extends ThinletFormField<String>{
	
	protected Object textBox;
	public static final String NAME = "textBoxField";
	
	public TextBox(ExtendedThinlet thinlet, String label){
		super(thinlet,label,NAME);
		//create the text box
		textBox =thinlet.createTextfield(null, null);
		//add the text box
		thinlet.add(mainPanel,textBox);
		thinlet.setInteger(textBox, "weightx", 1);
		thinlet.setInteger(mainPanel, "colspan", 1);
		thinlet.setAttachedObject(mainPanel, this);
	}
	
	protected TextBox(ExtendedThinlet thinlet, String label, String name){
		super(thinlet,label,name);
		//create the text box
		textBox =thinlet.createTextfield(null, null);
		//add the text box
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
	
	/** Text Boxes are always valid**/
	public boolean isValid() {
		return true;
	}

	@Override
	public void setResponse(String s) {
		thinlet.setText(textBox, s);
	}

}
