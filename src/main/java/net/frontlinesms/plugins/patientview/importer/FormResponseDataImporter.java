package net.frontlinesms.plugins.patientview.importer;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.frontlinesms.plugins.patientview.data.domain.framework.MedicForm;
import net.frontlinesms.plugins.patientview.data.domain.framework.MedicFormField;
import net.frontlinesms.plugins.patientview.data.domain.framework.MedicFormField.PatientFieldMapping;
import net.frontlinesms.plugins.patientview.data.domain.people.CommunityHealthWorker;
import net.frontlinesms.plugins.patientview.data.domain.people.Patient;
import net.frontlinesms.plugins.patientview.data.domain.people.Person;
import net.frontlinesms.plugins.patientview.data.domain.people.User;
import net.frontlinesms.plugins.patientview.data.domain.response.MedicFormFieldResponse;
import net.frontlinesms.plugins.patientview.data.domain.response.MedicFormResponse;
import net.frontlinesms.plugins.patientview.data.repository.CommunityHealthWorkerDao;
import net.frontlinesms.plugins.patientview.data.repository.MedicFormDao;
import net.frontlinesms.plugins.patientview.data.repository.MedicFormFieldDao;
import net.frontlinesms.plugins.patientview.data.repository.MedicFormResponseDao;
import net.frontlinesms.plugins.patientview.data.repository.PatientDao;
import net.frontlinesms.plugins.patientview.data.repository.UserDao;
import net.frontlinesms.plugins.patientview.importer.validation.CsvValidationException;
import net.frontlinesms.plugins.patientview.importer.validation.FormResponseCsvValidator;
import net.frontlinesms.ui.UiGeneratorController;
import net.frontlinesms.ui.i18n.InternationalisationUtils;

import org.springframework.context.ApplicationContext;

import thinlet.Thinlet;
import au.com.bytecode.opencsv.CSVReader;
import static net.frontlinesms.ui.i18n.InternationalisationUtils.*;

public class FormResponseDataImporter implements CsvDataImporter{
	
	private UiGeneratorController uiController;
	
	private PatientDao patientDao;
	
	private CommunityHealthWorkerDao chwDao;
	
	private UserDao userDao;
	
	private MedicFormResponseDao responseDao;
	
	private MedicFormDao formDao;
	
	private MedicFormFieldDao fieldDao;
	
	private FormResponseCsvValidator validator;
	
	private Object messageList;
	private Object formComboBox;
	
	public FormResponseDataImporter(Object messageList, UiGeneratorController uiController,ApplicationContext appCon){
		this.uiController = uiController;
		this.messageList = messageList;
		this.patientDao = (PatientDao) appCon.getBean("PatientDao");
		this.chwDao = (CommunityHealthWorkerDao) appCon.getBean("CHWDao");
		this.userDao = (UserDao) appCon.getBean("UserDao");
		this.formDao = (MedicFormDao) appCon.getBean("MedicFormDao");
		this.fieldDao = (MedicFormFieldDao) appCon.getBean("MedicFormFieldDao");
		this.responseDao = (MedicFormResponseDao) appCon.getBean("MedicFormResponseDao");
		validator = new FormResponseCsvValidator(appCon);
	}
	
	public Object getInformationPanel(){
		Object panel = uiController.createPanel("");
		uiController.setColumns(panel, 1);
		uiController.add(panel,uiController.createLabel(getI18NString("medic.importer.labels.column") + " 1: "+getI18NString("medic.importer.forms.id.info")));
		uiController.add(panel,uiController.createLabel(getI18NString("medic.importer.forms.remaining.columns") + " " +getI18NString("medic.importer.forms.form.response.data")));
		return panel;
	}
	public Object getAdditionalOptionsPanel() {
		Object panel = uiController.createPanel("");
		uiController.setColumns(panel,3);
		uiController.setWeight(panel, 1, 0);
		uiController.setGap(panel,5);
		uiController.add(panel,uiController.createLabel(getI18NString("medic.importer.import.form.select")));
		formComboBox=  uiController.create(Thinlet.COMBOBOX);
		uiController.setWeight(formComboBox, 1, 0);
		for(MedicForm mf: formDao.getAllMedicForms()){
			uiController.add(formComboBox, uiController.createComboboxChoice(mf.getName(), mf));
			if(uiController.getSelectedIndex(formComboBox) == -1){
				uiController.setSelectedIndex(formComboBox, 0);
				uiController.setText(formComboBox, mf.getName());
			}
		}
		uiController.add(panel,formComboBox);
		Object spacer = uiController.createLabel("");
		uiController.setWeight(spacer, 2, 0);
		uiController.add(panel,spacer);
		return panel;
	}

	public void importFile(String path) {
		//set the form for the validator
		addMessageToList(getI18NString("medic.importer.beginning.message")+ ": " + path);
		validator.setForm(getCurrentlySelectedForm());
		try {
			List<CsvValidationException> exceptions = validator.validateFile(path);
			if(exceptions.size() != 0){
				for(CsvValidationException e : exceptions){
					addMessageToList(e.toString());
				}
			}else{
				CSVReader reader = new CSVReader(new FileReader(path));
				String[] currLine;
				List<MedicFormResponse> responses = new ArrayList<MedicFormResponse>();
				int lineNumber = 0;
				try {
					while((currLine = reader.readNext()) != null){
						//determine the submitter of the form
						Person submitter = null;
						List<CommunityHealthWorker> chws  = chwDao.findCommunityHealthWorkerByName(currLine[0], -1);
						if(chws.size() != 1){
							List<User> users = userDao.getUsersByName(currLine[0], -1);
							if(users.size() != 1){
								List<User> usernames = userDao.findUsersByUsername(currLine[0]);
								if(usernames.size() != 1){
									try{
										long id=Long.parseLong(currLine[0]);
										CommunityHealthWorker chwById = chwDao.getCommunityHealthWorkerById(id);
										if(chwById == null){
											User userById = userDao.getUsersById(id);
											if(userById == null){
												exceptions.add(new CsvValidationException(lineNumber,getI18NString("medic.importer.unknown.submitter.error") + " \""+currLine[0]+"\""));
											}else{
												submitter = userById;
											}
										}else{
											submitter = chwById;
										}
									}catch(Exception e){
										exceptions.add(new CsvValidationException(lineNumber, getI18NString("medic.importer.unknown.submitter.error") + " \""+currLine[0]+"\""));
									}
								}else{
									submitter = usernames.get(0);
								}
							}else{
								submitter = users.get(0);
							}
						}else{
							submitter = chws.get(0);
						}
						//create the form response
						MedicFormResponse mfr = new MedicFormResponse(getCurrentlySelectedForm(),submitter,null);
						//get the list of fields off of the form
						List<MedicFormField> fields = fieldDao.getFieldsOnForm(getCurrentlySelectedForm());
						//prepare the id fields
						String name = null; String id = null; String birthdate = null;
						//iterate over the fields on the form, creating field responses as we go
						for(int i = 1; i < fields.size()+1;i++){
							MedicFormField field = fields.get(i-1);
							//check for mappings
							if(field.getMapping() == PatientFieldMapping.NAMEFIELD){
								name = currLine[i];
							}else if(field.getMapping() == PatientFieldMapping.BIRTHDATEFIELD){
								birthdate = currLine[i];
							}else if(field.getMapping() == PatientFieldMapping.IDFIELD){
								id = currLine[i];
							}
							
							MedicFormFieldResponse mffr = new MedicFormFieldResponse(currLine[i], field, null, submitter);
							mfr.addFieldResponse(mffr);
						}
						try{
							Patient p = patientDao.findPatient(name, birthdate, id);
							if(p == null){
								exceptions.add(new CsvValidationException(lineNumber,getI18NString("medic.importer.unknown.subject.error")));
							}else{
								mfr.setSubject(p);
							}
						}catch(Exception e){
							exceptions.add(new CsvValidationException(lineNumber,getI18NString("medic.importer.unknown.subject.error")));
						}
						
						responseDao.saveMedicFormResponse(mfr);
					}
				}catch(Exception e){
					addMessageToList(getI18NString("medic.importer.file.parsing.error"));
					addMessageToList(e.toString());
				}
				if(exceptions.size() == 0){
					addMessageToList("====== "+getI18NString("medic.common.form.response")+" " +getI18NString("medic.importer.creation.complete")+" ======");
					addMessageToList(lineNumber + getI18NString("medic.common.form.responses")+ " " +getI18NString("medic.importer.success.message"));
				}
			}
		} catch (FileNotFoundException e) {
			addMessageToList(getI18NString("medic.importer.file.not.found"));
			addMessageToList(e.toString());
		}
	}
	
	private void addMessageToList(String message){
		String text = uiController.getText(messageList);
		String newLine = "["+getI18NString("medic.common.form.response")+ " "+getI18NString("medic.data.importer") +"] "+InternationalisationUtils.getDatetimeFormat().format(new Date()) + " - " + message;
		text += "\n"+newLine;
		uiController.setText(messageList, text);
	}
	
	private MedicForm getCurrentlySelectedForm(){
		return uiController.getAttachedObject(uiController.getSelectedItem(formComboBox),MedicForm.class);
	}

	public String getTypeLabel() {
		return "Form Responses";
	}
	
}

