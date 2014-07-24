package com.cummins.mowo.activity.job;

import com.cummins.mowo.R;
import com.cummins.mowo.activity.MainActivity;
import com.cummins.mowo.activity.job.JobHeadlinesFragment.OnJobHeadlineSelectedListener;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;


public class JobsFragment extends Fragment{
	
	
	JobsFragmentListener mCallback;
	public static final String JOBS_TAG = "jobs";
	public static final String JOB_DETAIL_TAG = "job_detail";
	private JobHeadlinesFragment mJobsFragment;
	private JobDetailsFragment mJobDetailsFragment;
	

    public interface JobsFragmentListener {
        public void onJobDetailCreated(JobDetailsFragment jd);     
        public void onJobHeaderCreated(JobHeadlinesFragment jh);     
    }	
	
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception.
        try {
            mCallback = (JobsFragmentListener) activity;
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
		
		View view = inflater.inflate(R.layout.fragment_jobs, container, false);

		return view;
	}

	@Override
	public void onStart() {
		super.onStart();
		// It isn't possible to set a fragment's id programmatically so we set a
		// tag instead and search for it using that.
		mJobsFragment = (JobHeadlinesFragment) getChildFragmentManager()
				.findFragmentByTag(JOBS_TAG);

		// We only create a fragment if it doesn't already exist.
		if (mJobsFragment == null) {
			// To programmatically add the map, we first create a
			// SupportMapFragment.
			mJobsFragment = new JobHeadlinesFragment();
			// Then we add it using a FragmentTransaction.
			FragmentTransaction fragmentTransaction = getChildFragmentManager()
					.beginTransaction();
			fragmentTransaction.add(R.id.list_anchor,
					mJobsFragment, JOBS_TAG);
			fragmentTransaction.commit();
		}
		
		mCallback.onJobHeaderCreated(mJobsFragment);
	
		mJobDetailsFragment = (JobDetailsFragment) getChildFragmentManager()
				.findFragmentByTag(JOB_DETAIL_TAG);
	
		// We only create a fragment if it doesn't already exist.
		if (mJobDetailsFragment == null) {
			// To programmatically add the map, we first create a
			// SupportMapFragment.
			mJobDetailsFragment = new JobDetailsFragment();
			// Then we add it using a FragmentTransaction.
			FragmentTransaction fragmentTransaction = getChildFragmentManager()
					.beginTransaction();
			fragmentTransaction.add(R.id.list_item_anchor,
					mJobDetailsFragment, JOB_DETAIL_TAG);
			fragmentTransaction.commit();
		}

		mCallback.onJobDetailCreated(mJobDetailsFragment);
	}
	
	
	@Override
	public void onDestroyView() {
	    super.onDestroyView();
	    JobsFragment f = (JobsFragment) getFragmentManager()
	                                         .findFragmentById(R.id.fragment_jobs_container);
	    if (f != null) 
	        getFragmentManager().beginTransaction().remove(f).commit();
	}


}
