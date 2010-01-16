package net.frontlinesms.plugins.medic.ui.helpers.thinletformfields;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import net.frontlinesms.plugins.medic.ui.dialogs.DateSelectorDialog;
import net.frontlinesms.ui.ExtendedThinlet;

public class DateField extends TextBox {

	protected DateSelectorDialog ds;
	protected SimpleDateFormat df;
	public static final String NAME = "dateField";

	public DateField(ExtendedThinlet thinlet, String label) {
		super(thinlet, label, NAME);
		Object btn = thinlet.create("button");
		thinlet.setIcon(btn, "/icons/date.png");
		thinlet.setAction(btn, "showDateSelector()", null, this);
		thinlet.add(mainPanel, btn);
		thinlet.setInteger(mainPanel, "columns", 3);
		ds = new DateSelectorDialog(thinlet, textBox);
		df =  new SimpleDateFormat();
		df.applyLocalizedPattern("dd/MM/yyyy");
		thinlet.setAttachedObject(mainPanel, this);
	}

	protected DateField(ExtendedThinlet thinlet, String label, String name) {
		super(thinlet, label, name);
		Object btn = thinlet.create("button");
		thinlet.setIcon(btn, "/icons/date.png");
		thinlet.setAction(btn, "showDateSelector()", null, this);
		thinlet.add(mainPanel, btn);
		thinlet.setInteger(mainPanel, "columns", 3);
		df =  new SimpleDateFormat();
		df.applyLocalizedPattern("dd/MM/yyyy");
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

	public boolean isValid() {
		try {
			DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT);
			df.setLenient(false);
			Date date = df.parse(this.getResponse());
		} catch (Exception e) {
			return false;
		}
		return true;
	}

}
