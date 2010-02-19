package net.frontlinesms.plugins.patientview.history;

import net.frontlinesms.plugins.patientview.data.domain.framework.HistoryEntry;
import net.frontlinesms.plugins.patientview.data.domain.framework.MedicField;
import net.frontlinesms.plugins.patientview.data.domain.framework.MedicForm;
import net.frontlinesms.plugins.patientview.data.domain.people.CommunityHealthWorker;
import net.frontlinesms.plugins.patientview.data.domain.people.Patient;
import net.frontlinesms.plugins.patientview.data.domain.people.Person;
import net.frontlinesms.plugins.patientview.data.domain.people.Person.Gender;
import net.frontlinesms.plugins.patientview.data.repository.hibernate.HibernateHistoryEntryDao;
import net.frontlinesms.plugins.patientview.userlogin.UserSessionManager;

import org.springframework.context.ApplicationContext;

public class HistoryManager {
	
	private static HistoryManager historyManager;
	private static HibernateHistoryEntryDao historyEntryDao;
	
	public static HistoryManager getHistoryManager(){
		if(historyManager == null){
			historyManager = new HistoryManager();
		}
		return historyManager;
	}

	public void init(ApplicationContext appcon){
		historyEntryDao = (HibernateHistoryEntryDao) appcon.getBean("HistoryEntryDao");
	}
	
	public static void logEntry(Person actor, Person subject, String message){
		HistoryEntry he = new HistoryEntry(actor,subject,message);
		historyEntryDao.saveHistoryEntry(he);
	}
	
	public static void logEntryWithCurrentUser(Person subject, String message){
		HistoryEntry he = new HistoryEntry(UserSessionManager.getUserSessionManager().getCurrentUser(),subject,message);
		historyEntryDao.saveHistoryEntry(he);
	}
	
	public static void logNameChange(Person subject, String newName){
		String message = "changed subject's name from " + subject.getName() + " to " + newName;
		logEntryWithCurrentUser(subject,message);
	}
	
	public static void logBirthdateChange(Person subject, String newBday){
		String message = "changed "+ subject.getName() +"'s birthday from " + 
						  subject.getBirthdate().toLocaleString() + " to " + newBday;
		logEntryWithCurrentUser(subject,message);
	}
	
	public static void logGenderChange(Person subject, Gender newGender){
		String message = "changed "+ subject.getName() +"'s gender from " + 
						  subject.getGender().toString()+ " to " + newGender.toString();
		logEntryWithCurrentUser(subject,message);
	}
	
	public static void logCHWChange(Person subject, CommunityHealthWorker newCHW){
		String message = "changed "+ subject.getName() +"'s CHW from " + 
						  ((Patient) subject).getCHWName() + " to " + newCHW.getName();
		logEntryWithCurrentUser(subject,message);
	}
	
	public static void logDetailViewChange(Person subject, MedicField field, String newValue){
		String message = "changed the detail view field labelled "+ field.getLabel() +" for " +subject.getName() + 
						 " to " + newValue;
		logEntryWithCurrentUser(subject,message);
	}
	
	public static void logFormSubmssion(Person subject, MedicForm form){
		String message = "submitted the form " + form.getName() + " on " + subject.getName();
		logEntryWithCurrentUser(subject,message);
	}
	
	public static void logImageChange(Person subject){
		String message = "a new picture was uploaded for " +subject.getName();
		logEntryWithCurrentUser(subject,message);
	}
	
	public static void logDetailViewFieldCreated(MedicField field){
		String message = "a new detail view field was created for persontype " + field.getDetailViewPersonType() +", labelled "+ field.getLabel();
		logEntryWithCurrentUser(null,message);
	}
	
	public static void logDetailViewFieldRemoved(MedicField field){
		String message = "a detail view field was deleted for persontype " + field.getDetailViewPersonType() +", labelled "+ field.getLabel();
		logEntryWithCurrentUser(null,message);
	}
	
}
