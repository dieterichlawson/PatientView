package net.frontlinesms.plugins.patientview.data.repository.hibernate;

import java.util.Collection;

import net.frontlinesms.data.domain.FrontlineMessage;
import net.frontlinesms.data.repository.hibernate.BaseHibernateDao;
import net.frontlinesms.plugins.patientview.data.domain.response.MedicMessageResponse;
import net.frontlinesms.plugins.patientview.data.repository.MedicMessageResponseDao;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

public class HibernateMedicMessageResponseDao extends BaseHibernateDao<MedicMessageResponse> implements MedicMessageResponseDao{

	protected HibernateMedicMessageResponseDao() {
		super(MedicMessageResponse.class);
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

	public MedicMessageResponse getMessageForVanillaMessage(FrontlineMessage vanillaMessage) {
		DetachedCriteria c = DetachedCriteria.forClass(MedicMessageResponse.class);
		c.add(Restrictions.eq("message",vanillaMessage));
		return super.getUnique(c);
	}
}
