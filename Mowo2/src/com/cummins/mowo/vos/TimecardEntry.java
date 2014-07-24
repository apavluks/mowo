package com.cummins.mowo.vos;

import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;

import com.cummins.mowo.functions.fDATE;
import com.cummins.mowo.model.TimecardModel;

public class TimecardEntry {

	private static final String TAG = TimecardModel.class.getSimpleName();
	
	public static final int GAP_TOGETHER = 0;
	public static final int GAP_OVERLAP = -1;
	public static final int GAP_APART = 1;
	
	private int id;
	private int timecardId; 	
	private String activityTypeString;
	private ActivityType activityType;
	private String jobNumberString;
	private Job job;
	private String commentString;
	private Time timeStart;
	private Time timeEnd;
	private Long gapInMillis;
	private int gapType;
	private Integer activityTypeCode;

	
	public TimecardEntry(String activityTypeString, String jobNumberString,
			String commentString, Time timeStart, Time timeEnd) {
		super();
		this.activityTypeString = activityTypeString;
		this.jobNumberString = jobNumberString;
		this.commentString = commentString;
		this.timeStart = (timeStart != null)? timeStart : new Time();
		this.timeEnd = (timeEnd != null)? timeEnd : new Time();
		this.id = -1;
		this.gapInMillis = (long) 0;
		this.activityType = null;
		this.activityTypeCode = ActivityTypesButtonsHolder.ACTIVITY_TYPE_UNDEFINED;
		this.job = null;
	}
	
	public TimecardEntry(int id, int timecardId, String activityTypeString, String jobNumberString,
			String commentString, Time timeStart, Time timeEnd) {
		super();
		this.activityTypeString = activityTypeString;
		this.jobNumberString = jobNumberString;
		this.commentString = commentString;
		this.timeStart = (timeStart != null)? timeStart : new Time();
		this.timeEnd = (timeEnd != null)? timeEnd : new Time();
		this.timecardId = timecardId;
		this.id = id;
	}	

	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}

	
	public int getTimecardId() {
		return timecardId;
	}

	public void setTimecardId(int timecardId) {
		this.timecardId = timecardId;
	}

	public String getActivityTypeString() {
		return activityTypeString;
	}

	public void setActivityTypeString(String activityTypeString) {
		this.activityTypeString = activityTypeString;
	}

	public String getJobNumberString() {
		return jobNumberString;
	}

	public void setJobNumberString(String jobNumberString) {
		this.jobNumberString = jobNumberString;
	}

	public boolean isBillable() {
		
		if (jobNumberString != null ) {
			return true;
		}
		
		return false;
		
	}
	
	public String getCommentString() {
		return commentString;
	}

	public void setCommentString(String commentString) {
		this.commentString = commentString;
	}

	public Time getTimeStart() {
		return timeStart;
	}
	
	public Long getTimeStartInMillis() {
		return getTimeStart().toMillis(false);
	}
	
	public String getTimeStartString() {
		return fDATE.formatTimeHHMI(timeStart);
	}

	public void setTimeStart(Time timeStart) {
		this.timeStart = timeStart;
		
		/** ensure that the end time is never before start time*/
		if (timeEnd.toMillis(false) < timeStart.toMillis(false)) {
			if (getTimeEndString() != null) {
			   timeEnd.set(timeStart);
			}
		}
	}

	public Time getTimeEnd() {
		return timeEnd;
	}

	public Long getTimeEndInMillis() {
		return getTimeEnd().toMillis(false);
	}
	
	public String getTimeEndString() {
		
	  Long duration = fDATE.getDurationInMillis(new Time(), timeEnd);
	  if (duration > 100) {
		return fDATE.formatTimeHHMI(timeEnd);
	  }
	  return null;
	}	

	public void setTimeEnd(Time timeEnd) {
		this.timeEnd = fDATE.removeSeconds(timeEnd);
		
		/** ensure that the start time is never after the end time*/
		if (timeEnd.toMillis(false) < timeStart.toMillis(false)) {
			if (getTimeEndString() != null) {
				   timeStart.set(timeEnd);
			}
		}
	}
	
	public void setTimeEndNow () {
		Time t = new Time();
		t.setToNow();
		this.timeEnd = t;
	}
	
	public String getDurationString() {
		if (timeStart != null && timeEnd != null && timeStart != new Time() && fDATE.getDurationInMillis(new Time(), timeEnd) > 100) {
			
			return fDATE.getDifferenceHoursMinutes(timeStart, timeEnd);
		}

		return null;
	}
	
	public boolean isEndTimeSet() {
		Time sot = new Time();
		int result = sot.compare(timeEnd, sot);
		Log.d(TAG, String.valueOf(result));
		if (result < 0 || result > 0) {
			return true;
		}
		return false;
	}

	public boolean isActive() {
		if (getTimeEndString() == null) {
			return true;
		}
		return false;
	}
	
	public Long getGapInMillis() {
		if (gapInMillis<0) {
			return gapInMillis * -1;
		}
		return gapInMillis;
	}

	public void setGapInMillis(Long gap) {
		this.gapInMillis = gap;
		
		if (gapInMillis < 0) {
			gapType = GAP_OVERLAP;
		} else if (gapInMillis > 0) {
			gapType = GAP_APART;
		} else if (gapInMillis == 0) {
			gapType = GAP_TOGETHER;
		}
	}
	
    public int getGapType() {
    	return gapType;
    }

	public ActivityType getActivityType() {
		return activityType;
	}

	public void setActivityType(ActivityType activityType) {
		this.activityType = activityType;
	}

	public Job getJob() {
		return job;
	}

	public void setJob(Job job) {
		this.job = job;
		if (job != null) {
		  setJobNumberString(job.getJobNumberString());
		}
	}

	public Integer getActivityTypeCode() {
		return activityTypeCode;
	}

	public void setActivityTypeCode(Integer mActivityTypeCode) {
		this.activityTypeCode = mActivityTypeCode;
		this.activityType = ActivityTypesButtonsHolder.mActivityTypesMap.get(mActivityTypeCode);
		this.setActivityTypeString(this.activityType.getTitle());
	}
	
    
    
}
