package net.frontlinesms.plugins.patientview.search.impl;

import java.util.List;

import net.frontlinesms.plugins.patientview.data.domain.response.MedicFormResponse;
import net.frontlinesms.plugins.patientview.search.PagedResultSet;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.context.ApplicationContext;

public class FormMappingResultSet extends PagedResultSet {

	private List<MedicFormResponse> results;
	
	//private MedicFormResponseDao responseDao;
	
	private SessionFactory sessionFactory;
	
	private static final String QUERY = "from MedicFormResponse fr where fr.subject is";
	
	public static enum SearchState{
		MAPPED,
		UNMAPPED,
		ALL;
	}
	
	private SearchState state;
	
	public FormMappingResultSet(ApplicationContext appCon){
		//this.responseDao = (MedicFormResponseDao) appCon.getBean("MedicFormResponseDao");
		this.sessionFactory = (SessionFactory) appCon.getBean("sessionFactory");
		super.pageSize=17;
	}
	@Override
	public List getResultsPage() {
		int startIndex = currentPage * pageSize;
		Session session = sessionFactory.openSession();
		String query;
		if(state == SearchState.MAPPED){
			query = QUERY + " not null order by dateSubmitted desc"; 
		}else if(state == SearchState.UNMAPPED){
			query = QUERY + " null order by dateSubmitted desc";
		}else{
			query = "from MedicFormResponse  order by dateSubmitted desc";
		}
		results = session.createQuery(query).setFirstResult(startIndex).setMaxResults(pageSize).list();
		super.setTotalResults(((Long) session.createQuery("select count(*) " + query).uniqueResult()).intValue());
		for(MedicFormResponse mfr : results){
			mfr.getResponses().size();
			session.evict(mfr);
		}
		return results;
	}

	@Override
	public void refresh() {
		getResultsPage();
	}
	
	public void setSearchState(SearchState state) {
		this.state = state;
	}
	
	public SearchState getState() {
		return state;
	}

}
