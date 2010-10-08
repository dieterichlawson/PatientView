package net.frontlinesms.plugins.patientview.ui.thinletformfields.fieldgroups;

import net.frontlinesms.plugins.patientview.data.domain.people.User;
import net.frontlinesms.plugins.patientview.data.repository.UserDao;
import net.frontlinesms.plugins.patientview.security.UserSessionManager;
import net.frontlinesms.plugins.patientview.ui.thinletformfields.FormFieldDelegate;
import net.frontlinesms.plugins.patientview.ui.thinletformfields.personalformfields.RoleComboBox;
import net.frontlinesms.plugins.patientview.ui.thinletformfields.personalformfields.UsernameField;
import net.frontlinesms.ui.UiGeneratorController;

import org.springframework.context.ApplicationContext;

public class UserFieldGroup extends PersonFieldGroup<User> {

	private UserDao userDao;
	
	public UserFieldGroup(UiGeneratorController ui, ApplicationContext appCon, FormFieldDelegate delegate, User person) {
		super(ui, appCon, delegate, person);
		this.userDao = (UserDao) appCon.getBean("UserDao");
	}

	@Override
	protected void addAdditionalFields() {
		if(person == null || UserSessionManager.getUserSessionManager().getCurrentUser().equals(person)){
			UsernameField usernameField = new UsernameField(ui, appCon, true, person == null ? "" : person.getUsername(),null);
			super.addField(usernameField);
		}
		RoleComboBox roleCombo = new RoleComboBox(ui, person == null ? null :person.getRole(),null);
		super.addField(roleCombo);
	}
	
	@Override
	protected void saveOrUpdatePerson() {
		if(isNewPersonGroup){
			userDao.saveUser(person);
		}else{
			userDao.updateUser(person);
		}
	}

	@Override
	protected User createNewPerson() {
		return new User();
	}

}
