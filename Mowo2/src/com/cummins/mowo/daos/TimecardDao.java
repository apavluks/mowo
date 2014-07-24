package com.cummins.mowo.daos;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.format.Time;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.cummins.mowo.functions.fDATE;
import com.cummins.mowo.model.TimecardModel;
import com.cummins.mowo.vos.SessionDBParams;
import com.cummins.mowo.vos.TimecardEntry;
import com.cummins.mowo.activity.MainActivity;
import com.cummins.mowo.conrollers.timecard.TimecardState;
import com.cummins.mowo.database.DatabaseHelper;

public class TimecardDao {

	private static final String TAG = TimecardDao.class.getSimpleName();
	
	public static final String TC_TABLE = "TIMECARD";
	public static final String TC_ID = "ID";
	public static final String TC_START = "PUNCH_START";
	public static final String TC_END = "PUNCH_END";
	public static final String TC_STATUS = "STATUS";
	public static final String TC_START_AUTOPUNCH = "START_AUTO_PUNCH";
	public static final String TC_START_LATITUDE = "START_LATITUDE";
	public static final String TC_START_LONGTITUDE = "START_LONGTITUDE";
	public static final String TC_START_GPS_LOCATION = "START_GPS_LOCATION";
	public static final String TC_END_AUTOPUNCH = "END_AUTO_PUNCH";
	public static final String TC_END_LATITUDE = "END_LATITUDE";
	public static final String TC_END_LONGTITUDE = "END_LONGTITUDE";
	public static final String TC_END_GPS_LOCATION = "END_GPS_LOCATION";	
	
	protected static final String ACTUAL_START = "ACTUAL_START";
	protected static final String ACTUAL_END = "ACTUAL_END";
	protected static final String ACTIVITY_TYPE = "ACTIVITY_TYPE";
	protected static final String JOB_NUMBER = "JOB_NUMBER";

	public static final String TCE_TABLE = "TIMECARD_ENTRY";
	public static final String TCE_ID = "ID";
	public static final String TCE_START_TIME = "START_TIME";
	public static final String TCE_END_TIME = "END_TIME";
	public static final String TCE_ACTIVITY_TYPE = "ACTIVITY_TYPE";
	public static final String TCE_TC_ID = "TIMECARD_ID";
	public static final String TCE_JOB_ID = "JOB_ID";
	public static final String TCE_COMMENT = "COMMENT";
	
	
	public static final String TSP_TABLE = "SESSION_PARAMETERS";
	public static final String TSP_PARAM_NAME = "PARAMETER";
	public static final String TSP_PARAM_VALUE = "VALUE";
	
	public static final String V_PARAM_ACTIVE_TIMECARD_MODEL_ID = "ACTIVE_TC_MODEL_ID";
	
	public TimecardDao() {
		//
	}
	
	public int setDBParameter(SessionDBParams param) {
		
		int num = 0;
		
		SQLiteDatabase db = new DatabaseHelper().getWritableDatabase();
		ContentValues values = new ContentValues();
		
		values.put(TSP_PARAM_NAME, param.getParam());
		values.put(TSP_PARAM_VALUE, param.getValue());
		
		int numupd = db.update(TSP_TABLE, values, TSP_PARAM_NAME + "=?", new String[]{param.getParam()});
		num = numupd;
		Log.d(TAG, " class.TimecardDao method.setDBParameter UPDATE " + TSP_PARAM_NAME + " with value " + param.getValue());
		
		/*if no records updated then insert new record*/
		if (numupd < 1) {
			int numins = (int) db.insert(TSP_TABLE, null, values);
			num = numins;
			Log.d(TAG, " class.TimecardDao method.setDBParameter INSERT " + TSP_PARAM_NAME + " with value " + param.getValue());			
		}
		
		db.close();
		return num;
	}	
	
	public String getDBParameter(String paramName) {
		Log.d(TAG, " class.TimecardDao entering method.getDBParameter fetch param = " + paramName);
		SQLiteDatabase db = new DatabaseHelper().getWritableDatabase();
		
		Cursor cursor = db.query(TSP_TABLE, null, TSP_PARAM_NAME+"=?", new String[] {paramName}, null, null, null);
		String value = null;
		if (cursor.moveToFirst()) {
		   value = cursor.getString(cursor.getColumnIndex(TSP_PARAM_VALUE));
		}
		
		/*set timecard id to -1 , which means empty non existend id*/
		if (paramName.equals(V_PARAM_ACTIVE_TIMECARD_MODEL_ID) && value == null) {
			value = "-1";
		}
		
		cursor.close();
		db.close();
		return value;
	}	
	
	public TimecardEntry get(int id) {

		TimecardEntry te = new TimecardEntry("000", null, null, fDATE.getTimeNow(), null);

		return te;
	}	
	
	public TimecardEntry getEmtpyEntry() {

		TimecardEntry te = new TimecardEntry("000", null, null, fDATE.getTimeNow(), null);

		return te;
	}	
	
	public TimecardModel getTimecard(int id) {
		Log.d(TAG, " class.TimecardDao entering method.getTimecard id = " + id);
		SQLiteDatabase db = new DatabaseHelper().getWritableDatabase();
		
		Cursor cursor = db.query(TC_TABLE, null, TC_ID+"=?", new String[] {Integer.toString(id)}, null, null, null);
		TimecardModel tcm = null;
		if (cursor.moveToFirst()) {
			
			Time timeStart = new Time();
			Time timeEnd = new Time();
			
			Long timeStartLong = cursor.getLong(cursor.getColumnIndex(TC_START));
			timeStart.set(timeStartLong);
			
			Long timeEndLong = cursor.getLong(cursor.getColumnIndex(TC_END));
			timeEnd.set(timeEndLong);
			
			tcm = new TimecardModel();
			tcm.setId(cursor.getInt(cursor.getColumnIndex(TC_ID)));
			tcm.setClockInTime(timeStart);
			tcm.setClockOutTime(timeEnd);
			tcm.setTimecardStatus(cursor.getInt(cursor.getColumnIndex(TC_STATUS)));
			
			tcm.getPunchInData().setAutopanch(cursor.getString(cursor.getColumnIndex(TC_START_AUTOPUNCH)));
			tcm.getPunchInData().setLatitute(Double.valueOf(cursor.getString(cursor.getColumnIndex(TC_START_LATITUDE))));
			tcm.getPunchInData().setLongtitute(Double.valueOf(cursor.getString(cursor.getColumnIndex(TC_START_LONGTITUDE))));
			tcm.getPunchInData().setAddress(cursor.getString(cursor.getColumnIndex(TC_START_GPS_LOCATION)));
			
			tcm.getPunchOutData().setAutopanch(cursor.getString(cursor.getColumnIndex(TC_END_AUTOPUNCH)));
			tcm.getPunchOutData().setLatitute(Double.valueOf(cursor.getString(cursor.getColumnIndex(TC_END_LATITUDE))));
			tcm.getPunchOutData().setLongtitute(Double.valueOf(cursor.getString(cursor.getColumnIndex(TC_END_LONGTITUDE))));
			tcm.getPunchOutData().setAddress(cursor.getString(cursor.getColumnIndex(TC_END_GPS_LOCATION)));
			
			List<TimecardEntry> list = getEntries(db, id, tcm);
			
			tcm.setTimecardEntryList(list);
			
			tcm.calculateOverLaps();
		}
		
		cursor.close();
		db.close();
		return tcm;
	}	
	
	public int insert(TimecardModel model) {
		
		SQLiteDatabase db = new DatabaseHelper().getWritableDatabase();
		ContentValues values = new ContentValues();
		if (model.getId() > 0) values.put(TC_ID, model.getId());
		values.put(TC_START, model.getClockInTime().toMillis(false));
		values.put(TC_END, model.getClockOutTime().toMillis(false));
		values.put(TC_STATUS, model.getTimecardStatus());
		values.put(TC_START_AUTOPUNCH, model.getPunchInData().getAutopanch());
		values.put(TC_START_LATITUDE, model.getPunchInData().getLatitute());
		values.put(TC_START_LONGTITUDE, model.getPunchInData().getLongtitute());
		values.put(TC_START_GPS_LOCATION, model.getPunchInData().getAddress());
		values.put(TC_END_AUTOPUNCH, model.getPunchOutData().getAutopanch());
		values.put(TC_END_LATITUDE, model.getPunchOutData().getLatitute());
		values.put(TC_END_LONGTITUDE, model.getPunchOutData().getLongtitute());
		values.put(TC_END_GPS_LOCATION, model.getPunchOutData().getAddress());	
		
		int num = (int) db.insert(TC_TABLE, null, values);
		
		Log.d(TAG, " class.TimecardDao new timecard saved id = " + num);

		for (TimecardEntry entry : model.getTimecardEntryList()) {			

			if (entry.getId() > 0) {
		    	long entryId = updateEntry(db, entry, num);
		    	Log.d(TAG, " class.TimecardDao new entry updated id = " + entryId);
			} else {
				long entryId = insertEntry(db, entry, num);
				Log.d(TAG, " class.TimecardDao new entry inserted id = " + entryId);
			}
	    }

		db.close();
		return num;
	}	
	
	public int update(TimecardModel model) {
		SQLiteDatabase db = new DatabaseHelper().getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(TC_ID, model.getId());
		values.put(TC_START, model.getClockInTime().toMillis(false));
		values.put(TC_END, model.getClockOutTime().toMillis(false));
		values.put(TC_STATUS, String.valueOf(model.getTimecardStatus()));
		
		Log.d(TAG, " class.TimecardDao before updating tcid = " + model.getId());
		
		int num = db.update(TC_TABLE, values, TC_ID + "=?", new String[]{Integer.toString(model.getId())});
		
		Log.d(TAG, " class.TimecardDao after timecard updated id = " + num);
		
		for (TimecardEntry entry : model.getTimecardEntryList()) {

			if (entry.getId() > 0) {
		    	long numberofRowsUpdated = updateEntry(db, entry, model.getId());
		    	Log.d(TAG, " class.TimecardDao entry rows updated = " + numberofRowsUpdated);
			} else {
				long entryId = insertEntry(db, entry, model.getId());
				Log.d(TAG, " class.TimecardDao new entry inserted id = " + entryId);
			}
			
		}		
		
		db.close();
		return num;
	}
	
	private long insertEntry(SQLiteDatabase db, TimecardEntry entry, long tcId ) {
		
		ContentValues valuesEntries = new ContentValues();
		
		if (entry.getId() > 0) valuesEntries.put(TCE_ID, entry.getId());
		
		Long timeStartLong  = (long) 0;
		if (entry.isEndTimeSet()) {
			timeStartLong = fDATE.removeSeconds(entry.getTimeStart()).toMillis(false);
	    } else {
	    	timeStartLong = entry.getTimeStart().toMillis(false);
	    }
		
		valuesEntries.put(TCE_START_TIME, entry.getTimeStart().toMillis(false));
		valuesEntries.put(TCE_END_TIME, entry.getTimeEnd().toMillis(false));
		valuesEntries.put(TCE_ACTIVITY_TYPE, entry.getActivityTypeCode());
		valuesEntries.put(TCE_TC_ID, tcId);
		valuesEntries.put(TCE_JOB_ID, (entry.getJobNumberString() == null)? "0" : entry.getJobNumberString());
		valuesEntries.put(TCE_COMMENT, entry.getCommentString());
		
		long num = db.insert(TCE_TABLE, null, valuesEntries);
		
		return num;
	}
	
	private long updateEntry(SQLiteDatabase db, TimecardEntry entry, int tcId ) {
		
		ContentValues valuesEntries = new ContentValues();
		
		if (entry.getId() > 0) valuesEntries.put(TCE_ID, entry.getId());
		valuesEntries.put(TCE_START_TIME, entry.getTimeStart().toMillis(false));
		valuesEntries.put(TCE_END_TIME, entry.getTimeEnd().toMillis(false));
		valuesEntries.put(TCE_ACTIVITY_TYPE, entry.getActivityTypeCode());
		valuesEntries.put(TCE_TC_ID, tcId);
		if (entry.getJobNumberString() == null) {
			valuesEntries.put(TCE_JOB_ID, 0);
		} else if (!entry.getJobNumberString().equals("")) {
			valuesEntries.put(TCE_JOB_ID, Integer.valueOf(entry.getJobNumberString()));
		}	
		valuesEntries.put(TCE_COMMENT, entry.getCommentString());
		
		long num = db.update(TCE_TABLE, valuesEntries, TCE_ID + "=?", new String[]{Integer.toString(entry.getId())});
		
		return num;
	}	
	
	public long updateEntryWrapper(TimecardEntry entry, int tcId ) {
		SQLiteDatabase db = new DatabaseHelper().getWritableDatabase();
		
		long rowcount = updateEntry(db, entry, tcId);
		
		db.close();
		return rowcount;
	}
	
	public List<TimecardModel> getTimecards(Long refDateStart, Long refDateEnd) {
		
		Log.d(TAG, "Starting class.TimecardsDoa method.getTimecards");

		List<TimecardModel> list = new ArrayList<TimecardModel>();		
		SQLiteDatabase db = new DatabaseHelper().getWritableDatabase();
		
		String orderBy =  TC_START + " DESC";
		String whereStmt = TC_START + " BETWEEN " + refDateStart + " AND " + refDateEnd;
		
		
		
		Cursor cursor = db.query(TC_TABLE, null, "PUNCH_START BETWEEN ? AND ?", 
				                  new String[] {String.valueOf(refDateStart), String.valueOf(refDateEnd)}, null, null, null);
		
		while (cursor.moveToNext()) {
			
			Time timeStart = new Time();
			Time timeEnd = new Time();
			
			Long timeStartLong = cursor.getLong(cursor.getColumnIndex(TC_START));
			timeStart.set(timeStartLong);
			
			Log.d(TAG, "Starting class.TimecardsDoa method.getTimecards timeStartLong " + timeStartLong);
			
			Long timeEndLong = cursor.getLong(cursor.getColumnIndex(TC_END));
			timeEnd.set(timeEndLong);
			
			TimecardModel tcm = new TimecardModel();
			tcm.setId(cursor.getInt(cursor.getColumnIndex(TC_ID)));
			tcm.setClockInTime(timeStart);
			tcm.setClockOutTime(timeEnd);
			tcm.setTimecardStatus(cursor.getInt(cursor.getColumnIndex(TC_STATUS)));
			
			//List<TimecardEntry> listEntries = getEntries(db, tcm);
			
			List <TimecardEntry> listEntries = new ArrayList<TimecardEntry>();
			
			tcm.setTimecardEntryList(listEntries);
		
			list.add(tcm);
		}
		cursor.close();
		db.close();
		
		Log.d(TAG, "Inside class.TimecardsDoa method.getTimecards output List Size " + list.size());
		
		return list;
	}
	
	public List<TimecardEntry> getEntries(SQLiteDatabase db, int tcId, TimecardModel tcm) {
		
		Log.d(TAG, " class.TimecardDao entering method.getEntries tcId " + tcId);
		
		List<TimecardEntry> list = new ArrayList<TimecardEntry>();
		
		String orderBy =  TCE_START_TIME + " ASC";
		
		Cursor cursor = db.query(TCE_TABLE, null, TCE_TC_ID + "=?",new String[] { Integer.toString(tcId) }, null, null, orderBy);
	
		Log.d(TAG, " number of rows in the " + cursor.getCount());
		
		while (cursor.moveToNext()) {
			
			//Log.d(TAG, " class.TimecardDao after move to text entry id =  " + String.valueOf(cursor.getInt(cursor.getColumnIndex(TCE_ID))));

			Time timeStartEntry = new Time();
			Time timeEndEntry = new Time();

			Long timeStartEntryLong = cursor.getLong(cursor.getColumnIndex(TCE_START_TIME));
			timeStartEntry.set(timeStartEntryLong);

			Long timeEndEntryLong = cursor.getLong(cursor.getColumnIndex(TCE_END_TIME));
			timeEndEntry.set(timeEndEntryLong);
						
			TimecardEntry tce = new TimecardEntry(null, null, null, null, null);
			
			tce.setId(cursor.getInt(cursor.getColumnIndex(TCE_ID)));
			tce.setTimeStart(timeStartEntry);
			tce.setTimeEnd(timeEndEntry);
			tce.setActivityTypeCode(cursor.getInt(cursor.getColumnIndex(TCE_ACTIVITY_TYPE)));
			tce.setTimecardId(cursor.getInt(cursor.getColumnIndex(TCE_TC_ID)));
			tce.setJobNumberString(String.valueOf(cursor.getInt(cursor.getColumnIndex(TCE_JOB_ID))));
		    if (tce.getJobNumberString().equals("0")) tce.setJobNumberString(null);
			tce.setCommentString(cursor.getString(cursor.getColumnIndex(TCE_COMMENT)));
			
			list.add(tce);
			
			// use this to control gap calculation logic

		}
		cursor.close();
		return list;
	}	
	
	
	public int deleteTimecard (int id) {
		Log.d(TAG, " class.TimecardDao entering method.deleteTimecard id = " + id);
		SQLiteDatabase db = new DatabaseHelper().getWritableDatabase();
		
		deleteEntries(db, id);
		
		/** operation return 0 if error and 1 if success*/
		int status = db.delete(TC_TABLE, TC_ID+"=?", new String[] {Integer.toString(id)});

		db.close();
		return status;
	}
	
	public int deleteEntries(SQLiteDatabase db, int tcId) {
		
		Log.d(TAG, " class.TimecardDao entering method.deleteEntries tcId " + tcId);
		
		int status = db.delete(TCE_TABLE, TCE_TC_ID + "=?",new String[] { Integer.toString(tcId) });
		
		Log.d(TAG, " number of rows deleted " + status);
		
		return status;
		
	}	
	
	public int deleteEntry(TimecardEntry entry) {
		
		Log.d(TAG, " class.TimecardDao entering method deleteEntry id = " + entry.getId());
		SQLiteDatabase db = new DatabaseHelper().getWritableDatabase();
		
		int status = db.delete(TCE_TABLE, TCE_ID + "=?",new String[] { Integer.toString(entry.getId()) });
		
		Log.d(TAG, " timecard entry  " + entry.getId() + " is deleted ");
		
		db.close();
		
		return status;
		
	}		
	
	public void insertEntryWrapper(TimecardEntry entry, TimecardModel model) {
		
		Log.d(TAG, " class.TimecardDao entering method insertEntry id = ");
		SQLiteDatabase db = new DatabaseHelper().getWritableDatabase();
		
		long entryId = insertEntry(db, entry, model.getId());
		
		db.close();
		
		
	}	
	
}
