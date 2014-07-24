package com.cummins.mowo.widgets;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.text.format.Time;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ViewFlipper;
import android.widget.ViewSwitcher;
import android.widget.FrameLayout;
import android.widget.TabHost.TabSpec;
import android.widget.TabHost.TabContentFactory;

import com.cummins.mowo.R;
import com.cummins.mowo.activity.timecard.TimecardDetailsFragmentClock.TimecardsDetailClockListener;
import com.cummins.mowo.adapters.ActivityTypeSpinnerAdapter;
import com.cummins.mowo.adapters.JobsAdapter;
import com.cummins.mowo.adapters.JobsListAdapterDialog;
import com.cummins.mowo.daos.TimecardDao;
import com.cummins.mowo.model.JobsModel;
import com.cummins.mowo.vos.Job;
import com.cummins.mowo.vos.PunchData;
import com.cummins.mowo.vos.TimecardEntry;

public class NewActivityDialog extends Dialog {

	private static final String TAG = NewActivityDialog.class.getSimpleName();
	
	private static final int WINDOW_SIZE_COMPACT = 1;
	private static final int WINDOW_SIZE_LARGE = 2;
	private static final int CALLING_MODE_EXISTING_ACTIVITY = 1;
	private static final int CALLING_MODE_NEW_ACTIVITY = 2;
	private NewActivityListener mCallback;
	private Spinner activityTypeSpinner;
	private ActivityTypeSpinnerAdapter activityTypeAdapter;
	private Context context;
	private LayoutInflater inflater;
	private LocationManager locationManager;
	private Location location;
	private Address address;
	private String addressText;
	
	private TextView mStartActivityBtn;
	private TextView mCancelBtn;
	private ListView mJobList;
	private TextView mSelectJob;
	private String activityTypeString;
	private TimecardEntry mTimecardEntry;
	private String jobRequiredFlag = "N";
	private JobsListAdapterDialog jobsAdapter;
	private int callingMode = 0;
	private String existingActivityType;
	
	public interface NewActivityListener {
		public void onActivityParametersSelected(TimecardEntry timecardEntry);
	}
	
	public NewActivityDialog(Context context, NewActivityListener mCallback) {
		super(context, R.style.DialogSlideAnim);
		this.getWindow().setGravity(Gravity.TOP); 		
		this.context = context;
		this.mCallback = mCallback;
	    this.inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
		this.setContentView(R.layout.dialog_new_activity);
		this.callingMode = CALLING_MODE_NEW_ACTIVITY;
		this.setTitle("Start New Activity");
        //initialize new time card entry
		mTimecardEntry = new TimecardDao().getEmtpyEntry();

	}
	
	public NewActivityDialog(Context context, NewActivityListener mCallback, String existingActivityType, TimecardEntry entry) {
	    this(context, mCallback);
		this.existingActivityType = existingActivityType;
		this.callingMode = CALLING_MODE_EXISTING_ACTIVITY;
		this.setTitle("Set Entry Details");
		this.mTimecardEntry = entry;
	}
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
		//unit jobListView
		mJobList = (ListView) NewActivityDialog.this.findViewById(R.id.job_list);
        
        // init activity type spinner
		activityTypeSpinner = (Spinner) this.findViewById(R.id.spinner_activitytype);
		String[] activity_type_array = context.getResources().getStringArray(R.array.activity_types_array);
		activityTypeAdapter = new ActivityTypeSpinnerAdapter(context, 0,activity_type_array);
		activityTypeAdapter.setDropDownViewResource(R.layout.fragment_activity_type_spinner_item);
		activityTypeSpinner.setAdapter(activityTypeAdapter);

		int existingValuePosition = 0;
		int counter = 0;
		
		//set activity type value if dialog called from the existing activity
		if (callingMode == CALLING_MODE_EXISTING_ACTIVITY) {

			String[] existingActivityTypeString = existingActivityType.split(" ", -1);
			Log.d(TAG, "Leng " + existingActivityTypeString.length);
			// if key can be found match it with keys in the activity type array 
			if (existingActivityTypeString.length > 1) {
				String existingKey = existingActivityTypeString[0];
				String existingValue = existingActivityTypeString[1];

				for (String s : activity_type_array) {
					String[] splittedString = s.split("-", -1);
					String key = splittedString[0];
					String value = splittedString[1];

					if (existingKey.equals(key)) {
						existingValuePosition = counter;
					}
					counter++;
				}
				
				// set spinner value based on the matched key 
				activityTypeSpinner.setSelection(existingValuePosition);
			}
		}
	
		activityTypeSpinner
				.setOnItemSelectedListener(new OnItemSelectedListener() {
					@Override
					public void onNothingSelected(AdapterView<?> parent) {
					}
					@Override
					public void onItemSelected(AdapterView<?> parent,
							View view, int pos, long id) {
						//model.setEntryActivityType(position,activityTypeAdapter.getItem(pos));
				  		String spinnerValue = activityTypeAdapter.getItem(pos);
				  		String[] splittedString = spinnerValue.split("-");
				  		String key = splittedString[0];
				  		String value = splittedString[1];
				  		jobRequiredFlag = splittedString[2];
					    activityTypeString = key + " " + value;
					    showJobListView();
        
					}
				});

		mStartActivityBtn = (TextView) this.findViewById(R.id.start_activity_button);
		
		mStartActivityBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {

				Job job = null;
				
				// if job required validate that one was selected
				if (jobRequiredFlag.equals("Y")) {

					int pos = mJobList.getCheckedItemPosition();
					
					if (pos >= 0) {
						job = (Job) jobsAdapter.getItem(pos);
					}

					if (job == null) {

						Toast toast = Toast.makeText(context,
								"Please select job", Toast.LENGTH_SHORT);
						toast.setGravity(Gravity.CENTER | Gravity.CENTER, 0, 0);
						toast.show();
						return;
					}
					
					mTimecardEntry.setJobNumberString(job.getJobNumberString());

				} else {
					mTimecardEntry.setJobNumberString(null);
				}

				mTimecardEntry.setActivityTypeString(activityTypeString);
				mCallback.onActivityParametersSelected(mTimecardEntry);
				NewActivityDialog.this.dismiss();

			}
		});
		
        //set dialog window size; 
        showJobListView();

    }
    
    
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();

	}
	
	private void showJobListView() {
		if (jobRequiredFlag.equals("Y")) {
			mJobList.setVisibility(View.VISIBLE);
			JobsModel jobsModel = new JobsModel();
			jobsAdapter = new JobsListAdapterDialog(context,jobsModel.getJobsList());
			mJobList.setAdapter(jobsAdapter);
			mJobList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
			setWindowSize(WINDOW_SIZE_LARGE);
		} else {
			mJobList.setVisibility(View.INVISIBLE);
			setWindowSize(WINDOW_SIZE_COMPACT);
		} 
	}
	
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
        int width = (int) (displaymetrics.widthPixels * 1);
        int height = (int) (displaymetrics.heightPixels * sizeDialog);
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = width;
        params.height = height;
        getWindow().setAttributes(params);
	}
}
