package com.cummins.mowo.activity.job;

import com.cummins.mowo.R;
import com.cummins.mowo.activity.job.JobHeadlinesFragment.OnJobHeadlineSelectedListener;
import com.cummins.mowo.daos.JobDao;
import com.cummins.mowo.vos.Job;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

public class JobDetailsFragment extends Fragment {
	
	private static final String TAG = JobDetailsFragment.class.getSimpleName();
	
    public final static String ARG_ID = "id";
    int mCurrentId = -1;
    private JobDao mJobDao;
    private TextView jobNumber;
    private TextView jobDescription;

    public interface JobDetailListener {
        /** Called by HeadlinesFragment when a list item is selected */
        public void onJobDetailStarted(JobDetailsFragment jobDetailFragment);
        
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, 
        Bundle savedInstanceState) {

        // If activity recreated (such as from screen rotate), restore
        // the previous article selection set by onSaveInstanceState().
        // This is primarily necessary when in the two-pane layout.
        if (savedInstanceState != null) {
            mCurrentId = savedInstanceState.getInt(ARG_ID);
        }

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_job_view, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        jobNumber = (TextView) getActivity().findViewById(R.id.job_number);
        jobDescription = (TextView) getActivity().findViewById(R.id.job_description);
        
        // During startup, check if there are arguments passed to the fragment.
        // onStart is a good place to do this because the layout has already been
        // applied to the fragment at this point so we can safely call the method
        // below that sets the article text.
        Bundle args = getArguments();
        if (args != null) {
            // Set article based on argument passed in
            updateJobView(args.getInt(ARG_ID));
        } else if (mCurrentId != -1) {
            // Set article based on saved instance state defined during onCreateView
            updateJobView(mCurrentId);
        }
        
        mJobDao = new JobDao();

    }

    public void updateJobView(int id) {
        Log.d(TAG, "Received ID = " + id);
        Job job = mJobDao.getJob(id);
        jobNumber.setText(job.getJobNumberString());
        jobDescription.setText(job.getJobSubjectString());
        mCurrentId = id;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Save the current article selection in case we need to recreate the fragment
        outState.putInt(ARG_ID, mCurrentId);
    }
}