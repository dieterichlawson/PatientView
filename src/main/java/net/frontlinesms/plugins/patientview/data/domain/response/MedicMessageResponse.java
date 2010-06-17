package net.frontlinesms.plugins.patientview.data.domain.response;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;

import net.frontlinesms.data.domain.FrontlineMessage;
import net.frontlinesms.plugins.patientview.data.domain.people.Person;



@Entity
@DiscriminatorValue(value="mess")
public class MedicMessageResponse extends Response{
	
	@OneToOne(cascade=CascadeType.ALL,fetch=FetchType.LAZY,targetEntity=FrontlineMessage.class)
	private FrontlineMessage message;
	
	public MedicMessageResponse(){}
	
	public MedicMessageResponse(FrontlineMessage message, String messageContent,Person submitter, Person subject) {
		super(submitter,subject);
		this.message = message;
		this.messageContent = messageContent;
	}

	public FrontlineMessage getMessage() {
		return message;
	}

	public void setMessage(FrontlineMessage message) {
		this.message = message;
	}

	public String getMessageContent() {
		return messageContent;
	}

	public void setMessageContent(String messageContent) {
		this.messageContent = messageContent;
	}
	
	public String getSenderMsisdn(){
		return message.getSenderMsisdn();
	}
	
	public String getRecipientMsisdn(){
		return message.getRecipientMsisdn();
	}

	public String getStatus(){
		return message.getStatus().toString().toLowerCase();
	}
	
	@Column(length=FrontlineMessage.SMS_MAX_CHARACTERS)
	private String messageContent;
	
}
