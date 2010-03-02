package net.frontlinesms.plugins.patientview.data.domain.framework;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import net.frontlinesms.plugins.patientview.data.domain.response.PersonAttributeResponse;


/**
 * Person Attributes are meant to be pieces of information about a person that 
 * are not in the core info (name, gender, etc..) but are also not in the fields
 * being submitted about the person. This is a good place to put stuff like "HIV status"
 * or "TB Status" or things of that nature
 * @author Dieterich
 *
 */
@Entity
public class PersonAttribute extends Field{

	/**
	 * The type of person that this attribute describes
	 */
	@Enumerated(EnumType.STRING)
	private PersonType personType;
	
	@OneToMany(cascade=CascadeType.REMOVE, fetch=FetchType.LAZY, mappedBy="attribute")
	private Set<PersonAttributeResponse> responses;
	
	public enum PersonType{ PATIENT(),CHW(), USER()}
	
	public PersonAttribute(){}


	public PersonAttribute(String label, DataType datatype){
		super(label,datatype);
	}

	/**
	 * @return The person type that this attribute describes
	 */
	public PersonType getPersonType() {
		return personType;
	}

	/**
	 * @param detailViewPersonType the new person type that this attribute should describe
	 */
	public void setPersonType(PersonType personType) {
		this.personType = personType;
	}
	
	public Set<PersonAttributeResponse> getResponses(){
		return responses;
	}
	
	
}
