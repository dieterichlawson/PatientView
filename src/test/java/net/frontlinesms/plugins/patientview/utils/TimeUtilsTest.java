package net.frontlinesms.plugins.patientview.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

import net.frontlinesms.junit.BaseTestCase;

public class TimeUtilsTest extends BaseTestCase {

	/**
	 * Ensures that an exception is thrown when a long that is too large is
	 * passed to the safeLongToInt method
	 */
	public void test_safeLongToInt_longTooLarge_exceptionThrown() {
		boolean exceptionGenerated = false;
		try {
			TimeUtils.safeLongToInt(Integer.MAX_VALUE + 1L);
		} catch (IllegalArgumentException t) {
			exceptionGenerated = true;
		}
		if (!exceptionGenerated)
			fail();
	}

	/**
	 * Ensures that an exception is thrown when a long that is too small is
	 * passed to the safeLongToInt method
	 */
	public void test_safeLongToInt_longTooSmall_exceptionThrown() {
		boolean exceptionGenerated = false;
		try {
			TimeUtils.safeLongToInt(Integer.MIN_VALUE - 1L);
		} catch (IllegalArgumentException t) {
			exceptionGenerated = true;
		}
		if (!exceptionGenerated)
			fail();
	}

	/**
	 * Ensures that no exception is thrown when a long that is exactly at the
	 * positive limit is passed to the safeLongToInt method
	 */
	public void test_safeLongToInt_longAtPosLimit_noExceptionThrown() {
		try {
			TimeUtils.safeLongToInt(Integer.MAX_VALUE);
		} catch (IllegalArgumentException t) {
			fail();
		}
	}

	/**
	 * Ensures that no exception is thrown when a long that is exactly at the
	 * negative limit is passed to the safeLongToInt method
	 */
	public void test_safeLongToInt_longAtNegLimit_noExceptionThrown() {
		try {
			TimeUtils.safeLongToInt(Integer.MIN_VALUE);
		} catch (IllegalArgumentException t) {
			fail();
		}
	}

	/**
	 * Tests that 0 is returned when getYearsBetweenDates is passed the same date
	 */
	public void test_getYearsBetweenDates_sameDates_zeroReturned() {
		assertEquals(0, TimeUtils.getYearsBetweenDates(new Date().getTime(),
				new Date().getTime()));
	}

	/**
	 * 
	 */
	public void test_getYearsBetweenDates_dates3YearsApart_threeReturned() {
		DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT);
		try {
			assertEquals(3, TimeUtils.getYearsBetweenDates(df.parse("01/01/2010"), df.parse("01/03/2013")));
		} catch (ParseException e) {
			e.printStackTrace();
			fail();
		}
	}

	public void test_getYearsBetweenDates_datesExactly3YearsApart_threeReturned() {
		DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT);
		try {
			assertEquals(3, TimeUtils.getYearsBetweenDates(df.parse("01/01/2010"), df.parse("01/01/2013")));
		} catch (ParseException e) {
			e.printStackTrace();
			fail();
		}
	}

}
