package net.frontlinesms.plugins.patientview.data.repository.hibernate;

import java.util.Collection;
import java.util.List;

import net.frontlinesms.data.repository.hibernate.BaseHibernateDao;
import net.frontlinesms.plugins.patientview.data.domain.people.User;
import net.frontlinesms.plugins.patientview.data.domain.response.Response;
import net.frontlinesms.plugins.patientview.data.repository.UserDao;
import net.frontlinesms.plugins.patientview.security.UserSessionManager;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

/**
 * The default implementation of the User DAO
 */
public class HibernateUserDao extends BaseHibernateDao<User> implements UserDao {

	protected HibernateUserDao() {
		super(User.class);
	}

	public void deleteUser(User u) {
		super.delete(u);
	}

	public List<User> findUsersByUsername(String s) {
		DetachedCriteria c = super.getCriterion();
		c.add(Restrictions.like("username", "%" + s + "%"));
		return super.getList(c);
	}

	public Collection<User> getAllUsers() {
		return super.getAll();
	}

	public User getUserByUsername(String username) {
		DetachedCriteria c = super.getCriterion();
		c.add(Restrictions.eq("username", username));
		List<User> users = super.getList(c);
		if (users.size() >= 1) {
			return users.get(0);
		}
		return null;
	}

	public User getUsersById(long id) {
		DetachedCriteria c = super.getCriterion();
		c.add(Restrictions.eq("pid", id));
		return super.getUnique(c);
	}

	public List<User> getUsersByName(String nameFragment, int limit) {
		DetachedCriteria c = super.getCriterion();
		c.add(Restrictions.like("name", "%" + nameFragment + "%"));
		if (limit > 0)
			return super.getList(c, 0, limit);
		else {
			return super.getList(c);
		}
	}

	public void saveUser(User u) {
		super.saveWithoutDuplicateHandling(u);
	}

	public void updateUser(User u) {
		super.updateWithoutDuplicateHandling(u);
	}
	
	public void voidUser(User user, boolean keepVisible, String reason){
		user.setRemoved(true, keepVisible, UserSessionManager.getUserSessionManager().getCurrentUser(), reason);
		updateWithoutDuplicateHandling(user);
		//get a list of all responses related to this user (submitted by or about)
		DetachedCriteria c = DetachedCriteria.forClass(Response.class);
		c.add(Restrictions.or(Restrictions.eq("submitter",user), Restrictions.eq("subject", user)));
		List<Response> userResponses = super.getHibernateTemplate().findByCriteria(c);
		//void all responses
		for(Response response: userResponses){
			response.setRemoved(true, keepVisible, UserSessionManager.getUserSessionManager().getCurrentUser(), reason);
		}
	}
}
