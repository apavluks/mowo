package com.cummins.mowo.conrollers.timecard;


import android.view.KeyEvent;

public class TimecardLockedState extends TimecardState{

	public TimecardLockedState(TimecardController controller) {
		super(controller);
		// TODO Auto-generated constructor stub
	}
	
	
	@Override
	public boolean handleMessage(int what) {
		switch(what) {
			case TimecardController.MESSAGE_CLOCK_IN:
				return false;
			default:
				return super.handleMessage(what);
		}
		
	}
	
	@Override
	public boolean handleMessage(int what, Object data) {
		switch(what) {
			case TimecardController.MESSAGE_UPDATE_LOCK:
				updateLock((Boolean)data);
				return true;
			default:
				return super.handleMessage(what, data);
		}
	}	
	
	
	private void updateLock(boolean lock) {
		model.setLocked(lock);
		controller.setMessageState(new TimecardUnlockedState(controller));
	}

}
