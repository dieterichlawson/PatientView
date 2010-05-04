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

	// Files
	private final String XML_FILE = "/ui/plugins/patientview/admintab/security_settings.xml";

	// I18N Strings
	private final String LOCKOUT_DURATION = "admin.security.lockout.duration";



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

		/** @return the minimum value of this range */
		public int min() {
			return min;
		}

		/** @return the maximum value of this range */
		public int max() {
			return max;
		}

		public int value() {
			return value;
		}

		public String textValue() {
			return "" + value;
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
	}

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
	private final Range requiredAnswers = new Range(1, 4, 2);
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
		//TODO: load settings from file
		setDefaultValues();
	}

	/**
	 * Sets all of the components in the panel to be their default value.
	 */
	protected void setDefaultValues() {
		ui.setText(passwordLengthBox, passwordLength.textValue());
		ui.setText(requiredQuestionsBox, requiredQuestions.textValue());
		ui.setText(requiredAnswersBox, requiredAnswers.textValue());
		ui.setText(loginAttemptsBox, loginAttempts.textValue());
		ui.setText(questionAttemptsBox, questionAttempts.textValue());
		ui.setText(lockoutDurationBox, lockoutDuration.textValue());
		ui.setSelected(lowercaseCheckBox, lowercaseLettersRequired);
		ui.setSelected(uppercaseCheckBox, uppercaseLettersRequired);
		ui.setSelected(numbersCheckBox, numbersRequired);
		ui.setSelected(symbolsCheckBox, symbolsRequired);
	}

	public String getListItemTitle() {
		return getI18NString("admin.actionlist.manage.security");
	}

	public Object getPanel() {
		return mainPanel;
	}

}
