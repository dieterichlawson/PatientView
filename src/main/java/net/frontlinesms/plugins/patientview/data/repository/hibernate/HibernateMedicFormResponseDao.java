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

	/* (non-javadoc)
	 * @see net.frontlinesms.plugins.patientview.data.repository.MedicFormResponseDao#getAllFormResponses()
	 */
	public List<MedicFormResponse> getAllFormResponses() {
		return super.getAll();
	}

	/* (non-javadoc)
	 * @see net.frontlinesms.plugins.patientview.data.repository.MedicFormResponseDao#saveMedicFormResponse(net.frontlinesms.plugins.patientview.data.domain.response.MedicFormResponse)
	 */
	public void saveMedicFormResponse(MedicFormResponse response) {
		super.saveWithoutDuplicateHandling(response);
	}

	/* (non-javadoc)
	 * @see net.frontlinesms.plugins.patientview.data.repository.MedicFormResponseDao#updateMedicFormResponse(net.frontlinesms.plugins.patientview.data.domain.response.MedicFormResponse)
	 */
	public void updateMedicFormResponse(MedicFormResponse response) {
		super.updateWithoutDuplicateHandling(response);
	}
	
	/* (non-javadoc)
	 * @see net.frontlinesms.plugins.patientview.data.repository.MedicFormResponseDao#getFormResponsesForSubject(net.frontlinesms.plugins.patientview.data.domain.people.Person)
	 */
	public List<MedicFormResponse> getFormResponsesForSubject(Person p){
		DetachedCriteria c = super.getCriterion();
		c.add(Restrictions.like("subject", p));
		return super.getList(c);
	}
	
	/* (non-javadoc)
	 * @see net.frontlinesms.plugins.patientview.data.repository.MedicFormResponseDao#getFormResponsesForSubmitter(net.frontlinesms.plugins.patientview.data.domain.people.Person)
	 */
	public List<MedicFormResponse> getFormResponsesForSubmitter(Person p){
		DetachedCriteria c = super.getCriterion();
		c.add(Restrictions.like("submitter", p));
		return super.getList(c);
	}

	public List<MedicFormResponse> getFormResponsesForForm(MedicForm form) {
		DetachedCriteria c = super.getCriterion();
		c.add(Restrictions.eq("form", form));
		return super.getList(c);
	}
	
	public MedicFormResponse reattach(MedicFormResponse mf){
		super.getHibernateTemplate().update(mf);
		return mf;
	}

	public List<MedicFormResponse> getMappedResponses() {
		DetachedCriteria c = super.getCriterion();
		c.add(Restrictions.isNotNull("subject"));
		return super.getList(c);
	}


	public List<MedicFormResponse> getUnmappedResponses() {
		DetachedCriteria c = super.getCriterion();
		c.add(Restrictions.isNull("subject"));
		return super.getList(c);
	}

	
	public List<MedicFormResponse> getMappedResponses(int startIndex, int limit) {
		DetachedCriteria c  = super.getCriterion();
		c.add(Restrictions.isNotNull("subject"));
		return super.getList(c, startIndex, limit);
	}

	public List<MedicFormResponse> getUnmappedResponses(int startIndex, int limit) {
		DetachedCriteria c  = super.getCriterion();
		c.add(Restrictions.isNull("subject"));
		return super.getList(c, startIndex, limit);
	}

	public List<MedicFormResponse> getAllResponses(int startIndex, int limit) {
		return super.getAll(startIndex, limit);
	}
	
}
