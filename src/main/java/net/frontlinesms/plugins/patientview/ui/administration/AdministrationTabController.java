package net.frontlinesms.plugins.patientview.ui.administration;

import java.util.ArrayList;

import net.frontlinesms.plugins.patientview.importer.CsvImporterPanelController;
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
	private static final String ACTION_PANEL = "actionPanel";
		
	UiGeneratorController uiController;
	ApplicationContext appCon;
	
	private ArrayList<AdministrationTabPanel> panels;
	
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
		panels = new ArrayList<AdministrationTabPanel>();
		panels.add(new PatientAdministrationPanelController(uiController,appCon));
		panels.add(new CommunityHealthWorkerAdministrationPanelController(uiController,appCon));
		panels.add(new UserAdministrationPanelController(uiController,appCon));
//			panels.add(new FormAdministrationPanelController(uiController,appCon));
		panels.add(new SecurityPanelController(uiController));
		panels.add(new AttributeAdministrationPanelController(uiController,appCon));
		panels.add(new FormResponseMappingPanelController(uiController,appCon));
		panels.add(new CsvImporterPanelController(uiController,appCon));
		//create all the list items
		for(AdministrationTabPanel panel: panels){
			Object listItem = uiController.createListItem(panel.getListItemTitle(), panel.getPanel());
			uiController.add(actionList,listItem);
		}
		setSelection(0);
	}

	
	private Object getActionPanel(){
		return uiController.find(splitPanel, ACTION_PANEL);
	}
	
	private void setSelection(int index){
		Object panel = uiController.getAttachedObject(uiController.getItem(actionList, index));
		uiController.removeAll(getActionPanel());
		uiController.add(getActionPanel(), panel);
		uiController.setSelectedIndex(actionList, index);
	}
	
	public void listSelectionChanged(){
		Object panel = uiController.getAttachedObject(uiController.getSelectedItem(actionList));
		uiController.removeAll(getActionPanel());
		uiController.add(getActionPanel(), panel);
	}
	
	public Object getMainPanel(){
		return adminTab;
	}
	
}
