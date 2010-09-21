package net.frontlinesms.plugins.patientview.security;

/**
 * A helper object that keeps track of a specific setting, as well as its
 * minimum and maximum value. The value of the setting can never be set
 * outside of the range.
 */
public class Range {

	private final int min;
	private final int max;
	private int value;

	public Range(int min, int max, int value) {
		assert value >= min;
		assert value <= max;
		this.min = min;
		this.max = max;
		this.value = value;
	}

	/** @return the maximum value of this range */
	public int max() {
		return max;
	}

	/** @return the minimum value of this range */
	public int min() {
		return min;
	}

	/**
	 * Sets the value of this range. If the parameter is beneath the
	 * minimum, the value will be set to the minimum. Similarly, if the
	 * value is above the maximum, the value will be set to the maximim.
	 * 
	 * @param value
	 *            the new value
	 * @return false if the new value is outside the range
	 */
	public boolean setValue(int value) {
		if (value < min) {
			this.value = min;
			return false;
		}
		if (value > max) {
			this.value = max;
			return false;
		}
		this.value = value;
		return true;
	}

	public String textValue() {
		return "" + value;
	}

	public int value() {
		return value;
	}
}