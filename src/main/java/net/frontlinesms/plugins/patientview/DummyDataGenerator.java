package net.frontlinesms.plugins.patientview;

import java.security.GeneralSecurityException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

import net.frontlinesms.plugins.forms.data.repository.FormDao;
import net.frontlinesms.plugins.patientview.data.domain.people.User;
import net.frontlinesms.plugins.patientview.data.domain.people.Person.Gender;
import net.frontlinesms.plugins.patientview.data.domain.people.User.Role;
import net.frontlinesms.plugins.patientview.data.repository.CommunityHealthWorkerDao;
import net.frontlinesms.plugins.patientview.data.repository.MedicFormDao;
import net.frontlinesms.plugins.patientview.data.repository.MedicMessageResponseDao;
import net.frontlinesms.plugins.patientview.data.repository.PatientDao;
import net.frontlinesms.plugins.patientview.data.repository.UserDao;
import net.frontlinesms.ui.ThinletUiEventHandler;
import net.frontlinesms.ui.UiGeneratorController;
import net.frontlinesms.ui.i18n.InternationalisationUtils;

import org.springframework.context.ApplicationContext;

import thinlet.Thinlet;

public class DummyDataGenerator implements ThinletUiEventHandler {
	
	private static DateFormat df = InternationalisationUtils.getDateFormat();
	
	private boolean isGenerating=false;
	
	private ApplicationContext applicationContext;
	/** Message Response Dao for saving message responses when they come in**/
	MedicMessageResponseDao messageResponseDao;
	/**Community Health worker dao for fetching CHWs**/
	CommunityHealthWorkerDao chwDao;
	PatientDao patientDao;
	FormDao vanillaFormDao;
	MedicFormDao formDao;
	UserDao userDao ;
	
	/**the main panel of the box that is used to display progress about creating dummy data **/
	private Object mainPanel;
	/** the text area inside the main panel**/
	private Object textArea;
	/** local extendedthinlet instance for working on the mainPanel**/
	private UiGeneratorController thinlet;
	/** list of current messages on the panel**/
	private ArrayList<String> text;
	
	private PatientViewPluginController pluginController;
	
	public DummyDataGenerator(PatientViewPluginController pluginController, UiGeneratorController uiController){
		this.pluginController = pluginController;
		this.applicationContext = pluginController.getApplicationContext();
		this.thinlet = uiController;
		messageResponseDao = (MedicMessageResponseDao) applicationContext.getBean("MedicMessageResponseDao");
		chwDao = (CommunityHealthWorkerDao) applicationContext.getBean("CHWDao");
		patientDao = (PatientDao) applicationContext.getBean("PatientDao");
		vanillaFormDao = (FormDao) applicationContext.getBean("formDao");
		formDao =(MedicFormDao) applicationContext.getBean("MedicFormDao");
		userDao = (UserDao) applicationContext.getBean("UserDao");
		setMainPanel(thinlet.loadComponentFromFile("/ui/plugins/patientview/dummyDataPanel.xml", this));
	}
	/**
	 * Outputs a string to the message log. Used to show the user the progress of creating dummy daya
	 * @param s String to be logged
	 */
	public synchronized void log(String s){
		if(text ==null){
			text = new ArrayList<String>();
		}
		text.add(s);
		String result = "";
		if(text.size() >13){
			for(int i = 13; i>0; i--){
				result += text.get(text.size()-i) + "\n";
			}
		}else{
			for(String se:text){
				result += se + "\n";
			}
		}
		thinlet.setText(textArea, result);
	}
	
	public void skip(){
		createDummyData(true);
	}
	
	public void create(){
		createDummyData(false);
	}

	/**
	 * Creates dummy data and writes it to the database.
	 */
	@SuppressWarnings("serial")
	public void createDummyData(boolean skip) {
		pluginController.stopListening();
		boolean createOneUser = thinlet.isSelected(thinlet.find(getMainPanel(),"rootToorBox"));
		if(createOneUser){
			User user0 = null;
			try {
				user0 = new User("Root User",Gender.FEMALE, new Date(),"root",Role.ADMIN, "toor");
			} catch (GeneralSecurityException e) {
				e.printStackTrace();
			}
			userDao.saveUser(user0);
		}else{
			User user = null;
			User user2 = null;
			User user3 = null;
			User user4 = null;
			try {
				user = new User("Alex Harsha",Gender.FEMALE, new Date(),"aHarsha",Role.ADMIN,"medic");
				user2 = new User("Aisha Moniba",Gender.FEMALE, new Date(),"aMoniba",Role.READWRITE,"medic");
				user3 = new User("Daniel Kayiwa",Gender.MALE, new Date(),"dKayiwa",Role.READ,"medic");
				user4 = new User("Dieterich Lawson", Gender.MALE, new Date(),"dLawson",Role.REGISTRAR,"medic");
			} catch (GeneralSecurityException e) {
				e.printStackTrace();
			}
			userDao.saveUser(user);
			userDao.saveUser(user2);
			userDao.saveUser(user3);
			userDao.saveUser(user4);
		}
		
		if(skip){
			setGenerating(false);
			return;
		}
		
		int chwNum = Integer.parseInt(thinlet.getText(thinlet.find(getMainPanel(), "chwField")));
		int perCHWPatientNum = Integer.parseInt(thinlet.getText(thinlet.find(getMainPanel(), "patientField")));
		boolean createForms = thinlet.isSelected(thinlet.find(getMainPanel(),"createFormsField"));
		int formResponseNum = Integer.parseInt(thinlet.getText(thinlet.find(getMainPanel(), "formResponsesField")));
		int smsMessagesNum = Integer.parseInt(thinlet.getText(thinlet.find(getMainPanel(), "smsMessagesField")));
		
		thinlet.removeAll(thinlet.find(mainPanel,"dummyPanel"));
		textArea = thinlet.create(Thinlet.TEXTAREA);
		thinlet.setWeight(textArea,5,1);
		thinlet.setWeight(thinlet.find(mainPanel,"dummyPanel"), 1, 2);
		thinlet.add(thinlet.find(mainPanel,"dummyPanel"),textArea);
		DataGeneratorThread dgt = new DataGeneratorThread(chwNum, perCHWPatientNum, createForms, formResponseNum, smsMessagesNum, applicationContext, this);
		dgt.start();
	}
		
	private void setGenerating(boolean isGenerating) {
		this.isGenerating = isGenerating;
	}
	public boolean isGenerating() {
		return isGenerating;
	}
	public void setMainPanel(Object mainPanel) {
		this.mainPanel = mainPanel;
	}
	public Object getMainPanel() {
		return mainPanel;
	}
	
	public void startListening(){
		pluginController.startListening();
	}
	
	public void creationDone(){
		pluginController.getTabController().dummyDataDone();
	}
}
