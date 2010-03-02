package net.frontlinesms.plugins.patientview.data.repository;

import java.util.Collection;
import java.util.List;

import net.frontlinesms.plugins.patientview.data.domain.framework.MedicFormField;
import net.frontlinesms.plugins.patientview.data.domain.people.Person;
import net.frontlinesms.plugins.patientview.data.domain.response.MedicFormFieldResponse;

/**
 * This dao is fairly stripped down since, in general, you should not be deleting or
 * updating responses to fields. Additionally, most saving of fields should be done
 * by first placing them in a FormResponse container
 * @author Dieterich
 *
 */
public interface MedicFormFieldResponseDao {

	public void saveFieldResponse(MedicFormFieldResponse s);
	
	public Collection<MedicFormFieldResponse> getAllFieldResponses();
		
	/**
	 * Returns the most recently submitted response for the field f about the person p
	 * @param f The FormField in question
	 * @param p the Person in question
	 * @return the most recent response
	 */
	public MedicFormFieldResponse getMostRecentFieldResponse(MedicFormField f, Person p);
	
	/**
	 * Returns the most recent reponse for MedicFormField f concerning person p
	 * @param f the MedicFormField
	 * @param p the Person
	 * @return the most recent response
	 */
	public List<MedicFormFieldResponse> getResponsesForFieldAndPerson(MedicFormField f, Person p);
	
	/**
	 * Returns all the responses to the Form field mff
	 * @param mff
	 * @return
	 */
	public List<MedicFormFieldResponse> getResponsesForfield(MedicFormField mff);
	
}
