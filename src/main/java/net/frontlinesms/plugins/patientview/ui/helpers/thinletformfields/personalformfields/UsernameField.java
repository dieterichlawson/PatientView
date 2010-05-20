package net.frontlinesms.plugins.patientview.ui.helpers.thinletformfields.personalformfields;

import net.frontlinesms.plugins.patientview.data.domain.people.Person;
import net.frontlinesms.plugins.patientview.data.domain.people.User;
import net.frontlinesms.plugins.patientview.data.repository.hibernate.HibernateUserDao;
import net.frontlinesms.plugins.patientview.ui.helpers.thinletformfields.TextBox;
import net.frontlinesms.ui.ExtendedThinlet;
import static net.frontlinesms.ui.i18n.InternationalisationUtils.getI18NString;

import org.springframework.context.ApplicationContext;

/** A field for inputting login names. */
public class UsernameField extends TextBox implements PersonalFormField {

	private Object picture;
	public static final String NAME = "usernameField";
	protected boolean hasChanged;
	private HibernateUserDao userDao;

	public UsernameField(ExtendedThinlet thinlet, ApplicationContext appCon,
			boolean useIndicator, String initialUsername) {
		this(thinlet, appCon, useIndicator, initialUsername,
				getI18NString("login.username") + ":" + NAME);

	}

	protected UsernameField(ExtendedThinlet thinlet, ApplicationContext appCon,
			boolean useIndicator, String initialUsername, String name) {
		super(thinlet, "Username:", name);
		userDao = (HibernateUserDao) appCon.getBean("UserDao");
		if (useIndicator) {
			picture = thinlet.createButton("");
			thinlet.setEnabled(picture, false);
			thinlet.add(mainPanel, picture);
			// called to initially update the indicator icon
			textBoxKeyPressed("");
		}
		hasChanged = false;
		thinlet.setInteger(mainPanel, "columns", 3);
		thinlet.setAttachedObject(mainPanel, this);
		if (initialUsername != "" && initialUsername != null) {
			thinlet.setText(super.textBox, initialUsername);
		}
	}

	public void textBoxKeyPressed(String r) {
		hasChanged = true;
		// if we're using an icon for validation, set it depending on the
		// username
		if (picture != null) {
			if (isValid()) {
				thinlet.setIcon(picture, "/icons/live.png");
			} else {
				thinlet.setIcon(picture, "/icons/delete.png");
			}
		}
	}

	/**
	 * Returns true if the user name is not already associated with a user.
	 * Returns false if a user already has this name. Usernames must also be
	 * between three and 17 characters.
	 */
	public boolean isValid() {
		String r = thinlet.getText(super.textBox);
		if (r != null && r.length() >= 5 && r.length() <= 25) {
			// make sure no other users already have this name
			if (userDao.getUsersByUsername(r).size() == 0) {
				return true;
			}
		}
		return false;
	}

	public boolean hasChanged() {
		return hasChanged;
	}

	public void setFieldForPerson(Person p) {
		User u;
		try {
			u = (User) p;
		} catch (Throwable t) {
			return;
		}
		u.setUsername(getRawResponse());
	}
}
