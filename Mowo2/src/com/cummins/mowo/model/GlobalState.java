package com.cummins.mowo.model;

import com.cummins.mowo.activity.MainActivity;
import com.cummins.mowo.vos.TimecardEntry;

import android.app.Application;
import android.content.Context;
import android.util.Log;

/**
* This is a global POJO that we attach data to which we
* want to use across the application
**/

public class GlobalState extends Application {

	private TimecardModel timecardModel;
	private static GlobalState appContext;
	private int currentTimecardId; 

	@Override
	public void onCreate() {
		super.onCreate();
		Log.i(MainActivity.DEBUGTAG, "GlobalState.onCreate was called");
		appContext = this;
	}	
	
	public TimecardModel getTimecardModel() {
		return timecardModel;
	}

	public void setTimecardModel(TimecardModel timecardModel) {
		this.timecardModel = timecardModel;
	}
	
	public static Context getContext() {
		return appContext.getApplicationContext();
	}

	public int getCurrentTimecardId() {
		return currentTimecardId;
	}

	public void setCurrentTimecardId(int currentTimecardId) {
		this.currentTimecardId = currentTimecardId;
	}	
	
	
}
