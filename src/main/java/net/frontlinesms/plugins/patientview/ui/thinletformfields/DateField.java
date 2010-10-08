package net.frontlinesms.plugins.patientview.ui.thinletformfields;

import static net.frontlinesms.ui.i18n.InternationalisationUtils.getDateFormat;
import static net.frontlinesms.ui.i18n.InternationalisationUtils.getI18NString;

import java.text.DateFormat;
import java.util.Date;

import org.hibernate.classic.ValidationFailure;

import net.frontlinesms.FrontlineSMSConstants;
import net.frontlinesms.plugins.patientview.ui.components.DateSelectorDialog;
import net.frontlinesms.ui.ExtendedThinlet;

public class DateField extends TextBox<Date> {

	protected DateSelectorDialog ds;
	protected DateFormat df = getDateFormat();
	Object btn;

	public DateField(ExtendedThinlet thinlet, String label, FormFieldDelegate delegate) {
		super(thinlet, label + " ("+getI18NString(FrontlineSMSConstants.DATEFORMAT_YMD)+")", delegate);
		btn = thinlet.createButton("");
		thinlet.setIcon(btn, "/icons/date.png");
		thinlet.setAction(btn, "showDateSelector()", null, this);
		thinlet.add(mainPanel, btn);
		thinlet.setColumns(mainPanel, 3);
		ds = new DateSelectorDialog(thinlet, textBox);
	}

	public void showDateSelector() {
		try {
			ds.showSelecter();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	public void setRawResponse(Date d){
		setStringResponse(df.format(d));
	}
	
	public Date getRawResponse(){
		try{
			return df.parse(getStringResponse());
		}catch(Exception e){
			return null;
		}
	}
	
	public void setDateButtonEnabled(boolean value){
		thinlet.setEnabled(btn, value);
	}

	@Override
	public void validate() throws ValidationFailure{
		try {
			Date date = df.parse(this.getStringResponse());
		} catch (Exception e) {
			throw new ValidationFailure("\""+ getLabel().replace(":", "")+ "\" is formatted incorrectly");
		}
	}

}
