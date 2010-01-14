package net.frontlinesms.plugins.medic.ui;

import net.frontlinesms.ui.UiGeneratorController;

public class TableInfo {

	private Object header;
	private UiGeneratorController uiController;
	private String [] columnMethods;
	private String selectMethod;
	private String doubleClickMethod;
	private Object target;
	
	public UiGeneratorController getUiController() {
		return uiController;
	}

	public void setUiController(UiGeneratorController uiController) {
		this.uiController = uiController;
	}

	public String[] getColumnMethods() {
		return columnMethods;
	}

	public void setColumnMethods(String[] columnMethods) {
		this.columnMethods = columnMethods;
	}

	public String getSelectMethod() {
		return selectMethod;
	}

	public void setSelectMethod(String selectMethod) {
		this.selectMethod = selectMethod;
	}

	public String getDoubleClickMethod() {
		return doubleClickMethod;
	}

	public void setDoubleClickMethod(String doubleClickMethod) {
		this.doubleClickMethod = doubleClickMethod;
	}

	public Object getTarget() {
		return target;
	}

	public void setTarget(Object target) {
		this.target = target;
	}
	
	public TableInfo(String [] columnNames){
		header = uiController.create("header");
		for(String s : columnNames){
			uiController.add(header, uiController.createColumn(s, null));
		}
	}
}
