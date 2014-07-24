package com.cummins.mowo.conrollers.job;

import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import com.cummins.mowo.activity.MainActivity;
import com.cummins.mowo.conrollers.Controller;
import com.cummins.mowo.conrollers.ControllerState;
import com.cummins.mowo.conrollers.timecard.TimecardController;
import com.cummins.mowo.conrollers.timecardentry.TimecardEntryUnlockedState;
import com.cummins.mowo.daos.JobDao;
import com.cummins.mowo.daos.TimecardDao;
import com.cummins.mowo.model.TimecardModel;
import com.cummins.mowo.vos.Job;
import com.cummins.mowo.vos.SessionDBParams;
import com.cummins.mowo.vos.TimecardEntry;

public class JobController extends Controller implements ControllerState{

	private static final String TAG = JobController.class.getSimpleName();	
	public static final int MESSAGE_SAVE_JOB = 1;
	public static final int MESSAGE_SAVE_COMPLETE = 2;
	public static final int MESSAGE_POPULATE_MODEL_BY_ID = 4;
	
	
	private HandlerThread workerThread;
	private Job job;
	
	private Handler workerHandler;
	protected Handler getWorkerHander() {
		return workerHandler;
	}
	
	public Job getJob() {
		return job;
	}
	
	public JobController(Job job) {
		this.job = job;
		workerThread = new HandlerThread("Unlocked Save Thread");
		workerThread.start();
		workerHandler = new Handler(workerThread.getLooper());
		Log.d(MainActivity.DEBUGTAG, " TimecardEntryController constructor " );
	}	
	
	@Override
	public void dispose() {
		super.dispose();
		workerThread.getLooper().quit();
	}
	
	@Override
	public boolean handleMessage(int what) {
		Log.i(TAG, "handling message code of " + what);
		return handleMessage(what, null);
	}
	
	@Override
	public boolean handleMessage(int what, Object data) {
		Log.i(TAG, "handling message code of " + what);
		switch(what) {
		case JobController.MESSAGE_SAVE_JOB:
			handleSave();
			return true;
		default:
			return false;
		}
	}	
	
	public void handleSave() {
		workerHandler.post(new Runnable() {
			@Override
			public void run() {
				synchronized (job) {
					JobDao dao = new JobDao();
					if (job.getId() > 0) {
						int effected = dao.update(job);
						
						// this would be the case if 
						// item is saved, item is deleted from list, user goes history back,
						// old model still have id value.
						if (effected < 1) {
							long id = dao.insert(job);
							job.setId((int)id);
						}
					} else {
						long id = dao.insert(job);
						job.setId((int)id);
						Log.d(TAG, "Jobs saved ID is = " + id);
					}
										
					notifyOutboxHandlers(JobController.MESSAGE_SAVE_COMPLETE, 0, 0, null);
				}
			}
		});
	}
}
