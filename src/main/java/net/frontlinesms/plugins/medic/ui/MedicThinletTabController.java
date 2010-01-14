package net.frontlinesms.plugins.medic.ui;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Stack;
import java.util.Timer;

import net.frontlinesms.Utils;
import net.frontlinesms.plugins.PluginController;
import net.frontlinesms.plugins.medic.MedicPluginController;
import net.frontlinesms.plugins.medic.data.domain.framework.MedicField;
import net.frontlinesms.plugins.medic.data.domain.framework.MedicForm;
import net.frontlinesms.plugins.medic.data.domain.people.CommunityHealthWorker;
import net.frontlinesms.plugins.medic.data.domain.people.Patient;
import net.frontlinesms.plugins.medic.data.domain.people.User.Role;
import net.frontlinesms.plugins.medic.data.querygenerator.BreadCrumb;
import net.frontlinesms.plugins.medic.data.querygenerator.CommunityHealthWorkerBreadCrumb;
import net.frontlinesms.plugins.medic.data.querygenerator.EntityType;
import net.frontlinesms.plugins.medic.data.querygenerator.FieldBreadCrumb;
import net.frontlinesms.plugins.medic.data.querygenerator.FormBreadCrumb;
import net.frontlinesms.plugins.medic.data.querygenerator.PatientBreadCrumb;
import net.frontlinesms.plugins.medic.data.querygenerator.QueryGenerator;
import net.frontlinesms.plugins.medic.data.querygenerator.SearchDescriptorGenerator;
import net.frontlinesms.plugins.medic.userlogin.UserSessionManager;
import net.frontlinesms.plugins.medic.userlogin.UserSessionManager.AuthenticationResult;
import net.frontlinesms.ui.ThinletUiEventHandler;
import net.frontlinesms.ui.UiGeneratorController;

import org.apache.log4j.Logger;

public class MedicThinletTabController implements ThinletUiEventHandler{

	// > INSTANCE PROPERTIES
	/** Logging object */
	private final Logger LOG = Utils.getLogger(this.getClass());
	/** The {@link PluginController} that owns this class. */
	private final MedicPluginController pluginController; //potentially don't need this
	
	public MedicPluginController getPluginController() {
		return pluginController;
	}

	/** The {@link UiGeneratorController} that shows the tab. */
	private final UiGeneratorController uiController;
	
	//Thinlet UI objects
	/** the main tab**/
	private Object mainTab;
	/**login screen*/
	private Object loginScreen;
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
	
	//UI Resource file paths
	private static final String XML_MEDIC_TAB = "/ui/plugins/medic/medicTab.xml";
	private static final String XML_LOGIN_SCREEN = "/ui/plugins/medic/login_screen.xml";
	//other sub-controllers
	/** controller for the table of results **/
	private ResultsTableController tableController;
	/** controller for the detailed view **/
	private DetailedViewController detailViewController;
	/**object that generates queries, accesses the database, and returns results **/
	private QueryGenerator queryGenerator;
	/** Timer to properly initialize the thinlet UI objects. **/
	private Timer timer;

	//lists of objects
	
	/**An ArrayList of the actual breadcrumb objects */
	private ArrayList<BreadCrumb> breadCrumbs;
	/**Stack for history**/
	private Stack<ArrayList<BreadCrumb>> backStack;
	/**Stack for future**/
	private Stack<ArrayList<BreadCrumb>> forwardStack;
	
	//Password string
	private String loginText;
	
	/**
	 * Create a new instance of this class.
	 * 
	 * @param pluginController
	 * @param uiController
	 */
	public MedicThinletTabController(MedicPluginController pluginController, UiGeneratorController uiController) {
		this.pluginController = pluginController;
		this.uiController = uiController;
		timer = new Timer();
		initialInit();
	}
	
	public Object getTab(){
		return mainTab;
	}

	public void initialInit(){
		loginText ="";
		loginScreen = uiController.loadComponentFromFile(XML_LOGIN_SCREEN, this);
		mainTab = uiController.loadComponentFromFile(XML_MEDIC_TAB, this);
		uiController.removeAll(uiController.find(mainTab,"medicTabMainPanel"));
		uiController.add(uiController.find(mainTab,"medicTabMainPanel"),loginScreen);
	}
	
	public void login(){
		String username = uiController.getText(uiController.find(loginScreen, "UsernameField"));
		
		AuthenticationResult result = UserSessionManager.getUserSessionManager().login(username, loginText);
		if(result == AuthenticationResult.NOSUCHUSER){
			uiController.setText(uiController.find(loginScreen,"topLabel"), "There is no such username in the system.");
		}else if(result == AuthenticationResult.WRONGPASSWORD){
			uiController.setText(uiController.find(loginScreen,"topLabel"), "Your password was entered incorrectly.");
		}if(result == AuthenticationResult.SUCCESS){
			init();
		}
	}
	
	public void logout(){
		UserSessionManager.getUserSessionManager().logout();
		uiController.removeAll(uiController.find(mainTab,"medicTabMainPanel"));
		uiController.add(uiController.find(mainTab,"medicTabMainPanel"),loginScreen);
		uiController.setText(uiController.find(loginScreen, "UsernameField"), "");
		uiController.setText(uiController.find(loginScreen, "PasswordField"), "");
		loginText="";
	}
	
	public void textBoxKeyPressed(String response){
		String newText = response.substring(response.lastIndexOf("*") + 1);
		if(response.lastIndexOf("*") <  loginText.length() -1){
			loginText = loginText.substring(0, response.lastIndexOf("*") + 1) + newText;
		}else{
			loginText = loginText + newText;
		}
		String mask = "";
		for(int i = 0; i < loginText.length();i++){
			mask = mask + "*";
		}
		uiController.setText(uiController.find(loginScreen, "PasswordField"), mask);	
	}
	
	public void init() {
			uiController.removeAll(uiController.find(mainTab,"medic"));
			uiController.add(uiController.find(mainTab,"medic"),uiController.find(uiController.loadComponentFromFile(XML_MEDIC_TAB, this),"medicTabMainPanel"));
			// create and configure the BC panel
			breadCrumbsPanel = uiController.find(mainTab,"breadcrumbPanel");
			// Create and configure the organizerPanel
			organizerPanel = uiController.find(mainTab,"organizerPanel");
			//Create the searchBar
			searchBar = uiController.find(mainTab,"searchBar");
			//initialize the arrays
			breadCrumbs = new ArrayList<BreadCrumb>();
			organizerButtons = new ArrayList<Object>();
			// create all the organizer buttons by iterating through the entity types
			for (EntityType e: EntityType.values()) {
				Object btn = uiController.create("togglebutton");
				organizerButtons.add(btn);
				uiController.add(organizerPanel, btn);
				uiController.setString(btn, "text", e.getName());
				uiController.setChoice(btn, "halign", "center");
				uiController.setString(btn, "group", "organizerButtons");
				uiController.setAction(btn,"organizerButtonClicked(this,this.text)", null, this);
				uiController.setAttachedObject(btn, e);
				if(e.getName().equals("CHW"))
					uiController.setSelected(btn, true);
			}
			//add the "Responses" button
			Object btn = uiController.create("togglebutton");
			responseButton = btn;
			uiController.add(organizerPanel, btn);
			uiController.setString(btn, "text", "Responses");
			uiController.setChoice(btn, "halign", "center");
			uiController.setString(btn, "group", "organizerButtons");
			uiController.setVisible(btn, false);
			uiController.setAction(btn, "organizerButtonClicked(this,this.text)", null, this);
			
			//initialize the other controllers
			tableController = new ResultsTableController(uiController,this);
			queryGenerator = new QueryGenerator(pluginController.getApplicationContext(),tableController);
			detailViewController = new DetailedViewController(uiController,pluginController.getApplicationContext(),this);
			//initial state of the organizer
			organizerButtonClicked(EntityType.CHW,"CHW");
			//initialize the forward/backward stacks
			forwardStack = new Stack<ArrayList<BreadCrumb>>();
			backStack = new Stack<ArrayList<BreadCrumb>>();
			updateNavigationButtons();
			if(UserSessionManager.getUserSessionManager().getCurrentUserRole() == Role.ADMIN){
				AdminTab adminTab = new AdminTab(uiController,pluginController.getApplicationContext());
				uiController.add(uiController.getParent(getTab()),adminTab.getMainPanel());
			}
	}
	
	private void updateNavigationButtons() {
		Object forward = uiController.find(getTab(),"medicForwardButton");
		Object back = uiController.find(getTab(),"medicBackButton");
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

	private void addStateToHistory(ArrayList<BreadCrumb> bc){
		backStack.push(bc);
		forwardStack.removeAllElements();
		updateNavigationButtons();
		
	}
	
	private void organizerButtonClicked(EntityType type, String name){
		if(name.equals("Responses")){
			queryGenerator.setSearchingForResponses(true);
		}else{
			queryGenerator.setSearchingForResponses(false);
		}
		if(type != null){
			queryGenerator.updateCurrentSearchEntity(type);
		}
		queryGenerator.startSearch(uiController.getText(searchBar));
		uiController.setText(uiController.find(mainTab, "searchDescriptorLabel"), 
				SearchDescriptorGenerator.getSearchDescriptor(breadCrumbs, type,name.equals("Responses") ? true: false ));
		uiController.setFocus(searchBar);
	}
	
	public void organizerButtonClicked(Object btn, String name){
		for(Object o: organizerButtons){
			uiController.setSelected(o, false);
		}
		uiController.setSelected(responseButton,  false);
		uiController.setSelected(btn, true);
		EntityType currentEntity = null;
		try{
			currentEntity= (EntityType) uiController.getAttachedObject(btn);
		}catch(Throwable t){}
		organizerButtonClicked(currentEntity,name);
	}
	
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
	
	private void updateBreadCrumbs(ArrayList<BreadCrumb> newBreadCrumbs){
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
			organizerButtonClicked(responseButton,"Responses");
		}else{
			for(Object o: organizerButtons){
				if(uiController.getBoolean(o, "visible")){
					organizerButtonClicked(o,uiController.getText(o));	
					break;
				}
			}
		}
	}
	
	
	//adds a bread crumb to the display panel. Purely view-oriented
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
	
	//creates a bc trail from an arraylist of bcs. Purely view oriented
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
	
	//in the future, you will need to wire this up refilter based on the button clicked
	public void breadCrumbClicked(Object button){
		int index = Integer.valueOf(uiController.getProperty(button, "bcIndex").toString());
		System.out.println("Bread crumb "+ index + " clicked");
		updateBreadCrumbs(new ArrayList<BreadCrumb>(breadCrumbs.subList(0, index+1)));	
	}
	
	public void clearAllBreadCrumbs(){
		updateBreadCrumbs(new ArrayList<BreadCrumb>());
	}

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
	
	public void refresh(){
		queryGenerator.startSearch(uiController.getText(searchBar));
	}

	public void search(String searchText) {
			queryGenerator.startSearch(searchText);
	}
	
	public UiGeneratorController getUiController() {
		return uiController;
	}

	public ResultsTableController getTableController() {
		return tableController;
	}

	public DetailedViewController getDetailViewController() {
		return detailViewController;
	}
	
	public QueryGenerator getQueryGenerator(){
		return queryGenerator;
	}
	
	private void clearSearchBar(){
		uiController.setString(searchBar,"text","");
	}

}
