package net.frontlinesms.plugins.patientview.data.repository;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;

public interface CriteriaExecutor {

	public <T> List<T> executeCriteria(DetachedCriteria criteria, Class<? extends T> clazz);
	
	public  <T> T getUnique(DetachedCriteria criteria, Class<? extends T> clazz);
	
	public <T> List<T> executePagedCriteria(DetachedCriteria criteria, int firstResult, int maxResults, Class<? extends T> clazz);
}
