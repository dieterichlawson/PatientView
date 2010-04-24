package net.frontlinesms.plugins.patientview.ui.administration;

import org.springframework.context.ApplicationContext;

import net.frontlinesms.plugins.patientview.search.QueryGenerator;
import net.frontlinesms.plugins.patientview.ui.AdvancedTableController;

public class FormResponseMappingQueryGenerator extends QueryGenerator {
	
	private static final String GET_RESPONSES_QUERY = "select mfr from MedicFormResponse mfr";
	
	public static enum SearchState{
		MAPPED,
		UNMAPPED,
		ALL;
	}
	private SearchState state;
	
	public FormResponseMappingQueryGenerator(ApplicationContext appCon, AdvancedTableController resultsTable) {
		super(appCon, resultsTable);
		super.setPageSize(15);
	}

	@Override
	public void setSort(int column, boolean ascending) {

	}

	@Override
	public void startSearch() {
		if(state == SearchState.UNMAPPED){
			runQuery(GET_RESPONSES_QUERY +" where mfr.subject = null");
		}else if(state == SearchState.MAPPED){
			runQuery(GET_RESPONSES_QUERY +" where mfr.subject != null");
		}else{
			runQuery(GET_RESPONSES_QUERY);
		}
	}

	public void setSearchState(SearchState state){
		this.state = state;
	}

	@Override
	public boolean evictAfterFetch() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean inflateAfterFetch() {
		// TODO Auto-generated method stub
		return true;
	}
}
