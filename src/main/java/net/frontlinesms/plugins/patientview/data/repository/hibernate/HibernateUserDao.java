package net.frontlinesms.plugins.patientview.data.repository.hibernate;

import java.util.Collection;
import java.util.List;

import net.frontlinesms.data.repository.hibernate.BaseHibernateDao;
import net.frontlinesms.plugins.patientview.data.domain.people.User;
import net.frontlinesms.plugins.patientview.data.repository.UserDao;

import org.hibernate.Query;

public class HibernateUserDao extends BaseHibernateDao<User> implements UserDao{

	private static final String getUsersbyUsernameQuery = "select u from User u where u.username =";
	private static final String getUsersByNameQuery = "select u from User u where u.name like :name";
	protected HibernateUserDao() {
		super(User.class);
	}

	public void deleteUser(User u) {
		super.delete(u);
	}

	public Collection<User> getAllUsers() {
		return super.getAll();
	}

	public void saveUser(User u) {
		super.saveWithoutDuplicateHandling(u);
	}

	public void updateUser(User u) {
		super.updateWithoutDuplicateHandling(u);
	}
	
	public List<User> getUsersByUsername(String s) {
		Query q= super.getSession().createQuery(getUsersbyUsernameQuery + " '" +s +"'");		
		return q.list();
	}
	
	public Collection<User> getUsersByName(String s, int limit) {
		Query q= super.getSession().createQuery(getUsersByNameQuery);
		q.setParameter("name", "%" + s+"%");
		if(limit != -1){
			q.setFetchSize(limit);
			q.setMaxResults(limit);
		}
		return q.list();
	}
}
