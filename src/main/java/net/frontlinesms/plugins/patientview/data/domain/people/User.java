package net.frontlinesms.plugins.patientview.data.domain.people;

import static net.frontlinesms.plugins.patientview.data.domain.people.PasswordUtils.cryptoHash;
import static net.frontlinesms.plugins.patientview.data.domain.people.PasswordUtils.fillRandomBytes;
import static net.frontlinesms.plugins.patientview.data.domain.people.PasswordUtils.generatePassword;
import static net.frontlinesms.plugins.patientview.data.domain.people.PasswordUtils.verify;
import static net.frontlinesms.ui.i18n.InternationalisationUtils.getI18NString;

import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.OneToMany;

/** A login identity for medic users. */
@Entity
@DiscriminatorValue(value = "user")
public class User extends Person {

	/**
	 * Possible user roles. They're pretty self explanatory. To add a new role,
	 * just add a new declaration in the form: <br>
	 * 
	 * <pre>
	 * NEWROLE(&quot;i18n.string.location&quot;)
	 * </pre>
	 */
	public static enum Role {
		READ("roles.readonly"), // can only look up information
		READWRITE("roles.readwrite"), // can edit patient and chw data
		ADMIN("roles.admin"), // administration official, can add new users
		REGISTRAR("roles.registrar"); // patient registrar

		/**
		 * Returns the role object representing the string passed in. Returns
		 * null if a role could not be found for the name.
		 * 
		 * @param name
		 *            the name of the role
		 * @return the role corrosponding to the name
		 */
		public static Role getRoleForName(String name) {
			for (Role r : Role.values()) {
				if (name == r.toString()) {
					return r;
				}
			}
			return null;
		}

		/** The i18n reference for this role. */
		private String name;

		private Role(String name) {
			this.name = name;
		}

		/**
		 * Returns the internationalized string representing this role.
		 * 
		 * @return the role name
		 */
		public String toString() {
			return getI18NString(name);
		}
	}
	
	@OneToMany(cascade=CascadeType.REMOVE,fetch=FetchType.LAZY,mappedBy="user",targetEntity=SecurityQuestion.class)
	protected Set<SecurityQuestion> questions;

	/** The login name of this user. */
	private String username;

	/** The hashed password of this user. */
	@Lob
	private byte[] hash;

	/** The salt for this user's password. */
	@Lob
	private byte[] salt;

	/** The role of this user. */
	@Enumerated(EnumType.STRING)
	private Role role;

	/** True if this user needs a new password. */
	private boolean passwordCurrent;

//	/** A list of security questions for password retrieval. */
////	private HashMap<String, byte[]> securityQuestions;

	/** Default constructor for Hibernate. */
	public User() {
	}

	/**
	 * 
	 * @param name
	 * @param gender
	 * @param birthdate
	 * @param username
	 * @param password
	 * @param role
	 * @throws GeneralSecurityException
	 */
	public User(String name, Gender gender, Date birthdate, String username,
			Role role, String password) throws GeneralSecurityException {
		super(name, gender, birthdate);
		// TODO: Make sure no two users have the same login name
		assert username != null;
		this.username = username;
		assert role != null;
		this.role = role;
		if (password != null) {
			setPassword(password);
		} else {
			assignTempPassword();
		}
		// questions = new ArrayList<SecurityQuestion>();
		questions = new TreeSet<SecurityQuestion>();
	}

	public User(String username, Role role) throws GeneralSecurityException {
		this(null, null, null, username, role, null);
	}

	/**
	 * Adds a secutity question to this user.
	 * 
	 * @param question
	 *            the question to be added
	 * @param answer
	 *            the answer to be encrypted
	 * @throws GeneralSecurityException
	 *             if the crypto library cannot be found
	 */
	public void addSecurityQuestion(String question, String answer)
			throws GeneralSecurityException {
		SecurityQuestion q = new SecurityQuestion(question, answer, this);
		questions.add(q);
	}

	/**
	 * Creates and assigns a temporary password to this user. Their old password
	 * will be overwritten when this method is called. This method flags the
	 * user as needing to create a new password.
	 * 
	 * @return the new temporary password
	 * @throws GeneralSecurityException
	 *             when a securely random password cannot be generated
	 * @see {@link 
	 *      net.frontlinesms.plugins.patientview.data.domain.people.PasswordUtils
	 *      .generatePassword()}
	 */
	public String assignTempPassword() throws GeneralSecurityException {
		String newPass = generatePassword();
		setPassword(newPass);
		passwordCurrent = false;
		return newPass;
	}

	/**
	 * This is not meant for use other than Hibernate.
	 * 
	 * @return the hashed password stored in this user
	 */
	byte[] getHash() {
		return hash;
	}

	/** @return the hashed version of this user's password */
	public String getPassword() {
		String saltstr = String.format("%0" + (salt.length << 1) + "x",
				new BigInteger(1, salt));
		String hashstr = String.format("%0" + (hash.length << 1) + "x",
				new BigInteger(1, hash));
		return saltstr + ":" + hashstr;
	}

	@Override
	public String getPersonType() {
		return "User";
	}

	/** @return the role of this user */
	public Role getRole() {
		return role;
	}

	/** @return the string corrosponding to the role of this user */
	public String getRoleName() {
		if (role != null) {
			return role.toString();
		}
		return "";
	}

	/**
	 * Returns the salt used to store this user's password.
	 * 
	 * @return this user's salt
	 */
	public byte[] getSalt() {
		return salt.clone();
	}

	/**
	 * Returns the security questions of this user. The questions can be
	 * accessed by calling the keySet() method, and guesses should be hashed and
	 * compared to the value associated with the key.
	 * 
	 * @return the security questions of this user
	 */
	public Set<SecurityQuestion> getSecurityQuestions() {
		return questions;
	}

	/** @return the login name of this user */
	public String getUsername() {
		return username;
	}

	/**
	 * This is not meant for use other than Hibernate.
	 * 
	 * @return true if the user's password is current
	 */
	boolean isPasswordCurrent() {
		return passwordCurrent;
	}

	/**
	 * Under certain circumstances (e.g. new user creation, password expiration)
	 * users have their old password, but need to generate a new one. This
	 * utility returns true if so.
	 * 
	 * @return true if the user needs a new password
	 */
	public boolean needsNewPassword() {
		return !passwordCurrent;
	}

	/**
	 * Sets the hash value in this password. This is not meant for use other
	 * than Hibernate. The hash must align with the salt or the password will be
	 * meaningless.
	 * 
	 * @param hash
	 *            the new hash
	 */
	void setHash(byte[] hash) {
		this.hash = hash;
	}

	/**
	 * Assigns a new password to this user.
	 * 
	 * @param newPass
	 *            the new password
	 * @throws GeneralSecurityException
	 *             when a valid crypto algorithm cannot be found
	 */
	public void setPassword(String newPass) throws GeneralSecurityException {
		if (salt == null) {
			salt = new byte[4];
			fillRandomBytes(salt);
		}
		hash = cryptoHash(newPass, salt);
		passwordCurrent = true;
	}

	/**
	 * This is not meant for use other than Hibernate.
	 * 
	 * @param if the users password is current
	 */
	void setPasswordCurrent(boolean passwordCurrent) {
		this.passwordCurrent = passwordCurrent;
	}

	/**
	 * Sets the role of this user.
	 * 
	 * @param role
	 *            the new role for this user
	 */
	public void setRole(Role role) {
		this.role = role;
	}

	/**
	 * Sets the salt value in this password. This is not meant for use other
	 * than by Hibernate. The salt must align with the hash or the password will
	 * be meaningless.
	 * 
	 * @param salt
	 *            the new salt
	 */
	void setSalt(byte[] salt) {
		this.salt = salt;
	}

//	/**
//	 * For Hibernate only. Do not use.
//	 * 
//	 * @param questions
//	 *            the questions to set for this user
//	 */
//	void setSecurityQuestions(HashMap<String, byte[]> questions) {
//		questions = new HashMap<String, byte[]>(questions);
//	}

	/**
	 * Sets the login name of this user.
	 * 
	 * @param username
	 *            the new login name of this user
	 */
	public void setUsername(String username) {
		// TODO: Make sure no two users have the same login name
		this.username = username;
	}

	/**
	 * Use this method to check if a password attempt is valid. Returns true if
	 * the the hashed version of the guess is equal to the stored password hash
	 * for this user.
	 * 
	 * @param guess
	 *            the guess to verify
	 * @return true if the hash of the guess is equal to the stored password
	 *         hash
	 */
	public boolean verifyPassword(String guess) {
		return verify(guess, hash, salt);
	}

}
