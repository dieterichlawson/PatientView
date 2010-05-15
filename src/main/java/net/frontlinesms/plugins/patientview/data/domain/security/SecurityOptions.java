package net.frontlinesms.plugins.patientview.data.domain.security;

/** The object used to persist security settings. */
public class SecurityOptions {

	// Singleton instance
	private static SecurityOptions instance;

	// Settings
	private Range passwordLength = new Range(6, 12, 7);
	private Range requiredQuestions = new Range(1, 4, 3);
	private Range requiredAnswers = new Range(1, 4, 2);
	private Range loginAttempts = new Range(1, 10, 3);
	private Range questionAttempts = new Range(1, 10, 3);
	private Range lockoutDuration = new Range(1, Integer.MAX_VALUE, 5);
	private boolean lowercaseLettersRequired = true;
	private boolean uppercaseLettersRequired = true;
	private boolean numbersRequired = true;
	private boolean symbolsRequired = false;

	/** @return the singleton instance of this class */
	public static SecurityOptions getInstance() {
		if (instance == null) {
			instance = new SecurityOptions();
		}
		return instance;
	}

	public Range getPasswordLength() {
		return passwordLength;
	}

	public void setPasswordLength(Range passwordLength) {
		this.passwordLength = passwordLength;
	}

	public Range getRequiredQuestions() {
		return requiredQuestions;
	}

	public void setRequiredQuestions(Range requiredQuestions) {
		this.requiredQuestions = requiredQuestions;
	}

	public Range getRequiredAnswers() {
		return requiredAnswers;
	}

	public void setRequiredAnswers(Range requiredAnswers) {
		this.requiredAnswers = requiredAnswers;
	}

	public Range getLoginAttempts() {
		return loginAttempts;
	}

	public void setLoginAttempts(Range loginAttempts) {
		this.loginAttempts = loginAttempts;
	}

	public Range getQuestionAttempts() {
		return questionAttempts;
	}

	public void setQuestionAttempts(Range questionAttempts) {
		this.questionAttempts = questionAttempts;
	}

	public Range getLockoutDuration() {
		return lockoutDuration;
	}

	public void setLockoutDuration(Range lockoutDuration) {
		this.lockoutDuration = lockoutDuration;
	}

	public boolean isLowercaseLettersRequired() {
		return lowercaseLettersRequired;
	}

	public void setLowercaseLettersRequired(boolean lowercaseLettersRequired) {
		this.lowercaseLettersRequired = lowercaseLettersRequired;
	}

	public boolean isUppercaseLettersRequired() {
		return uppercaseLettersRequired;
	}

	public void setUppercaseLettersRequired(boolean uppercaseLettersRequired) {
		this.uppercaseLettersRequired = uppercaseLettersRequired;
	}

	public boolean isNumbersRequired() {
		return numbersRequired;
	}

	public void setNumbersRequired(boolean numbersRequired) {
		this.numbersRequired = numbersRequired;
	}

	public boolean isSymbolsRequired() {
		return symbolsRequired;
	}

	public void setSymbolsRequired(boolean symbolsRequired) {
		this.symbolsRequired = symbolsRequired;
	}

	public static void setInstance(SecurityOptions instance) {
		SecurityOptions.instance = instance;
	}

}
