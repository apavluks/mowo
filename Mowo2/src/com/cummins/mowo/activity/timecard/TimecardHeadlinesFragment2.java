package com.cummins.mowo.activity.timecard;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.Time;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cummins.mowo.R;
import com.cummins.mowo.activity.MainActivity;
import com.cummins.mowo.activity.timecard.TimecardHeadlinesFragment.TimecardsHeadlineListener;
import com.cummins.mowo.adapters.TimecardExpandableListAdapter;
import com.cummins.mowo.adapters.TimecardsAdapter;
import com.cummins.mowo.conrollers.timecard.TimecardController;
import com.cummins.mowo.daos.TimecardDao;
import com.cummins.mowo.functions.fDATE;
import com.cummins.mowo.model.TimecardModel;
import com.cummins.mowo.vos.HeaderInfo;
import com.cummins.mowo.vos.OnChangeListener;
import com.cummins.mowo.vos.ReferencePeriod;
import com.cummins.mowo.widgets.ClockButtonWidget;
import com.cummins.mowo.widgets.PeriodControlWidget;


public class TimecardHeadlinesFragment2 extends Fragment implements OnClickListener{
	
	private static final String TAG = TimecardHeadlinesFragment2.class.getSimpleName();
	
	TimecardsHeadlineListener mCallback;
	
	protected Object mActionMode;
	public int selectedItem = -1;
    private View view;
	private List<TimecardModel> mPeriodTimecardsArray = new ArrayList<TimecardModel>();
	private TimecardDao timecardDao;
	private CheckBox mExpandAllChk;
	
	private TimecardExpandableListAdapter listAdapter;
	private ExpandableListView myList;

	private LinkedHashMap<Integer, HeaderInfo> mDates = new LinkedHashMap<Integer, HeaderInfo>();
	private ArrayList<HeaderInfo> mDateList = new ArrayList<HeaderInfo>();

	public interface TimecardsHeadlineListener {
		public void onTimecardListChildSelected(int tcId);
		public void onTimecardListGroupSelected(int groupPosition, String dateString);
		public void onPeriodSelected(int period);

	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		view = inflater.inflate(R.layout.fragment_timecard_expandablelist,
				container, false);
		
		
		// Just add some data to start with
		loadData();
		// get reference to the ExpandableListView
		myList = (ExpandableListView) view.findViewById(R.id.list);
		// create the adapter by passing your ArrayList data
		listAdapter = new TimecardExpandableListAdapter(getActivity(), mDateList);
		// attach the adapter to the list
		myList.setAdapter(listAdapter);
		
		myList.setOnGroupClickListener(new OnGroupClickListener() {

			@Override
			public boolean onGroupClick(ExpandableListView parent, View v,
					int groupPosition, long id) {
				
				HeaderInfo header = mDateList.get(groupPosition);
				mCallback.onTimecardListGroupSelected(groupPosition, fDATE.formatDate(header.getDate()));
				Toast t = Toast.makeText(getActivity(), "Message",
						Toast.LENGTH_SHORT);
				t.show();
				Log.d(MainActivity.DEBUGTAG, "we clicked on list item");
				
				if(myList.isGroupExpanded(groupPosition)) {
					return true;
				}
				
				return false;
			}
			
		});
		
		myList.setOnChildClickListener(new OnChildClickListener() {

			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				int tmId = (int) id;
				mCallback.onTimecardListChildSelected(tmId);
				Toast t = Toast.makeText(getActivity(), "Child clicked id = " + String.valueOf(id), Toast.LENGTH_SHORT);
				t.show();
				Log.d(MainActivity.DEBUGTAG, "we clicked on list item");
				return false;
			}
			
		});
		
		mExpandAllChk = (CheckBox) view.findViewById(R.id.timecard_list_header_all_expand_chk);

		mExpandAllChk.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (!mExpandAllChk.isChecked()) {
					collapseAll();
				} else {
					expandAll();
				}
			}
		});
	       
		return view;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onClick(View v) {

		/*	switch (v.getId()) {

		// add entry to the List
		case R.id.add:

			TimecardModel timecard = new TimecardModel();

			// add a new item to the list
			int groupPosition = addTimecard(new Date(), timecard);
			// notify the list so that changes can take effect
			listAdapter.notifyDataSetChanged();

			// collapse all groups
			collapseAll();
			// expand the group where item was just added
			myList.expandGroup(groupPosition);
			// set the current group to be selected so that it becomes visible
			myList.setSelectedGroup(groupPosition);

			break;

		// More buttons go here (if any) ...

		}*/
	}
	
	// method to expand all groups
	private void expandAll() {
		int count = listAdapter.getGroupCount();
		for (int i = 0; i < count; i++) {
			myList.expandGroup(i);
		}
	}

	// method to collapse all groups
	private void collapseAll() {
		int count = listAdapter.getGroupCount();
		for (int i = 0; i < count; i++) {
			myList.collapseGroup(i);
		}
	}
	
	 //load some initial data into out list 
	public void loadData() {

		Log.d(TAG, "entering method LoadData");
		
		Calendar c = Calendar.getInstance();
		
		Log.d(TAG, "Reference Period Start " + fDATE.formatDate(new Date(ReferencePeriod.getStartDateInMillis())));
		Log.d(TAG, "Reference Period End " + fDATE.formatDate(new Date(ReferencePeriod.getEndDateInMillis())));

		/** clear list */
		
		while (mDateList.size()>0) {
			Log.d(TAG, "Removing data from the header list");
			mDateList.remove(0);
		}

		mDates.clear();
		
		/** get data saved in the database */
		while(mPeriodTimecardsArray .size() > 0) {
			mPeriodTimecardsArray .remove(0);
		}
		
		timecardDao = new TimecardDao();
	    for (TimecardModel tm: timecardDao.getTimecards(ReferencePeriod.getStartDateInMillis(), ReferencePeriod.getEndDateInMillis())) {
	    	mPeriodTimecardsArray.add(tm);
	    }
		
        /** generate period dates as headers and add timecard entries as child notes*/
		for (int i=0; i< ReferencePeriod.getPeriodDuration(); i++) {
			c.setTime(ReferencePeriod.getStartDate());
			c.add(Calendar.DATE, i);
			Date periodDate = c.getTime();
			
            boolean matchFound = false;
			
		    for (TimecardModel m : mPeriodTimecardsArray ) {
		    	
		    	c.setTimeInMillis(m.getClockInTimeInMillis());
				c.set(Calendar.HOUR_OF_DAY, 0);
				c.set(Calendar.MINUTE, 0);
				c.set(Calendar.SECOND, 0);
				c.set(Calendar.MILLISECOND, 0);
				
				Date timecardStartDate = c.getTime();
				
				Log.d(TAG, "Period Date " + fDATE.formatDate(periodDate) + " Timecard Date " + fDATE.formatDate(timecardStartDate));
				if (periodDate.compareTo(timecardStartDate) == 0) {
					addTimecard(periodDate, m);
					matchFound = true;
				} 

	    	}
		    
		    if (!matchFound) {
		    	addTimecard(periodDate, null);
		    }
			
		}	
		
  	    if (listAdapter != null) {
  	    	Log.d(TAG, "method.LoadData listAdapter is not null ");
  	    	listAdapter.notifyDataSetChanged();
	    }
	
	}
	 
	 //here we maintain our products in various departments
	 private int addTimecard(Date date, TimecardModel timecard){
		 
	   int groupPosition = 0;
	   DateFormat df = new SimpleDateFormat("yyyyMMdd");
	   Integer dateAsNumber = Integer.parseInt(df.format(date));
	   
	   //check the hash map if the group already exists
	   HeaderInfo headerInfo = mDates.get(dateAsNumber); 
	   //add the group if doesn't exists
	   if(headerInfo == null){
	     headerInfo = new HeaderInfo();
	     headerInfo.setDate(date);
	     mDates.put(dateAsNumber, headerInfo);
	     mDateList.add(headerInfo);
	   }
	 
	  if (timecard != null) {
		// get the children for the group
		ArrayList<TimecardModel> timecardList = headerInfo.getTimecardList();
		// size of the children list
		int listSize = timecardList.size();
		// add to the counter
		listSize++;

		// create a new child and add that to the group
		timecardList.add(timecard);
		headerInfo.setTimecardList(timecardList);
	  }
	  
	  //find the group position inside the list
	  groupPosition = mDateList.indexOf(headerInfo);
	  return groupPosition;
	 }
	 	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		// This makes sure that the container activity has implemented
		// the callback interface. If not, it throws an exception.
		try {
			mCallback = (TimecardsHeadlineListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnHeadlineSelectedListener");
		}
	}
	
	@Override
	public void onDetach() {
		super.onDetach();
		mCallback = null;
	}
	 
}