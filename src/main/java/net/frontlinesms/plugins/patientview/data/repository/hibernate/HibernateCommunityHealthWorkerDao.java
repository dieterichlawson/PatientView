package net.frontlinesms.plugins.patientview.data.repository.hibernate;

import java.util.Collection;

import net.frontlinesms.data.repository.hibernate.BaseHibernateDao;
import net.frontlinesms.plugins.patientview.data.domain.people.CommunityHealthWorker;
import net.frontlinesms.plugins.patientview.data.domain.people.Patient;
import net.frontlinesms.plugins.patientview.data.repository.CommunityHealthWorkerDao;

import org.hibernate.Query;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

public class HibernateCommunityHealthWorkerDao extends BaseHibernateDao<CommunityHealthWorker>
		implements CommunityHealthWorkerDao {
	
	
	private static String chwByNameQuery = "select chw from CommunityHealthWorker chw where chw.name like :name";
	private static String chwByPatientQuery = "select p.chw from Patient p where p = :patient";
	private static String chwByPhoneNumberQuery = "select chw from CommunityHealthWorker chw where chw.contactInfo.phoneNumber = ";
	
	protected HibernateCommunityHealthWorkerDao() {
		super(CommunityHealthWorker.class);
	}

	/* (non-Javadoc)
	 * @see net.frontlinesms.plugins.patientview.data.repository.CommunityHealthWorkerDao#deleteCommunityHealthWorker(net.frontlinesms.plugins.patientview.data.domain.people.CommunityHealthWorker)
	 */
	public void deleteCommunityHealthWorker(CommunityHealthWorker chw) {
		super.delete(chw);
	}

	/* (non-Javadoc)
	 * @see net.frontlinesms.plugins.patientview.data.repository.CommunityHealthWorkerDao#getAllCommunityHealthWorkers()
	 */
	public Collection<CommunityHealthWorker> getAllCommunityHealthWorkers() {
		return super.getAll();
	}

	/* (non-Javadoc)
	 * @see net.frontlinesms.plugins.patientview.data.repository.CommunityHealthWorkerDao#saveCommunityHealthWorker(net.frontlinesms.plugins.patientview.data.domain.people.CommunityHealthWorker)
	 */
	public void saveCommunityHealthWorker(CommunityHealthWorker chw) {
		super.saveWithoutDuplicateHandling(chw);
	}

	/* (non-Javadoc)
	 * @see net.frontlinesms.plugins.patientview.data.repository.CommunityHealthWorkerDao#updateCommunityHealthWorker(net.frontlinesms.plugins.patientview.data.domain.people.CommunityHealthWorker)
	 */
	public void updateCommunityHealthWorker(CommunityHealthWorker chw) {
		super.updateWithoutDuplicateHandling(chw);
	}

	/* (non-Javadoc)
	 * @see net.frontlinesms.plugins.patientview.data.repository.CommunityHealthWorkerDao#getCommunityHealthWorkerByName(java.lang.String, int)
	 */
	public Collection<CommunityHealthWorker> getCommunityHealthWorkerByName(String s, int limit){
		Query q = super.getSession().createQuery(chwByNameQuery);
		q.setParameter("name","%" + s + "%");
		if(limit != -1){
			q.setFetchSize(limit);
			q.setMaxResults(limit);
		}
		return q.list();
	}

	/* (non-Javadoc)
	 * @see net.frontlinesms.plugins.patientview.data.repository.CommunityHealthWorkerDao#getCommunityHealthWorkerForPatient(net.frontlinesms.plugins.patientview.data.domain.people.Patient)
	 */
	public CommunityHealthWorker getCommunityHealthWorkerForPatient(Patient p) {
		Query q = super.getSession().createQuery(chwByPatientQuery);
		q.setParameter("patient", p);
		q.setFetchSize(1);
		q.setMaxResults(1);
		return (CommunityHealthWorker) q.list().get(0);
	}
	
	/* (non-Javadoc)
	 * @see net.frontlinesms.plugins.patientview.data.repository.CommunityHealthWorkerDao#getCommunityHealthWorkerByPhoneNumber(java.lang.String)
	 */
	public CommunityHealthWorker getCommunityHealthWorkerByPhoneNumber(String phoneNumber){
		Query q = super.getSession().createQuery(chwByPhoneNumberQuery + phoneNumber);
		q.setFetchSize(1);
		q.setMaxResults(1);
		return (CommunityHealthWorker) q.list().get(0);
	}

	public CommunityHealthWorker getCommunityHealthWorkerById(long id) {
		DetachedCriteria c = super.getCriterion().forClass(CommunityHealthWorker.class);
		c.add(Restrictions.eq("pid", id));
		try{
			return super.getList(c).get(0);
		}catch(Throwable t){
			return null;
		}
	}

}
