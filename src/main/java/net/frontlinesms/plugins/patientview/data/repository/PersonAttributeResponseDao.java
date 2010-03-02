package net.frontlinesms.plugins.patientview.data.repository;

import java.util.List;

import net.frontlinesms.plugins.patientview.data.domain.framework.PersonAttribute;
import net.frontlinesms.plugins.patientview.data.domain.people.Person;
import net.frontlinesms.plugins.patientview.data.domain.response.PersonAttributeResponse;

public interface PersonAttributeResponseDao {
	
	public void saveAttributeResponse(PersonAttributeResponse attribute);

	public void deleteAttributeResponse(PersonAttributeResponse attribute);
		
	public List<PersonAttributeResponse> getAllAttributeResponses();
	
	/**
	 * Should return all responses to the PersonAttribute attribute about person p
	 * @param attribute
	 * @param p
	 * @return
	 */
	public List<PersonAttributeResponse> getAttributeResponsesForAttributeAndPerson(PersonAttribute attribute, Person p);
	
	/**
	 * Should return the most recent response to the PersonAttribute attribute about Person p
	 * @param attribute
	 * @param p
	 * @return
	 */
	public PersonAttributeResponse getMostRecentAttributeResponse(PersonAttribute attribute, Person p);
	
	/**
	 * @param attribute
	 * @return All repsonses for the attribute supplied
	 */
	public List<PersonAttributeResponse> getResponsesForAttribute(PersonAttribute attribute);
}
