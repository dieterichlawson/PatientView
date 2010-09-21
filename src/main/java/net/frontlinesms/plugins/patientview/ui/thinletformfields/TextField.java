package net.frontlinesms.plugins.patientview.ui.thinletformfields;

import net.frontlinesms.ui.ExtendedThinlet;

/**
 * A ThinletFormField that allows for the editing of plain text
 *
 */
public class TextField extends TextBox<String> {

	public TextField(ExtendedThinlet thinlet, String label,FormFieldDelegate delegate) {
		super(thinlet, label, delegate);
	}

	/** Text Fields are always valid **/
	public boolean isValid() {
		return true;
	}

	@Override
	public String getRawResponse() {
		return getStringResponse();
	}

	@Override
	public void setRawResponse(String response) {
		setStringResponse(response);
	}
}
