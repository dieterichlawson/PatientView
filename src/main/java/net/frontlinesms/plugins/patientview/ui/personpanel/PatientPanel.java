package net.frontlinesms.plugins.patientview.ui.personpanel;

import static net.frontlinesms.ui.i18n.InternationalisationUtils.getI18NString;
import net.frontlinesms.plugins.patientview.data.domain.people.Patient;
import net.frontlinesms.plugins.patientview.data.repository.PatientDao;
import net.frontlinesms.plugins.patientview.ui.helpers.thinletformfields.personalformfields.CHWComboBox;
import net.frontlinesms.ui.UiGeneratorController;
import net.frontlinesms.ui.i18n.InternationalisationUtils;

import org.springframework.context.ApplicationContext;

public class PatientPanel extends PersonPanel<Patient> {

	private static final String PATIENT_AAG ="personpanel.labels.patient.at.a.glance";
	private static final String EDIT_PATIENT= "personpanel.labels.edit.patient";
	private static final String ADD_PATIENT = "personpanel.labels.add.a.patient";
	private static final String CHW_FIELD = "medic.common.chw";
	private static final String DEMO_CHW = "editdetailview.demo.chw";
	private PatientDao patientDao;
	
	private boolean showingCHWPanel;
	private CommunityHealthWorkerPanel chwPanel;
	
	private String defaultTitle;
	/**
	 * Creates a PatientPanel used for creating new Patients.
	 * Uses a delegate
	 * @param uiController
	 * @param appCon
	 */
	public PatientPanel(UiGeneratorController uiController, ApplicationContext appCon, PersonPanelDelegate delegate) {
		super(uiController,appCon, delegate);
		patientDao = (PatientDao) appCon.getBean("PatientDao");
	}
	
	/**
	 * Creates a PatientPanel used for creating new Patients
	 * @param uiController
	 * @param appCon
	 */
	public PatientPanel(UiGeneratorController uiController, ApplicationContext appCon) {
		super(uiController,appCon);
		patientDao = (PatientDao) appCon.getBean("PatientDao");
	}
	
	/**
	 * Creates a PatientPanel for Patient p
	 * @param uiController
	 * @param appCon
	 * @param p
	 */
	public PatientPanel(UiGeneratorController uiController, ApplicationContext appCon, Patient p) {
		super(uiController, appCon,p);
		patientDao = (PatientDao) appCon.getBean("PatientDao");
	}

	/**
	 * Adds a Community Health Worker combo box to the editable person fields
	 * @see net.frontlinesms.plugins.patientview.ui.personpanel.PersonPanel#addAdditionalEditableFields()
	 */
	@Override
	protected void addAdditionalEditableFields() {
		CHWComboBox chwCombo = new CHWComboBox(uiController,appCon,isNewPersonPanel?null:getPerson().getChw(),null);
		uiController.add(getLabelPanel(),chwCombo.getThinletPanel());
	}

	/** adds a CHW field to the person info
	 * @see net.frontlinesms.plugins.patientview.ui.personpanel.PersonPanel#addAdditionalFields(java.lang.Object)
	 */
	@Override
	protected void addAdditionalFields() {
		Object panel = uiController.createPanel("");
		uiController.setGap(panel, 5);
		uiController.add(panel,uiController.createLabel(InternationalisationUtils.getI18NString("medic.common.chw")+":"));
		Object button = uiController.createButton(getPerson().getCHWName());
		uiController.setChoice(button, "type", "link");
		uiController.setAction(button, "showCHWPanel", null, this);
		uiController.add(panel,button);
		uiController.add(super.getLabelPanel(),panel);
	}

	/**
	 * @see net.frontlinesms.plugins.patientview.ui.personpanel.PersonPanel#createPerson()
	 */
	@Override
	protected Patient createPerson() {
		return new Patient();
	}

	/**
	 * @see net.frontlinesms.plugins.patientview.ui.personpanel.PersonPanel#getDefaultTitle()
	 */
	@Override
	protected String getDefaultTitle() {
		return defaultTitle==null?InternationalisationUtils.getI18NString(PATIENT_AAG):defaultTitle;
	}

	/**
	 * @see net.frontlinesms.plugins.patientview.ui.personpanel.PersonPanel#getEditingTitle()
	 */
	@Override
	protected String getEditingTitle() {
		return InternationalisationUtils.getI18NString(EDIT_PATIENT);
	}

	/**
	 * @see net.frontlinesms.plugins.patientview.ui.personpanel.PersonPanel#savePerson()
	 */
	@Override
	protected void savePerson() {
		patientDao.savePatient(getPerson());
	}
	
	/**
	 * @see net.frontlinesms.plugins.patientview.ui.personpanel.PersonPanel#updatePerson()
	 */
	@Override
	protected void updatePerson() {
		patientDao.updatePatient(getPerson());
	}

	@Override
	protected String getAddingTitle() {
		return InternationalisationUtils.getI18NString(ADD_PATIENT);
	}

	@Override
	protected void addAdditionalDemoFields() {
		addLabelToLabelPanel(getI18NString(CHW_FIELD) + ": " + getI18NString(DEMO_CHW));
	}

	public void setTitle(String title){
		defaultTitle=title;
	}
	
	public void showCHWPanel(){
		if(!showingCHWPanel){
			chwPanel= new CommunityHealthWorkerPanel(uiController, appCon, getPerson().getChw()); 
			uiController.add(super.getMainPanel(),chwPanel.getMainPanel());
			showingCHWPanel =true;
		}else{
			uiController.remove(chwPanel.getMainPanel());
			showingCHWPanel=false;
		}
	}
}
