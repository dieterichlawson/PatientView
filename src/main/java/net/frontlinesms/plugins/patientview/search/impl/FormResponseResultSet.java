package net.frontlinesms.plugins.patientview.search.impl;

import java.util.Date;
import java.util.List;

import net.frontlinesms.plugins.patientview.data.domain.framework.MedicForm;
import net.frontlinesms.plugins.patientview.data.domain.people.Person;
import net.frontlinesms.plugins.patientview.data.domain.response.MedicFormResponse;
import net.frontlinesms.plugins.patientview.search.OrderBySQL;
import net.frontlinesms.plugins.patientview.search.PagedResultSet;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
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
	public List<MedicFormResponse> getFreshResultsPage() {
		Session session = null;
		try{
			session = sessionFactory.getCurrentSession();
		}catch(Throwable t){			
			session = sessionFactory.openSession();
		}
		Criteria c = session.createCriteria(MedicFormResponse.class);
		c.setFetchMode("form.fields", FetchMode.SELECT);
		//create the criteria
		if(submitter != null){
			c.add(Restrictions.eq("submitter", submitter));
		}
		//search by subject
		if(subject != null){
			c.add(Restrictions.eq("subject", subject));
		}
		if(form != null){
			c.add(Restrictions.eq("form", form));
		}
		//count before we order
		c.setProjection(Projections.rowCount());
		super.setTotalResults(((Integer)c.uniqueResult()).intValue()); 
		//clean up after counting
		c.setProjection(null);
		c.setResultTransformer(Criteria.ROOT_ENTITY);
		//order
		if(aroundDate != null){
			c.addOrder(OrderBySQL.sqlFormula("abs(dateSubmitted - " + aroundDate.getTime() + ") asc"));
		}
		//get the results
		results = c.setFirstResult(super.getFirstResultOnPage()-1).setMaxResults(pageSize).list();
		return results;
	}

	@Override
	public void refresh() {
		getFreshResultsPage();
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
	
	public List getResultsPage(){
		return results;
	}


}
