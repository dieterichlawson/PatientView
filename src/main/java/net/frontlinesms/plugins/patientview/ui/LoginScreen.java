package net.frontlinesms.plugins.patientview.ui;

import static net.frontlinesms.ui.i18n.InternationalisationUtils.getI18NString;

import java.security.GeneralSecurityException;
import java.util.List;

import net.frontlinesms.plugins.patientview.data.domain.people.PasswordUtils;
import net.frontlinesms.plugins.patientview.data.domain.people.SecurityQuestion;
import net.frontlinesms.plugins.patientview.data.domain.people.User;
import net.frontlinesms.plugins.patientview.data.repository.SecurityQuestionDao;
import net.frontlinesms.plugins.patientview.data.repository.UserDao;
import net.frontlinesms.plugins.patientview.userlogin.UserSessionManager;
import net.frontlinesms.plugins.patientview.userlogin.UserSessionManager.AuthenticationResult;
import net.frontlinesms.ui.ThinletUiEventHandler;
import net.frontlinesms.ui.UiGeneratorController;

import org.springframework.context.ApplicationContext;

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

	public LoginScreen(UiGeneratorController uiController, PatientViewThinletTabController tabController, ApplicationContext appCon) {
		this.tabController = tabController;
		this.ui = uiController;
		userDao = (UserDao) appCon.getBean("UserDao");
		questionDao = (SecurityQuestionDao) appCon
				.getBean("SecurityQuestionDao");
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

	/** Transforms the login screen into a dialog for inputting a new password. */
	protected void newPasswordMode() {
		ui.removeAll(mainPanel);
		// create message instructing the user to choose a new password
		Object noticeLabel = ui.createLabel(getI18NString("login.new.password.notice"));
		ui.setColspan(noticeLabel, 2);
		ui.add(mainPanel, noticeLabel);
		// creat new password box and duplicate password box
		passwordBox = new PasswordTextField(ui, getI18NString("login.new.password"));
		ui.add(mainPanel, ui.createLabel(passwordBox.getLabel()));
		ui.add(mainPanel, passwordBox.getTextField());
		passwordBox2 = new PasswordTextField(ui, getI18NString("login.new.password.repeat"));
		ui.setPerform(passwordBox2.getTextField(), "saveNewPassword", null, this);
		ui.add(mainPanel, ui.createLabel(passwordBox2.getLabel()));
		ui.add(mainPanel, passwordBox2.getTextField());
		Object button = ui.createButton("Create");
		ui.setAction(button, "saveNewPassword", null, this);
		ui.add(mainPanel, button);
		ui.setFocus(passwordBox.getTextField());
	}

	/**
	 * The callback method for when a user preses a login button in this screen.
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
			questions[i] = new TextBox(ui, getI18NString("login.new.question") + " " + i);
			ui.add(mainPanel, ui.createLabel(questions[i].getLabel()));
			ui.add(mainPanel, questions[i].getTextField());
			answers[i] = new TextBox(ui, getI18NString("login.new.question.answer") + " " + i);
			ui.add(mainPanel, ui.createLabel(answers[i].getLabel()));
			ui.add(mainPanel, answers[i].getTextField());
		}
		Object skipButton = ui.createButton(getI18NString("login.login"));
		ui.setAction(skipButton, "init", null, tabController);
		ui.add(mainPanel, skipButton);
		Object saveButton = ui.createButton(getI18NString("login.new.question.save"));
		ui.setAction(saveButton, "saveNewSecurityQuestions", null, this);
		ui.add(mainPanel, saveButton);
		ui.setFocus(questions[0].getTextField());
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
	protected void recoverPasswordMode(User user) {
		ui.removeAll(mainPanel);
		Object backButton = ui.createButton(getI18NString("login.reset.back"));
		ui.setAction(backButton, "reset", null, this);
		if (user == null || user.getSecurityQuestions().size() < REQUIRED_QUESTIONS) {
			Object noticeLabel = ui.createLabel(getI18NString("login.reset.warning"));
			ui.add(mainPanel, noticeLabel);
			ui.add(mainPanel, backButton);
		} else {
			infoLabel = ui.createLabel(getI18NString("login.reset.notice"));
			ui.add(mainPanel, infoLabel);
			String[] questions = new String[user.getSecurityQuestions().size()];
			for(int i = 0; i < questions.length; i++){
				questions[i] = user.getSecurityQuestions().get(i).getQuestion();
			}
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
	 * The callback method for users creating new security questions.
	 * 
	 * @throws GeneralSecurityException
	 *             if the crypto libray cannot be loaded
	 */
	public void attemptSaveQuestions() throws GeneralSecurityException {
		// TODO: error handling
		String username = ui.getText(usernameBox.getTextField());
		//TODO: What if username is entered incorrectly?
		//TODO: if the resultant array is of a size > 1 output a message
		//saying 'there is no user by that username in the system
		User user = userDao.getUsersByUsername(username).get(0);
		List<SecurityQuestion> securityQuestions = user.getSecurityQuestions();
		boolean valid = true;
		for (TextBox answerBox : answers) {
			String quest = answerBox.getLabel();
			String ans = answerBox.getResponse();
			for(SecurityQuestion sq: securityQuestions){
				if(sq.getQuestion().equalsIgnoreCase(quest)){
					valid = sq.verifyAnswer(ans);
				}
			}
		}
		if (valid) {
			String tempPassword = user.assignTempPassword();
			ui.removeAll(mainPanel);
			Object backButton = ui.createButton(getI18NString("login.reset.back"));
			ui.setAction(backButton, "reset", null, this);
			infoLabel = ui.createLabel(getI18NString("login.reset.temp.password"));
			ui.setColspan(infoLabel, 2);
			ui.add(mainPanel, infoLabel);
			ui.add(mainPanel, ui.createLabel(tempPassword));
			ui.add(mainPanel, backButton);
			userDao.updateUser(user);
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
	public void attemptLogin() {
		String username = usernameBox.getResponse();
		String password = passwordBox.getResponse();
		if (username == null || username.equals("") || password == null || password.equals("")) {
			ui.setText(infoLabel, getI18NString(INCORRECT_LOGIN_MESSAGE));
			return;
		}
		UserSessionManager manager = UserSessionManager.getUserSessionManager();
		AuthenticationResult result = manager.login(username, password);
		if (result == AuthenticationResult.NOSUCHUSER || result == AuthenticationResult.WRONGPASSWORD) {
			ui.setText(infoLabel, getI18NString(INCORRECT_LOGIN_MESSAGE));
			return;
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
				user.addSecurityQuestion(q, a);
			}
		}
		// save user
		if (count > 0) {
			userDao.updateUser(user);
			ui.setText(infoLabel, count + " " + getI18NString("login.new.question.saved"));
			System.out.println(user.getSecurityQuestions().size());
		}
	}

}