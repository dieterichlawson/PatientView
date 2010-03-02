package net.frontlinesms.plugins.patientview.data.domain.response;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import net.frontlinesms.plugins.patientview.data.domain.framework.PersonAttribute;
import net.frontlinesms.plugins.patientview.data.domain.people.Person;

@Entity
@DiscriminatorValue("attributeresponse")
public class PersonAttributeResponse extends Response {

	/**
	 * The attribute that this response is in response to
	 */
	@ManyToOne(fetch=FetchType.EAGER,cascade={})
	@JoinColumn(name="attribute")
	PersonAttribute attribute;
	
	/**
	 * The plain-text value of this response
	 */
	String value;
	
	/**
	 * blank constructor for hibernate
	 */
	private PersonAttributeResponse(){}
	
	public PersonAttributeResponse(String value, PersonAttribute attribute, Person subject, Person submitter){
		super(submitter,subject);
		this.value =value;
		this.attribute = attribute;
	}
	
	public PersonAttribute getAttribute() {
		return attribute;
	}

	public void setAttribute(PersonAttribute attribute) {
		this.attribute = attribute;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
