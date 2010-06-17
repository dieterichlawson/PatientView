package net.frontlinesms.plugins.patientview.data.repository;

import java.util.Collection;

import net.frontlinesms.data.domain.FrontlineMessage;
import net.frontlinesms.plugins.patientview.data.domain.response.MedicMessageResponse;

public interface MedicMessageResponseDao {

	public Collection<MedicMessageResponse> getAllMedicMessageResponse();

	public void saveMedicMessageResponse(MedicMessageResponse message);

	public void updateMedicMessageResponse(MedicMessageResponse message);
	
	public MedicMessageResponse getMessageForVanillaMessage(FrontlineMessage m);
}
