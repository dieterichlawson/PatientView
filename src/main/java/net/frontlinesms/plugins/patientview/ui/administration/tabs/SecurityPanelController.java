package net.frontlinesms.plugins.patientview.ui.administration.tabs;

import static net.frontlinesms.ui.i18n.InternationalisationUtils.*;
import net.frontlinesms.plugins.patientview.data.domain.security.Range;
import net.frontlinesms.plugins.patientview.data.domain.security.SecurityOptions;
import net.frontlinesms.plugins.patientview.ui.administration.AdministrationTabPanel;
import net.frontlinesms.ui.ThinletUiEventHandler;
import net.frontlinesms.ui.UiGeneratorController;

/** This is the ui controller for the security settings panel. */
public class SecurityPanelController implements AdministrationTabPanel,
		ThinletUiEventHandler {

	// Files
	private final String XML_FILE = "/ui/plugins/patientview/administration/security/securitySettingsAdministrationPanel.xml";

	// I18N Strings
	private final String LOCKOUT_DURATION = "admin.security.lockout.duration";

	// UI Components
	private final UiGeneratorController ui;
	private final Object mainPanel;
	private final Object passwordLengthBox;
	private final Object lettersCheckBox;
	private final Object numbersCheckBox;
	private final Object symbolsCheckBox;
	private final Object requiredQuestionsBox;
	private final Object requiredAnswersBox;
	//private final Object loginAttemptsBox;
	//private final Object questionAttemptsBox;
	//private final Object lockoutDurationBox;

	// Persistance classes
	private final SecurityOptions securityOptions;

	public SecurityPanelController(UiGeneratorController uiController) {
		ui = uiController;
		securityOptions = SecurityOptions.getInstance();
		mainPanel = ui.loadComponentFromFile(XML_FILE, this);
		passwordLengthBox = ui.find(mainPanel, "passwordlengthbox");
		lettersCheckBox = ui.find(mainPanel, "letterscheckbox");
		numbersCheckBox = ui.find(mainPanel, "numberscheckbox");
		symbolsCheckBox = ui.find(mainPanel, "symbolscheckbox");
		requiredQuestionsBox = ui.find(mainPanel, "requiredquestionsbox");
		requiredAnswersBox = ui.find(mainPanel, "requiredanswersbox");
		//loginAttemptsBox = ui.find(mainPanel, "loginattemptsbox");
		//questionAttemptsBox = ui.find(mainPanel, "questionattemptsbox");
		//lockoutDurationBox = ui.find(mainPanel, "lockoutdurationbox");
		// properly split the text for the lockout duration preference
		//String[] lockoutText = getI18NString(LOCKOUT_DURATION).split("<X>");
		//Object label1 = ui.find(mainPanel, "durationlabel1");
		//Object label2 = ui.find(mainPanel, "durationlabel2");
		//ui.setText(label1, lockoutText[0].trim());
		//ui.setText(label2, lockoutText[1].trim());
		setDefaultValues();
	}

//	/** Callback method for the lockoutDurationBox. */
//	public void boxChangedLockoutDuration() {
//		spinboxChanged(lockoutDurationBox, securityOptions.getLockoutDurationRange());
//	}
//
//	/** Callback method for the questionAttemptsBox */
//	public void boxChangedLoginAttempts() {
//		spinboxChanged(loginAttemptsBox, securityOptions.getLoginAttemptsRange());
//	}

	/** Callback method for the passwordLengthBox */
	public void boxChangedPasswordLength() {
		spinboxChanged(passwordLengthBox, securityOptions.getPasswordLengthRange());
	}

//	/** Callback method for the questionAttemptsBox */
//	public void boxChangedQuestionAttempts() {
//		spinboxChanged(questionAttemptsBox, securityOptions
//				.getQuestionAttemptsRange());
//	}

	/** Callback method for the requiredAnswersBox */
	public void boxChangedRequiredAnswers() {
		spinboxChanged(requiredAnswersBox, securityOptions.getRequiredAnswersRange());
	}

	/** Callback method for the requiredQuestionsBox */
	public void boxChangedRequiredQuestions() {
		Range reqQ = securityOptions.getRequiredQuestionsRange();
		Range reqA = securityOptions.getRequiredAnswersRange();
		spinboxChanged(requiredQuestionsBox, reqQ);
		// reduce required answers to less than required questions
		int oldValue = reqA.value();
		if (oldValue > reqQ.value()) {
			oldValue = reqA.value();
		}
		securityOptions.setRequiredAnswersRange(new Range(1, reqQ.value(), oldValue));
		setSpinboxRange(requiredAnswersBox, securityOptions.getRequiredAnswersRange());

	}

	/** Callback method for the lettersCheckBox */
	public void boxChangedLetters() {
		securityOptions.setLettersRequired(ui.getBoolean(
				lettersCheckBox, "selected"));
	}

	/** Callback method for the numbersCheckBox */
	public void boxChangedNumbers() {
		securityOptions.setNumbersRequired(ui.getBoolean(numbersCheckBox,
				"selected"));
	}

	public void boxChangedSymbols() {
		securityOptions.setSymbolsRequired(ui.getBoolean(symbolsCheckBox,
				"selected"));
	}

	/* from interface */
	public String getListItemTitle() {
		return getI18NString("admin.actionlist.manage.security");
	}

	/* from interface */
	public Object getPanel() {
		return mainPanel;
	}

	/**
	 * Sets all of the components in the panel to be their default value.
	 */
	protected void setDefaultValues() {
		setSpinboxRange(passwordLengthBox, securityOptions.getPasswordLengthRange());
		setSpinboxRange(requiredQuestionsBox, securityOptions
				.getRequiredQuestionsRange());
		setSpinboxRange(requiredAnswersBox, securityOptions
				.getRequiredAnswersRange());
		//setSpinboxRange(loginAttemptsBox, securityOptions.getLoginAttemptsRange());
		//setSpinboxRange(questionAttemptsBox, securityOptions.getQuestionAttemptsRange());
		//setSpinboxRange(lockoutDurationBox, securityOptions.getLockoutDurationRange());
		ui.setSelected(lettersCheckBox, securityOptions.isCaseRequired());
		ui.setSelected(numbersCheckBox, securityOptions.isNumberRequired());
		ui.setSelected(symbolsCheckBox, securityOptions.isSymbolRequired());
	}

	/**
	 * Sets the maximum, minimum, and default values of a spinbox based on a
	 * range.
	 * 
	 * @param spinbox
	 *            the spinbox to set
	 * @param range
	 *            the range of allowable values
	 */
	protected void setSpinboxRange(Object spinbox, Range range) {
		ui.setText(spinbox, range.textValue());
		ui.setInteger(spinbox, "maximum", range.max());
		ui.setInteger(spinbox, "minimum", range.min());
	}

	/**
	 * A general handler for a spinbox with a range. This handler gets the new
	 * value from the spinbox, sets the range to that value, and if the value is
	 * outside the range, sets the spinbox back to an allowable state (the
	 * maximum or minimum value).
	 * 
	 * @param spinbox
	 *            the spinbox that changed
	 * @param range
	 *            the range associated with the spinbox
	 */
	protected void spinboxChanged(Object spinbox, Range range) {
		String newText = ui.getText(spinbox);
		if (newText != null && !newText.equals("")) {
			int newValue = Integer.parseInt(newText);
			boolean outside = !range.setValue(newValue);
			if (outside) {
				ui.setText(spinbox, range.textValue());
			}
		}
	}

	public String getIconPath() {
		return "/icons/big_lock.png";
	}
	
	public void viewWillAppear() {}

}
