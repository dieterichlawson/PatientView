package net.frontlinesms.plugins.medic.search.simplesearch;

import java.util.List;

import net.frontlinesms.plugins.medic.search.FieldDescriptor;
import net.frontlinesms.plugins.medic.search.QueryGenerator;
import net.frontlinesms.plugins.medic.ui.AdvancedTable;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.context.ApplicationContext;

public class SimpleSearchQueryGenerator implements QueryGenerator {
	
	private SimpleSearchController searchController;
	private ApplicationContext appCon;
	private AdvancedTable resultsTable;
	private SessionFactory sessionFactory;
	private Session session;
	
	public SimpleSearchQueryGenerator(SimpleSearchController searchController, ApplicationContext appCon, AdvancedTable resultsTable){
		sessionFactory = (SessionFactory) appCon.getBean("sessionFactory");
		this.appCon = appCon;
		this.resultsTable = resultsTable;
		this.searchController = searchController;
	}
	
	public void startSearch() {
		SimpleSearchEntity sEntity = searchController.getCurrentEntity();
		FieldDescriptor field = searchController.getCurrentField();
		String query = "select " + sEntity.getTableAlias() + " from " 
				+ sEntity.getDatabaseName() + " "  + sEntity.getTableAlias() + " where ";
		if(field.getDataType() == SimpleSearchDataType.STRING){
			query +=  sEntity.getTableAlias() + "." + field.getDatabaseName();
			query += " like '%" + searchController.getTextInput() + "%'";
		}else if(field.getDataType() == SimpleSearchDataType.NUMBER){
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
		runQuery(query);
	}
	
	public void runQuery(String query){
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
	
	public void setSort(String fieldName, boolean ascending) {
		// TODO Auto-generated method stub
		
	}

}
