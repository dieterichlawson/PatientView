package net.frontlinesms.plugins.medic.data.repository;

import java.util.Collection;
import java.util.List;

import net.frontlinesms.plugins.medic.data.domain.people.User;

public interface UserDao {

	public void saveUser(User s);

	public void deleteUser(User s);
	
	public void updateUser(User s);
	
	public Collection<User> getAllUsers();
	
	public List<User> getUsersByUsername(String username);
}
