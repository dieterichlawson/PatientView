package net.frontlinesms.plugins.patientview.ui.administration;

import static net.frontlinesms.ui.i18n.InternationalisationUtils.getI18NString;

import java.security.GeneralSecurityException;
import java.util.List;

import net.frontlinesms.plugins.patientview.data.domain.people.Person;
import net.frontlinesms.plugins.patientview.data.domain.people.User;
import net.frontlinesms.plugins.patientview.data.repository.UserDao;
import net.frontlinesms.plugins.patientview.ui.personpanel.PersonPanel;
import net.frontlinesms.plugins.patientview.ui.personpanel.UserPanel;
import net.frontlinesms.ui.UiGeneratorController;

import org.springframework.context.ApplicationContext;

/**
 * The controller for the user administration panel. The associated Thinlet
 * files are:
 *<p>
 *<code>search_action_panel.xml</code><br/>
 *<code>password_reset_notice.xml</code>
 */
public class UserAdministrationPanelController extends PersonAdministrationPanelController<User> {

	/**
	 * The path to the .xml widget that appears when a user's password is reset.
	 */
	private String XML_PASSWORD_RESET_NOTICE = "/ui/plugins/patientview/administration/security/passwordResetDialog.xml";

	private static final String ADD_ICON = "/icons/doctor_add_female.png";
	private static final String EDIT_ICON = "/icons/doctor_edit_female.png";
	private static final String DELETE_ICON = "/icons/doctor_delete_female.png";
	/**
	 * The notice that appears when a uses's password is reset. This is
	 * persistant so that multiple copies do not show up if the button is
	 * clicked multiple times in a row.
	 */
	private Object resetNotice;

	/** The userDao for saving users edited in this component */
	private UserDao userDao;
	
	public UserAdministrationPanelController( UiGeneratorController uiController, ApplicationContext appCon) {
		super(uiController, appCon);
		this.userDao = (UserDao) appCon.getBean("UserDao");
		search("");
	}

	@Override
	protected List<User> getPeopleForString(String s) {
		return userDao.getUsersByName(s,30);
	}

	@Override
	protected String getPersonType() {
		return getI18NString("medic.common.user");
	}

	@Override
	protected void putHeader() {
		String[] headerText = new String[] {
				getI18NString("medic.common.labels.name"),
				getI18NString("medic.common.labels.username"),
				getI18NString("medic.common.labels.role") };
		String[] headerMethods = new String[] { "getName", "getUsername",
				"getRoleName" };
		advancedTableController
				.putHeader(User.class, headerText, headerMethods);
	}

	public String getListItemTitle() {
		return "Manage Users";
	}

	/**
	 * The callback method for the resetPasswordButton.
	 * 
	 * @throws GeneralSecurityException
	 *             if the crypto library cannot be loaded
	 */
	public void resetPassword() throws GeneralSecurityException {
		// TODO: error handling
		User user = (User) advancedTableController.getCurrentlySelectedObject();
		String newPass = user.assignTempPassword();
		userDao.updateUser(user);
		if (resetNotice == null) {
			resetNotice = uiController
					.loadComponentFromFile(XML_PASSWORD_RESET_NOTICE);
		}
		Object passwordLabel = uiController.find(resetNotice, "passwordlabel");
		uiController.setText(passwordLabel, newPass);
		Object fieldsPanel = uiController.find(getPanel(), FIELDS_PANEL);
		uiController.add(fieldsPanel, resetNotice);
	}

	@Override
	protected PersonPanel<User> getPersonPanelForPerson(Person person) {
		return new UserPanel(uiController, appCon, (User) person);
	}

	@Override
	public void editButtonClicked() {
		currentPersonPanel.switchToEditingPanel();
		// add the reset password button!
		Object resetPasswordButton = uiController
				.createButton(getI18NString("password.reset"));
		uiController.setAction(resetPasswordButton, "resetPassword()", null,
				this);
		uiController.setHAlign(resetPasswordButton, "right");
		Object buttonsPanel = uiController.find(currentPersonPanel
				.getMainPanel(), PersonPanel.BUTTON_PANEL);
		uiController.add(buttonsPanel, resetPasswordButton);
	}

	public String getIconPath() {
		return "/icons/doctors.png";
	}

	@Override
	protected String[] getIcons() {
		String[] icons = new String[3];
		icons[ADD_INDEX] = ADD_ICON;
		icons[EDIT_INDEX] = EDIT_ICON;
		icons[REMOVE_INDEX] = DELETE_ICON;
		return icons;
	}

}
