package com.cummins.mowo.database;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.cummins.mowo.daos.JobDao;
import com.cummins.mowo.daos.TimecardDao;
import com.cummins.mowo.model.GlobalState;

public class DatabaseHelper  extends SQLiteOpenHelper{

	@SuppressWarnings("unused")
	private static final String TAG = DatabaseHelper.class.getSimpleName();
	private static final String DATABASE_NAME = "MOWO";
	private static final int DATABASE_VERSION = 10;
	
	public DatabaseHelper() {
		super(GlobalState.getContext(), DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		
		database.execSQL("DROP TABLE IF EXISTS " + TimecardDao.TC_TABLE);
		
		final String tc = "CREATE TABLE " + TimecardDao.TC_TABLE + "(" +
		    TimecardDao.TC_ID + " integer primary key, " +
			TimecardDao.TC_START + " real, " +	
			TimecardDao.TC_END + " real, " +
			TimecardDao.TC_STATUS + " int, " +
			TimecardDao.TC_START_AUTOPUNCH + " text, " +
			TimecardDao.TC_START_LATITUDE + " text, " +
			TimecardDao.TC_START_LONGTITUDE + " text, " +
			TimecardDao.TC_START_GPS_LOCATION + " text, " + 
			TimecardDao.TC_END_AUTOPUNCH + " text, " +
			TimecardDao.TC_END_LATITUDE + " text, " +
			TimecardDao.TC_END_LONGTITUDE + " text, " +			
			TimecardDao.TC_END_GPS_LOCATION + " text ) "; 
		
		database.execSQL(tc);
		
		database.execSQL("DROP TABLE IF EXISTS " + TimecardDao.TCE_TABLE);
		
		final String tce = "CREATE TABLE " + TimecardDao.TCE_TABLE + "(" +
			    TimecardDao.TCE_ID + " integer primary key, " +
				TimecardDao.TCE_START_TIME + " real, " +	
				TimecardDao.TCE_END_TIME + " real, " +
				TimecardDao.TCE_ACTIVITY_TYPE + " int, " +
				TimecardDao.TCE_TC_ID + " int, " +
				TimecardDao.TCE_JOB_ID + " int, " + 
				TimecardDao.TCE_COMMENT + " text)"; 
			
		database.execSQL(tce);	

		/** session paramters table*/
		database.execSQL("DROP TABLE IF EXISTS " + TimecardDao.TSP_TABLE);
		
		final String tsp = "CREATE TABLE " + TimecardDao.TSP_TABLE + "(" +
			    TimecardDao.TSP_PARAM_NAME + " text, " +
				TimecardDao.TSP_PARAM_VALUE + " text)"; 
			
		database.execSQL(tsp);
		
		/** jobs table*/
		database.execSQL("DROP TABLE IF EXISTS " + JobDao.JOB_TABLE);
		
		final String jobtbl = "CREATE TABLE " + JobDao.JOB_TABLE + "(" +
				JobDao.JOB_ID + " integer primary key, " +
				JobDao.JOB_NUMBER + " text, " +
				JobDao.JOB_DESCRIPTION + " text)"; 
			
		database.execSQL(jobtbl);			
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		onCreate(db);
	}

}

