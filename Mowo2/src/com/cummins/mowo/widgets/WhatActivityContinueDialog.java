package com.cummins.mowo.widgets;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.cummins.mowo.R;
import com.cummins.mowo.adapters.ActivityButtonsAdapter;
import com.cummins.mowo.daos.TimecardDao;
import com.cummins.mowo.vos.ActivityType;
import com.cummins.mowo.vos.ActivityTypesButtonsHolder;
import com.cummins.mowo.vos.TimecardEntry;

public class WhatActivityContinueDialog extends Dialog {

	private static final String TAG = WhatActivityContinueDialog.class.getSimpleName();
	private static final int WINDOW_SIZE_COMPACT = 1;
	private static final int WINDOW_SIZE_LARGE = 2;
	private WhatDialogListener mCallback;
	private Context context;
    private ActivityButtonsAdapter mActivityButtonsAdapter;
    private ActivityTypesButtonsHolder mActivityBtnHolder;
	private GridView mActionButtonGrid;
	private TextView mSetBtn;
    private Animation animAlpha;
    private TimecardEntry entryContinue;
    private TimecardEntry entryRunning;
    private ActivityType mActivityType;
    private TextView mDialogTitle;
    private TextView mCompletedActivity;
	
	public interface WhatDialogListener {
		public void onActivityParametersSelected(TimecardEntry timecardEntry);
	}
	
	public WhatActivityContinueDialog(Context context, WhatDialogListener mCallback, TimecardEntry entry) {
		super(context, R.style.DialogSlideAnim);
		this.getWindow().setGravity(Gravity.CENTER); 	
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.context = context;
		this.mCallback = mCallback;
		this.setContentView(R.layout.dialog_what_activity_continue);
		//initialize new time card entry
		this.entryRunning = entry;
		this.entryContinue = new TimecardDao().getEmtpyEntry();	 
				
		this.entryContinue.setJob(entry.getJob());
		this.entryContinue.setJobNumberString(entry.getJobNumberString());
		
		//initialize animation 
	    animAlpha = AnimationUtils.loadAnimation(context, R.anim.alpha_anim);	
	}
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDialogTitle = (TextView) findViewById(R.id.dialog_title);
        mCompletedActivity = (TextView) findViewById(R.id.completed_activity);
        
        mDialogTitle.setText("CONTINUE JOB " + entryRunning.getJobNumberString());
        mCompletedActivity.setText(entryRunning.getActivityTypeString());
        mCompletedActivity.setCompoundDrawablesWithIntrinsicBounds(entryRunning.getActivityType().getListIcon(), 0, 0, 0);
        
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
			    entryContinue.setActivityTypeCode(mActivityType.getCode());
			    
			 	mCallback.onActivityParametersSelected(entryContinue);
				WhatActivityContinueDialog.this.dismiss();
			}
		});

        // initialize SET button of the dialog window
		/*mSetBtn = (TextView) this.findViewById(R.id.start_activity_button);
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
				WhatActivityContinueDialog.this.dismiss();
			}
		});*/
		
		setWindowSize(WINDOW_SIZE_LARGE);
    }
    
	@Override
	protected void onStart() {
		super.onStart();
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
		mCallback.onActivityParametersSelected(entryContinue);
		super.dismiss();
	}
	
	
}
