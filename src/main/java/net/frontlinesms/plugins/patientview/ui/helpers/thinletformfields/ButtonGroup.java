package net.frontlinesms.plugins.patientview.ui.helpers.thinletformfields;

import net.frontlinesms.ui.ExtendedThinlet;

public class ButtonGroup extends ThinletFormField<Boolean> {
	
	private Object button1;
	private Object button2;
	protected boolean hasChanged = false;
	
	public ButtonGroup(ExtendedThinlet thinlet, String label, String trueLabel, String falseLabel, FormFieldDelegate delegate) {
		super(thinlet, label, delegate);
		button1 = this.thinlet.createRadioButton("button1", trueLabel, label, false);
		button2 = this.thinlet.createRadioButton("button2", falseLabel, label, false);
		this.thinlet.setAction(button1, "buttonClicked()", null, this);
		this.thinlet.setAction(button2, "buttonClicked()", null, this);
		this.thinlet.setWeight(button1, 1, 0);
		this.thinlet.setWeight(button2, 1, 0);
		this.thinlet.setColumns(mainPanel, 3);
		this.thinlet.add(mainPanel,button1);
		this.thinlet.add(mainPanel,button2);
	}
	
	public boolean hasChanged(){
		return hasChanged;
	}
	
	public void buttonClicked(){
		hasChanged = true;
		super.responseChanged();
	}

	/** button groups are always valid**/
	@Override
	public boolean isValid() {
		return true;
	}
	
	public String getStringResponse() {
		return (getRawResponse()) ? "true" :"false";
	}

	public Boolean getRawResponse(){
		 return thinlet.isSelected(button1);
	}
	
	@Override
	public boolean hasResponse() {
		return (thinlet.isSelected(button1) || thinlet.isSelected(button2));
	}

	public void setStringResponse(String s) {
		if(s.equals("true")){
			thinlet.setSelected(button1, true);
		}else if(s.equals("false")){
			thinlet.setSelected(button2, true);
		}
	}
	
	public void setRawResponse(Boolean s){
		if(s){
			thinlet.setSelected(button1, true);
		}else{
			thinlet.setSelected(button2, true);
		}
	}

}
