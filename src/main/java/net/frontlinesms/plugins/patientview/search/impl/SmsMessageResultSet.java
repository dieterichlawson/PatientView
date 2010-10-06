package net.frontlinesms.plugins.patientview.search.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.frontlinesms.plugins.patientview.data.domain.response.MedicMessageResponse;
import net.frontlinesms.plugins.patientview.data.repository.CriteriaExecutor;
import net.frontlinesms.plugins.patientview.search.OrderBySQL;
import net.frontlinesms.plugins.patientview.search.PagedResultSet;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.context.ApplicationContext;

public class SmsMessageResultSet extends PagedResultSet{

	private String contentSearchString = "";
	private Date aroundDate;
	
	private String senderNumber;
	private boolean searchingFrom=true;
	private boolean searchingTo=true;
	
	private List<MedicMessageResponse> results;
	private CriteriaExecutor executor;

	public SmsMessageResultSet(ApplicationContext appCon){
		this.executor = (CriteriaExecutor) appCon.getBean("CriteriaExecutor");
		super.pageSize = 17;
	}
	
	@Override
	public List<MedicMessageResponse> getFreshResultsPage() {
		//create the criteria
		DetachedCriteria c = DetachedCriteria.forClass(MedicMessageResponse.class);
		c.add(Restrictions.ilike("messageContent",contentSearchString,MatchMode.ANYWHERE));
		if(searchingFrom && !searchingTo){
			c.add(Restrictions.eq("senderMsisdn",senderNumber));
		}else if(searchingTo && !searchingFrom){
			c.add(Restrictions.eq("recipientMsisdn",senderNumber));
		}else if(searchingTo && searchingFrom){
			c.add(Restrictions.or(Restrictions.eq("senderMsisdn",senderNumber),Restrictions.eq("recipientMsisdn",senderNumber)));
		}
		if(aroundDate != null){
			c.addOrder(OrderBySQL.sqlFormula("abs(dateSubmitted - " + aroundDate.getTime() + ") asc"));
		}
//		String query = "from MedicMessageResponse mmr where messageContent like '%"+contentSearchString+"%'";
//		if(searchingFrom && !searchingTo){
//			query += " and message.senderMsisdn = '"+senderNumber + "'";
//		}
//		//search by subject
//		if(searchingTo && !searchingFrom){
//			query += " and message.recipientMsisdn = '"+senderNumber + "'";
//		}
//		if(searchingTo && searchingFrom){
//			query +=" and message.senderMsisdn = '" + senderNumber + "' or message.recipientMsisdn = '"+senderNumber + "'";
//		}
//		if(aroundDate != null){
//			query += " order by abs(dateSubmitted - " + aroundDate.getTime()+")";
//		}
		
		if(!searchingFrom && !searchingTo){
			results = new ArrayList<MedicMessageResponse>();
			return results;
		}
		c.setProjection(Projections.rowCount());
		super.setTotalResults(executor.getUnique(c, Integer.class)); 
		//clean up after counting
		c.setProjection(null);
		c.setResultTransformer(Criteria.ROOT_ENTITY);
		this.results = executor.executePagedCriteria(c, super.getFirstResultOnPage()-1, pageSize, MedicMessageResponse.class);
		return results;
	}

	@Override
	public void refresh() {
		getFreshResultsPage();
	}
	
	public void setContentSearchString(String contentSearchString) {
		this.contentSearchString = contentSearchString;
	}
	
	public void setAroundDate(Date aroundDate) {
		this.aroundDate = aroundDate;
	}

	public void setSenderNumber(String senderNumber) {
		this.senderNumber = senderNumber;
	}

	public String getSenderNumber() {
		return senderNumber;
	}

	public void setSearchingFrom(boolean searchingFrom) {
		this.searchingFrom = searchingFrom;
	}
	
	public void setSearchingTo(boolean searchingTo) {
		this.searchingTo = searchingTo;
	}
	
	public List getResultsPage(){
		return results;
	}
}
