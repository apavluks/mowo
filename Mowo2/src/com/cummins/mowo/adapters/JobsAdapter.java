package com.cummins.mowo.adapters;

import java.util.List;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.cummins.mowo.vos.Job;
import com.cummins.mowo.R;


public class JobsAdapter extends ArrayAdapter<Job> {

	private List<Job> jobList;
	private Context context; 	
	private int itemLayout;
	
	public JobsAdapter(Context context, List<Job> jobList, int itemLayout) {
		super(context, 0, 0);
		this.jobList = jobList;
		this.context = context;
		this.itemLayout = itemLayout;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return jobList.size();
	}

	@Override
	public Job getItem(int position) {
		// TODO Auto-generated method stub
		//jobList.getItem(position)
		return jobList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return jobList.get(position).getId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
		
		View view = inflater.inflate(itemLayout, null);
		
	    Job job = jobList.get(position);
	    
	 
	    
	    String jobNumber = job.getJobNumberString();
	    String jobSubject = job.getJobSubjectString();
	    
	    TextView jobNumberTextView = (TextView) view.findViewById(R.id.list_item_job_number);
	    TextView jobSubjectTextView = (TextView) view.findViewById(R.id.list_item_job_subject);
	    
	    jobNumberTextView.setFocusable(false);
	    jobSubjectTextView.setFocusable(false);
	    
	    
	    jobNumberTextView.setText(jobNumber);
	    jobSubjectTextView.setText(jobSubject);
		
		return view;
	}

	/*
	 * (non-Javadoc)
	 * @see android.widget.BaseAdapter#getViewTypeCount()
	 * getViewTypeCount() should return the number of different views your ListView contains. If all of items in your ListView are the same type, you should return 1.
	 */
	
	@Override
	public int getViewTypeCount() {
		// TODO Auto-generated method stub
		return 1; 
	}


	
}
