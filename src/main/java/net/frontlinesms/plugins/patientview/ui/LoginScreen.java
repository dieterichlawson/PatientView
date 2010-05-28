package net.frontlinesms.plugins.patientview.ui;

import static net.frontlinesms.ui.i18n.InternationalisationUtils.getI18NString;

import java.awt.Color;
import java.security.GeneralSecurityException;
import java.util.List;

import org.springframework.context.ApplicationContext;

import thinlet.Thinlet;
import net.frontlinesms.plugins.patientview.data.domain.people.SecurityQuestion;
import net.frontlinesms.plugins.patientview.data.domain.people.User;
import net.frontlinesms.plugins.patientview.data.repository.SecurityQuestionDao;
import net.frontlinesms.plugins.patientview.data.repository.UserDao;
import net.frontlinesms.plugins.patientview.userlogin.UserSessionManager;
import net.frontlinesms.plugins.patientview.userlogin.UserSessionManager.AuthenticationResult;
import net.frontlinesms.ui.ThinletUiEventHandler;
import net.frontlinesms.ui.UiGeneratorController;

/**
 * The login screen. This screen has several modes for verifying and collecting
 * user login information. It validates passwords, usernames, ensures users have
 * security questions for password recovery, and forces users with a temporary
 * password to choose a new one.
 * <p>
 * A brief note about the method names. All the attempt...() methods are
 * callbacks used by buttons in the ui. These methods are labeled as such
 * because the inputs are not guaurenteed, and frequently they won't be able to
 * do what they are supposed to because of bad user input. The only exceptions
 * to this are the reset() and resetSoft() methods, which are guarenteed to
 * succeed when they are called.
 * <p>
 * Meanwhile the protected changeMode...() methods are internally used and are
 * guarenteed to be called from a proper state.
 * <p>
 * Checkout /ui/plugins/patientview/login/ for related .xml widgets.
 */
public class LoginScreen implements ThinletUiEventHandler {

	// UI Resource file paths
	private static final String PATH = "/ui/plugins/patientview/login/";
	private static final String XML_LOGIN = PATH + "landing.xml";
	private static final String XML_QUESTIONS = PATH + "new_questions.xml";
	private static final String XML_NEW_PASSWORD = PATH + "new_password.xml";
	private static final String XML_RECOVER_PASSWORD = PATH
			+ "recover_password.xml";

	// UI elements
	private static final String MULTI_LABEL = "multiLabel";

	// I18N lookups
	private static final String INCORRECT_LOGIN_MESSAGE = "login.incorrect.login.message";
	private static final String QUESTION = "security.questions";
	private static final String QUESTIONS_ON_FILE = QUESTION + ".on.file";
	private static final String QUESTION_ON_FILE = QUESTION + ".on.file.1";
	private static final String QUESTION_DEFAULT = QUESTION + ".default";

	// TODO: These needs to be stored elsewhere
	public static final int REQUIRED_QUESTIONS = 3;
	public static final int REQUIRED_ANSWERS = 2;
	public static final int LOGIN_ATTEMPTS = 3;
	public static final int QUESTION_ATTEMPTS = 2;
	public static final int MIN_PASSWORD_LENGTH = 6;

	private Object[] questions;
	private Object[] answers;

	/** The top level thinlet component for this object. */
	private final Object mainPanel;

	/** Used for saving new passwords and security questions. */
	private UserDao userDao;
	private SecurityQuestionDao questionDao;

	private User user;

	/** The {@link UiGeneratorController} that shows the tab. */
	private final UiGeneratorController ui;

	private final PatientViewThinletTabController tabController;

	public LoginScreen(UiGeneratorController uiController,
			PatientViewThinletTabController tabController,
			ApplicationContext appCon) {
		this.tabController = tabController;
		this.ui = uiController;
		userDao = (UserDao) appCon.getBean("UserDao");
		questionDao = (SecurityQuestionDao) appCon.getBean("SecurityQuestionDao");
		mainPanel = ui.createPanel("");
		ui.setWeight(mainPanel, 1, 1);
		reset();
	}

	/**
	 * The callback method for the forgot password link on the landing screen.
	 */
	public void attemptForgotPassword() {
		Object usernameField = ui.find(mainPanel, "usernameField");
		String username = ui.getText(usernameField);
		
		if (username != null) {
			List<User> userList = userDao.getUsersByUsername(username);
			System.out.println(userList.size());
			if (!userList.isEmpty()) {
				user = userDao.getUsersByUsername(username).get(0);
				changeModeRecoverPassword();
			} else {
				displayIncorrectLoginMessage();
			}
		} else {
			displayIncorrectLoginMessage();
		}
	}

	/**
	 * The callback method for when a user preses a login button in this screen.
	 */
	public void attemptLogin() {
		Object usernameField = ui.find(mainPanel, "usernameField");
		Object passwordField = ui.find(mainPanel, "passwordField");
		String username = ui.getText(usernameField);
		String password = ui.getText(passwordField);
		// if (username == null || password == null) {
		// displayIncorrectLoginMessage();
		// return;
		// }
		UserSessionManager manager = UserSessionManager.getUserSessionManager();
		AuthenticationResult result = manager.login(username, password);
		if (result == AuthenticationResult.NOSUCHUSER
				|| result == AuthenticationResult.WRONGPASSWORD) {
			displayIncorrectLoginMessage();
			return;
		}
		if (result == AuthenticationResult.SUCCESS) {
			user = manager.getCurrentUser();
			if (user.needsNewPassword()) {
				changeModeNewPassword();
			} else if (numberOfSecurityQuestions(user) < REQUIRED_QUESTIONS) {
				changeModeNewQuestions();
			} else {
				changeModePatientView();
			}
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
		int correct = 0;
		for (int i = 0; i < questions.length; i++) {
			String a = ui.getText(answers[i]);
			// find the SecurityQuestion object that corrosponds to the string
			SecurityQuestion sq = ui.getAttachedObject(questions[i],
					SecurityQuestion.class);
			if (sq.verifyAnswer(a)) {
				correct++;
			}
		}
		System.out.println(correct);
		if (correct >= REQUIRED_ANSWERS) {
			changeModeNewPassword();
		} else {
			Object label = ui.find(mainPanel, MULTI_LABEL);
			String text = getI18NString("password.reset.wrong.answer");
//			text = text.replaceFirst("<X>", "" );
			ui.setText(label, text);
		}
	}

	/**
	 * The callback method for users creating a new password.
	 * 
	 * @throws GeneralSecurityException
	 *             if the crypto libray cannot be loaded
	 */
	public void attemptSavePassword() throws GeneralSecurityException {
		// TODO: error handling
		Object passwordBox = ui.find(mainPanel, "password1");
		String pass1 = ui.getText(passwordBox);
		passwordBox = ui.find(mainPanel, "password2");
		String pass2 = ui.getText(passwordBox);
		if (pass1.equals(pass2)) {
			user.setPassword(pass1);
			userDao.updateUser(user);
			resetSoft();
			Object label = ui.find(mainPanel, "multiLabel");
			ui.setText(label, getI18NString("password.new.use"));
		} else {
			Object label = ui.find(mainPanel, MULTI_LABEL);
			ui.setText(label, getI18NString("password.new.warning"));
			ui.setColor(label, Thinlet.FOREGROUND, Color.RED);
		}
	}

	/**
	 * The callback method for users creating new security questions.
	 * 
	 * @throws GeneralSecurityException
	 *             if the crypto libray cannot be loaded
	 */
	public void attemptSaveQuestions() throws GeneralSecurityException {
		// TODO: error handling
		if (questions == null || answers == null) {
			ui.createDialog(getI18NString("password.new.warning"));
			return;
		}
		for (int i = 0; i < questions.length; i++) {
			String q = ui.getText(questions[i]);
			String a = ui.getText(answers[i]);
			if (q != null && !q.equals("") && a != null && !a.equals("")) {
				// delete the question if it is alreay there
				saveQuestion(q, a, user);
			}
		}
		int count = numberOfSecurityQuestions(user);
		if (count < REQUIRED_QUESTIONS) {
			Object label = ui.find(mainPanel, MULTI_LABEL);
			String text;
			if (count == 1) {
				text = getI18NString(QUESTION_ON_FILE);
			} else {
				text = getI18NString(QUESTIONS_ON_FILE);
				text = text.replaceAll("<X>", "" + count);
			}
			int req = REQUIRED_QUESTIONS - count;
			text = text.replaceFirst("<Y>", "" + req);
			ui.setText(label, text);
		} else {
			answers = null;
			questions = null;
			changeModePatientView();
		}
	}

	/** Transforms the login screen into a dialog for inputting a new password. */
	protected void changeModeNewPassword() {
		Object passwordScreen = ui
				.loadComponentFromFile(XML_NEW_PASSWORD, this);
		Object notice = ui.find(passwordScreen, "noticeTextArea");
		String text = getI18NString("password.new.notice");
		text = text.replaceAll("<X>", "" + MIN_PASSWORD_LENGTH);
		ui.setText(notice, text);
		Object passwordBox = ui.find(passwordScreen, "passwordBox1");
		ui.removeAll(mainPanel);
		ui.add(mainPanel, passwordScreen);
		ui.setFocus(passwordBox);
	}

	/**
	 * Transforms the login screen into a dialog for inputing security
	 * questions.
	 */
	protected void changeModeNewQuestions() {
		// components that are worked with
		Object questionScreen = ui.loadComponentFromFile(XML_QUESTIONS, this);
		Object questionPanel = ui.find(questionScreen, "questionCreationPanel");
		Object multiLabel = ui.find(questionPanel, MULTI_LABEL);
		// set the text describing how many questions are needed
		String text;
		int num = numberOfSecurityQuestions(user);
		if (num == 1) {
			text = getI18NString(QUESTION_ON_FILE);
		} else {
			text = getI18NString(QUESTIONS_ON_FILE);
			text = text.replaceFirst("<X>", "" + num);
		}
		int required = REQUIRED_QUESTIONS - num;
		text = text.replaceFirst("<Y>", "" + required);
		ui.setText(multiLabel, text);

		// create the panel
		ui.removeAll(questionPanel);
		ui.add(questionPanel, multiLabel);
		questions = new Object[required];
		answers = new Object[required];
		for (int i = 0; i < required; i++) {
			questions[i] = createQuestionComboBox();
			ui.add(questionPanel, questions[i]);
			answers[i] = ui.createTextfield("", "");
			ui.setColspan(answers[i], 2);
			ui.add(questionPanel, answers[i]);
		}

		ui.removeAll(mainPanel);
		ui.add(mainPanel, questionScreen);
		ui.setFocus(questions[0]);
	}

	/**
	 * Instructs the tab controller to remove the login screen continue with the
	 * session.
	 */
	protected void changeModePatientView() {
		tabController.init();
	}

	/**
	 * The mode that lets a user answer their security questions to generate a
	 * new password.
	 */
	protected void changeModeRecoverPassword() {
		List<SecurityQuestion> userQuestions = questionDao
				.getSecurityQuestionsForUser(user);
		Object recoverPanel = ui.loadComponentFromFile(XML_RECOVER_PASSWORD,
				this);
		if (userQuestions.size() < REQUIRED_ANSWERS) {
			
		}

		Object questionsPanel = ui.find(recoverPanel, "questionsPanel");
		Object multiLabel = ui.find(questionsPanel, MULTI_LABEL);
		ui.removeAll(questionsPanel);
		ui.add(questionsPanel, multiLabel);
		questions = new Object[REQUIRED_QUESTIONS];
		answers = new Object[REQUIRED_QUESTIONS];
		for (int i = 0; i < questions.length; i++) {
			SecurityQuestion sq = userQuestions.get(i);
			questions[i] = ui.createLabel(sq.getQuestion());
			ui.setAttachedObject(questions[i], sq);
			ui.add(questionsPanel, questions[i]);
			answers[i] = ui.createTextfield("", "");
			ui.add(questionsPanel, answers[i]);
		}
		ui.removeAll(mainPanel);
		ui.add(mainPanel, recoverPanel);
		ui.setFocus(answers[0]);
	}

	protected Object createQuestionComboBox() {
		Object question = Thinlet.create(Thinlet.COMBOBOX);
		for (int j = 1; j <= 4; j++) {
			Object choice = ui.createComboboxChoice(getI18NString(QUESTION
					+ "." + j), null);
			ui.add(question, choice);
		}
		ui.setText(question, getI18NString(QUESTION_DEFAULT));
		ui.setColspan(question, 2);
		ui.setWeight(question, 1, 1);
		return question;
	}

	/**
	 * Displays an incorrect login message in the label above the username box.
	 */
	protected void displayIncorrectLoginMessage() {
		Object label = ui.find(mainPanel, MULTI_LABEL);
		ui.setColor(label, Thinlet.FOREGROUND, Color.RED);
		ui.setText(label, getI18NString(INCORRECT_LOGIN_MESSAGE));
	}

	/**
	 * @return the Thinlet panel that the login screen modifies.
	 */
	public Object getMainPanel() {
		return mainPanel;
	}

	/**
	 * @return the number of security questions for the user
	 */
	private int numberOfSecurityQuestions(User user) {
		return questionDao.getSecurityQuestionsForUser(user).size();
	}

	/** Resets the login screen back to its initial state. */
	public void reset() {
		UserSessionManager.getUserSessionManager().logout();
		Object landingScreen = ui.loadComponentFromFile(XML_LOGIN, this);
		Object usernameField = ui.find(landingScreen, "usernameField");
		ui.removeAll(mainPanel);
		ui.add(mainPanel, landingScreen);
		ui.setFocus(usernameField);
	}

	/** Resets the login screen, but preserves the username, if one was entered. */
	public void resetSoft() {
		reset();
		if (user != null) {
			Object usernameField = ui.find(mainPanel, "usernameField");
			ui.setText(usernameField, user.getUsername());
			ui.setFocus(ui.find(mainPanel, "passwordField"));
		}
	}

	/**
	 * This saves or overwrites a single security question.
	 * 
	 * @param question
	 *            the question string
	 * @param answer
	 *            the answer to the question
	 * @return true if the question already exsted for the current user
	 * @throws GeneralSecurityException
	 *             if the crypto library cannot be found
	 */
	private boolean saveQuestion(String question, String answer, User user)
			throws GeneralSecurityException {
		List<SecurityQuestion> userQuestions = questionDao
				.getSecurityQuestionsForUser(user);
		boolean found = false;
		for (SecurityQuestion sec : userQuestions) {
			if (sec.getQuestion().equals(question)) {
				questionDao.deleteSecurityQuestion(sec);
				found = true;
				break;
			}
		}
		SecurityQuestion sec = new SecurityQuestion(question, answer, user);
		questionDao.saveOrUpdateSecurityQuestion(sec);
		return found;
	}

}