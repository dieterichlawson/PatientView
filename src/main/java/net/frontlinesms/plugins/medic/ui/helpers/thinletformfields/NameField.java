package net.frontlinesms.plugins.medic.ui.helpers.thinletformfields;

import net.frontlinesms.ui.ExtendedThinlet;

public class NameField extends TextBox{

	protected boolean hasChanged;
	public static final String NAME = "nameField";
	
	public NameField(ExtendedThinlet thinlet, String initialText) {
		super(thinlet, "Name:", NAME);
		hasChanged = false;
		if(initialText != null){
			thinlet.setText(textBox, initialText);
		}
		thinlet.setAction(textBox, "textChanged(this.text)", null, this);
		thinlet.setAttachedObject(mainPanel, this);
	}
	
	public void textChanged(String text){
		hasChanged = true;
	}
	
	public boolean hasChanged(){
		return hasChanged;
	}

}
