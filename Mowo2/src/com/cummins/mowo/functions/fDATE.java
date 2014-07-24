package com.cummins.mowo.functions;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import android.text.format.DateUtils;
import android.text.format.Time;

public class fDATE {

	public static Time getTimeNow() {

		Time time = new Time();
		time.setToNow();

		return time;
	}

	public static String formatTimeHHMI (Time param) {
		if (param != null)
		return param.format("%H:%M"); // add %P if am / pm is required 
		
		return null;
		
	}
	
	public static String formatTimeHHMISS (Time param) {
		if (param != null)
		return param.format("%H:%M:%S %P");
		
		return null;
		
	}
	
	public static String formatTimeDMY (Time param) {
		if (param != null)
		return param.format("%d.%m.%Y");
		
		return null;
		
	}	
	
	public static long timeToMidnight(long time) {
		Calendar cal = Calendar.getInstance();
		Date d = new Date(time);
		cal.setTime(d);
		cal.set(Calendar.HOUR_OF_DAY, 0);            // set hour to midnight
		cal.set(Calendar.MINUTE, 0);                 // set minute in hour
		cal.set(Calendar.SECOND, 0);                 // set second in minute
		cal.set(Calendar.MILLISECOND, 0);            // set millis in second
		long zeroedDate = cal.getTimeInMillis();
		return zeroedDate;
	}
	
	public static String getDifferenceHoursMinutes(Time start, Time end){
		Long duration = (end.toMillis(true) - start.toMillis(true));
		
        if(duration>=0)
            //return formatInterval(duration);
             return formatElapsedTime(duration);
        else 
            return "error";
    }
	
	public static String formatElapsedTime(Long param) {
		return DateUtils.formatElapsedTime(param/1000/60);
	}
	
	
    public static String formatInterval(final long l)
    {
        final long hr = TimeUnit.MILLISECONDS.toHours(l);
        final long min = TimeUnit.MILLISECONDS.toMinutes(l - TimeUnit.HOURS.toMillis(hr));
        final long sec = TimeUnit.MILLISECONDS.toSeconds(l - TimeUnit.HOURS.toMillis(hr) - TimeUnit.MINUTES.toMillis(min));
        final long ms = TimeUnit.MILLISECONDS.toMillis(l - TimeUnit.HOURS.toMillis(hr) - TimeUnit.MINUTES.toMillis(min) - TimeUnit.SECONDS.toMillis(sec));
        return String.format("%02dh %02dm", hr, min);
    }

    public static String formatIntervalHMS(final long l)
    {
        final long hr = TimeUnit.MILLISECONDS.toHours(l);
        final long min = TimeUnit.MILLISECONDS.toMinutes(l - TimeUnit.HOURS.toMillis(hr));
        final long sec = TimeUnit.MILLISECONDS.toSeconds(l - TimeUnit.HOURS.toMillis(hr) - TimeUnit.MINUTES.toMillis(min));
        final long ms = TimeUnit.MILLISECONDS.toMillis(l - TimeUnit.HOURS.toMillis(hr) - TimeUnit.MINUTES.toMillis(min) - TimeUnit.SECONDS.toMillis(sec));
        return String.format("%02d:%02d:%02d", hr, min, sec);
    }
    
	public static Long  getDurationInMillis(Time start, Time end){
		Long duration = (end.toMillis(true) - start.toMillis(true));
		
        return duration;
    }
	
	public static String formatDate(Date date) {
		DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		return df.format(date);
	}
	
	public static String formatDateHHMM(Date date) {
		DateFormat df = new SimpleDateFormat("HH:mm a");
		return df.format(date).toLowerCase();
	}	
	
	public static String formatDateCustom(Date date, String mask) {
		DateFormat df = new SimpleDateFormat(mask);
		return df.format(date);
	}	
	
	public static boolean isSameDate(long date1, long date2) {
		
		if (timeToMidnight(date1) == timeToMidnight(date2)) { 
		  return true;
		}
		
		return false;
	}
	
	public static Time removeSeconds(Time time) {
		Time newTime = time;
		
		newTime.set(0, time.minute, time.hour, time.monthDay, time.month, time.year);
		
		return newTime;
		
	}

}
