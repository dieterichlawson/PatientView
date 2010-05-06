package net.frontlinesms.plugins.patientview.ui.administration;

import static net.frontlinesms.ui.i18n.InternationalisationUtils.getI18NString;

import java.util.List;

import net.frontlinesms.events.EventBus;
import net.frontlinesms.events.EventObserver;
import net.frontlinesms.events.FrontlineEvent;
import net.frontlinesms.plugins.patientview.data.domain.people.Person;
import net.frontlinesms.plugins.patientview.ui.AdvancedTableActionDelegate;
import net.frontlinesms.plugins.patientview.ui.AdvancedTableController;
import net.frontlinesms.plugins.patientview.ui.personpanel.PersonPanel;
import net.frontlinesms.ui.ThinletUiEventHandler;
import net.frontlinesms.ui.UiGeneratorController;

import org.springframework.context.ApplicationContext;

public abstract class PersonAdministrationPanelController<E extends Person> implements AdministrationTabPanel, ThinletUiEventHandler, AdvancedTableActionDelegate, EventObserver{

	/**
	 * The main panel of the person administration screen
	 */
	private Object mainPanel;
	protected final UiGeneratorController uiController;
	protected final ApplicationContext appCon;
	protected AdvancedTableController advancedTableController;
	private Object advancedTable;
	protected PersonPanel<E> currentPersonPanel;
	
	private static final String RESULTS_TABLE = "resultstable";
	private static final String ADD_BUTTON = "addbutton";
	private static final String REMOVE_BUTTON = "removebutton";
	private static final String EDIT_BUTTON = "editbutton";
	protected static final String FIELDS_PANEL = "fieldspanel";
	private static final String SEARCH_FIELD = "searchbox";
	
	private static final String MANAGE= "medic.common.labels.manage";
	private static final String ADD = "medic.common.labels.add";
	private static final String REMOVE = "medic.common.labels.remove";
	private static final String EDIT = "medic.common.labels.edit";
	private String UI_FILE_MANAGE_PERSON_PANEL = "/ui/plugins/patientview/admintab/search_action_panel.xml";
	
	
	public PersonAdministrationPanelController(UiGeneratorController uiController, ApplicationContext appCon){
		this.uiController = uiController;
		this.appCon = appCon;
		init();
	}

	private void init(){
		mainPanel = uiController.loadComponentFromFile(UI_FILE_MANAGE_PERSON_PANEL,this);
		advancedTable = uiController.find(mainPanel,RESULTS_TABLE);
		advancedTableController = new AdvancedTableController(this,uiController,advancedTable);
		putHeader();
		uiController.setText(uiController.find(mainPanel,"titleLabel"), getI18NString(MANAGE)+ " "+ getPersonType() + "s");
		uiController.setText(uiController.find(mainPanel,ADD_BUTTON), getI18NString(ADD)+ " " + getPersonType());
		uiController.setText(uiController.find(mainPanel,REMOVE_BUTTON), getI18NString(REMOVE)+ " " + getPersonType());
		uiController.setText(uiController.find(mainPanel,EDIT_BUTTON), getI18NString(EDIT)+ " " + getPersonType());
		uiController.setAction(uiController.find(mainPanel,EDIT_BUTTON), "editButtonClicked()", null, this);
		uiController.setAction(uiController.find(mainPanel,ADD_BUTTON), "addButtonClicked()", null, this);
		((EventBus) appCon.getBean("eventBus")).registerObserver(this);
		search("");
	}
	
	public void editButtonClicked(){
		currentPersonPanel.switchToEditingPanel();
	}
	
	public void addButtonClicked(){
		currentPersonPanel = getPersonPanelForPerson(null);
		uiController.removeAll(uiController.find(mainPanel,FIELDS_PANEL));
		uiController.add(uiController.find(mainPanel,FIELDS_PANEL), currentPersonPanel.getMainPanel());
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
	 * @param person
	 * @return A person panel of the proper type for the parameter
	 */
	protected abstract PersonPanel getPersonPanelForPerson(Person person);

	/**
	 * Used by the administration tab to get the main panel
	 * @see net.frontlinesms.plugins.patientview.ui.administration.AdministrationTabPanel#getPanel()
	 */
	public Object getPanel() {
		return mainPanel;
	}

	/** 
	 * Called when the selection of the results table is changed
	 * @see net.frontlinesms.plugins.patientview.ui.AdvancedTableActionDelegate#selectionChanged(java.lang.Object)
	 */
	public void selectionChanged(Object selectedObject) {
		currentPersonPanel = getPersonPanelForPerson((Person) selectedObject);
		uiController.removeAll(uiController.find(mainPanel,FIELDS_PANEL));
		uiController.add(uiController.find(mainPanel,FIELDS_PANEL), currentPersonPanel.getMainPanel());
		
	}

	/**
	 * Called when text in the search box changes. Should
	 * initiate the placement of the proper results in the results table
	 * @param text the text in the search box
	 */
	public void search(String text){
		advancedTableController.setResults(getPeopleForString(text));
		advancedTableController.setSelected(0);
	}
	
	/** @see net.frontlinesms.plugins.patientview.ui.AdvancedTableActionDelegate#doubleClickAction(java.lang.Object)*/
	public void doubleClickAction(Object selectedObject) {/*Do nothing*/}
	
	/** @see net.frontlinesms.plugins.patientview.ui.AdvancedTableActionDelegate#resultsChanged() */
	public void resultsChanged() {/*do nothing*/}
	
	/**
	 * @see net.frontlinesms.plugins.patientview.ui.AdvancedTableDataSource#refreshResults()
	 */
	public void notify(FrontlineEvent event){
		advancedTableController.setResults(getPeopleForString(uiController.getText(uiController.find(mainPanel, SEARCH_FIELD))));
	}
}
