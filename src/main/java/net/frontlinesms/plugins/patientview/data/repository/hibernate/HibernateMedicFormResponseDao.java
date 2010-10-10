package net.frontlinesms.plugins.patientview.data.repository.hibernate;

import java.util.List;

import net.frontlinesms.data.repository.hibernate.BaseHibernateDao;
import net.frontlinesms.plugins.patientview.data.domain.framework.MedicForm;
import net.frontlinesms.plugins.patientview.data.domain.people.Person;
import net.frontlinesms.plugins.patientview.data.domain.response.MedicFormResponse;
import net.frontlinesms.plugins.patientview.data.repository.MedicFormResponseDao;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

public class HibernateMedicFormResponseDao extends BaseHibernateDao<MedicFormResponse> implements MedicFormResponseDao{

	protected HibernateMedicFormResponseDao() {
		super(MedicFormResponse.class);
	}

	public List<MedicFormResponse> getAllFormResponses() {
		return super.getAll();
	}

	public void saveMedicFormResponse(MedicFormResponse response) {
		super.saveWithoutDuplicateHandling(response);
	}

	public void updateMedicFormResponse(MedicFormResponse response) {
		super.updateWithoutDuplicateHandling(response);
	}
	
	public List<MedicFormResponse> getFormResponsesForSubject(Person subject){
		DetachedCriteria c = DetachedCriteria.forClass(MedicFormResponse.class);
		c.add(Restrictions.eq("subject", subject));
		return super.getList(c);
	}
	
	public List<MedicFormResponse> getFormResponsesForSubmitter(Person submitter){
		DetachedCriteria c = DetachedCriteria.forClass(MedicFormResponse.class);
		c.add(Restrictions.eq("submitter", submitter));
		return super.getList(c);
	}

	public List<MedicFormResponse> getFormResponsesForForm(MedicForm form) {
		DetachedCriteria c = DetachedCriteria.forClass(MedicFormResponse.class);
		c.add(Restrictions.eq("form", form));
		return super.getList(c);
	}

	public List<MedicFormResponse> getMappedResponses() {
		DetachedCriteria c = DetachedCriteria.forClass(MedicFormResponse.class);
		c.add(Restrictions.isNotNull("subject"));
		return super.getList(c);
	}


	public List<MedicFormResponse> getUnmappedResponses() {
		DetachedCriteria c = DetachedCriteria.forClass(MedicFormResponse.class);
		c.add(Restrictions.isNull("subject"));
		return super.getList(c);
	}
}
