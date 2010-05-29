package net.frontlinesms.plugins.patientview.search.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.frontlinesms.plugins.patientview.data.domain.response.MedicMessageResponse;
import net.frontlinesms.plugins.patientview.search.PagedResultSet;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.context.ApplicationContext;

public class SmsMessageResultSet extends PagedResultSet{

	private String contentSearchString = "";
	private Date aroundDate;
	
	private String senderNumber;
	private boolean searchingFrom=true;
	private boolean searchingTo=true;
	
	private List<MedicMessageResponse> results;
	private SessionFactory sessionFactory;

	public SmsMessageResultSet(ApplicationContext appCon){
		this.sessionFactory = (SessionFactory) appCon.getBean("sessionFactory");
		super.pageSize = 17;
	}
	
	@Override
	public List<MedicMessageResponse> getResultsPage() {
		Session session = null;
		try{
			session = sessionFactory.getCurrentSession();
		}catch(Throwable t){			
			session = sessionFactory.openSession();
		}
		//create the criteria
		String query = "from MedicMessageResponse mmr where messageContent like '%"+contentSearchString+"%'";
		if(searchingFrom && !searchingTo){
			query += " and message.senderMsisdn = '"+senderNumber + "'";
		}
		//search by subject
		if(searchingTo && !searchingFrom){
			query += " and message.recipientMsisdn = '"+senderNumber + "'";
		}
		if(searchingTo && searchingFrom){
			query +=" and message.senderMsisdn = '" + senderNumber + "' or message.recipientMsisdn = '"+senderNumber + "'";
		}
		if(aroundDate != null){
			query += " order by abs(dateSubmitted - " + aroundDate.getTime()+")";
		}if(!searchingFrom && !searchingTo){
			results = new ArrayList<MedicMessageResponse>();
			return results;
		}
		results = session.createQuery(query).setFirstResult(super.getFirstResultOnPage()-1).setMaxResults(pageSize).list();
		super.setTotalResults(((Long) session.createQuery("select count(*) " + query).uniqueResult()).intValue());
		return results;
	}

	@Override
	public void refresh() {
		getResultsPage();
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
}
