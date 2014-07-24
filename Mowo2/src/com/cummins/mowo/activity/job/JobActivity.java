package com.cummins.mowo.activity.job;

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
import com.cummins.mowo.conrollers.job.JobController;
import com.cummins.mowo.conrollers.timecard.TimecardController;
import com.cummins.mowo.conrollers.timecardentry.TimecardEntryController;
import com.cummins.mowo.conrollers.timecardentry.TimecardEntryState;
import com.cummins.mowo.daos.JobDao;
import com.cummins.mowo.model.GlobalState;
import com.cummins.mowo.model.JobsModel;
import com.cummins.mowo.model.TimecardModel;
import com.cummins.mowo.vos.Job;
import com.cummins.mowo.vos.OnChangeListener;
import com.cummins.mowo.vos.PunchData;
import com.cummins.mowo.vos.TimecardEntry;
import com.cummins.mowo.widgets.ClockControlActivityDialog;
import com.cummins.mowo.widgets.ClockControlDialog;
import com.cummins.mowo.widgets.TimePickerDialog;
import com.cummins.mowo.R;
import com.doomonafireball.betterpickers.radialtimepicker.RadialPickerLayout;

public class JobActivity extends CustomActionBarActivity implements Handler.Callback{

	private static final String TAG = JobActivity.class.getSimpleName();

	public static final String PARAM_LIST_ITEM_POSITION = "LIST_POSITION";
	private TextView jobNumberButton;
	private GlobalState globalState;
	private TimecardModel model;
	private int position;
	private JobController controller;
	private EditText mJobNumberField;
	private EditText mCommentField;
	private Job job;
	private int jobId = -1;
	private JobDao dao;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_job);
        mJobNumberField = (EditText) findViewById(R.id.job_number_edit_text);
        mCommentField = (EditText) findViewById(R.id.job_comment_edit_text);
        dao = new JobDao();
        
    	job = new Job();
        controller = new JobController(job);
        controller.addOutboxHandler(new Handler(this));	
        
        /** determine to load job or create new one*/
        if (jobId > 0) {
        	controller.handleMessage(JobController.MESSAGE_POPULATE_MODEL_BY_ID, jobId);
        };
		
	}

	// update view either when model tells us to do that or when handler
	// publishes respective message
	private void updateView() {
		Log.d(TAG, "Update Job View " + job.getId());
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.job_edit, menu);
		return true;
	}

	/**
	 * Function handles response on menu bar actions
	 */

	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.timecardentry_save:

			String jobNumberString = mJobNumberField.getText().toString();
			String commentString = mCommentField.getText().toString();
			
			job.setJobNumberString(jobNumberString);
			job.setJobSubjectString(commentString);

			controller.handleMessage(JobController.MESSAGE_SAVE_JOB);

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



	@Override
	public boolean handleMessage(Message msg) {
		switch (msg.what) {
		case TimecardEntryController.MESSAGE_SAVE_COMPLETE:
			this.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					updateView();
					Log.d(TAG, "handler message , save completed ");
					showTop("Job Saved");
					//goBack();
				}
			});
			return true;
		}
		// TODO Auto-generated method stub
		return false;
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
