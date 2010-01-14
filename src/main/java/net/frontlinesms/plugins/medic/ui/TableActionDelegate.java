package net.frontlinesms.plugins.medic.ui;

public interface TableActionDelegate {
	public void selectionChanged(Object selectedObject);
	public void doubleClickAction(Object selectedObject);
	public Object getTable();
}
