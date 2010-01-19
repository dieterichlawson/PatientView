package net.frontlinesms.plugins.medic.search.drilldownsearch;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import net.frontlinesms.plugins.medic.search.drilldownsearch.breadcrumbs.BreadCrumb;
import net.frontlinesms.plugins.medic.search.drilldownsearch.breadcrumbs.EntityType;
import net.frontlinesms.plugins.medic.ui.AdvancedTable;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.context.ApplicationContext;

public class DrillDownQueryGenerator {
	private ArrayList<BreadCrumb> breadCrumbs;
	//currently searching entity type e
	private EntityType e;
	private SessionFactory sessionFactory;
	private Session session;
	private AdvancedTable resultsTable;
	private boolean searchingForResponses;
	private int sortColumn;
	private boolean isAscending;
	private String lastSearch;
	
	public DrillDownQueryGenerator(ApplicationContext appCon, AdvancedTable resultsTable){
		sessionFactory = (SessionFactory) appCon.getBean("sessionFactory");
		this.resultsTable = resultsTable;
		breadCrumbs = new ArrayList<BreadCrumb>();
		searchingForResponses=false;
		sortColumn = -1;
		lastSearch = "";
	}
	public void updateBreadCrumbs(ArrayList<BreadCrumb> breadCrumbs){
		this.breadCrumbs = breadCrumbs;
	}
	
	public void updateCurrentSearchEntity(EntityType entity){
		this.e = entity;
		removeSort();
	}
	
	public void setSort(int column, boolean isAscending){
		sortColumn = column;
		this.isAscending =isAscending;
		refresh();
	}
	
	public void removeSort(){
		sortColumn =-1;
	}
	
	public void setSearchingForResponses(boolean searching){
		searchingForResponses = searching;
	}
	//notify this query generator that a drill down operation has been performed
	//this allows it to reset its session cache
	public void notifyDrillDown(){
		session.getTransaction().commit();
		session.beginTransaction();
		removeSort();
	}
	
	public void refresh(){
		startSearch(lastSearch);
	}
	
	/** 
	 * This is the heart of the system, the query generation method
	 * do not meddle with this lightly, my friend
	 **/
	public void startSearch(String param){
		lastSearch = param;
		String query ="";
		if(breadCrumbs.size() != 0){
			//step one. Make a tally of super entities:
			HashSet<EntityType> superEntities = new HashSet<EntityType>();
			for(BreadCrumb bc: breadCrumbs){
				superEntities.addAll(bc.getRestrictedEntityTypes());
			}
			//next, go through the breadcrumb list, getting the salient breadcrumbs
			ArrayList<BreadCrumb> salientBreadCrumbs = new ArrayList<BreadCrumb>();
			for(BreadCrumb bcrumb: breadCrumbs){
				if(!superEntities.contains(bcrumb.getEntityType())){
						salientBreadCrumbs.add(bcrumb);
				}
			}
			//now it's time to get the queries from the relevant breadcrumbs
			ArrayList<String> queries = new ArrayList<String>();
			//if you're searching for responses, you want to do things differently
			if(searchingForResponses){
				//step 1, get the piece of content in the bread crumb trail and set it to the currently searching entity
				// since we already filtered out the superentities, it will automatically
				// be the lowest, most specific content container
				e = EntityType.FORM;						
				for(BreadCrumb bc: breadCrumbs){
					if(bc.getEntityType() == EntityType.FIELD){
						e = EntityType.FIELD;
						break;
					}
				}
				for(BreadCrumb bc: salientBreadCrumbs){
					queries.add(bc.getResponseQueryForEntityType(e));
				}
			}else{
				for(BreadCrumb bc: salientBreadCrumbs){
					queries.add(bc.getQueryForEntityType(e));
				}
			}
			//now we have the queries, time to construct the final query
			for(int i = 0; i < queries.size(); i++){
				query += queries.get(i);
				//if we have iterated more than once, then we should close off the previous subquery
				if(i != 0 ){
					query += ")";
				}
				//if we aren't at the end, then we should start a subquery
				if(i != queries.size()-1){
					query += " and " + QueryConstants.getTableAlias(e,searchingForResponses) + " in ( ";
				}
				//if we are at the end and not searching for responses, and the search text is not null,
				//then add the and x.name like '%param%' line.
				if(i == queries.size() -1 && !searchingForResponses && (param !=null && !param.equals(""))){
					query += " and " + QueryConstants.getTableAlias(e,false) + "." + QueryConstants.getNameField(e) +
						" like '%" + param + "%'";
				}
			}
		}else{
				query += "select " + QueryConstants.getTableAlias(e,false) + " from " + QueryConstants.getTableName(e,false) + " " + QueryConstants.getTableAlias(e,false);
				if(param != null && !param.equals("") ){
					query += " where " + QueryConstants.getTableAlias(e,false) + "." + QueryConstants.getNameField(e)
					+ " like '%" + param + "%'";	
				}
		}
		
		//finally, add the sorting
		if(sortColumn >= 0){
				query += " order by " + QueryConstants.getTableAlias(e, searchingForResponses) + "."
									  + QueryConstants.getSortColumn(sortColumn, e, searchingForResponses);
			query += isAscending? " asc":" desc";
		}

		System.out.println(query);
		//check if session is active
		if(session == null){
			session = sessionFactory.openSession();
			session.beginTransaction();
		}
		
		long prevTime = System.nanoTime();
		List results  = session.createQuery(query).setMaxResults(30).list();
		long elapsedTime = System.nanoTime() - prevTime;
		System.out.println("Query Time: " + elapsedTime/1000000.0);
			resultsTable.setResults(results);
	}
	
	private BreadCrumb getCurrentBreadCrumb(){
		return null;
	}
}
