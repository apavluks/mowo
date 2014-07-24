package com.cummins.mowo.activity.timecard;

import com.cummins.mowo.R;
import com.cummins.mowo.activity.MainActivity;
import com.cummins.mowo.activity.job.JobDetailsFragment.JobDetailListener;
import com.cummins.mowo.activity.job.JobHeadlinesFragment.OnJobHeadlineSelectedListener;
import com.cummins.mowo.vos.ReferencePeriod;
import com.cummins.mowo.widgets.PeriodControlWidget;
import com.cummins.mowo.activity.timecard.TimecardDayFragment;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;


public class TimecardsFragment extends Fragment{
	
	private static final String TAG = TimecardsFragment.class.getSimpleName();
	
	TimecardsFragmentListener mCallback;
	public static final String FRAGMENT_TAG_TC_HEADLINE = "tc_headline";
	public static final String FRAGMENT_TAG_TC_DETAIL = "tc_details";
	public static final String FRAGMENT_TAG_TC_DAY = "tc_day";
	public static final String FRAGMENT_BUNDLE_TC_ID = "TC_ID";
	private TimecardHeadlinesFragment2 mTimecardHeadlineFragment;
	private TimecardDetailFragmentManual mTimecardDetailsFragment;
	private TimecardDayFragment mTimecardDayFragment;
	private PeriodControlWidget mPreviosButton; 
	private PeriodControlWidget mNextButton;	
	private TextView mPeriodTitle;
	private int periodOffSet = 0;

    public interface TimecardsFragmentListener {
        public void onTimecardDetailCreated(TimecardDetailFragmentManual frag);   
        public void onTimecardHeadlinesCreated(TimecardHeadlinesFragment2 frag); 
        public void onTimecardsFragmentCreated(TimecardsFragment frag);
        public void onTimecardDayFragmentCreated(TimecardDayFragment frag);
        public void onTimecardPeriodChange();
    }	
	
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception.
        try {
            mCallback = (TimecardsFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(MainActivity.DEBUGTAG, "JobHeaderFragment-OnCreate: begin");
               
    }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
		View view = inflater.inflate(R.layout.fragment_timecards, container, false);

		mPreviosButton = (PeriodControlWidget) view.findViewById(R.id.previous_period_widget);
		mNextButton = (PeriodControlWidget) view.findViewById(R.id.next_period_widget);
		mPeriodTitle = (TextView) view.findViewById(R.id.period_title);
		mPeriodTitle.setText("CURRENT WEEK");
		
		mPreviosButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				periodOffSet = periodOffSet-1;
				mPeriodTitle.setText(getPeriodTitle()); 
				ReferencePeriod.setDates(periodOffSet);	
				mCallback.onTimecardPeriodChange();
			}

		});
		
		mNextButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				periodOffSet = periodOffSet+1;
				mPeriodTitle.setText(getPeriodTitle()); 
				ReferencePeriod.setDates(periodOffSet);	
				mCallback.onTimecardPeriodChange();
			}
		});		
		
		return view;
	}

	@Override
	public void onStart() {
		super.onStart();
	    
		addTimecardHeadlineFragment();
		//addTimecardDetailsFragment();
		
		mCallback.onTimecardsFragmentCreated(this);
		
		ReferencePeriod.setDates(periodOffSet);
	}
	
	
	@Override
	public void onDestroyView() {
	    super.onDestroyView();
	    TimecardsFragment f = (TimecardsFragment) getFragmentManager().findFragmentById(R.id.fragment_timecards_container);
	    if (f != null) 
	        getFragmentManager().beginTransaction().remove(f).commit();
	}
	
	public void addTimecardHeadlineFragment () {
		
		// It isn't possible to set a fragment's id programmatically so we set a
		// tag instead and search for it using that.
		
		mTimecardHeadlineFragment = (TimecardHeadlinesFragment2) getChildFragmentManager()
				.findFragmentByTag(FRAGMENT_TAG_TC_HEADLINE);

		// We only create a fragment if it doesn't already exist.
		if (mTimecardHeadlineFragment == null) {
			// To programmatically add the map, we first create a
			// SupportMapFragment.
			mTimecardHeadlineFragment = new TimecardHeadlinesFragment2();
			// Then we add it using a FragmentTransaction.
			FragmentTransaction fragmentTransaction = getChildFragmentManager()
					.beginTransaction();
			fragmentTransaction.add(R.id.list_anchor,
					mTimecardHeadlineFragment, FRAGMENT_TAG_TC_HEADLINE);
			fragmentTransaction.commit();
		}
		
		mCallback.onTimecardHeadlinesCreated(mTimecardHeadlineFragment);
		
	}
	
	public boolean isTimecardDayFragmentAdded() {
		getChildFragmentManager().executePendingTransactions();
		Log.d(TAG, "isTimecardDayFragmentAdded");
		if (mTimecardDayFragment != null) {
			Log.d(TAG, "isTimecardDayFragmentAdded after null check");
			if (mTimecardDayFragment.isAdded()) {
				Log.d(TAG, "isTimecardDayFragmentAdded isAdded");
				return true;
			}
		}
		
		return false;
	}	
	
	public boolean isTimecardDetailsFragmentAdded() {
		getChildFragmentManager().executePendingTransactions();
		if (mTimecardDetailsFragment != null) {
			if (mTimecardDetailsFragment.isAdded()) {
				return true;
			}
		}
		
		return false;
	}
	
	public void addTimecardDayFragment () {
		 Log.d(TAG, "class.TimecardsFragment method addTimecardDayFragment entering this method");
		 
		 mTimecardDayFragment = (TimecardDayFragment) getChildFragmentManager().findFragmentByTag(FRAGMENT_TAG_TC_DAY);
	
		// We only create a fragment if it doesn't already exist.
		if (mTimecardDayFragment == null) {
			// To programmatically add the map, we first create a
			// SupportMapFragment.
			mTimecardDayFragment = new TimecardDayFragment();
			// Then we add it using a FragmentTransaction.
		}

		FragmentTransaction fragmentTransaction = getChildFragmentManager()
				.beginTransaction();
		
		fragmentTransaction.replace(R.id.list_item_anchor,mTimecardDayFragment, FRAGMENT_TAG_TC_DAY);
		
		Log.d(TAG, "class.TimecardsFragment method addTimecardDayFragment replacing fragment " + mTimecardDayFragment);
		 
		fragmentTransaction.addToBackStack(null);
		fragmentTransaction.commit();

		mCallback.onTimecardDayFragmentCreated(mTimecardDayFragment);	
		
	}	
	public void addTimecardDetailsFragment (int tcId) {
		
		 Log.d(TAG, "class.TimecardsFragment method addTimecardDetailsFragment entering this method");
		 
		mTimecardDetailsFragment = (TimecardDetailFragmentManual) getChildFragmentManager()
				.findFragmentByTag(FRAGMENT_TAG_TC_DETAIL);
	
		// We only create a fragment if it doesn't already exist.
		if (mTimecardDetailsFragment == null) {
			// To programmatically add the map, we first create a
			// SupportMapFragment.
			mTimecardDetailsFragment = new TimecardDetailFragmentManual();
			// Then we add it using a FragmentTransaction.
			
	        Bundle data = new Bundle();
	        data.putInt(FRAGMENT_BUNDLE_TC_ID, tcId);
	        mTimecardDetailsFragment.setArguments(data);
		}
		
		FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
		fragmentTransaction.replace(R.id.list_item_anchor,mTimecardDetailsFragment, FRAGMENT_TAG_TC_DETAIL);
		fragmentTransaction.addToBackStack(null);
		fragmentTransaction.commit();

		mCallback.onTimecardDetailCreated(mTimecardDetailsFragment);		
	}
	

	
	private String getPeriodTitle() {
		
        String titleString = null;
        
        if (periodOffSet == 0) {
        	titleString = "CURRENT WEEK";
        } else if (periodOffSet == -1) {
        	titleString = "PREVIOUS WEEK";
        } else if (periodOffSet < 0) {
        	titleString = String.valueOf(periodOffSet * -1) + " WEEKS AGO" ;        	
        } else if (periodOffSet == 1) {
        	titleString = "NEXT WEEK";
        } else if (periodOffSet > 0) {
        	titleString = "IN " + String.valueOf(periodOffSet) + " WEEKS" ;
        }
	
		return titleString;
		
	}

}
