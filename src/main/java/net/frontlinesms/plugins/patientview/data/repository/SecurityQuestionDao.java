package net.frontlinesms.plugins.patientview.data.repository;

import java.util.List;

import net.frontlinesms.plugins.patientview.data.domain.people.SecurityQuestion;
import net.frontlinesms.plugins.patientview.data.domain.people.User;

public interface SecurityQuestionDao {

	
	public List<SecurityQuestion> getSecurityQuestionsForUser(User user);
	
	public List<SecurityQuestion> getSecurityQuestionsByQuestion(String question);
	
	public List<SecurityQuestion> getAllSecurityQuestionsForUser();
	
	public void saveOrUpdateSecurityQuestion(SecurityQuestion question);
	
	public void deleteSecurityQuestion(SecurityQuestion question);
}
