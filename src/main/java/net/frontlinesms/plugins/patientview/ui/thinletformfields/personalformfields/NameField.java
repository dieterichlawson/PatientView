package net.frontlinesms.plugins.patientview.ui.thinletformfields.personalformfields;

import org.hibernate.classic.ValidationFailure;

import net.frontlinesms.plugins.patientview.data.domain.people.Person;
import net.frontlinesms.plugins.patientview.ui.thinletformfields.FormFieldDelegate;
import net.frontlinesms.plugins.patientview.ui.thinletformfields.TextField;
import net.frontlinesms.ui.ExtendedThinlet;
import net.frontlinesms.ui.i18n.InternationalisationUtils;

public class NameField extends TextField implements PersonalFormField{

	
	public NameField(ExtendedThinlet thinlet, String initialText, FormFieldDelegate delegate) {
		super(thinlet, InternationalisationUtils.getI18NString("medic.common.labels.name")+":", delegate);
		if(initialText != null){
			thinlet.setText(textBox, initialText);
		}
	}
	
	@Override
	public void validate() throws ValidationFailure{
		if(!(super.getStringResponse() !="" && super.getStringResponse() != null)){
			throw new ValidationFailure("\""+ getLabel().replace(":", "")+ "\" is not filled out. Please enter the desired name.");
		}
	}
	
	public void setFieldForPerson(Person p) {
		p.setName(getRawResponse());
	}

}
