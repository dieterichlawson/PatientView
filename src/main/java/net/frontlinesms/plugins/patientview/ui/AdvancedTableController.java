package net.frontlinesms.plugins.patientview.ui;

import static net.frontlinesms.ui.i18n.InternationalisationUtils.getI18NString;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;

import net.frontlinesms.ui.UiGeneratorController;
/**
 * This class provides a controller for a thinlet table that can handle creating
 * headers, doing all the thinlet grunt-work, and autofitting columns.
 * @author Dieterich
 *
 */
public class AdvancedTableController{
	
	/** the thinlet table**/
	protected Object table;
	
	/** the headers for the table **/
	protected Map<Class,Object> headers;
		
	protected AdvancedTableActionDelegate delegate;
	
	/** the message displayed when there are no results**/
	protected String noResultsMessage;

	protected UiGeneratorController uiController;
	
	protected Class currentClass;
	
	/** The size of the results array */
	protected int resultsSize;
	
	/** Objects for determining text width**/
	protected static FontMetrics metrics;
	
	protected Object[][] methods;
	
	protected static final int SELECTION_CHANGED_INDEX = 0;
	protected static final int DOUBLE_CLICK_ACTION_INDEX = 1;
	protected static final int RESULTS_CHANGED_INDEX = 2;
	protected static final int METHOD_INDEX = 0;
	protected static final int HANDLER_INDEX = 1;
	
	static{
		//initialize stuff for determining font width
		ImageIcon icon = new ImageIcon();
		icon.setImage(new BufferedImage(10,10,BufferedImage.OPAQUE));
		Graphics graphics = icon.getImage().getGraphics();
		Font font= new Font("Sans Serif",Font.PLAIN,14);
		metrics = graphics.getFontMetrics(font);
	}
	
	/**
	 * Standard constructor for the Advanced table. Creates a thinlet 
	 * table that is accessible with the getTable() method
	 * @param delegate
	 * @param uiController
	 * @param useTableMethod
	 */
	public AdvancedTableController(AdvancedTableActionDelegate delegate, UiGeneratorController uiController){
		this.uiController = uiController;
		this.delegate = delegate;
		table = uiController.create("table");
		uiController.setInteger(table, "weightx", 1);
		uiController.setInteger(table, "weighty", 1);
		uiController.setAction(table, "tableSelectionChange()", null, this);
		uiController.setPerform(table, "doubleClick()",null,this);
		uiController.setChoice(table, "selection", "single");
		headers = new HashMap<Class, Object>();
		methods = new Object[3][2];
	}
	
	
	/**
	 * Constructor used when you already have the table you want to control
	 * @param delegate
	 * @param uiController
	 * @param table The table you want to control
	 */
	public AdvancedTableController(AdvancedTableActionDelegate delegate, UiGeneratorController uiController, Object table){
		this.uiController = uiController;
		this.delegate = delegate;
		this.table = table;
		uiController.setAction(table, "tableSelectionChange()", null, this);
		uiController.setPerform(table, "doubleClick()",null,this);
		uiController.setChoice(table, "selection", "single");
		headers = new HashMap<Class, Object>();
		methods = new Object[3][2];
	}
	
	/** creates a new header option for the specified class
	 * @param headerClass
	 * @param columnNames an arraylist of the desired titles of the columns
	 * @param columnMethods an arraylist of the methods that should be called to get the content of the rows
	 */
	@SuppressWarnings("static-access")
	public void putHeader(Class headerClass, String[] columnNames, String[] columnMethods){
		Object header = uiController.create("header");
		for(int i = 0; i < columnNames.length; i++){
			uiController.add(header, uiController.createColumn(columnNames[i], columnMethods[i]));
		}
		uiController.setAction(header,"headerClicked()",null,this);
		headers.put(getRealClass(headerClass), header);
	}
	
	
	/**
	 * sets the results of the table
	 * if the header for the class of the results has already been set, it will create the proper 
	 * header and autofit the columns to the width of the results
	 * @param results
	 */
	public void setResults(List results){
		resultsSize = results.size();
		if(results.size() == 0){
			uiController.removeAll(table);
			Object header = uiController.create("header");
			uiController.add(header,uiController.createColumn(getI18NString("advancedtable.no.results.to.display"), null));
			uiController.add(table,header);
			Object row = uiController.createTableRow(null);
			uiController.add(row, uiController.createTableCell(noResultsMessage==null?getI18NString("advancedtable.no.search.results"):noResultsMessage));
			uiController.add(table,row);
			resultsChanged();
			return;
		}
		uiController.removeAll(table);
		currentClass = getRealClass(results.get(0).getClass());
		if(findSuperClass(currentClass)!=null && findSuperClass(currentClass)!= currentClass){
			currentClass = findSuperClass(currentClass);
		}
		uiController.add(table,getAutoFitHeader(results));
		List<Method> methods = getMethodsForClass(currentClass);
		for(Object result: results){
			Object row = uiController.createTableRow(result);
			for(Method m:methods){
				try {
					uiController.add(row,uiController.createTableCell((String) m.invoke(result,null)));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			uiController.add(table,row);
		}
		resultsChanged();
	}
	
	/**
	 * Occasionally, hibernate wraps classes in javassist classes, which breaks
	 * some of the functionality of this table controller. This will remove any wrapper
	 * classes and return the core class
	 * @param c
	 * @return
	 */
	private static Class getRealClass(Class c){
		String s = c.getName();
		if(s.indexOf("_$$_javassist") != -1){
			s = s.substring(0, s.indexOf("_$$_javassist"));
		}
		try {
			return Class.forName(s);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private Class findSuperClass(Class ce){
		if(ce == null){
			return ce;
		}
		if(!headers.keySet().contains(ce)){
			return findSuperClass(ce.getSuperclass());
		}
		return ce;
	}
	
	/**
	 * get all of the methods for the columns that are in the result display of Class c
	 * @param c
	 * @return
	 */
	private List<Method> getMethodsForClass(Class c){
		ArrayList<Method> results = new ArrayList<Method>();
		Object [] columns = uiController.getItems(headers.get(c));
		for(Object column : columns){
			try {
				results.add(c.getMethod((String) uiController.getAttachedObject(column), null));
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			}
		}
		return results;
	}
	
	/**
	 * get the auto-fitted header for the class of the objects in results
	 * @param results
	 * @return
	 */
	private Object getAutoFitHeader(List results){
		Class c = getRealClass(results.get(0).getClass());
		Object tempHeader = headers.get(currentClass);
		for(Object column :uiController.getItems(tempHeader)){
			uiController.setWidth(column, getColumnWidth(column,results,currentClass));
		}
		return tempHeader;
	}
	
	private int getColumnWidth(Object column, List results, Class c){
		int result = getStringWidth(uiController.getText(column));
		Method m=null;
		try {
			m = c.getMethod((String) uiController.getAttachedObject(column), null);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
		m.setAccessible(true);
		for(Object r :results){
			int tempWidth = 0;
			try {
				String s = (String) m.invoke(r,null);
				tempWidth = getStringWidth(s);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
			result = Math.max(result, tempWidth);
		}
		return result;
	}
	
	private int getStringWidth(String text){
		return metrics.stringWidth(text);
	}
	
	public void setTable(Object table){
		uiController.removeAll(table);
		this.table = table;
		uiController.setAction(table, "tableSelectionChange()", null, this);
		uiController.setPerform(table, "doubleClick()",null,this);
	}
	
	/**
	 * Called by thinlet when the table selection changes
	 */
	public void tableSelectionChange(){
		Object entity = uiController.getAttachedObject(uiController.getSelectedItem(table));
		if(methods[SELECTION_CHANGED_INDEX][METHOD_INDEX] == null){
			delegate.selectionChanged(entity);
		}else{
			try {
				((Method) methods[SELECTION_CHANGED_INDEX][METHOD_INDEX]).invoke(methods[SELECTION_CHANGED_INDEX][HANDLER_INDEX], new Object[]{entity});
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
	
	/**
	 * Called by thinlet when a row on the table is double clicked
	 */
	public void doubleClick(){		
		Object entity = uiController.getAttachedObject(uiController.getSelectedItem(table));
		if(methods[DOUBLE_CLICK_ACTION_INDEX][METHOD_INDEX] == null){
			delegate.doubleClickAction(entity);
		}else{
			try {
				((Method) methods[DOUBLE_CLICK_ACTION_INDEX][METHOD_INDEX]).invoke(methods[DOUBLE_CLICK_ACTION_INDEX][HANDLER_INDEX], new Object[]{entity});
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private void resultsChanged(){
		if(methods[RESULTS_CHANGED_INDEX][METHOD_INDEX] == null){
			delegate.resultsChanged();
		}else{
			try {
				((Method) methods[RESULTS_CHANGED_INDEX][METHOD_INDEX]).invoke(methods[RESULTS_CHANGED_INDEX][HANDLER_INDEX], (Object[]) null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public Object getTable(){
		return table;
	}
	
	/**
	 * Selects the row at "index"
	 * @param index
	 */
	public void setSelected(int index){
		if(index < resultsSize){
			uiController.setSelectedIndex(table,index);
			tableSelectionChange();
		}
	}
	
	/**
	 * @return The object attached to the currently selected row
	 */
	public Object getCurrentlySelectedObject(){
		return uiController.getAttachedObject(uiController.getSelectedItem(table));
	}
	
	/**
	 * @return the header object that is currently in use
	 */
	private Object getCurrentHeader(){
		return headers.get(currentClass);
	}
	
	public void headerClicked(){
//		int index = uiController.getSelectedIndex(getCurrentHeader());
//		String sort = uiController.getChoice(uiController.getSelectedItem(getCurrentHeader()), "sort");
//		boolean sortOrder = (sort.equals("ascent"))? true:false;
//		delegate.getQueryGenerator().setSort(, sortOrder);
	}
	
	public void clearResults(){
		uiController.removeAll(table);
	}
	
	public void setNoResultsMessage(String message){
		this.noResultsMessage = message;
	}
	
	public void setSelectionChangedMethod(String method, Object handler){
		Method m = null;
		try {
			m = handler.getClass().getMethod(method, new Class[]{Object.class});
		} catch (Exception e) {
			e.printStackTrace();
		}
		methods[SELECTION_CHANGED_INDEX][METHOD_INDEX] = m;
		methods[SELECTION_CHANGED_INDEX][HANDLER_INDEX] = handler;
	}
	
	public void setDoubleClickActionMethod(String method, Object handler){
		Method m = null;
		try {
			m = handler.getClass().getMethod(method, new Class[]{Object.class});
		} catch (Exception e) {
			e.printStackTrace();
		}
		methods[DOUBLE_CLICK_ACTION_INDEX][METHOD_INDEX] = m;
		methods[DOUBLE_CLICK_ACTION_INDEX][HANDLER_INDEX] = handler;
	}
	
	public void setResultsChangedMethod(String method, Object handler){
		Method m = null;
		try {
			m = handler.getClass().getMethod(method, (Class[]) null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		methods[RESULTS_CHANGED_INDEX][METHOD_INDEX] = m;
		methods[RESULTS_CHANGED_INDEX][HANDLER_INDEX] = handler;
	}
}
