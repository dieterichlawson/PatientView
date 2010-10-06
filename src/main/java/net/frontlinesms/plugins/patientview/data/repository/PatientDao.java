package net.frontlinesms.plugins.patientview.data.repository;

import java.util.Collection;
import java.util.List;

import net.frontlinesms.plugins.patientview.data.domain.people.CommunityHealthWorker;
import net.frontlinesms.plugins.patientview.data.domain.people.Patient;

public interface PatientDao {

	/**
	 * Returns the patients of a CHW
	 * 
	 * @param chw the CHW
	 * @return the patients
	 */
	public Collection<Patient> getPatientsForCHW(
			CommunityHealthWorker chw);

	/**
	 * Saves a Patient to the data source
	 * 
	 * @param p the Patient to save
	 */
	public void savePatient(Patient p);

	/**
	 * Updates a Patient in the data source
	 * 
	 * @param p the Patient to update
	 */
	public void updatePatient(Patient p);

	/**
	 * Deletes a Patient from the data source.
	 * 
	 * @param p Patient to save
	 */
	public void deletePatient(Patient p);

	/** @return all CHWs saved in the data source */
	public List<Patient> getAllPatients();
	
	/** get all patients with string s in their name**/
	public List<Patient> getPatientsByName(String s);
	
	/**get limit # of patients with string s in their name**/
	public List<Patient> getPatientsByNameWithLimit(String s, int limit);
	
	public Patient getPatientById(Long id);
	
	public Patient getPatient(String name, String birthdate, String id);

	public void voidPatient(Patient patient, boolean keepVisible, String reason);
}
