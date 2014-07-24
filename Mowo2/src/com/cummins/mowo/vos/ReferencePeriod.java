package com.cummins.mowo.vos;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.util.Log;

import com.cummins.mowo.activity.timecard.TimecardHeadlinesFragment;
import com.cummins.mowo.functions.fDATE;

public class ReferencePeriod {
	
	private static final String TAG = ReferencePeriod.class.getSimpleName();
	
	private static Date refStartDate; 
	private static Date refEndDate;
	private static int  periodDuration;
	private static Calendar c;
	private final static int DAYS_IN_WEEK = 7;

	public static Date getStartDate() {
		return refStartDate;
	}

	public static Date getEndDate() {
		return refEndDate;
	}

	public static void setEndDate(Date endDate) {
		refEndDate = endDate;
	}
	
	public static long getStartDateInMillis() {
		c.setTime(refStartDate);
		return c.getTimeInMillis();
	}

	public static long getEndDateInMillis() {
		c.setTime(refEndDate);
		return c.getTimeInMillis();
	}
	
	public static void setDates(int period) {
		
		int offset = period * 7;
		
		periodDuration = DAYS_IN_WEEK; 
		
		// Get calendar set to current date and time
		c = Calendar.getInstance();
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
	    c.setFirstDayOfWeek(Calendar.MONDAY);
	    
		Log.d(TAG, "Today's date " + fDATE.formatDate(c.getTime()));
		Log.d(TAG, "Week of Year " + c.get(Calendar.WEEK_OF_YEAR));

		// Set the calendar to monday of the current week
		c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		c.add(Calendar.DATE, offset);
		refStartDate = c.getTime();		
		
		Log.d(TAG, "Today's date " + fDATE.formatDate(refStartDate));
		c.add(Calendar.DATE, periodDuration);
		refEndDate = c.getTime();
		
		Log.d(TAG, "Today's date " + fDATE.formatDate(refEndDate));
	
	}

	public static int getPeriodDuration() {
		return periodDuration;
	}	
	
}
