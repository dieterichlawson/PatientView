package net.frontlinesms.plugins.patientview.data.repository.hibernate;

import java.util.List;

import net.frontlinesms.data.repository.hibernate.BaseHibernateDao;
import net.frontlinesms.plugins.patientview.data.domain.framework.PersonAttribute;
import net.frontlinesms.plugins.patientview.data.domain.framework.PersonAttribute.PersonType;
import net.frontlinesms.plugins.patientview.data.domain.people.CommunityHealthWorker;
import net.frontlinesms.plugins.patientview.data.domain.people.Patient;
import net.frontlinesms.plugins.patientview.data.domain.people.Person;
import net.frontlinesms.plugins.patientview.data.repository.PersonAttributeDao;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

public class HibernatePersonAttributeDao extends BaseHibernateDao<PersonAttribute> implements PersonAttributeDao {

	protected HibernatePersonAttributeDao() {
		super(PersonAttribute.class);
	}

	public void deleteAttribute(PersonAttribute attribute) {
		super.delete(attribute);
	}

	public List<PersonAttribute> getAllAttributes() {
		return super.getAll();
	}

	public List<PersonAttribute> getAllAttributesForPerson(Person p) {
		if(p instanceof CommunityHealthWorker){
			return getAttributesForPersonType(PersonType.CHW);
		}else if(p instanceof Patient){
			return getAttributesForPersonType(PersonType.PATIENT);
		}else{
			return null;
		}
	}

	/**
	 * @see net.frontlinesms.plugins.patientview.data.repository.PersonAttributeDao#getAnsweredAttributesForPerson(net.frontlinesms.plugins.patientview.data.domain.people.Person)
	 */
	public List<PersonAttribute> getAnsweredAttributesForPerson(Person p) {
		DetachedCriteria c = super.getCriterion().createCriteria("responses")
		.add(Restrictions.eq("subject", p));	
		return super.getList(c);
	}

	/*
	 * @see net.frontlinesms.plugins.patientview.data.repository.PersonAttributeDao#getAttributesForPersonType(net.frontlinesms.plugins.patientview.data.domain.framework.PersonAttribute.PersonType)
	 */
	public List<PersonAttribute> getAttributesForPersonType(PersonType personType) {
		DetachedCriteria c = super.getCriterion();
		c.add(Restrictions.eq("personType",personType));
		return super.getList(c);
	}

	public void saveAttribute(PersonAttribute attribute) {
		super.saveWithoutDuplicateHandling(attribute);
	}

	public void updateAttribute(PersonAttribute attribute) {
		super.updateWithoutDuplicateHandling(attribute);
	}

}
