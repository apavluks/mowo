package com.cummins.mowo.activity.timecard;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.text.format.Time;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.cummins.mowo.R;
import com.cummins.mowo.functions.fDATE;
import com.cummins.mowo.vos.TimecardEntry;
import com.cummins.mowo.widgets.WhatActivityContinueDialog;

public class TimecardCurrentActivityFragment extends Fragment {

	private static final String TAG = TimecardCurrentActivityFragment.class.getSimpleName();
	private TimecardCurrentActivityFragmentListener mCallback;
	private View view;
	private TextView mStopActivityBtn;
	private TextView mDuration;
    private TextView mActiveActivityJob;
    private TextView mActiveActivityStart;
    private ImageView activityIcon;
	private long mStartTime = 0L;
	private Handler mHandler = new Handler();
	private TimecardEntry entry;
	private TimecardEntry entryContinue;
	private TextView mWhatNextBtn;
	private TextView mStartActivityButton;
	private TextView mJobContinue;
	private TextView mActivityType;
	private LinearLayout mCurrentActivityLayout;
	private Time time = new Time();
	private ImageButton mCancelButton;
	private Animation animAlpha, anim_in, anim_out;
	private ViewFlipper mViewFlipper;
	
	// setup task variable to update duration field  
	final Runnable mUpdateTimeTask = new Runnable() {
		public void run() {
			if (entry!=null) {
			   final long start = mStartTime;
			   /*long millis = SystemClock.uptimeMillis() - start;
			   int seconds = (int) (millis / 1000);
		       int minutes = seconds / 60;
			   seconds = seconds % 60;*/
			   //mDuration.setText("" + minutes + ":" + String.format("%02d", seconds));
			   long millis = SystemClock.uptimeMillis();
			   time.setToNow();
			   long now = time.toMillis(false);
			   long duration = now - entry.getTimeStart().toMillis(false);		
			   mDuration.setText(fDATE.formatIntervalHMS(duration));
			   mHandler.postDelayed(this, 500);
			}
		}
	};
	
	public interface TimecardCurrentActivityFragmentListener {
		public void onRunningActivityStopped();
		public void onContinueRunningActivity(TimecardEntry entry);
		public void onWhatNextActivityClicked(Object sourceObj);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		Log.d(TAG, "Entering TimecardCurrentActivity onCreateView");
		this.view = inflater.inflate(R.layout.current_activity, container,false);
		activityIcon = (ImageView) view.findViewById(R.id.curr_activity_icon);
		mDuration = (TextView) view.findViewById(R.id.curr_activity_duration);
		mStopActivityBtn = (TextView) view.findViewById(R.id.end_activity_button);
		mActiveActivityJob = (TextView) view.findViewById(R.id.curr_job_number);
		mActiveActivityStart = (TextView) view.findViewById(R.id.curr_activity_started);
		mWhatNextBtn = (TextView) view.findViewById(R.id.curr_activity_what);
		mStartActivityButton = (TextView) view.findViewById(R.id.start_activity_button);
		mCurrentActivityLayout = (LinearLayout) view.findViewById(R.id.current_activity_layout);
		mCancelButton = (ImageButton) view.findViewById(R.id.cancel_what_button);
		mViewFlipper = (ViewFlipper) view.findViewById(R.id.current_activity_layout_flipper);
		mJobContinue = (TextView) view.findViewById(R.id.continue_activity_job);
		mActivityType = (TextView) view.findViewById(R.id.curr_job_description);
		
		String durationFont = getActivity().getResources().getString(R.string.clock_button_font);
		Typeface tf = Typeface.createFromAsset(getActivity().getAssets(),durationFont);
		mDuration.setTypeface(tf);
		animAlpha = AnimationUtils.loadAnimation(getActivity(), R.anim.alpha_anim);	
		
		mCancelButton.setVisibility(View.INVISIBLE);
		mStartActivityButton.setVisibility(view.INVISIBLE);
		
		return view;
	}

	@Override
	public void onStart() {
		super.onStart();
		
		anim_in = AnimationUtils.loadAnimation(getActivity(), R.anim.alpha_anim_long);
		anim_out = AnimationUtils.loadAnimation(getActivity(), R.anim.alpha_anim_reverse_long);
		
		mStopActivityBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mHandler.removeCallbacks(mUpdateTimeTask);
				mCallback.onRunningActivityStopped();
			}
		});
		
		mWhatNextBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// first we slide footer up so that WHAT field and Start button are both visible 
				// when footer is slided up main fragment will call display dialog in postscroll execute. 
				mCallback.onWhatNextActivityClicked(TimecardCurrentActivityFragment.this);
				mWhatNextBtn.setSelected(true);
				mWhatNextBtn.setTextColor(getActivity().getResources().getColor(android.R.color.black));
			}
		});
		
		mStartActivityButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (entryContinue == null) {
				   showCenter("Choose activity type");	
				} else {
				   mCallback.onContinueRunningActivity(entryContinue);
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
			mCallback = (TimecardCurrentActivityFragmentListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement TimecardCurrentActivityFragment");
		}
	}

	public void stopRunning() {
		if (mHandler != null) {
	    	mHandler.removeCallbacksAndMessages(null);
		}
	}
	
    public void showLoading () {		
    	if (mViewFlipper.getDisplayedChild() == 0) {
    	   mViewFlipper.setInAnimation(anim_in);
    	   mViewFlipper.setOutAnimation(anim_out);
    	   mViewFlipper.showNext();
    	}
    
    }
    
    public void showRunning () {	
    	if (mViewFlipper.getDisplayedChild() == 1) {
    	   mViewFlipper.setInAnimation(anim_in);
    	   mViewFlipper.setOutAnimation(anim_out);
    	   mViewFlipper.showPrevious();
    	}
    }
	
	public void startRunning() {
		mStartTime = SystemClock.uptimeMillis();
		mHandler.removeCallbacks(mUpdateTimeTask); 
		mHandler.postDelayed(mUpdateTimeTask, 100);
	}
	
	/**
	 * update the activity activity values 
	 */
	public void updateRunningView() {
		if (this.entry != null) {
			mActiveActivityStart.setText(entry.getTimeStartString());
			if (entry.getJobNumberString() != null) {
				String job = "Job: " + entry.getJobNumberString();
				mActiveActivityJob.setText(job);
			} else {
				mActiveActivityJob.setText(entry.getActivityTypeString());
			}
			activityIcon.setImageDrawable(getActivity().getResources().getDrawable(entry.getActivityType().getListIcon()));
			mActivityType.setText(entry.getActivityTypeString());
			Log.d(TAG, "List icon = " + entry.getActivityType().getListIcon());
		} 
	}
	
	public void displayActivityTypeDialog() {
		// now display dialog box. 
		WhatActivityContinueDialog mActivityDialog = new WhatActivityContinueDialog(getActivity(), new WhatActivityContinueDialog.WhatDialogListener() {					
			@Override
			public void onActivityParametersSelected(TimecardEntry timecardEntry) {
				entryContinue = timecardEntry;
				entryContinue.setJob(entry.getJob());
				// if activity type was selected then update what field. 
				if (entryContinue.getActivityType() != null ) {
				   // setup string that will be display in the WHAT field
				   String whatString = entryContinue.getActivityType().getTitle();
			       mWhatNextBtn.setText(whatString);
			       mWhatNextBtn.setSelected(true);
			       mWhatNextBtn.setCompoundDrawablesWithIntrinsicBounds(entryContinue.getActivityType().getListIcon(), 0, 0, 0);
			       mJobContinue.setText(whatString +" job " + entryContinue.getJobNumberString());
			      // mActivityTypeContinue.setText(whatString);
			       mJobContinue.setCompoundDrawablesWithIntrinsicBounds(entryContinue.getActivityType().getListIcon(), 0, 0, 0);
				   mStartActivityButton.setVisibility(view.VISIBLE);
				   mCancelButton.setVisibility(view.VISIBLE);
				   mStopActivityBtn.setVisibility(view.INVISIBLE);
				} else {
					resetWhatSelection();
				}
			}
		}, entry);			
		mActivityDialog.show(); 
	}
	
	public void animateOnContinue() {
		
		float initialTranslation = 350f;

		mCurrentActivityLayout.setTranslationY(initialTranslation);
		mCurrentActivityLayout
				.animate()
				.setInterpolator(new DecelerateInterpolator(1.0f))
				.translationY(0f)
				.setDuration(1000l)
				.setListener(null);
		
		//animAlpha.reset();
		//mCurrentActivityLayout.clearAnimation();
		//mCurrentActivityLayout.startAnimation(animAlpha);
	}
	
	
    public TimecardEntry getEntry() {
		return entry;
	}

	public void setEntry(TimecardEntry entry) {
		this.entry = entry;
	}

	public void showCenter(String param) {
		Toast toast= Toast.makeText(getActivity(), param, Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER|Gravity.CENTER, 0, 0);
		toast.show();
    } 
    
    public void resetWhatSelection() {
    	entryContinue = null;
    	mWhatNextBtn.setText(getActivity().getResources().getString(R.string.continue_activity_question));
    	mWhatNextBtn.setSelected(false);
    	mWhatNextBtn.setTextColor(getActivity().getResources().getColor(android.R.color.secondary_text_dark));
    	mStartActivityButton.setVisibility(view.INVISIBLE);
    	mCancelButton.setVisibility(view.INVISIBLE);
    	mWhatNextBtn.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
    	mStopActivityBtn.setVisibility(View.VISIBLE);
    }
	
}
