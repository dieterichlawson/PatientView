package net.frontlinesms.plugins.medic.data.repository.hibernate;

import java.util.Collection;

import net.frontlinesms.data.repository.hibernate.BaseHibernateDao;
import net.frontlinesms.plugins.medic.data.domain.people.CommunityHealthWorker;
import net.frontlinesms.plugins.medic.data.domain.people.Patient;
import net.frontlinesms.plugins.medic.data.repository.CommunityHealthWorkerDao;

import org.hibernate.Query;

public class HibernateCommunityHealthWorkerDao extends BaseHibernateDao<CommunityHealthWorker>
		implements CommunityHealthWorkerDao {
	
	
	private static String chwByNameQuery = "select chw from CommunityHealthWorker chw where chw.name like :name";
	private static String chwByPatientQuery = "select p.chw from Patient p where p = :patient";
	private static String chwByPhoneNumberQuery = "select chw from CommunityHealthWorker chw where chw.contactInfo.phoneNumber = ";
	
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
	public Collection<CommunityHealthWorker> getCommunityHealthWorkerByName(String s, int limit){
		Query q = super.getSession().createQuery(chwByNameQuery);
		q.setParameter("name","%" + s + "%");
		if(limit != -1){
			q.setFetchSize(limit);
			q.setMaxResults(limit);
		}
		return q.list();
	}
	
	public Collection<CommunityHealthWorker> getCommunityHealthWorkerByName(String s){
		return getCommunityHealthWorkerByName(s,-1);
	}

	public CommunityHealthWorker getCommunityHealthWorkerForPatient(Patient p) {
		Query q = super.getSession().createQuery(chwByPatientQuery);
		q.setParameter("patient", p);
		q.setFetchSize(1);
		q.setMaxResults(1);
		return (CommunityHealthWorker) q.list().get(0);
	}
	
	public CommunityHealthWorker getCommunityHealthWorkerByPhoneNumber(String msisdn){
		Query q = super.getSession().createQuery(chwByPhoneNumberQuery + msisdn);
		q.setFetchSize(1);
		q.setMaxResults(1);
		return (CommunityHealthWorker) q.list().get(0);
	}

}
