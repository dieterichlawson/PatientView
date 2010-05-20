package net.frontlinesms.plugins.patientview.importer.validation;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;

import au.com.bytecode.opencsv.CSVReader;

public abstract class CsvValidator {
	
	public List<CsvValidationException> validateFile(String filename) throws FileNotFoundException{
		CSVReader reader = new CSVReader(new FileReader(filename));
		return validate(reader);
	}
	
	public abstract List<CsvValidationException> validate(CSVReader reader);
}
