package com.cummins.mowo.widgets;

import java.util.Calendar;
import java.util.Date;

import com.cummins.mowo.functions.fDATE;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class TimerManager {

	private final static String TAG = TimerManager.class.getSimpleName();
	
	private static int DEFAULT_FPS = 1;
	private static int DEFAULT_FREQUENCY_MILLIS = 1000;
	
	private Handler mUiHandler;
	private TextView mView;
	private long startTime;
	
	public TimerManager(TextView v) {
		this.mView = v;
		
	}
	
	 /**
     * Starts timer. Don't forget to stop it
     * if your activity goes to the background.
     * @throws IllegalStateException - if it isn't called form the UI thread
     */
    public void startTimer(long startTime) throws IllegalStateException {
        checkIfUiThread();
        if (mUiHandler != null) {
            Log.w(TAG, "Marker was already added.");
            return;
        }
        
        this.startTime = startTime;
        mUiHandler = new Handler();
        mUiHandler.post(mRunnable);
    }	
	
    /**
     * Stops timer. You sould call this method
     * at least on the onPause() of the Activity.
     * @throws IllegalStateException - if it isn't called form the UI thread
     */
    public void stopTimer() throws IllegalStateException {
        checkIfUiThread();
        if (mUiHandler == null) {
            return;
        }
        mUiHandler.removeCallbacks(mRunnable);
        mUiHandler = null;
    }
    
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
    		Calendar cal = Calendar.getInstance();
    		Date d = new Date();
    		cal.setTime(d);
    		long now = cal.getTimeInMillis();
    		long duration = now - startTime;
    		
    		Log.d(TAG, "Start Time " + startTime + " end Time " + now + " duration " + duration);
    		mView.setText(fDATE.formatInterval(duration));
            mUiHandler.postDelayed(mRunnable, 10000 / DEFAULT_FPS);
        }
    };
    
    private void checkIfUiThread() throws IllegalStateException {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            throw new IllegalStateException("This call has to be made from the UI thread.");
        }
    }
	
}
