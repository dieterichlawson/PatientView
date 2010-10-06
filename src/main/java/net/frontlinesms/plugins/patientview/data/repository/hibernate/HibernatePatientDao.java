package net.frontlinesms.plugins.patientview.data.repository.hibernate;


import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import net.frontlinesms.data.repository.hibernate.BaseHibernateDao;
import net.frontlinesms.plugins.patientview.data.domain.people.CommunityHealthWorker;
import net.frontlinesms.plugins.patientview.data.domain.people.Patient;
import net.frontlinesms.plugins.patientview.data.repository.PatientDao;
import net.frontlinesms.ui.i18n.InternationalisationUtils;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

public class HibernatePatientDao extends BaseHibernateDao<Patient> implements PatientDao {

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
	public List<Patient> getAllPatients() {
		return super.getAll();
	}

	public List<Patient> getPatientsByNameWithLimit(String s, int limit) {
		DetachedCriteria c= super.getCriterion();
		c.add(Restrictions.like("name", "%"+s+"%"));
		if(limit > 0)
			return super.getList(c, 0, limit);
		else{
			return super.getList(c);
		}
	}
	
	public List<Patient> getPatientsByCHWAndName(String name, CommunityHealthWorker chw){
		DetachedCriteria c= super.getCriterion();
		c.add(Restrictions.like("name", "%"+name+"%"));
		c.add(Restrictions.eq("chw", chw));
		return super.getList(c);
	}
	
	public List<Patient> getPatientsByName(String s){
		return getPatientsByNameWithLimit(s,-1);
	}
	
	public Patient getPatientById(Long id){
		DetachedCriteria c = super.getCriterion().forClass(Patient.class);
		c.add(Restrictions.eq("pid", id));
		try{
			return super.getList(c).get(0);
		}catch(Throwable t){
			return null;
		}
	}
	
	public Patient getPatient(String name, String birthdate, String id){
		DetachedCriteria c = super.getCriterion();
		//add the name restriction
		if(name !=null && !name.equals("")){
			c.add(Restrictions.eq("name", name));
		}
		//add the birthdate restriction
		if(birthdate !=null && !birthdate.equals("")){
			Date bday = null;
			try {
				bday = InternationalisationUtils.getDateFormat().parse(birthdate);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			long lower = bday.getTime() - 86400000;
			long upper = bday.getTime() + 86400000;
			c.add(Restrictions.and(Restrictions.gt("birthdate", lower), Restrictions.lt("birthdate", upper)));
		}
		//add the id restriction
		if(id != null && !id.equals("")){
			long longId = Long.parseLong(id);
			c.add(Restrictions.eq("id", longId));
		}
		Patient p = super.getUnique(c);
		return p;
	}

}
