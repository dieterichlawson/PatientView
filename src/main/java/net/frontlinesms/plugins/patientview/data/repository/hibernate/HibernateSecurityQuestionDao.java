package net.frontlinesms.plugins.patientview.data.repository.hibernate;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import net.frontlinesms.data.repository.hibernate.BaseHibernateDao;
import net.frontlinesms.plugins.patientview.data.domain.people.SecurityQuestion;
import net.frontlinesms.plugins.patientview.data.domain.people.User;
import net.frontlinesms.plugins.patientview.data.repository.SecurityQuestionDao;

public class HibernateSecurityQuestionDao extends
		BaseHibernateDao<SecurityQuestion> implements SecurityQuestionDao {

	protected HibernateSecurityQuestionDao() {
		super(SecurityQuestion.class);
	}

	public void deleteSecurityQuestion(SecurityQuestion question) {
		super.delete(question);

	}

	public List<SecurityQuestion> getSecurityQuestionsForUser(User user) {
		DetachedCriteria c = getCriterion();
		c.add(Restrictions.eq("user", user));
		return super.getList(c);
	}

	public void saveSecurityQuestion(SecurityQuestion question) {
		super.saveWithoutDuplicateHandling(question);
	}

}
