package net.frontlinesms.plugins.patientview.search.impl;

import java.util.List;

import net.frontlinesms.plugins.patientview.data.domain.people.Person;
import net.frontlinesms.plugins.patientview.search.PagedResultSet;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.context.ApplicationContext;

public class PersonResultSet<P extends Person> extends PagedResultSet {

	private List<P> results;
	
	private Class<P> personClass;
	
	private SessionFactory sessionFactory;
	
	private String nameString;

	@Override
	public List getFreshResultsPage() {
		Session session = null;
		try{
			session = sessionFactory.getCurrentSession();
		}catch(Throwable t){			
			session = sessionFactory.openSession();
		}
		Criteria c = session.createCriteria(personClass);
		if(nameString != null){
			c.add(Restrictions.ilike("name",nameString,MatchMode.ANYWHERE));
		}
		c.setProjection(Projections.rowCount());
		super.setTotalResults(((Integer)c.uniqueResult()).intValue()); 
		//clean up after counting
		c.setProjection(null);
		c.setResultTransformer(Criteria.ROOT_ENTITY);
		this.results = c.setFirstResult(super.getFirstResultOnPage()-1).setMaxResults(pageSize).list();
		for(P person: results){
			session.evict(person);
		}
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
		this.sessionFactory = (SessionFactory) appCon.getBean("sessionFactory");
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
