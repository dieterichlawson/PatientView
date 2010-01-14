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
		response = role;
		thinlet.setInteger(comboBox, "weightx", 5);
		//initialize the textbox
		if(response == Role.ADMIN){
			thinlet.setText(comboBox, "Administrator");
			thinlet.setSelectedIndex(comboBox, 0);
		}else if(response == Role.READWRITE){
			thinlet.setText(comboBox, "Read/Write");
			thinlet.setSelectedIndex(comboBox, 1);
		}else if(response == Role.READ){
			thinlet.setText(comboBox, "Read");
			thinlet.setSelectedIndex(comboBox, 2);
		}
		thinlet.setAttachedObject(mainPanel, this);
	}

	public void selectionChanged(int index){
		if(index >=0){
			System.out.println(hasChanged);
			hasChanged = true;
			System.out.println(hasChanged);
			response = (Role) thinlet.getAttachedObject(thinlet.getItem(comboBox, index));
		}else{
			response = null;
		}
	}
	
	
	public Role getSelectedRole(){
		return response;
	}

	@Override
	public boolean isValid() {
		return hasResponse();
	}
	
	public boolean hasChanged(){		
		return hasChanged;
	}

	@Override
	public void setResponse(Role s) {
		response= s;
		thinlet.setText(comboBox, Role.getRoleName(s));
		thinlet.setSelectedIndex(comboBox, s == Role.ADMIN? 0: s == Role.READWRITE? 1:2);
	}
}
