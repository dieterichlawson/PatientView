package net.frontlinesms.plugins.patientview.ui;

import static net.frontlinesms.ui.i18n.InternationalisationUtils.getI18NString;
import java.security.GeneralSecurityException;
import java.util.Map;
import org.springframework.context.ApplicationContext;
import net.frontlinesms.plugins.patientview.data.domain.people.PasswordUtils;
import net.frontlinesms.plugins.patientview.data.domain.people.User;
import net.frontlinesms.plugins.patientview.data.repository.UserDao;
import net.frontlinesms.plugins.patientview.ui.helpers.thinletformfields.TextBox;
import net.frontlinesms.plugins.patientview.ui.helpers.thinletformfields.personalformfields.PasswordTextField;
import net.frontlinesms.plugins.patientview.ui.helpers.thinletformfields.personalformfields.UsernameField;
import net.frontlinesms.plugins.patientview.userlogin.UserSessionManager;
import net.frontlinesms.plugins.patientview.userlogin.UserSessionManager.AuthenticationResult;
import net.frontlinesms.ui.ThinletUiEventHandler;
import net.frontlinesms.ui.UiGeneratorController;

/**
 * The login screen. This screen has several modes for verifying and collecting
 * user login information. It validates passwords, usernames, ensures users have
 * security questions for password recovery, and forces users with a temporary
 * password to choose a new one.
 */
public class LoginScreen implements ThinletUiEventHandler {

	// UI Resource file paths
	private static final String XML_LOGIN_SCREEN = "/ui/plugins/patientview/login_screen.xml";
	private static final String INCORRECT_LOGIN_MESSAGE = "login.incorrect.login.message";

	public static final int REQUIRED_QUESTIONS = 3;

	/** The box for username input. */
	private final UsernameField usernameBox;

	/**
	 * The password boxes. Usually only the first is used, but the second is
	 * avaliable for when passwords need to be double checked.
	 */
	private PasswordTextField passwordBox, passwordBox2;

	private TextBox[] questions;
	private TextBox[] answers;

	/** The medic logo. */
	private final Object logo;

	/** A multipurpose label. */
	private Object infoLabel;

	/** The top level thinlet component for this object. */
	private final Object mainPanel;

	/** Used for saving new passwords and security questions. */
	private UserDao userDao;

	/** The {@link UiGeneratorController} that shows the tab. */
	private final UiGeneratorController ui;

	private final PatientViewThinletTabController tabController;

	public LoginScreen(UiGeneratorController uiController,
			PatientViewThinletTabController tabController,
			ApplicationContext appCon) {
		this.tabController = tabController;
		this.ui = uiController;
		userDao = (UserDao) appCon.getBean("UserDao");
		mainPanel = uiController.loadComponentFromFile(XML_LOGIN_SCREEN, this);
		logo = ui.find(mainPanel, "logo");
		usernameBox = new UsernameField(ui, appCon, false, "");
		reset();
	}

	/**
	 * @return the Thinlet panel that the login screen modifies.
	 */
	public Object getMainPanel() {
		return mainPanel;
	}

	/** Transforms the login screen into a dialog for inputting a new password. */
	protected void newPasswordMode() {
		ui.removeAll(mainPanel);
		// create message instructing the user to choose a new password
		Object noticeLabel = ui
				.createLabel(getI18NString("login.new.password.notice"));
		ui.setColspan(noticeLabel, 2);
		ui.add(mainPanel, noticeLabel);
		// creat new password box and duplicate password box
		passwordBox = new PasswordTextField(ui,
				getI18NString("login.new.password"));
		ui.add(mainPanel, ui.createLabel(passwordBox.getLabel()));
		ui.add(mainPanel, passwordBox.getTextField());
		passwordBox2 = new PasswordTextField(ui,
				getI18NString("login.new.password.repeat"));
		ui.setPerform(passwordBox2.getTextField(), "saveNewPassword", null,
				this);
		ui.add(mainPanel, ui.createLabel(passwordBox2.getLabel()));
		ui.add(mainPanel, passwordBox2.getTextField());
		Object button = ui.createButton("Create");
		ui.setAction(button, "saveNewPassword", null, this);
		ui.add(mainPanel, button);
		ui.setFocus(passwordBox.getTextField());
	}

	/**
	 * Transforms the login screen into a dialog for inputing security
	 * questions.
	 */
	protected void newQuestionsMode() {
		ui.removeAll(mainPanel);
		// TODO: Make this mode a constant width (ask Dieterich)
		// ui.setInteger(mainPanel, "width", 450);
		// create message instructing the user to choose a new password
		infoLabel = ui.createLabel(getI18NString("login.new.questions.notice"));
		ui.setColspan(infoLabel, 2);
		ui.add(mainPanel, infoLabel);
		questions = new TextBox[REQUIRED_QUESTIONS];
		answers = new TextBox[REQUIRED_QUESTIONS];
		for (int i = 0; i < REQUIRED_QUESTIONS; i++) {
			questions[i] = new TextBox(ui, getI18NString("login.new.question")
					+ " " + i);
			ui.add(mainPanel, ui.createLabel(questions[i].getLabel()));
			ui.add(mainPanel, questions[i].getTextField());
			answers[i] = new TextBox(ui,
					getI18NString("login.new.question.answer") + " " + i);
			ui.add(mainPanel, ui.createLabel(answers[i].getLabel()));
			ui.add(mainPanel, answers[i].getTextField());
		}
		Object skipButton = ui.createButton(getI18NString("login.login"));
		ui.setAction(skipButton, "init", null, tabController);
		ui.add(mainPanel, skipButton);
		Object saveButton = ui
				.createButton(getI18NString("login.new.question.save"));
		ui.setAction(saveButton, "saveNewSecurityQuestions", null, this);
		ui.add(mainPanel, saveButton);
		ui.setFocus(questions[0].getTextField());
	}

	/** 
	 * 
	 */
	public void forgotPassword() {
		String username = usernameBox.getResponse();
		if (username == null || username.equals("")) {
			recoverPasswordMode(null);
		} else {
			User u = userDao.getUsersByUsername(username).get(0);
			recoverPasswordMode(u);
		}
	}

	/**
	 * The mode that lets a user answer their security questions to generate a
	 * new password.
	 */
	protected void recoverPasswordMode(User user) {
		ui.removeAll(mainPanel);
		Object backButton = ui.createButton(getI18NString("login.reset.back"));
		ui.setAction(backButton, "reset", null, this);
		if (user == null
				|| user.getSecurityQuestions().size() < REQUIRED_QUESTIONS) {
			Object noticeLabel = ui
					.createLabel(getI18NString("login.reset.warning"));
			ui.add(mainPanel, noticeLabel);
			ui.add(mainPanel, backButton);
		} else {
			infoLabel = ui.createLabel(getI18NString("login.reset.notice"));
			ui.add(mainPanel, infoLabel);
			String[] questions = new String[user.getSecurityQuestions().size()];
			user.getSecurityQuestions().keySet().toArray(questions);
			answers = new TextBox[REQUIRED_QUESTIONS];
			for (int i = 0; i < REQUIRED_QUESTIONS; i++) {
				answers[i] = new TextBox(ui, questions[i]);
				Object label = ui.createLabel(answers[i].getLabel());
				ui.setColspan(label, 2);
				ui.add(mainPanel, label);
				ui.setColspan(answers[i].getTextField(), 2);
				ui.add(mainPanel, answers[i].getTextField());
			}
			ui.add(mainPanel, backButton);
			Object resetButton = ui.createButton(getI18NString("login.reset"));
			ui.setAction(resetButton, "attemptRecoverPassword", null, this);
			ui.add(mainPanel, resetButton);
		}
	}

	/**
	 * The callback function for when a user attempts to recover their security
	 * questions by answering their security questions.
	 * 
	 * @throws GeneralSecurityException
	 */
	public void attemptRecoverPassword() throws GeneralSecurityException {
		// TODO: error handling
		String username = ui.getText(usernameBox.getTextField());
		User user = userDao.getUsersByUsername(username).get(0);
		Map<String, byte[]> securityQuestions = user.getSecurityQuestions();
		boolean valid = true;
		for (TextBox answerBox : answers) {
			String quest = answerBox.getLabel();
			String ans = answerBox.getResponse();
			byte[] hash = securityQuestions.get(quest);
			boolean correctAns = PasswordUtils
					.verify(ans, hash, user.getSalt());
			if (!correctAns) {
				valid = false;
			}
		}
		if (valid) {
			String tempPassword = user.assignTempPassword();
			ui.removeAll(mainPanel);
			Object backButton = ui
					.createButton(getI18NString("login.reset.back"));
			ui.setAction(backButton, "reset", null, this);
			infoLabel = ui
					.createLabel(getI18NString("login.reset.temp.password"));
			ui.setColspan(infoLabel, 2);
			ui.add(mainPanel, infoLabel);
			ui.add(mainPanel, ui.createLabel(tempPassword));
			ui.add(mainPanel, backButton);
			userDao.updateUser(user);
		} else {
			ui.setText(infoLabel, getI18NString("login.reset.wrong.answer"));
		}
	}

	/**
	 * The callback method for when a user preses a login button in this screen.
	 */
	public void attemptLogin() {
		String username = usernameBox.getResponse();
		String password = passwordBox.getResponse();
		if (username == null || username.equals("") || password == null
				|| password.equals("")) {
			ui.setText(infoLabel, getI18NString(INCORRECT_LOGIN_MESSAGE));
			return;
		}
		UserSessionManager manager = UserSessionManager.getUserSessionManager();
		AuthenticationResult result = manager.login(username, password);
		if (result == AuthenticationResult.NOSUCHUSER
				|| result == AuthenticationResult.WRONGPASSWORD) {
			ui.setText(infoLabel, getI18NString(INCORRECT_LOGIN_MESSAGE));
			return;
		}
		if (result == AuthenticationResult.SUCCESS) {
			if (manager.getCurrentUser().needsNewPassword()) {
				newPasswordMode();
			} else if (manager.getCurrentUser().getSecurityQuestions().size() < 3) {
				newQuestionsMode();
			} else {
				tabController.init();
			}
		}
	}

	/** Resets the login screen back to its initial state. */
	public void reset() {
		// clear out all the old junk
		UserSessionManager manager = UserSessionManager.getUserSessionManager();
		manager.logout();
		ui.removeAll(mainPanel);
		ui.setColumns(mainPanel, 2);
		// add the logo
		ui.add(mainPanel, logo);
		infoLabel = ui.createLabel(getI18NString("login.screen.text"));
		// add the text above the login boxes
		ui.setColspan(infoLabel, 2);
		ui.add(mainPanel, infoLabel);
		// add the username label / box, the hackyness gives cleaner columns
		ui.add(mainPanel, ui.createLabel(usernameBox.getLabel()));
		ui.add(mainPanel, usernameBox.getTextField());
		// add the password box, same hackyness as above
		passwordBox = new PasswordTextField(ui, getI18NString("login.password"));
		ui.setPerform(passwordBox.getTextField(), "attemptLogin", null, this);
		ui.add(mainPanel, ui.createLabel(passwordBox.getLabel()));
		ui.add(mainPanel, passwordBox.getTextField());
		// add the buttons
		Object panel = ui.createPanel(null);
		ui.setInteger(panel, "columns", 2);
		ui.setInteger(panel, "gap", 9);
		Object forgotButton = ui.createButton(getI18NString("login.forgot"));
		ui.setAction(forgotButton, "forgotPassword", null, this);
		ui.add(panel, forgotButton);
		Object loginButton = ui.createButton(getI18NString("login.login"));
		ui.setAction(loginButton, "attemptLogin", null, this);
		ui.add(panel, loginButton);
		ui.add(mainPanel, ui.createPanel(null)); // this takes the space in the
		// left column
		ui.add(mainPanel, panel); // the buttons panel goes in the right column
		// focus on username box for easy login
		ui.setFocus(usernameBox.getTextField());
	}

	/**
	 * The callback method for users creating a new password.
	 * 
	 * @throws GeneralSecurityException
	 *             if the crypto libray cannot be loaded
	 */
	public void saveNewPassword() throws GeneralSecurityException {
		// TODO: error handling
		UserSessionManager manager = UserSessionManager.getUserSessionManager();
		String pass = passwordBox.getResponse();
		if (pass.equals(passwordBox2.getResponse())) {
			User u = manager.getCurrentUser();
			u.setPassword(pass);
			userDao.updateUser(u);
			reset();
			ui.setText(infoLabel, getI18NString("login.new.password.use"));
		} else {
			ui.createDialog(getI18NString("login.new.password.warning"));
		}
		passwordBox2 = null;
	}

	/**
	 * The callback method for users creating new security questions.
	 * 
	 * @throws GeneralSecurityException
	 *             if the crypto libray cannot be loaded
	 */
	public void saveNewSecurityQuestions() throws GeneralSecurityException {
		// TODO: error handling
		User user = UserSessionManager.getUserSessionManager().getCurrentUser();
		if (questions == null || answers == null) {
			ui.createDialog(getI18NString("login.new.password.warning"));
			return;
		}
		// add questions to user
		int count = 0;
		for (int i = 0; i < questions.length; i++) {
			String q = questions[i].getResponse();
			String a = answers[i].getResponse();
			if (q != null && !q.equals("") && a != null && !a.equals("")) {
				// if the question is not already there, add it
				if (!user.getSecurityQuestions().keySet().contains(q)) {
					user.addSecurityQuestion(q, a);
				} else { // otherwise, overwrite the former question
					user.getSecurityQuestions().remove(q);
					user.addSecurityQuestion(q, a);
				}
				count++;
			}
		}
		// save user
		if (count > 0) {
			userDao.updateUser(user);
			ui.setText(infoLabel, count + " "
					+ getI18NString("login.new.question.saved"));
			System.out.println(user.getSecurityQuestions().size());
		}
	}

}
