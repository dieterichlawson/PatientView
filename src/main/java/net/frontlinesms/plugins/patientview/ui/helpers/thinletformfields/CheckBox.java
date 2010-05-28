package net.frontlinesms.plugins.patientview.ui.helpers.thinletformfields;

import thinlet.Thinlet;
import net.frontlinesms.ui.ExtendedThinlet;

public class CheckBox extends ThinletFormField<Boolean>{
	
	private Object checkbox;
	protected boolean hasChanged = false;
	
	public CheckBox(ExtendedThinlet thinlet, String label, FormFieldDelegate delegate){
		super(thinlet,label, delegate);
		checkbox = thinlet.createCheckbox(null, "", false);
		thinlet.setWeight(checkbox, 1, 0);
		thinlet.setColspan(checkbox, 1);
		thinlet.setHAlign(checkbox, Thinlet.LEFT);
		thinlet.setHAlign(mainPanel, Thinlet.LEFT);
		thinlet.setAction(checkbox, "checkboxClicked()", null, this);
		thinlet.add(mainPanel,checkbox);
	}

	public boolean hasResponse() {
		return getRawResponse() != null && getStringResponse() != null && getStringResponse()!= "";
	}

	/** checkboxes are always valid */
	public boolean isValid() {
		return true;
	}
	
	public boolean hasChanged(){
		return hasChanged;
	}
	public void checkboxClicked(){
		hasChanged=true;
		super.responseChanged();
	}
	
	@Override
	public void setStringResponse(String s) {
		if(s.equals("true")){
			thinlet.setSelected(checkbox, true);
		}else{
			thinlet.setSelected(checkbox, false);
		}
	}
	
	@Override
	public String getStringResponse() {
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
