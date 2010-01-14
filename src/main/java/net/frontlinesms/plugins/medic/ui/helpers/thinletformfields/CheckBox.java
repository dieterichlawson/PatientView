package net.frontlinesms.plugins.medic.ui.helpers.thinletformfields;

import net.frontlinesms.ui.ExtendedThinlet;

public class CheckBox extends ThinletFormField{
	
	private Object checkbox;
	public static final String NAME = "checkBox";
	
	public CheckBox(ExtendedThinlet thinlet, String label){
		super(thinlet,label, NAME);
		checkbox = this.thinlet.createCheckbox(null, label, false);
		this.thinlet.setInteger(checkbox, "weightx", 1);
		this.thinlet.setInteger(checkbox, "colspan", 1);
		this.thinlet.setChoice(checkbox, "halign", "center");
		thinlet.setAttachedObject(mainPanel, this);
	}

	public String getResponse() {
		return thinlet.isSelected(checkbox) + "";
	}

	public Object getThinletPanel() {
		return checkbox;
	}

	public boolean hasResponse() {
		return true;
	}

	public boolean isValid() {
		return true;
	}

	public void setResponse(String s) {
		if(s.equals("true")){
			thinlet.setSelected(checkbox, true);
		}
	}

}
