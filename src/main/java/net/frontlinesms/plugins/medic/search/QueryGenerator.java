package net.frontlinesms.plugins.medic.search;

public interface QueryGenerator {

	public void startSearch();
	public void setSort(String fieldName, boolean ascending);
}
