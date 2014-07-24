package com.cummins.mowo.conrollers.timecardentry;

import java.util.ArrayList;

import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import com.cummins.mowo.activity.MainActivity;
import com.cummins.mowo.conrollers.ControllerState;
import com.cummins.mowo.conrollers.timecard.TimecardController;
import com.cummins.mowo.daos.TimecardDao;
import com.cummins.mowo.model.TimecardModel;
import com.cummins.mowo.vos.TimecardEntry;

public class TimecardEntryState implements ControllerState{

	private static final String TAG = TimecardEntryState.class.getSimpleName();
	
	private HandlerThread workerThread;
	
	protected TimecardEntryController controller;
	protected TimecardModel model;
	
	private Handler workerHandler;
	protected Handler getWorkerHander() {
		return workerHandler;
	}
	
	public TimecardEntryState(TimecardEntryController controller) {
		this.controller = controller;
		this.model = controller.getModel();
		workerThread = new HandlerThread("Unlocked Save Thread");
		workerThread.start();
		workerHandler = new Handler(workerThread.getLooper());
	}
	
	@Override
	public void dispose() {
		workerThread.getLooper().quit();
	}	
	
	@Override
	public boolean handleMessage(int what) {
		return handleMessage(what, null);
	}

	@Override
	public boolean handleMessage(int what, Object data) {
		switch(what) {
			case TimecardController.MESSAGE_SAVE_MODEL:
				saveEntry((Integer)data);
				return true;
			case TimecardController.MESSAGE_POPULATE_MODEL_BY_ID:
				populateModel((Integer)data);
				return true;
			case TimecardController.MESSAGE_CREATE_NEW_MODEL:
				createNewModel();
				return true;
			default:
				return false;
		}
	}


	private void createNewModel() {
		TimecardModel tm = new TimecardModel();
		model.consume(tm);
	}

	private void populateModel(final int id) {
		Log.d(TAG, "Entering method.populateModel id = " + id);
		if (id < 0) return;
		workerHandler.post(new Runnable() {
			@Override
			public void run() {
				synchronized (model) {
					TimecardDao dao = new TimecardDao();
					TimecardModel tcm = dao.getTimecard(id);
					Log.d(TAG, "     tcm_id = " + tcm.getId());
					if (tcm == null) tcm = new TimecardModel();
					Log.d(TAG, "     before consume entry size = " + tcm.getTimecardEntryList().size());
					// for some reason copying list though consume method does not work in adapter refresh 
					// so will manually loop through the list and assign values to the model's list
					/*while(model.getTimecardEntryList().size() > 0) {
						model.getTimecardEntryList().remove(0);
					}
					for (TimecardEntry entry : tcm.getTimecardEntryList()) {
						model.getTimecardEntryList().add(entry);
						Log.d(TAG, "     adding list item to model " + id);
					}*/
		
					model.consume(tcm);					
					controller.notifyOutboxHandlers(TimecardController.MESSAGE_UPDATE_VIEW, 0, 0, null);	
				}
			}
		});
	}	
	
	private void saveEntry(final int pos) {
		Log.d(TAG, "class.TimecardEntryStage method.saveEntry model = " + model.toString());
		workerHandler.post(new Runnable() {
			@Override
			public void run() {
				synchronized (model) {
					long effected = 0;
					TimecardDao dao = new TimecardDao();
					if (model.getId() > 0) {
						TimecardEntry entry = model.getTimecardEntryList().get(pos);
					    effected = dao.updateEntryWrapper(entry, model.getId());
						
						// this would be the case if 
						// item is saved, item is deleted from list, user goes history back,
						// old model still have id value.
						if (effected < 1) {
							//long id = dao.insert(model);
							//model.setId((int)id);
						}
					} else {
						//long id = dao.insert(model);
						//model.setId((int)id);
					}
					Log.d(TAG, "class.TimecardEntryStage method.saveEntry fire MESSAGE_SAVE_COMPLETE rows saved " + effected );
					controller.notifyOutboxHandlers(TimecardEntryController.MESSAGE_SAVE_COMPLETE, 0, 0, null);
				}
			}
		});
	}


    private void log(String str) {
    	Log.d(MainActivity.DEBUGTAG, str);
    }
}
