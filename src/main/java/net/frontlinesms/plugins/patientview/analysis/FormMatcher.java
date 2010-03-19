package net.frontlinesms.plugins.patientview.analysis;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

import net.frontlinesms.plugins.forms.data.domain.Form;
import net.frontlinesms.plugins.forms.data.domain.FormResponse;
import net.frontlinesms.plugins.patientview.data.domain.framework.MedicForm;
import net.frontlinesms.plugins.patientview.data.domain.framework.MedicFormField;
import net.frontlinesms.plugins.patientview.data.domain.framework.MedicFormField.PatientFieldMapping;
import net.frontlinesms.plugins.patientview.data.domain.people.CommunityHealthWorker;
import net.frontlinesms.plugins.patientview.data.domain.people.Patient;
import net.frontlinesms.plugins.patientview.data.repository.CommunityHealthWorkerDao;
import net.frontlinesms.plugins.patientview.data.repository.MedicFormDao;
import net.frontlinesms.plugins.patientview.data.repository.PatientDao;

import org.springframework.context.ApplicationContext;

import uk.ac.shef.wit.simmetrics.similaritymetrics.JaroWinkler;
import uk.ac.shef.wit.simmetrics.similaritymetrics.Levenshtein;

public class FormMatcher {

	
	private Levenshtein levenshtein;
	private JaroWinkler jaroWinkler;
	private static FormMatcher formMatcher;
	private ApplicationContext applicationContext;
	
	public FormMatcher(ApplicationContext appCon){
		this.applicationContext= appCon;
	}
	
	public boolean isMedicForm(Form f){
		return ((MedicFormDao) applicationContext.getBean("MedicFormDao")).getMedicFormForForm(f) !=null;
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
			if(formField.getMapping() == PatientFieldMapping.NAMEFIELD){
				for(int i = 0; i < patients.size(); i++){
					scores[i] += getNameDistance(patients.get(i).getName(),formResponse.getResults().get(formField.getPosition()).toString());
				}
				numberOfFields++;
			//if it is mapped to an id field, score it as an ID
			}else if(formField.getMapping() == PatientFieldMapping.IDFIELD){
				for(int i = 0; i < patients.size(); i++){
					scores[i] += getEditDistance(patients.get(i).getStringID(),formResponse.getResults().get(formField.getPosition()).toString());
				}
				numberOfFields++;
			//if it is mapped as a bday field, score it as a bday
			}else if(formField.getMapping() == PatientFieldMapping.BIRTHDATEFIELD){
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
	public float getNameDistance(String patientName, String responseName){
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
	 * @param patients the patients whose birthdates will be compared
	 * @param stringDate the compare date (generally the date typed into the form)
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
