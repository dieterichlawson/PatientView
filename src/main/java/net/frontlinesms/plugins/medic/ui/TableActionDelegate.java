package net.frontlinesms.plugins.medic.ui;

import net.frontlinesms.plugins.medic.search.QueryGenerator;

public interface TableActionDelegate {
	public void selectionChanged(Object selectedObject);
	public void doubleClickAction(Object selectedObject);
	public Object getTable();
	public QueryGenerator getQueryGenerator();
}
