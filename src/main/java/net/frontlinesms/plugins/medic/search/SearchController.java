package net.frontlinesms.plugins.medic.search;

public interface SearchController {
	public void controllerWillAppear();
	public QueryGenerator getQueryGenerator();
}
