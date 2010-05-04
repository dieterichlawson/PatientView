package net.frontlinesms.plugins.patientview.data.repository.hibernate;

import java.util.List;

import net.frontlinesms.data.repository.hibernate.BaseHibernateDao;
import net.frontlinesms.plugins.patientview.data.domain.graph.GraphSettings;
import net.frontlinesms.plugins.patientview.data.domain.people.Person;
import net.frontlinesms.plugins.patientview.data.repository.GraphSettingsDao;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

public class HibernateGraphSettingsDao extends BaseHibernateDao<GraphSettings> implements GraphSettingsDao{

	protected HibernateGraphSettingsDao() {
		super(GraphSettings.class);
	}

	public void deleteGraphSettings(GraphSettings graphSettings) {
		super.delete(graphSettings);
	}

	public List<GraphSettings> getAllGraphSettings() {
		return super.getAll();
	}

	public List<GraphSettings> getGraphSettingsForPerson(Person p) {
		DetachedCriteria c = super.getCriterion();
		c.add(Restrictions.eq("person", p));
		return super.getList(c);
	}

	public void saveOrUpdateGraphSettings(GraphSettings graphSettings) {
		super.getHibernateTemplate().saveOrUpdate(graphSettings);
	}

}
