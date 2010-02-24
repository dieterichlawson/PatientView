package net.frontlinesms.plugins.patientview;

import java.awt.event.WindowEvent;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import net.frontlinesms.FrontlineSMS;
import net.frontlinesms.data.domain.Message;
import net.frontlinesms.data.repository.MessageDao;
import net.frontlinesms.events.EventObserver;
import net.frontlinesms.events.FrontlineEvent;
import net.frontlinesms.events.impl.DidSaveNotification;
import net.frontlinesms.listener.IncomingMessageListener;
import net.frontlinesms.plugins.BasePluginController;
import net.frontlinesms.plugins.PluginControllerProperties;
import net.frontlinesms.plugins.PluginInitialisationException;
import net.frontlinesms.plugins.forms.data.domain.Form;
import net.frontlinesms.plugins.forms.data.domain.FormField;
import net.frontlinesms.plugins.forms.data.domain.FormFieldType;
import net.frontlinesms.plugins.forms.data.domain.FormResponse;
import net.frontlinesms.plugins.forms.data.domain.ResponseValue;
import net.frontlinesms.plugins.forms.data.repository.FormDao;
import net.frontlinesms.plugins.patientview.data.domain.framework.DataType;
import net.frontlinesms.plugins.patientview.data.domain.framework.MedicForm;
import net.frontlinesms.plugins.patientview.data.domain.framework.MedicFormField;
import net.frontlinesms.plugins.patientview.data.domain.framework.MedicFormField.PatientAttributeMapping;
import net.frontlinesms.plugins.patientview.data.domain.people.CommunityHealthWorker;
import net.frontlinesms.plugins.patientview.data.domain.people.Patient;
import net.frontlinesms.plugins.patientview.data.domain.people.User;
import net.frontlinesms.plugins.patientview.data.domain.people.Person.Gender;
import net.frontlinesms.plugins.patientview.data.domain.people.User.Role;
import net.frontlinesms.plugins.patientview.data.domain.response.MedicFieldResponse;
import net.frontlinesms.plugins.patientview.data.domain.response.MedicFormResponse;
import net.frontlinesms.plugins.patientview.data.domain.response.MedicMessageResponse;
import net.frontlinesms.plugins.patientview.data.repository.CommunityHealthWorkerDao;
import net.frontlinesms.plugins.patientview.data.repository.MedicFormDao;
import net.frontlinesms.plugins.patientview.data.repository.PatientDao;
import net.frontlinesms.plugins.patientview.data.repository.hibernate.HibernateMedicFormDao;
import net.frontlinesms.plugins.patientview.data.repository.hibernate.HibernateMedicFormResponseDao;
import net.frontlinesms.plugins.patientview.data.repository.hibernate.HibernateMedicMessageResponseDao;
import net.frontlinesms.plugins.patientview.data.repository.hibernate.HibernateUserDao;
import net.frontlinesms.plugins.patientview.history.HistoryManager;
import net.frontlinesms.plugins.patientview.ui.PatientViewThinletTabController;
import net.frontlinesms.plugins.patientview.userlogin.UserSessionManager;
import net.frontlinesms.ui.ExtendedThinlet;
import net.frontlinesms.ui.UiGeneratorController;

import org.springframework.context.ApplicationContext;

import thinlet.FrameLauncher;
import thinlet.Thinlet;
import uk.ac.shef.wit.simmetrics.similaritymetrics.JaroWinkler;
import uk.ac.shef.wit.simmetrics.similaritymetrics.Levenshtein;

@PluginControllerProperties(name="Patient View", iconPath="/icons/big_medic.png",
		springConfigLocation="classpath:net/frontlinesms/plugins/patientview/patientview-spring-hibernate.xml",
		hibernateConfigPath="classpath:net/frontlinesms/plugins/patientview/patientview.hibernate.cfg.xml")
public class PatientViewPluginController extends BasePluginController implements IncomingMessageListener, EventObserver  {

	/** the {@link FrontlineSMS} instance that this plugin is attached to */
	private FrontlineSMS frontlineController;
	
	/** The application context used for fetching daos and other spring beans**/
	private ApplicationContext applicationContext;
	
	/** Message Response Dao for saving message responses when they come in**/
	HibernateMedicMessageResponseDao messageResponseDao;
	/**Community Health worker dao for fetching CHWs**/
	CommunityHealthWorkerDao chwDao;
	PatientDao patientDao;
	FormDao vanillaFormDao;
	HibernateMedicFormDao formDao;
	
	/**the main panel of the box that is used to display progress about creating dummy data **/
	Object mainPanel;
	/** the text area inside the main panel**/
	Object textArea;
	/** local extendedthinlet instance for working on the mainPanel**/
	ExtendedThinlet thinlet;
	/** list of current messages on the panel**/
	ArrayList<String> text;


	Levenshtein levenshtein;
	JaroWinkler jaroWinkler;
	
	/** 
	 * @see net.frontlinesms.plugins.BasePluginController#initThinletTab(net.frontlinesms.ui.UiGeneratorController)
	 */
	@Override
	protected Object initThinletTab(UiGeneratorController uiController) {
		PatientViewThinletTabController controller= new PatientViewThinletTabController(this,uiController);
		return controller.getTab();
	}

	/**
	 * @see net.frontlinesms.plugins.PluginController#deinit()
	 */
	public void deinit() {
		frontlineController.removeIncomingMessageListener(this);
	}

	/** @return {@link #frontlineController} */
	public FrontlineSMS getFrontlineController() {
		return this.frontlineController;
	}

	/**
	 * @return the applicationContext
	 */
	public ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	/** 
	 * @see net.frontlinesms.plugins.PluginController#init(net.frontlinesms.FrontlineSMS, org.springframework.context.ApplicationContext)
	 */
	public void init(FrontlineSMS frontlineController, ApplicationContext applicationContext) throws PluginInitialisationException {
		this.frontlineController = frontlineController;
		frontlineController.addIncomingMessageListener(this);
		this.applicationContext = applicationContext;
		UserSessionManager.getUserSessionManager().init(applicationContext);
		HistoryManager.getHistoryManager().init(applicationContext);
		messageResponseDao = (HibernateMedicMessageResponseDao) applicationContext.getBean("MedicMessageResponseDao");
		chwDao = (CommunityHealthWorkerDao) applicationContext.getBean("CHWDao");
		patientDao = (PatientDao) applicationContext.getBean("PatientDao");
		vanillaFormDao = (FormDao) applicationContext.getBean("formDao");
		formDao =(HibernateMedicFormDao) applicationContext.getBean("MedicFormDao");
		
		try {
			testing();
		} catch (Throwable t) {
			log.warn("Unable to load Medic plugin", t);
			throw new PluginInitialisationException(t);
		}	
		testFormHandling();
	}

	/**
	 * Outputs a string to the message log. Used to show the user the progress of creating dummy daya
	 * @param s String to be logged
	 */
	private void log(String s){
		if(text ==null){
			text = new ArrayList<String>();
		}
		text.add(s);
		String result = "";
		if(text.size() >30){
			for(int i = 30; i>0; i--){
				result += text.get(text.size()-i) + "\n";
			}
		}else{
			for(String se:text){
				result += se + "\n";
			}
		}
		thinlet.setText(textArea, result);
	}

	/**
	 * Creates dummy data and writes it to the database
	 */
	private void testing() {
		//initialize chw dao
		if(chwDao.getAllCommunityHealthWorkers().size() !=0){
			return;
		}
		MessageDao messageDao = (MessageDao) applicationContext.getBean("messageDao");
		HibernateMedicFormResponseDao formResponseDao =(HibernateMedicFormResponseDao) applicationContext.getBean("MedicFormResponseDao");
		thinlet = new ExtendedThinlet();
		mainPanel = thinlet.createPanel("mainPanel");
		textArea = thinlet.create("textarea");
		thinlet.setWeight(mainPanel, 1, 1);
		thinlet.setWeight(textArea, 1, 1);
		thinlet.add(mainPanel,textArea);
		thinlet.add(Thinlet.create("panel"));
		thinlet.add(mainPanel);
		//you have to use a special framelauncher class because otherwise it will close all open windows
		FrameLauncher framel = new FrameLauncher("Creating Dummy Data",thinlet,500,500,null)
		{ public void windowClosing(WindowEvent e){  dispose(); }}; 
		
		log("Creating dummy data..");
		// list of first names
		String[] firsts = { "Dieterich", "Dolores", "Freddy", "Alex",
				"Charlie", "Lindsay", "Winnie", "Terrence", "Wilson", "Jenny",
				"Meghan", "Katherine", "Poe", "Phillip", "Andrew", "Elizabeth",
				"Whitney", "Frank", "Jared", "Pope", "Wylie","Theodore", "Margot",
				"Forscythe","Lars","Sarah","Teddy","Fitz","Humphrey","James","Mark","Jesse" };
		// list of last names
		String[] lasts = { "Lawson", "Threadbare", "Evermore", "Brown",
				"Tender", "Taraban", "Polombo", "Pekkerwood", "Trought",
				"Finkley", "Coriander", "Groesbeck", "Trounce", "Longbottom",
				"Yip", "Fiars", "Trunch", "Whelp", "Schy", "Munificent",
				"Coyote","Brown","Black","Ames","Chavez","Richards","Swanson","Ballard"
				,"Roosevelt","Jackson","Trueblood","Wachowsky","Corleogne" };
		
		ArrayList<CommunityHealthWorker> chws = new ArrayList<CommunityHealthWorker>();
		ArrayList<Patient> ps = new ArrayList<Patient>();
		// create 80 chws with 100 patients each
		for (int i = 0; i < 35; i++) {
			String name = firsts[rand.nextInt(firsts.length)] + " "
					+ lasts[rand.nextInt(lasts.length)];
			Gender gender = (rand.nextBoolean()) ? Gender.MALE:Gender.FEMALE;
			CommunityHealthWorker chw = new CommunityHealthWorker(name, getRandomNumber(),gender,getRandomDate());
			chws.add(chw);
			ArrayList<Patient> patients = new ArrayList<Patient>();
			//create 100 patients for each chw
			for (int j = 0; j < 35; j++) {
				Gender gender2 = (rand.nextBoolean()) ? Gender.MALE:Gender.FEMALE;
				String name2 = firsts[rand.nextInt(firsts.length)] + " " + lasts[rand.nextInt(lasts.length)];
				Patient p = new Patient(chw, name2, gender2, getRandomDate());
				patients.add(p);
				ps.add(p);
			}
			try {
				//save the chw
				chwDao.saveCommunityHealthWorker(chw);
				//save all the patients
				for (Patient pe : patients) {
					patientDao.savePatient(pe);
				}
				log("CHW saved " + i);
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}

		ArrayList<MedicForm> forms = new ArrayList<MedicForm>();
		// create new Patient Entry form
		MedicForm f1 = new MedicForm("Patient Entry");
		f1.addField(new MedicFormField(f1, DataType.TEXT_FIELD, "Patient Name", PatientAttributeMapping.NAMEFIELD));
		f1.addField(new MedicFormField(f1, DataType.DATE_FIELD, "Patient Birthdate", PatientAttributeMapping.BIRTHDATEFIELD));
		f1.addField(new MedicFormField(f1, DataType.TEXT_FIELD, "Patient Gender"));
		f1.addField(new MedicFormField(f1, DataType.TEXT_FIELD, "Patient Height"));
		f1.addField(new MedicFormField(f1, DataType.TRUEFALSE, "Patient HIV Status"));
		
		Form f1b = new Form("Patient Entry");
		f1b.addField(new FormField(FormFieldType.TEXT_FIELD, "Patient Name"));
		f1b.addField(new FormField(FormFieldType.DATE_FIELD, "Patient Birthdate"));
		f1b.addField(new FormField(FormFieldType.TEXT_FIELD, "Patient ID"));
		f1b.addField(new FormField(FormFieldType.TEXT_FIELD, "Patient Gender"));
		f1b.addField(new FormField(FormFieldType.TEXT_FIELD, "Patient Height"));
		f1b.addField(new FormField(FormFieldType.CHECK_BOX, "Patient HIV Status"));
		f1.setForm(f1b);
		vanillaFormDao.saveForm(f1b);
		
		// create patient weight form
		MedicForm f2 = new MedicForm("Patient Weight");
		f2.addField(new MedicFormField(f2, DataType.TEXT_FIELD, "Patient Name"));
		f2.addField(new MedicFormField(f2, DataType.DATE_FIELD, "Patient Birthdate"));
		f2.addField(new MedicFormField(f2, DataType.NUMERIC_TEXT_FIELD, "Patient Weight"));
		// create patient death form
		MedicForm f3 = new MedicForm("Patient Death");
		f3.addField(new MedicFormField(f3, DataType.TEXT_FIELD, "Patient Name"));
		f3.addField(new MedicFormField(f3, DataType.DATE_FIELD, "Patient Birthdate"));
		f3.addField(new MedicFormField(f3, DataType.DATE_FIELD, "Date of Death"));
		f3.addField(new MedicFormField(f3, DataType.TEXT_AREA, "Probable Cause"));
		// create patient ARV adherence form
		MedicForm f4 = new MedicForm("ARV adherence");
		f4.addField(new MedicFormField(f4, DataType.TEXT_FIELD, "Patient Name"));
		f4.addField(new MedicFormField(f4, DataType.DATE_FIELD, "Patient Birthdate"));
		f4.addField(new MedicFormField(f4, DataType.CHECK_BOX, "ARVs taken?"));
		f4.addField(new MedicFormField(f4, DataType.TEXT_AREA, "Additional notes"));
		// create birth form
		MedicForm f5 = new MedicForm("Patient Birth");
		f5.addField(new MedicFormField(f5, DataType.TEXT_FIELD, "Patient Name"));
		f5.addField(new MedicFormField(f5, DataType.DATE_FIELD, "Patient Birthdate"));
		f5.addField(new MedicFormField(f5, DataType.CHECK_BOX, "Check if baby healthy"));
		f5.addField(new MedicFormField(f5, DataType.CHECK_BOX, "Check if mother healthy"));
		f5.addField(new MedicFormField(f5, DataType.TEXT_AREA, "Additional notes"));
		// create rash form
		MedicForm f6 = new MedicForm("Unknown Rash");
		f6.addField(new MedicFormField(f6, DataType.TEXT_FIELD, "Patient Name"));
		f6.addField(new MedicFormField(f6, DataType.DATE_FIELD, "Patient Birthdate"));
		f6.addField(new MedicFormField(f6, DataType.CHECK_BOX, "Head Rash?"));
		f6.addField(new MedicFormField(f6, DataType.CHECK_BOX, "Body Rash?"));
		f6.addField(new MedicFormField(f6, DataType.TEXT_FIELD,
				"Location of Rash"));
		f6.addField(new MedicFormField(f6, DataType.TEXT_AREA, "Other notes"));
		// create other form
		MedicForm f7 = new MedicForm("Diarrheal Disease");
		f7.addField(new MedicFormField(f7, DataType.TEXT_FIELD, "Patient Name"));
		f7.addField(new MedicFormField(f7, DataType.DATE_FIELD, "Patient Birthdate"));
		f7.addField(new MedicFormField(f7,DataType.TEXT_FIELD,"Severity"));
		f7.addField(new MedicFormField(f7,DataType.TEXT_FIELD,"Frequency"));
		f7.addField(new MedicFormField(f7, DataType.CHECK_BOX, "Is patient dehydrated?"));
		f7.addField(new MedicFormField(f7, DataType.CHECK_BOX, "Check if bloody"));
		f7.addField(new MedicFormField(f7, DataType.TEXT_AREA, "additional notes"));
		// create other form
		MedicForm f8 = new MedicForm("Joint Pain");
		f8.addField(new MedicFormField(f8, DataType.TEXT_FIELD, "Patient Name"));
		f8.addField(new MedicFormField(f8, DataType.DATE_FIELD, "Patient Birthdate"));
		f8.addField(new MedicFormField(f8, DataType.TEXT_FIELD, "Joints affected"));
		f8.addField(new MedicFormField(f8, DataType.TEXT_FIELD, "Type of Pain"));
		f8.addField(new MedicFormField(f8, DataType.TEXT_AREA, "Additional Notes"));
		// create other form
		MedicForm f9 = new MedicForm("Long Test Form");
		f9.addField(new MedicFormField(f9, DataType.TEXT_FIELD, "Patient Name"));
		f9.addField(new MedicFormField(f9, DataType.DATE_FIELD, "Patient Birthdate"));
		f9.addField(new MedicFormField(f9, DataType.TEXT_FIELD, "Field 1"));
		f9.addField(new MedicFormField(f9, DataType.CHECK_BOX, "more fun!"));
		f9.addField(new MedicFormField(f9, DataType.TEXT_FIELD, "Field 2"));
		f9.addField(new MedicFormField(f9, DataType.TEXT_FIELD, "Field 3"));
		f9.addField(new MedicFormField(f9, DataType.TEXT_FIELD, "Field 4"));
		f9.addField(new MedicFormField(f9, DataType.TEXT_FIELD, "Field 5"));
		f9.addField(new MedicFormField(f9, DataType.TEXT_FIELD, "Field 6"));
		f9.addField(new MedicFormField(f9, DataType.TEXT_FIELD, "Field 7"));
		f9.addField(new MedicFormField(f9, DataType.TEXT_FIELD, "Field 8"));

		// create other form
		MedicForm f0 = new MedicForm("CheckBox Long Test Form");
		f0.addField(new MedicFormField(f0, DataType.TEXT_FIELD, "Patient Name"));
		f0.addField(new MedicFormField(f0, DataType.DATE_FIELD, "Patient Birthdate"));
		f0.addField(new MedicFormField(f0, DataType.TEXT_FIELD, "Field 1"));
		f0.addField(new MedicFormField(f0, DataType.CHECK_BOX, "Field 2"));
		f0.addField(new MedicFormField(f0, DataType.CHECK_BOX, "Field 1"));
		f0.addField(new MedicFormField(f0, DataType.CHECK_BOX, "Field 2"));
		f0.addField(new MedicFormField(f0, DataType.CHECK_BOX, "Field 3"));
		f0.addField(new MedicFormField(f0, DataType.CHECK_BOX, "Field 4"));
		f0.addField(new MedicFormField(f0, DataType.CHECK_BOX, "Field 5"));
		f0.addField(new MedicFormField(f0, DataType.CHECK_BOX, "Field 6"));
		f0.addField(new MedicFormField(f0, DataType.CHECK_BOX, "Field 7"));
		
		MedicForm f11 = new MedicForm("Side Effects");
		f11.addField(new MedicFormField(f11, DataType.TEXT_FIELD, "Patient Name"));
		f11.addField(new MedicFormField(f11, DataType.DATE_FIELD, "Patient Birthdate"));
		f11.addField(new MedicFormField(f11, DataType.TEXT_FIELD, "Medication the patient is on"));
		f11.addField(new MedicFormField(f11, DataType.CHECK_BOX, "Check for headache"));
		f11.addField(new MedicFormField(f11, DataType.CHECK_BOX, "Check for nausea"));
		f11.addField(new MedicFormField(f11, DataType.CHECK_BOX, "Check for diarrhea"));
		f11.addField(new MedicFormField(f11, DataType.CHECK_BOX, "Check for internal bleeding"));
		f11.addField(new MedicFormField(f11, DataType.CHECK_BOX, "Check for sores"));
		f11.addField(new MedicFormField(f11, DataType.CHECK_BOX, "Check for dizziness"));
		
		MedicForm f12 = new MedicForm("Morphine Adherence");
		f12.addField(new MedicFormField(f12, DataType.TEXT_FIELD, "Patient Name"));
		f12.addField(new MedicFormField(f12, DataType.DATE_FIELD, "Patient Birthdate"));
		f12.addField(new MedicFormField(f12, DataType.CHECK_BOX, "Check if Morphine taken"));
		f12.addField(new MedicFormField(f12, DataType.TEXT_AREA, "Additional notes"));
		
		MedicForm f13 = new MedicForm("Present Complaints of Patient");
		f13.addField(new MedicFormField(f13, DataType.TEXT_FIELD, "Patient Name"));
		f13.addField(new MedicFormField(f13, DataType.DATE_FIELD, "Patient Birthdate"));
		f13.addField(new MedicFormField(f13, DataType.TEXT_AREA, "Complaints"));
		
		forms.add(f1);
		forms.add(f2);
		forms.add(f3);
		forms.add(f4);
		forms.add(f5);
		forms.add(f6);
		forms.add(f7);
		forms.add(f8);
		forms.add(f9);
		forms.add(f0);
		forms.add(f11);
		forms.add(f12);
		forms.add(f13);
		
		log("Forms Created");
		ArrayList<MedicFormResponse> fresponses = new ArrayList<MedicFormResponse>();
		
		for (MedicForm f : forms) {
			for (int i = 0; i < 500; i++) {
				//randomly select a Patient/CHW pair
				
				Patient p = ps.get(rand.nextInt(ps.size()));
				CommunityHealthWorker  chw = p.getChw();
				//generate response values
				List<MedicFieldResponse> rvs = new ArrayList<MedicFieldResponse>();
				for (MedicFormField ff : f.getFields()) {
					//if the field is a checkbox, randomly answer yes and no
					if (ff.getDatatype() == DataType.CHECK_BOX) {
						rvs.add(new MedicFieldResponse(new Boolean(rand
								.nextBoolean()).toString(), ff,p,chw));
					} else if (ff.getDatatype() == DataType.TEXT_FIELD) {
						if (ff.getLabel().equals("Patient Name")) {
							rvs.add(new MedicFieldResponse(p.getName(), ff,p,chw));
						} else {
							rvs.add(new MedicFieldResponse("response for text field",ff,p,chw));
						}
					} else if (ff.getDatatype() == DataType.TEXT_AREA) {
						rvs.add(new MedicFieldResponse(
								"I'm entering additional notes for a text area", ff,p,chw));
					} else if (ff.getDatatype() == DataType.DATE_FIELD) {
						if (ff.getLabel().equals("Patient Birthdate")) {
							rvs.add(new MedicFieldResponse(p.getBirthdate().toString(), ff,p,chw));
						} else {
							rvs.add(new MedicFieldResponse(getRandomDate().toString(), ff,p,chw));
						}
					}else if(ff.getDatatype() == DataType.NUMERIC_TEXT_FIELD){
						rvs.add(new MedicFieldResponse(rand.nextInt(500)+"" , ff,p,chw));	
					}
				}
				
				MedicFormResponse fr = new MedicFormResponse(f, rvs,chw,p);
				Date dsumbitted= getRandomDate();
				fr.setDateSubmitted(dsumbitted);
				for(MedicFieldResponse mfr: fr.getResponses()){
					mfr.setDateSubmitted(dsumbitted);
				}
				fresponses.add(fr);
			}
		}
		log("Form Responses Created");
		
		ArrayList<MedicMessageResponse> mresponse = new ArrayList<MedicMessageResponse>();
		ArrayList<Message> messages = new ArrayList<Message>();
		for(int g = 0; g< 1000; g++){
			Patient subject = ps.get(rand.nextInt(ps.size()));
			CommunityHealthWorker submitter = subject.getChw();
			String mess ="This is a freeform text message from " + submitter.getName() + " about " + subject.getName();
			Message m = Message.createIncomingMessage(getRandomDate().getTime(), submitter.getPhoneNumber(), "8004329", mess);
			MedicMessageResponse message = new MedicMessageResponse(m, mess,submitter,subject);
			messages.add(m);
			mresponse.add(message);
		}
		log("Messages Created");
		
		
		log("Information generated, saving Forms...");
		for(MedicForm f: forms){
			try{
				formDao.saveMedicForm(f);
			}catch(Throwable t){
				t.printStackTrace();
			}
		}
		log("Forms saved, saving Form responses...");
		int count = 0;
		for(MedicFormResponse fr: fresponses){
			try{
			formResponseDao.saveMedicFormResponse(fr);
			}catch(Throwable t){
				t.printStackTrace();
			}
			count++;
			if(count % 200==0){
				log(count +" form responses saved");
			}
		}
		System.out.println("Form Responses saved, saving messages...");
		count = 0;
		
		for(Message m: messages){
			try{
				messageDao.saveMessage(m);
				}catch(Throwable t){
					t.printStackTrace();
				}
				count++;
				if(count % 200==0){
					log(count +" messages saved");
				}
		}
		log("Messages saved, saving message responses...");
		count = 0;
		for(MedicMessageResponse ri:mresponse){
			try{
			messageResponseDao.saveMedicMessageResponse(ri);
			}catch(Throwable t){
				t.printStackTrace();
			}
			count++;
			if(count % 200==0){
				log(count +" message Responses saved");
			}
		}
		log("Everything saved, creating user credentials");
		
		User user0 = new User("Tester Admin",Gender.FEMALE, new Date(),"admin","medic",Role.ADMIN);
		User user9 = new User("Tester Read/Write",Gender.MALE, new Date(),"readwrite","medic",Role.READWRITE);
		User user8 = new User("Tester Read Only",Gender.FEMALE, new Date(),"readonly","medic",Role.READ);
		User user = new User("Alex Harsha",Gender.FEMALE, new Date(),"aHarsha","medic",Role.ADMIN);
		User user2 = new User("Aisha Moniba ",Gender.FEMALE, new Date(),"aMoniba","medic",Role.READWRITE);
		User user3 = new User("Daniel Kayiwa ",Gender.MALE, new Date(),"dKayiwa","medic",Role.READ);
		HibernateUserDao userDao = (HibernateUserDao) applicationContext.getBean("UserDao");
		userDao.saveUser(user);
		userDao.saveUser(user2);
		userDao.saveUser(user3);
		userDao.saveUser(user0);
		userDao.saveUser(user8);
		userDao.saveUser(user9);

	}

	/**
	 * Random object used for generating random numbers and dates
	 */
	private static Random rand = new Random();

	
	/**
	 * Used to create random birthdates
	 * @return A date between today and 40 years ago
	 */
	private Date getRandomDate(){
		double d = rand.nextDouble();
		long time = (long) (d * 31536000000.0);
		return new Date(time);	
	}
	
	/**
	 * Used to create random phone numbers
	 * @return a String containing a random 7 digit number
	 */
	private String getRandomNumber() {
		String result = "";
		for (int i = 0; i < 10; i++) {
			result += rand.nextInt(10);
		}
		return result;
	}

	/** 
	 * @see net.frontlinesms.listener.IncomingMessageListener#incomingMessageEvent(net.frontlinesms.data.domain.Message)
	 */
	public void incomingMessageEvent(Message message) {
		CommunityHealthWorker submitter = chwDao.getCommunityHealthWorkerByPhoneNumber(message.getSenderMsisdn());
		if(submitter!= null){
			MedicMessageResponse mr = new MedicMessageResponse(message,message.getTextContent(),submitter,null);
			messageResponseDao.saveMedicMessageResponse(mr);
		}
	}

	/** 
	 * @see net.frontlinesms.events.EventObserver#notify(net.frontlinesms.events.FrontlineEvent)
	 */
	public void notify(FrontlineEvent event) {
		if(event instanceof DidSaveNotification){
			if(((DidSaveNotification) event).getSavedObject() instanceof FormResponse){
				handleFormResponse((FormResponse) ((DidSaveNotification) event).getSavedObject());
			}
		}
	}
	
	
	public boolean isMedicForm(Form f){
		return ((MedicFormDao) applicationContext.getBean("MedicFormDao")).getMedicFormForForm(f) !=null;
	}
	
	public void testFormHandling(){
		try{
		Message mess = Message.createIncomingMessage(24342, "2088473937", "234234234", "wtf mate");
		Form parentForm = formDao.getMedicFormsForString("Patient Entry").iterator().next().getForm();
		List<ResponseValue> rvs = new ArrayList<ResponseValue>();
		rvs.add(new ResponseValue("drew Chavez"));
		rvs.add(new ResponseValue("06/23/1970"));
		rvs.add(new ResponseValue("male"));
		rvs.add(new ResponseValue("500 inches"));
		rvs.add(new ResponseValue("HIV Positive"));
		FormResponse fr = new FormResponse(mess,parentForm,rvs);
		handleFormResponse(fr);
		}catch(Exception e){}
	}
	
	/**
	 * When a form is submitted, attempts to pair that form with the patient that is it's subject.
	 * If there is more than one possibility, the form is posted to the changelog with a list of
	 * suggested patients. If there is only one real possibility, but that possibility does
	 * not match exactly, it is also posted to the changelog with a snippet about what did not 
	 * match and what did.
	 * 
	 * @param formResponse
	 */
	private void handleFormResponse(FormResponse formResponse){
		//if the form submitted is not a medic form, then do nothing
		if(!isMedicForm(formResponse.getParentForm())){
			return;
		}
		//get the medic form equivalent of the form submitted
		MedicForm mForm = ((MedicFormDao) applicationContext.getBean("MedicFormDao")).getMedicFormForForm(formResponse.getParentForm());
		//get the CHW that submitted the form
		CommunityHealthWorker chw = ((CommunityHealthWorkerDao) applicationContext.getBean("CHWDao")).getCommunityHealthWorkerByPhoneNumber(formResponse.getSubmitter());
		//get the list of patients that the CHW cares for
		ArrayList<Patient> patients = new ArrayList<Patient>(((PatientDao) applicationContext.getBean("PatientDao")).getPatientsForCHW(chw));
		//create an array of scores for the patients
		float[] scores = new float[patients.size()];
		float numberOfFields = 0f;
		//iterate through all fields on the form, seeing if they are mapped to patient identifying fields
		//e.g. Birthdate, Name, and Patient ID
		for(MedicFormField formField : mForm.getFields()){
			//if it is mapped to a namefield, score it as a name
			if(formField.getMapping() == PatientAttributeMapping.NAMEFIELD){
				for(int i = 0; i < patients.size(); i++){
					scores[i] += scoreName(patients.get(i).getName(),formResponse.getResults().get(formField.getPosition()).toString());
				}
				numberOfFields++;
			//if it is mapped to an id field, score it as an ID
			}else if(formField.getMapping() == PatientAttributeMapping.IDFIELD){
				for(int i = 0; i < patients.size(); i++){
					scores[i] += getEditDistance(patients.get(i).getStringID(),formResponse.getResults().get(formField.getPosition()).toString());
				}
				numberOfFields++;
			//if it is mapped as a bday field, score it as a bday
			}else if(formField.getMapping() == PatientAttributeMapping.BIRTHDATEFIELD){
				float[] dateScores = getCombinedBirthdateDistances(patients,formResponse.getResults().get(formField.getPosition()).toString());
				for(int i = 0; i < dateScores.length; i++){
					scores[i] += dateScores[i] * 0.6f;
				}
				numberOfFields+=0.6f;
			}
		}
		System.out.println("Scores for all patients of "+ chw.getName());
		for(int i = 0; i < scores.length; i++){
			System.out.println(patients.get(i).getName() + " " + scores[i]);
		}
	}
	
	
	/**
	 * Returns a float from 1.0 - 0.0 that measures the similarity between 2 strings (mainly names) using
	 * the jaro-winkler method. The higher the number, the greater the similarity
	 * @param patientName string 1 (generally the name of the patient)
	 * @param responseName	string 2 (generally the name typed into the form
	 * @return a float from 1.0 - 0.0
	 */
	public float scoreName(String patientName, String responseName){
		if(jaroWinkler == null){
			jaroWinkler = new JaroWinkler();
		}
		return jaroWinkler.getSimilarity(patientName, responseName);
	}
	
	/**
	 * Returns an array of floats (1 score for each person) from 1.0 - 0.0 that measures the similarity between 
	 * 2 dates (specifically the patient's birthdate and the date provided on the form)
	 * using a hybrid method. Half of the score is edit distance (Leshvenstein) and the other half
	 * of the score is based on the number of milliseconds difference between the 2 dates.
	 * The higher the number, the greater the similarity
	 * @param patients the patient's whose birthdates will be compared
	 * @param stringDate the second date (generally the date typed into the form)
	 * @return a float[] with scores from 1.0 - 0.0
	 */
	public float[] getCombinedBirthdateDistances(ArrayList<Patient> patients, String stringDate){
		
		DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT);
		Date responseDate;
		try {
			responseDate = df.parse(stringDate);
		} catch (ParseException e) {
			return null;
		}
		float[] scores = getBirthdateTimeDistances(patients,responseDate);
		for(int i = 0; i < scores.length;i++){
			scores[i] = scores[i] * 0.5f;
		}
		for(int i = 0; i < scores.length; i++){
			scores[i] += 0.5f * getEditDistance(df.format(patients.get(i).getBirthdate()),stringDate);
		}
		return scores;
	}
	
	/**
	 * Returns a float array of scores that reflect the distance between a list of
	 * dates (in this case the patients' birthdates) and the provided date. This is
	 * calculated by getting the milliseconds between the dates and then re-distributing 
	 * them over the interval from 0 - 1. The greater the number, the higher the similarity
	 * @param patients
	 * @param responseDate
	 * @return
	 */
	public float[] getBirthdateTimeDistances(ArrayList<Patient> patients, Date responseDate){
		//create the results array
		float[] scores = new float[patients.size()];
		//compute all the distances
		for(int i = 0; i< patients.size();i++){
			scores[i] = responseDate.getTime() - patients.get(i).getBirthdate().getTime();
		}
		//compute the min value and start the set at the min score (to start the set at 0)
		float min = Float.MAX_VALUE;
		for(float f:scores){
			min = Math.min(min, f);
		}
		for(int i = 0; i <scores.length;i++){
			scores[i] = scores[i] - min;
		}
		//compute the max value to re-distribute all the scores
		//across the interval from 0 -1 as percents of the max score
		float max = Float.MIN_VALUE;
		for(float f:scores){
			max = Math.max(max, f);
		}
		for(int i = 0; i <scores.length;i++){
			scores[i] = 1.0f - (scores[i] / max) ;
		}
		return scores;
	}
	
	/**
	 * Returns the edit distance between 2 strings, as implemented by Levenshtein
	 * @param stringOne 
	 * @param stringTwo
	 * @return a value from 0.0 -1.0. The greater the similarity, the higher the number
	 */
	public float getEditDistance(String stringOne, String stringTwo){
		if(levenshtein == null){
			levenshtein = new Levenshtein();
		}
		return levenshtein.getSimilarity(stringOne, stringTwo);
	}
	

	
}
