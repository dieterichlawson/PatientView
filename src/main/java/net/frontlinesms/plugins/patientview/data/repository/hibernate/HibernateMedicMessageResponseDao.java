package net.frontlinesms.plugins.patientview.data.repository.hibernate;

import java.util.Collection;

import net.frontlinesms.data.domain.Message;
import net.frontlinesms.data.repository.hibernate.BaseHibernateDao;
import net.frontlinesms.plugins.patientview.data.domain.response.MedicMessageResponse;
import net.frontlinesms.plugins.patientview.data.repository.MedicMessageResponseDao;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

public class HibernateMedicMessageResponseDao extends BaseHibernateDao<MedicMessageResponse> implements MedicMessageResponseDao{

	protected HibernateMedicMessageResponseDao() {
		super(MedicMessageResponse.class);
	}

	/* (non-javadoc)
	 * @see net.frontlinesms.plugins.patientview.data.repository.MedicMessageResponseDao#getAllMedicMessageResponse()
	 */
	public Collection<MedicMessageResponse> getAllMedicMessageResponse() {
		return super.getAll();
	}

	/* (non-javadoc)
	 * @see net.frontlinesms.plugins.patientview.data.repository.MedicMessageResponseDao#saveMedicMessageResponse(net.frontlinesms.plugins.patientview.data.domain.response.MedicMessageResponse)
	 */
	public void saveMedicMessageResponse(MedicMessageResponse message) {
		super.saveWithoutDuplicateHandling(message);
	}

	/* (non-javadoc)
	 * @see net.frontlinesms.plugins.patientview.data.repository.MedicMessageResponseDao#updateMedicMessageResponse(net.frontlinesms.plugins.patientview.data.domain.response.MedicMessageResponse)
	 */
	public void updateMedicMessageResponse(MedicMessageResponse message) {
		super.updateWithoutDuplicateHandling(message);
	}

	public MedicMessageResponse getMessageForVanillaMessage(Message m) {
		DetachedCriteria dc = super.getCriterion();
		dc.add(Restrictions.eq("message",m));
		return super.getUnique(dc);
	}
}
