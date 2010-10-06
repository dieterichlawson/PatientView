package net.frontlinesms.plugins.patientview.data.repository.hibernate;

import java.util.List;

import net.frontlinesms.plugins.patientview.data.repository.CriteriaExecutor;

import org.hibernate.criterion.DetachedCriteria;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class HibernateCriteriaExecutor extends HibernateDaoSupport implements CriteriaExecutor {

	@SuppressWarnings("unchecked")
	public <T> List<T> executeCriteria(DetachedCriteria criteria, Class<? extends T> clazz) {
		return (List<T>) this.getHibernateTemplate().findByCriteria(criteria);
	}

	@SuppressWarnings("unchecked")
	public <T> T getUnique(DetachedCriteria criteria, Class<? extends T> clazz) {
		return (T) DataAccessUtils.uniqueResult(this.executeCriteria(criteria,clazz));
	}

	public <T> List<T> executePagedCriteria(DetachedCriteria criteria, int firstResult, int maxResults, Class<? extends T> clazz) {
		return (List<T>) this.getHibernateTemplate().findByCriteria(criteria,firstResult,maxResults);
	}
}
