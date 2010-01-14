package net.frontlinesms.plugins.medic.data.querygenerator;

import java.util.ArrayList;

import net.frontlinesms.data.domain.Message;

public class MessageBreadCrumb extends BreadCrumb {
	
	private Message message;


	public MessageBreadCrumb(Message m){
		message = m;
		restrictedEntities = new ArrayList<EntityType>();
		restrictedEntities.add(EntityType.FORM);
		restrictedEntities.add(EntityType.FIELD);
		restrictedEntities.add(EntityType.CHW);
		restrictedEntities.add(EntityType.PATIENT);
		type = EntityType.MESSAGE;
	}
	
	@Override
	public String getName() {
		return "from " + message.getSenderMsisdn();
	}

	@Override
	public String getQueryForEntityType(EntityType e) {
		return null;
	}

	@Override
	public String getResponseQueryForEntityType(EntityType e) {
		return null;
	}
}
