package net.frontlinesms.plugins.patientview.data.repository;

import java.util.Collection;

import net.frontlinesms.plugins.patientview.data.domain.people.CommunityHealthWorker;
import net.frontlinesms.plugins.patientview.data.domain.people.Patient;

public interface CommunityHealthWorkerDao {
	
	/**
	 * Saves a CHW to the data source
	 * @param chw the CHW to save
	 */
	public void saveCommunityHealthWorker(CommunityHealthWorker chw);
	
	/**
	 * Updates a CHW in the data source
	 * @param chw the CHW to update
	 */
	public void updateCommunityHealthWorker(CommunityHealthWorker chw);

	/**
	 * Deletes a CHW from the data source.
	 * @param chw CHW to save
	 */
	public void deleteCommunityHealthWorker(CommunityHealthWorker chw);
	
	/** @return all CHWs saved in the data source */
	public Collection<CommunityHealthWorker> getAllCommunityHealthWorkers();
	
	/**
	 * Returns the community health worker that cares for patient p
	 * @param p The patient
	 * @return the Community Health Worker
	 */
	public CommunityHealthWorker getCommunityHealthWorkerForPatient(Patient p);
	
	/**
	 * Returns the community health worker that has the phone number supplied
	 * @param phoneNumber The phone number
	 * @return the Community Health Worker
	 */
	public CommunityHealthWorker getCommunityHealthWorkerByPhoneNumber(String phoneNumber);
	
	/**
	 * Returns community health workers that have string s in their name. Performs a 'like' query
	 * with %'s surrounding s.
	 * @param s the string to be searched for
	 * @param limit number of results returned if -1, all are returned
	 * @return 
	 */
	public Collection<CommunityHealthWorker> getCommunityHealthWorkerByName(String s, int limit);
	
	/**
	 * Returns the community health worker with the id specified
	 * If there is no such CHW, null is returned
	 * @param id
	 * @return
	 */
	public CommunityHealthWorker getCommunityHealthWorkerById(long id);
}
