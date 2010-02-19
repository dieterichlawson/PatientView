package net.frontlinesms.plugins.patientview.search.drilldownsearch;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import net.frontlinesms.plugins.patientview.search.QueryGenerator;
import net.frontlinesms.plugins.patientview.search.drilldownsearch.breadcrumbs.BreadCrumb;
import net.frontlinesms.plugins.patientview.search.drilldownsearch.breadcrumbs.EntityType;
import net.frontlinesms.plugins.patientview.ui.AdvancedTable;

import org.springframework.context.ApplicationContext;

public class DrillDownQueryGenerator extends QueryGenerator{
	/**
	 * the arraylist of the breadcrumbs in the drill down search screen
	 */
	private ArrayList<BreadCrumb> breadCrumbs;
	/**
	 * the entity that is currently being searche for
	 */
	private EntityType e;
	
	/**
	 * boolean that indicates whether or not 'reponses' (for Forms or Fields)
	 * are being searched for
	 */
	private boolean searchingForResponses;
	
	/**
	 * the index of the current column that is being sorted
	 */
	private int sortColumn;
	
	/**
	 * a boolean that indicates whether the column is being sorted ascending or descending
	 */
	private boolean isAscending;
	
	public DrillDownQueryGenerator(ApplicationContext appCon, AdvancedTable resultsTable){
		super(appCon,resultsTable);
		breadCrumbs = new ArrayList<BreadCrumb>();
		searchingForResponses=false;
		sortColumn = -1;
	}
	
	public void updateBreadCrumbs(ArrayList<BreadCrumb> breadCrumbs){
		this.breadCrumbs = breadCrumbs;
	}
	
	public void updateCurrentSearchEntity(EntityType entity){
		this.e = entity;
		removeSort();
	}
	
	@Override
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
	
	/**
	 * a hook that allows the search screen to notify the query generator that
	 * a drill-down operation has been performed
	 */
	public void notifyDrillDown(){
		session.getTransaction().commit();
		session.beginTransaction();
		removeSort();
	}
	
	/** 
	 * This is the heart of the system, the query generation method
	 * do not meddle with this lightly, my friend
	 **/
	public void startSearch(String param){
		String query ="";
		if(breadCrumbs.size() != 0){
			//step one. Make a tally of super-entities:
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
		super.resetPaging();
		runQuery(query);
	}

	@Override
	public void startSearch() {
		
	}
}
