package net.frontlinesms.plugins.medic.data.repository.hibernate;

import java.util.Collection;

import net.frontlinesms.data.repository.hibernate.BaseHibernateDao;
import net.frontlinesms.plugins.medic.data.domain.response.MedicFormResponse;

public class HibernateMedicFormResponseDao extends BaseHibernateDao<MedicFormResponse>{

	protected HibernateMedicFormResponseDao() {
		super(MedicFormResponse.class);
	}

	public void deleteMedicFormResponse(MedicFormResponse response) {
		super.delete(response);
	}

	public Collection<MedicFormResponse> getAllFormResponses() {
		return super.getAll();
	}

	public void saveMedicFormResponse(MedicFormResponse response) {
		super.saveWithoutDuplicateHandling(response);
	}

	public void updateMedicFormResponse(MedicFormResponse response) {
		super.updateWithoutDuplicateHandling(response);
	}
}
