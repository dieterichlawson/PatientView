package net.frontlinesms.plugins.patientview.data.domain.people;

import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.util.Date;
import java.util.Map;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Lob;

import static net.frontlinesms.plugins.patientview.data.domain.people.PasswordUtils.*;
import static net.frontlinesms.ui.i18n.InternationalisationUtils.getI18NString;

/** A login identity for medic users. */
@Entity
@DiscriminatorValue(value = "user")
public class User extends Person {

	/**
	 * Possible user roles. They're pretty self explanatory. To add a new role,
	 * just add a new declaration in the form: <br>
	 * <pre>NEWROLE ("i18n.string.location")</pre>
	 */
	public static enum Role {
		READ      ("roles.readonly"), 
		READWRITE ("roles.readwrite"), 
		ADMIN     ("roles.admin"), 
		REGISTRAR ("roles.registrar");

		/** The i18n reference for this role. */
		private String name;

		private Role(String name) {
			this.name = name;
		}

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

		/**
		 * Returns the internationalized string representing this role.
		 * 
		 * @return the role name
		 */
		public String toString() {
			return getI18NString(name);
		}
	}

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
	
//	/** A list of security questions for password retrieval. */
//	private Map<String, byte[]> securityQuestions;

	/** Default constructor for Hibernate. */
	public User() {}

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
			String password, Role role) throws GeneralSecurityException {
		super(name, gender, birthdate);
		// TODO: Make sure no two users have the same login name
		this.username = username;
		this.role = role;
		setPassword(password);
	}

	/**
	 * This is not meant for use other than Hibernate.
	 * 
	 * @return the hash stored in this password
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
		return role.toString();
	}

	/**
	 * This is not meant for use other than Hibernate.
	 * 
	 * @return the salt stored in this password
	 */
	byte[] getSalt() {
		return salt;
	}

	/** @return the login name of this user */
	public String getUsername() {
		return username;
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
	 *             passwords
	 */
	public void setPassword(String newPass) throws GeneralSecurityException {
		salt = new byte[4];
		PasswordUtils.fillRandomBytes(salt);
		hash = cryptoHash(newPass, salt);
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
	 * @return
	 */
	public boolean verifyPassword(String guess) {
		return verify(guess, hash, salt);
	}

}
