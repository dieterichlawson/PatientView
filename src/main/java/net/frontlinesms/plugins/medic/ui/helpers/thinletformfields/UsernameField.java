package net.frontlinesms.plugins.medic.ui.helpers.thinletformfields;

import net.frontlinesms.plugins.medic.data.repository.hibernate.HibernateUserDao;
import net.frontlinesms.ui.ExtendedThinlet;

import org.springframework.context.ApplicationContext;

public class UsernameField extends ThinletFormField<String>{

	private Object textBox;
	private Object picture;
	public static final String NAME = "usernameField";
	
	private HibernateUserDao userDao;
	
	public UsernameField(ExtendedThinlet thinlet, ApplicationContext appCon,boolean useIndicator){
		super(thinlet, "Username:",NAME);
		textBox =thinlet.createTextfield(null, "");
		thinlet.setAction(textBox, "textBoxKeyPressed(this.text)", null, this);
		thinlet.add(mainPanel,textBox);
		if(useIndicator){
			picture = thinlet.createButton("");
			thinlet.setEnabled(picture, false);
			thinlet.add(mainPanel,picture);
		}
		thinlet.setInteger(textBox, "weightx", 1);
		thinlet.setInteger(mainPanel, "colspan", 1);
		thinlet.setInteger(mainPanel, "columns", 3);
		thinlet.setAttachedObject(mainPanel, this);
		userDao = (HibernateUserDao) appCon.getBean("UserDao");
	}
	
	protected UsernameField(ExtendedThinlet thinlet, ApplicationContext appCon, boolean useIndicator, String name){
		super(thinlet, "Username:",name);
		textBox =thinlet.createTextfield(null, "username");
		thinlet.setAction(textBox, "textBoxKeyPressed(this.text)", null, this);
		thinlet.add(mainPanel,textBox);
		if(useIndicator){
			picture = thinlet.createButton("");
			thinlet.setEnabled(picture, false);
			thinlet.add(mainPanel,picture);
		}
		thinlet.setInteger(textBox, "weightx", 1);
		thinlet.setInteger(mainPanel, "colspan", 1);
		thinlet.setInteger(mainPanel, "columns", 3);
		userDao = (HibernateUserDao) appCon.getBean("UserDao");
	}
	
	public void textBoxKeyPressed(String response){
		if(picture != null){
			if(response.length() <=7 || userDao.getUsersByUsername(response).size() !=0){
				thinlet.setIcon(picture, "/icons/delete.png");
			}else{
				thinlet.setIcon(picture, "/icons/live.png");
			}
		}
	}
	
	@Override
	public String getResponse() {
		return thinlet.getText(textBox);
	}
	
	@Override
	public void setResponse(String s) {
		textBoxKeyPressed(s);
	}
	
	@Override
	public boolean hasResponse() {
		return (getResponse() != "");
	}

	public boolean isValid() {
		return true;
	}

	

}
