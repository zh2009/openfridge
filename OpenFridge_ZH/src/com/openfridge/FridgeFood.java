package com.openfridge;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.NoSuchElementException;
import java.util.Scanner;

import android.os.Bundle;

/**
 * Encapsulates the data associated with an item of food, including description
 * and expiry date.
 * 
 * @author Tom, Jesse
 * 
 */
public class FridgeFood extends DataObject implements Cloneable {

	private static final long serialVersionUID = 336833461080537069L;
	private GregorianCalendar expirationDate;

	public FridgeFood() {
		this("");
	}

	public FridgeFood(String description) {
		this(description, weekLater());
	}

	public FridgeFood(String description, String expirationDate) {
		this(description, defaultDate(expirationDate, weekLater()));
	}

	public FridgeFood(String description, GregorianCalendar expirationDate) {
		this(description, expirationDate, "", "");
	}

	public FridgeFood(String description, String expirationDate,
			String id, String userId) {
		this(description, defaultDate(expirationDate, weekLater()), id, userId);
	}

	public FridgeFood(String description, GregorianCalendar expirationDate,
			String id, String userId) {
		super(description, id, userId);
		this.expirationDate = expirationDate;
	}

	public FridgeFood(String description, GregorianCalendar expirationDate,
			int id, int userId) {
		super(description, id, userId);
		this.expirationDate = expirationDate;
	}

	private static GregorianCalendar weekLater() {
		GregorianCalendar aWeekLater = new GregorianCalendar();
		aWeekLater.roll(Calendar.DAY_OF_YEAR, 7);
		return aWeekLater;
	}

	private static GregorianCalendar defaultDate(String dateString,
			GregorianCalendar defaultDateValue) {
		GregorianCalendar ans = parseXMLDateTime(dateString);
		if (ans == null) {
			return defaultDateValue;
		} else {
			return ans;
		}
	}

	/**
	 * Parse a string containing date and time information from the XML format
	 * to a GregorianCalendar object representation
	 * 
	 * @param xmlDateTime
	 *            an XML string with Date and Time information in the format
	 *            yyyy-mm-ddThh:mm:ssZ.
	 * @return An object representation of the given Date and Time string
	 */
	private static GregorianCalendar parseXMLDateTime(String xmlDateTime) {
		String dateTimeString = xmlDateTime.replace('-', ' ').replace('T', ' ')
				.replace('Z', ' ').replace(':', ' ');
		Scanner s = new Scanner(dateTimeString);

		try {
			int year = s.nextInt();
			int month = s.nextInt();
			int day = s.nextInt();

			// Return if only the date is included
			if (!s.hasNext())
				return new GregorianCalendar(year, month, day);
			int hour = s.nextInt();
			int minute = s.nextInt();
			int second = s.nextInt();

			return new GregorianCalendar(year, month, day, hour, minute, second);
		} catch (IllegalStateException e) {
			return null;
		} catch (NoSuchElementException e) {
			return null;
		}
	}

	public GregorianCalendar getExpirationDate() {
		return (GregorianCalendar) expirationDate.clone();
	}

	public String getExpirationDateString() {
		return expirationDate.get(Calendar.YEAR) + "-"
				+ expirationDate.get(Calendar.MONTH) + "-"
				+ expirationDate.get(Calendar.DAY_OF_MONTH);
	}

	public int getExpirationYear() {
		return expirationDate.get(Calendar.YEAR);
	}

	public int getExpirationMonth() {
		return expirationDate.get(Calendar.MONTH);
	}

	public int getExpirationDay() {
		return expirationDate.get(Calendar.DAY_OF_MONTH);
	}

	/**
	 * Get a short string summary of this food item, including its description
	 * and expiration date.
	 * 
	 * @return A string containing a summary of this food record
	 */
	public String getSummary() {
		return description + "\nExpires: " + expirationDate.get(Calendar.YEAR)
				+ "-" + expirationDate.get(Calendar.MONTH) + "-"
				+ expirationDate.get(Calendar.DAY_OF_MONTH) + "\n";
	}

	public Object clone() {
		try {
			FridgeFood o = (FridgeFood) super.clone();

			o.expirationDate = (GregorianCalendar) expirationDate.clone();

			return o;
		} catch (CloneNotSupportedException e) {
			// Should never happen
			return null;
		}
	}

	public Bundle bundle() {
		Bundle b = new Bundle();
		b.putSerializable("food", this);
		return b;
	}

	public static FridgeFood getFoodFromBundle(Bundle b) {
		return (FridgeFood) b.getSerializable("food");
	}

	public void setExpirationDate(String expirationDate) {
		setExpirationDate(parseXMLDateTime(expirationDate));
	}

	public void setExpirationDate(GregorianCalendar expirationDate) {
		if (expirationDate != null) {
			this.expirationDate = expirationDate;
		}
	}
}
