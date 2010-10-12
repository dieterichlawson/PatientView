package net.frontlinesms.plugins.patientview.ui.administration.tabs;

import static net.frontlinesms.ui.i18n.InternationalisationUtils.getI18NString;

import java.util.ArrayList;
import java.util.List;

import net.frontlinesms.plugins.patientview.data.domain.people.Patient;
import net.frontlinesms.plugins.patientview.data.domain.people.Person;
import net.frontlinesms.plugins.patientview.data.repository.PatientDao;
import net.frontlinesms.plugins.patientview.security.UserSessionManager;
import net.frontlinesms.plugins.patientview.ui.administration.DeleteDialogController;
import net.frontlinesms.plugins.patientview.ui.administration.DeleteDialogDelegate;
import net.frontlinesms.plugins.patientview.ui.advancedtable.HeaderColumn;
import net.frontlinesms.plugins.patientview.ui.personpanel.PatientPanel;
import net.frontlinesms.plugins.patientview.ui.personpanel.PersonPanel;
import net.frontlinesms.ui.UiGeneratorController;

import org.springframework.context.ApplicationContext;

public class PatientAdministrationPanelController extends PersonAdministrationPanelController<Patient> implements DeleteDialogDelegate{

	private PatientDao patientDao;
	
	private static final String ADD_ICON = "/icons/patient_add_male.png";
	private static final String EDIT_ICON = "/icons/patient_edit_male.png";
	private static final String DELETE_ICON = "/icons/patient_delete_male.png";
	
	public PatientAdministrationPanelController(UiGeneratorController uiController, ApplicationContext appCon) {
		super(uiController, appCon);
		patientDao = (PatientDao) appCon.getBean("PatientDao");
		search("");
	}

	@Override
	protected List<Patient> getPeopleForString(String s) {
		return new ArrayList<Patient>(patientDao.findPatientsByName(s, 30));
	}

	@Override
	protected String getPersonType() {
		return getI18NString("medic.common.patient");
	}

	@Override
	protected void putHeader() {
		advancedTableController.putHeader(Patient.class, HeaderColumn.createColumnList(new String[]{getI18NString("medic.common.labels.name"), getI18NString("medic.common.labels.id"), getI18NString("medic.common.chw")},
				 new String[]{"/icons/user.png", "/icons/key.png","/icons/user_phone.png"},
				 new String[]{"getName", "getStringID","getCHWName"}));	
	}

	public String getListItemTitle() {
		return getI18NString("admin.actionlist.manage.patients");
	}

	@Override
	protected PersonPanel getPersonPanelForPerson(Person person) {
		return new PatientPanel(uiController,appCon,(Patient) person);
	}

	public String getIconPath() {
		return "/icons/patients_large.png";
	}
	
	@Override
	protected String[] getIcons() {
		String[] icons = new String[3];
		icons[ADD_INDEX] = ADD_ICON;
		icons[EDIT_INDEX] = EDIT_ICON;
		icons[REMOVE_INDEX] = DELETE_ICON;
		return icons;
	}

	@Override
	protected Class<Patient> getPersonClass() {
		return Patient.class;
	}
	
	public void viewWillAppear() {}
	
	public void removeButtonClicked(){
		DeleteDialogController d = new DeleteDialogController(uiController,this,"Patient");
	}

	public void dialogReturned(Boolean delete, Boolean keepVisible, String reason) {
		if(delete){
			super.currentPersonPanel.getPerson().setRemoved(true, keepVisible,UserSessionManager.getUserSessionManager().getCurrentUser(), reason);
			patientDao.updatePatient(super.currentPersonPanel.getPerson());
		}
		super.advancedTableController.updateTable();
	}

}
