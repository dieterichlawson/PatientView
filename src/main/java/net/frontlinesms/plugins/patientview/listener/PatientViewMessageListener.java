package net.frontlinesms.plugins.patientview.listener;

import net.frontlinesms.FrontlineUtils;
import net.frontlinesms.data.domain.FrontlineMessage;
import net.frontlinesms.data.events.DatabaseEntityNotification;
import net.frontlinesms.data.events.EntitySavedNotification;
import net.frontlinesms.data.events.EntityUpdatedNotification;
import net.frontlinesms.events.EventBus;
import net.frontlinesms.events.EventObserver;
import net.frontlinesms.events.FrontlineEventNotification;
import net.frontlinesms.plugins.patientview.DummyDataGenerator;
import net.frontlinesms.plugins.patientview.data.domain.people.CommunityHealthWorker;
import net.frontlinesms.plugins.patientview.data.domain.response.MedicMessageResponse;
import net.frontlinesms.plugins.patientview.data.repository.CommunityHealthWorkerDao;
import net.frontlinesms.plugins.patientview.data.repository.MedicMessageResponseDao;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;

public class PatientViewMessageListener implements EventObserver {

	private MedicMessageResponseDao messageDao;
	private CommunityHealthWorkerDao chwDao;
	private DummyDataGenerator ddg;
	
	private static Logger LOG = FrontlineUtils.getLogger(PatientViewMessageListener.class);
	
	public PatientViewMessageListener(ApplicationContext appCon, DummyDataGenerator ddg){
		((EventBus) appCon.getBean("eventBus")).registerObserver(this);
		this.messageDao = (MedicMessageResponseDao) appCon.getBean("MedicMessageResponseDao");
		this.chwDao = (CommunityHealthWorkerDao) appCon.getBean("CHWDao");
		this.ddg = ddg;
	}
	public void notify(FrontlineEventNotification notification) {
		if(ddg.isGenerating()){
			return;
		}
		if((notification instanceof  EntitySavedNotification && 
		    ((EntitySavedNotification) notification).getDatabaseEntity() instanceof FrontlineMessage) ||
		    (notification instanceof EntityUpdatedNotification &&
			((EntityUpdatedNotification) notification).getDatabaseEntity() instanceof FrontlineMessage)){
			FrontlineMessage m = (FrontlineMessage) ((DatabaseEntityNotification) notification).getDatabaseEntity();
				if(messageDao.getMessageForVanillaMessage(m) == null){
					try{
						CommunityHealthWorker chw = chwDao.getCommunityHealthWorkerByPhoneNumber(m.getSenderMsisdn());
						MedicMessageResponse mmr = new MedicMessageResponse(m,m.getTextContent(),chw,null);
						messageDao.saveMedicMessageResponse(mmr);
					}catch(Exception e){
						LOG.error("Unable to creat medic message from incoming SMS message",e);
					}
				}
		}
	}

}
