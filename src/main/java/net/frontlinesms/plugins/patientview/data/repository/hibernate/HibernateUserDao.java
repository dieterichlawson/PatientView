package net.frontlinesms.plugins.patientview.data.repository.hibernate;

import java.util.Collection;
import java.util.List;

import net.frontlinesms.data.repository.hibernate.BaseHibernateDao;
import net.frontlinesms.plugins.patientview.data.domain.people.User;
import net.frontlinesms.plugins.patientview.data.repository.UserDao;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

public class HibernateUserDao extends BaseHibernateDao<User> implements UserDao{

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
		DetachedCriteria c= super.getCriterion();
		c.add(Restrictions.like("name", "%"+s+"%"));
		return super.getList(c);
	}
	
	public List<User> getUsersByName(String s, int limit) {
		DetachedCriteria c= super.getCriterion();
		c.add(Restrictions.like("name", "%"+s+"%"));
		if(limit > 0)
			return super.getList(c, 0, limit);
		else{
			return super.getList(c);
		}
	}

	public User getUsersById(long id) {
		DetachedCriteria c = super.getCriterion();
		c.add(Restrictions.eq("pid",id));
		return super.getUnique(c);
	}
}
