package net.frontlinesms.plugins.patientview;

import net.frontlinesms.data.domain.Message;
import net.frontlinesms.data.events.DatabaseEntityNotification;
import net.frontlinesms.data.events.EntitySavedNotification;
import net.frontlinesms.data.events.EntityUpdatedNotification;
import net.frontlinesms.events.EventBus;
import net.frontlinesms.events.EventObserver;
import net.frontlinesms.events.FrontlineEventNotification;
import net.frontlinesms.plugins.patientview.data.domain.people.CommunityHealthWorker;
import net.frontlinesms.plugins.patientview.data.domain.response.MedicMessageResponse;
import net.frontlinesms.plugins.patientview.data.repository.CommunityHealthWorkerDao;
import net.frontlinesms.plugins.patientview.data.repository.MedicMessageResponseDao;

import org.springframework.context.ApplicationContext;

public class PatientViewMessageListener implements EventObserver {

	private MedicMessageResponseDao messageDao;
	private CommunityHealthWorkerDao chwDao;
	
	public PatientViewMessageListener(ApplicationContext appCon){
		((EventBus) appCon.getBean("eventBus")).registerObserver(this);
		this.messageDao = (MedicMessageResponseDao) appCon.getBean("MedicMessageResponseDao");
		this.chwDao = (CommunityHealthWorkerDao) appCon.getBean("CHWDao");
	}
	public void notify(FrontlineEventNotification notification) {
		if((notification instanceof  EntitySavedNotification && 
		    ((EntitySavedNotification) notification).getDatabaseEntity() instanceof Message) ||
		    (notification instanceof EntityUpdatedNotification &&
			((EntityUpdatedNotification) notification).getDatabaseEntity() instanceof Message)){
				Message m = (Message) ((DatabaseEntityNotification) notification).getDatabaseEntity();
				if(messageDao.getMessageForVanillaMessage(m) == null){
					CommunityHealthWorker chw = chwDao.getCommunityHealthWorkerByPhoneNumber(m.getSenderMsisdn());
					MedicMessageResponse mmr = new MedicMessageResponse(m,m.getTextContent(),chw,null);
					messageDao.saveMedicMessageResponse(mmr);
				}
		}
	}

}
