package net.frontlinesms.plugins.patientview.data.domain.response;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import net.frontlinesms.data.domain.Message;
import net.frontlinesms.plugins.patientview.data.domain.people.Person;




@Entity
@DiscriminatorValue(value="mess")
public class MedicMessageResponse extends Response{

	@OneToOne(fetch=FetchType.LAZY, cascade=CascadeType.ALL)
	@JoinColumn(name="message")
	private Message message;
	
	public MedicMessageResponse(){}
	
	public MedicMessageResponse(Message message, String messageContent,Person submitter, Person subject) {
		super(submitter,subject);
		this.message = message;
		this.messageContent = messageContent;
	}

	public Message getMessage() {
		return message;
	}

	public void setMessage(Message message) {
		this.message = message;
	}

	public String getMessageContent() {
		return messageContent;
	}

	public void setMessageContent(String messageContent) {
		this.messageContent = messageContent;
	}

	private String messageContent;
	
}
