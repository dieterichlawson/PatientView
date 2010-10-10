package net.frontlinesms.plugins.patientview.data.repository;

import java.util.Collection;

import net.frontlinesms.data.domain.FrontlineMessage;
import net.frontlinesms.plugins.patientview.data.domain.response.MedicMessageResponse;

public interface MedicMessageResponseDao {

	/**
	 * @return all MedicMessageResponses
	 */
	public Collection<MedicMessageResponse> getAllMedicMessageResponse();

	/**
	 * Saves a message response
	 * @param message
	 */
	public void saveMedicMessageResponse(MedicMessageResponse message);

	/**
	 * Updates a message response
	 * @param message
	 */
	public void updateMedicMessageResponse(MedicMessageResponse message);
	
	/**
	 * Returns the medic message that corresponds with the given FrontlineSMS message
	 * @param vanillaMessage
	 * @return
	 */
	public MedicMessageResponse getMessageForVanillaMessage(FrontlineMessage vanillaMessage);
}
