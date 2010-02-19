package net.frontlinesms.plugins.patientview.data.repository;

import java.util.Collection;

import net.frontlinesms.plugins.patientview.data.domain.people.CommunityHealthWorker;
import net.frontlinesms.plugins.patientview.data.domain.people.Patient;

public interface CommunityHealthWorkerDao {
	//TODO Right now we offer basic CRUD, add other options as needed
	
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
	
	public CommunityHealthWorker getCommunityHealthWorkerForPatient(Patient p);
}
