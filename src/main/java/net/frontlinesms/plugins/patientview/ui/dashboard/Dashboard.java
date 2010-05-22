package net.frontlinesms.plugins.patientview.ui.dashboard;

import net.frontlinesms.plugins.patientview.utils.PVConstants;
import net.frontlinesms.ui.ThinletUiEventHandler;
import net.frontlinesms.ui.UiGeneratorController;

import org.springframework.context.ApplicationContext;

public abstract class Dashboard implements ThinletUiEventHandler{

	private static final String UI_FILE = "/ui/plugins/patientview/dashboard/dashboard.xml";

	//cached thinlet objects
	protected Object mainPanel;
	protected Object leftPanel;
	protected Object tabbedPanel;
	
	protected Object stashedPanel;
	
	//controllers
	protected UiGeneratorController uiController;
	protected ApplicationContext appCon;
	
	public Dashboard(UiGeneratorController uiController, ApplicationContext appCon){
		this.uiController = uiController;
		this.appCon = appCon;
		mainPanel = uiController.loadComponentFromFile(UI_FILE, this); 
		leftPanel = uiController.find(mainPanel,"leftPanel");
		tabbedPanel = uiController.find(mainPanel,"tabPanel");
	}
	
	public Object getMainPanel(){
		return mainPanel;
	}
	
	
	public void expandDashboard(){
		stashedPanel = uiController.find(PVConstants.MAIN_PANEL_NAME);
		uiController.removeAll(uiController.find(PVConstants.TAB_NAME));
		uiController.add(uiController.find(PVConstants.TAB_NAME),getMainPanel());	
	}

	public void collapseDashboard(){
		uiController.removeAll(uiController.find(PVConstants.TAB_NAME));
		uiController.add(uiController.find(PVConstants.TAB_NAME),stashedPanel);
	}


	
}

