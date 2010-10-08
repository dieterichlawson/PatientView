package net.frontlinesms.plugins.patientview.ui.thinletformfields.personalformfields;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.frontlinesms.data.repository.ContactDao;
import net.frontlinesms.plugins.patientview.data.domain.people.CommunityHealthWorker;
import net.frontlinesms.plugins.patientview.data.domain.people.Person;
import net.frontlinesms.plugins.patientview.ui.thinletformfields.FormFieldDelegate;
import net.frontlinesms.plugins.patientview.ui.thinletformfields.TextField;
import net.frontlinesms.ui.ExtendedThinlet;
import net.frontlinesms.ui.i18n.InternationalisationUtils;

import org.springframework.context.ApplicationContext;

public class PhoneNumberField extends TextField implements PersonalFormField {

	private static final String PHONE_NUMBER_FIELD = "medic.common.labels.phone.number";
	private ContactDao contactDao;

	public PhoneNumberField(ExtendedThinlet thinlet, String phoneNumber, FormFieldDelegate delegate, ApplicationContext appCon) {
		super(thinlet, InternationalisationUtils.getI18NString(PHONE_NUMBER_FIELD) + ":", delegate);
		thinlet.setText(textBox, phoneNumber);
		this.contactDao = (ContactDao) appCon.getBean("contactDao");
	}

	public boolean isValid() {
		// if there are any letters in the input, it will find them and fail validation
		// TODO: right now, we don't know enough about the possible phone number
		// formats to really validate this. Should fix this.
		Pattern pattern = Pattern.compile("[^0-9+]", Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(getStringResponse());
		if (matcher.matches()) return false;
		// if there is another CHW with this number, it is not valid
		return contactDao.getFromMsisdn(getStringResponse()) == null;
	}

	/**
	 * Sets the phone number of the person that is passed in, as long as it is a
	 * CHW and the number is formatted correctly
	 * 
	 * @see net.frontlinesms.plugins.patientview.ui.thinletformfields.personalformfields.PersonalFormField#setFieldForPerson(net.frontlinesms.plugins.patientview.data.domain.people.Person)
	 */
	public void setFieldForPerson(Person p) {
		CommunityHealthWorker chw;
		try {
			chw = (CommunityHealthWorker) p;
		} catch (Throwable t) {
			return;
		}
		try {
			chw.setPhoneNumber(getRawResponse());
		} catch (Exception e) {
			// these exceptions aren't actually thrown
		}
	}
}
