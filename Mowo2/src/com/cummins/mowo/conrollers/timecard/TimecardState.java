package com.cummins.mowo.conrollers.timecard;

import java.util.ArrayList;

import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import com.cummins.mowo.R;
import com.cummins.mowo.activity.MainActivity;
import com.cummins.mowo.conrollers.ControllerState;
import com.cummins.mowo.daos.TimecardDao;
import com.cummins.mowo.model.TimecardModel;
import com.cummins.mowo.vos.SessionDBParams;
import com.cummins.mowo.vos.TimecardEntry;

public class TimecardState implements ControllerState{

	private static final String TAG = TimecardState.class.getSimpleName();
	
	private HandlerThread workerThread;
	
	protected TimecardController controller;
	protected TimecardModel model;
	
	private Handler workerHandler;
	protected Handler getWorkerHander() {
		return workerHandler;
	}
	
	public TimecardState(TimecardController controller) {
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
				saveModel();
				return true;
			case TimecardController.MESSAGE_POPULATE_MODEL_BY_ID:
				populateModel((Integer)data);
				return true;
			case TimecardController.MESSAGE_CREATE_NEW_MODEL:
				createNewModel();
				return true;
			case TimecardController.MESSAGE_STOP_CURRENT_ENTRY:
				 handleStopCurrentEntry();
				 return true;				
			case TimecardController.MESSAGE_CONTINUE_CURRENT_ENTRY:
				 handleContinueCurrentEntry();
				 return true;	
			case TimecardController.MESSAGE_DELETE_TIMECARD:
				deleteTimecard((Integer)data);
				return true;	
			case TimecardController.MESSAGE_SET_SESSION_PARAMETER:
				setSessionDBParameter((SessionDBParams)data);
				return true;
			case TimecardController.MESSAGE_START_NEW_SESSION:
				prepareNewSession();
				return true;
			case TimecardController.MESSAGE_DELETE_ENTRY:
			     handleDeleteEntry((TimecardEntry) data);
				 return true;	 				
			default:
				return false;
		}
	}

	private void handleDeleteEntry(final TimecardEntry entry) {
		workerHandler.post(new Runnable() {
			@Override
			public void run() {
				synchronized (model) {		
					TimecardDao dao = new TimecardDao();
					dao.deleteEntry(entry);
					populateModel(controller.getModel().getId());
				}
			}
		});
	}
	
	public void handleAddEntry(final TimecardEntry entry) {
		workerHandler.post(new Runnable() {
			@Override
			public void run() {
				synchronized (model) {		
					TimecardDao dao = new TimecardDao();
					dao.insertEntryWrapper(entry, model);
					TimecardModel tcm = dao.getTimecard(model.getId());
					model.consume(tcm);					
					controller.notifyOutboxHandlers(TimecardController.MESSAGE_UPDATE_VIEW, 0, 0, null);	
				}			
			}
		});
	}

	private void prepareNewSession() {
		final SessionDBParams sessionParam = new SessionDBParams(TimecardDao.V_PARAM_ACTIVE_TIMECARD_MODEL_ID, null);
		workerHandler.post(new Runnable() {
			@Override
			public void run() {
				synchronized (model) {		
					TimecardDao dao = new TimecardDao();
					dao.setDBParameter(sessionParam);
					controller.notifyOutboxHandlers(TimecardController.MESSAGE_READY_FOR_NEW_SESSION, 0, 0, null);
				}
			}
		});
	}

	private void setSessionDBParameter(final SessionDBParams data) {
		
		/** not required to save timecard because ID is less than zero , which means timecard has not been saved yet*/
		if (data.getParam().equals(TimecardDao.V_PARAM_ACTIVE_TIMECARD_MODEL_ID) && Integer.valueOf(data.getValue()) < 0) {
			return;
		}
		workerHandler.post(new Runnable() {
			@Override
			public void run() {
				synchronized (model) {		
					TimecardDao dao = new TimecardDao();
					dao.setDBParameter(data);
				}
			}
		});
		
	}

	private void deleteTimecard(final int id) {
		Log.d(TAG, "Entering method.deleteTimecard id = " + id);
		if (id < 0) return;
		workerHandler.post(new Runnable() {
			@Override
			public void run() {
				synchronized (model) {		
					TimecardDao dao = new TimecardDao();
					dao.deleteTimecard(id);
					controller.notifyOutboxHandlers(TimecardController.MESSAGE_TIMECARD_DELETED, 0, 0, null);	
				}
			}
		});
	}

	private void createNewModel() {
		TimecardModel tm = new TimecardModel();
		model.consume(tm);
	}

	private void populateModel(final int id) {
		Log.d(TAG, "Entering method.populateModel id = " + id);
		if (id < 0) {
			controller.notifyOutboxHandlers(TimecardController.MESSAGE_LOADING_NOT_REQUIRED, 0, 0, null);	
			return;
		}
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
	
	private void saveModel() {
		workerHandler.post(new Runnable() {
			@Override
			public void run() {
				synchronized (model) {
					TimecardDao dao = new TimecardDao();
					if (model.getId() > 0) {
						int effected = dao.update(model);
						
						// this would be the case if 
						// item is saved, item is deleted from list, user goes history back,
						// old model still have id value.
						if (effected < 1) {
							long id = dao.insert(model);
							model.setId((int)id);
						}
					} else {
						long id = dao.insert(model);
						model.setId((int)id);
					}
					
					final SessionDBParams sessionParam = new SessionDBParams(TimecardDao.V_PARAM_ACTIVE_TIMECARD_MODEL_ID, String.valueOf(model.getId()));
					dao.setDBParameter(sessionParam);
					
					controller.notifyOutboxHandlers(TimecardController.MESSAGE_SAVE_COMPLETE, 0, 0, null);
				}
			}
		});
	}

	private void handleStopCurrentEntry() {
		workerHandler.post(new Runnable() {
			@Override
			public void run() {
				synchronized (model) {
					TimecardDao dao = new TimecardDao();
					if (model.getId() > 0) {
						int effected = dao.update(model);
						
						// this would be the case if 
						// item is saved, item is deleted from list, user goes history back,
						// old model still have id value.
						if (effected < 1) {
							long id = dao.insert(model);
							model.setId((int)id);
						}
					} else {
						long id = dao.insert(model);
						model.setId((int)id);
					}
					
					final SessionDBParams sessionParam = new SessionDBParams(TimecardDao.V_PARAM_ACTIVE_TIMECARD_MODEL_ID, String.valueOf(model.getId()));
					dao.setDBParameter(sessionParam);
					
					controller.notifyOutboxHandlers(TimecardController.MESSAGE_CURRENT_ENTRY_STOPED, 0, 0, null);
				}
			}
		});
		
	}
	
	private void handleContinueCurrentEntry() {
		workerHandler.post(new Runnable() {
			@Override
			public void run() {
				synchronized (model) {
					
					////Log.d(TAG, "Active Entry is " + model.getActiveEntry().getJobNumberString()+ " " + model.getActiveEntry().getTimeEndString());
					TimecardDao dao = new TimecardDao();
					if (model.getId() > 0) {
						int effected = dao.update(model);
						
						// this would be the case if 
						// item is saved, item is deleted from list, user goes history back,
						// old model still have id value.
						if (effected < 1) {
							long id = dao.insert(model);
							model.setId((int)id);
						}
					} else {
						long id = dao.insert(model);
						model.setId((int)id);
					}
					dao.insertEntryWrapper(model.getContinueEntry(), model);
					TimecardModel tcm = dao.getTimecard(model.getId());
					model.consume(tcm);		
					controller.notifyOutboxHandlers(TimecardController.MESSAGE_CONTINUE_ENTRY_COMPLETED, 0, 0, null);	
				}
			}
		});
		
	}

	
	

    private void log(String str) {
    	Log.d(MainActivity.DEBUGTAG, str);
    }
}
