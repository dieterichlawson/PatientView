package net.frontlinesms.plugins.patientview.data.domain.people;

import static net.frontlinesms.plugins.patientview.data.domain.people.PasswordUtils.*;

import java.security.GeneralSecurityException;

import javax.persistence.*;

//@Entity
//@Table(name="medic_securityQs")
public class SecurityQuestion {

	/** Unique id for this entity. This is for hibernate usage. */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(unique = true, nullable = false, updatable = false)
	protected long pid;

	/** The question. */
	private String question;

	/** A hashed version of the answer. */
	private byte[] hash;

	/** The salt used to hash the answer. */
	private byte[] salt;

	public SecurityQuestion(String question, String answer) throws GeneralSecurityException {
		this.question = question;
		salt = new byte[4];
		fillRandomBytes(salt);
		hash = cryptoHash(answer, salt);
	}

	public boolean verifyAnswer(String guess) {
		return verify(guess, hash, salt);
	}

	/**
	 * For Hibernate only. Do not use.
	 * 
	 * @param questions
	 *            the questions to set for this user
	 */
	String getQuestion() {
		return question;
	}

	/**
	 * For Hibernate only. Do not use.
	 * 
	 * @param questions
	 *            the questions to set for this user
	 */
	void setQuestion(String question) {
		this.question = question;
	}

	/**
	 * For Hibernate only. Do not use.
	 * 
	 * @param questions
	 *            the questions to set for this user
	 */
	byte[] getHash() {
		return hash;
	}

	/**
	 * For Hibernate only. Do not use.
	 * 
	 * @param questions
	 *            the questions to set for this user
	 */
	void setHash(byte[] hash) {
		this.hash = hash;
	}

	/**
	 * For Hibernate only. Do not use.
	 * 
	 * @param questions
	 *            the questions to set for this user
	 */
	byte[] getSalt() {
		return salt;
	}

	/**
	 * For Hibernate only. Do not use.
	 * 
	 * @param questions
	 *            the questions to set for this user
	 */
	void setSalt(byte[] salt) {
		this.salt = salt;
	}
	

}