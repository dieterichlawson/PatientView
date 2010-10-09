package net.frontlinesms.plugins.patientview.data.repository.hibernate;

import java.util.Collection;
import java.util.List;

import net.frontlinesms.data.repository.hibernate.BaseHibernateDao;
import net.frontlinesms.plugins.patientview.data.domain.people.CommunityHealthWorker;
import net.frontlinesms.plugins.patientview.data.repository.CommunityHealthWorkerDao;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

public class HibernateCommunityHealthWorkerDao extends BaseHibernateDao<CommunityHealthWorker> implements CommunityHealthWorkerDao {
	
	protected HibernateCommunityHealthWorkerDao() {
		super(CommunityHealthWorker.class);
	}

	public void saveCommunityHealthWorker(CommunityHealthWorker chw) {
		super.saveWithoutDuplicateHandling(chw);
	}

	public void updateCommunityHealthWorker(CommunityHealthWorker chw) {
		super.updateWithoutDuplicateHandling(chw);
	}
	
	public void deleteCommunityHealthWorker(CommunityHealthWorker chw) {
		super.delete(chw);
	}

	public Collection<CommunityHealthWorker> getAllCommunityHealthWorkers() {
		return super.getAll();
	}

	public List<CommunityHealthWorker> findCommunityHealthWorkerByName(String nameFragment, int limit){
		DetachedCriteria c= super.getCriterion();
		c.add(Restrictions.like("name", nameFragment,MatchMode.ANYWHERE));
		if(limit > 0) return super.getList(c, 0, limit);
		else return super.getList(c);
	}

	public List<CommunityHealthWorker> findCommunityHealthWorkerByName(String nameFragment){
		return findCommunityHealthWorkerByName(nameFragment,-1);
	}
	
	public CommunityHealthWorker getCommunityHealthWorkerByPhoneNumber(String phoneNumber){
		DetachedCriteria c= super.getCriterion();
		c.createCriteria("contactInfo").add(Restrictions.eq("phoneNumber",phoneNumber));
		return super.getUnique(c);
	}

	public CommunityHealthWorker getCommunityHealthWorkerById(long id) {
		DetachedCriteria c = DetachedCriteria.forClass(CommunityHealthWorker.class);
		c.add(Restrictions.eq("pid", id));
		return super.getUnique(c);
	}
}
