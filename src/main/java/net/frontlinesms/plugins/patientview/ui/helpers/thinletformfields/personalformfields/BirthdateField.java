package net.frontlinesms.plugins.patientview.ui.helpers.thinletformfields.personalformfields;

import java.util.Date;

import net.frontlinesms.plugins.patientview.data.domain.people.Person;
import net.frontlinesms.plugins.patientview.ui.helpers.thinletformfields.DateField;
import net.frontlinesms.plugins.patientview.ui.helpers.thinletformfields.FormFieldDelegate;
import net.frontlinesms.ui.ExtendedThinlet;
import net.frontlinesms.ui.i18n.InternationalisationUtils;

public class BirthdateField extends DateField implements PersonalFormField{
	
	public BirthdateField(ExtendedThinlet thinlet, Date initialDate, FormFieldDelegate delegate) {
		super(thinlet, InternationalisationUtils.getI18NString("thinletformfields.birthdate"), delegate);		
		if(initialDate != null){
			String initialText = df.format(initialDate);
			thinlet.setText(textBox, initialText);
		}
	}

	public void setFieldForPerson(Person p) {
		p.setBirthdate(super.getRawResponse());
	}

}
