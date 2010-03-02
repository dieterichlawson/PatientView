package net.frontlinesms.plugins.patientview.data.repository;

import java.util.List;

import net.frontlinesms.plugins.patientview.data.domain.people.Person;
import net.frontlinesms.plugins.patientview.data.domain.response.MedicFormResponse;

public interface MedicFormResponseDao {
	
	/**
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
	 * All form responses about p
	 * @param p
	 * @return
	 */
	public List<MedicFormResponse> getFormResponsesForSubject(Person p);
	
	/**
	 * @param p
	 * @return All Form Responses submitted by p
	 */
	public List<MedicFormResponse> getFormResponsesForSubmitter(Person p);
}
