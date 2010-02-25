package net.frontlinesms.plugins.patientview.ui.personpanel;

import net.frontlinesms.plugins.patientview.data.domain.people.User;
import net.frontlinesms.plugins.patientview.data.repository.UserDao;
import net.frontlinesms.plugins.patientview.ui.helpers.thinletformfields.personalformfields.RoleComboBox;
import net.frontlinesms.plugins.patientview.ui.helpers.thinletformfields.personalformfields.UsernameField;
import net.frontlinesms.ui.UiGeneratorController;
import net.frontlinesms.ui.i18n.InternationalisationUtils;

import org.springframework.context.ApplicationContext;

public class UserPanel extends PersonPanel<User> {

	private static final String EDIT_USER_DATA_BUTTON = "personpanel.labels.edit.user";
	private static final String USER_AAG = "personpanel.labels.user.at.a.glance";
	private static final String ADD_USER = "personpanel.labels.add.a.user";
	private static final String ROLE_LABEL = "medic.common.labels.role";
	private static final String USERNAME_LABEL = "medic.common.labels.username";
	
	private UserDao userDao;
	
	public UserPanel(UiGeneratorController uiController, ApplicationContext appCon, User p) {
		super(uiController, appCon,p);
		userDao = (UserDao) appCon.getBean("UserDao");
	}
	
	public UserPanel(UiGeneratorController uiController, ApplicationContext appCon) {
		super(uiController,appCon);
		userDao = (UserDao) appCon.getBean("UserDao");
	}

	/**
	 * @see net.frontlinesms.plugins.patientview.ui.personpanel.PersonPanel#addAdditionalEditableFields()
	 */
	@Override
	protected void addAdditionalEditableFields() {
		UsernameField usernameField = new UsernameField(uiController,appCon,true,isNewPersonPanel?"":person.getUsername());
		uiController.add(getLabelPanel(),usernameField.getThinletPanel());
		RoleComboBox roleCombo = new RoleComboBox(uiController,isNewPersonPanel?null:person.getRole());
		uiController.add(getLabelPanel(),roleCombo.getThinletPanel());
	}

	/**
	 * @see net.frontlinesms.plugins.patientview.ui.personpanel.PersonPanel#addAdditionalFields()
	 */
	@Override
	protected void addAdditionalFields() {
		addLabelToLabelPanel(InternationalisationUtils.getI18NString(USERNAME_LABEL) + ": "+ person.getUsername());
		addLabelToLabelPanel(InternationalisationUtils.getI18NString(ROLE_LABEL) + ": " + person.getRoleName());
	}

	/**
	 * @see net.frontlinesms.plugins.patientview.ui.personpanel.PersonPanel#createPerson()
	 */
	@Override
	protected void createPerson() {
		person = new User();
	}

	@Override
	protected String getDefaultTitle() {
		return InternationalisationUtils.getI18NString(USER_AAG);
	}

	@Override
	protected String getEditingTitle() {
		return InternationalisationUtils.getI18NString(EDIT_USER_DATA_BUTTON);
	}

	@Override
	protected void savePerson() {
		userDao.saveUser(person);
	}

	@Override
	protected void updatePerson() {
		userDao.updateUser(person);
	}

	@Override
	protected String getAddingTitle() {
		return InternationalisationUtils.getI18NString(ADD_USER);
	}

}
