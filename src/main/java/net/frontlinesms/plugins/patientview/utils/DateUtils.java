package net.frontlinesms.plugins.patientview.utils;

import java.text.DateFormat;

public class DateUtils {

	public static DateFormat getDateFormatter(){
		return DateFormat.getDateInstance(DateFormat.SHORT);
	}
}
