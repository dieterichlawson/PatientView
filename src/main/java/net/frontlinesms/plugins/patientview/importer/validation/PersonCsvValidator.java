package net.frontlinesms.plugins.patientview.importer.validation;

import static net.frontlinesms.ui.i18n.InternationalisationUtils.getI18NString;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.frontlinesms.ui.i18n.InternationalisationUtils;
import au.com.bytecode.opencsv.CSVReader;

public abstract class PersonCsvValidator extends CsvValidator{

	
	protected static String[] genderPossibilities = new String[]{getI18NString("medic.common.male"),
																getI18NString("medic.common.female"),
																getI18NString("medic.common.transgender")};
	
	protected static final int NAME_INDEX = 0;
	protected static final int BDAY_INDEX = 1;
	protected static final int GENDER_INDEX = 2;
	
	@Override
	public List<CsvValidationException> validate(CSVReader reader) {
		String[] currLine;
		List<CsvValidationException> exceptions = new ArrayList<CsvValidationException>();
		int lineNumber = 0;
		try {
			while((currLine = reader.readNext()) != null){
				lineNumber++;
				//check the name
				if(currLine[NAME_INDEX] == null || currLine[NAME_INDEX].equals("") ){
					exceptions.add(new CsvValidationException(lineNumber, getI18NString("medic.importer.blank.chw.name")));
				}
				//check the birthdate
				try{
					InternationalisationUtils.getDateFormat().parse(currLine[BDAY_INDEX]);
				}catch(Exception e){
					exceptions.add(new CsvValidationException(lineNumber, getI18NString("medic.importer.date.format.error")+ ": \""+currLine[BDAY_INDEX]+"\""));
				}
				//check gender
				boolean validGender = false;
				for(String gender: genderPossibilities){
					if(currLine[GENDER_INDEX].equalsIgnoreCase(gender)){
						validGender = true;
					}
				}
				if(!validGender){
					exceptions.add(new CsvValidationException(lineNumber, getI18NString("medic.importer.gender.format.error")+": \""+currLine[GENDER_INDEX]+"\""));
				}
				doAdditionalValidation(lineNumber,currLine,exceptions);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return exceptions;
	}
	
	public abstract void doAdditionalValidation(int lineNumber, String[] line,List<CsvValidationException> exceptions);

}
