package com.cummins.mowo.model;

import java.util.ArrayList;
import java.util.List;

import android.text.format.Time;
import android.util.Log;

import com.cummins.mowo.R;
import com.cummins.mowo.activity.MainActivity;
import com.cummins.mowo.daos.TimecardDao;
import com.cummins.mowo.functions.fDATE;
import com.cummins.mowo.utils.MowoListEntries;
import com.cummins.mowo.vos.PunchData;
import com.cummins.mowo.vos.SimpleObservable;
import com.cummins.mowo.vos.TimecardEntry;
import com.cummins.mowo.widgets.ClockControlDialog;

public class TimecardModel extends SimpleObservable {

	private static final String TAG = TimecardModel.class.getSimpleName();
	
	public static final int TIMESHEET_STATUS_NOTSTARTED = 1;
	public static final int TIMESHEET_STATUS_STARTED = 2;
	public static final int TIMESHEET_STATUS_ENDED = 3;
	public static final String TIME_FORMAT_HH_MM_AAA = "hh:mm aaa";
	public static final String TIME_FORMAT_DD_MMM_YYYY = "dd.MMM.yyyy";
	private List<TimecardEntry> timecardEntryList;
	private Time clockInTime;
	private Time clockOutTime;	
	private PunchData punchInData;
	private PunchData punchOutData;
	private int timecardStatus;
	private String timezoneString;
	private boolean locked = false;
	private TimecardEntry activeEntry;
	private TimecardEntry continueEntry;
	private int id;
	
	private MowoListEntries mowoListEntries;
	
	public TimecardModel () {
		this.id = -1;
		this.timecardEntryList = new ArrayList<TimecardEntry>();
		this.timecardStatus = TIMESHEET_STATUS_NOTSTARTED;
		this.clockInTime = new Time();
		this.clockOutTime = new Time();
		this.mowoListEntries = new MowoListEntries();
		this.punchInData = new PunchData();
		this.punchOutData = new PunchData();
		this.activeEntry = null;
	}
	
	@Override
	synchronized public TimecardModel clone() {
		Log.i(MainActivity.DEBUGTAG, "class.TimecardModel entering method.TimecardModel clone");	
		TimecardModel tm = new TimecardModel();
		tm.setLocked(locked);	
		tm.setTimecardEntryList(timecardEntryList);	
	    tm.setTimecardStatus(timecardStatus);
	    tm.setId(id);
	    tm.setMowoListEntries(mowoListEntries);
	    tm.setPunchInData(punchInData);
	    tm.setPunchOutData(punchOutData);
		Log.d(MainActivity.DEBUGTAG, "TimecardModel clone() ");
        return tm;
	}

	synchronized public void consume(TimecardModel tm) {
	//	this.timecardEntryList = tm.getTimecardEntryList();
		Log.d(TAG, "entering class TimecardModel method.consume() " + tm.getTimecardEntryList().size());
		this.id = tm.getId();
		this.timecardStatus = tm.getTimecardStatus();
		this.clockInTime = tm.getClockInTime();
		this.clockOutTime = tm.getClockOutTime();
		this.punchInData = tm.getPunchInData();
		this.punchOutData = tm.getPunchOutData();
		TimecardEntry lastEntry = null;
		
		// get the last entry 
		if (tm.getTimecardEntryList().size() > 0) {
		   lastEntry = tm.getTimecardEntryList().get(tm.getTimecardEntryList().size() - 1);
		}	
		// for some reason copying list though consume method does not work in adapter refresh 
		// so will manually loop through the list and assign values to the model's list
		while(timecardEntryList.size() > 0) {
			timecardEntryList.remove(0);
		}
		// add entries to the list, if last activity is running set it as activity entry and don't add to list
		for (TimecardEntry entry : tm.getTimecardEntryList()) {
			if (entry == lastEntry && entry.isActive()) {
				this.activeEntry = entry;
			} else {
			    this.timecardEntryList.add(entry);
			}
		}
		Log.d(TAG, "leaving class TimecardModel method.consume() " + this.getTimecardEntryList().size());
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public boolean isClockInTimeSet() {
		Time sot = new Time();
		int result = sot.compare(clockInTime, sot);
		Log.d(TAG, String.valueOf(result));
		if (result < 0 || result > 0) {
			return true;
		}
		return false;
	}
	
	public Time getClockInTime() {
		return clockInTime;
	}
	
	public long getClockInTimeInMillis() {
		return clockInTime.toMillis(false);
	}	

	public void setClockInTime(Time time) {
		this.clockInTime = time;
	}

	public boolean isClockOutTimeSet() {
		Time sot = new Time();
		int result = sot.compare(clockOutTime, sot);
		Log.d(TAG, String.valueOf(result));
		if (result < 0 || result > 0) {
			return true;
		}
		return false;
	}
	
	public Time getClockOutTime() {
		return clockOutTime;
	}

	public void setClockOutTime(Time time) {
		this.clockOutTime = time;
	}

	public String getClockInTimeString() {
	    if (clockInTime.toMillis(false) != new Time().toMillis(false)) {
	    	return fDATE.formatTimeHHMI(clockInTime);
	    } 
	    return GlobalState.getContext().getResources().getString(R.string.clockbtnactionlbl_clockin); 
	}
	
	public boolean isClockedIn() {
	    if (clockInTime.toMillis(false) != new Time().toMillis(false)) {
	    	return true;
	    } 
	    return false; 
	}

	public String getClockOutTimeString() {	
		if (clockOutTime.toMillis(false) != new Time().toMillis(false)) {
	        return fDATE.formatTimeHHMI(clockOutTime);
		} 
	    return GlobalState.getContext().getResources().getString(R.string.clockbtnactionlbl_clockout); 
	}
	
	public String getClockInTimeDate() {
		if (clockInTime.toMillis(false) != new Time().toMillis(false)) {
	        return fDATE.formatTimeDMY(clockInTime);
		}
		return "";
	}
	

	public String getClockOutTimeDate() {		
		if (clockOutTime.toMillis(false) != new Time().toMillis(false)) {
	       return fDATE.formatTimeDMY(clockOutTime);
		}
		return "";
	}	

	public String getTimezoneString() {
		return timezoneString;
	}

	public void setTimezoneString(String timezoneString) {
		this.timezoneString = timezoneString;
	}
	
	public void setTimeInNow() {
		
	    clockInTime = fDATE.getTimeNow();
	}
	
	public void setTimeOutNow() {
		clockOutTime = fDATE.getTimeNow();
		//notifyObservers(this);
		
	}	
	
	public List<TimecardEntry> getTimecardEntryList() {
		return this.timecardEntryList;
	}
	
	public void setTimecardEntryList(List<TimecardEntry> timecardEntryList) {
		this.timecardEntryList = timecardEntryList;
	}
	
	public void addTimecardEntry(TimecardEntry timecardEntry) {
		timecardEntryList.add(timecardEntry);
		    
		//	    if (timecardEntryList.size()>1) {
		//	    	TimecardEntry entryActivityPrev = timecardEntryList.get(timecardEntryList.size()-2);
		//	    	entryActivityPrev.setTimeEnd(timecardEntry.getTimeStart());
		//	    }
    
        //mowoListEntries.add(timecardEntry);
	    //if (mowoListEntries.size()>1) {
	    //	TimecardEntry Prev = (TimecardEntry) mowoListEntries.get(mowoListEntries.size()-2);
	    //	Prev.setTimeEnd(timecardEntry.getTimeStart());
	    //}
	
	}
	
	public void addEmptyEntry() {
		addTimecardEntry(getEmptyTimecardEntry());
	}
	
	public TimecardEntry getEmptyTimecardEntry() {
		TimecardEntry timecardEntry = new TimecardDao().getEmtpyEntry();
		return timecardEntry;
	}
	
	public void stopLastEntry () {
		
		if (this.timecardEntryList.size() > 0) {
			this.timecardEntryList.get(timecardEntryList.size() - 1).setTimeEnd(fDATE.getTimeNow());
		}
	}
	
	/*if any activity is running return true*/
	public boolean isAnyActivityRunning() {
		for (TimecardEntry entry: timecardEntryList) {
			if (!entry.isEndTimeSet()) {
				return true;
			}
		}	
		return false;
	}
	
	public int getTimecardEntryListCount() {
		return this.timecardEntryList.size();
	}

	public void setEntryActivityType(int pos, String param) {
		if (this.timecardEntryList.size() > 0) {
			this.timecardEntryList.get(pos).setActivityTypeString(param);
		}
	}
	
	public int getTimecardStatus() {
		return this.timecardStatus;
	}

	public void setTimecardStatus(int timecardStatus) {
		this.timecardStatus = timecardStatus;
	}

	public String getTitleBarString() {
		switch(timecardStatus) {
		case TimecardModel.TIMESHEET_STATUS_NOTSTARTED:
			return GlobalState.getContext().getResources().getString(R.string.timecard_status_bar_title_notstarted);
		case TimecardModel.TIMESHEET_STATUS_STARTED:
		    return GlobalState.getContext().getResources().getString(R.string.timecard_status_bar_title_started);
		case TimecardModel.TIMESHEET_STATUS_ENDED:
		    return GlobalState.getContext().getResources().getString(R.string.timecard_status_bar_title_ended);
		}
		return "";
	}

	public String getClockInLabel() {
		if (getClockInTimeDate() == null) {
			return "";
		}	
		return getClockInTimeDate();
	}

	public String getClockOutLabel() {
		if (getClockOutTimeDate() == null) {
			return "";
		}	
		return getClockOutTimeDate();
	}	
	
	public boolean isLocked() {
		return locked;
	}
	public void setLocked(boolean locked) {
		this.locked = locked;
	}

	public MowoListEntries getMowoListEntries() {
		return mowoListEntries;
	}

	public void setMowoListEntries(MowoListEntries mowoListEntries) {
		this.mowoListEntries = mowoListEntries;
	}

	public PunchData getPunchInData() {
		return punchInData;
	}

	public void setPunchInData(PunchData punchInData) {
		this.punchInData = punchInData;
	}

	public PunchData getPunchOutData() {
		return punchOutData;
	}

	public void setPunchOutData(PunchData punchOutData) {
		this.punchOutData = punchOutData;
	}
	
	public void getActivitiesTotal() {
		long totalAllocation = 0;
		for (TimecardEntry entry: timecardEntryList) {
			if (entry.isEndTimeSet()) {
			   totalAllocation = totalAllocation + fDATE.getDurationInMillis(entry.getTimeStart(), entry.getTimeEnd());
			}
		}	
		
		Log.d(TAG, "Total elapsed time " + fDATE.formatElapsedTime(totalAllocation));
	}

	public void calculateOverLaps() {
		
		int counter = 0;
		Long gapInMillis = (long) 0;
		Long prevTimeEndEntryLong = (long) 0;

		for (TimecardEntry item: timecardEntryList) {
			
			//Log.d(TAG, " class.TimecardDao after move to text entry id =  " + String.valueOf(cursor.getInt(cursor.getColumnIndex(TCE_ID))));

			Time timeStartEntry = new Time();
			Time timeEndEntry = new Time();

			Long timeStartEntryLong = item.getTimeStartInMillis();
			Long timeEndEntryLong = item.getTimeEndInMillis();
			
			if (counter == 0) {
				prevTimeEndEntryLong = timeEndEntryLong;	
				gapInMillis = timeStartEntryLong - getClockInTimeInMillis();
				Log.d(TAG, "C = " + counter + " st " + timeStartEntryLong + " ed " + timeEndEntryLong + " ped " + prevTimeEndEntryLong + " d " + gapInMillis);
			} else if (counter > 0) {
				gapInMillis = timeStartEntryLong - prevTimeEndEntryLong;
				Log.d(TAG, "C = " + counter + " st " + timeStartEntryLong + " ed " + timeEndEntryLong + " ped " + prevTimeEndEntryLong + " d " + gapInMillis);
				prevTimeEndEntryLong = timeEndEntryLong;			
			}
			
			item.setGapInMillis(gapInMillis);
			counter++;
			
		}	
	}

	public TimecardEntry getActiveEntry() {
		return activeEntry;
	}

	public void setActiveEntry(TimecardEntry activeEntry) {
		this.activeEntry = activeEntry;
	}
	
	public void stopActiveEntry () {
		
		if (this.activeEntry != null) {
			this.activeEntry.setTimeEnd(fDATE.getTimeNow());
			TimecardEntry newEntry = activeEntry;
			this.timecardEntryList.add(newEntry);
			activeEntry = null;
		}
	}

	public TimecardEntry getContinueEntry() {
		return continueEntry;
	}

	public void setContinueEntry(TimecardEntry continueEntry) {
		this.continueEntry = continueEntry;
	}
	
	
	
}
