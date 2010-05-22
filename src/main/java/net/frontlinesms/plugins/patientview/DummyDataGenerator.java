package net.frontlinesms.plugins.patientview;

import java.awt.event.WindowEvent;
import java.security.GeneralSecurityException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Random;

import net.frontlinesms.FrontlineSMSConstants;
import net.frontlinesms.data.DuplicateKeyException;
import net.frontlinesms.data.domain.Group;
import net.frontlinesms.data.domain.Message;
import net.frontlinesms.data.repository.GroupDao;
import net.frontlinesms.data.repository.GroupMembershipDao;
import net.frontlinesms.data.repository.MessageDao;
import net.frontlinesms.plugins.forms.data.domain.Form;
import net.frontlinesms.plugins.forms.data.domain.FormField;
import net.frontlinesms.plugins.forms.data.domain.FormFieldType;
import net.frontlinesms.plugins.forms.data.repository.FormDao;
import net.frontlinesms.plugins.patientview.data.domain.framework.DataType;
import net.frontlinesms.plugins.patientview.data.domain.framework.MedicForm;
import net.frontlinesms.plugins.patientview.data.domain.framework.MedicFormField;
import net.frontlinesms.plugins.patientview.data.domain.framework.MedicFormField.PatientFieldMapping;
import net.frontlinesms.plugins.patientview.data.domain.people.CommunityHealthWorker;
import net.frontlinesms.plugins.patientview.data.domain.people.Patient;
import net.frontlinesms.plugins.patientview.data.domain.people.User;
import net.frontlinesms.plugins.patientview.data.domain.people.Person.Gender;
import net.frontlinesms.plugins.patientview.data.domain.people.User.Role;
import net.frontlinesms.plugins.patientview.data.domain.response.MedicFormFieldResponse;
import net.frontlinesms.plugins.patientview.data.domain.response.MedicFormResponse;
import net.frontlinesms.plugins.patientview.data.domain.response.MedicMessageResponse;
import net.frontlinesms.plugins.patientview.data.repository.CommunityHealthWorkerDao;
import net.frontlinesms.plugins.patientview.data.repository.MedicFormDao;
import net.frontlinesms.plugins.patientview.data.repository.MedicFormResponseDao;
import net.frontlinesms.plugins.patientview.data.repository.MedicMessageResponseDao;
import net.frontlinesms.plugins.patientview.data.repository.PatientDao;
import net.frontlinesms.plugins.patientview.data.repository.UserDao;
import net.frontlinesms.ui.ExtendedThinlet;
import net.frontlinesms.ui.i18n.InternationalisationUtils;

import org.springframework.context.ApplicationContext;

import thinlet.FrameLauncher;
import thinlet.Thinlet;
import uk.ac.shef.wit.simmetrics.similaritymetrics.JaroWinkler;
import uk.ac.shef.wit.simmetrics.similaritymetrics.Levenshtein;

public class DummyDataGenerator {
	
	private static DateFormat df = InternationalisationUtils.getDateFormat();
	
	private ApplicationContext applicationContext;
	/** Message Response Dao for saving message responses when they come in**/
	MedicMessageResponseDao messageResponseDao;
	/**Community Health worker dao for fetching CHWs**/
	CommunityHealthWorkerDao chwDao;
	PatientDao patientDao;
	FormDao vanillaFormDao;
	MedicFormDao formDao;
	UserDao userDao ;
	/**the community health worker group*/
	private Group chwgroup;
	
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
	
	public DummyDataGenerator(ApplicationContext appCon){
		this.applicationContext = appCon;
		messageResponseDao = (MedicMessageResponseDao) applicationContext.getBean("MedicMessageResponseDao");
		chwDao = (CommunityHealthWorkerDao) applicationContext.getBean("CHWDao");
		patientDao = (PatientDao) applicationContext.getBean("PatientDao");
		vanillaFormDao = (FormDao) applicationContext.getBean("formDao");
		formDao =(MedicFormDao) applicationContext.getBean("MedicFormDao");
		userDao = (UserDao) applicationContext.getBean("UserDao");
		
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
	 * Creates dummy data and writes it to the database.
	 */
	@SuppressWarnings("serial")
	public void createDummyData() {
//		if(chwDao.getAllCommunityHealthWorkers().size() !=0){
//			return;
//		}
		if(userDao.getAllUsers().size() != 0){
			return;
		}
		
		MessageDao messageDao = (MessageDao) applicationContext.getBean("messageDao");
		MedicFormResponseDao formResponseDao =(MedicFormResponseDao) applicationContext.getBean("MedicFormResponseDao");
		thinlet = new ExtendedThinlet();
		mainPanel = thinlet.createPanel("mainPanel");
		textArea = Thinlet.create("textarea");
		thinlet.setWeight(mainPanel, 1, 1);
		thinlet.setWeight(textArea, 1, 1);
		thinlet.add(mainPanel,textArea);
		thinlet.add(Thinlet.create("panel"));
		thinlet.add(mainPanel);
		// you have to use a special framelauncher class because otherwise it will close all open windows
		new FrameLauncher("Creating Dummy Data",thinlet,500,500,null)
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
		//create the group
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
				log("CHW " + i + " saved");
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
		log("Creating the CHW group and adding all CHWs to it...");
		//create a CHW group and add all the chws to it
		Group rootGroup = new Group(null, null) {
			@Override
			/** Provide an internationalised version of this group's name */
			public String getName() {
				return InternationalisationUtils.getI18NString(FrontlineSMSConstants.CONTACTS_ALL);
			}
		};
		GroupDao gd = (GroupDao) applicationContext.getBean("groupDao");
		chwgroup = new Group(rootGroup,"Community Health Workers");
		try {
			gd.saveGroup(chwgroup);
		} catch (DuplicateKeyException e1) {
			e1.printStackTrace();
		}
		GroupMembershipDao gmd = (GroupMembershipDao) applicationContext.getBean("groupMembershipDao");
		for(CommunityHealthWorker chw:chws){
			gmd.addMember(chwgroup, chw.getContactInfo());
		}
		
		log("Group created.  Creating forms...");
		ArrayList<MedicForm> forms = new ArrayList<MedicForm>();
		// create new Patient Entry form
		MedicForm f1 = new MedicForm("Patient Entry");
		f1.addField(new MedicFormField(f1, DataType.TEXT_FIELD, "Patient Name", PatientFieldMapping.NAMEFIELD));
		f1.addField(new MedicFormField(f1, DataType.DATE_FIELD, "Patient Birthdate", PatientFieldMapping.BIRTHDATEFIELD));
		f1.addField(new MedicFormField(f1, DataType.TEXT_FIELD, "Patient Gender"));
		f1.addField(new MedicFormField(f1, DataType.TEXT_FIELD, "Patient Height"));
		f1.addField(new MedicFormField(f1, DataType.CHECK_BOX, "Patient HIV Status"));
		log("Patient Entry Form created.");
		
		// create patient weight form
		MedicForm f2 = new MedicForm("Patient Weight");
		f2.addField(new MedicFormField(f2, DataType.TEXT_FIELD, "Patient Name", PatientFieldMapping.NAMEFIELD));
		f2.addField(new MedicFormField(f2, DataType.DATE_FIELD, "Patient Birthdate", PatientFieldMapping.BIRTHDATEFIELD));
		f2.addField(new MedicFormField(f2, DataType.NUMERIC_TEXT_FIELD, "Patient Weight"));
		log("Patient Weight Form created.");
		
		// create patient death form
		MedicForm f3 = new MedicForm("Patient Death");
		f3.addField(new MedicFormField(f3, DataType.TEXT_FIELD, "Patient Name", PatientFieldMapping.NAMEFIELD));
		f3.addField(new MedicFormField(f3, DataType.DATE_FIELD, "Patient Birthdate", PatientFieldMapping.BIRTHDATEFIELD));
		f3.addField(new MedicFormField(f3, DataType.DATE_FIELD, "Date of Death"));
		f3.addField(new MedicFormField(f3, DataType.TEXT_AREA, "Probable Cause"));
		log("Patient Death Form created.");
		
		// create patient ARV adherence form
		MedicForm f4 = new MedicForm("ARV adherence");
		f4.addField(new MedicFormField(f4, DataType.TEXT_FIELD, "Patient Name", PatientFieldMapping.NAMEFIELD));
		f4.addField(new MedicFormField(f4, DataType.DATE_FIELD, "Patient Birthdate", PatientFieldMapping.BIRTHDATEFIELD));
		f4.addField(new MedicFormField(f4, DataType.CHECK_BOX, "ARVs taken?"));
		f4.addField(new MedicFormField(f4, DataType.TEXT_AREA, "Additional notes"));
		log("ARV Adherence Form created.");
		
		// create birth form
		MedicForm f5 = new MedicForm("Patient Birth");
		f5.addField(new MedicFormField(f5, DataType.TEXT_FIELD, "Patient Name", PatientFieldMapping.NAMEFIELD));
		f5.addField(new MedicFormField(f5, DataType.DATE_FIELD, "Patient Birthdate", PatientFieldMapping.BIRTHDATEFIELD));
		f5.addField(new MedicFormField(f5, DataType.CHECK_BOX, "Check if baby healthy"));
		f5.addField(new MedicFormField(f5, DataType.CHECK_BOX, "Check if mother healthy"));
		f5.addField(new MedicFormField(f5, DataType.TEXT_AREA, "Additional notes"));
		log("Patient Birth Form created.");
		
		// create rash form
		MedicForm f6 = new MedicForm("Unknown Rash");
		f6.addField(new MedicFormField(f6, DataType.TEXT_FIELD, "Patient Name", PatientFieldMapping.NAMEFIELD));
		f6.addField(new MedicFormField(f6, DataType.DATE_FIELD, "Patient Birthdate", PatientFieldMapping.BIRTHDATEFIELD));
		f6.addField(new MedicFormField(f6, DataType.CHECK_BOX, "Head Rash?"));
		f6.addField(new MedicFormField(f6, DataType.CHECK_BOX, "Body Rash?"));
		f6.addField(new MedicFormField(f6, DataType.TEXT_FIELD,
				"Location of Rash"));
		f6.addField(new MedicFormField(f6, DataType.TEXT_AREA, "Other notes"));
		log("Unknown Rash Form created.");
		
		// create other form
		MedicForm f7 = new MedicForm("Diarrheal Disease");
		f7.addField(new MedicFormField(f7, DataType.TEXT_FIELD, "Patient Name", PatientFieldMapping.NAMEFIELD));
		f7.addField(new MedicFormField(f7, DataType.DATE_FIELD, "Patient Birthdate", PatientFieldMapping.BIRTHDATEFIELD));
		f7.addField(new MedicFormField(f7,DataType.TEXT_FIELD,"Severity"));
		f7.addField(new MedicFormField(f7,DataType.TEXT_FIELD,"Frequency"));
		f7.addField(new MedicFormField(f7, DataType.CHECK_BOX, "Is patient dehydrated?"));
		f7.addField(new MedicFormField(f7, DataType.CHECK_BOX, "Check if bloody"));
		f7.addField(new MedicFormField(f7, DataType.TEXT_AREA, "Additional Notes"));
		log("Diarrheal Disease Form created.");
		
		// create other form
		MedicForm f8 = new MedicForm("Joint Pain");
		f8.addField(new MedicFormField(f8, DataType.TEXT_FIELD, "Patient Name", PatientFieldMapping.NAMEFIELD));
		f8.addField(new MedicFormField(f8, DataType.DATE_FIELD, "Patient Birthdate", PatientFieldMapping.BIRTHDATEFIELD));
		f8.addField(new MedicFormField(f8, DataType.TEXT_FIELD, "Joints affected"));
		f8.addField(new MedicFormField(f8, DataType.TEXT_FIELD, "Type of Pain"));
		f8.addField(new MedicFormField(f8, DataType.TEXT_AREA, "Additional Notes"));
		log("Joint Pain Form created.");
		
		// create other form
		MedicForm f9 = new MedicForm("Long Test Form");
		f9.addField(new MedicFormField(f9, DataType.TEXT_FIELD, "Patient Name", PatientFieldMapping.NAMEFIELD));
		f9.addField(new MedicFormField(f9, DataType.DATE_FIELD, "Patient Birthdate", PatientFieldMapping.BIRTHDATEFIELD));
		f9.addField(new MedicFormField(f9, DataType.TEXT_FIELD, "Field 1"));
		f9.addField(new MedicFormField(f9, DataType.CHECK_BOX, "more fun!"));
		f9.addField(new MedicFormField(f9, DataType.TEXT_FIELD, "Field 2"));
		f9.addField(new MedicFormField(f9, DataType.TEXT_FIELD, "Field 3"));
		f9.addField(new MedicFormField(f9, DataType.TEXT_FIELD, "Field 4"));
		f9.addField(new MedicFormField(f9, DataType.TEXT_FIELD, "Field 5"));
		f9.addField(new MedicFormField(f9, DataType.TEXT_FIELD, "Field 6"));
		f9.addField(new MedicFormField(f9, DataType.TEXT_FIELD, "Field 7"));
		f9.addField(new MedicFormField(f9, DataType.TEXT_FIELD, "Field 8"));
		log("Long Text Form created.");

		// create other form
		MedicForm f0 = new MedicForm("Checkbox Long Test Form");
		f0.addField(new MedicFormField(f0, DataType.TEXT_FIELD, "Patient Name", PatientFieldMapping.NAMEFIELD));
		f0.addField(new MedicFormField(f0, DataType.DATE_FIELD, "Patient Birthdate", PatientFieldMapping.BIRTHDATEFIELD));
		f0.addField(new MedicFormField(f0, DataType.TEXT_FIELD, "Field 1"));
		f0.addField(new MedicFormField(f0, DataType.CHECK_BOX, "Field 2"));
		f0.addField(new MedicFormField(f0, DataType.CHECK_BOX, "Field 1"));
		f0.addField(new MedicFormField(f0, DataType.CHECK_BOX, "Field 2"));
		f0.addField(new MedicFormField(f0, DataType.CHECK_BOX, "Field 3"));
		f0.addField(new MedicFormField(f0, DataType.CHECK_BOX, "Field 4"));
		f0.addField(new MedicFormField(f0, DataType.CHECK_BOX, "Field 5"));
		f0.addField(new MedicFormField(f0, DataType.CHECK_BOX, "Field 6"));
		f0.addField(new MedicFormField(f0, DataType.CHECK_BOX, "Field 7"));
		log("Long Checkbox Form created.");
		
		MedicForm f11 = new MedicForm("Side Effects");
		f11.addField(new MedicFormField(f11, DataType.TEXT_FIELD, "Patient Name", PatientFieldMapping.NAMEFIELD));
		f11.addField(new MedicFormField(f11, DataType.DATE_FIELD, "Patient Birthdate", PatientFieldMapping.BIRTHDATEFIELD));
		f11.addField(new MedicFormField(f11, DataType.TEXT_FIELD, "Medication the patient is on"));
		f11.addField(new MedicFormField(f11, DataType.CHECK_BOX, "Check for headache"));
		f11.addField(new MedicFormField(f11, DataType.CHECK_BOX, "Check for nausea"));
		f11.addField(new MedicFormField(f11, DataType.CHECK_BOX, "Check for diarrhea"));
		f11.addField(new MedicFormField(f11, DataType.CHECK_BOX, "Check for internal bleeding"));
		f11.addField(new MedicFormField(f11, DataType.CHECK_BOX, "Check for sores"));
		f11.addField(new MedicFormField(f11, DataType.CHECK_BOX, "Check for dizziness"));
		log("Side Effects Form created.");
		
		MedicForm f12 = new MedicForm("Morphine Adherence");
		f12.addField(new MedicFormField(f12, DataType.TEXT_FIELD, "Patient Name", PatientFieldMapping.NAMEFIELD));
		f12.addField(new MedicFormField(f12, DataType.DATE_FIELD, "Patient Birthdate", PatientFieldMapping.BIRTHDATEFIELD));
		f12.addField(new MedicFormField(f12, DataType.CHECK_BOX, "Check if Morphine taken"));
		f12.addField(new MedicFormField(f12, DataType.TEXT_AREA, "Additional notes"));
		log("Morphine Adherence Form created.");
		
		MedicForm f13 = new MedicForm("Present Complaints of Patient");
		f13.addField(new MedicFormField(f13, DataType.TEXT_FIELD, "Patient Name", PatientFieldMapping.NAMEFIELD));
		f13.addField(new MedicFormField(f13, DataType.DATE_FIELD, "Patient Birthdate", PatientFieldMapping.BIRTHDATEFIELD));
		f13.addField(new MedicFormField(f13, DataType.TEXT_AREA, "Complaints"));
		log("Complaints Form created.");
		
		createFSMSFormFromMedicForm(f1);
		createFSMSFormFromMedicForm(f2);
		createFSMSFormFromMedicForm(f3);
		createFSMSFormFromMedicForm(f4);
		createFSMSFormFromMedicForm(f5);
		createFSMSFormFromMedicForm(f6);
		createFSMSFormFromMedicForm(f7);
		createFSMSFormFromMedicForm(f8);
		createFSMSFormFromMedicForm(f9);
		createFSMSFormFromMedicForm(f0);
		createFSMSFormFromMedicForm(f11);
		createFSMSFormFromMedicForm(f12);
		createFSMSFormFromMedicForm(f13);
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
		
		log("Forms created. Creating form responses...");
		ArrayList<MedicFormResponse> fresponses = new ArrayList<MedicFormResponse>();
		
		for (MedicForm f : forms) {
			for (int i = 0; i < 100; i++) {
				//randomly select a Patient/CHW pair
				
				Patient p = ps.get(rand.nextInt(ps.size()));
				CommunityHealthWorker  chw = p.getChw();
				//generate response values
				List<MedicFormFieldResponse> rvs = new ArrayList<MedicFormFieldResponse>();
				for (MedicFormField ff : f.getFields()) {
					//if the field is a checkbox, randomly answer yes and no
					if (ff.getDatatype() == DataType.CHECK_BOX) {
						rvs.add(new MedicFormFieldResponse(new Boolean(rand.nextBoolean()).toString(), ff,p,chw));
					} else if (ff.getDatatype() == DataType.TEXT_FIELD) {
						if (ff.getLabel().equals("Patient Name")) {
							rvs.add(new MedicFormFieldResponse(p.getName(), ff,p,chw));
						} else {
							rvs.add(new MedicFormFieldResponse("response for text field",ff,p,chw));
						}
					} else if (ff.getDatatype() == DataType.TEXT_AREA) {
						rvs.add(new MedicFormFieldResponse(
								"I'm entering additional notes for a text area", ff,p,chw));
					} else if (ff.getDatatype() == DataType.DATE_FIELD) {
						if (ff.getLabel().equals("Patient Birthdate")) {
							rvs.add(new MedicFormFieldResponse(df.format(p.getBirthdate()), ff,p,chw));
						} else {
							rvs.add(new MedicFormFieldResponse(df.format(getRandomDate()), ff,p,chw));
						}
					}else if(ff.getDatatype() == DataType.NUMERIC_TEXT_FIELD){
						rvs.add(new MedicFormFieldResponse(rand.nextInt(500)+"" , ff,p,chw));	
					}
				}
				
				MedicFormResponse fr = new MedicFormResponse(f, rvs,chw,p);
				Date dsumbitted= getRandomDate();
				fr.setDateSubmitted(dsumbitted);
				for(MedicFormFieldResponse mfr: fr.getResponses()){
					mfr.setDateSubmitted(dsumbitted);
				}
				fresponses.add(fr);
			}
		}
		log("Form Responses Created");
		
		ArrayList<MedicMessageResponse> mresponse = new ArrayList<MedicMessageResponse>();
		ArrayList<Message> messages = new ArrayList<Message>();
		for(int g = 0; g< 300; g++){
			Patient subject = ps.get(rand.nextInt(ps.size()));
			CommunityHealthWorker submitter = subject.getChw();
			String mess ="This is a freeform text message from " + submitter.getName() + " about " + subject.getName();
			Message m = Message.createIncomingMessage(getRandomDate().getTime(), submitter.getPhoneNumber(), "8004329", mess);
			MedicMessageResponse message = new MedicMessageResponse(m, mess,submitter,subject);
			messages.add(m);
			mresponse.add(message);
		}
		log("Messages Created");
		
		
		log("All data created, beginning to save Forms");
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
		log("Form Responses saved, saving messages...");
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
		
		try {
			User user0 = new User("Tester Admin",Gender.FEMALE, new Date(),"admin",Role.ADMIN, "medic");
			User user9 = new User("Tester Read/Write",Gender.MALE, new Date(),"readwrite",Role.READWRITE,"medic");
			User user8 = new User("Tester Read Only",Gender.FEMALE, new Date(),"readonly",Role.READ,"medic");
			User user11 = new User("Tester Registrar", Gender.FEMALE, new Date(),"registrar",Role.REGISTRAR,"medic");
			User user = new User("Alex Harsha",Gender.FEMALE, new Date(),"aHarsha",Role.ADMIN,"medic");
			User user2 = new User("Aisha Moniba",Gender.FEMALE, new Date(),"aMoniba",Role.READWRITE,"medic");
			User user3 = new User("Daniel Kayiwa",Gender.MALE, new Date(),"dKayiwa",Role.READ,"medic");
			User user4 = new User("Dieterich Lawson", Gender.MALE, new Date(),"dLawson",Role.REGISTRAR,"medic");
			UserDao userDao = (UserDao) applicationContext.getBean("UserDao");
			userDao.saveUser(user);
			userDao.saveUser(user2);
			userDao.saveUser(user3);
			userDao.saveUser(user0);
			userDao.saveUser(user8);
			userDao.saveUser(user9);
			userDao.saveUser(user11);
			userDao.saveUser(user4);
		} catch (GeneralSecurityException e1) {
			log("Error creating users!");
			log(e1.getStackTrace().toString());
		}
	}
	

	/**
	 * Creates a FrontlineSMS form from a Medic form.
	 * Also finalizes the form after setting it's group to the CHW
	 * group
	 * @param mf
	 */
	private void createFSMSFormFromMedicForm(MedicForm mf){
		Form form = new Form(mf.getName());
		for(MedicFormField mff: mf.getFields()){
			form.addField(new FormField(FormFieldType.valueOf(mff.getDatatype().name()),mff.getLabel()));
		}
		form.setPermittedGroup(chwgroup);
		vanillaFormDao.saveForm(form);
		vanillaFormDao.finaliseForm(form);
		mf.setForm(form);
	}

	/**
	 * Random object used for generating random numbers and dates
	 */
	private static Random rand = new Random();

	
	/**
	 * Used to create random birthdates
	 * @return A date between today and 40 years ago
	 */
	private static Date getRandomDate(){
		int day = rand.nextInt(29);
		int month = rand.nextInt(12);
		int year = rand.nextInt(110);
		GregorianCalendar calendar = new GregorianCalendar(1900+year,month,day);
		return calendar.getTime();
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
	
}
