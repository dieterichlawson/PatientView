package net.frontlinesms.plugins.patientview.data.repository.hibernate;

import java.util.Collection;
import java.util.List;

import net.frontlinesms.data.repository.hibernate.BaseHibernateDao;
import net.frontlinesms.plugins.patientview.data.domain.people.CommunityHealthWorker;
import net.frontlinesms.plugins.patientview.data.repository.CommunityHealthWorkerDao;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

public class HibernateCommunityHealthWorkerDao extends BaseHibernateDao<CommunityHealthWorker>
		implements CommunityHealthWorkerDao {
	
	private static String chwByPatientQuery = "select p.chw from Patient p where p = :patient";
	
	protected HibernateCommunityHealthWorkerDao() {
		super(CommunityHealthWorker.class);
	}

	public void deleteCommunityHealthWorker(CommunityHealthWorker chw) {
		super.delete(chw);
	}

	public Collection<CommunityHealthWorker> getAllCommunityHealthWorkers() {
		return super.getAll();
	}

	public void saveCommunityHealthWorker(CommunityHealthWorker chw) {
		super.saveWithoutDuplicateHandling(chw);
	}

	public void updateCommunityHealthWorker(CommunityHealthWorker chw) {
		super.updateWithoutDuplicateHandling(chw);
	}

	/* (non-Javadoc)
	 * @see net.frontlinesms.plugins.patientview.data.repository.CommunityHealthWorkerDao#getCommunityHealthWorkerByName(java.lang.String, int)
	 */
	public List<CommunityHealthWorker> getCommunityHealthWorkerByName(String s, int limit){
		DetachedCriteria c= super.getCriterion();
		c.add(Restrictions.like("name", "%"+s+"%"));
		if(limit > 0)
			return super.getList(c, 0, limit);
		else{
			return super.getList(c);
		}
	}
	
	public CommunityHealthWorker getCommunityHealthWorkerByPhoneNumber(String phoneNumber){
		DetachedCriteria c= super.getCriterion();
		c.createCriteria("contactInfo").add(Restrictions.eq("phoneNumber",phoneNumber));
		return super.getUnique(c);
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
