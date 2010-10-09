package net.frontlinesms.plugins.patientview.utils;

import java.util.Date;

/**
 * A collection of utility methods for dealing with time,
 * specifically ages and birthdates.
 * @author dieterichlawson
 *
 */
public class TimeUtils {
	
	
	/**
	 * Returns the number of years between dates, taking longs
	 * as dates. The order of the dates does not matter. 
	 * @param firstDate
	 * @param secondDate
	 * @return
	 */
	public static int getYearsBetweenDates(long firstDate, long secondDate){
		long millis = Math.abs(firstDate - secondDate);
		long years =  millis / 31558464000L;
		return safeLongToInt(years);
	}
	
	/**
	 * Returns the number of years between dates, taking Date objects
	 * as parameters. The order of the dates doesn't matter.
	 * 
	 * @param firstDate
	 * @param secondDate
	 * @return
	 */
	public static int getYearsBetweenDates(Date firstDate, Date secondDate){
		return getYearsBetweenDates(firstDate.getTime(),secondDate.getTime());
	}
	
	/**
	 * Performs a safe cast from a long to an int.
	 * If the long is out of the int range, an exception is thrown 
	 * @param l
	 * @return
	 */
	public static int safeLongToInt(long l) {
	    if (l < Integer.MIN_VALUE || l > Integer.MAX_VALUE) {
	        throw new IllegalArgumentException(l + " cannot be cast to int without changing its value.");
	    }
	    return (int) l;
	}
	
	/**
	 * Gets the number in years between the supplied date and the current date.
	 * @param birthdate
	 * @return
	 */
	public static int getAge(Date birthdate){
		return getAge(birthdate.getTime());
	}
	
	/**
	 * @param birthdate
	 * @return
	 */
	public static int getAge(long birthdate){
		return getYearsBetweenDates(birthdate, new Date().getTime());
	}
}
