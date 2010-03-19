package net.frontlinesms.plugins.patientview.ui.helpers.thinletformfields.personalformfields;

import net.frontlinesms.plugins.patientview.data.domain.people.Person;
import net.frontlinesms.plugins.patientview.data.domain.people.User;
import net.frontlinesms.plugins.patientview.data.domain.people.User.Role;
import net.frontlinesms.plugins.patientview.ui.helpers.thinletformfields.ThinletFormField;
import net.frontlinesms.ui.ExtendedThinlet;
import net.frontlinesms.ui.i18n.InternationalisationUtils;

public class RoleComboBox extends ThinletFormField<Role> implements PersonalFormField{

	protected Object comboBox;
	protected boolean hasChanged;
	public static final String NAME = "roleComboBox";
	public RoleComboBox(ExtendedThinlet thinlet, Role role) {
		super(thinlet, InternationalisationUtils.getI18NString("medic.common.labels.role")+":", NAME);
		hasChanged = false;
		comboBox = thinlet.create("combobox");
		for(Role r:Role.values()){
			thinlet.add(comboBox,thinlet.createComboboxChoice(Role.getRoleName(r), r));
		}
		thinlet.setAction(comboBox, "selectionChanged(this.selected)", null, this);
		thinlet.add(mainPanel,comboBox);
		thinlet.setInteger(comboBox, "weightx", 5);
		setRawResponse(role);
		thinlet.setAttachedObject(mainPanel, this);
	}

	public void selectionChanged(int index){
		if(index >=0){
			hasChanged = true;
		}
	}

	@Override
	public boolean isValid() {
		return hasResponse();
	}
	
	public boolean hasChanged(){		
		return hasChanged;
	}

	@Override
	public Role getRawResponse() {
		return (Role) thinlet.getAttachedObject(thinlet.getSelectedItem(comboBox));
	}
	
	@Override
	public void setRawResponse(Role s) {
		thinlet.setText(comboBox, Role.getRoleName(s));
		for(int i =0; i < Role.values().length;i++){
			if(Role.values()[i] == s){
				thinlet.setSelectedIndex(comboBox,i);
			}
		}
	}

	@Override
	public String getResponse() {
		return Role.getRoleName(getRawResponse());
	}

	@Override
	public void setResponse(String response) {
		if(Role.getRoleForName(response) != null)
			setRawResponse(Role.getRoleForName(response));
	}

	/**
	 * sets the role of the person passed in, if it is a User
	 * @see net.frontlinesms.plugins.patientview.ui.helpers.thinletformfields.personalformfields.PersonalFormField#setFieldForPerson(net.frontlinesms.plugins.patientview.data.domain.people.Person)
	 */
	public void setFieldForPerson(Person p) {
		User user;
		try{
			user = (User) p;
		}catch(Throwable t){ return;}
		user.setRole(getRawResponse());
	}
}
