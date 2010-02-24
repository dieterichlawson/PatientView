package net.frontlinesms.plugins.patientview.ui.administration.people;

import java.util.List;

import net.frontlinesms.plugins.patientview.data.domain.people.Person;
import net.frontlinesms.plugins.patientview.ui.AdvancedTableController;
import net.frontlinesms.plugins.patientview.ui.PersonPanel;
import net.frontlinesms.plugins.patientview.ui.AdvancedTableActionDelegate;
import net.frontlinesms.plugins.patientview.ui.administration.AdministrationTabPanel;
import net.frontlinesms.ui.ThinletUiEventHandler;
import net.frontlinesms.ui.UiGeneratorController;

import org.springframework.context.ApplicationContext;

public abstract class PersonAdministrationPanelController<E extends Person> implements AdministrationTabPanel, ThinletUiEventHandler, AdvancedTableActionDelegate{

	/**
	 * The main panel of the person administration screen
	 */
	private Object mainPanel;
	private UiGeneratorController uiController;
	protected ApplicationContext appCon;
	protected AdvancedTableController advancedTableController;
	private Object advancedTable;
	protected PersonPanel<E> currentPersonPanel;
	
	private static final String RESULTS_TABLE = "resultstable";
	private static final String ADD_BUTTON = "addbutton";
	private static final String REMOVE_BUTTON = "removebutton";
	private static final String EDIT_BUTTON = "editbutton";
	private static final String FIELDS_PANEL = "fieldspanel";
	
	
	private String UI_FILE_MANAGE_PERSON_PANEL = "/ui/plugins/patientview/admintab/search_action_panel.xml";
	
	
	public PersonAdministrationPanelController(UiGeneratorController uiController, ApplicationContext appCon){
		this.uiController = uiController;
		this.appCon = appCon;
		init();
	}

	private void init(){
		mainPanel = uiController.loadComponentFromFile(UI_FILE_MANAGE_PERSON_PANEL,this);
		advancedTableController = new AdvancedTableController(this,uiController,true);
		advancedTable = uiController.find(mainPanel,RESULTS_TABLE);
		putHeader();
		uiController.setText(uiController.find(mainPanel,ADD_BUTTON), "Add " + getPersonType());
		uiController.setText(uiController.find(mainPanel,REMOVE_BUTTON), "Remove " + getPersonType());
		uiController.setText(uiController.find(mainPanel,EDIT_BUTTON), "Edit " + getPersonType());
		uiController.setAction(uiController.find(mainPanel,EDIT_BUTTON), "editButtonClicked()", null, this);
		search("");
	}
	
	public void editButtonClicked(){
		currentPersonPanel.switchToEditingPanel();
	}
	
	/**
	 * should set the header of the advanced table as is required
	 * for the class of person being displayed
	 */
	protected abstract void putHeader();
	
	/**
	 * @return a name for the type of person that is being displayed
	 */
	protected abstract String getPersonType();
	
	/**
	 * @return an arraylist of people for the search string s
	 */
	protected abstract List<E> getPeopleForString(String s);

	/**
	 * Used by the administration tab to get the main panel
	 * @see net.frontlinesms.plugins.patientview.ui.administration.AdministrationTabPanel#getPanel()
	 */
	public Object getPanel() {
		return mainPanel;
	}

	/**
	 * Used by the advanced table controller to get a reference to the table it should be controlling
	 * @see net.frontlinesms.plugins.patientview.ui.AdvancedTableActionDelegate#getTable()
	 */
	public Object getTable() {
		return advancedTable;
	}

	/** 
	 * Called when the selection of the results table is changed
	 * @see net.frontlinesms.plugins.patientview.ui.AdvancedTableActionDelegate#selectionChanged(java.lang.Object)
	 */
	public void selectionChanged(Object selectedObject) {
		PersonPanel<E> panel = new PersonPanel<E>(uiController,(E) selectedObject,appCon);
		uiController.removeAll(uiController.find(mainPanel,FIELDS_PANEL));
		uiController.add(uiController.find(mainPanel,FIELDS_PANEL), panel.getMainPanel());
		currentPersonPanel = panel;
	}
	
	/**
	 * Called when text in the search box changes. Should
	 * initiate the placement of the proper results in the results table
	 * @param text the text in the search box
	 */
	public void search(String text){
		advancedTableController.setResults(getPeopleForString(text));
		advancedTableController.setSelected(1);
	}
	
	/** @see net.frontlinesms.plugins.patientview.ui.AdvancedTableActionDelegate#doubleClickAction(java.lang.Object)*/
	public void doubleClickAction(Object selectedObject) {/*Do nothing*/}
	
	/** @see net.frontlinesms.plugins.patientview.ui.AdvancedTableActionDelegate#resultsChanged() */
	public void resultsChanged() {/*do nothing*/}
}
