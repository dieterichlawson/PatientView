package net.frontlinesms.plugins.patientview.search.impl;

import java.util.List;

import net.frontlinesms.plugins.patientview.data.domain.people.Person;
import net.frontlinesms.plugins.patientview.data.repository.CriteriaExecutor;
import net.frontlinesms.plugins.patientview.search.PagedResultSet;

import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.context.ApplicationContext;

public class PersonResultSet<P extends Person> extends PagedResultSet {

	private List<P> results;
	
	private Class<P> personClass;
		
	private String nameString;
	
	private CriteriaExecutor executor;

	@Override
	public List getFreshResultsPage() {
		DetachedCriteria c = DetachedCriteria.forClass(personClass);
		if(nameString != null){
			c.add(Restrictions.ilike("name",nameString,MatchMode.ANYWHERE));
		}
		c.add(Restrictions.eq("deleted",false));
		c.setProjection(Projections.rowCount());
		super.setTotalResults(executor.getUnique(c, Integer.class)); 
		//clean up after counting
		c.setProjection(null);
		c.setResultTransformer(Criteria.ROOT_ENTITY);
		this.results = executor.executePagedCriteria(c, super.getFirstResultOnPage()-1, pageSize, personClass);
		return results;
	}

	@Override
	public List getResultsPage() {
		return results;
	}

	@Override
	public void refresh() {
		getFreshResultsPage();
	}
	
	public PersonResultSet(ApplicationContext appCon, Class<P> personClass){
		this.executor = ((CriteriaExecutor) appCon.getBean("CriteriaExecutor"));
		super.pageSize = 28;
		this.personClass = personClass;
	}

	public void setNameString(String nameString) {
		this.nameString = nameString;
	}

	public String getNameString() {
		return nameString;
	}
}
