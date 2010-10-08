package net.frontlinesms.plugins.patientview.ui.thinletformfields;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import net.frontlinesms.ui.ExtendedThinlet;

import org.hibernate.classic.ValidationFailure;

public class TimeField extends TextBox<Date> {
	
	private static final String SHORT_TIME_FORMAT= "hh:mm";
	private static final String LONG_TIME_FORMAT= "hh:mm:ss";
	public TimeField(ExtendedThinlet thinlet, String label, FormFieldDelegate delegate ){
		super(thinlet, label,delegate);
	}
	
	@Override
	public void validate() throws ValidationFailure{
		if(getRawResponse() == null){
			throw new ValidationFailure("\""+ getLabel().replace(":", "")+ "\" is formatted incorrectly. Please enter the time in an HH:MM format.");
		}
	}

	@Override
	public Date getRawResponse() {
		Date result = null;
		try {
			DateFormat f = new SimpleDateFormat(SHORT_TIME_FORMAT);
			f.setLenient(true);
			result = f.parse(getStringResponse());
			return result;
		} catch (ParseException e) {
			try{
				DateFormat f = new SimpleDateFormat(LONG_TIME_FORMAT);
				f.setLenient(true);
				result = f.parse(getStringResponse());
				return result;
			}catch(ParseException f){
				return null;
			}
		}
	}
	
	@Override
	public void setRawResponse(Date response) {
		setStringResponse(DateFormat.getTimeInstance(DateFormat.SHORT).format(response));
	}

}
