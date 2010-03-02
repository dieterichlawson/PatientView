package net.frontlinesms.plugins.patientview.ui.helpers.thinletformfields;

import net.frontlinesms.ui.ExtendedThinlet;

public class CheckBox extends ThinletFormField<Boolean>{
	
	private Object checkbox;
	public static final String NAME = "checkBox";
	protected boolean hasChanged;
	
	public CheckBox(ExtendedThinlet thinlet, String label){
		super(thinlet,label, NAME);
		checkbox = this.thinlet.createCheckbox(null, "", false);
		this.thinlet.setInteger(checkbox, "weightx", 1);
		this.thinlet.setInteger(checkbox, "colspan", 1);
		this.thinlet.setChoice(checkbox, "halign", "left");
		this.thinlet.setChoice(mainPanel, "halign", "left");
		this.thinlet.setAction(checkbox, "checkboxClicked()", null, this);
		thinlet.add(mainPanel,checkbox);
		thinlet.setAttachedObject(mainPanel, this);
		hasChanged = false;
	}

	public boolean hasResponse() {
		return getRawResponse() != null && getResponse() != null && getResponse()!= "";
	}

	public boolean isValid() {
		return true;
	}
	
	public boolean hasChanged(){
		return hasChanged;
	}
	public void checkboxClicked(){
		hasChanged=true;
	}
	
	@Override
	public void setResponse(String s) {
		if(s.equals("true")){
			thinlet.setSelected(checkbox, true);
		}else{
			thinlet.setSelected(checkbox, false);
		}
	}
	
	@Override
	public String getResponse() {
		return thinlet.isSelected(checkbox) + "";
	}
	
	@Override
	public Boolean getRawResponse(){
		return thinlet.isSelected(checkbox);
	}
	
	@Override
	public void setRawResponse(Boolean s) {
		if(s){
			thinlet.setSelected(checkbox, true);
		}else{
			thinlet.setSelected(checkbox, false);
		}
	}

}
