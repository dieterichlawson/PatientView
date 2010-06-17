package net.frontlinesms.plugins.patientview.data.domain.security;

/** The object used to persist security settings. */
public class SecurityOptions {

	// Singleton instance
	private static SecurityOptions instance;

	/** @return the singleton instance of this class */
	public static SecurityOptions getInstance() {
		if (instance == null) {
			instance = new SecurityOptions();
		}
		return instance;
	}

	// Settings
	protected Range passwordLength = new Range(6, 12, 7);
	protected Range requiredQuestions = new Range(1, 4, 1);
	protected Range requiredAnswers = new Range(1, 4, 1);
	protected Range loginAttempts = new Range(1, 10, 3);
	protected Range questionAttempts = new Range(1, 10, 3);
	protected Range lockoutDuration = new Range(1, Integer.MAX_VALUE, 5);
	protected boolean lettersRequired = false;
	protected boolean numbersRequired = false;
	protected boolean symbolsRequired = false;

	public Range getLockoutDurationRange() {
		return lockoutDuration;
	}
	
	public int getLockoutDuration() {
		return lockoutDuration.value();
	}

	public Range getLoginAttemptsRange() {
		return loginAttempts;
	}
	
	public int getLoginAttempts() {
		return loginAttempts.value();
	}

	public Range getPasswordLengthRange() {
		return passwordLength;
	}
	
	public int getPasswordLength() {
		return passwordLength.value();
	}

	public Range getQuestionAttemptsRange() {
		return questionAttempts;
	}
	
	public int getQuestionAttempts() {
		return questionAttempts.value();
	}

	public Range getRequiredAnswersRange() {
		return requiredAnswers;
	}
	
	public int getRequiredAnswers() {
		return requiredAnswers.value();
	}

	public Range getRequiredQuestionsRange() {
		return requiredQuestions;
	}
	
	public int getRequiredQuestions() {
		return requiredQuestions.value();
	}

	public boolean isCaseRequired() {
		return lettersRequired;
	}

	public boolean isNumberRequired() {
		return numbersRequired;
	}

	public boolean isSymbolRequired() {
		return symbolsRequired;
	}

	public void setLettersRequired(boolean lettersRequired) {
		this.lettersRequired = lettersRequired;
	}

	public void setLockoutDurationRange(Range lockoutDuration) {
		this.lockoutDuration = lockoutDuration;
	}

	public void setLoginAttemptsRange(Range loginAttempts) {
		this.loginAttempts = loginAttempts;
	}

	public void setNumbersRequired(boolean numbersRequired) {
		this.numbersRequired = numbersRequired;
	}

	public void setPasswordLengthRange(Range passwordLength) {
		this.passwordLength = passwordLength;
	}

	public void setQuestionAttemptsRange(Range questionAttempts) {
		this.questionAttempts = questionAttempts;
	}

	public void setRequiredAnswersRange(Range requiredAnswers) {
		this.requiredAnswers = requiredAnswers;
	}

	public void setRequiredQuestionsRange(Range requiredQuestions) {
		this.requiredQuestions = requiredQuestions;
	}

	public void setSymbolsRequired(boolean symbolsRequired) {
		this.symbolsRequired = symbolsRequired;
	}

}
