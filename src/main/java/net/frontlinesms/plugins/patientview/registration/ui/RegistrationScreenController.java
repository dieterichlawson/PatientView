package net.frontlinesms.plugins.patientview.registration.ui;

import static net.frontlinesms.ui.i18n.InternationalisationUtils.getI18NString;

import java.util.ArrayList;

import net.frontlinesms.plugins.patientview.data.domain.people.Patient;
import net.frontlinesms.plugins.patientview.data.repository.PatientDao;
import net.frontlinesms.plugins.patientview.security.UserSessionManager;
import net.frontlinesms.plugins.patientview.ui.PatientViewThinletTabController;
import net.frontlinesms.plugins.patientview.ui.advancedtable.AdvancedTableActionDelegate;
import net.frontlinesms.plugins.patientview.ui.advancedtable.AdvancedTableController;
import net.frontlinesms.plugins.patientview.ui.advancedtable.HeaderColumn;
import net.frontlinesms.plugins.patientview.ui.personpanel.PatientPanel;
import net.frontlinesms.plugins.patientview.ui.personpanel.PersonPanelDelegate;
import net.frontlinesms.ui.ThinletUiEventHandler;
import net.frontlinesms.ui.UiGeneratorController;

import org.springframework.context.ApplicationContext;

import thinlet.Thinlet;
public class RegistrationScreenController implements ThinletUiEventHandler, AdvancedTableActionDelegate, PersonPanelDelegate {

	private UiGeneratorController uiController;
	private ApplicationContext appCon;
	
	private PatientViewThinletTabController parent;
	
	private Object mainPanel;
	
	private AdvancedTableController patientTable;
	
	private static final String UI_FILE_XML = "/ui/plugins/patientview/registrationTab.xml";
	
	private PatientDao patientDao;
	
	public RegistrationScreenController(UiGeneratorController uiController, ApplicationContext appCon, PatientViewThinletTabController parent){
		this.uiController = uiController;
		this.appCon = appCon;
		this.parent = parent;
		init();
	}
	
	private void init(){
		mainPanel = uiController.loadComponentFromFile(UI_FILE_XML, this);
		patientDao = (PatientDao) appCon.getBean("PatientDao");
		patientTable = new AdvancedTableController(this,uiController,uiController.find(mainPanel,"resultsTable"));
		patientTable.putHeader(Patient.class, HeaderColumn.createColumnList(new String[]{getI18NString("medic.common.labels.name"), getI18NString("thinletformfields.birthdate"), getI18NString("medic.common.labels.id"),getI18NString("medic.common.chw")},
				 new String[]{"/icons/user.png", "/icons/cake.png", "/icons/key.png",""},
				 new String[]{"getName", "getStringBirthdate", "getStringID","getCHWName"}));
		PatientPanel panel = new PatientPanel(uiController,appCon,this);
		uiController.add(uiController.find(mainPanel,"bottomPanel"),panel.getMainPanel());
		searchKeyPressed();
		uiController.setText(uiController.find(mainPanel,"loginLabel"), getI18NString("login.logged.in.as")+" "+ UserSessionManager.getUserSessionManager().getCurrentUser().getName());
	}
	
	public void searchKeyPressed(){
		ArrayList<Patient> results = new ArrayList<Patient>();
		results.addAll(patientDao.findPatientsByName(uiController.getText(uiController.find(mainPanel,"searchBox")),15));
		patientTable.setResults(results);
		patientTable.setSelected(0);
	}

	public void doubleClickAction(Object selectedObject) {
		// TODO Auto-generated method stub
		
	}

	public void resultsChanged() {
		// TODO Auto-generated method stub
		
	}

	public void selectionChanged(Object selectedObject) {
		uiController.removeAll(uiController.find(mainPanel,"topPanel"));
		PatientPanel p =new PatientPanel(uiController, appCon,(Patient) selectedObject);
		p.setPanelTitle("");
		uiController.add(uiController.find(mainPanel,"topPanel"), p.getMainPanel());
	}
	
	public void logout(){
		parent.logout();
	}
	
	public Object getMainPanel(){
		return mainPanel;
	}

	public void didCreatePerson() {
		Object button = uiController.createButton(getI18NString("registration.create.another.patient"));
		uiController.setAction(button, "createAnotherPatient()", null, this);
		uiController.setWeight(button, 1, 0);
		uiController.setHAlign(button,Thinlet.CENTER);
		Object label = uiController.createLabel(getI18NString("registration.patient.created.successfully"));
		uiController.setWeight(label, 1, 0);
		uiController.setHAlign(label,Thinlet.CENTER);
		uiController.add(uiController.find(mainPanel,"bottomPanel"),label);
		uiController.add(uiController.find(mainPanel,"bottomPanel"),button);	
	}
	
	public void createAnotherPatient(){
		uiController.removeAll(uiController.find(mainPanel,"bottomPanel"));
		uiController.add(uiController.find(mainPanel,"bottomPanel"),new PatientPanel(uiController,appCon,this).getMainPanel());
	}
}
