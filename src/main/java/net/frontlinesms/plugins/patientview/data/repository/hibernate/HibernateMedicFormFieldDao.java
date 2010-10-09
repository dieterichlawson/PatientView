package net.frontlinesms.plugins.patientview.data.repository.hibernate;

import java.util.List;

import net.frontlinesms.data.repository.hibernate.BaseHibernateDao;
import net.frontlinesms.plugins.patientview.data.domain.framework.MedicForm;
import net.frontlinesms.plugins.patientview.data.domain.framework.MedicFormField;
import net.frontlinesms.plugins.patientview.data.domain.people.Person;
import net.frontlinesms.plugins.patientview.data.repository.MedicFormFieldDao;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

public class HibernateMedicFormFieldDao extends BaseHibernateDao<MedicFormField> implements MedicFormFieldDao{
	
	protected HibernateMedicFormFieldDao() {
		super(MedicFormField.class);
	}

	public List<MedicFormField> getAllFields() {
		return super.getAll();
	}
	
	public List<MedicFormField> findFieldsByLabel(String labelFragment, int limit) {
		DetachedCriteria c = DetachedCriteria.forClass(MedicFormField.class);
		c.add(Restrictions.like("label", labelFragment,MatchMode.ANYWHERE));
		if(limit > 0) {
			return super.getList(c, 0, limit);
		}else{
			return super.getList(c);
		}
	}
	
	public List<MedicFormField> findFieldsByLabel(String labelFragment){
		return findFieldsByLabel(labelFragment,-1);
	}

	public List<MedicFormField> getAttributePanelFields() {
		DetachedCriteria c = DetachedCriteria.forClass(MedicFormField.class);
		c.add(Restrictions.eq("isAttributePanelField", new Boolean(true)));
		return super.getList(c);
	}

	public List<MedicFormField> getAnsweredAttributePanelFieldsForPerson(Person p) {
		DetachedCriteria c = DetachedCriteria.forClass(MedicFormField.class);
		c.add(Restrictions.eq("isAttributePanelField", new Boolean(true)))
		.createCriteria("responses")
		.add(Restrictions.eq("subject", p));	
		return super.getList(c);
	}

	public void updateField(MedicFormField mff) {
		super.updateWithoutDuplicateHandling(mff);
	}

	public List<MedicFormField> getFieldsOnForm(MedicForm f) {
		DetachedCriteria c = DetachedCriteria.forClass(MedicFormField.class).add(Restrictions.eq("parentForm", f));
		c.addOrder(Order.asc("position"));
		return super.getList(c);
	}
}
