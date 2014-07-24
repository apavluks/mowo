package com.cummins.mowo.activity;

import java.text.DateFormat;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import android.text.GetChars;
import android.view.ActionMode;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Toast;

import com.cummins.mowo.activity.TabsAdapter.TabInfo;
import com.cummins.mowo.activity.job.JobActivity;
import com.cummins.mowo.activity.job.JobDetailsFragment;
import com.cummins.mowo.activity.job.JobHeadlinesFragment;
import com.cummins.mowo.activity.job.JobsFragment;
import com.cummins.mowo.activity.swipelistview.SettingsActivity;
import com.cummins.mowo.activity.swipelistview.SwipeListViewExampleActivity;
import com.cummins.mowo.activity.timecard.TimecardDetailFragmentManual;
import com.cummins.mowo.activity.timecard.TimecardCurrentActivityFragment.TimecardCurrentActivityFragmentListener;
import com.cummins.mowo.activity.timecard.TimecardDetailFragmentManual.TimecardsDetailManualListener;
import com.cummins.mowo.activity.timecard.TimecardDetailsFragmentClock;
import com.cummins.mowo.activity.timecard.TimecardDayFragment;
import com.cummins.mowo.activity.timecard.TimecardHeadlinesFragment;
import com.cummins.mowo.activity.timecard.TimecardHeadlinesFragment2;
import com.cummins.mowo.activity.timecard.TimecardWhatActivityFragment.WhatFragmentListener;
import com.cummins.mowo.activity.timecard.TimecardsFragment;
import com.cummins.mowo.model.GlobalState;
import com.cummins.mowo.model.TimecardModel;
import com.cummins.mowo.vos.TimecardEntry;
import com.cummins.mowo.R;
import com.cummins.mowo.R.menu;

public class MainActivity extends CustomActionBarActivity implements
        JobsFragment.JobsFragmentListener,
        JobHeadlinesFragment.OnJobHeadlineSelectedListener,
		TimecardsFragment.TimecardsFragmentListener,
		TimecardHeadlinesFragment2.TimecardsHeadlineListener,
		TimecardDetailFragmentManual.TimecardsDetailManualListener, 
		TimecardDetailsFragmentClock.TimecardsDetailClockListener, 
		TimecardDayFragment.TimecardsDetailManualDayListener,
		TimecardCurrentActivityFragmentListener, 
		WhatFragmentListener
        { // FragmentActivity{

	public static final String DEBUGTAG = "MOWO";
	private static final String TAG = MainActivity.class.getSimpleName();
	private static final int TAB_CLOCK = 1;
	private static final int TAB_JOBS = 2;
	private static final int TAB_TIMESHEET = 3;
	
	//ViewPager mViewPager;
	private FragmentManager fragmentManager;
	private DemoCollectionPagerAdapter mDemoCollectionPagerAdapter;
	private TabsAdapter mTabsAdapter;
	private MyPageAdapter pageAdapter;
	private ActionBar actionBar; 
	private GlobalState globalState;
	private ViewPager mViewPager;
	private JobDetailsFragment mJobDetailsFragment;
	private JobHeadlinesFragment mJobHeaderFragment;
	private TimecardDetailFragmentManual mTimecardDetailFragment;
	private TimecardDetailsFragmentClock mTimecardDetailFragmentClock;
	private TimecardHeadlinesFragment2 mTimecardHeadlinesFragment;
	private TimecardsFragment mTimecardsFragment;
    private TimecardDayFragment mTimecardDayFragment;
    private Menu mMenu;
    private int mCurrentTabId;
	
	private TimecardModel timecardModel;
	
	protected Object mActionMode;
	private android.view.ActionMode.Callback actionModeCallback;
	public int selectedItem = -1;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// mViewPager = new ViewPager(this);
		// mViewPager.setId(R.id.pager);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);		
		setContentView(R.layout.activity_main);
		
		/*
		 * assign these attributes to the root view making it possible to clear
		 * focus on every input field and preventing input fields gaining focus
		 * on activity startup (making the content view the "focus catcher"):
		 */
		View view = findViewById(R.id.top_container_layout);
		
	//	view.setFocusable(true);
		//view.setFocusableInTouchMode(true);
		
		
		
		
	    actionBar = getSupportActionBar();
	    mViewPager = (ViewPager) findViewById(R.id.viewpager);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);		
		
		
		try {
		Log.i(DEBUGTAG, "STARTING MAIN ACTIVITY:  timecardfragmentcontainer is " + findViewById(R.id.timecardfragmentcontainer).getClass().getName() );
		} catch (Exception e) {
			Log.i(DEBUGTAG, "STARTING MAIN ACTIVITY: some exception in verifying state of view");
		}
		
	 //	if (findViewById(R.id.timecardfragmentcontainer) == null) {
      //  if (savedInstanceState != null) {
      //      return;
      //  }
        
		Log.i(DEBUGTAG, "STARTING MAIN ACTIVITY: " );
		
		//List<Fragment> fragments = getFragments();
	    //mTabsAdapter = new TabsAdapter(getSupportFragmentManager(), fragments);
		
		//Tab tabJobs = actionBar.newTab();
		//Tab tabTimecard = actionBar.newTab();
		
		//tabJobs.setText("Jobs");
		//tabTimecard.setText("Timecards");
		

	    //tabJobs.setTabListener(mTabsAdapter);
	    //tabTimecard.setTabListener(mTabsAdapter);
	    
	    //actionBar.addTab(tabJobs);
	    //actionBar.addTab(tabTimecard);
	    //pager.setAdapter(mTabsAdapter);

	    // 1. create tabs adapter, that will add listeners to ViewPager 
		// 2. pass tab as argument and class that will generate layout under tab
		// 
	    mTabsAdapter = new TabsAdapter(this, mViewPager);
	    
	    Bundle tab1Args = new Bundle();
	    tab1Args.putInt("TAB_ID", TAB_CLOCK);
	    
	    Bundle tab2Args = new Bundle();
	    tab2Args.putInt("TAB_ID", TAB_JOBS);
	    
	    Bundle tab3Args = new Bundle();
	    tab3Args.putInt("TAB_ID", TAB_TIMESHEET);

		ActionBar.Tab tab1 = actionBar.newTab().setText("Clock Time");
	    ActionBar.Tab tab2 = actionBar.newTab().setText("Jobs");
	    ActionBar.Tab tab3 = actionBar.newTab().setText("Timesheets");
	    mTabsAdapter.addTab(tab1,TimecardDetailsFragmentClock.class, tab1Args);
	    mTabsAdapter.addTab(tab2,JobsFragment.class, tab2Args);
		mTabsAdapter.addTab(tab3,TimecardsFragment.class, tab3Args);	
		


	 	
	 //	mTabsAdapter.addTab(actionBar.newTab().setText("Log"),
	 //		TimecardHeadlinesFragment.class, tab3Args);	
	 	
	 	
	 	
	 //	Log.d(DEBUGTAG, "FRAGMENT NAME " + mTabsAdapter.getItem(1).getClass().getName());
		
	//	TimecardFragment timecardFragment = (TimecardFragment) mTabsAdapter.getItem(1);
		//timecardFragment.setArguments(tab2Args);
		
		//FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();	
		//transaction.add(R.id.timecardfragmentcontainer, timecardFragment);
		//transaction.addToBackStack(null);
		//transaction.commit();		
				 
		/*
		 * ActionBar actionBar = getSupportActionBar();
		 * actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		 * //actionBar.setDisplayShowTitleEnabled(false);
		 * 
		 * //actionBar.setCustomView(R.layout.hello_world);
		 * actionBar.setDisplayShowCustomEnabled(true);
		 * //actionBar.setDisplayOptions(0, actionBar.NAVIGATION_MODE_STANDARD);
		 * 
		 * 
		 * //setContentView(R.layout.activity_main); //drawList();
		 * 
		 * fragmentManager = getSupportFragmentManager();
		 * 
		 * 
		 * actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		 * 
		 * 
		 * //bar.setDisplayOptions(0, ActionBar.DISPLAY_SHOW_TITLE);
		 * 
		 * mTabsAdapter = new TabsAdapter(this, mViewPager);
		 * mTabsAdapter.addTab(actionBar.newTab().setText("Simple"),
		 * CountingFragment.ArrayListFragment.class, null);
		 * mTabsAdapter.addTab(actionBar.newTab().setText("List"),
		 * FragmentPagerSupport.ArrayListFragment.class, null);
		 * 
		 */

		 
		//}
	 	
	 	setActionModeCallback();
	 	
	 	globalState = (GlobalState) getApplication();
	 	
	 	// initialize timecardModel when this activities starts 
	 	if (globalState.getTimecardModel() == null) {
	 	  
	 	   timecardModel = new TimecardModel();
	 	   globalState.setTimecardModel(timecardModel);
	 	};
	 	
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub

		getMenuInflater().inflate(R.menu.main, menu);

		// MenuItemCompat.setShowAsAction(menu.findItem(R.id.action_clock),
		// MenuItemCompat.SHOW_AS_ACTION_ALWAYS);

		// menu.add("Normal item");

		// MenuItem actionItem = menu.add("Action Button");
		// MenuItemCompat.setShowAsAction(actionItem,
		// MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
		// actionItem.setIcon(android.R.drawable.ic_menu_share);
		mMenu = menu;
		mTabsAdapter.setMenu(mMenu);
		changeMenuOnTabChange(TAB_CLOCK);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.new_timesheet:
			mTimecardDetailFragmentClock.startNewSession();
			//mTimecardsFragment.reAttachTimecardDetailsFragment();
			//globalState = (GlobalState) this.getApplication();
			//globalState.setTimecardModel(null);
			//mTimecardDetailFragment.onStartInit();
			//mTimecardHeadlinesFragment.addNewEntry(mTimecardDetailFragment.getModel());
			//mTimecardDetailFragment.animateLeftToRight();

            //startActivityForResult(intent, REQUEST_CODE_SETTINGS);
/*
			try {

				Toast.makeText(MainActivity.this,
						getString(R.string.hello_world), Toast.LENGTH_LONG)
						.show();
				Log.d(DEBUGTAG, "Starting new activity to clock in");

				Intent i = new Intent(MainActivity.this, ClockActivity.class);
				startActivity(i);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				Log.d(DEBUGTAG,
						"Error -- Add button clicked.... Unable to save file");
				Log.d(DEBUGTAG, e.getMessage());
			}
*/
			break;
			
		case R.id.delete:
			if (mCurrentTabId == TAB_JOBS ) {
				mJobHeaderFragment.deleteJob();
			}
			break;
		case R.id.action_new:
			
            Intent intent = new Intent(this, JobActivity.class);
            startActivity(intent);
            
			break;
		//case R.id.action_delete:

			//mTimecardHeadlinesFragment.getSelectedItemId();
			//Log.d(TAG, "class.MainActivity method.MenuItemSelected Delete Item ID " + mTimecardHeadlinesFragment.getSelectedModelId());	
			//mTimecardDetailFragment.deleteTimecard(mTimecardHeadlinesFragment.getSelectedModelId());
						
		//	break;
		
		case R.id.prefs:
			startActivity(new Intent(this, PrefsActivity.class));
			return true;
  
		default:
			break;
		}

		return true;

	}

	@Override
	public void onJobHeaderSelected(int id) {
	       // The user selected the headline of an article from the HeadlinesFragment

        // Capture the article fragment from the activity layout
     //   JobDetailsFragment jobDetailsFragment = (JobDetailsFragment)
       //         getSupportFragmentManager().findFragmentByTag(JobsFragment.JOB_DETAIL_TAG);

		
		
        if (mJobDetailsFragment != null) {
            // If article frag is available, we're in two-pane layout...

            // Call a method in the ArticleFragment to update its content
        	mJobDetailsFragment.updateJobView(id);

        } else {
            // If the frag is not available, we're in the one-pane layout and must swap frags...

            // Create fragment and give it an argument for the selected article
            JobDetailsFragment newFragment = new JobDetailsFragment();
            Bundle args = new Bundle();
            args.putInt(JobDetailsFragment.ARG_ID, id);
            newFragment.setArguments(args);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            // Replace whatever is in the fragment_container view with this fragment,
            // and add the transaction to the back stack so the user can navigate back
            //transaction.replace(R.id.fragment_container, newFragment);
            transaction.addToBackStack(null);

            // Commit the transaction
            transaction.commit();
        }
        
        
    } 

	//@Override
	//public void OnAddActivitySelected() {    
		
		/*	    Bundle args = new Bundle();
	    args.putInt("num", 2);
		ActivityFragment activityFragment = new ActivityFragment();
		activityFragment.setArguments(args);	
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();		
		transaction.replace(R.id.timecardfragmentcontainer, activityFragment, "ACTIVITY_FRAG");
		transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		transaction.addToBackStack(null);
		transaction.commit();
		*/
	//}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		Log.d(MainActivity.DEBUGTAG, "OnRestoreInstnaceState");
		super.onRestoreInstanceState(savedInstanceState);	
		
	}
	
    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
    
    
	private void setActionModeCallback() {
		
		 actionModeCallback =  new ActionMode.Callback() {


			// Called when the action mode is created; startActionMode() was
			// called
			public boolean onCreateActionMode(ActionMode mode, Menu menu) {
				// inflate a menu resource providing context menu items
	
				MenuInflater inflater = mode.getMenuInflater();
				// assumes that you have "contexual.xml" menu resources
				inflater.inflate(R.menu.contextualjobtoactivity, menu);
				//JobHeadlinesFragment jobHeadlinesFragment = (JobHeadlinesFragment) getSupportFragmentManager()
				//		.findFragmentByTag(JobsFragment.JOBS_TAG);
				//jobHeadlinesFragment.SelectItem();				
				return true;
			}
			
			

			// called each time the action mode is shown. Always called after
			// onCreateActionMode, but
			// may be called multiple times if the mode is invalidated.
			public boolean onPrepareActionMode(ActionMode mode, Menu menu) {

				return false; // Return false if nothing is done
			}

			// called when the user selects a contextual menu item
			public boolean onActionItemClicked(ActionMode mode, MenuItem item) {				
				switch (item.getItemId()) {
				case R.id.addtoactivity:
					//actionBar.setSelectedNavigationItem(1);
					//JobHeadlinesFragment jobHeadlinesFragment = (JobHeadlinesFragment) getSupportFragmentManager()
					//		.findFragmentByTag(JobsFragment.JOBS_TAG);;
					//jobHeadlinesFragment.unSelectItem();
					
		//			ActivityFragment activityFragment = (ActivityFragment) getSupportFragmentManager()
		//					.findFragmentByTag("ACTIVITY_FRAG");
		//			if (activityFragment != null) {
		//			  activityFragment.setJobNumberField(jobHeadlinesFragment.getJobNumber(selectedItem));	
		//			}
					mode.finish(); // Action picked, so close the CAB
					return true;
				default:
					return false;
				}
			}
	
			// called when the user exits the action mode
			public void onDestroyActionMode(ActionMode mode) {
				JobHeadlinesFragment jobHeadlinesFragment = (JobHeadlinesFragment) getSupportFragmentManager()
						.findFragmentByTag(JobsFragment.JOBS_TAG);;
				jobHeadlinesFragment.unSelectItem();
				mActionMode = null;
				selectedItem = -1;
	        }
	      };
		
	}

	@Override
	public boolean onJobHeaderLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		// start the CAB using the ActionMode.Callback defined above
	//	if (mActionMode != null) {
	//		return false;
	//	}

		selectedItem = position;
	//	mActionMode = startActionMode(actionModeCallback);
		Toast.makeText(this, String.valueOf("Position of selected item "  + position),
				Toast.LENGTH_LONG).show();
		
		JobHeadlinesFragment jobHeadlinesFragment = (JobHeadlinesFragment) getSupportFragmentManager()
				.findFragmentByTag(JobsFragment.JOBS_TAG);
		jobHeadlinesFragment.unSelectItem();
		
//		ActivityFragment activityFragment = (ActivityFragment) getSupportFragmentManager()
//				.findFragmentByTag("ACTIVITY_FRAG");
//		if (activityFragment != null) {
//		  activityFragment.setJobNumberField(jobHeadlinesFragment.getJobNumber(selectedItem));	
//		}	
		
		actionBar.setSelectedNavigationItem(1);
		
		actionBar.show();
		return true;
	}
		

	@Override
	public void onJobDetailCreated(JobDetailsFragment jd) {
		mJobDetailsFragment = jd;
		
	}

	@Override
	public void onJobHeaderCreated(JobHeadlinesFragment jh) {
		mJobHeaderFragment = jh;
	}

//	@Override
//	public void onJobSelectButtonPressed() {
//		actionBar.setSelectedNavigationItem(0);for selection"),
	//			Toast.LENGTH_LONG).show();
		
		//actionBar.hide();
		
//	}
	@Override
	public void onTimecardHeadlinesCreated(TimecardHeadlinesFragment2 frag) {
		
		this.mTimecardHeadlinesFragment = frag;
		
	}
	
	@Override
	public void onTimecardDetailCreated(TimecardDetailFragmentManual frag) {
		this.mTimecardDetailFragment = frag;
		
	}
	
	@Override
	public void onTimecardListGroupSelected(int groupPosition, String dateString) {
		
		if (!mTimecardsFragment.isTimecardDayFragmentAdded()) {
		  Log.d("onTimecardListGroupSelected", "class.MainActivity method onTimecardListGroupSelected entering this method");
		  mTimecardsFragment.addTimecardDayFragment();
		} else {
			mTimecardDayFragment.initDate(dateString);
		}
	}	

	@Override
	public void onTimecardListChildSelected(int tcId) {

		Log.d(TAG,"class.MainActivity method.onTimecardListChildSelected step 1 tc_id " + tcId);
		if (mTimecardsFragment.isTimecardDetailsFragmentAdded()) {
			Log.d(TAG,"class.MainActivity method.onTimecardListChildSelected step 2 tc_id " + tcId);
			if (tcId < 0) {
				mTimecardDetailFragment.onStartInit();
			} else {
				mTimecardDetailFragment.populateModelById(tcId);
			}
		} else {
			mTimecardsFragment.addTimecardDetailsFragment(tcId);
			Log.d(TAG,"class.MainActivity method.onTimecardListChildSelected step 3 tc_id " + tcId);
		}

		
		//mTimecardDetailFragment.animateLeftToRight();
		
	}  
	
	
	@Override
	public void onTimecardDetailSaved(TimecardModel model) {
		//v bmTimecardHeadlinesFragment.updateList(model);
	}   

    private void showTop(String param) {
		Toast toast= Toast.makeText(this, param, Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.TOP|Gravity.CENTER, 0, 0);
		toast.show();
    }

    private void show(String param) {
		Toast toast= Toast.makeText(this, param, Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER|Gravity.CENTER, 0, 0);
		toast.show();
    }
    
	@Override
	public void onTimecardsFragmentCreated(TimecardsFragment frag) {
		this.mTimecardsFragment = frag;
		
	}

	@Override
	public void onTimecardDeleted() {
		//mTimecardHeadlinesFragment.postDeleteTimecard();
		
	}

	@Override
	public void onPeriodSelected(int period) {
		
		if (period > TimecardHeadlinesFragment.PERIOD_CURRENT) {
	    	mMenu.findItem(R.id.new_timesheet).setIcon(R.drawable.ic_action_plus);
		} else if (period == TimecardHeadlinesFragment.PERIOD_CURRENT)
			mMenu.findItem(R.id.new_timesheet).setIcon(R.drawable.ic_action_add_timer);
		
	}

	@Override
	public void onTimecardPeriodChange() {
		this.mTimecardHeadlinesFragment.loadData();

	}

	@Override
	public void onTimecardDayFragmentCreated(TimecardDayFragment frag) {
		// TODO Auto-generated method stub
		mTimecardDayFragment = frag;
	}

    public void insertEntryBeforePosition(int beforePosition) {
    	mTimecardDetailFragmentClock.insertEntryBeforePosition(beforePosition);
    }
    
	@Override
	public void onTimecardDetailFragmentClockCreated(
			TimecardDetailsFragmentClock frag) {
		this.mTimecardDetailFragmentClock = frag;
		
	}

	public void changeMenuOnTabChange(int tabId) {
		mCurrentTabId = tabId;
		show("Tab changed " + tabId);
		if (mMenu != null) {
		   if (tabId == TAB_CLOCK ) {
			  mMenu.findItem(R.id.new_timesheet).setVisible(true);
		      mMenu.findItem(R.id.action_new).setVisible(false);
		      mMenu.findItem(R.id.delete).setVisible(false);
		   } else if (tabId == TAB_JOBS ) {
			  mMenu.findItem(R.id.new_timesheet).setVisible(false);
	          mMenu.findItem(R.id.action_new).setVisible(true);
	          mMenu.findItem(R.id.delete).setVisible(true);
		   } else if (tabId == TAB_TIMESHEET ) {
		      mMenu.findItem(R.id.new_timesheet).setVisible(false);
		      mMenu.findItem(R.id.action_new).setVisible(false);
		      mMenu.findItem(R.id.delete).setVisible(false);
		   }
		}
	}

	public void setAnimateLastItem(boolean b) {
		mTimecardDetailFragmentClock.setAnimateLastItem(b);
		
	}

	@Override
	public void onRunningActivityStopped() {
		mTimecardDetailFragmentClock.onStopRunningActivity();
		
	}

	@Override
	public void onStartRunningActivity(TimecardEntry entry) {
		mTimecardDetailFragmentClock.onStartRunningActivity(entry);
		
	}

	@Override
	public void onWhatNextActivityClicked(Object sourceObj) {
		mTimecardDetailFragmentClock.onWhatNextActivityClicked(sourceObj);
		
	}

	
	@Override
	public void onContinueRunningActivity(TimecardEntry entry) {
		mTimecardDetailFragmentClock.onContinueRunningActivity(entry);
		
	}
	
	public void animatCurentActivityOnContinue() {
		mTimecardDetailFragmentClock.animatCurentActivityOnContinue();
		
	}
	
}
