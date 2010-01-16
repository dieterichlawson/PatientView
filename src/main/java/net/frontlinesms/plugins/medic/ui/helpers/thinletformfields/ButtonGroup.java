package net.frontlinesms.plugins.medic.ui.helpers.thinletformfields;

import net.frontlinesms.ui.ExtendedThinlet;

public class ButtonGroup extends ThinletFormField<Boolean> {
	
	private Object button1;
	private Object button2;
	public static final String NAME = "buttonGroup";
	
	public ButtonGroup(ExtendedThinlet thinlet, String label, String trueLabel, String falseLabel) {
		super(thinlet, label,NAME);
		button1 = this.thinlet.createRadioButton("button1", trueLabel, label, false);
		button2 = this.thinlet.createRadioButton("button2", falseLabel, label, false);
		this.thinlet.setInteger(button1, "weightx", 1);
		this.thinlet.setInteger(button2, "weightx", 1);
		this.thinlet.setInteger(mainPanel, "columns", 3);
		this.thinlet.add(mainPanel,button1);
		this.thinlet.add(mainPanel,button2);
		thinlet.setAttachedObject(mainPanel, this);
	}

	/** button groups are always valid**/
	@Override
	public boolean isValid() {
		return true;
	}
	
	public String getResponse() {
		return (getRawResponse()) ? "true" :"false";
	}

	public Boolean getRawResponse(){
		 return thinlet.isSelected(button1);
	}
	
	@Override
	public boolean hasResponse() {
		return (thinlet.isSelected(button1) || thinlet.isSelected(button2));
	}

	public void setResponse(String s) {
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
