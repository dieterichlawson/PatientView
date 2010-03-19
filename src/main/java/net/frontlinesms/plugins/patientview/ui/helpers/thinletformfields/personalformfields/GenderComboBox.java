package net.frontlinesms.plugins.patientview.ui.helpers.thinletformfields.personalformfields;

import net.frontlinesms.plugins.patientview.data.domain.people.Person;
import net.frontlinesms.plugins.patientview.data.domain.people.Person.Gender;
import net.frontlinesms.plugins.patientview.ui.helpers.thinletformfields.ThinletFormField;
import net.frontlinesms.ui.ExtendedThinlet;
import net.frontlinesms.ui.i18n.InternationalisationUtils;

public class GenderComboBox extends ThinletFormField<Gender> implements PersonalFormField{

	protected Object comboBox;
	protected boolean hasChanged;
	public static final String NAME = "genderComboBox";
	
	public GenderComboBox(ExtendedThinlet thinlet, Gender gender) {
		super(thinlet, InternationalisationUtils.getI18NString("medic.common.labels.gender")+":", NAME);
		hasChanged = false;
		comboBox = thinlet.create("combobox");
		for(Gender g: Gender.values()){
			thinlet.add(comboBox,thinlet.createComboboxChoice(g.toString(), g));
		}
		thinlet.setAction(comboBox, "selectionChanged(this.selected)", null, this);
		thinlet.add(mainPanel,comboBox);
		thinlet.setInteger(comboBox, "weightx", 5);
		//initialize the comboBox
		if(gender !=null){
			setRawResponse(gender);
		}
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
	public Gender getRawResponse() {
		return (Gender) thinlet.getAttachedObject(thinlet.getSelectedItem(comboBox));
	}
	
	@Override
	public void setRawResponse(Gender s) {
		thinlet.setText(comboBox, s.toString());
		for(int i =0; i < Gender.values().length;i++){
			if(Gender.values()[i] == s){
				thinlet.setSelectedIndex(comboBox,i);
			}
		}
	}
	
	@Override
	public String getResponse() {
		if(getRawResponse()!=null){
			return getRawResponse().toString();
		}else{
			return null;
		}
	}

	@Override
	public void setResponse(String response) {
		if(Gender.getGenderForName(response) != null)
			setRawResponse(Gender.getGenderForName(response));
	}

	public void setFieldForPerson(Person p) {
		p.setGender(getRawResponse());
	}
}
