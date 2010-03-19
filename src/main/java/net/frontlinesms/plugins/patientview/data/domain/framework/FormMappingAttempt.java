package net.frontlinesms.plugins.patientview.data.domain.framework;

import net.frontlinesms.plugins.forms.data.domain.FormResponse;
import net.frontlinesms.plugins.patientview.data.domain.response.MedicFormResponse;

public class FormMappingAttempt {

	private FormResponse submittedForm;
	
	private MedicFormResponse mappedForm;
	
	public boolean isMapped(){
		return mappedForm != null;
	}
}
