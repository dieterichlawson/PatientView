package net.frontlinesms.plugins.patientview.ui.administration;

import java.util.ArrayList;
import java.util.List;

import net.frontlinesms.plugins.patientview.data.domain.people.Patient;
import net.frontlinesms.plugins.patientview.data.domain.people.Person;
import net.frontlinesms.plugins.patientview.data.repository.PatientDao;
import net.frontlinesms.plugins.patientview.ui.personpanel.PatientPanel;
import net.frontlinesms.plugins.patientview.ui.personpanel.PersonPanel;
import net.frontlinesms.ui.UiGeneratorController;

import org.springframework.context.ApplicationContext;
import static net.frontlinesms.ui.i18n.InternationalisationUtils.*;

public class PatientAdministrationPanelController extends PersonAdministrationPanelController<Patient> {

	private PatientDao patientDao;
	
	public PatientAdministrationPanelController(UiGeneratorController uiController, ApplicationContext appCon) {
		super(uiController, appCon);
		patientDao = (PatientDao) appCon.getBean("PatientDao");
		search("");
	}

	@Override
	protected List<Patient> getPeopleForString(String s) {
		return new ArrayList<Patient>(patientDao.getPatientsByNameWithLimit(s, 30));
	}

	@Override
	protected String getPersonType() {
		return getI18NString("medic.common.patient");
	}

	@Override
	protected void putHeader() {
		advancedTableController.putHeader(Patient.class, new String[]{getI18NString("medic.common.labels.name"),getI18NString("medic.common.labels.age"),getI18NString("medic.common.chw")}, new String[]{"getName", "getStringAge", "getCHWName"});
	}

	public String getListItemTitle() {
		return getI18NString("admin.actionlist.manage.patients");
	}

	@Override
	protected PersonPanel getPersonPanelForPerson(Person person) {
		return new PatientPanel(uiController,appCon,(Patient) person);
	}

}
