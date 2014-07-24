package com.cummins.mowo.conrollers.timecardentry;

import android.util.Log;

import com.cummins.mowo.activity.MainActivity;
import com.cummins.mowo.conrollers.Controller;
import com.cummins.mowo.conrollers.ControllerState;
import com.cummins.mowo.model.TimecardModel;

public class TimecardEntryController extends Controller{

	private static final String TAG = TimecardEntryController.class.getSimpleName();
	
	
	private ControllerState messageState;
	protected void setMessageState(ControllerState messageState) {
		if (this.messageState != null) {
			this.messageState.dispose();
		}
		this.messageState = messageState;
	}	
	
	public static final int MESSAGE_SAVE_MODEL = 1;
	public static final int MESSAGE_SAVE_COMPLETE = 2;

	private TimecardModel model;
	public TimecardModel getModel() {
		return model;
	}
	
	public TimecardEntryController(TimecardModel model) {
		this.model = model;
		Log.d(MainActivity.DEBUGTAG, " TimecardEntryController constructor " );
		messageState = new TimecardEntryUnlockedState(this);
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
