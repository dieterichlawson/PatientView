package net.frontlinesms.plugins.patientview.utils;

import java.util.Date;

public class TimeUtils {
	
	
	public static int getYearsBetweenDates(long firstDate, long secondDate){
		long millis = Math.abs(firstDate - secondDate);
		long years =  millis / 31558464000L;
		return safeLongToInt(years);
	}
	
	public static int getYearsBetweenDates(Date firstDate, Date secondDate){
		return getYearsBetweenDates(firstDate.getTime(),secondDate.getTime());
	}
	
	public static int safeLongToInt(long l) {
	    if (l < Integer.MIN_VALUE || l > Integer.MAX_VALUE) {
	        throw new IllegalArgumentException(l + " cannot be cast to int without changing its value.");
	    }
	    return (int) l;
	}
	
	public static int getAge(Date birthdate){
		return getAge(birthdate.getTime());
	}
	
	public static int getAge(long birthdate){
		return getYearsBetweenDates(birthdate, new Date().getTime());
	}
}
