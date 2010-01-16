package net.frontlinesms.plugins.medic.ui.helpers.thinletformfields;

import net.frontlinesms.plugins.medic.data.repository.hibernate.HibernateUserDao;
import net.frontlinesms.ui.ExtendedThinlet;

import org.springframework.context.ApplicationContext;

public class UsernameField extends TextBox{

	private Object picture;
	public static final String NAME = "usernameField";
	
	private HibernateUserDao userDao;
	
	public UsernameField(ExtendedThinlet thinlet, ApplicationContext appCon,boolean useIndicator){
		super(thinlet, "Username:",NAME);
		if(useIndicator){
			picture = thinlet.createButton("");
			thinlet.setEnabled(picture, false);
			thinlet.add(mainPanel,picture);
		}
		thinlet.setInteger(mainPanel, "columns", 3);
		thinlet.setAttachedObject(mainPanel, this);
		userDao = (HibernateUserDao) appCon.getBean("UserDao");
	}
	
	protected UsernameField(ExtendedThinlet thinlet, ApplicationContext appCon, boolean useIndicator, String name){
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
		if(picture != null){
			if(r.length() <=7 || userDao.getUsersByUsername(r).size() !=0){
				thinlet.setIcon(picture, "/icons/delete.png");
			}else{
				thinlet.setIcon(picture, "/icons/live.png");
			}
		}
	}
}
