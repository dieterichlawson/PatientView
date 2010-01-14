package net.frontlinesms.plugins.medic.ui;

import java.util.ArrayList;
import java.util.Vector;

import net.frontlinesms.ui.UiGeneratorController;

public class SideTabBar {
	/**the buttons in the tab bar*/
	private Vector<Object> buttons;
	/**the panels for the buttons**/
	private Vector<Object> panels;
	/**the panel containing the buttons*/
	private Object buttonPanel;
	private Object tabPanel;
	
	public SideTabBar(ArrayList<String> titles, ArrayList<Object> panels, UiGeneratorController uiController){
		buttons = new Vector<Object>();
		for(String s:titles){
			Object button = uiController.createButton(s);
			uiController.setChoice(button, "type", "link");
			uiController.add(buttonPanel,button);
			buttons.add(button);
		}
	}
}
