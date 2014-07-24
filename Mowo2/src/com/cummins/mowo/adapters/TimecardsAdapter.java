package com.cummins.mowo.adapters;

import java.util.List;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.cummins.mowo.model.TimecardModel;
import com.cummins.mowo.vos.Job;
import com.cummins.mowo.R;


public class TimecardsAdapter extends ArrayAdapter<TimecardModel> {

	private List<TimecardModel> list;
	private Context context; 	
	
	public TimecardsAdapter(Context context, List<TimecardModel> list) {
		super(context, 0, 0);
		this.list = list;
		this.context = context;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public TimecardModel getItem(int position) {
		// TODO Auto-generated method stub
		//jobList.getItem(position)
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;// list.get(position).getId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
		
		View view = inflater.inflate(R.layout.fragment_timecard_list_item, null);
		
	    TimecardModel tm = list.get(position);
	    
	    String clockInString = tm.getClockInTimeString();
	    String clockOutString = tm.getClockOutTimeString();
	    String statusString = String.valueOf(tm.getTimecardStatus());
	    String timecardIdString = Integer.toString(tm.getId());
	    
	    //TextView statusTextView = (TextView) view.findViewById(R.id.status_textview);
	    //TextView clockInTextView = (TextView) view.findViewById(R.id.clock_in_texview);
	    //TextView clockOutTextView = (TextView) view.findViewById(R.id.clock_out_texview);
	    //TextView tcIdTextView = (TextView) view.findViewById(R.id.tcid_textview);
	    
	    /*statusTextView.setFocusable(false);
	    clockInTextView.setFocusable(false);
	    clockOutTextView.setFocusable(false);	
	    tcIdTextView.setFocusable(false);
	    
	    statusTextView.setText(statusString);
	    clockInTextView.setText(clockInString);
	    clockOutTextView.setText(clockOutString);
	    tcIdTextView.setText(timecardIdString);*/
		
		return view;
	}

	@Override
	public int getViewTypeCount() {
		// TODO Auto-generated method stub
		return list.size();
	}
	
	public int getItemPosition(int id)
	{
	    for (int position=0; position<list.size(); position++)
	        if (list.get(position).getId() == id)
	            return position;
	    return 0;
	}
	
}
