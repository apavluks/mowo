package com.cummins.mowo.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cummins.mowo.R;
import com.cummins.mowo.activity.timecard.TimecardDetailsFragmentClock;

public class TimecardContainerFragment extends Fragment {
	
	private String TOP_FRAGMENT_VIEW = "topview";
	private String FRAG_ACTIVITY_TAG = "activity";
	private String topViewState;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(MainActivity.DEBUGTAG, "TimecardContainerFragment-OnCreate: begin");
         
    }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
		View view = inflater.inflate(R.layout.fragmenttimecardcontainer, container, false);
		
        FragmentTransaction ft =  getActivity().getSupportFragmentManager().beginTransaction();
	    Bundle args = new Bundle();
	    args.putInt("num", 2);
	    
	    if (savedInstanceState!=null) {
	    	Log.d(MainActivity.DEBUGTAG, "TimecardContainerFragment -> savedInstanceState is not null" + savedInstanceState );
	    	
	    	topViewState = savedInstanceState.getString(TOP_FRAGMENT_VIEW);
	    	
	    	if (topViewState.equals(FRAG_ACTIVITY_TAG)) {
	    	
	    /*	  ActivityFragment activityFragment = new ActivityFragment();
	    	  activityFragment.setArguments(args);
	    	  ft.replace(R.id.timecardfragmentcontainer, activityFragment);
	    	  ft.commit(); */
	    	  return view;
	    	}
	    }
	    
	    Log.d(MainActivity.DEBUGTAG, "TimecardContainerFragment -> Adding Timecard View to container " );
	    
	    TimecardDetailsFragmentClock timecardFragment = new TimecardDetailsFragmentClock();
	    timecardFragment.setArguments(args);
        ft.replace(R.id.timecardfragmentcontainer, timecardFragment);
       // ft.addToBackStack(null);
        ft.commit();

		return view;	
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		topViewState = FRAG_ACTIVITY_TAG;
		
		// Save the current article selection in case we need to recreate the
		// fragment
		 outState.putString(TOP_FRAGMENT_VIEW, topViewState);
	}
    
}
