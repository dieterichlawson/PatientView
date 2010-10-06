package net.frontlinesms.plugins.patientview.search;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.context.ApplicationContext;

/**
 * A base class for generating HQL search queries
 * Has utilities for paging, query running, and performance monitoring
 * @author Dieterich Lawson
 *
 */
public abstract class QueryGenerator extends PagedResultSet{
	
	protected String previousQuery;
	
	protected SessionFactory sessionFactory;
	
	protected List results;
	
	public abstract void startSearch();
	public abstract void setSort(int column, boolean ascending);
	
	public QueryGenerator(ApplicationContext appCon){
		this.sessionFactory =  ((SessionFactory) appCon.getBean("sessionFactory"));
		previousQuery="";
	}
	
	/**
	 * Takes an already-constructed HQL query and runs it, handling the pagination
	 * and the performance measurement
	 * @param query The query to be run
	 */
	protected void runQuery(String query){
		//check if session is active
		Session session = sessionFactory.openSession();
		//construct the count query
		String querySuffix = query.substring(query.indexOf("from"));
		String countQuery = "select count(*) " + querySuffix;
		//run the count query, obtaining the total number of results
		long countPrevTime = System.nanoTime();
		totalResults = ((Long) session.createQuery(countQuery).list().get(0)).intValue();
		long countElapsedTime = System.nanoTime() - countPrevTime;		
		if(totalResults % pageSize == 0){
			totalPages = totalResults / pageSize;
		}else{
			totalPages = (totalResults / pageSize) +1;
		}
		//set up the time measurement
		long prevTime = System.nanoTime();
		System.out.println(query);
		//run the query
		results = session.createQuery(query).setFirstResult(currentPage * pageSize).setMaxResults(pageSize).list();
		//output time elapsed
		long elapsedTime = System.nanoTime() - prevTime;
		previousQuery = query;
	}
	
	@Override
	public List getFreshResultsPage() {
		refresh();
		return results;
	}
	
	public List getResultsPage(){
		return results;
	}
	
	@Override
	public void refresh() {
		runQuery(previousQuery);
	}
	
}
