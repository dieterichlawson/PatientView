package net.frontlinesms.plugins.patientview.data.repository.hibernate;

import java.util.Collection;

import net.frontlinesms.data.repository.hibernate.BaseHibernateDao;
import net.frontlinesms.plugins.patientview.data.domain.people.CommunityHealthWorker;
import net.frontlinesms.plugins.patientview.data.domain.people.Patient;
import net.frontlinesms.plugins.patientview.data.repository.PatientDao;

import org.hibernate.Query;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

public class HibernatePatientDao extends BaseHibernateDao<Patient> implements PatientDao {
	
	private static final String getPatientsByNameQuery = "select p from Patient p where p.name like :name";
	private static final String getPatientsByCHWAndNameQuery = "select p from Patient p where p.chw = :chw and p.name like :name";
	
	protected HibernatePatientDao() {
		super(Patient.class);
	}
	
	//TODO fix this so it works
	public Collection<Patient> getPatientsForCHW(CommunityHealthWorker chw) {
		DetachedCriteria criteria = super.getCriterion();
		criteria.add(Restrictions.eq("chw", chw));
		return super.getList(criteria);
	}

	/** @see PatientDao#savePatient(Patient) */
	public void savePatient(Patient p) {
		super.saveWithoutDuplicateHandling(p);
	}

	/** @see PatientDao#updatePatient(Patient) */
	public void updatePatient(Patient p) {
		super.updateWithoutDuplicateHandling(p);
	}

	/** @see PatientDao#deletePatient(Patient) */
	public void deletePatient(Patient p) {
		super.delete(p);
	}

	/** @see PatientDao#getAllPatients() */
	public Collection<Patient> getAllPatients() {
		return super.getAll();
	}

	public Collection<Patient> getPatientsByName(String s, int limit) {
		Query q= super.getSession().createQuery(getPatientsByNameQuery);
		q.setParameter("name", "%" + s+"%");
		if(limit != -1){
			q.setFetchSize(limit);
			q.setMaxResults(limit);
		}
		return q.list();
	}
	
	public Collection<Patient> getPatientsByCHWAndName(String name, CommunityHealthWorker chw){
		Query q= super.getSession().createQuery(getPatientsByCHWAndNameQuery);
		q.setParameter("name", "%" + name+"%");
		q.setParameter("chw", chw);
		return q.list();
	}
	
	public Collection<Patient> getPatientsByName(String s){
		return getPatientsByName(s,-1);
	}

}
