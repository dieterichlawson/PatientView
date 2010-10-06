package net.frontlinesms.plugins.patientview;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Random;

import net.frontlinesms.FrontlineSMSConstants;
import net.frontlinesms.data.DuplicateKeyException;
import net.frontlinesms.data.domain.FrontlineMessage;
import net.frontlinesms.data.domain.Group;
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
import net.frontlinesms.plugins.patientview.data.domain.people.Person.Gender;
import net.frontlinesms.plugins.patientview.data.domain.response.MedicFormFieldResponse;
import net.frontlinesms.plugins.patientview.data.domain.response.MedicFormResponse;
import net.frontlinesms.plugins.patientview.data.domain.response.MedicMessageResponse;
import net.frontlinesms.plugins.patientview.data.repository.CommunityHealthWorkerDao;
import net.frontlinesms.plugins.patientview.data.repository.MedicFormDao;
import net.frontlinesms.plugins.patientview.data.repository.MedicFormResponseDao;
import net.frontlinesms.plugins.patientview.data.repository.MedicMessageResponseDao;
import net.frontlinesms.plugins.patientview.data.repository.PatientDao;
import net.frontlinesms.ui.i18n.InternationalisationUtils;

import org.springframework.context.ApplicationContext;

public class DataGeneratorThread extends Thread{
	
		private int chwNum;
		private int perCHWPatientNum;
		private boolean createForms; 
		private int formResponseNum; 
		private int smsMessagesNum;
		private ApplicationContext appCon;
		private DateFormat df = InternationalisationUtils.getDateFormat();
		private Group chwgroup;
		private DummyDataGenerator parentController;
		
		public DataGeneratorThread(int chwNum, int perCHWPatientNum, boolean createForms,  int formResponseNum, int smsMessagesNum, ApplicationContext appCon, DummyDataGenerator parentController){
			this.chwNum = chwNum;
			this.perCHWPatientNum = perCHWPatientNum;
			this.createForms = createForms;
			this.formResponseNum = formResponseNum;
			this.smsMessagesNum = smsMessagesNum;
			this.parentController = parentController;
			this.appCon = appCon;
		}
		
		@Override
		public void run(){
			CommunityHealthWorkerDao chwDao = (CommunityHealthWorkerDao) appCon.getBean("CHWDao");
			PatientDao patientDao = (PatientDao) appCon.getBean("PatientDao");
			log("Creating dummy data..");
			// list of first names
			String[] firsts = { "Dieterich", "Dolores", "Freddy", "Alex",
					"Charlie", "Lindsay", "Winnie", "Terrence", "Wilson", "Jenny",
					"Meghan", "Katherine", "Poe", "Phillip", "Andrew", "Elizabeth",
					"Whitney", "Frank", "Jared", "Maximillian", "Wylie","Theodore", "Margot",
					"Forscythe","Lars","Sarah","Teddy","Fitz","Humphrey","James","Mark","Jesse"};
			// list of last names
			String[] lasts = { "Lawson", "Threadbare", "Evermore", "Brown",
					"DeWilliams", "Taraban", "Polombo", "Benter", "Trought",
					"Finkley", "Coriander", "Groesbeck", "Trounce", "Longbottom",
					"Yip", "Fiars", "Trunch", "Whelp", "Schy", "Munificent",
					"Coyote","Brown","Black","Ames","Chavez","Richards","Phillips","Ballard"
					,"Roosevelt","Jackson","Trueblood","Wachowsky","Corlogne" };
			
			ArrayList<CommunityHealthWorker> chws = new ArrayList<CommunityHealthWorker>();
			ArrayList<Patient> ps = new ArrayList<Patient>();
			//create the group
			// create 80 chws with 100 patients each
			for (int i = 0; i < chwNum; i++) {
				String name = firsts[rand.nextInt(firsts.length)] + " " + lasts[rand.nextInt(lasts.length)];
				Gender gender = (rand.nextBoolean()) ? Gender.MALE:Gender.FEMALE;
				CommunityHealthWorker chw = new CommunityHealthWorker(name, getRandomNumber(),gender,getRandomDate());
				chws.add(chw);
				ArrayList<Patient> patients = new ArrayList<Patient>();
				//create 100 patients for each chw
				for (int j = 0; j < perCHWPatientNum; j++) {
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
			GroupDao gd = (GroupDao) appCon.getBean("groupDao");
			chwgroup = new Group(rootGroup,"Community Health Workers");
			try {
				gd.saveGroup(chwgroup);
			} catch (DuplicateKeyException e1) {
				e1.printStackTrace();
			}
			GroupMembershipDao gmd = (GroupMembershipDao) appCon.getBean("groupMembershipDao");
			for(CommunityHealthWorker chw:chws){
				gmd.addMember(chwgroup, chw.getContactInfo());
			}
			
			log("Group created.");
			ArrayList<MedicForm> forms = new ArrayList<MedicForm>();
			if(createForms){
				log("Creating forms...");
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
				f0.addField(new MedicFormField(f0, DataType.WRAPPED_TEXT, "Labelling"));
				f0.addField(new MedicFormField(f0, DataType.CHECK_BOX, "Field 2"));
				f0.addField(new MedicFormField(f0, DataType.CHECK_BOX, "Field 1"));
				f0.addField(new MedicFormField(f0, DataType.CHECK_BOX, "Field 2"));
				f0.addField(new MedicFormField(f0, DataType.TRUNCATED_TEXT, "More Labelling"));
				f0.addField(new MedicFormField(f0, DataType.CHECK_BOX, "Field 3"));
				f0.addField(new MedicFormField(f0, DataType.CHECK_BOX, "Field 4"));
				f0.addField(new MedicFormField(f0, DataType.CHECK_BOX, "Field 5"));
				f0.addField(new MedicFormField(f0, DataType.CHECK_BOX, "Field 6"));
				f0.addField(new MedicFormField(f0, DataType.CHECK_BOX, "Field 7"));
				f0.addField(new MedicFormField(f0, DataType.CHECK_BOX, "Field 8"));
				f0.addField(new MedicFormField(f0, DataType.CHECK_BOX, "Field 9"));
				f0.addField(new MedicFormField(f0, DataType.CHECK_BOX, "Field 10"));
				f0.addField(new MedicFormField(f0, DataType.CHECK_BOX, "Field 11"));
				f0.addField(new MedicFormField(f0, DataType.CHECK_BOX, "Field 12"));
				f0.addField(new MedicFormField(f0, DataType.CHECK_BOX, "Field 13"));
				f0.addField(new MedicFormField(f0, DataType.CHECK_BOX, "Field 14"));
				f0.addField(new MedicFormField(f0, DataType.CHECK_BOX, "Field 15"));
				f0.addField(new MedicFormField(f0, DataType.CHECK_BOX, "Field 16"));
				f0.addField(new MedicFormField(f0, DataType.CHECK_BOX, "Field 17"));
				f0.addField(new MedicFormField(f0, DataType.CHECK_BOX, "Field 18"));
				f0.addField(new MedicFormField(f0, DataType.CHECK_BOX, "Field 19"));
				f0.addField(new MedicFormField(f0, DataType.CHECK_BOX, "Field 20"));
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
				log("Forms created.");	
				log("Creating form responses...");
			}
			ArrayList<MedicFormResponse> fresponses = new ArrayList<MedicFormResponse>();
			
			for (MedicForm f : forms) {
				for (int i = 0; i < formResponseNum; i++) {
					//randomly select a Patient/CHW pair
					Patient p = ps.get(rand.nextInt(ps.size()));
					CommunityHealthWorker  chw = p.getChw();
					boolean fudge = oneInFifty();
					//generate response values
					List<MedicFormFieldResponse> rvs = new ArrayList<MedicFormFieldResponse>();
					for (MedicFormField ff : f.getFields()) {
						//if the field is a checkbox, randomly answer yes and no
						if (ff.getDatatype() == DataType.CHECK_BOX) {
							rvs.add(new MedicFormFieldResponse(new Boolean(rand.nextBoolean()).toString(), ff,p,chw));
						} else if (ff.getDatatype() == DataType.TEXT_FIELD) {
							if (ff.getLabel().equals("Patient Name") && !fudge) {
								rvs.add(new MedicFormFieldResponse(p.getName(), ff,p,chw));
							} else if(ff.getLabel().equals("Patient Name") && fudge){
								rvs.add(new MedicFormFieldResponse(fudgeName(p.getName()), ff,p,chw));
							}else {
								rvs.add(new MedicFormFieldResponse("response for text field",ff,p,chw));
							}
						} else if (ff.getDatatype() == DataType.TEXT_AREA) {
							rvs.add(new MedicFormFieldResponse(
									"I'm entering additional notes for a text area", ff,p,chw));
						} else if (ff.getDatatype() == DataType.DATE_FIELD) {
							if (ff.getLabel().equals("Patient Birthdate") && !fudge) {
								rvs.add(new MedicFormFieldResponse(df.format(p.getBirthdate()), ff,p,chw));
							} else if (ff.getLabel().equals("Patient Birthdate") && !fudge) {
								rvs.add(new MedicFormFieldResponse(fudgeDate(p), ff,p,chw));
							}else{
								rvs.add(new MedicFormFieldResponse(df.format(getRandomDate()), ff,p,chw));
							}
						}else if(ff.getDatatype() == DataType.NUMERIC_TEXT_FIELD){
							rvs.add(new MedicFormFieldResponse(rand.nextInt(500)+"" , ff,p,chw));	
						}
					}
					
					MedicFormResponse fr = new MedicFormResponse(f, rvs,chw,fudge? null:p);
					Date dsumbitted= getRandomDate();
					fr.setDateSubmitted(dsumbitted);
					for(MedicFormFieldResponse mfr: fr.getResponses()){
						mfr.setDateSubmitted(dsumbitted);
					}
					fresponses.add(fr);
				}
			}
			if(fresponses.size() >0){
				log("Form Responses Created");
			}
			
			ArrayList<MedicMessageResponse> mresponse = new ArrayList<MedicMessageResponse>();
			ArrayList<FrontlineMessage> messages = new ArrayList<FrontlineMessage>();
			for(int g = 0; g< smsMessagesNum; g++){
				Patient subject = ps.get(rand.nextInt(ps.size()));
				CommunityHealthWorker submitter = subject.getChw();
				String mess ="This is a freeform text message from " + submitter.getName() + " about " + subject.getName();
				Date newDate = getRandomDate();
				FrontlineMessage m = FrontlineMessage.createIncomingMessage(newDate.getTime(), submitter.getPhoneNumber(), "8004329", mess);
				MedicMessageResponse message = new MedicMessageResponse(m, mess,submitter,subject);
				message.setDateSubmitted(newDate);
				messages.add(m);
				mresponse.add(message);
			}
			log("Messages Created");
			
			if(forms.size() > 0){
				MedicFormDao formDao = (MedicFormDao) appCon.getBean("MedicFormDao");
				log("All data created, beginning to save Forms");
				for(MedicForm f: forms){
					try{
						formDao.saveMedicForm(f);
					}catch(Throwable t){
						t.printStackTrace();
					}
				}
				log("Forms saved");
			}
			
			int count = 0;
			
			if(fresponses.size() >0){
				MedicFormResponseDao formResponseDao = (MedicFormResponseDao) appCon.getBean("MedicFormResponseDao");
				log("Saving Form responses...");
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
				log("Form Responses saved");
			}
			count = 0;
			if(messages.size() > 0){
			log("Saving messages...");
			MessageDao messageDao = (MessageDao) appCon.getBean("messageDao");
				for(FrontlineMessage m: messages){
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
				log("Messages saved");
				log("Messages saved, saving message responses");
				count = 0;
				MedicMessageResponseDao messageResponseDao = (MedicMessageResponseDao) appCon.getBean("MedicMessageResponseDao");
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
			}
			log("Data creation done, starting PatientView for the first time");
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			parentController.creationDone();
		}
		

		/**
		 * Creates a FrontlineSMS form from a Medic form.
		 * Also finalizes the form after setting it's group to the CHW
		 * group
		 * @param mf
		 */
		private void createFSMSFormFromMedicForm(MedicForm mf){
			FormDao vanillaFormDao = (FormDao) appCon.getBean("formDao");
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
		private Random rand = new Random();

		
		/**
		 * Used to create random birthdates
		 * @return A date between today and 40 years ago
		 */
		private Date getRandomDate(){
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
		
		private boolean oneInFifty(){
			return rand.nextInt(26) == 25;
		}
		
		private void log(String text){
			parentController.log(text);
		}
		
		private String fudgeName(String name){
			name = name.replace("v", "b");
			name = name.replace("d", "t");
			name = name.replace("th", "f");
			name = name.replace("m", "n");
			name = name.replace("ll", "y");
			name = name.replace("ay", "ey");
			return name;
		}
		
		private String fudgeDate(Patient p){
			Date d = p.getBirthdate();
			Date newDate = new Date(d.getTime() + 86400000L);
			return df.format(newDate);
		}
			
			
}
