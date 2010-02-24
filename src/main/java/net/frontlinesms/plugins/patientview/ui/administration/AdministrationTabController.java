package net.frontlinesms.plugins.patientview.ui.administration;

import java.util.Map;

import net.frontlinesms.plugins.patientview.ui.administration.people.CommunityHealthWorkerAdministrationPanelController;
import net.frontlinesms.plugins.patientview.ui.administration.people.PatientAdministrationPanelController;
import net.frontlinesms.plugins.patientview.ui.administration.people.UserAdministrationPanelController;
import net.frontlinesms.ui.ThinletUiEventHandler;
import net.frontlinesms.ui.UiGeneratorController;

import org.springframework.context.ApplicationContext;

public class AdministrationTabController implements ThinletUiEventHandler{
		
	/** the Admin tab */
	private Object adminTab;
	/** the list of actions that can be performed*/
	private Object actionList;
	/** the panel containing the list to the left and the juicy stuff to the right
	 * you add the options to this panel to make them appear*/
	private Object splitPanel;
	
	/**the Thinlet XML files used for this tab **/
	private static final String UI_FILE_MAINTAB =  "/ui/plugins/patientview/admintab/admin_tab.xml";
	private static final String TASK_LIST = "tasklist";
	private static final String SPLIT_PANEL = "splitpanel";
	private static final String ACTION_PANEL = "actionpanel";
		
	UiGeneratorController uiController;
	ApplicationContext appCon;
	
	
	public AdministrationTabController(UiGeneratorController uiController, ApplicationContext appCon){
		this.uiController = uiController;
		this.appCon = appCon;
		init();
		
	}
	
	public void init(){
		//init the main, static components
		adminTab = uiController.loadComponentFromFile(UI_FILE_MAINTAB, this);
		actionList = uiController.find(adminTab, TASK_LIST);
		splitPanel = uiController.find(adminTab, SPLIT_PANEL);
		//init the different choices for the action list
		PatientAdministrationPanelController patientAdmin = new PatientAdministrationPanelController(uiController,appCon);
		CommunityHealthWorkerAdministrationPanelController chwAdmin = new CommunityHealthWorkerAdministrationPanelController(uiController,appCon);
		UserAdministrationPanelController userAdmin = new UserAdministrationPanelController(uiController,appCon);
		Object managePatientsChoice = uiController.createListItem(patientAdmin.getListItemTitle(), patientAdmin.getPanel());
		Object manageCHWsChoice = uiController.createListItem(chwAdmin.getListItemTitle(), chwAdmin.getPanel());
		Object manageUsersChoice = uiController.createListItem(userAdmin.getListItemTitle(), userAdmin.getPanel());
		//add the choices to the list
		uiController.add(actionList, managePatientsChoice);
		uiController.add(actionList, manageCHWsChoice);
		uiController.add(actionList, manageUsersChoice);	
		setSelection(0);
	}

	
	private Object getActionPanel(){
		return uiController.find(splitPanel, ACTION_PANEL);
	}
	
	private void setSelection(int index){
		Object panel = uiController.getAttachedObject(uiController.getItem(actionList, index));
		uiController.remove(getActionPanel());
		uiController.add(splitPanel, panel);
		uiController.setSelectedIndex(actionList, index);
	}
	
	public void listSelectionChanged(){
		Object panel = uiController.getAttachedObject(uiController.getSelectedItem(actionList));
		uiController.remove(getActionPanel());
		uiController.add(splitPanel, panel);
	}
	
	public Object getMainPanel(){
		return adminTab;
	}
	
}
