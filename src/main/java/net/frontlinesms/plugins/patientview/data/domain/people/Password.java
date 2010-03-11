package net.frontlinesms.plugins.patientview.data.domain.people;

import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;

/**
 * A securely stored password. To check against a password use equals(String) or
 * equals(Password).
 */
public class Password {

	/** A secure source of random data. */
	private static transient final SecureRandom rand = new SecureRandom();

	/**
	 * The number of iterations the cryptographic function will be run through.
	 * This is a key strengthening measure to prevent brute force attacks.
	 */
	private static transient final int ITERATIONS = 1000;

	/**
	 * A cryptographic one way hash function using key strengthening.
	 * 
	 * * @param message the message to be hashed
	 * 
	 * @param salt
	 *            the salt associated with this message
	 * @return the hashed version of the message
	 * @throws NoSuchAlgorithmException
	 *             if SHA-1 can't be found (this shouldn't happen)
	 */
	protected static byte[] cryptoHash(byte[] message, byte[] salt)
			throws NoSuchAlgorithmException {
		MessageDigest digest = MessageDigest.getInstance("SHA-1");
		digest.reset();
		digest.update(salt);
		message = digest.digest(message);
		for (int i = 0; i < ITERATIONS; i++) {
			digest.reset();
			message = digest.digest(message);
		}
		return message;
	}

	/**
	 * A convienience wrapper for cryptoHash(byte[], byte[]).
	 * 
	 * @param message
	 *            the message to be hashed
	 * @param salt
	 *            the salt associated with this message
	 * @return the hashed version of the message
	 * @throws NoSuchAlgorithmException
	 *             if SHA-1 can't be found (this shouldn't happen)
	 */
	protected static byte[] cryptoHash(String message, byte[] salt)
			throws NoSuchAlgorithmException {
		return cryptoHash(message.getBytes(), salt);
	}

	/** A cryptographically hashed version of the original password. */
	private final byte[] hash;

	/** A randomly generated salt for this password. */
	private final byte[] salt;

	/**
	 * Creates a new password. Once created, a password cannot be modified.
	 * 
	 * @param value
	 *            the string to be hashed and stored
	 */
	public Password(String value) throws GeneralSecurityException {
		// generate a random salt
		salt = new byte[4];
		rand.nextBytes(salt);
		// hash and store the password
		// XXX: should getBytes("UTF-8") be used for i18n?
		hash = cryptoHash(value, salt);
	}

	/**
	 * Returns a string representation of this password in the form:
	 * <code>salt:hash</code>
	 */
	public final String toString() {
		// String saltstr = new String(salt);
		// String hashstr = new String(hash);
		String saltstr = String.format("%0" + (salt.length << 1) + "x",
				new BigInteger(1, salt));
		String hashstr = String.format("%0" + (hash.length << 1) + "x",
				new BigInteger(1, hash));
		return saltstr + ":" + hashstr;
	}

	/**
	 * Check if a the hash of a string matches the stored password hash.
	 * 
	 * @param guess
	 *            the password to hash and compare to this
	 * @return true if the hash of the guess matches the hash of this password
	 */
	public boolean verify(String guess) {
		byte[] ghash;
		try {
			ghash = cryptoHash(guess, salt);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return false;
		}
		
		return Arrays.equals(hash, ghash);
	}

	public static void main(String[] args) throws java.io.IOException,
			GeneralSecurityException {
		System.out.println("Enter a test password:");
		java.io.BufferedReader in = new java.io.BufferedReader(
				new java.io.InputStreamReader(System.in));
		String pass = in.readLine();
		Password pw = new Password(pass);
		System.out.println("\nHashed password:");
		System.out.println(pw);
		String guess = "";
		while (!pw.verify(guess)) {
			System.out.println("\nEnter guess for verification:");
			guess = in.readLine();
			System.out.println(pw.verify(guess));
		}
	}
}
