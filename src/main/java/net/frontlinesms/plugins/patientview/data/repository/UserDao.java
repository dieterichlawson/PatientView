package net.frontlinesms.plugins.patientview.data.repository;

import java.util.Collection;
import java.util.List;

import net.frontlinesms.plugins.patientview.data.domain.people.User;

public interface UserDao {

	// CRUD
	/**
	 * Saves a user, creating a new database record. This method does NOT check
	 * for duplicates.
	 * 
	 * @param s
	 */
	public void saveUser(User s);

	/**
	 * Permanently deletes a user's record. Should not be used - use void
	 * instead.
	 * 
	 * @param s
	 */
	public void deleteUser(User s);

	/**
	 * Updates an existing User's recprd in the database. This method does NOT
	 * check for duplicates.
	 * 
	 * @param s
	 */
	public void updateUser(User s);

	/**
	 * Returns all users
	 * 
	 * @return
	 */
	public Collection<User> getAllUsers();

	/**
	 * Finds all users with a username that has the parameter as a substring.
	 * This will return all users if passed an empty string
	 * 
	 * @param username
	 *            part or all of a username
	 * @return the list of users
	 */
	public List<User> findUsersByUsername(String username);

	/**
	 * Returns the user with the input username, or null if there is no user for
	 * the username.
	 * 
	 * @param username
	 * @return the user
	 */
	public User getUserByUsername(String username);

	public User getUsersById(long id);

	public List<User> getUsersByName(String s, int limit);
}
