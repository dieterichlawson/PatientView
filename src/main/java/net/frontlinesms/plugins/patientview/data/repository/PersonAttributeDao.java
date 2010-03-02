package net.frontlinesms.plugins.patientview.data.repository;

import java.util.List;

import net.frontlinesms.plugins.patientview.data.domain.framework.PersonAttribute;
import net.frontlinesms.plugins.patientview.data.domain.framework.PersonAttribute.PersonType;
import net.frontlinesms.plugins.patientview.data.domain.people.Person;

public interface PersonAttributeDao {

	public void saveAttribute(PersonAttribute attribute);

	public void deleteAttribute(PersonAttribute attribute);
	
	public void updateAttribute(PersonAttribute attribute);
	
	public List<PersonAttribute> getAllAttributes();
	
	/**
	 * Returns all attributes that can be filled out for the person type supplied
	 * @param personType
	 * @return
	 */
	public List<PersonAttribute> getAttributesForPersonType(PersonType personType);
	
	/**
	 * Returns the attributes that Person p has responses for
	 * @param p The person in question
	 * @return all attributes that have been answered at least once for person p
	 */
	public List<PersonAttribute> getAnsweredAttributesForPerson(Person p);
	
	/**
	 * Returns all attributes that person p could have filled out on them.
	 * This is a convenience method that is just like calling 
	 * getAttributesForPersonType(PersonType personType)
	 * except you don't have to supply a persontype.
	 * @param p The person in question
	 * @return all possible attributes for that type of person
	 */
	public List<PersonAttribute> getAllAttributesForPerson(Person p);
	
	
}
