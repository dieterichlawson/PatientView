package net.frontlinesms.plugins.patientview.listener;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.frontlinesms.data.events.DatabaseEntityNotification;
import net.frontlinesms.data.events.EntitySavedNotification;
import net.frontlinesms.data.events.EntityUpdatedNotification;
import net.frontlinesms.events.EventBus;
import net.frontlinesms.events.EventObserver;
import net.frontlinesms.events.FrontlineEventNotification;
import net.frontlinesms.plugins.patientview.data.domain.flag.FlagDefinition;
import net.frontlinesms.plugins.patientview.data.domain.framework.DataType;
import net.frontlinesms.plugins.patientview.data.domain.framework.Field;
import net.frontlinesms.plugins.patientview.data.domain.framework.MedicFormField;
import net.frontlinesms.plugins.patientview.data.domain.framework.PersonAttribute;
import net.frontlinesms.plugins.patientview.data.domain.people.Patient;
import net.frontlinesms.plugins.patientview.data.domain.people.Person;
import net.frontlinesms.plugins.patientview.data.domain.response.MedicFormFieldResponse;
import net.frontlinesms.plugins.patientview.data.domain.response.MedicFormResponse;
import net.frontlinesms.plugins.patientview.data.domain.response.PersonAttributeResponse;
import net.frontlinesms.plugins.patientview.data.domain.response.Response;
import net.frontlinesms.plugins.patientview.data.repository.FieldDao;
import net.frontlinesms.plugins.patientview.data.repository.FlagDefinitionDao;
import net.frontlinesms.plugins.patientview.data.repository.MedicFormFieldResponseDao;
import net.frontlinesms.plugins.patientview.data.repository.PersonAttributeResponseDao;
import net.frontlinesms.ui.UiGeneratorController;

import org.hibernate.classic.ValidationFailure;
import org.springframework.context.ApplicationContext;

import bsh.EvalError;
import bsh.Interpreter;

public class PatientFlagListener implements EventObserver {
	
	private PersonAttributeResponseDao attributeResponseDao;
	private MedicFormFieldResponseDao formFieldResponseDao;
	private FlagDefinitionDao flagDefinitionDao;
	private FieldDao fieldDao;
	private UiGeneratorController uiController;
	
	public PatientFlagListener(ApplicationContext appCon, UiGeneratorController uiController){
		this.uiController = uiController;
		((EventBus) appCon.getBean("eventBus")).registerObserver(this);
		this.attributeResponseDao = (PersonAttributeResponseDao) appCon.getBean("PersonAttributeResponseDao");
		this.formFieldResponseDao = (MedicFormFieldResponseDao) appCon.getBean("MedicFormFieldResponseDao");
		this.flagDefinitionDao = (FlagDefinitionDao) appCon.getBean("FlagDefinitionDao");
		this.fieldDao = (FieldDao) appCon.getBean("FieldDao");
	}
	
	public void checkPatientFlags(Patient patient){
		List<FlagDefinition> flags = flagDefinitionDao.getAllFlagDefinitions();
		for(FlagDefinition flag: flags){
			String condition = flag.getFlagCondition();
			Pattern p = Pattern.compile("\\{([^}]*)\\}");
			Matcher matcher = p.matcher(condition);
			StringBuffer testableCondition = new StringBuffer();
			while(matcher.find()){
				//get the field id from the matcher
				long fieldId = Long.parseLong(matcher.toMatchResult().group().replace("{","").replace("}", ""));
					//attempt to retrieve the field by id
					Field f = fieldDao.getFieldById(fieldId);
					String replacement = "";
					if(f instanceof PersonAttribute){
						replacement = attributeResponseDao.getMostRecentAttributeResponse((PersonAttribute) f, patient).getValue();
					}else{
						replacement = formFieldResponseDao.getMostRecentFieldResponse((MedicFormField) f, patient).getValue();
					}
					if(f.getDatatype() == DataType.TEXT_AREA || f.getDatatype() == DataType.TEXT_FIELD){
						replacement = "\""+replacement +"\"";
					}
					matcher.appendReplacement(testableCondition, replacement);
			}
			matcher.appendTail(testableCondition);
			Interpreter interpreter = new Interpreter();
			interpreter.setStrictJava(true);    
			String cond = testableCondition.toString();
			System.out.println(cond);
			try {
			    interpreter.eval("boolean result = ("+ cond + ")");
			    System.out.println(interpreter.get("result"));
			    if((Boolean) interpreter.get("result")){
			    	uiController.alert("The flag \""+flag.getName() +"\" is true for "+patient.getName()+"!!!!!");
			    }
			    if(interpreter.get("result") == null){
			        throw new ValidationFailure("Eval error while parsing the condition");
			    }
			} catch (EvalError e) {
			    e.printStackTrace();
			    throw new ValidationFailure("Eval error while parsing the condition");
			}
		}
	}

	public void notify(FrontlineEventNotification notification) {
		if(notification instanceof EntitySavedNotification || notification instanceof EntityUpdatedNotification){
			DatabaseEntityNotification dNotification = (DatabaseEntityNotification) notification;
			if(dNotification.getDatabaseEntity() instanceof MedicFormResponse || dNotification.getDatabaseEntity() instanceof MedicFormFieldResponse || dNotification.getDatabaseEntity() instanceof PersonAttributeResponse){
				Person p = ((Response) dNotification.getDatabaseEntity()).getSubject();
				if(p != null){
					checkPatientFlags((Patient) p);
				}
			}
		}
	}
}
