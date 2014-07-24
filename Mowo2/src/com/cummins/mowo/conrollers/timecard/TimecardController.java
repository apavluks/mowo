package com.cummins.mowo.conrollers.timecard;

import android.util.Log;

import com.cummins.mowo.activity.MainActivity;
import com.cummins.mowo.conrollers.Controller;
import com.cummins.mowo.conrollers.ControllerState;
import com.cummins.mowo.model.TimecardModel;

public class TimecardController extends Controller{

	private static final String TAG = TimecardController.class.getSimpleName();
	
	
	private ControllerState messageState;
	protected void setMessageState(ControllerState messageState) {
		if (this.messageState != null) {
			this.messageState.dispose();
		}
		this.messageState = messageState;
	}	
	
	public static final int MESSAGE_SAVE_MODEL = 1;
	public static final int MESSAGE_POPULATE_MODEL_BY_ID = 2;
	public static final int MESSAGE_DELETE_TIMECARD =  3;
	public static final int MESSAGE_LOADING_NOT_REQUIRED =  4;
	public static final int MESSAGE_START_NEW_SESSION = 5;
	public static final int MESSAGE_STOP_CURRENT_ENTRY = 6;
	public static final int MESSAGE_CONTINUE_CURRENT_ENTRY = 7;
	
	public static final int MESSAGE_CLOCK_IN = 21;
	public static final int MESSAGE_CLOCK_OUT = 22;
	public static final int MESSAGE_ADD_ENTRY = 23;	
	public static final int MESSAGE_END_ENTRY = 24;	
	public static final int MESSAGE_DELETE_ENTRY = 25;	

	public static final int MESSAGE_SAVE_COMPLETE = 40; 
	public static final int MESSAGE_SAVE_NOW = 41;
	public static final int MESSAGE_UPDATE_VIEW = 42;
	public static final int MESSAGE_TIMECARD_DELETED = 43;
	public static final int MESSAGE_CURRENT_ENTRY_STOPED = 44;
	public static final int MESSAGE_CONTINUE_ENTRY_COMPLETED = 45;
	
	public static final int MESSAGE_UPDATE_LOCK = 60;

	public static final int MESSAGE_CREATE_NEW_MODEL = 70;

	public static final int MESSAGE_SHOW_TOAST = 101;
	
	public static final int MESSAGE_SET_SESSION_PARAMETER = 200;
	public static final int MESSAGE_READY_FOR_NEW_SESSION = 201;




	private TimecardModel model;
	public TimecardModel getModel() {
		return model;
	}
	
	public TimecardController(TimecardModel model) {
		this.model = model;
		Log.d(MainActivity.DEBUGTAG, " TimecardController constructor " );
		messageState = new TimecardUnlockedState(this);
	}	
	
	@Override
	public void dispose() {
		super.dispose();
		messageState.dispose();
	}
	
	@Override
	public boolean handleMessage(int what) {
		Log.i(TAG, "handling message code of " + what);
		return messageState.handleMessage(what);
	}
	
	@Override
	public boolean handleMessage(int what, Object data) {
		Log.i(TAG, "handling message code of " + what);
		return messageState.handleMessage(what, data);
	}	
}
