package net.frontlinesms.plugins.patientview.data.repository.hibernate;

import java.util.Collection;
import java.util.List;

import net.frontlinesms.data.repository.hibernate.BaseHibernateDao;
import net.frontlinesms.plugins.patientview.data.domain.framework.MedicFormField;
import net.frontlinesms.plugins.patientview.data.domain.people.Person;
import net.frontlinesms.plugins.patientview.data.domain.response.MedicFormFieldResponse;
import net.frontlinesms.plugins.patientview.data.domain.response.MedicFormResponse;
import net.frontlinesms.plugins.patientview.data.repository.MedicFormFieldResponseDao;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

public class HibernateMedicFormFieldResponseDao extends BaseHibernateDao<MedicFormFieldResponse> implements MedicFormFieldResponseDao{

	protected HibernateMedicFormFieldResponseDao() {
		super(MedicFormFieldResponse.class);
	}
	
	public void saveFieldResponse(MedicFormFieldResponse s) {
		super.saveWithoutDuplicateHandling(s);
	}
	
	public Collection<MedicFormFieldResponse> getAllFieldResponses() {
		return super.getAll();
	}

	public MedicFormFieldResponse getMostRecentFieldResponse(MedicFormField field, Person subject) {
		DetachedCriteria c = DetachedCriteria.forClass(MedicFormFieldResponse.class);
		c.add(Restrictions.eq("field", field));
		c.add(Restrictions.eq("subject", subject));
		c.addOrder(Order.desc("dateSubmitted"));
		try{
			return super.getList(c, 0, 1).get(0);
		}catch(Throwable t){
			return null;
		}
	}

	public List<MedicFormFieldResponse> getResponsesForFieldAndPerson(MedicFormField field, Person subject) {
		DetachedCriteria c = DetachedCriteria.forClass(MedicFormFieldResponse.class);
		c.add(Restrictions.eq("field", field));
		c.add(Restrictions.eq("subject", subject));
		return super.getList(c);
	}

	public List<MedicFormFieldResponse> getResponsesForfield(MedicFormField mff) {
		DetachedCriteria c = DetachedCriteria.forClass(MedicFormFieldResponse.class);
		c.add(Restrictions.eq("field", mff));
		return super.getList(c);
	}

	public List<MedicFormFieldResponse> getResponsesForFormResponse(MedicFormResponse mfr) {	
		DetachedCriteria c = DetachedCriteria.forClass(MedicFormFieldResponse.class);
		c.add(Restrictions.eq("formResponse", mfr));
		c.addOrder(Order.asc("responsePosition"));
		return super.getList(c);
	}
	
	public void updateSubjects(MedicFormResponse formResponse){
		formResponse = (MedicFormResponse) super.getSession().merge(formResponse);
		for(MedicFormFieldResponse fieldResponse: formResponse.getResponses()){
			fieldResponse.setSubject(formResponse.getSubject());
			updateWithoutDuplicateHandling(fieldResponse);
		}
	}
	
	public void updateSubmitters(MedicFormResponse formResponse){
		formResponse = (MedicFormResponse) super.getSession().merge(formResponse);
		for(MedicFormFieldResponse fieldResponse: formResponse.getResponses()){
			fieldResponse.setSubmitter(formResponse.getSubmitter());
			updateWithoutDuplicateHandling(fieldResponse);
		}
	}

}
