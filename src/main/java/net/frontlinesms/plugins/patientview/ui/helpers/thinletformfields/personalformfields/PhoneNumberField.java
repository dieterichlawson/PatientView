package net.frontlinesms.plugins.patientview.ui.helpers.thinletformfields.personalformfields;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.frontlinesms.data.DuplicateKeyException;
import net.frontlinesms.plugins.patientview.data.domain.people.CommunityHealthWorker;
import net.frontlinesms.plugins.patientview.data.domain.people.Person;
import net.frontlinesms.plugins.patientview.ui.helpers.thinletformfields.TextBox;
import net.frontlinesms.ui.ExtendedThinlet;
import net.frontlinesms.ui.i18n.InternationalisationUtils;

public class PhoneNumberField extends TextBox implements PersonalFormField{

	
	public static final String NAME = "phoneNumberField";
	private static final String PHONE_NUMBER_FIELD = "medic.common.labels.phone.number";
	private boolean hasChanged;
	
	public PhoneNumberField(ExtendedThinlet thinlet, String phoneNumber){
		super(thinlet, InternationalisationUtils.getI18NString(PHONE_NUMBER_FIELD)+":", NAME);
		thinlet.setText(textBox, phoneNumber);
		thinlet.setAction(textBox, "textChanged(this.text)", null, this);
		thinlet.setAttachedObject(mainPanel, this);
	}
	
	public void textChanged(String text){
		hasChanged = true;
	}
	
	public boolean hasChanged(){
		return hasChanged;
	}
	
	protected PhoneNumberField(ExtendedThinlet thinlet, String label, String name){
		super(thinlet, label, name);
	}

	
	public boolean isValid() {
		//if there are any letters in the input, it will find them and fail validation
		//TODO: right now, we don't know enough about the possible phone number
		//formats to really validate this. Should fix this.
		Pattern pattern = Pattern.compile("[a-z]+",Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(getResponse());
		if(matcher.matches()){
			return false;
		}else{
			return true;
		}
	}

	/**
	 * sets the phone number of the person that is passed in, as long as it is a CHW
	 * and the number is formatter correctly
	 * @see net.frontlinesms.plugins.patientview.ui.helpers.thinletformfields.personalformfields.PersonalFormField#setFieldForPerson(net.frontlinesms.plugins.patientview.data.domain.people.Person)
	 */
	public void setFieldForPerson(Person p) {
		CommunityHealthWorker chw;
		try{
			chw = (CommunityHealthWorker) p;
		}catch(Throwable t){return;}
		chw.setPhoneNumber(getRawResponse());
	}
}
