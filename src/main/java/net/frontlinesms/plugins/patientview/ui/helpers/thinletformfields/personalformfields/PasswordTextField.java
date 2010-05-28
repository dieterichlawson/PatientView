package net.frontlinesms.plugins.patientview.ui.helpers.thinletformfields.personalformfields;

import java.security.GeneralSecurityException;

import net.frontlinesms.plugins.patientview.data.domain.people.Person;
import net.frontlinesms.plugins.patientview.data.domain.people.User;
import net.frontlinesms.plugins.patientview.ui.helpers.thinletformfields.FormFieldDelegate;
import net.frontlinesms.plugins.patientview.ui.helpers.thinletformfields.TextField;
import net.frontlinesms.ui.ExtendedThinlet;
import net.frontlinesms.ui.i18n.InternationalisationUtils;

/** A masked password box. */
public class PasswordTextField extends TextField implements PersonalFormField {

	private String response;

	public PasswordTextField(ExtendedThinlet thinlet, String initialText, FormFieldDelegate delegate) {
		super(thinlet, InternationalisationUtils.getI18NString("login.password")+":", delegate);
		response = "";
		if(initialText != null && !initialText.equals("")){
			textBoxKeyPressed(initialText);
		}
		thinlet.setColspan(mainPanel, 1);
	}

	public void textBoxKeyPressed(String typed) {
		super.textBoxKeyPressed(typed);
		String newText = typed.substring(typed.lastIndexOf("*") + 1);
		if (typed.lastIndexOf("*") < response.length() - 1) {
			response = response.substring(0, typed.lastIndexOf("*") + 1)
					+ newText;
		} else {
			response = response + newText;
		}
		String mask = "";
		for (int i = 0; i < response.length(); i++) {
			mask = mask + "*";
		}
		thinlet.setText(textBox, mask);
	}

	public boolean isValid() {
		return true;
	}

	@Override
	public String getStringResponse() {
		return response;
	}

	@Override
	public void setStringResponse(String s) {
		textBoxKeyPressed(s);
	}

	public void setFieldForPerson(Person p) {
		User user = (User) p;
		try {
			user.setPassword(getStringResponse());
		} catch (GeneralSecurityException e) {
			e.printStackTrace();
		}
	}

}
