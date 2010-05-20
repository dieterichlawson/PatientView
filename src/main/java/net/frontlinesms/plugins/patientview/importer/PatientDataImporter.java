package net.frontlinesms.plugins.patientview.importer;

import static net.frontlinesms.ui.i18n.InternationalisationUtils.getI18NString;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.frontlinesms.FrontlineSMSConstants;
import net.frontlinesms.plugins.patientview.data.domain.people.CommunityHealthWorker;
import net.frontlinesms.plugins.patientview.data.domain.people.Patient;
import net.frontlinesms.plugins.patientview.data.domain.people.Person.Gender;
import net.frontlinesms.plugins.patientview.data.repository.CommunityHealthWorkerDao;
import net.frontlinesms.plugins.patientview.data.repository.PatientDao;
import net.frontlinesms.plugins.patientview.importer.validation.CsvValidationException;
import net.frontlinesms.plugins.patientview.importer.validation.PatientCsvValidator;
import net.frontlinesms.ui.UiGeneratorController;
import net.frontlinesms.ui.i18n.InternationalisationUtils;

import org.springframework.context.ApplicationContext;

import au.com.bytecode.opencsv.CSVReader;

public class PatientDataImporter implements CsvDataImporter{

	private UiGeneratorController uiController;
	
	private PatientDao patientDao;
	
	private CommunityHealthWorkerDao chwDao;
	
	private PatientCsvValidator validator;
	
	private Object messageList;
	
	public PatientDataImporter(Object messageList, UiGeneratorController uiController,ApplicationContext appCon){
		this.uiController = uiController;
		this.messageList = messageList;
		this.patientDao = (PatientDao) appCon.getBean("PatientDao");
		this.chwDao = (CommunityHealthWorkerDao) appCon.getBean("CHWDao");
		validator = new PatientCsvValidator(appCon);
	}
	
	public Object getAdditionalOptionsPanel() {
		AdditionalInfoPanel infoPanel = new AdditionalInfoPanel(uiController);
		String patient = getI18NString("medic.common.patient");
		infoPanel.addLine(patient+ " " + getI18NString("simplesearch.fields.name"));
		infoPanel.addLine(patient+ " " + getI18NString("simplesearch.fields.birthdate")+" ("+getI18NString(FrontlineSMSConstants.DATEFORMAT_YMD)+")");
		infoPanel.addLine(patient+ " " + getI18NString("simplesearch.fields.gender")+" (" +getI18NString("medic.common.male")+", " +getI18NString("medic.common.female")+", or " +getI18NString("medic.common.transgender") +")");
		infoPanel.addLine(getI18NString("medic.importer.patient.chw.info"));
		return infoPanel.getMainPanel();
	}

	public void importFile(String path) {
		addMessageToList(getI18NString("medic.importer.beginning.message")+": " + path);
		try {
			List<CsvValidationException> exceptions = validator.validateFile(path);
			if(exceptions.size() != 0){
				for(CsvValidationException e : exceptions){
					addMessageToList(e.toString());
				}
			}else{
				CSVReader reader = new CSVReader(new FileReader(path));
				String[] currLine;
				List<Patient> patients = new ArrayList<Patient>();
				int lineNumber = 0;
				try {
					while((currLine = reader.readNext()) != null){
						List<CommunityHealthWorker> chw = chwDao.getCommunityHealthWorkerByName(currLine[3],-1);
						if(chw.size() == 1){
							Patient patient  = new Patient(chw.get(0),currLine[0],parseGender(currLine[2]),InternationalisationUtils.getDateFormat().parse(currLine[1]));
							patientDao.savePatient(patient);
						}else{
							addMessageToList(getI18NString("medic.importer.line")+" " + lineNumber+ ": "+ getI18NString("medic.importer.patient.chw.parsing.error"));
						}
						lineNumber ++;
					}
				}catch(Exception e){
					addMessageToList(getI18NString("medic.importer.file.parsing.error"));
					addMessageToList(e.toString());
				}
				if(exceptions.size() == 0){
					addMessageToList("====== "+getI18NString("medic.common.patient")+" " +getI18NString("medic.importer.creation.complete")+" ======");
					addMessageToList(lineNumber + getI18NString("medic.common.patients")+ " " +getI18NString("medic.importer.success.message"));
				}
			}
		} catch (FileNotFoundException e) {
			addMessageToList(getI18NString("medic.importer.file.not.found"));
			addMessageToList(e.toString());
		}
	}
	
	private void addMessageToList(String message){
		String text = uiController.getText(messageList);
		String newLine = "["+getI18NString("medic.common.patient")+ " "+getI18NString("medic.data.importer") +"] "+InternationalisationUtils.getDatetimeFormat().format(new Date()) + " - " + message;
		text += "\n"+newLine;
		uiController.setText(messageList, text);
	}
	
	private Gender parseGender(String gender){
		String male = getI18NString("medic.common.male");
		String female = getI18NString("medic.common.female");
		String transGender = getI18NString("medic.common.transgender");
		if(gender.equalsIgnoreCase(male)){
			return Gender.MALE;
		}else if(gender.equalsIgnoreCase(female)){
			return Gender.FEMALE;
		}else if(gender.equalsIgnoreCase(transGender)){
			return Gender.TRANSGENDER;
		}
		return null;
	}

	public String getTypeLabel() {
		return "Patients";
	}

}
