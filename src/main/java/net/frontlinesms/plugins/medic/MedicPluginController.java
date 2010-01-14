package net.frontlinesms.plugins.medic;

import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import net.frontlinesms.FrontlineSMS;
import net.frontlinesms.Utils;
import net.frontlinesms.data.domain.Message;
import net.frontlinesms.data.repository.MessageDao;
import net.frontlinesms.plugins.BasePluginController;
import net.frontlinesms.plugins.PluginControllerProperties;
import net.frontlinesms.plugins.PluginInitialisationException;
import net.frontlinesms.plugins.medic.data.domain.framework.DataType;
import net.frontlinesms.plugins.medic.data.domain.framework.MedicForm;
import net.frontlinesms.plugins.medic.data.domain.framework.MedicFormField;
import net.frontlinesms.plugins.medic.data.domain.people.CommunityHealthWorker;
import net.frontlinesms.plugins.medic.data.domain.people.Patient;
import net.frontlinesms.plugins.medic.data.domain.people.User;
import net.frontlinesms.plugins.medic.data.domain.people.User.Role;
import net.frontlinesms.plugins.medic.data.domain.response.MedicFieldResponse;
import net.frontlinesms.plugins.medic.data.domain.response.MedicFormResponse;
import net.frontlinesms.plugins.medic.data.domain.response.MedicMessageResponse;
import net.frontlinesms.plugins.medic.data.repository.CommunityHealthWorkerDao;
import net.frontlinesms.plugins.medic.data.repository.PatientDao;
import net.frontlinesms.plugins.medic.data.repository.hibernate.HibernateMedicFormDao;
import net.frontlinesms.plugins.medic.data.repository.hibernate.HibernateMedicFormResponseDao;
import net.frontlinesms.plugins.medic.data.repository.hibernate.HibernateMedicMessageResponseDao;
import net.frontlinesms.plugins.medic.data.repository.hibernate.HibernateUserDao;
import net.frontlinesms.plugins.medic.ui.MedicThinletTabController;
import net.frontlinesms.plugins.medic.userlogin.UserSessionManager;
import net.frontlinesms.ui.ExtendedThinlet;
import net.frontlinesms.ui.UiGeneratorController;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;

import thinlet.FrameLauncher;
import thinlet.Thinlet;

@PluginControllerProperties(name="Patient View", iconPath="/icons/big_medic.png",
		springConfigLocation="classpath:net/frontlinesms/plugins/medic/medic-spring-hibernate.xml",
		hibernateConfigPath="classpath:net/frontlinesms/plugins/medic/medic.hibernate.cfg.xml")
public class MedicPluginController extends BasePluginController  {

	// > INSTANCE PROPERTIES
	/** Logging object */
	private Logger log = Utils.getLogger(this.getClass());
	/** the {@link FrontlineSMS} instance that this plugin is attached to */
	private FrontlineSMS frontlineController;

	/** Data access object for CHWs **/
	private CommunityHealthWorkerDao CHWDao;

	/** Data access object for Patients **/
	private PatientDao patientDao;

	private ApplicationContext applicationContext;
	
	Object mainPanel;
	Object textArea;
	ExtendedThinlet thinlet;
	ArrayList<String> text;
	
	@Override
	protected Object initThinletTab(UiGeneratorController uiController) {
		MedicThinletTabController controller= new MedicThinletTabController(this,uiController);
		return controller.getTab();
	}

	public void deinit() {
		// TODO Auto-generated method stub
		
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

	public void init(FrontlineSMS frontlineController, ApplicationContext applicationContext) throws PluginInitialisationException {
		this.frontlineController = frontlineController;
		this.applicationContext = applicationContext;
		UserSessionManager.getUserSessionManager().init(applicationContext);
		try {
			this.CHWDao = (CommunityHealthWorkerDao) applicationContext
					.getBean("CHWDao");
			this.patientDao = (PatientDao) applicationContext
					.getBean("PatientDao");
			testing(applicationContext);
		} catch (Throwable t) {
			log.warn("Unable to load Medic plugin", t);
			throw new PluginInitialisationException(t);
		}	
	}
	
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

	private void testing(ApplicationContext appCon) {
		if(CHWDao.getAllCommunityHealthWorkers().size() !=0){
			return;
		}
		thinlet = new ExtendedThinlet();
		mainPanel = thinlet.createPanel("mainPanel");
		textArea = thinlet.create("textarea");
		thinlet.setWeight(mainPanel, 1, 1);
		thinlet.setWeight(textArea, 1, 1);
		thinlet.add(mainPanel,textArea);
		thinlet.add(Thinlet.create("panel"));
		thinlet.add(mainPanel);
		//you have to use a special framelauncher class because otherwise it will close all open windows
		FrameLauncher framel = new FrameLauncher("Edit the Detail View",thinlet,500,500,null)
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
		
		String [] affiliations = { "Afar","Anlo-Ewe","Amhara","Ashanti","Bakongo",
				"Bambara,Bemba","Berber","Bobo","San","Chewa","Dogon","Fang","Fon",
				"Fulani","Ibos","Kikuyu (Gikuyu)","Maasai","Mandinka","Pygmies",
				"Samburu","Senufo","Tuareg","Wolof","Yoruba","Zulu"};
		
		ArrayList<CommunityHealthWorker> chws = new ArrayList<CommunityHealthWorker>();
		ArrayList<Patient> ps = new ArrayList<Patient>();
		// create 80 chws with 100 patients each
		for (int i = 0; i < 35; i++) {
			String name = firsts[rand.nextInt(firsts.length)] + " "
					+ lasts[rand.nextInt(lasts.length)];
			char gender = (rand.nextBoolean()) ? 'm':'f';
			CommunityHealthWorker chw = new CommunityHealthWorker(name, getRandomNumber(),gender,getRandomDate());
			chws.add(chw);
			ArrayList<Patient> patients = new ArrayList<Patient>();
			//create 100 patients for each chw
			for (int j = 0; j < 35; j++) {
				char gender2= (rand.nextBoolean()) ? 'm':'f';
				String name2 = firsts[rand.nextInt(firsts.length)] + " " + lasts[rand.nextInt(lasts.length)];
				Patient p = new Patient(chw, name2, gender2, getRandomDate());
				patients.add(p);
				ps.add(p);
			}
			try {
				//save the chw
				CHWDao.saveCommunityHealthWorker(chw);
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
		f1.addField(new MedicFormField(f1, DataType.TEXT, "Patient Name"));
		f1.addField(new MedicFormField(f1, DataType.DATE_FIELD, "Patient Birthdate"));
		f1.addField(new MedicFormField(f1, DataType.TEXT, "Patient affiliation"));
		f1.addField(new MedicFormField(f1, DataType.TEXT, "Patient Gender"));
		f1.addField(new MedicFormField(f1, DataType.TEXT, "Patient height"));
		// create patient weight form
		MedicForm f2 = new MedicForm("Patient Weight");
		f2.addField(new MedicFormField(f2, DataType.TEXT, "Patient Name"));
		f2.addField(new MedicFormField(f2, DataType.DATE_FIELD, "Patient Birthdate"));
		f2.addField(new MedicFormField(f2, DataType.NUMBER, "Patient Weight"));
		// create patient death form
		MedicForm f3 = new MedicForm("Patient Death");
		f3.addField(new MedicFormField(f3, DataType.TEXT, "Patient Name"));
		f3.addField(new MedicFormField(f3, DataType.DATE_FIELD, "Patient Birthdate"));
		f3.addField(new MedicFormField(f3, DataType.DATE_FIELD, "Date of Death"));
		f3.addField(new MedicFormField(f3, DataType.TEXT_AREA, "Probable Cause"));
		// create patient ARV adherence form
		MedicForm f4 = new MedicForm("ARV adherence");
		f4.addField(new MedicFormField(f4, DataType.TEXT, "Patient Name"));
		f4.addField(new MedicFormField(f4, DataType.DATE_FIELD, "Patient Birthdate"));
		f4.addField(new MedicFormField(f4, DataType.CHECK_BOX, "ARVs taken?"));
		f4.addField(new MedicFormField(f4, DataType.TEXT_AREA, "Additional notes"));
		// create birth form
		MedicForm f5 = new MedicForm("Patient Birth");
		f5.addField(new MedicFormField(f5, DataType.TEXT, "Patient Name"));
		f5.addField(new MedicFormField(f5, DataType.DATE_FIELD, "Patient Birthdate"));
		f5.addField(new MedicFormField(f5, DataType.CHECK_BOX, "Check if baby healthy"));
		f5.addField(new MedicFormField(f5, DataType.CHECK_BOX, "Check if mother healthy"));
		f5.addField(new MedicFormField(f5, DataType.TEXT_AREA, "Additional notes"));
		// create rash form
		MedicForm f6 = new MedicForm("Unknown Rash");
		f6.addField(new MedicFormField(f6, DataType.TEXT, "Patient Name"));
		f6.addField(new MedicFormField(f6, DataType.DATE_FIELD, "Patient Birthdate"));
		f6.addField(new MedicFormField(f6, DataType.CHECK_BOX, "Head Rash?"));
		f6.addField(new MedicFormField(f6, DataType.CHECK_BOX, "Body Rash?"));
		f6.addField(new MedicFormField(f6, DataType.TEXT,
				"Location of Rash"));
		f6.addField(new MedicFormField(f6, DataType.TEXT_AREA, "Other notes"));
		// create other form
		MedicForm f7 = new MedicForm("Diarrheal Disease");
		f7.addField(new MedicFormField(f7, DataType.TEXT, "Patient Name"));
		f7.addField(new MedicFormField(f7, DataType.DATE_FIELD, "Patient Birthdate"));
		f7.addField(new MedicFormField(f7,DataType.TEXT,"Severity"));
		f7.addField(new MedicFormField(f7,DataType.TEXT,"Frequency"));
		f7.addField(new MedicFormField(f7, DataType.CHECK_BOX, "Is patient dehydrated?"));
		f7.addField(new MedicFormField(f7, DataType.CHECK_BOX, "Check if bloody"));
		f7.addField(new MedicFormField(f7, DataType.TEXT_AREA, "additional notes"));
		// create other form
		MedicForm f8 = new MedicForm("Joint Pain");
		f8.addField(new MedicFormField(f8, DataType.TEXT, "Patient Name"));
		f8.addField(new MedicFormField(f8, DataType.DATE_FIELD, "Patient Birthdate"));
		f8.addField(new MedicFormField(f8, DataType.TEXT, "Joints affected"));
		f8.addField(new MedicFormField(f8, DataType.TEXT, "Type of Pain"));
		f8.addField(new MedicFormField(f8, DataType.TEXT_AREA, "Additional Notes"));
		// create other form
		MedicForm f9 = new MedicForm("Long Test MedicForm");
		f9.addField(new MedicFormField(f9, DataType.TEXT, "Patient Name"));
		f9.addField(new MedicFormField(f9, DataType.DATE_FIELD, "Patient Birthdate"));
		f9.addField(new MedicFormField(f9, DataType.TEXT, "MedicField 1"));
		f9.addField(new MedicFormField(f9, DataType.CHECK_BOX, "more fun!"));
		f9.addField(new MedicFormField(f9, DataType.TEXT, "MedicField 2"));
		f9.addField(new MedicFormField(f9, DataType.TEXT, "MedicField 3"));
		f9.addField(new MedicFormField(f9, DataType.TEXT, "MedicField 4"));
		f9.addField(new MedicFormField(f9, DataType.TEXT, "MedicField 5"));
		f9.addField(new MedicFormField(f9, DataType.TEXT, "MedicField 6"));
		f9.addField(new MedicFormField(f9, DataType.TEXT, "MedicField 7"));
		f9.addField(new MedicFormField(f9, DataType.TEXT, "MedicField 8"));

		// create other form
		MedicForm f0 = new MedicForm("CheckBox Long Test MedicForm");
		f0.addField(new MedicFormField(f0, DataType.TEXT, "Patient Name"));
		f0.addField(new MedicFormField(f0, DataType.DATE_FIELD, "Patient Birthdate"));
		f0.addField(new MedicFormField(f0, DataType.TEXT, "Field 1"));
		f0.addField(new MedicFormField(f0, DataType.CHECK_BOX, "Field 2"));
		f0.addField(new MedicFormField(f0, DataType.CHECK_BOX, "MedicField 1"));
		f0.addField(new MedicFormField(f0, DataType.CHECK_BOX, "MedicField 2"));
		f0.addField(new MedicFormField(f0, DataType.CHECK_BOX, "MedicField 3"));
		f0.addField(new MedicFormField(f0, DataType.CHECK_BOX, "MedicField 4"));
		f0.addField(new MedicFormField(f0, DataType.CHECK_BOX, "MedicField 5"));
		f0.addField(new MedicFormField(f0, DataType.CHECK_BOX, "MedicField 6"));
		f0.addField(new MedicFormField(f0, DataType.CHECK_BOX, "MedicField 7"));
		
		MedicForm f11 = new MedicForm("Side Effects");
		f11.addField(new MedicFormField(f11, DataType.TEXT, "Patient Name"));
		f11.addField(new MedicFormField(f11, DataType.DATE_FIELD, "Patient Birthdate"));
		f11.addField(new MedicFormField(f11, DataType.TEXT, "Medication the patient is on"));
		f11.addField(new MedicFormField(f11, DataType.CHECK_BOX, "Check for headache"));
		f11.addField(new MedicFormField(f11, DataType.CHECK_BOX, "Check for nausea"));
		f11.addField(new MedicFormField(f11, DataType.CHECK_BOX, "Check for diarrhea"));
		f11.addField(new MedicFormField(f11, DataType.CHECK_BOX, "Check for internal bleeding"));
		f11.addField(new MedicFormField(f11, DataType.CHECK_BOX, "Check for sores"));
		f11.addField(new MedicFormField(f11, DataType.CHECK_BOX, "Check for dizziness"));
		
		MedicForm f12 = new MedicForm("Morphine Adherence");
		f12.addField(new MedicFormField(f12, DataType.TEXT, "Patient Name"));
		f12.addField(new MedicFormField(f12, DataType.DATE_FIELD, "Patient Birthdate"));
		f12.addField(new MedicFormField(f12, DataType.CHECK_BOX, "Check if Morphine taken"));
		f12.addField(new MedicFormField(f12, DataType.TEXT_AREA, "Additional notes"));
		
		MedicForm f13 = new MedicForm("Present Complaints of Patient");
		f13.addField(new MedicFormField(f13, DataType.TEXT, "Patient Name"));
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
					} else if (ff.getDatatype() == DataType.TEXT) {
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
					}else if(ff.getDatatype() == DataType.NUMBER){
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
			Message m = Message.createIncomingMessage(new Date().getTime(), submitter.getPhoneNumber(), "8004329", mess);
			MedicMessageResponse message = new MedicMessageResponse(m, mess,submitter,subject);
			message.setDateSubmitted(getRandomDate());
			messages.add(m);
			mresponse.add(message);
		}
		log("Messages Created");
		
		HibernateMedicFormResponseDao formResponseDao =(HibernateMedicFormResponseDao) appCon.getBean("MedicFormResponseDao");
		HibernateMedicFormDao formDao =(HibernateMedicFormDao) appCon.getBean("MedicFormDao");
		HibernateMedicMessageResponseDao messageResponseDao = (HibernateMedicMessageResponseDao) appCon.getBean("MedicMessageResponseDao");
		MessageDao messageDao = (MessageDao) appCon.getBean("messageDao");
		
		log("Information generated, saving...");
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
		
		User user = new User("Alex Harsha",'f', new Date(),"aHarsha","medic",Role.ADMIN);
		User user2 = new User("Aisha Moniba ",'f', new Date(),"aMoniba","medic",Role.READWRITE);
		User user3 = new User("Daniel Kayiwa ",'m', new Date(),"dKayiwa","medic",Role.READ);
		HibernateUserDao userDao = (HibernateUserDao) applicationContext.getBean("UserDao");
		userDao.saveUser(user);
		userDao.saveUser(user2);
		userDao.saveUser(user3);

	}

	private static Random rand = new Random();

	
	private Date getRandomDate(){
		double d = rand.nextDouble();
		long time = (long) (d * 31536000000L);
		return new Date(time);	
	}
	
	private String getRandomNumber() {
		String result = "";
		for (int i = 0; i < 10; i++) {
			result += rand.nextInt(10);
		}
		return result;
	}


	
	
}
