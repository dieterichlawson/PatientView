package net.frontlinesms.plugins.patientview.data.repository.hibernate;

import java.util.List;

import net.frontlinesms.data.repository.hibernate.BaseHibernateDao;
import net.frontlinesms.plugins.patientview.data.domain.people.SecurityQuestion;
import net.frontlinesms.plugins.patientview.data.domain.people.User;
import net.frontlinesms.plugins.patientview.data.repository.SecurityQuestionDao;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

public class HibernateSecurityQuestionDao extends BaseHibernateDao<SecurityQuestion> implements SecurityQuestionDao {

	protected HibernateSecurityQuestionDao() {
		super(SecurityQuestion.class);
	}

	public void deleteSecurityQuestion(SecurityQuestion question) {
		super.delete(question);
	}

	public List<SecurityQuestion> getAllSecurityQuestionsForUser() {
		return super.getAll();
	}

	public List<SecurityQuestion> getSecurityQuestionsByQuestion(String question) {
		DetachedCriteria c = super.getCriterion();
		c.add(Restrictions.like("question", "%"+question+"%"));
		return null;
	}

	public List<SecurityQuestion> getSecurityQuestionsForUser(User user) {
		DetachedCriteria c = super.getCriterion();
		c.add(Restrictions.eq("user", user));
		return super.getList(c);
	}

	public void saveOrUpdateSecurityQuestion(SecurityQuestion question) {
		super.getHibernateTemplate().saveOrUpdate(question);
	}


}
