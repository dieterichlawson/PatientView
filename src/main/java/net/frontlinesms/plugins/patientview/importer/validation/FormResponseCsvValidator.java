package net.frontlinesms.plugins.patientview.importer.validation;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import net.frontlinesms.plugins.patientview.data.domain.framework.DataType;
import net.frontlinesms.plugins.patientview.data.domain.framework.MedicForm;
import net.frontlinesms.plugins.patientview.data.domain.framework.MedicFormField;
import net.frontlinesms.plugins.patientview.data.domain.framework.MedicFormField.PatientFieldMapping;
import net.frontlinesms.plugins.patientview.data.domain.people.CommunityHealthWorker;
import net.frontlinesms.plugins.patientview.data.domain.people.Patient;
import net.frontlinesms.plugins.patientview.data.domain.people.User;
import net.frontlinesms.plugins.patientview.data.repository.CommunityHealthWorkerDao;
import net.frontlinesms.plugins.patientview.data.repository.MedicFormFieldDao;
import net.frontlinesms.plugins.patientview.data.repository.PatientDao;
import net.frontlinesms.plugins.patientview.data.repository.UserDao;
import net.frontlinesms.ui.i18n.InternationalisationUtils;

import org.springframework.context.ApplicationContext;

import au.com.bytecode.opencsv.CSVReader;
import static net.frontlinesms.ui.i18n.InternationalisationUtils.*;
public class FormResponseCsvValidator extends CsvValidator{
	
	protected List<MedicFormField> fields;
	
	protected String[] booleanValues = new String[]{getI18NString("datatype.true"),getI18NString("datatype.false")};
	
	protected PatientDao patientDao;
	
	protected CommunityHealthWorkerDao chwDao;
	protected UserDao userDao;
	
	protected ApplicationContext appCon;
	
	public FormResponseCsvValidator(ApplicationContext appCon){
		patientDao = (PatientDao) appCon.getBean("PatientDao");
		chwDao = (CommunityHealthWorkerDao) appCon.getBean("CHWDao");
		userDao = (UserDao) appCon.getBean("UserDao");
		this.appCon = appCon;
	}
	
	public void setForm(MedicForm form){
		fields = ((MedicFormFieldDao) appCon.getBean("MedicFormFieldDao")).getFieldsOnForm(form);
	}
	
	@Override
	public List<CsvValidationException> validate(CSVReader reader) {
		int lineNumber = 0;
		String[] currLine;
		List<CsvValidationException> exceptions = new ArrayList<CsvValidationException>();
		try {
			while((currLine = reader.readNext()) != null){
				if(currLine.length != fields.size() +1){
					exceptions.add(new CsvValidationException(lineNumber, getI18NString("medic.importer.column.mismatch.error")));
				}
				//determine the submitter of the form
				List<CommunityHealthWorker> chws  = chwDao.getCommunityHealthWorkerByName(currLine[0], -1);
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
										exceptions.add(new CsvValidationException(lineNumber,getI18NString("medic.importer.unknown.submitter.error")+" \""+currLine[0]+"\""));
									}
								}
							}catch(Exception e){
								exceptions.add(new CsvValidationException(lineNumber,getI18NString("medic.importer.unknown.submitter.error")+" \""+currLine[0]+"\""));
							}
						}
					}
				}
			
				String name = null; String id = null; String birthdate = null;
				for(int i = 1; i < fields.size();i++){
					MedicFormField field = fields.get(i-1);
					//check for mappings
					if(field.getMapping() == PatientFieldMapping.NAMEFIELD){
						name = currLine[i];
					}else if(field.getMapping() == PatientFieldMapping.BIRTHDATEFIELD){
						birthdate = currLine[i];
					}else if(field.getMapping() == PatientFieldMapping.IDFIELD){
						id = currLine[i];
					}
					
					if(field.getDatatype() == DataType.DATE_FIELD){
						validateDate(currLine[i], lineNumber,exceptions);
					}else if(field.getDatatype() == DataType.POSITIVENEGATIVE || 
							 field.getDatatype() == DataType.TRUEFALSE || 
							 field.getDatatype() == DataType.YESNO || 
							 field.getDatatype() == DataType.CHECK_BOX){
						validateBoolean(currLine[i], lineNumber,exceptions);
					}else{
						validateString(currLine[i], lineNumber,exceptions);
					}
		
				}
				Patient p = patientDao.getPatient(name, birthdate, id);
				if (p == null) {
					exceptions.add(new CsvValidationException(lineNumber,getI18NString("medic.importer.unknown.subject.error")));
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return exceptions;
	}
	
	public void validateDate(String date, int lineNumber, List<CsvValidationException> exceptions){
		try {
			InternationalisationUtils.getDateFormat().parse(date);
		} catch (ParseException e) {
			exceptions.add(new CsvValidationException(lineNumber,getI18NString("medic.importer.date.format.error")+": "+ date));
		}
	}
	
	public void validateBoolean(String value,int lineNumber, List<CsvValidationException> exceptions){
		boolean valid = false;
		for(String br: booleanValues){
			if(value.equalsIgnoreCase(br)){
				valid = true;
			}
		}
		if(!valid){
			exceptions.add(new CsvValidationException(lineNumber, getI18NString("medic.importer.boolean.format.error")+": "+ value));
		}
	}
	
	public void validateString(String value,int lineNumber, List<CsvValidationException> exceptions){
		if(value.length() > 255){
			exceptions.add(new CsvValidationException(lineNumber, getI18NString("medic.importer.response.length.error") + ": "+ value));
		}
	}	
}
