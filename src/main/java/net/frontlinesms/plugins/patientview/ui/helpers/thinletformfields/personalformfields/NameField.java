package net.frontlinesms.plugins.patientview.ui.helpers.thinletformfields.personalformfields;

import net.frontlinesms.plugins.patientview.data.domain.people.Person;
import net.frontlinesms.plugins.patientview.ui.helpers.thinletformfields.FormFieldDelegate;
import net.frontlinesms.plugins.patientview.ui.helpers.thinletformfields.TextField;
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
	public boolean isValid(){
		return super.getStringResponse() !="" && super.getStringResponse() != null;
	}

	public void setFieldForPerson(Person p) {
		p.setName(getRawResponse());
	}

}
