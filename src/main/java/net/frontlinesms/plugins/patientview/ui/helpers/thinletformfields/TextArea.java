package net.frontlinesms.plugins.patientview.ui.helpers.thinletformfields;

import net.frontlinesms.ui.ExtendedThinlet;

public class TextArea extends ThinletFormField<String>{

	private Object textArea;
	public static final String NAME = "textAreaField";
	
	public TextArea(ExtendedThinlet thinlet, String label){
		super(thinlet, label, NAME);
		textArea =ExtendedThinlet.create("textarea");
		thinlet.add(mainPanel,textArea);
		thinlet.setInteger(textArea, "weightx", 1);
		thinlet.setInteger(textArea, "colspan", 1);
		thinlet.setInteger(mainPanel, "colspan", 1);
		thinlet.setInteger(mainPanel, "columns", 1);
		thinlet.setAttachedObject(mainPanel, this);

	}
	
	protected TextArea(ExtendedThinlet thinlet, String label, String name){
		super(thinlet, label, name);
		textArea =ExtendedThinlet.create("textarea");
		thinlet.add(mainPanel,textArea);
		thinlet.setInteger(textArea, "weightx", 1);
		thinlet.setInteger(textArea, "colspan", 1);
		thinlet.setInteger(mainPanel, "colspan", 1);
		thinlet.setInteger(mainPanel, "columns", 1);
	}
	
	/** Text Areas are always valid**/
	public boolean isValid() {
		return true;
	}

	@Override
	public String getRawResponse() {
		return getResponse();
	}

	@Override
	public String getResponse() {
		return thinlet.getText(textArea);
		
	}

	@Override
	public void setRawResponse(String response) {
		setResponse(response);
	}

	@Override
	public void setResponse(String response) {
		thinlet.setText(textArea, response);
	}
	
}