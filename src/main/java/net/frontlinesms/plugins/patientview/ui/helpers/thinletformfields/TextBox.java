package net.frontlinesms.plugins.patientview.ui.helpers.thinletformfields;

import net.frontlinesms.ui.ExtendedThinlet;

public class TextBox extends ThinletFormField<String> {

	protected Object textBox;
	protected boolean hasChanged;
	public static final String NAME = "textBoxField";

	public TextBox(ExtendedThinlet thinlet, String label) {
		this(thinlet, label, NAME);
	}

	protected TextBox(ExtendedThinlet thinlet, String label, String name) {
		super(thinlet, label, name);
		// create the text box
		textBox = thinlet.createTextfield(null, null);
		// add the text box
		thinlet.add(mainPanel, textBox);
		thinlet.setInteger(textBox, "weightx", 1);
		thinlet.setInteger(mainPanel, "colspan", 1);
		thinlet.setAction(textBox, "textBoxKeyPressed(this.text)", null, this);
		hasChanged = false;
	}

	/**
	 * The method called whenever a key is pressed in the text box.
	 * 
	 * @param text
	 *            the text currently in the box
	 */
	public void textBoxKeyPressed(String text) {
		hasChanged = true;
	}

	/**
	 * A method for getting at the text field of this text box. Useful if you
	 * want to change the default size or behaviour of the textbox.
	 * 
	 * @return the Thinlet textfield of this text box
	 */
	public Object getTextField() {
		return textBox;
	}

	public boolean hasChanged() {
		return hasChanged;
	}

	/** Text Boxes are always valid **/
	public boolean isValid() {
		return true;
	}

	@Override
	public String getRawResponse() {
		return getResponse();
	}

	@Override
	public String getResponse() {
		return thinlet.getText(textBox);

	}

	@Override
	public void setRawResponse(String response) {
		setResponse(response);
	}

	@Override
	public void setResponse(String response) {
		thinlet.setText(textBox, response);
	}

}
