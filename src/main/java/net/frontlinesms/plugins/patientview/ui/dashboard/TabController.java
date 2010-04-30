package net.frontlinesms.plugins.patientview.ui.dashboard;

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
