package com.cummins.mowo.widgets;

import java.util.ArrayList;
import java.util.Map.Entry;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cummins.mowo.R;
import com.cummins.mowo.adapters.ActivityButtonsAdapter;
import com.cummins.mowo.adapters.JobsListAdapterDialog;
import com.cummins.mowo.daos.JobDao;
import com.cummins.mowo.daos.TimecardDao;
import com.cummins.mowo.model.JobsModel;
import com.cummins.mowo.vos.ActivityType;
import com.cummins.mowo.vos.ActivityTypesButtonsHolder;
import com.cummins.mowo.vos.Job;
import com.cummins.mowo.vos.TimecardEntry;

public class WhatActivityDialog extends Dialog {

	private static final String TAG = WhatActivityDialog.class.getSimpleName();
	private static final int WINDOW_SIZE_COMPACT = 1;
	private static final int WINDOW_SIZE_LARGE = 2;
	private WhatDialogListener mCallback;
	private Context context;
    private ActivityButtonsAdapter mActivityButtonsAdapter;
    private ActivityTypesButtonsHolder mActivityBtnHolder;
	private GridView mActionButtonGrid;
	private TextView mSetBtn;
	private ListView mJobList;
	private JobsListAdapterDialog jobsAdapter;	
    private Animation animAlpha;
    private TimecardEntry entry;
    private ActivityType mActivityType;
    private JobDao mJobDao;
    private ArrayList<Job> jobsDBList = new ArrayList<Job>();
	
	public interface WhatDialogListener {
		public void onActivityParametersSelected(TimecardEntry timecardEntry);
	}
	
	public WhatActivityDialog(Context context, WhatDialogListener mCallback) {
		super(context, R.style.DialogSlideAnim);
		this.getWindow().setGravity(Gravity.CENTER); 	
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.context = context;
		this.mCallback = mCallback;
		this.setContentView(R.layout.dialog_what_activity);
		//initialize new time card entry
		entry = new TimecardDao().getEmtpyEntry();	
		//initialize animation 
	    animAlpha = AnimationUtils.loadAnimation(context, R.anim.alpha_anim);	
	}
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		// create button grid 
		mActionButtonGrid = (GridView) findViewById(R.id.button_grid);
	    mActivityBtnHolder = new ActivityTypesButtonsHolder();
	    mActivityButtonsAdapter = new ActivityButtonsAdapter(context, R.layout.activity_buttons_grid_item, mActivityBtnHolder.getButtonArray());
	    mActionButtonGrid.setAdapter(mActivityButtonsAdapter);
	    
		// set action types click listener 
	    mActionButtonGrid.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3) {
				// Visually display selected activity type
				for (int i=0; i < mActionButtonGrid.getChildCount(); i++) {
					 mActionButtonGrid.getChildAt(i).setBackgroundResource(R.drawable.activity_btn_selector);
				}
				v.setBackgroundResource(R.drawable.activity_btn_background_pressed);
				// initialize selected activity type object and set it inside entry object 
			    mActivityType = mActivityBtnHolder.getButtonArray().get(position);
			    entry.setActivityTypeCode(mActivityType.getCode());
			    // set entry job to null. Job shall be selected by user again after job list refresh
			    entry.setJob(null);
				// display job list if activity type is chargeable 
			    showJobListView();
			}
		});

        // initialize SET button of the dialog window
		mSetBtn = (TextView) this.findViewById(R.id.start_activity_button);
		mSetBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {	
				// if job required validate that one was selected
				if (entry.getActivityType() != null) {
				  if (entry.getActivityType().isChargable() && entry.getJob() == null) {
					  Toast toast = Toast.makeText(context, "Please select job", Toast.LENGTH_SHORT);
					  toast.setGravity(Gravity.CENTER | Gravity.CENTER, 0, 0);
				      toast.show();
					  return;
				  }		
				}
			 	mCallback.onActivityParametersSelected(entry);
				WhatActivityDialog.this.dismiss();
			}
		});
		
		//Initialize jobListView
		mJobList = (ListView) WhatActivityDialog.this.findViewById(R.id.job_list);
		mJobList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				view.setSelected(true);
				entry.setJob(jobsAdapter.getItem(position));
			}
		});
	    
		setWindowSize(WINDOW_SIZE_LARGE);
    }
    
	@Override
	protected void onStart() {
		super.onStart();
	}
	
	/**
	 * Display job List only if activity is chargeable
	 */
	private void showJobListView() {
		if (mActivityType.isChargable()) {
			animAlpha.reset();
			mJobList.clearAnimation();
			mJobList.startAnimation(animAlpha);
			mJobList.setVisibility(View.VISIBLE);
			//JobsModel jobsModel = new JobsModel();
			
			// generate job list from the database
			mJobDao = new JobDao();
			while(jobsDBList.size() > 0) {
				jobsDBList.remove(0);
			}
			for (Job job : mJobDao.getJobs()) {
				jobsDBList.add(job);
			}
			
			jobsAdapter = new JobsListAdapterDialog(context,jobsDBList);
			mJobList.setAdapter(jobsAdapter);
			mJobList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		} else {
			mJobList.setVisibility(View.INVISIBLE);
		} 
	}
	
	/**
	 * Control dialog window size
	 */
	private void setWindowSize(int size) {
        //set the size of the dialog
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindow().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int orientation = context.getResources().getConfiguration().orientation;
        double sizeDialog = 0;
		switch (size) {
		case WINDOW_SIZE_COMPACT:
			if (orientation == 1) {
				sizeDialog = 0.40;
			} else {
				sizeDialog = 0.65;
			}
			break;
		case WINDOW_SIZE_LARGE:
			if (orientation == 1) {
				sizeDialog = 0.40;
			} else {
				sizeDialog = 0.65;
			}
		default:
		}
        int width = (int) (displaymetrics.widthPixels * 0.9);
        int height = (int) (displaymetrics.heightPixels * sizeDialog);
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = width;
        params.height = height;
        getWindow().setAttributes(params);
	}

	@Override
	public void dismiss() {
		mCallback.onActivityParametersSelected(entry);
		super.dismiss();
	}
	
	
}
