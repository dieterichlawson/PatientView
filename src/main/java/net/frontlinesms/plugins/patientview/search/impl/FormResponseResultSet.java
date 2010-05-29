package net.frontlinesms.plugins.patientview.search.impl;

import java.util.Date;
import java.util.List;

import net.frontlinesms.plugins.patientview.data.domain.framework.MedicForm;
import net.frontlinesms.plugins.patientview.data.domain.people.Person;
import net.frontlinesms.plugins.patientview.data.domain.response.MedicFormResponse;
import net.frontlinesms.plugins.patientview.search.PagedResultSet;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.context.ApplicationContext;


/**
 * A paged result set for retrieving form responses
 * @author dieterichlawson
 *
 */
public class FormResponseResultSet extends PagedResultSet {

	/**
	 * Orders the results by the distance from this date
	 */
	private Date aroundDate;
	
	
	/**
	 * Limits the results to form responses submitted by this person
	 */
	private Person submitter;
	
	
	/**
	 * Limits the results to form responses about this persom
	 */
	private Person subject;
	
	
	/**
	 * Limits the results to form responses for this form
	 */
	private MedicForm form;
	
	/**
	 * The results
	 */
	private List<MedicFormResponse> results;
	
	private SessionFactory sessionFactory;

	public FormResponseResultSet(ApplicationContext appCon){
		this.sessionFactory = (SessionFactory) appCon.getBean("sessionFactory");
		super.pageSize = 28;
	}
	
	@Override
	public List<MedicFormResponse> getResultsPage() {
		Session session = null;
		try{
			session = sessionFactory.getCurrentSession();
		}catch(Throwable t){			
			session = sessionFactory.openSession();
		}
		String query = "from MedicFormResponse mfr";
		boolean previousStatement = false;
		//create the criteria
		if(submitter != null){
			query += " where mfr.submitter.pid = "+ submitter.getPid();
			previousStatement=true;
		}
		//search by subject
		if(subject != null){
			if(previousStatement){
				query += " and";
			}else{
				query+=" where";
			}
			query += " mfr.subject.pid = "+ subject.getPid();
			previousStatement=true;
		}
		if(form != null){
			if(previousStatement){
				query += " and";
			}else{
				query+=" where";
			}
			query += " mfr.form.fid = "+ form.getFid();
		}
		if(aroundDate != null){
			query += " order by abs(mfr.dateSubmitted - " + aroundDate.getTime() + ") asc";
		}
		//get the results
		results = session.createQuery(query).setFirstResult(super.getFirstResultOnPage()-1).setMaxResults(pageSize).list();
		//count the results
		super.setTotalResults(((Long) session.createQuery("select count(*) " + query).uniqueResult()).intValue()); 
		return results;
	}

	@Override
	public void refresh() {
		getResultsPage();
	}
	
	public List<MedicFormResponse> getResults() {
		return results;
	}

	public void setAroundDate(Date aroundDate) {
		this.aroundDate = aroundDate;
	}

	public void setSubmitter(Person submitter) {
		this.submitter = submitter;
	}

	public void setSubject(Person subject) {
		this.subject = subject;
	}

	public void setForm(MedicForm form) {
		this.form = form;
	}


}
