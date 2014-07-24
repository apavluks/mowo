package com.cummins.mowo.activity.job;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.cummins.mowo.activity.MainActivity;
import com.cummins.mowo.activity.timecard.TimecardHeadlinesFragment2;
import com.cummins.mowo.adapters.JobsAdapter;
import com.cummins.mowo.daos.JobDao;
import com.cummins.mowo.model.JobsModel;
import com.cummins.mowo.model.TimecardModel;
import com.cummins.mowo.vos.Job;
import com.cummins.mowo.vos.ReferencePeriod;
import com.cummins.mowo.vos.TimecardEntry;
import com.cummins.mowo.R;

public class JobHeadlinesFragment extends ListFragment {
	
	private static final String TAG = JobHeadlinesFragment.class.getSimpleName();
	
    OnJobHeadlineSelectedListener mCallback;
    protected Object mActionMode;
    public int selectedItem = -1;
    private ActionMode.Callback mActionModeCallback;
    private View selectedItemView;
    private int selectedItemId = -1;
    private JobsAdapter jobsAdapter;
    private JobDao mJobDao;
    private JobsModel jobsModel;
    private ArrayList<Job> jobsList = new ArrayList<Job>();

    // The container Activity must implement this interface so the frag can deliver messages
    public interface OnJobHeadlineSelectedListener {
        /** Called by HeadlinesFragment when a list item is selected */
        public void onJobHeaderSelected(int id);
        public boolean onJobHeaderLongClick(AdapterView<?> parent, View view, int position, long id);
        
    }

    
    
   // @Override
//	public View onCreateView(LayoutInflater inflater, ViewGroup container,
//			Bundle savedInstanceState) {
	

		
		// TODO Auto-generated method stub

	//	return super.onCreateView(inflater, container, savedInstanceState);
		
	
//		return inflater.inflate(R.layout.fragment_job_list, container, false);
//	}

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

      // We need to use a different list item layout for devices older than Honeycomb
      //  int layout = android.R.layout.simple_list_item_single_choice;
      //    int layout = Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ?
      //         android.R.layout.simple_list_item_activated_1 : android.R.layout.simple_list_item_1;
      // Create an array adapter for the list view, using the Ipsum headlines array
      //setListAdapter(new ArrayAdapter<String>(getActivity(), layout, Ipsum.Headlines));
      // setListAdapter(jobsAdapter);
          
    }

    @Override
    public void onStart() {
        super.onStart();

        // When in two-pane layout, set the listview to highlight the selected list item
        // (We do this during onStart because at the point the listview is available.)
       // if (getFragmentManager().findFragmentById(R.id.article_fragment) != null) {
            getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            getListView().setBackgroundResource(R.drawable.job_list_normal);
       // }
            
        // when item is long pressed then call main activity to assess what action should be taken 
        // i.e. pass the content of the selected job number item into timesheet activity -> job number field. 
        //getListView().setOnItemLongClickListener(new OnItemLongClickListener() {

        //    @Override
        //    public boolean onItemLongClick(AdapterView<?> parent, View view,
        //        int position, long id) {
            	
        //        selectedItemView = view;
        	
            	//int position_custom = 10;
        //    	return mCallback.onJobHeaderLongClick(parent, view, position, id);
        //    }
        //}); 
    }
    
    
    @Override
	public void onResume() {
		Log.d(TAG, "entering method onResume");
		super.onResume();
		loadData();
		jobsAdapter.notifyDataSetChanged();	
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onViewCreated(view, savedInstanceState);

        jobsModel = new JobsModel();
        loadData();
	    jobsAdapter = new JobsAdapter(getActivity(),jobsList, R.layout.fragment_job_list_item);

		setListAdapter(jobsAdapter);

		ListView list = (ListView) view.findViewById(android.R.id.list);

		show("ListView in onViewCreated" + list);

		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				
				selectedItemId = (int) id;
				Log.d(TAG, "we clicked on list item id " + selectedItemId);
				mCallback.onJobHeaderSelected(selectedItemId);
				Toast t = Toast.makeText(getActivity(), "Message " + id, Toast.LENGTH_SHORT);
				t.show();

			}
		});

	} 
    
	/* 
	 * Load data from the database  
	 */
	public void loadData() {
		Log.d(TAG, "entering method LoadData");
		mJobDao = new JobDao();
		
		while(jobsList.size() > 0) {
			jobsList.remove(0);
		}
		
		for (Job job : mJobDao.getJobs()) {
			jobsList.add(job);
		}
	
	}

	public void unSelectItem() {
		selectedItemView.setBackgroundResource(R.drawable.selectorjoblistitem);
    	selectedItemView.setSelected(false);
    	
        show("Unselect Items");
    }
	
	public void SelectItem() {
		// selectedItemView.setBackgroundResource(R.drawable.selectorjoblistitemlongpress);
  
    }
	
	public String getJobNumber (int position) {
		String jobNumberString = jobsAdapter.getItem(position).getJobNumberString();
		
		return jobNumberString;
	}
    
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception.
        try {
            mCallback = (OnJobHeadlineSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnHeadlineSelectedListener");
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
    	super.onListItemClick(l, v, position, id);
    
        // Notify the parent activity of selected item
        mCallback.onJobHeaderSelected(position);
        
        show("test click");
        // Set the item as checked to be highlighted when in two-pane layout
        //getListView().setItemChecked(position, true);
    }
    
    public void deleteJob() {
    	
    	if (jobsAdapter.getCount() == 0) {
    		show("List doest not contain any items");
    		return; 
    	}
    	
    	DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
    	    @Override
    	    public void onClick(DialogInterface dialog, int which) {
    	        switch (which){
    	        case DialogInterface.BUTTON_POSITIVE:
    	            Log.d(TAG, "Positive button pressed");
    	            if (selectedItemId > -1) {
    	               //int id = (int) jobsAdapter.getItemId(getListView().getCheckedItemPosition ());
    	               mJobDao.deleteJob(selectedItemId);
    	       		   loadData();
    	    		   jobsAdapter.notifyDataSetChanged();	
    	            }
    	            
    	            if (jobsAdapter.getCount() > 0) {
    	              Log.d(TAG, "Getchecked item position " + jobsAdapter.getItemId(getListView().getCheckedItemPosition ()-1 ));
    	            }
    	            //selectedItemId = (int) jobsAdapter.getItemId(getListView().getCheckedItemPosition ());
    	            break;

    	        case DialogInterface.BUTTON_NEGATIVE:
    	            Log.d(TAG, "Negative button pressed");
    	            break;
    	        }
    	    }
    	};
    	
    	AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    	builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
    	    .setNegativeButton("No", dialogClickListener).show();
    }
    
    /*private void mActionModeCallBack () {
    	mActionModeCallback = new ActionMode.Callback() {

    	    // called when the action mode is created; startActionMode() was called
    	    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
    	      // Inflate a menu resource providing context menu items
    	      MenuInflater inflater = mode.getMenuInflater();
    	      // assumes that you have "contexual.xml" menu resources
    	      inflater.inflate(R.menu.contextual, menu);
    	      return true;
    	    }

    	    // the following method is called each time 
    	    // the action mode is shown. Always called after
    	    // onCreateActionMode, but
    	    // may be called multiple times if the mode is invalidated.
    	    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
    	      return false; // Return false if nothing is done
    	    }

    	    // called when the user selects a contextual menu item
    	    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
    	      switch (item.getItemId()) {
    	      case R.id.toast:
    	        show();
    	        // the Action was executed, close the CAB
    	        mode.finish();
    	        return true;
    	      default:
    	        return false;
    	      }
    	    }

    	    // called when the user exits the action mode
    	    public void onDestroyActionMode(ActionMode mode) {
    	      mActionMode = null;
    	      selectedItem = -1;
    	    }
    	  };
    } */
    
    private void show(String str) {
        Toast.makeText(getActivity(),
            String.valueOf(selectedItem) + " " + str, Toast.LENGTH_LONG).show();
      }
    
}