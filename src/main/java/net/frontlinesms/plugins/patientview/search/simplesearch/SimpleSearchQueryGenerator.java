package net.frontlinesms.plugins.patientview.search.simplesearch;

import net.frontlinesms.plugins.patientview.search.FieldDescriptor;
import net.frontlinesms.plugins.patientview.search.QueryGenerator;

import org.springframework.context.ApplicationContext;

public class SimpleSearchQueryGenerator extends QueryGenerator{
	
	private SimpleSearchController searchController;
	
	public SimpleSearchQueryGenerator(SimpleSearchController searchController, ApplicationContext appCon){
		super(appCon);
		this.searchController = searchController;
	}
	
	@Override
	public void startSearch() {
		SimpleSearchEntity sEntity = searchController.getCurrentEntity();
		FieldDescriptor field = searchController.getCurrentField();
		String query = "select " + sEntity.getTableAlias() + " from "  + sEntity.getDatabaseName() + " "  + sEntity.getTableAlias() + " where ";
		if(field.getDataType() == SimpleSearchDataType.STRING){
			query +=  "lower(" +sEntity.getTableAlias() + "." + field.getDatabaseName()+")";
			query += " like '%" + searchController.getTextInput().toLowerCase() + "%'";
		}else if(field.getDataType() == SimpleSearchDataType.NUMBER){
			if(searchController.getNumberInput().equals("") || searchController.getNumberInput() == null){
				return;
			}
			try{
				Integer.parseInt(searchController.getNumberInput());
			}catch(Exception e){
				return;
			}
			query +=  sEntity.getTableAlias() + "." + field.getDatabaseName();
			query += " = " + searchController.getNumberInput();
		}else if(field.getDataType() == SimpleSearchDataType.ENUM){
			query +=  sEntity.getTableAlias() + "." + field.getDatabaseName();
			query += " = '" + searchController.getEnumInput() + "'";
		}else if(field.getDataType() == SimpleSearchDataType.DATE){
			if(searchController.getBeforeDate() != null){
				query +=  sEntity.getTableAlias() + "." + field.getDatabaseName();
				query += " < " +searchController.getBeforeDate().getTime();
			    if(searchController.getAfterDate() != null){
			    	query += " and ";
			    }
			}
			if(searchController.getAfterDate() != null){
				query +=  sEntity.getTableAlias() + "." + field.getDatabaseName();
				query += " > " +searchController.getAfterDate().getTime();
			}
			
		}
		super.resetPaging();
		runQuery(query);
	}
	
	@Override
	public void setSort(int column, boolean ascending) {
		// TODO Make this work
	}

}
