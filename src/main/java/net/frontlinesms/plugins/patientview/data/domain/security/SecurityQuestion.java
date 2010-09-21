package net.frontlinesms.plugins.patientview.data.domain.security;

import static net.frontlinesms.plugins.patientview.security.PasswordUtils.cryptoHash;
import static net.frontlinesms.plugins.patientview.security.PasswordUtils.fillRandomBytes;
import static net.frontlinesms.plugins.patientview.security.PasswordUtils.verify;

import java.security.GeneralSecurityException;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import net.frontlinesms.plugins.patientview.data.domain.people.User;

@Entity
@Table(name="medic_security_questions")
public class SecurityQuestion {

	/** Unique id for this entity. This is for hibernate usage. */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(unique = true, nullable = false, updatable = false)
	protected long qid;

	@ManyToOne(cascade={},fetch=FetchType.EAGER,optional=false,targetEntity=User.class)
	private User user;
	
	/** The question. */
	private String question;

	/** A hashed version of the answer. */
	@Lob
	private byte[] hash;

	/** The salt used to hash the answer. */
	@Lob
	private byte[] salt;
	
	/** For hibernate. */
	SecurityQuestion() {}

	public SecurityQuestion(String question, String answer, User user) throws GeneralSecurityException {
		this.question = question;
		this.user = user;
		salt = new byte[4];
		fillRandomBytes(salt);
		hash = cryptoHash(answer, salt);
	}

	public boolean verifyAnswer(String guess) {
		return verify(guess, hash, salt);
	}

	/**
	 * 
	 * @param questions
	 *            the questions to set for this user
	 */
	public String getQuestion() {
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

	public void setUser(User user) {
		this.user = user;
	}

	public User getUser() {
		return user;
	}
	

}