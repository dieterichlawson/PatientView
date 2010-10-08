package net.frontlinesms.plugins.patientview.ui.personpanel;

import static net.frontlinesms.ui.i18n.InternationalisationUtils.getI18NString;
import net.frontlinesms.plugins.patientview.data.domain.people.User;
import net.frontlinesms.plugins.patientview.ui.thinletformfields.fieldgroups.PersonFieldGroup;
import net.frontlinesms.plugins.patientview.ui.thinletformfields.fieldgroups.UserFieldGroup;
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
	
	public UserPanel(UiGeneratorController uiController, ApplicationContext appCon, User p) {
		super(uiController, appCon, p);
	}

	public UserPanel(UiGeneratorController uiController,ApplicationContext appCon) {
		super(uiController, appCon);
	}

	/**
	 * @see net.frontlinesms.plugins.patientview.ui.personpanel.PersonPanel#addAdditionalFields()
	 */
	@Override
	protected void addAdditionalFields() {
		addLabelToLabelPanel(getI18NString(USERNAME_LABEL) + ": " + getPerson().getUsername());
		addLabelToLabelPanel(getI18NString(ROLE_LABEL) + ": "	+ getPerson().getRoleName());
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

	@Override
	protected User createPerson() {
		return new User();
	}

	@Override
	protected PersonFieldGroup<User> getEditableFields() {
		return new UserFieldGroup(uiController, appCon, null, getPerson());
	}

}
