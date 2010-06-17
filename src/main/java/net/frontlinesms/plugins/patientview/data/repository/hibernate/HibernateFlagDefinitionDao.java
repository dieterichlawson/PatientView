package net.frontlinesms.plugins.patientview.data.repository.hibernate;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import net.frontlinesms.data.repository.hibernate.BaseHibernateDao;
import net.frontlinesms.plugins.patientview.data.domain.flag.FlagDefinition;
import net.frontlinesms.plugins.patientview.data.repository.FlagDefinitionDao;

public class HibernateFlagDefinitionDao extends BaseHibernateDao<FlagDefinition> implements FlagDefinitionDao{

	protected HibernateFlagDefinitionDao() {
		super(FlagDefinition.class);
	}

	public void deleteFlagDefinition(FlagDefinition definition) {
		super.delete(definition);
	}

	public List<FlagDefinition> findFlagDefinitionsByName(String name) {
		DetachedCriteria c = super.getCriterion();
		c.add(Restrictions.like("name", "%"+name +"%"));
		return super.getList(c);
	}

	public List<FlagDefinition> getAllFlagDefinitions() {
		return super.getAll();
	}

	public FlagDefinition getFlagDefinitionByID(long id) {
		DetachedCriteria c = super.getCriterion();
		c.add(Restrictions.like("fid", id));
		return super.getUnique(c);
	}

	public void saveFlagDefinition(FlagDefinition definition) {
		super.saveWithoutDuplicateHandling(definition);
	}
	
	public void updateFlagDefinition(FlagDefinition definition) {
		super.updateWithoutDuplicateHandling(definition);
	}

}
