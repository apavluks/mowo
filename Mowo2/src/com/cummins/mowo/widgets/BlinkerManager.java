package com.cummins.mowo.widgets;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;

public class BlinkerManager {

	private final static String TAG = BlinkerManager.class.getSimpleName();
	
	private static int DEFAULT_FPS = 1;
	private static int DEFAULT_FREQUENCY_MILLIS = 1000;
	
	private Handler mUiHandler;
	private View mView;
	
	public BlinkerManager(View v) {
		this.mView = v;
	}
	
	 /**
     * Starts the blinking of the view. Don't forget to stop it
     * if your activity goes to the background.
     * @throws IllegalStateException - if it isn't called form the UI thread
     */
    public void startBlinking() throws IllegalStateException {
        checkIfUiThread();
        if (mUiHandler != null) {
            Log.w(TAG, "Marker was already added.");
            return;
        }

        mUiHandler = new Handler();
        mUiHandler.post(mBlinkerRunnable);
    }	
	
    /**
     * <p>Stops the blinking of the marker. You sould call this method
     * at least on the onPause() of the Activity.</p>
     * @throws IllegalStateException - if it isn't called form the UI thread
     */
    public void stopBlinking() throws IllegalStateException {
        checkIfUiThread();
        if (mUiHandler == null) {
            return;
        }
        mUiHandler.removeCallbacks(mBlinkerRunnable);
        mUiHandler = null;
    }
    
    private Runnable mBlinkerRunnable = new Runnable() {
        @Override
        public void run() {
            if(mView.getVisibility() == View.VISIBLE){
            	mView.setVisibility(View.INVISIBLE);
            }else{
            	mView.setVisibility(View.VISIBLE);
            }
            mUiHandler.postDelayed(mBlinkerRunnable, 1000 / DEFAULT_FPS);
        }
    };
    
    private void checkIfUiThread() throws IllegalStateException {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            throw new IllegalStateException("This call has to be made from the UI thread.");
        }
    }
	
}
