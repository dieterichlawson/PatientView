package net.frontlinesms.plugins.patientview.ui.dialogs.searchareas;

import java.util.Collection;

import net.frontlinesms.ui.ExtendedThinlet;
import net.frontlinesms.ui.i18n.InternationalisationUtils;

public abstract class EntitySearchArea<E> {

	protected ExtendedThinlet uiController;
	
	/**When collapsed, this is the entity selected**/
	protected E currentEntity;
	
	/** state variable describing whether it is in collapsed/selected mode or search/selection mode**/
	private boolean isCollapsed;
	
	/**Thinlet objects**/
	
	/**the search bar **/
	private Object searchBar;
	
	/** the results table**/
	protected Object table;
	
	/** the main panel**/
	private Object mainPanel;
	
	protected SearchAreaDelegate<E> delegate;
	
	//i18n strings
	private static final String COLLAPSE_BUTTON ="searchareas.buttons.collapse";
	private static final String EXPAND_BUTTON ="searchareas.buttons.expand";
	private static final String SELECT_BUTTON ="searchareas.buttons.select";
	private static final String NO_RESULTS ="searchareas.no.result.mathing";
	private static final String INITIAL_SELECT ="searchareas.initial.select";
	private static final String NOT_SELECTED ="searchareas.initial.not.selected";
	
	public EntitySearchArea(E entity, ExtendedThinlet uiController, SearchAreaDelegate<E> delegate){
		this.uiController = uiController;
		currentEntity = entity;
		this.delegate = delegate;
		init();
	}
	private void init(){
		mainPanel = uiController.create("panel");
		uiController.setInteger(mainPanel, "gap", 6);
		uiController.setInteger(mainPanel, "colspan", 1);
		uiController.setInteger(mainPanel, "weightx", 1);
		uiController.setInteger(mainPanel, "weighty", 1);
		searchBar = uiController.createTextfield("searchBar","");
		uiController.setInteger(searchBar, "weightx", 1);
		uiController.setAction(searchBar, "searchBarKeyPressed(this.text)", null, this);
		table = uiController.create("table");
		uiController.setName(table, "resultsTable");
		uiController.setInteger(table, "weightx", 1);
		uiController.setInteger(table, "weighty", 1);
		uiController.setInteger(table, "colspan", 2);
		uiController.setPerform(table,"selectionMade()", null, this);
		uiController.setAction(table, "selectionChanged()", null, this);
		
		if(currentEntity!=null){
			collapse();
		}else{
			expand();
		}
	}
	
	protected abstract String getEntityTypeName();
	
	protected abstract String getEntityName(E entity);
	
	public void collapse(){
		uiController.removeAll(mainPanel);
		Object label;
		Object btn;
		if(currentEntity != null){
			label = uiController.createLabel(getEntityTypeName() + ": " + getEntityName(currentEntity));
		 	btn = uiController.createButton(InternationalisationUtils.getI18NString(EXPAND_BUTTON));
		}else{
			label = uiController.createLabel(getEntityTypeName() + ": "+ InternationalisationUtils.getI18NString(NOT_SELECTED));
		 	btn = uiController.createButton(InternationalisationUtils.getI18NString(SELECT_BUTTON) +" "+ getEntityTypeName());
		}
		uiController.setChoice(btn, "halign", "right");
		uiController.setInteger(btn,"weightx",1);
		uiController.setAction(btn,"expand()",null,this);
		uiController.setInteger(mainPanel, "columns", 2);
		uiController.add(mainPanel,label);
		uiController.add(mainPanel,btn);
		uiController.setInteger(mainPanel, "weighty", 0);
	}
	
	public void expand(){
		uiController.removeAll(mainPanel);
		uiController.setInteger(mainPanel, "columns", 2);
		uiController.add(mainPanel,searchBar);
		Object btn = uiController.createButton(InternationalisationUtils.getI18NString(COLLAPSE_BUTTON));
		uiController.setChoice(btn, "halign", "right");
		uiController.setAction(btn,"selectionMade()",null,this);
		uiController.add(mainPanel,btn);
		uiController.add(mainPanel,setUpTable());

		uiController.setInteger(mainPanel, "weighty", 1);
		
		if(currentEntity !=null){
			setTableResults(getEntitiesForString(getEntityName(currentEntity)));
			uiController.setSelectedIndex(table, 0);
			uiController.setText(searchBar, getEntityName(currentEntity));
		}
	}
	
	protected Object setUpTable(){
		String text = InternationalisationUtils.getI18NString(INITIAL_SELECT) + " " + getEntityTypeName();
		Object header = uiController.create("header");
		uiController.add(header,uiController.createColumn(text,null));
		uiController.removeAll(table);
		uiController.add(table,header);
		return table;
	}
	
	public void selectionMade(){
		currentEntity = (E) uiController.getAttachedObject(uiController.getSelectedItem(table));
		collapse();
	}
	
	public void selectionChanged(){
		E object = (E) uiController.getAttachedObject(uiController.getSelectedItem(table));
		delegate.selectionChanged(object);
	}
	
	public void searchBarKeyPressed(String text){
		uiController.removeAll(table);
		Collection<E> results = getEntitiesForString(text);
		if(results != null){
			setTableResults(results);
		}else{
			Object row = uiController.createTableRow(null);
			uiController.add(row,uiController.createTableCell(InternationalisationUtils.getI18NString(NO_RESULTS)+" \""+ text +"\""));
			uiController.add(table,row);
		}
	}
	
	protected void setTableResults(Collection<E> entities){
		for(E entity:entities){
			Object row = uiController.createTableRow(entity);
			uiController.add(row,uiController.createTableCell(getEntityName(entity)));
			uiController.add(table,row);
		}
	}
	
	/**This method fetches all entities related to the string s. DAO stuff goes here**/
	protected abstract Collection<E> getEntitiesForString(String s);
	
	public E getCurrentlySelectedEntity(){
		return currentEntity;
	}
	
	public Object getThinletPanel(){
		return mainPanel;
	}
}
