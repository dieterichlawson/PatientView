package net.frontlinesms.plugins.patientview.ui.helpers.thinletformfields;

import static net.frontlinesms.ui.i18n.InternationalisationUtils.getDateFormat;
import static net.frontlinesms.ui.i18n.InternationalisationUtils.getI18NString;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

import net.frontlinesms.FrontlineSMSConstants;
import net.frontlinesms.plugins.patientview.ui.dialogs.DateSelectorDialog;
import net.frontlinesms.ui.ExtendedThinlet;

public class DateField extends TextBox {

	protected DateSelectorDialog ds;
	protected DateFormat df = getDateFormat();
	Object btn;
	public static final String NAME = "dateField"; 

	public DateField(ExtendedThinlet thinlet, String label) {
		super(thinlet, label + " ("+getI18NString(FrontlineSMSConstants.DATEFORMAT_YMD)+")", NAME);
		btn = thinlet.create("button");
		thinlet.setIcon(btn, "/icons/date.png");
		thinlet.setAction(btn, "showDateSelector()", null, this);
		thinlet.add(mainPanel, btn);
		thinlet.setInteger(mainPanel, "columns", 3);
		ds = new DateSelectorDialog(thinlet, textBox);
		thinlet.setAttachedObject(mainPanel, this);
		
	}

	protected DateField(ExtendedThinlet thinlet, String label, String name) {
		super(thinlet, label + " ("+getI18NString(FrontlineSMSConstants.DATEFORMAT_YMD)+")", name);
		btn = thinlet.create("button");
		thinlet.setIcon(btn, "/icons/date.png");
		thinlet.setAction(btn, "showDateSelector()", null, this);
		thinlet.add(mainPanel, btn);
		thinlet.setInteger(mainPanel, "columns", 3);
		ds = new DateSelectorDialog(thinlet, textBox);
	}

	public Date getDateResponse() {
		try {
			return df.parse(getResponse());
		} catch (ParseException e) {
			return null;
		}
	}

	public void showDateSelector() {
		try {
			ds.showSelecter();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	public void setRawResponse(Date d){
		setResponse(df.format(d));
	}
	
	public void setDateButtonEnabled(boolean value){
		thinlet.setEnabled(btn, value);
	}

	public boolean isValid() {
		try {
			Date date = df.parse(this.getResponse());
		} catch (Exception e) {
			return false;
		}
		return true;
	}

}
