package com.cummins.mowo.activity.timecardentry;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.format.Time;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.cummins.mowo.activity.CustomActionBarActivity;
import com.cummins.mowo.activity.MainActivity;
import com.cummins.mowo.activity.timecard.TimecardDetailsFragmentClock;
import com.cummins.mowo.adapters.JobsAdapter;
import com.cummins.mowo.adapters.ActivityTypeSpinnerAdapter;
import com.cummins.mowo.adapters.TimePeriodSpinnerAdapter;
import com.cummins.mowo.conrollers.timecard.TimecardController;
import com.cummins.mowo.conrollers.timecardentry.TimecardEntryController;
import com.cummins.mowo.conrollers.timecardentry.TimecardEntryState;
import com.cummins.mowo.model.GlobalState;
import com.cummins.mowo.model.JobsModel;
import com.cummins.mowo.model.TimecardModel;
import com.cummins.mowo.vos.OnChangeListener;
import com.cummins.mowo.vos.PunchData;
import com.cummins.mowo.vos.TimecardEntry;
import com.cummins.mowo.widgets.ClockControlActivityDialog;
import com.cummins.mowo.widgets.ClockControlDialog;
import com.cummins.mowo.widgets.TimePickerDialog;
import com.cummins.mowo.R;
import com.doomonafireball.betterpickers.radialtimepicker.RadialPickerLayout;

public class TimecardEntryActivity extends CustomActionBarActivity implements
		OnChangeListener<TimecardModel>, Handler.Callback {

	private static final String TAG = TimecardEntryActivity.class
			.getSimpleName();

	public static final String PARAM_LIST_ITEM_POSITION = "LIST_POSITION";
	private final static int SET_START_FIELD = 1;
	private final static int SET_END_FIELD = 2;
	private ActionBar actionBar;
	private Spinner activityTypeSpinner;
	private JobsAdapter jobsAdapter;
	private TextView jobNumberButton;
	private TextView actClockIn;
	private TextView actClockOut;
	private String activityTypeString;
	private EditText commentEditText;
	private TextView mDuration;
	private GlobalState globalState;
	private TimecardModel model;
	private TimecardEntry timecardEntry;
	private String activityTypeTag;
	private String VIEW_VALUE_EMPTY = "EMPTY";
	private int position;
	private ActivityTypeSpinnerAdapter activityTypeAdapter;
	private List<TimecardEntry> timecardEntryList;
	private Time timeStart;
	private Time timeEnd;
	private TimecardEntryController controller;
    private Time newTime;
    private RadioGroup allocationRadioGroup;
    private LinearLayout jobContainer;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		setContentView(R.layout.fragment_entry_activity);
		actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		//actionBar.hide();

		// get model and global state
		globalState = (GlobalState) getApplication();
		model = globalState.getTimecardModel();
		timecardEntryList = new ArrayList<TimecardEntry>();

		// initialize controller for this activity
		controller = new TimecardEntryController(model);
		controller.addOutboxHandler(new Handler(this));

		actionBar.setTitle("Timecard (" + model.getId() + ") - Edit activity ");

		// initialize simple GUI elements
		actClockIn = (TextView) this.findViewById(R.id.clock_in_button_entryscreen);
		actClockOut = (TextView) this.findViewById(R.id.clock_out_button_entryscreen);
		mDuration = (TextView) this.findViewById(R.id.duration_entryscreen);
		activityTypeSpinner = (Spinner) this.findViewById(R.id.spinner_activitytype);
		commentEditText = (EditText) this.findViewById(R.id.jobcomments_edittext);
		jobNumberButton = (TextView) this.findViewById(R.id.job_number_button);
		allocationRadioGroup = (RadioGroup) this.findViewById(R.id.radio_allocation);
		jobContainer = (LinearLayout) this.findViewById(R.id.job_number_container);

		// populate activity type spinner
		String[] activity_type_array = this.getResources().getStringArray(R.array.activity_types_array);
		activityTypeAdapter = new ActivityTypeSpinnerAdapter(this, 0,activity_type_array);
		activityTypeAdapter.setDropDownViewResource(R.layout.fragment_activity_type_spinner_item);
		activityTypeSpinner.setAdapter(activityTypeAdapter);

		Intent intent = getIntent();
		position = intent.getIntExtra(PARAM_LIST_ITEM_POSITION, -1);
		timecardEntry = model.getTimecardEntryList().get(position);

		// set listeners
		activityTypeSpinner
				.setOnItemSelectedListener(new OnItemSelectedListener() {
					@Override
					public void onNothingSelected(AdapterView<?> parent) {

					}

					@Override
					public void onItemSelected(AdapterView<?> parent,
							View view, int pos, long id) {

						model.setEntryActivityType(position,
								activityTypeAdapter.getItem(pos));
					}

				});

		jobNumberButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// mCallback.onJobSelectButtonPressed();
				displayJobNumberDialog();
			}
		});

		// set listener on activity start button 
        actClockIn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				// handle end time change through the dialog control
				selectNewTime(timecardEntry, SET_START_FIELD);
				
			}
		});
        
		// set listener on activity start button 
        actClockOut.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				// allow to set time if activity is ended, otherwise allow to end activity
				if (timecardEntry.getTimeEndString() != null) {
					
					// handle end time change through the dialog control
					selectNewTime(timecardEntry, SET_END_FIELD);
					
				} else {

					AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(TimecardEntryActivity.this);

					// set title
					alertDialogBuilder.setTitle("End Activity - " + timecardEntry.getActivityTypeString() + " " + timecardEntry.getJobNumberString());

					// set dialog message
					alertDialogBuilder
							.setMessage("Activity started at " + timecardEntry.getTimeStartString() + ". End this activity now?")
							.setCancelable(false)
							.setPositiveButton("Yes",
									new DialogInterface.OnClickListener() {
										public void onClick(DialogInterface dialog, int id) {
											// set end date and save model
											timecardEntry.setTimeEndNow();
											controller.handleMessage(TimecardEntryController.MESSAGE_SAVE_MODEL, position);
										}
									})
							.setNegativeButton("Cancel",
									new DialogInterface.OnClickListener() {
										public void onClick(DialogInterface dialog, int id) {
											// if this button is clicked, just close
											// the dialog box and do nothing
											dialog.cancel();
										}
									});
					
					// create alert dialog
					AlertDialog alertDialog = alertDialogBuilder.create();

					// show it
					alertDialog.show();
				}
				
			}
		});
        
        allocationRadioGroup.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// get selected radio button from radioGroup
                int selectedId = allocationRadioGroup.getCheckedRadioButtonId();
                // find the radiobutton by returned id
                 RadioButton radioAllocationButton = (RadioButton) findViewById(selectedId);
 
                 showTop(String.valueOf(radioAllocationButton.getText()));
			}
		});
        
		
		// finally update view
		updateView();

	}

	// trigger whenever model changes
	@Override
	public void onChange(TimecardModel model) {
		this.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				updateView();
			}
		});

	}

	// update view either when model tells us to do that or when handler
	// publishes respective message
	private void updateView() {

		timecardEntry = model.getTimecardEntryList().get(position);

		actClockIn.setText(timecardEntry.getTimeStartString());
		actClockOut.setText(timecardEntry.getTimeEndString());
		mDuration.setText(timecardEntry.getDurationString());

		// get position of the activity type in spinner array and default
		// spinner to that value
		int pos = activityTypeAdapter.getPosition(timecardEntry
				.getActivityTypeString());
		activityTypeSpinner.setSelection(pos);

		jobNumberButton.setText(timecardEntry.getJobNumberString());
		commentEditText.setText(timecardEntry.getCommentString());
	
		
		if (!timecardEntry.isBillable()) {
			//jobContainer.setVisibility(View.INVISIBLE);
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.timecard_activity, menu);
		return true;
	}

	/**
	 * Function handles response on menu bar actions
	 */

	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.timecardentry_save:

			String jobNumberString = jobNumberButton.getText().toString();
			model.getTimecardEntryList().get(position)
					.setJobNumberString(jobNumberString);
			String commentString = commentEditText.getText().toString();
			model.getTimecardEntryList().get(position)
					.setCommentString(commentString);

			controller.handleMessage(
					TimecardEntryController.MESSAGE_SAVE_MODEL, position);

			break;

		// Respond to the action bar's Up/Home button
		case android.R.id.home:
			goBack();

			return true;

		default:
			break;
		}

		return super.onOptionsItemSelected(item);

	}

	/**
	 * helper function to retun back
	 */

	private void goBack() {
		// http://stackoverflow.com/questions/18906116/which-one-to-use-navutils-navigateupfromsametask-vs-onbackpressed
		Intent intent = NavUtils.getParentActivityIntent(this);
		intent.putExtra(TimecardDetailsFragmentClock.TC_ID, 26);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		NavUtils.navigateUpTo(this, intent);
	}

	/**
	 * pop up job selection dialog
	 */

	protected void displayJobNumberDialog() {

		JobsModel jobsModel = new JobsModel();
		final JobsAdapter jobsAdapter = new JobsAdapter(this,
				jobsModel.getJobsList(), R.layout.fragment_job_list_item);

		AlertDialog.Builder builderSingle = new AlertDialog.Builder(
				TimecardEntryActivity.this);
		builderSingle.setTitle("Select Job");

		builderSingle.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});

		builderSingle.setAdapter(jobsAdapter,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						String jobNumberString = jobsAdapter.getItem(which)
								.getJobNumberString();
						jobNumberButton.setText(jobNumberString);
						jobNumberButton.setTag(jobNumberString);

						jobNumberButton.setTextColor(getResources().getColor(
								R.color.textEdit));

					}
				});
		builderSingle.show();

	}

	/**
	 * Handle messages called by worker handler in controller state
	 */

	@Override
	public boolean handleMessage(Message msg) {
		switch (msg.what) {
		case TimecardEntryController.MESSAGE_SAVE_COMPLETE:
			this.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					updateView();
					Log.d(TAG,
							"class.TimecardEntryStage method.saveEntry fire MESSAGE_SAVE_COMPLETE rows saved ");
					globalState.setTimecardModel(model);
					showTop("Entry Saved");
					//goBack();
				}
			});
			return true;
		}
		// TODO Auto-generated method stub
		return false;
	}

	
    public void selectNewTime(final TimecardEntry item, final int field) {
		
		Log.d(TAG, "Entering method setTime");
		
		// decide if we are dealing with start or end time
		switch(field) {
		case SET_START_FIELD:
			// there should always be a start time in the model
			newTime = item.getTimeStart();
			Log.d(TAG, "Existing start time " + newTime.HOUR + ":" + newTime.MINUTE + " stirngval " + item.getTimeStartString());
			break;
		case SET_END_FIELD:
		    // if end time is null that means activity is running to set it to the current time.
			if (item.getTimeEndString() == null) {
				newTime.setToNow();
			} else {
				newTime = item.getTimeEnd();
			}
			Log.d(TAG, "Existing end time " + newTime.HOUR + ":" + newTime.MINUTE + " stirngval " + item.getTimeEndString());
			break;
		}
		
		// instantiate timePicker class
		TimePickerDialog timePicker = new TimePickerDialog();
		
		//set initial position of the time picker 
		timePicker.setStartTime(newTime.hour, newTime.minute);
		
		// set call back listener on what happens when time is set 
		TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {

			@Override
			public void onTimeSet(RadialPickerLayout view, int hourOfDay,
					int minute) {
				
				// set new time 
				newTime.set(0, minute, hourOfDay,  newTime.monthDay, newTime.month, newTime.year);
				
				// set field value 
				if (field == SET_START_FIELD) {
				    item.setTimeStart(newTime);
				} else if (field == SET_END_FIELD) {
					item.setTimeEnd(newTime);
				}
				
				// save model to apply time change
				controller.handleMessage(TimecardController.MESSAGE_SAVE_MODEL,  position);
				
			}
		};
		
		timePicker.setOnTimeSetListener(onTimeSetListener);
		
		
		// show time picker dialog
		timePicker.show(getSupportFragmentManager(), "test");
		
	}
	
	/**
	 * 
	 * helper function to show toasts on the top of the screen
	 */

	private void showTop(String param) {
		Toast toast = Toast.makeText(this, param, Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.TOP | Gravity.CENTER, 0, 0);
		toast.show();
	}
	
	
	
}
