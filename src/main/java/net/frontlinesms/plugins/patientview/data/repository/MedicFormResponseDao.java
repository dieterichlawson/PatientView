package net.frontlinesms.plugins.patientview.data.repository;

import java.util.List;

import net.frontlinesms.plugins.patientview.data.domain.framework.MedicForm;
import net.frontlinesms.plugins.patientview.data.domain.people.Person;
import net.frontlinesms.plugins.patientview.data.domain.response.MedicFormResponse;

public interface MedicFormResponseDao {
	
	/**
	 * Returns all MedicFormResponses
	 * @return All form responses
	 */
	public List<MedicFormResponse> getAllFormResponses();

	/**
	 * Saves a Form response
	 * @param response
	 */
	public void saveMedicFormResponse(MedicFormResponse response);

	/**
	 * Updates a form response
	 * @param response
	 */
	public void updateMedicFormResponse(MedicFormResponse response);
	
	/**
	 * Returns all form responses about the given subject 
	 * @param subject
	 * @return
	 */
	public List<MedicFormResponse> getFormResponsesForSubject(Person subject);
	
	/**
	 * Returns all form responses submitted by the given person
	 */
	public List<MedicFormResponse> getFormResponsesForSubmitter(Person submitter);
	
	/**
	 * Returns all form responses for the given form
	 */
	public List<MedicFormResponse> getFormResponsesForForm(MedicForm form);
	
	/**
	 * Returns all unmapped form responses. Unmapped responses are defined
	 * to be responses without a subject
	 */
	public List<MedicFormResponse> getUnmappedResponses();
	
	/**
	 * Returns all mapped form responses. Mapped responses are defined
	 * to be responses that have a subject
	 */
	public List<MedicFormResponse> getMappedResponses();
	
}
