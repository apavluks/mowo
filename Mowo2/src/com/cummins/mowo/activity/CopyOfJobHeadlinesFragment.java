package com.cummins.mowo.activity;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.cummins.mowo.adapters.JobsAdapter;
import com.cummins.mowo.model.JobsModel;
import com.cummins.mowo.R;

public class CopyOfJobHeadlinesFragment extends Fragment {
    OnJobHeadlineSelectedListener mCallback;
    protected Object mActionMode;
    public int selectedItem = -1;
    private ActionMode.Callback mActionModeCallback;
    private View selectedItemView;
    private ListView listView;

    // The container Activity must implement this interface so the frag can deliver messages
    public interface OnJobHeadlineSelectedListener {
        /** Called by HeadlinesFragment when a list item is selected */
        public void onJobHeaderSelected(int position);
        public boolean onJobHeaderLongClick(AdapterView<?> parent, View view, int position, long id);
    }

    
    
   // @Override
	//public View onCreateView(LayoutInflater inflater, ViewGroup container,
	//		Bundle savedInstanceState) {
		// TODO Auto-generated method stub
//		return inflater.inflate(R.layout.fragment_job_list, container, false);
	//}

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // We need to use a different list item layout for devices older than Honeycomb
      //  int layout = android.R.layout.simple_list_item_single_choice;
          int layout = Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ?
               android.R.layout.simple_list_item_activated_1 : android.R.layout.simple_list_item_1;

        JobsModel jobsModel = new JobsModel(); 
        
       // JobsAdapter jobsAdapter = new JobsAdapter(getActivity(), jobsModel.getJobsList());
        
        Log.d(MainActivity.DEBUGTAG,"Job List size " + jobsModel.getJobsList().size());
        
        // Create an array adapter for the list view, using the Ipsum headlines array
       // setListAdapter(new ArrayAdapter<String>(getActivity(), layout, Ipsum.Headlines));
        
       listView = new ListView(getActivity());
       //setListAdapter(jobsAdapter);
        
        
        
    }

    
}