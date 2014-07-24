package com.cummins.mowo.conrollers;

import java.util.ArrayList;
import java.util.List;

import com.cummins.mowo.conrollers.job.JobController;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

public abstract class Controller {
	
	@SuppressWarnings("unused")
	private static final String TAG = Controller.class.getSimpleName();
	private final List<Handler> outboxHandlers = new ArrayList<Handler>();
	

	public Controller() {
		
	}
	
	public void dispose() {}
	
	abstract public boolean handleMessage(int what, Object data);

	public boolean handleMessage(int what) {
		return handleMessage(what, null);
	}
	
	public final void addOutboxHandler(Handler handler) {
		outboxHandlers.add(handler);
	}

	public final void removeOutboxHandler(Handler handler) {
		outboxHandlers.remove(handler);
	}
	
	public final void notifyOutboxHandlers(int what, int arg1, int arg2, Object obj) {
		Log.d(TAG, "Insite Controller.notifyOutBoxHnadlers");
		if (!outboxHandlers.isEmpty()) {
			Log.d(TAG, "Insite Controller.notifyOutBoxHnadlers, outboxHnadler is not empty");
			for (Handler handler : outboxHandlers) {
				Log.d(TAG, "loop through handlers and send to target");
				Message msg = Message.obtain(handler, what, arg1, arg2, obj);
				msg.sendToTarget();
			}
		}
	}
}