package net.frontlinesms.plugins.patientview.security;

import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;

/**
 * A suite of utilities for secure encryption and checking against encrypted
 * values.
 */
public class PasswordUtils {

	/** A secure source of random data. */
	private static final SecureRandom rand = new SecureRandom();

	/**
	 * The number of iterations the cryptographic function will be run through.
	 * This is a key strengthening measure to prevent brute force attacks.
	 */
	private static final int ITERATIONS = 1000;

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
	public static byte[] cryptoHash(byte[] message, byte[] salt)
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
	public static byte[] cryptoHash(String message, byte[] salt)
			throws NoSuchAlgorithmException {
		if (message == null) {
			message = "";
		}
		return cryptoHash(message.getBytes(), salt);
	}

	/**
	 * Fills the input array with securely generated random bytes. Useful for
	 * salt creation.
	 * 
	 * @param array
	 *            the array to be filled with random bytes
	 */
	public static void fillRandomBytes(byte[] array) {
		rand.nextBytes(array);
	}

	/** Generates a random 7 digit alphanumeric passowrd. */
	public static String generatePassword() {
		String result = new BigInteger(36, rand).toString(36).toUpperCase();
		return result;
	}

	public static void main(String[] args) throws java.io.IOException,
			GeneralSecurityException {
		// User u = new User("Hank", Person.Gender.FEMALE, new Date(), "hank",
		// User.Role.READ, "");
		java.io.BufferedReader in = new java.io.BufferedReader(
				new java.io.InputStreamReader(System.in));
		String pass = "";
		while (!pass.equals("q")) {
			System.out.println("Enter a test password:");
			pass = in.readLine();
			System.out.println("Password is acceptable: "
					+ passwordMeetsRequirements(pass));
		}
		// while (!u.verifyPassword(guess)) {
		// System.out.println("\nEnter guess for verification:");
		// guess = in.readLine();
		// System.out.println(u.verifyPassword(guess));
		// }
		// for (int i = 0; i < 10; i++) {
		// System.out.println(generatePassword());
		// }
	}

	/**
	 * Checks to see if the input string meets all the requirements as set in
	 * the SecurityOptions. This consists of a length requirement, and possibly
	 * case, number and symbol requirements.
	 * 
	 * @param password
	 *            the password to be checked
	 * @return true if the password meets all requirements
	 */
	public static boolean passwordMeetsRequirements(String password) {
		SecurityOptions settings = SecurityOptions.getInstance();
		if (password.length() < settings.getPasswordLength()) {
			System.out.println("hi");
			return false;
		}
		if (settings.isCaseRequired()) {
			boolean uppercase = password.matches(".*[A-Z].*");
			boolean lowercase = password.matches(".*[a-z].*");
			if (!uppercase || !lowercase) {
				return false;
			}
		}

		if (settings.isNumberRequired()) {
			if (!password.matches(".*\\d.*")) {
				return false;
			}
		}
		if (settings.isSymbolRequired()) {
			if (!password.matches(".*[^\\w].*")) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Check if a the hash of a string matches the stored password hash.
	 * 
	 * @param guess
	 *            the password to hash and compare to this
	 * @return true if the hash of the guess matches the hash of this password
	 */
	public static boolean verify(String guess, byte[] hash, byte[] salt) {
		byte[] ghash;
		try {
			ghash = cryptoHash(guess, salt);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return false;
		}

		return Arrays.equals(hash, ghash);
	}
}
