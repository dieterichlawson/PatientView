package net.frontlinesms.plugins.medic.ui.helpers.thinletformfields;

import net.frontlinesms.ui.ExtendedThinlet;

public class GenderComboBox extends ThinletFormField<Character>{

	protected Object comboBox;
	protected boolean hasChanged;
	public static final String NAME = "genderComboBox";
	
	public GenderComboBox(ExtendedThinlet thinlet, Character gender) {
		super(thinlet, "Gender:",NAME);
		hasChanged = false;
		comboBox = thinlet.create("combobox");
		thinlet.add(comboBox,thinlet.createComboboxChoice("Male", 'm'));
		thinlet.add(comboBox,thinlet.createComboboxChoice("Female", 'f'));
		thinlet.add(comboBox,thinlet.createComboboxChoice("Trans-gender", 't'));
		thinlet.setAction(comboBox, "selectionChanged(this.selected)", null, this);
		thinlet.add(mainPanel,comboBox);
		response = gender;
		thinlet.setInteger(comboBox, "weightx", 5);
		//initialize the textbox
		if(response !=null){
			if(response == 'm'){
				thinlet.setText(comboBox, "Male");
				thinlet.setSelectedIndex(comboBox, 0);
			}else if(response == 'f'){
				thinlet.setText(comboBox, "Female");
				thinlet.setSelectedIndex(comboBox, 1);
			}else if(response == 't'){
				thinlet.setText(comboBox, "Trans-gender");
				thinlet.setSelectedIndex(comboBox, 2);
			}
		}
		thinlet.setAttachedObject(mainPanel, this);
	}

	public void selectionChanged(int index){
		System.out.println("Selection changed, bitches " + index);
		if(index >=0){
			hasChanged = true;
			response = (Character) thinlet.getAttachedObject(thinlet.getItem(comboBox, index));
		}else{
			response = null;
		}
	}
	
	public String getStringResponse() {
		return response + "";
	}

	@Override
	public boolean isValid() {
		return hasResponse();
	}
	
	public boolean hasChanged(){		
		return hasChanged;
	}

}
