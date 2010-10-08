package net.frontlinesms.plugins.patientview.ui.thinletformfields;

import net.frontlinesms.ui.ExtendedThinlet;

public class TextArea extends ThinletFormField<String>{

	private Object textArea;
	protected boolean hasChanged = false;
	
	public TextArea(ExtendedThinlet thinlet, String label, FormFieldDelegate delegate){
		super(thinlet, label, delegate);
		textArea =ExtendedThinlet.create("textarea");
		thinlet.add(mainPanel,textArea);
		thinlet.setWeight(textArea, 1,0);
		thinlet.setColspan(textArea,1);
		thinlet.setColspan(mainPanel, 1);
		thinlet.setColumns(mainPanel, 1);
		thinlet.setGap(mainPanel, 4);
		thinlet.setAction(textArea, "textAreaKeyPressed(this.text)", null, this);
	}
	
	/** Text Areas are always valid**/
	public void validate() {
		return;
	}
	
	public void textAreaKeyPressed(String text){
		hasChanged = true;
		super.responseChanged();
	}

	@Override
	public String getRawResponse() {
		return getStringResponse();
	}

	@Override
	public String getStringResponse() {
		return thinlet.getText(textArea);
		
	}

	@Override
	public void setRawResponse(String response) {
		setStringResponse(response);
	}

	@Override
	public void setStringResponse(String response) {
		thinlet.setText(textArea, response);
	}

	@Override
	public boolean hasChanged() {
		return hasChanged;
	}
	
}
