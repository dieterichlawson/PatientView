package net.frontlinesms.plugins.patientview.data.repository.hibernate;

import java.util.List;

import net.frontlinesms.data.repository.hibernate.BaseHibernateDao;
import net.frontlinesms.plugins.patientview.data.domain.framework.MedicForm;
import net.frontlinesms.plugins.patientview.data.domain.framework.MedicFormField;
import net.frontlinesms.plugins.patientview.data.domain.people.Person;
import net.frontlinesms.plugins.patientview.data.repository.MedicFormFieldDao;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

public class HibernateMedicFormFieldDao extends BaseHibernateDao<MedicFormField> implements MedicFormFieldDao{
	
	protected HibernateMedicFormFieldDao() {
		super(MedicFormField.class);
	}

	public List<MedicFormField> getAllFields() {
		return super.getAll();
	}
	
	/* (non-javadoc)
	 * @see net.frontlinesms.plugins.patientview.data.repository.MedicFormFieldDao#getFieldsByName(java.lang.String)
	 */
	public List<MedicFormField> getFieldsByName(String s){
		DetachedCriteria c = DetachedCriteria.forClass(MedicFormField.class);
		c.add(Restrictions.like("label", "%" + s+ "%"));
		return super.getList(c);
	}

	/* (non-javadoc)
	 * @see net.frontlinesms.plugins.patientview.data.repository.MedicFormFieldDao#getAttributePanelFields()
	 */
	public List<MedicFormField> getAttributePanelFields() {
		DetachedCriteria c = super.getCriterion();
		c.add(Restrictions.eq("isAttributePanelField", new Boolean(true)));
		return super.getList(c);
	}

	/* (non-javadoc)
	 * @see net.frontlinesms.plugins.patientview.data.repository.MedicFormFieldDao#getAnsweredAttributePanelFieldsForPerson(net.frontlinesms.plugins.patientview.data.domain.people.Person)
	 */
	public List<MedicFormField> getAnsweredAttributePanelFieldsForPerson(Person p) {
		DetachedCriteria c = super.getCriterion().add(Restrictions.eq("isAttributePanelField", new Boolean(true)))
		.createCriteria("responses")
		.add(Restrictions.eq("subject", p));	
		return super.getList(c);
	}

	public void updateField(MedicFormField mff) {
		super.updateWithoutDuplicateHandling(mff);
	}

	/* (non-Javadoc)
	 * @see net.frontlinesms.plugins.patientview.data.repository.MedicFormFieldDao#getFieldsOnForm(net.frontlinesms.plugins.patientview.data.domain.framework.MedicForm)
	 */
	public List<MedicFormField> getFieldsOnForm(MedicForm f) {
		DetachedCriteria c = super.getCriterion().add(Restrictions.eq("parentForm", f));
		return super.getList(c);
	}
}
