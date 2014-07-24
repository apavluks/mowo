package com.cummins.mowo.activity.timecard;

import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.cummins.mowo.R;
import com.cummins.mowo.functions.fDATE;
import com.cummins.mowo.vos.TimecardEntry;
import com.cummins.mowo.widgets.WhatActivityDialog;
import com.cummins.mowo.widgets.TimerManager;

public class TimecardWhatActivityFragment extends Fragment {

	private static final String TAG = TimecardWhatActivityFragment.class.getSimpleName();
	private WhatFragmentListener mCallback;
	private View view;
	private TextView mStartActivityButton;
	private TextView mWhatNextBtn;
	private ImageButton mCancelButton;
	private TimecardEntry entry;
	
	public interface WhatFragmentListener {
		public void onStartRunningActivity(TimecardEntry entry);
		public void onWhatNextActivityClicked(Object sourceObj);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		this.view = inflater.inflate(R.layout.current_activity_what, container,false);
		mWhatNextBtn = (TextView) view.findViewById(R.id.curr_activity_what);
		mStartActivityButton = (TextView) view.findViewById(R.id.start_activity_button);
		mStartActivityButton = (TextView) view.findViewById(R.id.start_activity_button);
		mCancelButton = (ImageButton) view.findViewById(R.id.cancel_what_button);
		mCancelButton.setVisibility(View.INVISIBLE);
		mStartActivityButton.setVisibility(view.INVISIBLE);
		return view;
	}

	@Override
	public void onStart() {
		super.onStart();
		mWhatNextBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// first we slide footer up so that WHAT field and Start button are both visible 
				// when footer is slided up main fragment will call display dialog in postscroll execute. 
				mCallback.onWhatNextActivityClicked(TimecardWhatActivityFragment.this);
				mWhatNextBtn.setSelected(true);
				mWhatNextBtn.setTextColor(getActivity().getResources().getColor(android.R.color.black));
			}
		});

		mStartActivityButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (entry == null) {
				   showCenter("Choose activity type");	
				} else {
				   entry.setTimeStart(fDATE.getTimeNow());
				   mCallback.onStartRunningActivity(entry);
				}
			}
		});
		
		mCancelButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				resetWhatSelection();
			}
		});
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mCallback = (WhatFragmentListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement TimecardCurrentActivityFragment");
		}
	}
	
	public void displayActivityTypeDialog() {
		// now display dialog box. 
		WhatActivityDialog mActivityDialog = new WhatActivityDialog(getActivity(), new WhatActivityDialog.WhatDialogListener() {					
			@Override
			public void onActivityParametersSelected(TimecardEntry timecardEntry) {
				entry = timecardEntry;
				// if activity type was selected then update what field. 
				if (entry.getActivityType() != null ) {
				   // setup string that will be display in the WHAT field
				   String whatString = entry.getActivityType().getTitle();
			       if (entry.getActivityType().isChargable()) {
			        	whatString = whatString + " job " + entry.getJob().getJobNumberString();
			       } 
				   mWhatNextBtn.setText(whatString);
				   
				   mWhatNextBtn.setCompoundDrawablesWithIntrinsicBounds(entry.getActivityType().getListIcon(), 0, 0, 0);
				   mWhatNextBtn.setSelected(true);
				   mStartActivityButton.setVisibility(view.VISIBLE);
				   mCancelButton.setVisibility(view.VISIBLE);
				} else {
					resetWhatSelection();
				}
			}
		});			
		mActivityDialog.show(); 
	}
	
    public void showCenter(String param) {
		Toast toast= Toast.makeText(getActivity(), param, Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER|Gravity.CENTER, 0, 0);
		toast.show();
    }   
    
    public void resetWhatSelection() {
    	entry = null;
    	mWhatNextBtn.setText(getActivity().getResources().getString(R.string.new_activity_question));
    	mWhatNextBtn.setSelected(false);
    	mWhatNextBtn.setTextColor(getActivity().getResources().getColor(android.R.color.secondary_text_dark));
    	mStartActivityButton.setVisibility(view.INVISIBLE);
    	mCancelButton.setVisibility(view.INVISIBLE);
    	mWhatNextBtn.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
    }
	
}
