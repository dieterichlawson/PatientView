package net.frontlinesms.plugins.patientview.data.repository;

import java.util.Collection;
import java.util.List;

import net.frontlinesms.plugins.patientview.data.domain.framework.MedicFormField;
import net.frontlinesms.plugins.patientview.data.domain.people.Person;
import net.frontlinesms.plugins.patientview.data.domain.response.MedicFormFieldResponse;
import net.frontlinesms.plugins.patientview.data.domain.response.MedicFormResponse;

/**
 * The MedicFormFieldResponseDao does not have update or delete methods because
 * in general you should not be manipulating fields directly but should instead
 * manipulate their parent form.
 */
public interface MedicFormFieldResponseDao {

	/**
	 * Saves a MedicFormFieldResponse to the data source.
	 */
	public void saveFieldResponse(MedicFormFieldResponse fieldResponse);

	/**
	 * Gets all form field responses from the data source.
	 * 
	 * @return
	 */
	public Collection<MedicFormFieldResponse> getAllFieldResponses();

	/**
	 * Returns the most recently submitted response for a field about a person
	 * 
	 * @param field
	 * @param subject
	 */
	public MedicFormFieldResponse getMostRecentFieldResponse(
			MedicFormField field, Person subject);

	/**
	 * Returns all responses for the supplied field about the supplied subject
	 * 
	 * @param field
	 *            the MedicFormField
	 * @param subject
	 *            the Person
	 * @return the most recent response
	 */
	public List<MedicFormFieldResponse> getResponsesForFieldAndPerson(
			MedicFormField field, Person subject);

	/**
	 * Returns all the responses to the supplied field
	 * 
	 * @param field
	 * @return
	 */
	public List<MedicFormFieldResponse> getResponsesForfield(
			MedicFormField field);

	/**
	 * Returns all form field responses on the supplied form response. This
	 * method should be destroyed as soon as we figure out a better way to fix
	 * the Lazy Loading exceptions
	 * 
	 * @param mfr
	 * @return
	 */
	public List<MedicFormFieldResponse> getResponsesForFormResponse(
			MedicFormResponse mfr);

	/**
	 * Updates the subjects of the fields on the supplied form response
	 * to match the supplied form's subject.
	 * 
	 * @param formResponse
	 */
	public void updateSubjects(MedicFormResponse formResponse);

	/**
	 * Updates the submitters of the fields on the supplied form response
	 * to match the supplied form's submitter.
	 * 
	 * @param formResponse
	 */
	public void updateSubmitters(MedicFormResponse formResponse);

}
