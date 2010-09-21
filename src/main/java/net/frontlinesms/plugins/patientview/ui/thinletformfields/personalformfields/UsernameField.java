package net.frontlinesms.plugins.patientview.ui.thinletformfields.personalformfields;

import static net.frontlinesms.ui.i18n.InternationalisationUtils.getI18NString;
import net.frontlinesms.plugins.patientview.data.domain.people.Person;
import net.frontlinesms.plugins.patientview.data.domain.people.User;
import net.frontlinesms.plugins.patientview.data.repository.hibernate.HibernateUserDao;
import net.frontlinesms.plugins.patientview.ui.thinletformfields.FormFieldDelegate;
import net.frontlinesms.plugins.patientview.ui.thinletformfields.TextField;
import net.frontlinesms.ui.ExtendedThinlet;

import org.springframework.context.ApplicationContext;

/** A field for inputting login names. */
public class UsernameField extends TextField implements PersonalFormField {

	private Object picture;
	private HibernateUserDao userDao;

	public UsernameField(ExtendedThinlet thinlet, ApplicationContext appCon,boolean useIndicator, String initialUsername,FormFieldDelegate delegate) {
		super(thinlet,getI18NString("login.username") + ":", delegate);
		userDao = (HibernateUserDao) appCon.getBean("UserDao");
		if (useIndicator) {
			picture = thinlet.createButton("");
			thinlet.setEnabled(picture, false);
			thinlet.add(mainPanel, picture);
			// called to initially update the indicator icon
			textBoxKeyPressed("");
		}
		hasChanged = false;
		thinlet.setColumns(mainPanel, 3);
		if (initialUsername != "" && initialUsername != null) {
			thinlet.setText(textBox, initialUsername);
		}
	}

	public void textBoxKeyPressed(String r) {
		hasChanged = true;
		super.responseChanged();
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
			if (userDao.findUsersByUsername(r).size() == 0) {
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
