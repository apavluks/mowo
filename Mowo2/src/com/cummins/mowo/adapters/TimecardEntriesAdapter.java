package com.cummins.mowo.adapters;

import java.util.List;

import android.content.Context;
import android.database.DataSetObserver;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.cummins.mowo.activity.MainActivity;
import com.cummins.mowo.vos.TimecardEntry;
import com.cummins.mowo.R;

public class TimecardEntriesAdapter extends ArrayAdapter<TimecardEntry> {
	private List<TimecardEntry> timecardEntryList;
	private Context context;
	
	public TimecardEntriesAdapter(Context context, List<TimecardEntry> timecardEntryList) {
		super(context, R.layout.fragment_entry_list_item, timecardEntryList);
		this.timecardEntryList = timecardEntryList;
		this.context = context;
		
	} 
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return timecardEntryList.size();
	}

	@Override
	public TimecardEntry getItem(int position) {
		// TODO Auto-generated method stub
		//jobList.getItem(position)
		return timecardEntryList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0; //jobList.getItem(position).getId();
	}
	
	

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
		
		View view = inflater.inflate(R.layout.fragment_entry_list_item, null);
		
	    TimecardEntry entryActivity = timecardEntryList.get(position);
	 
	    String timeStartString = entryActivity.getTimeStartString();
	    String timeEndString = entryActivity.getTimeEndString();
	    String durationString = entryActivity.getDurationString();
	    String jobNumberString = entryActivity.getJobNumberString();
	    String activityTypeString = entryActivity.getActivityTypeString();
	    
	    //String jobSubject = job.getJobSubjectString();
	    TextView timeStartTV = (TextView) view.findViewById(R.id.actual_start_time);
	    TextView timeEndTV = (TextView) view.findViewById(R.id.actual_end_time);
	    TextView durationTV = (TextView) view.findViewById(R.id.duration_textview);
	    TextView jobNumberTV = (TextView) view.findViewById(R.id.job_number_textview);
	    TextView activityTypeTV = (TextView) view.findViewById(R.id.activity_type_textview);
	    
	    //jobNumberTV.setFocusable(false);
	    //actualStartTime.setFocusable(false);
	    
	    timeStartTV.setText(timeStartString);
	    timeEndTV.setText(timeEndString);
	    durationTV.setText(durationString);
	    jobNumberTV.setText(jobNumberString);
	    activityTypeTV.setText(activityTypeString);
	   // jobSubjectTextView.setText(jobSubject);
	    
	/*	if (timecardEntryList.size() - 1 == position) {
			Animation animation = AnimationUtils.loadAnimation(context,R.anim.slide_top_to_bottom);
			view.startAnimation(animation);
		}
		*/
		
		return view;
	}

	@Override
	public int getViewTypeCount() {
		//Since we have only one "type" of list view item use this 
		return 1;
	}
	
}
