package net.frontlinesms.plugins.patientview.search;

public interface SearchController {
	public void controllerWillAppear();
	public QueryGenerator getQueryGenerator();
}
