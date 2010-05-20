package net.frontlinesms.plugins.patientview.importer.validation;

import java.util.Collection;
import java.util.List;

import net.frontlinesms.plugins.patientview.data.domain.people.CommunityHealthWorker;
import net.frontlinesms.plugins.patientview.data.repository.CommunityHealthWorkerDao;

import org.springframework.context.ApplicationContext;
import static net.frontlinesms.ui.i18n.InternationalisationUtils.*;

public class PatientCsvValidator extends PersonCsvValidator{

	protected CommunityHealthWorkerDao chwDao;
	 
	protected static final int CHW_INDEX = 3;
	
	public PatientCsvValidator(ApplicationContext appCon) {
		super();
		chwDao = (CommunityHealthWorkerDao) appCon.getBean("CHWDao");
	}

	@Override
	public void doAdditionalValidation(int lineNumber, String[] line, List<CsvValidationException> exceptions) {
		Collection<CommunityHealthWorker> chws = chwDao.getCommunityHealthWorkerByName(line[CHW_INDEX], -1);
		if(chws.size() == 0){
			exceptions.add(new CsvValidationException(lineNumber, getI18NString("medic.importer.no.chw.association.error")+" \"" + line[CHW_INDEX]+"\""));
		}else if(chws.size() >1){
			exceptions.add(new CsvValidationException(lineNumber, getI18NString("medic.importer.multiple.chw.association.error")+" \"" + line[CHW_INDEX]+"\""));
		}
	}
}
