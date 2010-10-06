package net.frontlinesms.plugins.patientview.search.impl;

import java.util.Date;
import java.util.List;

import net.frontlinesms.plugins.patientview.data.domain.framework.MedicForm;
import net.frontlinesms.plugins.patientview.data.domain.response.MedicFormResponse;
import net.frontlinesms.plugins.patientview.data.repository.CriteriaExecutor;
import net.frontlinesms.plugins.patientview.search.OrderBySQL;
import net.frontlinesms.plugins.patientview.search.PagedResultSet;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.context.ApplicationContext;

public class FormMappingResultSet extends PagedResultSet {

	private List<MedicFormResponse> results;
		
	private CriteriaExecutor executor;
	
	private boolean searchingMapped;
	
	private Date aroundDate;
	
	private MedicForm form;
	
	public FormMappingResultSet(ApplicationContext appCon){
		this.executor = ((CriteriaExecutor) appCon.getBean("CriteriaExecutor"));
		super.pageSize=30;
		setSearchingMapped(false);
	}
	
	@Override
	public List getFreshResultsPage() {
		int startIndex = currentPage * pageSize;
		DetachedCriteria c = DetachedCriteria.forClass(MedicFormResponse.class);
		c.setFetchMode("form.fields", FetchMode.LAZY);
		if(isSearchingMapped()){
			c.add(Restrictions.isNotNull("subject"));
			c.createCriteria("submitter").add(Restrictions.sqlRestriction("{alias}.person_type='chw'"));
		}else{
			c.add(Restrictions.isNull("subject"));
		}
		if(form != null){
			c.add(Restrictions.eq("form", form));
		}
		//count before we order
		c.setProjection(Projections.rowCount());
		super.setTotalResults(executor.getUnique(c, Integer.class));
		//clean up after counting
		c.setProjection(null);
		c.setResultTransformer(Criteria.ROOT_ENTITY);
		//order
		if(aroundDate != null){
			c.addOrder(OrderBySQL.sqlFormula("abs(dateSubmitted - " + aroundDate.getTime() + ") asc"));
		}else{
			c.addOrder(OrderBySQL.sqlFormula("dateSubmitted desc"));
		}
		//get results
		this.results = executor.executePagedCriteria(c, startIndex, pageSize, MedicFormResponse.class);
		return results;
	}

	@Override
	public void refresh() {
		getFreshResultsPage();
	}
	
	public List getResultsPage(){
		return results;
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
