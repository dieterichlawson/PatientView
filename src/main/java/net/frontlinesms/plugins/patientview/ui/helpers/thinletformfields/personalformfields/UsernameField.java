package net.frontlinesms.plugins.patientview.ui.helpers.thinletformfields.personalformfields;

import net.frontlinesms.plugins.patientview.data.domain.people.Person;
import net.frontlinesms.plugins.patientview.data.domain.people.User;
import net.frontlinesms.plugins.patientview.data.repository.hibernate.HibernateUserDao;
import net.frontlinesms.plugins.patientview.ui.helpers.thinletformfields.TextBox;
import net.frontlinesms.ui.ExtendedThinlet;
import net.frontlinesms.ui.i18n.InternationalisationUtils;

import org.springframework.context.ApplicationContext;

public class UsernameField extends TextBox implements PersonalFormField{

	private Object picture;
	public static final String NAME = "usernameField";
	protected boolean hasChanged;
	private HibernateUserDao userDao;
	
	public UsernameField(ExtendedThinlet thinlet, ApplicationContext appCon,boolean useIndicator, String initialUsername){
		super(thinlet, InternationalisationUtils.getI18NString("login.username")+":", NAME);
		if(useIndicator){
			picture = thinlet.createButton("");
			thinlet.setEnabled(picture, false);
			thinlet.add(mainPanel,picture);
		}
		hasChanged = false;
		thinlet.setInteger(mainPanel, "columns", 3);
		thinlet.setAttachedObject(mainPanel, this);
		if(initialUsername !="" && initialUsername !=null ){
			thinlet.setText(super.textBox, initialUsername);
		}
		userDao = (HibernateUserDao) appCon.getBean("UserDao");
	}
	
	protected UsernameField(ExtendedThinlet thinlet, ApplicationContext appCon, boolean useIndicator, String initialUsername, String name){
		super(thinlet, "Username:",name);
		if(useIndicator){
			picture = thinlet.createButton("");
			thinlet.setEnabled(picture, false);
			thinlet.add(mainPanel,picture);
		}
		thinlet.setInteger(mainPanel, "columns", 3);
		userDao = (HibernateUserDao) appCon.getBean("UserDao");
	}
	
	public void textBoxKeyPressed(String r){
		hasChanged = true;
		if(picture != null){
			if(r.length() <=7 || userDao.getUsersByUsername(r).size() !=0){
				thinlet.setIcon(picture, "/icons/delete.png");
			}else{
				thinlet.setIcon(picture, "/icons/live.png");
			}
		}
	}
	
	public boolean isValid(){
		String r = thinlet.getText(super.textBox);
		if(r.length() <=7 || userDao.getUsersByUsername(r).size() !=0){
			return false;
		}else{
			return true;
		}
	}
	
	public boolean hasChanged(){
		return hasChanged;
	}

	public void setFieldForPerson(Person p) {
		User u;
		try{
			u = (User) p;
		}catch(Throwable t){return;}
		u.setUsername(getRawResponse());
	}
}
