package net.frontlinesms.plugins.patientview.data.repository;

import java.util.Collection;
import java.util.List;

import net.frontlinesms.plugins.patientview.data.domain.people.CommunityHealthWorker;

public interface CommunityHealthWorkerDao {

	/**
	 * Saves a CHW to the data source
	 * 
	 * @param chw
	 *            the CHW to save
	 */
	public void saveCommunityHealthWorker(CommunityHealthWorker chw);

	/**
	 * Updates a CHW in the data source
	 * 
	 * @param chw
	 *            the CHW to update
	 */
	public void updateCommunityHealthWorker(CommunityHealthWorker chw);

	/**
	 * Deletes a CHW from the data source.
	 * 
	 * @param chw
	 *            CHW to save
	 */
	public void deleteCommunityHealthWorker(CommunityHealthWorker chw);

	/** @return all CHWs saved in the data source */
	public Collection<CommunityHealthWorker> getAllCommunityHealthWorkers();

	/**
	 * Returns the community health worker that has exactly the supplied phone
	 * number. If there is no such CHW, null is returned.
	 * 
	 * @param phoneNumber
	 *            The phone number
	 * @return the Community Health Worker
	 */
	public CommunityHealthWorker getCommunityHealthWorkerByPhoneNumber(
			String phoneNumber);

	/**
	 * Returns all CHWs that have the string 'nameFragment' anywhere in their
	 * name
	 * 
	 * @param nameFragment
	 *            the string to be searched for
	 * @param limit
	 *            number of results to be returned
	 * @return
	 */
	public List<CommunityHealthWorker> findCommunityHealthWorkerByName(
			String nameFragment);

	/**
	 * Returns community health workers that have string nameFragment anywhere
	 * in their name. You can limit the number of results returned by supplying
	 * the int limit. If limit is less than 0, all results will be returned
	 * 
	 * @param nameFragment
	 *            the string to be searched for
	 * @param limit
	 *            number of results to be returned
	 * @return
	 */
	public List<CommunityHealthWorker> findCommunityHealthWorkerByName(
			String nameFragment, int limit);

	/**
	 * Returns the CHW with the id specified. If there is no such CHW, null is
	 * returned
	 * 
	 * @param id
	 * @return
	 */
	public CommunityHealthWorker getCommunityHealthWorkerById(long id);
}
