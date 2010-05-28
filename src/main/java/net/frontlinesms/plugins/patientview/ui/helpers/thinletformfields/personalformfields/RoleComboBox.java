package net.frontlinesms.plugins.patientview.ui.helpers.thinletformfields.personalformfields;

import net.frontlinesms.plugins.patientview.data.domain.people.Person;
import net.frontlinesms.plugins.patientview.data.domain.people.User;
import net.frontlinesms.plugins.patientview.data.domain.people.User.Role;
import net.frontlinesms.plugins.patientview.ui.helpers.thinletformfields.FormFieldDelegate;
import net.frontlinesms.plugins.patientview.ui.helpers.thinletformfields.ThinletFormField;
import net.frontlinesms.ui.ExtendedThinlet;
import net.frontlinesms.ui.i18n.InternationalisationUtils;

public class RoleComboBox extends ThinletFormField<Role> implements PersonalFormField {

	protected Object comboBox;
	protected boolean hasChanged = false;
	
	public RoleComboBox(ExtendedThinlet thinlet, Role role, FormFieldDelegate delegate) {
		super(thinlet, InternationalisationUtils.getI18NString("medic.common.labels.role") + ":",delegate);
		comboBox = thinlet.create("combobox");
		for (Role r : Role.values()) {
			thinlet.add(comboBox, thinlet.createComboboxChoice(r.toString(), r));
		}
		thinlet.setAction(comboBox, "selectionChanged(this.selected)", null, this);
		thinlet.add(mainPanel, comboBox);
		thinlet.setWeight(comboBox, 5, 0);
		setRawResponse(role);
		thinlet.setAttachedObject(mainPanel, this);
	}

	public void selectionChanged(int index) {
		if (index >= 0) {
			hasChanged = true;
			super.responseChanged();
		}
	}

	@Override
	public boolean isValid() {
		return hasResponse();
	}

	public boolean hasChanged() {
		return hasChanged;
	}

	@Override
	public Role getRawResponse() {
		return (Role) thinlet.getAttachedObject(thinlet.getSelectedItem(comboBox));
	}

	@Override
	public void setRawResponse(Role s) {
		if (s == null) { // Default to read only
			s = Role.READ;
		}
		thinlet.setText(comboBox, s.toString());
		int i = 0;
		for (Role r : Role.values()) {
			if (r.name().equals(s.name())) {
				thinlet.setSelectedIndex(comboBox, i);
			}
			i++;
		}
	}

	@Override
	public String getStringResponse() {
		return getRawResponse().toString();
	}

	@Override
	public void setStringResponse(String response) {
		if (Role.getRoleForName(response) != null){
			setRawResponse(Role.getRoleForName(response));
		}
	}

	/**
	 * sets the role of the person passed in, if it is a User
	 * 
	 * @see net.frontlinesms.plugins.patientview.ui.helpers.thinletformfields.personalformfields.PersonalFormField#setFieldForPerson(net.frontlinesms.plugins.patientview.data.domain.people.Person)
	 */
	public void setFieldForPerson(Person p) {
		User user;
		try {
			user = (User) p;
		} catch (Throwable t) {
			return;
		}
		user.setRole(getRawResponse());
	}
}
