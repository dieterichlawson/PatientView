package net.frontlinesms.plugins.patientview.data.repository.hibernate;


import java.text.ParseException;
import java.util.Date;
import java.util.List;

import net.frontlinesms.data.repository.hibernate.BaseHibernateDao;
import net.frontlinesms.plugins.patientview.data.domain.people.CommunityHealthWorker;
import net.frontlinesms.plugins.patientview.data.domain.people.Patient;
import net.frontlinesms.plugins.patientview.data.domain.response.Response;
import net.frontlinesms.plugins.patientview.data.repository.PatientDao;
import net.frontlinesms.plugins.patientview.security.UserSessionManager;
import net.frontlinesms.ui.i18n.InternationalisationUtils;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

public class HibernatePatientDao extends BaseHibernateDao<Patient> implements PatientDao {
	
	protected HibernatePatientDao() {
		super(Patient.class);
	}
	
	public void savePatient(Patient p) {
		super.saveWithoutDuplicateHandling(p);
	}

	public void updatePatient(Patient p) {
		super.updateWithoutDuplicateHandling(p);
	}

	public void deletePatient(Patient p) {
		super.delete(p);
	}

	public List<Patient> getAllPatients() {
		DetachedCriteria c= getBaseCriterion();
		return super.getList(c);
	}
	
	public List<Patient> getPatientsForCHW(CommunityHealthWorker chw) {
		DetachedCriteria criteria = getBaseCriterion();
		criteria.add(Restrictions.eq("chw", chw));
		return super.getList(criteria);
	}

	public List<Patient> findPatientsByName(String nameFragment, int resultsLimit) {
		DetachedCriteria c= getBaseCriterion();
		c.add(Restrictions.like("name", nameFragment, MatchMode.ANYWHERE));
		if(resultsLimit > 0)
			return super.getList(c, 0, resultsLimit);
		else{
			return super.getList(c);
		}
	}
	
	public List<Patient> findPatientsByName(String nameFragment){
		return findPatientsByName(nameFragment,-1);
	}
	
	public Patient getPatientById(Long id){
		DetachedCriteria c = getBaseCriterion();
		c.add(Restrictions.eq("pid", id));
		return super.getUnique(c);
	}
	
	public Patient findPatient(String name, String birthdate, String id){
		DetachedCriteria c = getBaseCriterion();
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
		return super.getUnique(c);
	}

	private DetachedCriteria getBaseCriterion(){
		DetachedCriteria c= DetachedCriteria.forClass(Patient.class);
		c.add(Restrictions.eq("deleted",false));
		return c;
	}
	
	public void voidPatient(Patient patient, boolean keepVisible, String reason){
		patient.setRemoved(true, keepVisible, UserSessionManager.getUserSessionManager().getCurrentUser(), reason);
		updateWithoutDuplicateHandling(patient);
		//get a list of all responses about this patient
		DetachedCriteria c = DetachedCriteria.forClass(Response.class);
		c.add(Restrictions.eq("subject", patient));
		List<Response> patientResponses = super.getHibernateTemplate().findByCriteria(c);
		//void all responses
		for(Response response: patientResponses){
			response.setRemoved(true, keepVisible, UserSessionManager.getUserSessionManager().getCurrentUser(), reason);
		}
	}
}
