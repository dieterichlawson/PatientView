package net.frontlinesms.plugins.patientview.data.repository.hibernate;

import java.util.List;

import net.frontlinesms.data.repository.hibernate.BaseHibernateDao;
import net.frontlinesms.plugins.patientview.data.domain.framework.Field;
import net.frontlinesms.plugins.patientview.data.repository.FieldDao;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

public class HibernateFieldDao extends BaseHibernateDao<Field> implements FieldDao {

	protected HibernateFieldDao() {
		super(Field.class);
	}

	public List<Field> getAllFields() {
		return super.getAll();
	}

	public Field getFieldById(long id) {
		DetachedCriteria c = super.getCriterion();
		c.add(Restrictions.eq("fid", id));
		return super.getUnique(c);
	}

	public List<Field> findFieldsByLabel(String label) {
		DetachedCriteria c = super.getCriterion();
		c.add(Restrictions.like("label", "%"+label+"%"));
		return super.getList(c);
	}
	
	public List<Field> getFieldsByLabel(String label) {
		DetachedCriteria c = super.getCriterion();
		c.add(Restrictions.eq("label", label));
		return super.getList(c);
	}

}
