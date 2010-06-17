package net.frontlinesms.plugins.patientview.ui.personpanel;

import static net.frontlinesms.ui.i18n.InternationalisationUtils.getI18NString;
import net.frontlinesms.plugins.patientview.data.domain.people.User;
import net.frontlinesms.plugins.patientview.data.repository.UserDao;
import net.frontlinesms.plugins.patientview.ui.helpers.thinletformfields.personalformfields.RoleComboBox;
import net.frontlinesms.plugins.patientview.ui.helpers.thinletformfields.personalformfields.UsernameField;
import net.frontlinesms.plugins.patientview.userlogin.UserSessionManager;
import net.frontlinesms.ui.UiGeneratorController;

import org.springframework.context.ApplicationContext;

public class UserPanel extends PersonPanel<User> {

	private static final String EDIT_USER_DATA_BUTTON = "personpanel.labels.edit.user";
	private static final String USER_AAG = "personpanel.labels.user.at.a.glance";
	private static final String ADD_USER = "personpanel.labels.add.a.user";
	private static final String ROLE_LABEL = "medic.common.labels.role";
	private static final String USERNAME_LABEL = "medic.common.labels.username";
	private static final String DEMO_USERNAME = "editdetailview.demo.username";
	private static final String DEMO_ROLE = "roles.readwrite";

	private UserDao userDao;

	public UserPanel(UiGeneratorController uiController, ApplicationContext appCon, User p) {
		super(uiController, appCon, p);
		userDao = (UserDao) appCon.getBean("UserDao");
	}

	public UserPanel(UiGeneratorController uiController,ApplicationContext appCon) {
		super(uiController, appCon);
		userDao = (UserDao) appCon.getBean("UserDao");
	}

	/**
	 * @see net.frontlinesms.plugins.patientview.ui.personpanel.PersonPanel#addAdditionalEditableFields()
	 */
	@Override
	protected void addAdditionalEditableFields() {
		if(UserSessionManager.getUserSessionManager().getCurrentUser().equals(this.getPerson()) || isNewPersonPanel){
			UsernameField usernameField = new UsernameField(uiController, appCon, true, isNewPersonPanel ? "" : getPerson().getUsername(),null);
			uiController.add(getLabelPanel(), usernameField.getThinletPanel());
		}
		RoleComboBox roleCombo = new RoleComboBox(uiController, isNewPersonPanel ? null : getPerson().getRole(),null);
		uiController.add(getLabelPanel(), roleCombo.getThinletPanel());
	}

	/**
	 * @see net.frontlinesms.plugins.patientview.ui.personpanel.PersonPanel#addAdditionalFields()
	 */
	@Override
	protected void addAdditionalFields() {
		addLabelToLabelPanel(getI18NString(USERNAME_LABEL) + ": " + getPerson().getUsername());
		addLabelToLabelPanel(getI18NString(ROLE_LABEL) + ": "	+ getPerson().getRoleName());
	}

	/**
	 * @see net.frontlinesms.plugins.patientview.ui.personpanel.PersonPanel#createPerson()
	 */
	@Override
	protected User createPerson() {
		return new User();
	}

	@Override
	protected String getDefaultTitle() {
		return getI18NString(USER_AAG);
	}

	@Override
	protected String getEditingTitle() {
		return getI18NString(EDIT_USER_DATA_BUTTON);
	}

	@Override
	protected void savePerson() {
		userDao.saveUser(getPerson());
	}

	@Override
	protected void updatePerson() {
		userDao.updateUser(getPerson());
	}

	@Override
	protected String getAddingTitle() {
		return getI18NString(ADD_USER);
	}

	@Override
	protected void addAdditionalDemoFields() {
		addLabelToLabelPanel(getI18NString(USERNAME_LABEL) + ": "
				+ getI18NString(DEMO_USERNAME));
		addLabelToLabelPanel(getI18NString(ROLE_LABEL) + ": "
				+ getI18NString(DEMO_ROLE));

	}

}
