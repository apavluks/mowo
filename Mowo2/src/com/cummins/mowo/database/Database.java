package com.cummins.mowo.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Point;
import android.util.Log;

import com.cummins.mowo.activity.MainActivity;

public class Database extends SQLiteOpenHelper {

	private static final String ACTIVITIES_TABLE = "ACTIVITIES";
	private static final String COL_ID = "ID";
	private static final String COL_WWID = "WWID";
	private static final String COL_CLOCK_IN = "CLOCK_IN";
	private static final String COL_CLOCK_OUT = "CLOCK_OUT";
	private static final String COL_ACTIVITY_TYPE = "ACTIVITY_TYPE";
	private static final String COL_WORK_ORDER = "WORK_ORDER";
	private static final String COL_ATT1 = "ATTRIBUTE1";
	private static final String COL_ATT2 = "ATTRIBUTE2";
	private static final String COL_ATT3 = "ATTRIBUTE3";
	private static final String COL_ATT4 = "ATTRIBUTE4";
	private static final String COL_ATT5 = "ATTRIBUTE5";
	
	
	public Database(Context context) {
		super(context, "mowo.db", null, 2);
		//context.deleteDatabase("mowo.db");
		// TODO Auto-generated constructor stub
	}	
	@Override	
	public void onCreate(SQLiteDatabase db) {
		
		Log.d(MainActivity.DEBUGTAG, "Formating create table string ");

		db.execSQL("DROP TABLE IF EXISTS " + ACTIVITIES_TABLE);
		
		String sql = String.format("create table %s (%S INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT NOT NULL, %s TEXT NOT NULL, %s TEXT, %s TEXT NOT NULL, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT)",ACTIVITIES_TABLE, COL_ID, COL_WWID, COL_CLOCK_IN, COL_CLOCK_OUT, COL_ACTIVITY_TYPE, COL_ATT1, COL_ATT2, COL_ATT3, COL_ATT4, COL_ATT5);
				
		Log.d(MainActivity.DEBUGTAG, "CREATA TABLE STRING "+ sql);
		db.execSQL(sql);
		
		
		 
	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
		 onCreate(db);
		// TODO Auto-generated method stub
		
	}
	
	/*public void saveActivity(TimeActivity activity) {
		SQLiteDatabase db = getWritableDatabase();
		
		//COL_ID, COL_WWID, COL_ACTIVITY_TYPE, COL_ATT1, COL_ATT2, COL_ATT3, COL_ATT4, COL_ATT5
		
		ContentValues values = new ContentValues();
		values.put(COL_WWID, activity.getWwid());
		values.put(COL_ACTIVITY_TYPE, activity.getActivityType());
		values.put(COL_CLOCK_IN, activity.getClockIn());
		values.put(COL_CLOCK_OUT, activity.getClockOut());

		values.put(COL_ATT1, activity.getWorkOrder());
		values.put(COL_ATT2, activity.getEngineSerialNumber());
		
		db.insert(ACTIVITIES_TABLE, null, values);
		
		Log.d(MainActivity.DEBUGTAG, "Activity Saved in Database");
		
		db.close();
		
	}
	
	public List<TimeActivity> getTimeActivities() {
		
		Log.d(MainActivity.DEBUGTAG, "Retrieve Time Activities from Database");
		
		List<TimeActivity> tActivityList = new ArrayList<TimeActivity>();
		
		SQLiteDatabase db = getReadableDatabase();
		
		String sql = String.format("SELECT %s, %s, %s, %s, %s FROM %s ORDER BY %s", COL_ID, COL_ACTIVITY_TYPE, COL_CLOCK_IN, COL_ATT1, COL_ATT2, ACTIVITIES_TABLE, COL_ID);

		
		Cursor cursor = db.rawQuery(sql, null);
		
		while(cursor.moveToNext()) {
			 

			int x = cursor.getInt(0);
			String activityType = cursor.getString(1);
			String clockIn = cursor.getString(2);
		    String workOrder = cursor.getString(3);
		    String engineSerNo = cursor.getString(4);
		    String wwid = "EZ491";
		    String engineModel = "012332";
		    String clockOut = "NULL";
		    		
		    
		    tActivityList.add(new TimeActivity(wwid, activityType,
					workOrder, engineSerNo, engineModel, clockIn, clockOut));
		    
		}
		
		db.close();
		
		return tActivityList;
	
	}
	*/
	
}
