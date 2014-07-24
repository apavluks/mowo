package com.cummins.mowo.adapters;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.graphics.Typeface;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.cummins.mowo.R;
import com.cummins.mowo.vos.HeaderInfo;
import com.cummins.mowo.widgets.ClockControlActivityDialog;
import com.cummins.mowo.activity.MainActivity;
import com.cummins.mowo.activity.timecardentry.TimecardEntryActivity;
import com.cummins.mowo.functions.fDATE;
import com.cummins.mowo.model.TimecardModel;

public class TimecardExpandableListAdapter extends BaseExpandableListAdapter {

	private Context context;
	private ArrayList<HeaderInfo> dateList;
	

	public TimecardExpandableListAdapter(Context context, ArrayList<HeaderInfo> dateList) {
		this.context = context;
		this.dateList = dateList;
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		ArrayList<TimecardModel> timecardList = dateList.get(groupPosition).getTimecardList();
		return timecardList.get(childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		TimecardModel tm = (TimecardModel) getChild(groupPosition,childPosition);
		long tmId = tm.getId();
		return tmId;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View view, ViewGroup parent) {

        final TimecardModel tm = (TimecardModel) getChild(groupPosition,childPosition);
		
		if (view == null) {
			LayoutInflater infalInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = infalInflater.inflate(R.layout.fragment_timecard_list_item, null);
		}	 
	    
	    String clockInString = tm.getClockInTimeString();
	    String clockOutString = tm.getClockOutTimeString();
	    String statusString = String.valueOf(tm.getTimecardStatus());
	    String timecardIdString = Integer.toString(tm.getId());
	    
	    
	    TextView clockInTextView = (TextView) view.findViewById(R.id.clock_in_texview);
	    TextView clockOutTextView = (TextView) view.findViewById(R.id.clock_out_texview);
	    TextView tcIdTextView = (TextView) view.findViewById(R.id.tcid_textview);
	    TextView statusTextView = (TextView) view.findViewById(R.id.status_textview);
	    
	    statusTextView.setFocusable(false);
	    clockInTextView.setFocusable(false);
	    clockOutTextView.setFocusable(false);	
	    tcIdTextView.setFocusable(false);
	    
	    statusTextView.setText(statusString);
	    clockInTextView.setText(clockInString);
	    clockOutTextView.setText(clockOutString);
	    tcIdTextView.setText(timecardIdString);
	    
		return view;
	}

	@Override
	public int getChildrenCount(int groupPosition) {

		ArrayList<TimecardModel> timecardList = dateList.get(groupPosition).getTimecardList();
		return timecardList.size();

	}

	@Override
	public Object getGroup(int groupPosition) {
		return dateList.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		return dateList.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public View getGroupView(final int groupPosition, boolean isLastChild, View view,
			ViewGroup parent) {

		HeaderInfo headerInfo = (HeaderInfo) getGroup(groupPosition);
		if (view == null) {
			LayoutInflater inf = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inf.inflate(R.layout.fragment_timecard_list_headline, null);
		}


		
		TextView heading = (TextView) view.findViewById(R.id.heading);
		Date date = headerInfo.getDate();
		heading.setText(fDATE.formatDateCustom(date, "EEE dd/MM"));

        String font = context.getResources().getString(R.string.timecardlistheading_font);
        Typeface tf = Typeface.createFromAsset(context.getAssets(), font);
        heading.setTypeface(tf);
		
		return view;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

}
