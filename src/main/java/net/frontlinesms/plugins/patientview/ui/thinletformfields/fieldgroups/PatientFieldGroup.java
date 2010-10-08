package net.frontlinesms.plugins.patientview.ui.thinletformfields.fieldgroups;

import net.frontlinesms.plugins.patientview.data.domain.people.Patient;
import net.frontlinesms.plugins.patientview.data.repository.PatientDao;
import net.frontlinesms.plugins.patientview.ui.thinletformfields.FormFieldDelegate;
import net.frontlinesms.plugins.patientview.ui.thinletformfields.personalformfields.CHWComboBox;
import net.frontlinesms.ui.UiGeneratorController;

import org.springframework.context.ApplicationContext;

public class PatientFieldGroup extends PersonFieldGroup<Patient> {

	private PatientDao patientDao;
	
	public PatientFieldGroup(UiGeneratorController ui, ApplicationContext appCon, FormFieldDelegate delegate, Patient person) {
		super(ui, appCon, delegate, person);
		this.patientDao = (PatientDao) appCon.getBean("PatientDao");
	}

	@Override
	protected void addAdditionalFields() {
		CHWComboBox chwCombo = new CHWComboBox(ui, appCon, person == null?null:person.getChw(),null);
		super.addField(chwCombo);
	}
	
	@Override
	protected void saveOrUpdatePerson() {
		if(isNewPersonGroup){
			patientDao.savePatient(person);
		}else{
			patientDao.updatePatient(person);
		}
	}

	@Override
	protected Patient createNewPerson() {
		return new Patient();
	}
}
