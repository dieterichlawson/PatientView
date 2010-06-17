package net.frontlinesms.plugins.patientview.data.repository.hibernate;

import java.util.Collection;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import net.frontlinesms.data.repository.hibernate.BaseHibernateDao;
import net.frontlinesms.plugins.patientview.data.domain.framework.MedicFormField;
import net.frontlinesms.plugins.patientview.data.domain.people.Person;
import net.frontlinesms.plugins.patientview.data.domain.response.MedicFormFieldResponse;
import net.frontlinesms.plugins.patientview.data.domain.response.MedicFormResponse;
import net.frontlinesms.plugins.patientview.data.repository.MedicFormFieldResponseDao;

public class HibernateMedicFormFieldResponseDao extends BaseHibernateDao<MedicFormFieldResponse> implements MedicFormFieldResponseDao{

	protected HibernateMedicFormFieldResponseDao() {
		super(MedicFormFieldResponse.class);
	}

	public Collection<MedicFormFieldResponse> getAllFieldResponses() {
		return super.getAll();
	}

	public void saveMedicFieldResponse(MedicFormFieldResponse response) {
		super.saveWithoutDuplicateHandling(response);
	}

	/* (non-javadoc)
	 * @see net.frontlinesms.plugins.patientview.data.repository.MedicFormFieldResponseDao#getMostRecentFieldResponse(net.frontlinesms.plugins.patientview.data.domain.framework.MedicFormField, net.frontlinesms.plugins.patientview.data.domain.people.Person)
	 */
	public MedicFormFieldResponse getMostRecentFieldResponse(MedicFormField f, Person p) {
		DetachedCriteria c = super.getCriterion();
		c.add(Restrictions.eq("field", f));
		c.add(Restrictions.eq("subject", p));
		c.addOrder(Order.desc("dateSubmitted"));
		try{
			return super.getList(c,0,1).get(0);
		}catch(Exception e){
			return null;
		}
	}

	/* (non-javadoc)
	 * @see net.frontlinesms.plugins.patientview.data.repository.MedicFormFieldResponseDao#getResponsesForFieldAndPerson(net.frontlinesms.plugins.patientview.data.domain.framework.MedicFormField, net.frontlinesms.plugins.patientview.data.domain.people.Person)
	 */
	public List<MedicFormFieldResponse> getResponsesForFieldAndPerson(MedicFormField f, Person p) {
		DetachedCriteria c = super.getCriterion();
		c.add(Restrictions.eq("field", f));
		c.add(Restrictions.eq("subject", p));
		return super.getList(c);
	}

	/* (non-javadoc)
	 * @see net.frontlinesms.plugins.patientview.data.repository.MedicFormFieldResponseDao#getResponsesForfield(net.frontlinesms.plugins.patientview.data.domain.framework.MedicFormField)
	 */
	public List<MedicFormFieldResponse> getResponsesForfield(MedicFormField mff) {
		DetachedCriteria c = super.getCriterion();
		c.add(Restrictions.eq("field", mff));
		return super.getList(c);
	}

	public void saveFieldResponse(MedicFormFieldResponse s) {
		super.saveWithoutDuplicateHandling(s);
	}

	public List<MedicFormFieldResponse> getResponsesForForm(MedicFormResponse mfr) {	
		DetachedCriteria c = super.getCriterion();
		c.add(Restrictions.eq("formResponse", mfr));
		c.addOrder(Order.asc("responsePosition"));
		return super.getList(c);
	}

}
