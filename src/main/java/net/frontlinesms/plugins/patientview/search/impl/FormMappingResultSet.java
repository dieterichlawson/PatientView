package net.frontlinesms.plugins.patientview.search.impl;

import java.util.Date;
import java.util.List;

import net.frontlinesms.plugins.patientview.data.domain.framework.MedicForm;
import net.frontlinesms.plugins.patientview.data.domain.response.MedicFormResponse;
import net.frontlinesms.plugins.patientview.search.PagedResultSet;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.context.ApplicationContext;

public class FormMappingResultSet extends PagedResultSet {

	private List<MedicFormResponse> results;
		
	private SessionFactory sessionFactory;
	
	private static final String QUERY = "from MedicFormResponse mfr where mfr.subject is";
	
	private boolean searchingMapped;
	
	private Date aroundDate;
	
	private MedicForm form;
	
	public FormMappingResultSet(ApplicationContext appCon){
		this.sessionFactory = (SessionFactory) appCon.getBean("sessionFactory");
		super.pageSize=30;
		setSearchingMapped(false);
	}
	
	@Override
	public List getResultsPage() {
		int startIndex = currentPage * pageSize;
		Session session = null;
		try{
			session = sessionFactory.getCurrentSession();
		}catch(Throwable t){			
			session = sessionFactory.openSession();
		}
		String query;
		if(isSearchingMapped()){
			query = QUERY + " not null and mfr.submitter.class = 'chw'"; 
		}else{
			query = QUERY + " null";
		}
		if(form != null){
			query += " and mfr.form.fid = "+ form.getFid();
		}
		if(aroundDate != null){
			query += " order by abs(mfr.dateSubmitted - " + aroundDate.getTime() + ") asc";
		}else{
			query += " order by mfr.dateSubmitted desc";
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
	public void setSearchingMapped(boolean searchingMapped) {
		this.searchingMapped = searchingMapped;
	}
	public boolean isSearchingMapped() {
		return searchingMapped;
	}

	public void setAroundDate(Date aroundDate) {
		this.aroundDate = aroundDate;
	}

	public void setForm(MedicForm form) {
		this.form = form;
	}
}
