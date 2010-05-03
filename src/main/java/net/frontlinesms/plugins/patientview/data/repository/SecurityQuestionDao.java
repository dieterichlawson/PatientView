package net.frontlinesms.plugins.patientview.data.repository;

import java.util.List;

import net.frontlinesms.plugins.patientview.data.domain.people.SecurityQuestion;
import net.frontlinesms.plugins.patientview.data.domain.people.User;

public interface SecurityQuestionDao {
	
	public void saveSecurityQuestion(SecurityQuestion question);
	
	public void deleteSecurityQuestion(SecurityQuestion question);
	
	public List<SecurityQuestion> getSecurityQuestionsForUser(User user);

}
