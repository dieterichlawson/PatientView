package net.frontlinesms.plugins.patientview.data.repository.hibernate;

import java.util.List;

import net.frontlinesms.data.repository.hibernate.BaseHibernateDao;
import net.frontlinesms.plugins.patientview.data.domain.framework.PersonAttribute;
import net.frontlinesms.plugins.patientview.data.domain.people.Person;
import net.frontlinesms.plugins.patientview.data.domain.response.PersonAttributeResponse;
import net.frontlinesms.plugins.patientview.data.repository.PersonAttributeResponseDao;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

public class HibernatePersonAttributeResponseDao extends BaseHibernateDao<PersonAttributeResponse> implements PersonAttributeResponseDao {

	protected HibernatePersonAttributeResponseDao() {
		super(PersonAttributeResponse.class);
	}

	public void deleteAttributeResponse(PersonAttributeResponse attribute) {
		super.delete(attribute);
	}

	public List<PersonAttributeResponse> getAllAttributeResponses() {
		return super.getAll();
	}

	/*
	 * @see net.frontlinesms.plugins.patientview.data.repository.PersonAttributeResponseDao#getAttributeResponses(net.frontlinesms.plugins.patientview.data.domain.framework.PersonAttribute, net.frontlinesms.plugins.patientview.data.domain.people.Person)
	 */
	public List<PersonAttributeResponse> getAttributeResponsesForAttributeAndPerson(PersonAttribute attribute, Person p) {
		DetachedCriteria c = super.getCriterion();
		c.add(Restrictions.eq("attribute", attribute));
		c.add(Restrictions.eq("subject", p));
		return super.getList(c);
	}

	/*
	 * @see net.frontlinesms.plugins.patientview.data.repository.PersonAttributeResponseDao#getMostRecentAttributeResponse(net.frontlinesms.plugins.patientview.data.domain.framework.PersonAttribute, net.frontlinesms.plugins.patientview.data.domain.people.Person)
	 */
	public PersonAttributeResponse getMostRecentAttributeResponse(PersonAttribute attribute, Person p) {
		DetachedCriteria c = super.getCriterion();
		c.add(Restrictions.eq("attribute", attribute));
		c.add(Restrictions.eq("subject", p));
		c.addOrder(Order.desc("dateSubmitted"));
		try{
			return super.getList(c,0,1).get(0);
		}catch(Exception e){
			return null;
		}
	}

	public void saveAttributeResponse(PersonAttributeResponse attribute) {
		super.saveWithoutDuplicateHandling(attribute);
	}

	/* (non-Javadoc)
	 * @see net.frontlinesms.plugins.patientview.data.repository.PersonAttributeResponseDao#getResponsesForAttribute(net.frontlinesms.plugins.patientview.data.domain.framework.PersonAttribute)
	 */
	public List<PersonAttributeResponse> getResponsesForAttribute(PersonAttribute attribute) {
		DetachedCriteria c = super.getCriterion();
		c.add(Restrictions.eq("attribute", attribute));
		return super.getList(c);
	}

}
