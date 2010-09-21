package net.frontlinesms.plugins.patientview.flags.ui;

import net.frontlinesms.ui.UiGeneratorController;

public class IconChooser {

	private UiGeneratorController uiController;
	private Object mainPanel;
	private String selectedIconPath;
	
	public IconChooser(UiGeneratorController uiController){
		this.uiController = uiController;
		this.mainPanel = uiController.createPanel("");
		uiController.setColumns(mainPanel, 12);
		uiController.setInteger(mainPanel, "top", 5);
		uiController.setInteger(mainPanel, "left", 5);
		uiController.setInteger(mainPanel, "right", 5);
		uiController.setInteger(mainPanel, "bottom", 5);
		
		
	}
	public String getSelectedIconPath(){
		return selectedIconPath;
	}
	
	private void loadIcons(){
		
		
	}
	
	private void addIcon(String iconPath){
		Object button = uiController.create("togglebutton");
		uiController.setString(button,"group","icons");
		uiController.setIcon(button, iconPath);
		uiController.setAction(button, "iconClicked(this)", null, this);
		uiController.setAttachedObject(button, iconPath);
		uiController.add(mainPanel,button);
		
	}
	
	public void iconClicked(Object button){
		selectedIconPath = uiController.getAttachedObject(button,String.class);
	}
	
}
