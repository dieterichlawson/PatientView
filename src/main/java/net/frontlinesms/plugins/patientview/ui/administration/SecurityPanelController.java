package net.frontlinesms.plugins.patientview.ui.administration;

import static net.frontlinesms.ui.i18n.InternationalisationUtils.*;
import net.frontlinesms.ui.ThinletUiEventHandler;
import net.frontlinesms.ui.UiGeneratorController;

/**
 * This is the ui controller for the security panel. currently it im
 * 
 * @author javins
 * 
 */
public class SecurityPanelController implements AdministrationTabPanel,
		ThinletUiEventHandler {

	/**
	 * A helper object that keeps track of a specific setting, as well as it's
	 * minimum and maximum value. The value of the setting can never be set
	 * outside of the range.
	 */
	class Range {

		private final int min;
		private final int max;
		private int value;

		Range(int min, int max, int value) {
			assert value >= min;
			assert value <= max;
			this.min = min;
			this.max = max;
			this.value = value;
		}

		/** @return the maximum value of this range */
		public int max() {
			return max;
		}

		/** @return the minimum value of this range */
		public int min() {
			return min;
		}

		/**
		 * Sets the value of this range. If the parameter is beneath the
		 * minimum, the value will be set to the minimum. Similarly, if the
		 * value is above the maximum, the value will be set to the maximim.
		 * 
		 * @param value
		 *            the new value
		 * @return false if the new value is outside the range
		 */
		public boolean setValue(int value) {
			if (value < min) {
				this.value = min;
				return false;
			}
			if (value > max) {
				this.value = max;
				return false;
			}
			this.value = value;
			return true;
		}

		public String textValue() {
			return "" + value;
		}

		public int value() {
			return value;
		}
	}

	// Files
	private final String XML_FILE = "/ui/plugins/patientview/administration/security/securitySettingsAdministrationPanel.xml";

	// I18N Strings
	private final String LOCKOUT_DURATION = "admin.security.lockout.duration";

	// UI Components
	private final Object mainPanel;
	private final Object passwordLengthBox;
	private final Object lowercaseCheckBox;
	private final Object uppercaseCheckBox;
	private final Object numbersCheckBox;
	private final Object symbolsCheckBox;
	private final Object requiredQuestionsBox;
	private final Object requiredAnswersBox;
	private final Object loginAttemptsBox;
	private final Object questionAttemptsBox;
	private final Object lockoutDurationBox;

	// Settings
	private final Range passwordLength = new Range(6, 12, 7);
	private final Range requiredQuestions = new Range(1, 4, 3);
	// cannot be final because the max is dependant on how many questions are
	// required
	private Range requiredAnswers = new Range(1, 4, 2);
	private final Range loginAttempts = new Range(1, 10, 3);
	private final Range questionAttempts = new Range(1, 10, 3);
	private final Range lockoutDuration = new Range(1, Integer.MAX_VALUE, 5);
	private boolean lowercaseLettersRequired = true;
	private boolean uppercaseLettersRequired = true;
	private boolean numbersRequired = true;
	private boolean symbolsRequired;

	private final UiGeneratorController ui;

	public SecurityPanelController(UiGeneratorController uiController) {
		ui = uiController;
		mainPanel = ui.loadComponentFromFile(XML_FILE, this);
		passwordLengthBox = ui.find(mainPanel, "passwordlengthbox");
		lowercaseCheckBox = ui.find(mainPanel, "lowercasecheckbox");
		uppercaseCheckBox = ui.find(mainPanel, "uppercasecheckbox");
		numbersCheckBox = ui.find(mainPanel, "numberscheckbox");
		symbolsCheckBox = ui.find(mainPanel, "symbolscheckbox");
		requiredQuestionsBox = ui.find(mainPanel, "requiredquestionsbox");
		requiredAnswersBox = ui.find(mainPanel, "requiredanswersbox");
		loginAttemptsBox = ui.find(mainPanel, "loginattemptsbox");
		questionAttemptsBox = ui.find(mainPanel, "questionattemptsbox");
		lockoutDurationBox = ui.find(mainPanel, "lockoutdurationbox");
		// properly split the text for the lockout duration preference
		String[] lockoutText = getI18NString(LOCKOUT_DURATION).split("<X>");
		Object label1 = ui.find(mainPanel, "durationlabel1");
		Object label2 = ui.find(mainPanel, "durationlabel2");
		ui.setText(label1, lockoutText[0].trim());
		ui.setText(label2, lockoutText[1].trim());
		// TODO: load settings from file
		setDefaultValues();
	}

	/** Callback method for the lockoutDurationBox. */
	public void boxChangedLockoutDuration() {
		spinboxChanged(lockoutDurationBox, lockoutDuration);
	}

	/** Callback method for the questionAttemptsBox */
	public void boxChangedLoginAttempts() {
		spinboxChanged(loginAttemptsBox, loginAttempts);
	}

	/** Callback method for the passwordLengthBox */
	public void boxChangedPasswordLength() {
		spinboxChanged(passwordLengthBox, passwordLength);
	}

	/** Callback method for the questionAttemptsBox */
	public void boxChangedQuestionAttempts() {
		spinboxChanged(questionAttemptsBox, questionAttempts);
	}

	/** Callback method for the requiredAnswersBox */
	public void boxChangedRequiredAnswers() {
		spinboxChanged(requiredAnswersBox, requiredAnswers);
	}

	/** Callback method for the requiredQuestionsBox */
	public void boxChangedRequiredQuestions() {
		spinboxChanged(requiredQuestionsBox, requiredQuestions);
		// reduce required answers to less than required questions
		int oldValue = requiredAnswers.value();
		if (oldValue > requiredQuestions.value()) {
			oldValue = requiredQuestions.value();
		}
		requiredAnswers = new Range(1, requiredQuestions.value(), oldValue);
		setSpinboxRange(requiredAnswersBox, requiredAnswers);

	}

	/** Callback method for the lowercaseCheckBox */
	public void boxChangedLowercase() {
		lowercaseLettersRequired = ui.getBoolean(lowercaseCheckBox, "selected");
	}

	/** Callback method for the uppercaseCheckBox */
	public void boxChangedUppercase() {
		uppercaseLettersRequired = ui.getBoolean(uppercaseCheckBox, "selected");
	}

	/** Callback method for the numbersCheckBox */
	public void boxChangedNumbers() {
		numbersRequired = ui.getBoolean(numbersCheckBox, "selected");
	}

	public void boxChangedSymbols() {
		symbolsRequired = ui.getBoolean(symbolsCheckBox, "selected");
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
		setSpinboxRange(passwordLengthBox, passwordLength);
		setSpinboxRange(requiredQuestionsBox, requiredQuestions);
		setSpinboxRange(requiredAnswersBox, requiredAnswers);
		setSpinboxRange(loginAttemptsBox, loginAttempts);
		setSpinboxRange(questionAttemptsBox, questionAttempts);
		setSpinboxRange(lockoutDurationBox, lockoutDuration);
		ui.setSelected(lowercaseCheckBox, lowercaseLettersRequired);
		ui.setSelected(uppercaseCheckBox, uppercaseLettersRequired);
		ui.setSelected(numbersCheckBox, numbersRequired);
		ui.setSelected(symbolsCheckBox, symbolsRequired);
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

}
