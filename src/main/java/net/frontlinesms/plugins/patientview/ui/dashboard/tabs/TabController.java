package net.frontlinesms.plugins.patientview.ui.dashboard.tabs;

import net.frontlinesms.ui.UiGeneratorController;

import org.springframework.context.ApplicationContext;

public class TabController {

	protected UiGeneratorController uiController;
	protected ApplicationContext appCon;
	private Object tab;
	protected Object mainPanel;
	
	public TabController(UiGeneratorController uiController, ApplicationContext appCon) {
		super();
		this.uiController = uiController;
		this.appCon = appCon;
		this.tab = uiController.create("tab");
		this.mainPanel = uiController.create("panel");
		uiController.setWeight(mainPanel, 1, 1);
		uiController.setInteger(mainPanel, "top", 5);
		uiController.setInteger(mainPanel, "left", 5);
		uiController.setInteger(mainPanel, "right", 5);
		uiController.setInteger(mainPanel, "bottom", 5);
		uiController.add(tab,mainPanel);
	}

	public Object getTab() {
		return tab;
	}
	
	public Object getMainPanel(){
		return mainPanel;
	}
	
	protected void setTitle(String title){
		uiController.setText(tab, title);
	}
	
	protected void setIconPath(String iconPath){
		uiController.setIcon(tab,iconPath);
	}
	
}
