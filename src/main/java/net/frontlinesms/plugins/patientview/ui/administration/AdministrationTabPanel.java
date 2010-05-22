package net.frontlinesms.plugins.patientview.ui.administration;

public interface AdministrationTabPanel {

	/**
	 * @return The desired title for the panel's list item in the action list
	 */
	public String getListItemTitle();
	
	/**
	 * Should return the main Thinlet panel of the administration tab item
	 * @return the thinlet panel
	 */
	public Object getPanel();
	
	public String getIconPath();
}
