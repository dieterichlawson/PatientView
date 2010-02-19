package net.frontlinesms.plugins.patientview.ui.expandeddetailview;

import net.frontlinesms.ui.ThinletUiEventHandler;
import net.frontlinesms.ui.UiGeneratorController;

import org.springframework.context.ApplicationContext;

public abstract class ExpandedDetailView implements ThinletUiEventHandler{

	private static final String UI_FILE = "/ui/plugins/patientview/expanded_detail_view.xml";

	//cached thinlet objects
	protected Object mainPanel;
	protected Object leftPanel;
	protected Object rightPanel;
	protected Object middlePanel;
	
	//controllers
	protected UiGeneratorController uiController;
	protected ApplicationContext appCon;
	
	public ExpandedDetailView(UiGeneratorController uiController, ApplicationContext appCon){
		this.uiController = uiController;
		this.appCon = appCon;
		mainPanel = uiController.loadComponentFromFile(UI_FILE, this); 
		leftPanel = uiController.find(mainPanel,"leftPanel");
		rightPanel = uiController.find(mainPanel,"rightPanel");
		middlePanel = uiController.find(mainPanel,"middlePanel");
	}
	
	public Object getMainPanel(){
		return mainPanel;
	}
}

