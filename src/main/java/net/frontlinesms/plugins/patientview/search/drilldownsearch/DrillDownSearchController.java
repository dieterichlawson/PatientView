package net.frontlinesms.plugins.patientview.search.drilldownsearch;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Stack;

import net.frontlinesms.plugins.patientview.data.domain.framework.MedicField;
import net.frontlinesms.plugins.patientview.data.domain.framework.MedicForm;
import net.frontlinesms.plugins.patientview.data.domain.people.CommunityHealthWorker;
import net.frontlinesms.plugins.patientview.data.domain.people.Patient;
import net.frontlinesms.plugins.patientview.search.QueryGenerator;
import net.frontlinesms.plugins.patientview.search.SearchController;
import net.frontlinesms.plugins.patientview.search.drilldownsearch.breadcrumbs.BreadCrumb;
import net.frontlinesms.plugins.patientview.search.drilldownsearch.breadcrumbs.CommunityHealthWorkerBreadCrumb;
import net.frontlinesms.plugins.patientview.search.drilldownsearch.breadcrumbs.EntityType;
import net.frontlinesms.plugins.patientview.search.drilldownsearch.breadcrumbs.FieldBreadCrumb;
import net.frontlinesms.plugins.patientview.search.drilldownsearch.breadcrumbs.FormBreadCrumb;
import net.frontlinesms.plugins.patientview.search.drilldownsearch.breadcrumbs.PatientBreadCrumb;
import net.frontlinesms.plugins.patientview.ui.AdvancedTable;
import net.frontlinesms.ui.ThinletUiEventHandler;
import net.frontlinesms.ui.UiGeneratorController;
import net.frontlinesms.ui.i18n.InternationalisationUtils;

import org.springframework.context.ApplicationContext;

public class DrillDownSearchController implements ThinletUiEventHandler, SearchController{
	
	//Thinlet Objects
	/**the main panel**/
	private Object searchPanel;
	/** the panel that holds the bread crumbs **/
	private Object breadCrumbsPanel;
	/**the panel that holds the toggle organizer buttons **/
	private Object organizerPanel;
	/** the searchbar **/
	private Object searchBar;
	/** The thinlet toggle buttons in the organizer panel **/
	private ArrayList<Object> organizerButtons;
	/**The very special "response" toggle button **/
	private Object responseButton;
	
	//Controllers
	/** The {@link UiGeneratorController} that shows the tab. */
	private UiGeneratorController uiController;
	private DrillDownQueryGenerator queryGenerator;
	private AdvancedTable tableController;
	
	//BreadCrumb stuff
	/**An ArrayList of the actual breadcrumb objects */
	private ArrayList<BreadCrumb> breadCrumbs;
	/**Stack for history**/
	private Stack<ArrayList<BreadCrumb>> backStack;
	/**Stack for future**/
	private Stack<ArrayList<BreadCrumb>> forwardStack;
	
	//UI XML files
	private static final String SEARCH_PANEL_XML = 	"/ui/plugins/patientview/drill_down_search.xml";

	//i18n constants
	private static final String RESPONSE_ORGANIZER_BUTTON = "medic.common.responses";
	private static final String PATIENT_ORGANIZER_BUTTON = "medic.common.patient";
	
	public DrillDownSearchController(UiGeneratorController uiController, ApplicationContext appCon, AdvancedTable tableController){
		this.uiController = uiController;
		this.tableController =tableController;
		queryGenerator = new DrillDownQueryGenerator(appCon,tableController);
		init();
	}
	private void init(){
		searchPanel = uiController.loadComponentFromFile(SEARCH_PANEL_XML, this);
		breadCrumbsPanel = uiController.find(searchPanel,"breadcrumbPanel");
		// Create and configure the organizerPanel
		organizerPanel = uiController.find(searchPanel,"organizerPanel");
		//Create the searchBar
		searchBar = uiController.find(searchPanel,"searchBar");
		//initialize the arrays
		breadCrumbs = new ArrayList<BreadCrumb>();
		organizerButtons = new ArrayList<Object>();
		// create all the organizer buttons by iterating through the entity types
		for (EntityType e: EntityType.values()) {
			Object btn = uiController.create("togglebutton");
			organizerButtons.add(btn);
			uiController.add(organizerPanel, btn);
			uiController.setString(btn, "text",e.getName());
			uiController.setChoice(btn, "halign", "center");
			uiController.setString(btn, "group", "organizerButtons");
			uiController.setAction(btn,"organizerButtonClicked(this,this.text)", null, this);
			uiController.setAttachedObject(btn, e);
			if(e.getName().equalsIgnoreCase(InternationalisationUtils.getI18NString(PATIENT_ORGANIZER_BUTTON)))
				uiController.setSelected(btn, true);
		}
		//add the "Responses" button
		Object btn = uiController.create("togglebutton");
		responseButton = btn;
		uiController.add(organizerPanel, btn);
		uiController.setString(btn, "text", InternationalisationUtils.getI18NString(RESPONSE_ORGANIZER_BUTTON));
		uiController.setChoice(btn, "halign", "center");
		uiController.setString(btn, "group", "organizerButtons");
		uiController.setVisible(btn, false);
		uiController.setAction(btn, "organizerButtonClicked(this,this.text)", null, this);
		//initial state of the organizer
		organizerButtonClicked(EntityType.PATIENT,InternationalisationUtils.getI18NString(PATIENT_ORGANIZER_BUTTON));
		//initialize the forward/backward stacks
		forwardStack = new Stack<ArrayList<BreadCrumb>>();
		backStack = new Stack<ArrayList<BreadCrumb>>();
		updateNavigationButtons();
	}
	
	/** 
	 * every time the search controls change, the navigation buttons
	 * need to be enabled or disabled. This method does that
	 */
	private void updateNavigationButtons() {
		Object forward = uiController.find(searchPanel,"medicForwardButton");
		Object back = uiController.find(searchPanel,"medicBackButton");
		if(forwardStack.size() > 0){
			uiController.setEnabled(forward,true);
		}else{
			uiController.setEnabled(forward,false);
		}
		if(backStack.size() > 0){
			uiController.setEnabled(back,true);
		}else{
			uiController.setEnabled(back, false);
		}
	}

	/**
	 * every time the user changes the search state, the previous 
	 * state is recorded in the history
	 * @param bc the list of breadcrumbs representing the search state to be stored
	 */
	private void addStateToHistory(ArrayList<BreadCrumb> bc){
		backStack.push(bc);
		forwardStack.removeAllElements();
		updateNavigationButtons();
		
	}
	
	/**
	 * After the Entity type is retrieved from the organizer button,
	 * this method is called. It is also used to automatically click
	 * an organizer button from code.
	 * @param type The entity that is being selected
	 * @param name The name of that entity
	 */
	private void organizerButtonClicked(EntityType type, String name){
		if(name.equals(InternationalisationUtils.getI18NString(RESPONSE_ORGANIZER_BUTTON))){
			queryGenerator.setSearchingForResponses(true);
		}else{
			queryGenerator.setSearchingForResponses(false);
		}
		if(type != null){
			queryGenerator.updateCurrentSearchEntity(type);
		}
		queryGenerator.startSearch(uiController.getText(searchBar));
		uiController.setText(uiController.find(searchPanel, "searchDescriptorLabel"), 
				SearchDescriptorGenerator.getSearchDescriptor(breadCrumbs, type,name.equals(InternationalisationUtils.getI18NString(RESPONSE_ORGANIZER_BUTTON)) ? true: false ));
		uiController.setFocus(searchBar);
	}
	
	/**
	 * This method is called by Thinlet when the buttons in the organizer
	 * panel are selected. It handles all the changes to the screen
	 * that need to be made when switching the entity being fetched
	 * @param type
	 * @param name
	 */
	public void organizerButtonClicked(Object btn, String name){
		//deselect all the butotns
		for(Object o: organizerButtons){
			uiController.setSelected(o, false);
		}
		uiController.setSelected(responseButton,  false);
		uiController.setSelected(btn, true);
		EntityType currentEntity = null;
		currentEntity= (EntityType) uiController.getAttachedObject(btn);
		organizerButtonClicked(currentEntity,name);
	}
	
	/**
	 * Called when a double click is performed on the results table
	 * @param o the attached object of the row that was clicked
	 */
	public void drillDown(Object o){
		BreadCrumb breadCrumb;
		if(o instanceof MedicForm ){
			breadCrumb = new FormBreadCrumb((MedicForm) o);
		}else if(o instanceof MedicField){
			breadCrumb = new FieldBreadCrumb((MedicField) o);
		}else if(o instanceof CommunityHealthWorker){
			breadCrumb = new CommunityHealthWorkerBreadCrumb((CommunityHealthWorker) o);
		}else if(o instanceof Patient){
			breadCrumb = new PatientBreadCrumb((Patient) o);
		}else{
			return;
		}
		addBreadCrumb(breadCrumb);
		//we don't need to pre-display the results because it's already done in the 'organizerbuttonclicked' method
		//queryGenerator.startSearch(uiController.getText(searchBar));
	}
	
	/**
	 * Starting point for adding a breadcrumb. Handles all the ui changes and search changes
	 * @param breadCrumb bread crumb to be added
	 */
	private void addBreadCrumb(BreadCrumb breadCrumb){
		//update the history
		ArrayList<BreadCrumb> toStore = new ArrayList<BreadCrumb>(breadCrumbs);
		addStateToHistory(toStore);
		//add breadCrumb to the internal model
		breadCrumbs.add(breadCrumb);
		//add button to display
		addCrumbToTrailDisplay(breadCrumb);
		//update the query generator
		queryGenerator.updateBreadCrumbs(breadCrumbs);
		//clear the results
		tableController.clearResults();
		//clear the search bar
		clearSearchBar();
		//update search organizer
		updateSearchOrganizer();
		//we don't need to do this here because this is only called in drill-down
		//queryGenerator.startSearch(uiController.getText(searchBar));	
	}
	
	/**
	 * Another break crumb updating method. Instead of adding a single bread crumb,
	 * this takes an entire list of breadcrumbs and changes the ui to reflect
	 * that state
	 * @param newBreadCrumbs
	 */
	private void setBreadCrumbs(ArrayList<BreadCrumb> newBreadCrumbs){
		//update the history
		ArrayList<BreadCrumb> toStore = new ArrayList<BreadCrumb>(breadCrumbs);
		addStateToHistory(toStore);
		//add breadCrumb to the internal model
		breadCrumbs = newBreadCrumbs;
		//add button to display
		makeDisplayTrailFromCrumbs(breadCrumbs);
		//update the query generator
		queryGenerator.updateBreadCrumbs(breadCrumbs);
		//clear the results
		tableController.clearResults();
		//clear the search bar
		clearSearchBar();
		//update search organizer
		updateSearchOrganizer();
	}
	
	/**
	 * Updates the search organizer every time a drilldown event occurs, 
	 * or when the breadcrumbs change
	 */
	private void updateSearchOrganizer(){
		//using a set because it doesn't allow for duplicate entities
		HashSet<EntityType> forbidden = new HashSet<EntityType>();
		//booleans for determining whether or not to display the "Responses" button
		boolean hasContent = false;
		boolean hasPerson = false;
		//get the restricted entities, including the entities of the breadcrumbs
		//themselves and any they have listed as restricted
		for(BreadCrumb b:breadCrumbs){
			forbidden.addAll(b.getRestrictedEntityTypes());
			forbidden.add(b.getEntityType());
			//check if breadcrumb is  content or a person
			if(b.getEntityType() == EntityType.FORM || b.getEntityType() == EntityType.FIELD){
				hasContent = true;
			}
			if(b.getEntityType() == EntityType.CHW || b.getEntityType() == EntityType.PATIENT){
				hasPerson = true;
			}
		}
		//Now, go through each button and see if it's on the list of restricted entities
		//If it is, make it invisible
		for(Object o:organizerButtons){
			if(forbidden.size() == 0){
				uiController.setVisible(o,true);
			}else{
				for(EntityType e:forbidden){
					try{
						if(uiController.getText(o).equals(e.getName())){
							uiController.setVisible(o, false);
							break;
						}else{
							uiController.setVisible(o,true);
						}
					}catch(Exception E){
						
					}
				}
			}
		}
		//if we have a content or a person, then display the results button
		if(hasPerson || hasContent){
			uiController.setVisible(responseButton, true);
		}else{
			uiController.setVisible(responseButton, false);
		}
		//make sure an organizer button is clicked
		if(uiController.getBoolean(responseButton, "visible")){
			organizerButtonClicked(responseButton,InternationalisationUtils.getI18NString(RESPONSE_ORGANIZER_BUTTON));
		}else{
			for(Object o: organizerButtons){
				if(uiController.getBoolean(o, "visible")){
					organizerButtonClicked(o,uiController.getText(o));	
					break;
				}
			}
		}
	}
	
	/**
	 * Performs the display side of adding a breadcrumb. Purely view-oriented
	 * @param breadCrumb
	 */
	private void addCrumbToTrailDisplay(BreadCrumb breadCrumb){
		if(uiController.getItems(breadCrumbsPanel).length ==0){
			Object clearAll = uiController.createButton("");
			uiController.add(breadCrumbsPanel,clearAll);
			uiController.setAction(clearAll,"clearAllBreadCrumbs()",null,this);
			uiController.setChoice(clearAll, "type", "link");
			uiController.setIcon(clearAll, "/icons/house.png");
		}
		if(uiController.getItems(breadCrumbsPanel).length >0 ){
			Object spacer = uiController.createLabel(">");
			uiController.add(breadCrumbsPanel,spacer);
		}
		Object bc = uiController.createButton(breadCrumb.getEntityTypeName() + ": " + breadCrumb.getName());
		uiController.add(breadCrumbsPanel,bc);
		uiController.setChoice(bc,"type", "link");
		uiController.putProperty(bc, "bcIndex", breadCrumbs.size()-1);
		uiController.setAction(bc,"breadCrumbClicked(this)",null,this);
	}
	
	/**
	 * Analog to addCrumbToTrailDisplay, instead of taking 1 breadcrumb, it
	 * takes an entire list of breadcrumbs
	 * @param breadCrumbs
	 */
	private void makeDisplayTrailFromCrumbs(ArrayList<BreadCrumb> breadCrumbs){
		uiController.removeAll(breadCrumbsPanel);
		if(uiController.getItems(breadCrumbsPanel).length ==0){
			Object clearAll = uiController.createButton("");
			uiController.add(breadCrumbsPanel,clearAll);
			uiController.setAction(clearAll,"clearAllBreadCrumbs()",null,this);
			uiController.setChoice(clearAll, "type", "link");
			uiController.setIcon(clearAll, "/icons/house.png");
		}
		for(BreadCrumb breadCrumb: breadCrumbs){
			if(uiController.getItems(breadCrumbsPanel).length !=0){
				Object spacer = uiController.createLabel(">");
				uiController.add(breadCrumbsPanel,spacer);
			}
			Object bc = uiController.createButton(breadCrumb.getEntityTypeName() + ": " + breadCrumb.getName());
			uiController.add(breadCrumbsPanel,bc);
			uiController.setChoice(bc,"type", "link");
			uiController.putProperty(bc, "bcIndex", breadCrumbs.indexOf(breadCrumb));
			uiController.setAction(bc,"breadCrumbClicked(this)",null,this);
		}
	}
	
	/**
	 * Action method called by thinlet when a breadcrumb is clicked
	 * @param button
	 */
	public void breadCrumbClicked(Object button){
		int index = Integer.valueOf(uiController.getProperty(button, "bcIndex").toString());
		setBreadCrumbs(new ArrayList<BreadCrumb>(breadCrumbs.subList(0, index+1)));	
	}
	
	/**
	 * Action method called by thinlet when the 'home button' is clicked
	 * @param button
	 */
	public void clearAllBreadCrumbs(){
		setBreadCrumbs(new ArrayList<BreadCrumb>());
	}

	/**
	 * Action method called by thinlet when the 'back button' is clicked
	 * @param button
	 */
	public void back() {
		//push breadCrumbs onto forward stack
		ArrayList<BreadCrumb> toStore = new ArrayList<BreadCrumb>(breadCrumbs);
		forwardStack.push(toStore);
		//pull breadcrumbs from history
		breadCrumbs = backStack.pop();
		//update display
		makeDisplayTrailFromCrumbs(breadCrumbs);
		//update the query generator
		queryGenerator.updateBreadCrumbs(breadCrumbs);
		//clear the results
		tableController.clearResults();
		//update the forward/back buttons
		updateNavigationButtons();
		//update search organizer
		updateSearchOrganizer();
	}

	/**
	 * Action method called by thinlet when the 'forward button' is clicked
	 * @param button
	 */
	public void forward() {
		//push the state onto the back stack
		ArrayList<BreadCrumb> toStore = new ArrayList<BreadCrumb>(breadCrumbs);
		backStack.push(toStore);
		//pull breadcrumbs from forward stack
		breadCrumbs =forwardStack.pop();
		//update display
		makeDisplayTrailFromCrumbs(breadCrumbs);
		//update the query generator
		queryGenerator.updateBreadCrumbs(breadCrumbs);
		//clear the results
		tableController.clearResults();
		//update the forward/back buttons
		updateNavigationButtons();
		//update search organizer
		updateSearchOrganizer();
	}
	
	/**
	 * Action method called by thinlet when the 'refresh button' is clicked
	 * @param button
	 */
	public void refresh(){
		queryGenerator.startSearch(uiController.getText(searchBar));
	}

	/**
	 * Action method called by thinlet when the search field text changes
	 * @param button
	 */
	public void search(String searchText) {
			queryGenerator.startSearch(searchText);
	}
	
	private void clearSearchBar(){
		uiController.setString(searchBar,"text","");
	}
	
	public Object getMainPanel(){
		return searchPanel;
	}
	public void controllerWillAppear() {
		refresh();
	}
	public QueryGenerator getQueryGenerator() {
		return queryGenerator;
	}
}
