package net.frontlinesms.plugins.patientview.ui.administration.people;

import java.util.ArrayList;
import java.util.List;

import net.frontlinesms.plugins.patientview.data.domain.people.Patient;
import net.frontlinesms.plugins.patientview.data.domain.people.Person;
import net.frontlinesms.plugins.patientview.data.repository.PatientDao;
import net.frontlinesms.plugins.patientview.ui.personpanel.PatientPanel;
import net.frontlinesms.plugins.patientview.ui.personpanel.PersonPanel;
import net.frontlinesms.ui.UiGeneratorController;

import org.springframework.context.ApplicationContext;

public class PatientAdministrationPanelController extends PersonAdministrationPanelController<Patient> {

	private PatientDao patientDao;
	
	public PatientAdministrationPanelController(UiGeneratorController uiController, ApplicationContext appCon) {
		super(uiController, appCon);	
	}

	@Override
	protected List<Patient> getPeopleForString(String s) {
		if(patientDao == null){
			patientDao = (PatientDao) appCon.getBean("PatientDao");
		}
		return new ArrayList<Patient>(patientDao.getPatientsByNameWithLimit(s, 30));
	}

	@Override
	protected String getPersonType() {
		return "Patient";
	}

	@Override
	protected void putHeader() {
		advancedTableController.putHeader(Patient.class, new String[]{"Name","Age","CHW"}, new String[]{"getName", "getStringAge", "getCHWName"});
	}

	public String getListItemTitle() {
		return "Manage Patients";
	}

	@Override
	protected PersonPanel getPersonPanelForPerson(Person person) {
		return new PatientPanel(uiController,appCon,(Patient) person);
	}

}
