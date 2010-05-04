package net.frontlinesms.plugins.patientview.data.repository;

import java.util.Collection;
import java.util.List;

import net.frontlinesms.plugins.patientview.data.domain.people.User;

public interface UserDao {

	public void saveUser(User s);

	public void deleteUser(User s);
	
	public void updateUser(User s);
	
	public Collection<User> getAllUsers();
	
	public List<User> getUsersByUsername(String username);
	
	public User getUsersById(long id);
	
	public List<User> getUsersByName(String s, int limit);
}
