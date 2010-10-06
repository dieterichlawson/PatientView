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
	 * Returns the user with the supplied username, or null if there is no user
	 * for the username. This method should not be confused with getUsersByName,
	 * which searches for users via their real names as opposed to this method,
	 * which searches with usernames.
	 * 
	 * @param username
	 * @return the user
	 */
	public User getUserByUsername(String username);

	/**
	 * Returns the user with the supplied ID, or null if there is none
	 * 
	 * @param id
	 * @return
	 */
	public User getUsersById(long id);

	/**
	 * Returns a list of users that have 'nameFragment' in their name by running
	 * a 'like' query with the supplied string. The like query is constructed to
	 * be placement-agnostic (it doesn't care where the name fragment is in the
	 * name, it just cares whether or not its there).
	 * 
	 * @param nameFragment
	 * @param limit
	 *            The maximum number of results returned
	 * @return
	 */
	public List<User> getUsersByName(String nameFragment, int limit);

	/**
	 * Voids this user. Also voids all responses submitted by or about this
	 * user.
	 * 
	 * @param user
	 * @param keepVisible
	 * @param reason
	 */
	public void voidUser(User user, boolean keepVisible, String reason);
}
