package com.cummins.mowo.widgets;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.cummins.mowo.R;
import com.cummins.mowo.activity.MainActivity;
import com.cummins.mowo.activity.timecard.TimecardDetailsFragmentClock.TimecardsDetailClockListener;
import com.cummins.mowo.adapters.ActivityButtonsAdapter;
import com.cummins.mowo.adapters.ActivityJobOptionAdapter;
import com.cummins.mowo.adapters.JobsAdapter;
import com.cummins.mowo.animations.DownSliderAnimation;
import com.cummins.mowo.model.JobsModel;
import com.cummins.mowo.vos.ActivityType;
import com.cummins.mowo.vos.ActivityTypesButtonsHolder;
import com.cummins.mowo.vos.ActivityJobOptionHolder;
import com.cummins.mowo.vos.Job;


public class SlidingActivityOptions extends RelativeLayout {

	private static final String TAG = SlidingActivityOptions.class.getSimpleName();
	
	private SlidingActivityOptionsListener mCallback;
    private GridView mActionButtonGrid;
    private ListView mActionJobOptionGrid;
    private ActivityButtonsAdapter mActivityButtonsAdapter;
    private ActivityJobOptionAdapter mActivityJobOptionAdapter;
    private ActivityTypesButtonsHolder activityBtnHolder;
    private ActivityJobOptionHolder activityJobOptionHolder; 
    private Context mContext;
    private int mSliderHeight;
    private JobsAdapter jobsAdapter;
    private ListView mJobsList; 
    private String jobNumber;
    private String activityType;
    private String activityTypeText;
    private boolean isChargable; 
    private LinearLayout mJobSelectionContainer;
    private ViewFlipper mJobSelectionFlipper;
    private Animation animAlpha;
    private Animation animAlphaReverse;
    
    public interface SlidingActivityOptionsListener {
    	public void stateChanged(int whatBoxDrawable);
    	public void updateWhatText(String text);
    };
    
	public SlidingActivityOptions(Context context, AttributeSet attrs) {
		super(context, attrs);
        this.mContext = context;
        this.jobNumber = null;
        this.activityType = null;
        this.activityTypeText = null;
        this.isChargable = false;
        
        animAlpha = AnimationUtils.loadAnimation(mContext, R.anim.alpha_anim);
        animAlphaReverse = AnimationUtils.loadAnimation(mContext, R.anim.alpha_anim_reverse);
        
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.activity_sliding_options_layout, this, true);
		
		mActionButtonGrid = (GridView) findViewById(R.id.button_grid);
		mActionJobOptionGrid = (ListView) findViewById(R.id.button_job_option_grid);
		mJobsList = (ListView) findViewById(R.id.list_jobs);
		mJobSelectionContainer = (LinearLayout) findViewById(R.id.job_selection_container);
		mJobSelectionFlipper = (ViewFlipper) findViewById(R.id.job_selection_flippper);
		
	    // set adapter for activities buttons 
	    activityBtnHolder = new ActivityTypesButtonsHolder();  
	    Log.d(TAG, "Before setting up adapter in slidingActivityOptions");
	    mActivityButtonsAdapter = new ActivityButtonsAdapter(context, R.layout.activity_buttons_grid_item, activityBtnHolder.getButtonArray());
	    mActionButtonGrid.setAdapter(mActivityButtonsAdapter);

	    // set adapter for activities job options  
	    activityJobOptionHolder = new ActivityJobOptionHolder(context);   
	    mActivityJobOptionAdapter = new ActivityJobOptionAdapter(context, R.layout.activity_buttons_grid_job_options, activityJobOptionHolder.getButtonArray());
	    mActionJobOptionGrid.setAdapter(mActivityJobOptionAdapter);	 
	    
	    // load jobs into list
	    new LoadJobsTask().execute();
	    
		// set action types click listener 
	    mActionButtonGrid.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3) {
				unselectTypes();
				v.setBackgroundResource(R.drawable.activity_btn_background_pressed);
				ActivityType ab = activityBtnHolder.getButtonArray().get(position);
				activityType = String.valueOf(ab.getCode());
				Log.d(TAG, "activityType " + activityType);
				activityTypeText = ab.getTitle();
				updateWhatActivityField();
				//mWhatActivityBtn.setText(ab.getTitle());
				isChargable = ab.isChargable();
				if (isChargable) {
				    //mNewJobNumberBtn.setVisibility(View.VISIBLE);
					//mJobSelectionContainer.setVisibility(View.VISIBLE);
					
					animAlpha.reset();
					mJobSelectionContainer.clearAnimation();
					mJobSelectionContainer.startAnimation(animAlpha);
					mJobSelectionContainer.setVisibility(View.VISIBLE);
				} else {
					mJobSelectionContainer.setVisibility(View.INVISIBLE);
				}
			}
		});
	    
	    mActionJobOptionGrid.setOnItemClickListener(new OnItemClickListener() {
	    	@Override
	    	public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3) {
				unselectJobOptions();
				v.setBackgroundResource(R.drawable.activity_btn_job_opt_background_pressed);
				mJobSelectionFlipper.setDisplayedChild(position+1);
				Log.d(TAG, "Position clicked " + (position+1));
				
				if (mJobSelectionFlipper.getChildAt(position+1).getTag().equals("PREVIOUS_JOB")) {
					jobNumber = "1";
				} else if (mJobSelectionFlipper.getChildAt(position+1).getTag().equals("JOB_LIST")) {
					try {
						jobNumber = null;
						mJobsList.clearChoices();
						mJobsList.requestLayout();
						jobNumber = null;
					} catch (Exception e) {
						Log.d(TAG, "Print exception " + e);
					}
					
				} else if (mJobSelectionFlipper.getChildAt(position+1).getTag().equals("JOB_UNDEFINED")) {
					jobNumber = "0";
				}
				
				Log.d(TAG, "Position clicked view TAG " + mJobSelectionFlipper.getChildAt(position+1).getTag());
				Log.d(TAG, "Job Number " +jobNumber);
				updateWhatActivityField();
			}
		});
	    
	}
	
	/**
	 * provide slide down animation of the action types selection 
	 */
	public void animateSlidingMenu() {
		
		Log.d(TAG, "Is Slider Visible" + this.getVisibility());
	    if(this.getVisibility() == View.VISIBLE){
	    	animAlphaReverse.reset(); 
	    	this.clearAnimation();
			this.startAnimation(animAlphaReverse);
			this.setVisibility(View.INVISIBLE);
	        //DownSliderAnimation a = new DownSliderAnimation(this, 400, DownSliderAnimation.COLLAPSE);
	        //mSliderHeight = a.getHeight();
	        //this.startAnimation(a);
	        mCallback.stateChanged(R.drawable.ic_whatactivity_collapsed);
	    }else{
	    	
	        //DownSliderAnimation a = new DownSliderAnimation(this, 400, DownSliderAnimation.EXPAND);
	        //a.setHeight(mSliderHeight);
	        //this.startAnimation(a);
	    	animAlpha.reset();
			this.clearAnimation();
			this.startAnimation(animAlpha);
			this.setVisibility(View.VISIBLE);
	        mCallback.stateChanged(R.drawable.spinner_focused_holo_dark);
	        //setDefaultWhatActivityField();
	    }
	}
	
	/**
	 * unmark selected item on the grid so when we open next time it does not show previous selection
	 */
	public void unselect() {
		unselectTypes();
		unselectJobOptions();
		mJobSelectionFlipper.setDisplayedChild(0);
		mJobSelectionContainer.setVisibility(View.INVISIBLE);
		activityTypeText = null;
		activityType = null;
		isChargable = false;
		try {
			jobNumber = null;
			mJobsList.clearChoices();
			mJobsList.requestLayout();
			jobNumber = null;
		} catch (Exception e) {
			Log.d(TAG, "Print exception " + e);
		}		
	}
	
	public void unselectTypes() {
		for (int i=0; i < mActionButtonGrid.getChildCount(); i++) {
			 mActionButtonGrid.getChildAt(i).setBackgroundResource(R.drawable.activity_btn_selector);
		}
	}
	
	public void unselectJobOptions() {
		for (int i=0; i < mActionJobOptionGrid.getChildCount(); i++) {
			mActionJobOptionGrid.getChildAt(i).setBackgroundResource(R.drawable.activity_btn_job_opt_selector);
		}
	}	
	
	public void manageSelection() {
		
		if (activityType != null && jobNumber != null) {
			animateSlidingMenu();
		}
		
	}
	
	public void updateWhatActivityField() {
		if (activityType != null && jobNumber == null) {
			mCallback.updateWhatText(activityTypeText);
		} else if (activityType != null && jobNumber != null) {
			mCallback.updateWhatText(activityTypeText + " WO " + jobNumber);
		}	
	}
	
	public void setDefaultWhatActivityField() {
		mCallback.updateWhatText(mContext.getResources().getString(R.string.new_activity_question));
	}
	
	public void setSlidingActivityOptionsListener(SlidingActivityOptionsListener mCallBack) {
		this.mCallback = mCallBack;
	}
	
	private class LoadJobsTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... arg0) {

			try {
				JobsModel jobsModel = new JobsModel();
				Log.d(TAG, "Loading JobModel");
			    //set jobs adapter 			
			    jobsAdapter = new JobsAdapter(mContext,jobsModel.getJobsList(), R.layout.activity_job_selector_list_item);
			} catch (Exception e) {
				Log.e(TAG, "Error loading Jobs", e);
			}

			// TODO Auto-generated method stub
			return null;
		}
		
        @Override
        protected void onPostExecute(Void result) {
        	Log.d(TAG, "Setting Adapter");
		    mJobsList.setAdapter(jobsAdapter);
		    mJobsList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		    
		    mJobsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					jobNumber = jobsAdapter.getItem(position).getJobNumberString();
					updateWhatActivityField();
				}
			});
        }
	
	}

	public String getJobNumber() {
		return jobNumber;
	}

	public String getActivityType() {
		return activityType;
	}	
	
	public boolean isChargable() {
		return isChargable;
	}
	
	public void removeAdapters() {
		mActionButtonGrid.setAdapter(null);
		mActionJobOptionGrid.setAdapter(null);
		
	}
	
	public void setAdapters() {
		if (mActivityButtonsAdapter != null) {
			mActionButtonGrid.setAdapter(mActivityButtonsAdapter);
		}
		if (mActivityJobOptionAdapter != null) {
			mActionJobOptionGrid.setAdapter(mActivityJobOptionAdapter);
		}
	}
}
