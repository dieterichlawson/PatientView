package net.frontlinesms.plugins.medic.ui.helpers.thinletformfields;

import net.frontlinesms.plugins.medic.data.domain.people.User.Role;
import net.frontlinesms.ui.ExtendedThinlet;

public class RoleComboBox extends ThinletFormField<Role>{

	protected Object comboBox;
	protected boolean hasChanged;
	public static final String NAME = "roleComboBox";
	
	public RoleComboBox(ExtendedThinlet thinlet, Role role) {
		super(thinlet, "Role:",NAME);
		hasChanged = false;
		comboBox = thinlet.create("combobox");
		thinlet.add(comboBox,thinlet.createComboboxChoice("Administrator", Role.ADMIN));
		thinlet.add(comboBox,thinlet.createComboboxChoice("Read/Write", Role.READWRITE));
		thinlet.add(comboBox,thinlet.createComboboxChoice("Read Only", Role.READ));
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
		thinlet.setSelectedIndex(comboBox, s == Role.ADMIN? 0: s == Role.READWRITE? 1:2);
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
	
	
}
