package com.cummins.mowo.conrollers.timecardentry;


import android.view.KeyEvent;

public class TimecardEntryLockedState extends TimecardEntryState{

	public TimecardEntryLockedState(TimecardEntryController controller) {
		super(controller);
		// TODO Auto-generated constructor stub
	}
	
	
	@Override
	public boolean handleMessage(int what) {
		switch(what) {
			default:
				return super.handleMessage(what);
		}
		
	}
	
	@Override
	public boolean handleMessage(int what, Object data) {
		switch(what) {
			default:
				return super.handleMessage(what, data);
		}
	}	
	
	
	private void updateLock(boolean lock) {
		model.setLocked(lock);
		controller.setMessageState(new TimecardEntryUnlockedState(controller));
	}

}
