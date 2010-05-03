package net.frontlinesms.plugins.patientview.ui.administration;

import static net.frontlinesms.ui.i18n.InternationalisationUtils.*;

/** This is the ui controller for the security panel.  currently it im
 * 
 * @author javins
 *
 */
public class SecurityPanelController implements AdministrationTabPanel {
	
	// UI Components
	private Object mainPanel;
	private Object passwordLengthBox;
	private Object lowerCaseCheckBox;
	private Object upperCaseCheckBox;
	private Object numbersCheckBox;
	private Object symbolsCheckBox;
	
	// I18N Strings
	private String LOCKOUT_DURATION = "admin.security.lockout.duration";
	

	public String getListItemTitle() {
		return getI18NString("admin.actionlist.manage.security");
	}


	public Object getPanel() {
		return mainPanel;
	}

}
