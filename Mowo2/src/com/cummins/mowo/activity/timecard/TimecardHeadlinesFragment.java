package com.cummins.mowo.activity.timecard;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.location.Address;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.cummins.mowo.activity.MainActivity;
import com.cummins.mowo.activity.timecardentry.TimecardEntryActivity;
import com.cummins.mowo.adapters.JobsAdapter;
import com.cummins.mowo.adapters.ActivityTypeSpinnerAdapter;
import com.cummins.mowo.adapters.TimePeriodSpinnerAdapter;
import com.cummins.mowo.adapters.TimecardsAdapter;
import com.cummins.mowo.daos.TimecardDao;
import com.cummins.mowo.functions.fDATE;
import com.cummins.mowo.model.JobsModel;
import com.cummins.mowo.model.TimecardModel;
import com.cummins.mowo.vos.OnChangeListener;
import com.cummins.mowo.vos.ReferencePeriod;
import com.cummins.mowo.vos.TimecardEntry;
import com.cummins.mowo.R;

public class TimecardHeadlinesFragment extends ListFragment {
	TimecardsHeadlineListener mCallback;
	
	private static final String TAG = TimecardHeadlinesFragment.class.getSimpleName();
	
	protected Object mActionMode;
	public int selectedItem = -1;
	private ActionMode.Callback mActionModeCallback;
	private View selectedItemView;
	private Spinner mPeriodSpinner;
	private TimePeriodSpinnerAdapter mTimePeriodAdapter;
	private TimecardsAdapter timecardsAdapter;
	private ListView list;
	private List<TimecardModel> mTimecardModelArray = new ArrayList<TimecardModel>();
	private List<TimecardModel> mPeriodTimecardsArray = new ArrayList<TimecardModel>();
	private TimecardDao timecardDao;
	private int selectedModelId = -2; 
	private int mPeriod = 1;
	public static final int PERIOD_CURRENT = 1;
	public static final int PERIOD_PREVIOUS = 2;
	public static final int PERIOD_DATE_RANGE = 3;

	public interface TimecardsHeadlineListener {
		/** Called by HeadlinesFragment when a list item is selected */
		public void onTimecardHeadlineSelected(int position);
		public void onPeriodSelected(int period);

	}


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override 
	public void onStart() {
		super.onStart();

		getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		getListView().setBackgroundResource(R.drawable.job_list_normal);

	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onViewCreated(view, savedInstanceState);

		mPeriodSpinner = new Spinner(getActivity()); //(Spinner) getActivity().findViewById(R.id.time_period_spinner);
		
		// populate activity type spinner
		String[] period_array = this.getResources().getStringArray(R.array.time_period_array);
		mTimePeriodAdapter = new TimePeriodSpinnerAdapter(getActivity(), 0, period_array);
		mTimePeriodAdapter.setDropDownViewResource(R.layout.fragment_time_period_spinner_item);
		
		Log.d(TAG, "mPeriodSpinner " + mPeriodSpinner);
		mPeriodSpinner.setAdapter(mTimePeriodAdapter);
		
		// set listener on period spinner 
	/*	mPeriodSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				
			}

			@Override
			public void onItemSelected(AdapterView<?> parent,
					View view, int pos, long id) {
			
				show("Listener in Spinner fired value TAG =" + view.getTag());
				String periodTagString = (String) view.getTag();
				mPeriod = Integer.parseInt(periodTagString);
				//populateList();
				//timecardsAdapter.notifyDataSetChanged();
				//mCallback.onPeriodSelected(mPeriod);
			}

		});		
	*/	
		
		populateList();
		Log.d(TAG, "class.TimecardHeadlinesFragment method.onViewCreated mTimecardModelArray size is " + mTimecardModelArray.size());
		timecardsAdapter = new TimecardsAdapter(getActivity(), mTimecardModelArray);
		

	    list = (ListView) view.findViewById(android.R.id.list);
		setListAdapter(timecardsAdapter);
		list.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);

		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				//TimecardsAdapter adapter =  (TimecardsAdapter) parent.getAdapter(); 
				//TimecardModel model = adapter.getItem(position);
				TimecardModel model = timecardsAdapter.getItem(position);
				selectedModelId = model.getId();
				mCallback.onTimecardHeadlineSelected(selectedModelId);
				Toast t = Toast.makeText(getActivity(), "Clicked on list item model id " + model.getId(),
						Toast.LENGTH_SHORT);
				t.show();			
				Log.d(MainActivity.DEBUGTAG, "we clicked on list item");
				
				/**
				 * if user clicked on saved Time card, check if any not saved
				 * Time card exists and remove it
				 */
				if (selectedModelId != -1) {
					for (int i = 0; i < mTimecardModelArray.size(); i++) {
						// the model that has been updated is matching model in
						// the list then update the adapter and refresh
						if (mTimecardModelArray.get(i).getId() == -1) {
							mTimecardModelArray.remove(i);
							timecardsAdapter.notifyDataSetChanged();
						}
					}
				}				
				
			}
		});
		
		
	
	}
	
	public void populateList() {

		while(mTimecardModelArray .size() > 0) {
			mTimecardModelArray .remove(0);
		}

		while(mPeriodTimecardsArray .size() > 0) {
			mPeriodTimecardsArray .remove(0);
		}
		
		/** set start and end and duration of the reference period */
		ReferencePeriod.setDates(mPeriod);
		
		Log.d(TAG, "Reference Period Start " + fDATE.formatDate(new Date(ReferencePeriod.getStartDateInMillis())));
		Log.d(TAG, "Reference Period End " + fDATE.formatDate(new Date(ReferencePeriod.getEndDateInMillis())));
		
		/** get data saved in the database */
		timecardDao = new TimecardDao();
		
	    for (TimecardModel tm: timecardDao.getTimecards(ReferencePeriod.getStartDateInMillis(), ReferencePeriod.getEndDateInMillis())) {
	    	mPeriodTimecardsArray.add(tm);
	    }
		Log.d(TAG, "Timecards in DB size " + mPeriodTimecardsArray.size());

		Calendar c = Calendar.getInstance();

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
					mTimecardModelArray.add(m);
					matchFound = true;
				} 
		
	    	}
		    
		    if (!matchFound) {
		    	mTimecardModelArray.add(new TimecardModel());
		    }
		}
		
		/** if timecardArray does not return any rows, then create dummy row with empty model)*/
		if (mTimecardModelArray.size() == 0) {
			mTimecardModelArray.add(new TimecardModel());
			
		}
		
		
	
	}
	
	public void addNewEntry(TimecardModel tm) {
		
		mTimecardModelArray.add(tm);
		timecardsAdapter.notifyDataSetChanged();
		
		Log.d(TAG, "class.TimecardheadlinesFragment method.addNewEntry tm.getId " + tm.getId());
		
		updateList(tm);
		
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
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		// Notify the parent activity of selected item
		mCallback.onTimecardHeadlineSelected(position);

		show("test click");
		// Set the item as checked to be highlighted when in two-pane layout
		// getListView().setItemChecked(position, true);
	}

	public void updateList(TimecardModel model) {
		
		selectedModelId = model.getId();
		
		Log.d(TAG, "entering class.TimecardHeadlinesFragment method.updateList");
		boolean updatedFlag = false;
		
		for(int i = 0; i < mTimecardModelArray.size(); i++) {
		     
			Log.d(TAG, "index " + i + " mTimecardModelArray.get(childIndex).getId() " +  mTimecardModelArray.get(i).getId() );
		     
		     // the model that has been updated is matching model in the list then update the adapter and refresh
		     if (model.getId() == mTimecardModelArray.get(i).getId()) {
		    	 
		    	 Log.d(TAG, "class.TimecardHeadlinesFragment method.updateList Match found" +  model.getId());
		    	
		    	 show("<<<<< MATCH FOUND >>>>>>");
		    	 
		    	 updatedFlag = true;
		    	 
		    	 mTimecardModelArray.get(i).consume(model);
		    	 timecardsAdapter.notifyDataSetChanged();
					int position = timecardsAdapter.getItemPosition(model.getId());
					list.setSelection(position);
					list.setItemChecked(position, true);
				
		     }

		}
		
		if (!updatedFlag) {
			
			Log.d(TAG, "class.TimecardHeadlinesFragment method.updateList Match not found");   
				
			    Log.d(TAG, "class.TimecardHeadlinesFragment reloading list" +  model.getId());

				populateList();
				
				timecardsAdapter.notifyDataSetChanged();
				
				int position = timecardsAdapter.getItemPosition(model.getId());
				list.setSelection(position);
				list.setItemChecked(position, true);
				
		}
	
		
	}
	 
	public int getSelectedModelId() {
		return selectedModelId;
	}
	
	public void postDeleteTimecard() {

		Animation anim = AnimationUtils.loadAnimation(getActivity(),android.R.anim.slide_out_right);
		anim.setDuration(500);
		
		final int position = timecardsAdapter.getItemPosition(selectedModelId);
		
		anim.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub
				
			}
			
			/** remove item from the heading list and reset selection o the next item of last item
			 *  if we happen to delete the last item. Animate the removal of the item. 
			 */
			
			@Override
			public void onAnimationEnd(Animation animation) {
		    	mTimecardModelArray.remove(position);
		        timecardsAdapter.notifyDataSetChanged();
		        
		        int positionNew  = position;
		        /** if list has no items return , otherwise check if last item was selected and re-adjust position */
                if (mTimecardModelArray.size() == 0) {
		            /** we will use -2 just in case because empty ModelID = -1 */
		        	selectedModelId = -2;
		        	return;
		        } else if (positionNew >= mTimecardModelArray.size()) {
		        	positionNew = mTimecardModelArray.size()-1;
		        } 
		        
		        selectedModelId = mTimecardModelArray.get(positionNew).getId();
				list.setSelection(positionNew);
				list.setItemChecked(positionNew, true);
				
				/** update newly selected model*/
				mCallback.onTimecardHeadlineSelected(selectedModelId);
				
			}
		});
		
		list.getChildAt(position).startAnimation(anim);
		
	}
	
	private void show(String str) {
		Toast.makeText(getActivity(), String.valueOf(selectedItem) + " " + str,
				Toast.LENGTH_LONG).show();
	}
	
}