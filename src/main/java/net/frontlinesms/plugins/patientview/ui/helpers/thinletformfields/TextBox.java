package net.frontlinesms.plugins.patientview.ui.helpers.thinletformfields;

import net.frontlinesms.ui.ExtendedThinlet;

/**
 * An abstract class for ThinletFormFields that need a text box and a label
 *
 * @param <E>
 */
public abstract class TextBox<E> extends ThinletFormField<E> {

	protected Object textBox;
	protected boolean hasChanged = false;

	protected TextBox(ExtendedThinlet thinlet, String label, FormFieldDelegate delegate) {
		super(thinlet, label, delegate);
		// create the text box
		textBox = thinlet.createTextfield(null, null);
		// add the text box
		thinlet.add(mainPanel, textBox);
		thinlet.setWeight(textBox,1,0);
		thinlet.setColspan(textBox, 1);
		thinlet.setAction(textBox, "textBoxKeyPressed(this.text)", null, this);
	}

	/**
	 * The method called whenever a key is pressed in the text box.
	 * 
	 * @param text
	 *            the text currently in the box
	 */
	public void textBoxKeyPressed(String text) {
		hasChanged = true;
		super.responseChanged();
	}
	
	public boolean hasChanged() {
		return hasChanged;
	}
	
	@Override
	public String getStringResponse() {
		return thinlet.getText(textBox);

	}

	@Override
	public void setStringResponse(String response) {
		thinlet.setText(textBox, response);
	}

}
