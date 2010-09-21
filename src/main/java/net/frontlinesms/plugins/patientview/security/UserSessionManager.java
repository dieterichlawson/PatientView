package net.frontlinesms.plugins.patientview.security;

import java.util.Date;

import net.frontlinesms.plugins.patientview.data.domain.people.User;
import net.frontlinesms.plugins.patientview.data.domain.people.User.Role;
import net.frontlinesms.plugins.patientview.data.repository.hibernate.HibernateUserDao;

import org.springframework.context.ApplicationContext;

public class UserSessionManager {

	private User currentUser;

	private long currentSessionStartTime;

	private boolean isLoggedIn;

	private HibernateUserDao userDao;

	private static UserSessionManager sessionManager;

	public static enum AuthenticationResult {
		NOSUCHUSER(), WRONGPASSWORD(), SUCCESS();
	}

	private UserSessionManager() {
		isLoggedIn = false;
	}

	public static UserSessionManager getUserSessionManager() {
		if (sessionManager == null) {
			sessionManager = new UserSessionManager();
		}
		return sessionManager;
	}

	public User getCurrentUser() {
		return currentUser;
	}

	public Role getCurrentUserRole() {
		return currentUser.getRole();
	}

	public AuthenticationResult login(String username, String password) {
		User user = userDao.getUserByUsername(username);
		if (user != null && user.verifyPassword(password)) {
			currentUser = user;
			isLoggedIn = true;
			currentSessionStartTime = new Date().getTime();
			return AuthenticationResult.SUCCESS;
		} else {
			return AuthenticationResult.WRONGPASSWORD;
		}

	}

	public void init(ApplicationContext appcon) {
		userDao = (HibernateUserDao) appcon.getBean("UserDao");
	}

	public void logout() {
		currentUser = null;
		isLoggedIn = false;
	}

	public Date getCurrentSessionStartTime() {
		return new Date(currentSessionStartTime);
	}

	public boolean isLoggedIn() {
		return isLoggedIn;
	}

}
