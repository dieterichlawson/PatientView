package net.frontlinesms.plugins.patientview.data.repository.hibernate;

import java.util.Collection;

import net.frontlinesms.data.repository.hibernate.BaseHibernateDao;
import net.frontlinesms.plugins.patientview.data.domain.response.MedicMessageResponse;

public class HibernateMedicMessageResponseDao extends BaseHibernateDao<MedicMessageResponse>{

	protected HibernateMedicMessageResponseDao() {
		super(MedicMessageResponse.class);
	}

	public void deleteMedicMessageResponse(MedicMessageResponse message) {
		super.delete(message);
	}

	public Collection<MedicMessageResponse> getAllMedicMessageResponse() {
		return super.getAll();
	}

	public void saveMedicMessageResponse(MedicMessageResponse message) {
		super.saveWithoutDuplicateHandling(message);
	}

	public void updateMedicMessageResponse(MedicMessageResponse message) {
		super.updateWithoutDuplicateHandling(message);
	}
}
