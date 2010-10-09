package net.frontlinesms.plugins.patientview.responsemapping;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.frontlinesms.FrontlineSMSConstants;
import net.frontlinesms.FrontlineUtils;
import net.frontlinesms.data.events.EntitySavedNotification;
import net.frontlinesms.events.EventBus;
import net.frontlinesms.events.EventObserver;
import net.frontlinesms.events.FrontlineEventNotification;
import net.frontlinesms.plugins.forms.data.domain.Form;
import net.frontlinesms.plugins.forms.data.domain.FormResponse;
import net.frontlinesms.plugins.forms.data.domain.ResponseValue;
import net.frontlinesms.plugins.forms.data.repository.FormDao;
import net.frontlinesms.plugins.patientview.data.domain.framework.MedicForm;
import net.frontlinesms.plugins.patientview.data.domain.framework.MedicFormField.PatientFieldMapping;
import net.frontlinesms.plugins.patientview.data.domain.people.CommunityHealthWorker;
import net.frontlinesms.plugins.patientview.data.domain.people.Patient;
import net.frontlinesms.plugins.patientview.data.domain.people.Person;
import net.frontlinesms.plugins.patientview.data.domain.response.MedicFormFieldResponse;
import net.frontlinesms.plugins.patientview.data.domain.response.MedicFormResponse;
import net.frontlinesms.plugins.patientview.data.repository.CommunityHealthWorkerDao;
import net.frontlinesms.plugins.patientview.data.repository.MedicFormDao;
import net.frontlinesms.plugins.patientview.data.repository.MedicFormFieldResponseDao;
import net.frontlinesms.plugins.patientview.data.repository.MedicFormResponseDao;
import net.frontlinesms.plugins.patientview.data.repository.PatientDao;
import net.frontlinesms.ui.ExtendedThinlet;
import net.frontlinesms.ui.i18n.InternationalisationUtils;

import org.apache.log4j.Logger;
import org.jfree.util.Log;
import org.springframework.context.ApplicationContext;

import uk.ac.shef.wit.simmetrics.similaritymetrics.JaroWinkler;
import uk.ac.shef.wit.simmetrics.similaritymetrics.Levenshtein;

public class IncomingFormMatcher implements EventObserver{

	private static Logger LOG = FrontlineUtils.getLogger(MedicFormResponse.class);
	
	private Levenshtein levenshtein;
	private JaroWinkler jaroWinkler;
	
	private FormDao vanillaFormDao;
	private MedicFormDao formDao;
	private MedicFormResponseDao formResponseDao;
	private MedicFormFieldResponseDao formFieldResponseDao;
	private PatientDao patientDao;
	private CommunityHealthWorkerDao chwDao;
	
	private SimpleDateFormat shortFormatter;
	private DateFormat longFormatter;
	
	public IncomingFormMatcher(ApplicationContext appCon){
		vanillaFormDao = (FormDao) appCon.getBean("formDao");
		formDao = (MedicFormDao) appCon.getBean("MedicFormDao");
		formResponseDao = (MedicFormResponseDao) appCon.getBean("MedicFormResponseDao");
		formFieldResponseDao = (MedicFormFieldResponseDao) appCon.getBean("MedicFormFieldResponseDao");
		patientDao = (PatientDao) appCon.getBean("PatientDao");
		chwDao = (CommunityHealthWorkerDao) appCon.getBean("CHWDao");
		((EventBus) appCon.getBean("eventBus")).registerObserver(this);
		//create the test harness
		ExtendedThinlet thinlet = new ExtendedThinlet();
		Object panel = thinlet.createPanel("mainPanel");
		thinlet.setWeight(panel, 1, 1);
		Object button = thinlet.createButton("Click me to test form handling");
		thinlet.setAction(button, "testHandler", null, this);
		thinlet.add(panel,button);
		thinlet.add(panel);
		//set up the date formatter
		String dateString = InternationalisationUtils.getI18NString(FrontlineSMSConstants.DATEFORMAT_YMD);
		dateString = dateString.toLowerCase();
		dateString = dateString.replace("mm", "MM");
		dateString = dateString.replace("yyyy", "yy");
		shortFormatter = new SimpleDateFormat(dateString);
		longFormatter = InternationalisationUtils.getDateFormat();
	//	FrameLauncher f = new FrameLauncher("Test form handling",thinlet,200,100,null)
		//{ public void windowClosing(WindowEvent e){  dispose(); }};
	}
	
	public void testHandler(){
		List<ResponseValue> responses = new ArrayList<ResponseValue>();
		responses.add(new ResponseValue("Frankie Tenday"));
		responses.add(new ResponseValue("16/06/1964"));
		responses.add(new ResponseValue("First answer"));
		responses.add(new ResponseValue("true"));
		responses.add(new ResponseValue("false"));
		responses.add(new ResponseValue("true"));
		responses.add(new ResponseValue("false"));
		responses.add(new ResponseValue("true"));
		responses.add(new ResponseValue("false"));
		responses.add(new ResponseValue("true"));
		responses.add(new ResponseValue("false"));
		responses.add(new ResponseValue("true"));
		responses.add(new ResponseValue("false"));
		responses.add(new ResponseValue("true"));
		responses.add(new ResponseValue("false"));
		responses.add(new ResponseValue("true"));
		responses.add(new ResponseValue("false"));
		responses.add(new ResponseValue("true"));
		responses.add(new ResponseValue("false"));
		responses.add(new ResponseValue("true"));
		responses.add(new ResponseValue("false"));
		responses.add(new ResponseValue("true"));
		responses.add(new ResponseValue("false"));
		responses.add(new ResponseValue("true"));
		FormResponse fr = new FormResponse("2099707079",vanillaFormDao.getFromId(10L),responses);
		handleFormResponse(fr);
	}
	
	public boolean isMedicForm(Form f){
		return formDao.getMedicFormForForm(f) != null;
	}
	
	/**
	 * When a form is submitted, this method attempts to pair that form with the patient that is its subject.
	 * If there is more than one possibility, the form is posted to the changelog with a list of
	 * suggested patients. If there is only one real possibility, but that possibility does
	 * not match exactly, it is also posted to the changelog with a snippet about what did not 
	 * match and what did.
	 * 
	 * @param formResponse
	 */
	public void handleFormResponse(FormResponse formResponse){
		//if the form submitted is not a medic form, then do nothing
		if(!isMedicForm(formResponse.getParentForm())){
			return;
		}
		//get the medic form equivalent of the form submitted
		MedicForm mForm = formDao.getMedicFormForForm(formResponse.getParentForm());
		CommunityHealthWorker submitter = chwDao.getCommunityHealthWorkerByPhoneNumber(formResponse.getSubmitter());
		MedicFormResponse mfr = new MedicFormResponse(formResponse,mForm,submitter,null);
		mfr.setSubject(getFinalCandidate(mfr));
		formResponseDao.saveMedicFormResponse(mfr);
	}
	
	
	/**
	 * Returns a float from 1.0 - 0.0 that measures the similarity between 2 strings (mainly names) using
	 * the jaro-winkler method. The higher the number, the greater the similarity
	 * @param patientName string 1 (generally the name of the patient)
	 * @param responseName	string 2 (generally the name typed into the form
	 * @return a float from 1.0 - 0.0
	 */
	private float getNameDistance(String patientName, String responseName){
		if(jaroWinkler == null){
			jaroWinkler = new JaroWinkler();
		}
		return jaroWinkler.getSimilarity(patientName, responseName);
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
	
	public List<Candidate> getCandidatesForResponse(MedicFormResponse response){
		Log.info("Attempting to map response");
		//get the CHW that submitted the form
		CommunityHealthWorker chw = (CommunityHealthWorker) response.getSubmitter();
		//get the list of patients that the CHW cares for
		ArrayList<Patient> patients = (ArrayList<Patient>) patientDao.getPatientsForCHW(chw);
		ArrayList<Candidate> candidates = new ArrayList<Candidate>();
		//iterate through all fields on the form, seeing if they are mapped to patient identifying fields
		//e.g. Birthdate, Name, and Patient ID
		for(Patient patient: patients){
			candidates.add(new Candidate(patient));
		}
		List<MedicFormFieldResponse> responses = response.getResponses();
		try{
			responses.get(0).getDateSubmitted();
		}catch(Exception e){
			responses = formFieldResponseDao.getResponsesForFormResponse(response);
		}
		
		for(MedicFormFieldResponse fieldResponse : responses){
			//if it is mapped to a namefield, score it as a name
			if(fieldResponse.getField().getMapping() == PatientFieldMapping.NAMEFIELD){
				for(Candidate c: candidates){
					c.setNameScore(getNameDistance(c.getName().toLowerCase(),fieldResponse.getValue().toLowerCase()));
				}
			//if it is mapped to an id field, score it as an ID
			}else if(fieldResponse.getField().getMapping() == PatientFieldMapping.IDFIELD){
				for(Candidate c: candidates){
					c.setIdScore(getEditDistance(c.getStringID(),fieldResponse.getValue()));
				}
			//if it is mapped as a bday field, score it as a bday
			}else if(fieldResponse.getField().getMapping() == PatientFieldMapping.BIRTHDATEFIELD){
				for(Candidate c: candidates){
					if(fieldResponse.getValue().length() <=8){
						c.setBirthdateScore(getEditDistance(shortFormatter.format(c.getPatient().getBirthdate()),fieldResponse.getValue()));
					}else{
						c.setBirthdateScore(getEditDistance(longFormatter.format(c.getPatient().getBirthdate()),fieldResponse.getValue()));
					}
				}
			}
		}
		Collections.sort(candidates);
		return candidates.subList(0, 5);
	}
	
	public float getConfidence(Patient subject, MedicFormResponse mfr){
		List<MedicFormFieldResponse> responses = formFieldResponseDao.getResponsesForFormResponse(mfr);
		float result = 0.0F;
		float total = 0.0F;
		for(MedicFormFieldResponse fieldResponse : responses){
			//if it is mapped to a namefield, score it as a name
			if(fieldResponse.getField().getMapping() == PatientFieldMapping.NAMEFIELD){
					result += getNameDistance(subject.getName(),fieldResponse.getValue());
					total += 1.0F;
			//if it is mapped to an id field, score it as an ID
			}else if(fieldResponse.getField().getMapping() == PatientFieldMapping.IDFIELD){			
					result += getEditDistance(subject.getStringID(),fieldResponse.getValue());
					total += 1.0F;
			//if it is mapped as a bday field, score it as a bday
			}else if(fieldResponse.getField().getMapping() == PatientFieldMapping.BIRTHDATEFIELD){
				if(fieldResponse.getValue().length() <=8){
					result += getEditDistance(shortFormatter.format(subject.getBirthdate()),fieldResponse.getValue());
				}else{
					result += getEditDistance(longFormatter.format(subject.getBirthdate()),fieldResponse.getValue());
				}
					total += 1.0F;
			}
		}
		return (result/total)*100 ;
	}
	
	/**
	 * Returns the 'final candidate', i.e. the most likely candidate for the subject
	 * of the supplied form response. This is determined by fetching all candidates,
	 * and selecting the ones that are over 97% confidence. If there is only one over 97%
	 * confidence, then that candidate is returned. Otherwise, this method returns null
	 * @param response
	 * @return
	 */
	public Person getFinalCandidate(MedicFormResponse response){
		List<Candidate> candidates = getCandidatesForResponse(response);
		List<Candidate> finalCandidates = new ArrayList<Candidate>();
		for(Candidate c: candidates){
			if(c.getAverageScore()  >= 97F){
				finalCandidates.add(c);
			}
		}
		if(finalCandidates.size() == 1){
			return finalCandidates.get(0).getPatient();
		}
		return null;
	}

	public void notify(FrontlineEventNotification notification) {
		if(notification instanceof EntitySavedNotification<?>){
			EntitySavedNotification<?> sNotification = (EntitySavedNotification<?>) notification;
			if(sNotification.getDatabaseEntity() instanceof FormResponse){
				handleFormResponse((FormResponse) sNotification.getDatabaseEntity());
			}
		}
	}
	
}
