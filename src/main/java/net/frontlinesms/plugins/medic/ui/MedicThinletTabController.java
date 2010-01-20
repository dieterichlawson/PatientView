package net.frontlinesms.plugins.medic.ui;

import net.frontlinesms.Utils;
import net.frontlinesms.plugins.PluginController;
import net.frontlinesms.plugins.medic.MedicPluginController;
import net.frontlinesms.plugins.medic.data.domain.framework.MedicField;
import net.frontlinesms.plugins.medic.data.domain.framework.MedicForm;
import net.frontlinesms.plugins.medic.data.domain.framework.MedicFormField;
import net.frontlinesms.plugins.medic.data.domain.people.CommunityHealthWorker;
import net.frontlinesms.plugins.medic.data.domain.people.Patient;
import net.frontlinesms.plugins.medic.data.domain.people.User.Role;
import net.frontlinesms.plugins.medic.data.domain.response.MedicFieldResponse;
import net.frontlinesms.plugins.medic.data.domain.response.MedicFormResponse;
import net.frontlinesms.plugins.medic.data.domain.response.MedicMessageResponse;
import net.frontlinesms.plugins.medic.search.QueryGenerator;
import net.frontlinesms.plugins.medic.search.drilldownsearch.DrillDownSearchController;
import net.frontlinesms.plugins.medic.search.simplesearch.SimpleSearchController;
import net.frontlinesms.plugins.medic.userlogin.UserSessionManager;
import net.frontlinesms.plugins.medic.userlogin.UserSessionManager.AuthenticationResult;
import net.frontlinesms.ui.ThinletUiEventHandler;
import net.frontlinesms.ui.UiGeneratorController;

import org.apache.log4j.Logger;

public class MedicThinletTabController implements ThinletUiEventHandler,TableActionDelegate {

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
	
	//UI Resource file paths
	private static final String XML_MEDIC_TAB = "/ui/plugins/medic/medicTab.xml";
	private static final String XML_LOGIN_SCREEN = "/ui/plugins/medic/login_screen.xml";
	
	//other sub-controllers
	/** controller for the detailed view **/
	private DetailedViewController detailViewController;
	private AdvancedTable tableController;
	
	//Password string
	private String loginText;
	
	//Search Controllers
	private SimpleSearchController simpleSearch;
	private DrillDownSearchController drillDownSearch;
	
	//current search controls
	private static enum SearchState{
		SIMPLESEARCH(),
		DRILLDOWNSEARCH();
	}
	
	private SearchState currentSearchState;
	/**
	 * Create a new instance of this class.
	 * 
	 * @param pluginController
	 * @param uiController
	 */
	public MedicThinletTabController(MedicPluginController pluginController, UiGeneratorController uiController) {
		this.pluginController = pluginController;
		this.uiController = uiController;
		initialInit();
	}
	
	public Object getTab(){
		return mainTab;
	}
	
	/**
	 * performs the initialization required for the login screen
	 */
	public void initialInit(){
		loginText ="";
		loginScreen = uiController.loadComponentFromFile(XML_LOGIN_SCREEN, this);
		mainTab = uiController.loadComponentFromFile(XML_MEDIC_TAB, this);
		uiController.removeAll(uiController.find(mainTab,"medicTabMainPanel"));
		uiController.add(uiController.find(mainTab,"medicTabMainPanel"),loginScreen);
	}
	
	/**
	 * activated when a user presses enter while the password box has focus
	 * logs the user in
	 */
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
	
	/**
	 * ends the user's session, and returns them to the login screen
	 */
	public void logout(){
		UserSessionManager.getUserSessionManager().logout();
		uiController.removeAll(uiController.find(mainTab,"medicTabMainPanel"));
		uiController.add(uiController.find(mainTab,"medicTabMainPanel"),loginScreen);
		uiController.setText(uiController.find(loginScreen, "UsernameField"), "");
		uiController.setText(uiController.find(loginScreen, "PasswordField"), "");
		loginText="";
	}
	
	/**
	 * method that handles the keypresses from the password textbox
	 * @param response
	 */
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
	
	/**
	 * performs the initialization required for the main patient view screen
	 */
	public void init() {
			uiController.removeAll(uiController.find(mainTab,"medic"));
			uiController.add(uiController.find(mainTab,"medic"),uiController.find(uiController.loadComponentFromFile(XML_MEDIC_TAB, this),"medicTabMainPanel"));
			detailViewController = new DetailedViewController(uiController,pluginController.getApplicationContext(),this);
			uiController.setInteger(uiController.find(mainTab,"splitPanel"), "divider", (int) (uiController.getWidth() * 0.56));
			//if user is an admin, add the admin tab
			if(UserSessionManager.getUserSessionManager().getCurrentUserRole() == Role.ADMIN){
				AdminTab adminTab = new AdminTab(uiController,pluginController.getApplicationContext());
				uiController.add(uiController.getParent(getTab()),adminTab.getMainPanel());
			}
			//initialize the results table
			tableController = new AdvancedTable(this, uiController,true);
			tableController.putHeader(CommunityHealthWorker.class, new String[]{"Name","Age","Gender", "Phone Number"}, new String[]{"getName", "getStringAge","getStringGender","getPhoneNumber"});
			tableController.putHeader(Patient.class, new String[]{"Name","Age","Gender","CHW"}, new String[]{"getName", "getStringAge","getStringGender","getCHWName"});
			tableController.putHeader(MedicForm.class, new String[]{"Name"}, new String[]{"getName"});
			tableController.putHeader(MedicField.class, new String[]{"Label"}, new String[]{"getLabel"});
			tableController.putHeader(MedicFormField.class, new String[]{"Label","Parent Form"}, new String[]{"getLabel","getParentFormName"});
			tableController.putHeader(MedicMessageResponse.class, new String[]{"Sender","Date Sent", "Message Content"}, new String[]{"getSubmitterName","getStringDateSubmitted","getMessageContent"});
			tableController.putHeader(MedicFormResponse.class, new String[]{"Form Name", "Sender","Subject", "Date Submitted"}, new String[]{"getFormName","getSubmitterName","getSubjectName","getStringDateSubmitted"});
			tableController.putHeader(MedicFieldResponse.class, new String[]{"Field Label", "Sender","Subject", "Date Submitted","Response"}, new String[]{"getFieldLabel","getSubmitterName","getSubjectName","getStringDateSubmitted","getValue"});
			
			//intialize the search controllers
			simpleSearch = new SimpleSearchController(uiController,pluginController.getApplicationContext(),tableController);
			drillDownSearch = new DrillDownSearchController(uiController,pluginController.getApplicationContext(),tableController);
			//add the simple search controller
			uiController.add(uiController.find(mainTab,"searchContainer"), simpleSearch.getMainPanel());
			currentSearchState = SearchState.SIMPLESEARCH;
			//set the login label
			uiController.setText(uiController.find(mainTab,"userStatusLabel"), "Logged in as " + 
								 UserSessionManager.getUserSessionManager().getCurrentUser().getName());
	}
	
	//TableActionDelegate methods
	
	public void doubleClickAction(Object selectedObject) {
		if(currentSearchState == SearchState.DRILLDOWNSEARCH){
			drillDownSearch.drillDown(selectedObject);
		}
	}
	
	public void selectionChanged(Object selectedObject) {
		detailViewController.selectionChanged(selectedObject);
	}

	public Object getTable() {
		return uiController.find(mainTab, "resultTable");
	}

	public QueryGenerator getQueryGenerator() {
		return null;
	}
	
	public void refresh(){
		
	}
	
	/**
	 * Switches from Drill-Down Search to Simple search or vice-versa
	 * when one of the selection buttons is clicked
	 * @param sender the button that was clicked
	 */
	public void switchSearchControls(Object sender){
		if(uiController.getName(sender).equalsIgnoreCase("simpleSearchButton")){
			if(currentSearchState == SearchState.SIMPLESEARCH){
				return;
			}else{
				uiController.remove(drillDownSearch.getMainPanel());
				uiController.add(uiController.find(mainTab,"searchContainer"), simpleSearch.getMainPanel());
				simpleSearch.controllerWillAppear();
				currentSearchState = SearchState.SIMPLESEARCH;
			}
		}else if(uiController.getName(sender).equalsIgnoreCase("drillDownSearchButton")){
			if(currentSearchState == SearchState.DRILLDOWNSEARCH){
				return;
			}else{
				uiController.remove(simpleSearch.getMainPanel());
				uiController.add(uiController.find(mainTab,"searchContainer"), drillDownSearch.getMainPanel());
				drillDownSearch.controllerWillAppear();
				currentSearchState = SearchState.DRILLDOWNSEARCH;
			}
		}
	}


}
