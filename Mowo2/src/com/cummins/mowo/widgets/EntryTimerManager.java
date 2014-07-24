package com.cummins.mowo.widgets;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import com.cummins.mowo.R;
import com.cummins.mowo.adapters.TimecardEntriesAdapter;
import com.cummins.mowo.adapters.TimecardEntriesAdapterNew;
import com.cummins.mowo.functions.fDATE;
import com.cummins.mowo.vos.HeaderInfo;
import com.cummins.mowo.vos.TimecardEntry;
import com.cummins.mowo.widgets.swipelistview.SwipeListView;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

public class EntryTimerManager {

	private final static String TAG = EntryTimerManager.class.getSimpleName();
	
	private static int DEFAULT_FPS = 1;
	private static int DEFAULT_FREQUENCY_MILLIS = 1000;
	
	public static String FIELD_END_TIME = "END_TIME_FIELD";
	public static String FIELD_DURATION = "DURATION_FIELD";
	
	private Handler mUiHandler;
	private LinkedHashMap<Integer, EntryHolder> mEntries;
	private List<TimecardEntry> entries;
	private ListView swipeListView;
	private Context context;
	
	public EntryTimerManager(Context context,  List<TimecardEntry> entries, ListView swipeListView) {
		this.mEntries = new LinkedHashMap<Integer, EntryHolder>();
		this.entries = entries;
		this.swipeListView = swipeListView;
		this.context = context;
	}
	
    /**
     * <p>Add the view to the TimerManager. TimeManager manages multiple time indicators 
     * that need to update dynamically as time passess by.</p>
     * <p>Note! Have to be called from the UI thread.</p>
     * @param position - the position of the marker
     * @throws IllegalStateException - if it isn't called form the UI thread
     */
    public void add(int id, TextView endTimeTV, TextView durationTV, long startTime) throws IllegalStateException {
    	checkIfUiThread();
        if (!mEntries.containsKey(id)) {
        	Log.w(TAG, "Add New id " + id + " obj " + endTimeTV);
            mEntries.put(id, new EntryHolder(startTime, endTimeTV, durationTV));
        }
    }
	
    /**
     * <p>Add the view to the TimerManager. TimeManager manages multiple time indicators 
     * that need to update dynamically as time passess by.</p>
     * <p>Note! Have to be called from the UI thread.</p>
     * @param position - the position of the marker
     * @throws IllegalStateException - if it isn't called form the UI thread
     */
    public void remove(int id) throws IllegalStateException {
    	checkIfUiThread();
        if (mEntries.containsKey(id)) {
            mEntries.remove(id);
            Log.w(TAG, "removed item for id " + id);
        }
        
    }    
	
    /**
     * Get end time for specific entry id
     * @param position - the position of the marker
     * @throws IllegalStateException - if it isn't called form the UI thread
     */

	 /**
     * Starts timer. Don't forget to stop it
     * if your activity goes to the background.
     * @throws IllegalStateException - if it isn't called form the UI thread
     */
    public void startTimer() throws IllegalStateException {
        checkIfUiThread();
        if (mUiHandler != null) {
            Log.w(TAG, "Marker was already added.");
            return;
        }

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
     	
        	for (int k = 0; k < swipeListView.getChildCount(); k++) {
        	    View v = swipeListView.getChildAt(k);
        	    
            	for (int i=0; i<entries.size(); i++) {
            		
					TimecardEntry entry = entries.get(i);
					if (entry.getTimeEndString() == null) {
						Log.w(TAG, "Entry Id " + entry.getId());
						TextView tViewEnd = (TextView) v.findViewWithTag(FIELD_END_TIME + String.valueOf(entry.getId()));
						TextView tViewDuration = (TextView) v.findViewWithTag(FIELD_DURATION + String.valueOf(entry.getId()));
						if (tViewEnd != null) {
							Time time = new Time();
							time.setToNow();
							String endTimeString = fDATE.formatTimeHHMI(time);
							tViewEnd.setText(endTimeString);
							tViewEnd.setTextColor(context.getResources().getColor(R.color.swip_activte_activity_time));
				        //    if(tViewEnd.getVisibility() == View.VISIBLE){
				        //    	tViewEnd.setVisibility(View.INVISIBLE);
				        //    }else{
				        //    	tViewEnd.setVisibility(View.VISIBLE);
				        //    }
							
							long now = time.toMillis(false);
							long duration = now - entry.getTimeStart().toMillis(false);
							tViewDuration.setText(fDATE.formatIntervalHMS(duration));
					
						}
					}
					;
            		
            	}
            	
        	   

        	    }
    		
            mUiHandler.postDelayed(mRunnable, DEFAULT_FREQUENCY_MILLIS / DEFAULT_FPS);
        }
    };
    
    private void checkIfUiThread() throws IllegalStateException {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            throw new IllegalStateException("This call has to be made from the UI thread.");
        }
    }
    
    class EntryHolder {
    	
        long startTime;
        String endTimeString;
        TextView endTimeTV;
        TextView durationTV;
   
        EntryHolder(long startTime, TextView endTime, TextView duration) {
    		this.startTime = startTime;
    		this.endTimeTV = endTime;
    		this.durationTV = duration;
    	}   	

    	public long getStartTime() {
    		return startTime;
    	}
    	
    	public TextView getEndTimeTV() {
    		return endTimeTV;
    	}	
    	
    	public TextView getDurationTV() {
    		return durationTV;
    	}	    
    }
	
}
