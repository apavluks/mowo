package com.cummins.mowo.activity.timecard;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.DataSetObserver;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ActionMode;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cummins.mowo.R;
import com.cummins.mowo.activity.MainActivity;
import com.cummins.mowo.activity.swipelistview.PackageAdapter;
import com.cummins.mowo.activity.swipelistview.PackageItem;
import com.cummins.mowo.adapters.TimecardEntriesAdapter;
import com.cummins.mowo.adapters.TimecardEntriesAdapterNew;
import com.cummins.mowo.conrollers.timecard.TimecardController;
import com.cummins.mowo.daos.TimecardDao;
import com.cummins.mowo.model.TimecardModel;
import com.cummins.mowo.utils.MowoListEntries;
import com.cummins.mowo.vos.OnChangeListener;
import com.cummins.mowo.vos.SessionDBParams;
import com.cummins.mowo.vos.TimecardEntry;
import com.cummins.mowo.widgets.BlinkerManager;
import com.cummins.mowo.widgets.ClockButtonWidget;
import com.cummins.mowo.widgets.TimerManager;
import com.cummins.mowo.widgets.EntryTimerManager;
import com.cummins.mowo.widgets.swipelistview.BaseSwipeListViewListener;
import com.cummins.mowo.widgets.swipelistview.SwipeListView;

public class TimecardDetailFragmentManual extends Fragment implements OnChangeListener<TimecardModel>, Handler.Callback {

	private static final String TAG = TimecardDetailFragmentManual.class.getSimpleName();
	
    private TimecardController controller;
	private TimecardsDetailManualListener mCallback;
	private ImageButton addEntryButton;
	private TextView titleTV;
	private TextView mTcIdTV;
	private RelativeLayout titleBar;
    private TimecardModel model;
    private TimecardEntriesAdapterNew entriesAdapter;
    private ListView entriesList;
    private View view;
    private ClockButtonWidget clockInWidget;
    private ClockButtonWidget clockOutWidget;
    private Button sessionCntrBtn; 
    private List<TimecardEntry> entries;
    public static final String TC_ID = "tcId";
    private int savedInstanceTcId;
    private int  tcId;
    private BlinkerManager sessionBlinker;
    private View sessionIndicator;
    private TextView sessionDuration;
    private TimerManager durationTimer;
    private EntryTimerManager entryTimerManager;
    MowoListEntries mowoEntriesList;
    
    /** variables for swipe list view*/
    private static final int REQUEST_CODE_SETTINGS = 0;
    private PackageAdapter adapter;
    private List<PackageItem> data;
    
    private TimecardEntriesAdapter entriesAdapterNew;
    
    private SwipeListView swipeListView;
    private ProgressDialog progressDialog;
    
	public interface TimecardsDetailManualListener {
		public void onTimecardDetailSaved(TimecardModel model);
		public void onTimecardDeleted();

	}    
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
		
		Bundle bundle = this.getArguments();
		tcId = bundle.getInt(TimecardsFragment.FRAGMENT_BUNDLE_TC_ID, -1);
		Log.d(TAG, "method onCreate tcId = " + tcId);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
        
		view = inflater.inflate(R.layout.fragment_timecard_session_manual, container,false);	
		// Make view invisible until data is laoded. 
		view.setVisibility(view.INVISIBLE);
		
		return view;
	}
	
	@Override
	public void onStart() {
		super.onStart();
		onStartInit();
	}

	public void onStartInit() {

	    model = new TimecardModel();	
	 	model.addListener(this);
	
	    controller = new TimecardController(model);
	    controller.addOutboxHandler(new Handler(this));	   
        entries = model.getTimecardEntryList();
	    
		entriesAdapter = new TimecardEntriesAdapterNew(getActivity(), entries, controller);
		
		titleTV = (TextView) view.findViewById(R.id.title);
		titleBar = (RelativeLayout) view.findViewById(R.id.title_layout);
		clockInWidget = (ClockButtonWidget) view.findViewById(R.id.clock_in_widget);
		clockOutWidget = (ClockButtonWidget) view.findViewById(R.id.clock_out_widget);	 
		addEntryButton = (ImageButton) view.findViewById(R.id.AddActivityButton);
		mTcIdTV = (TextView) view.findViewById(R.id.timecard_id_fragment_timecard);
		sessionCntrBtn = (Button) view.findViewById(R.id.session_control_button);
		sessionIndicator = (View) view.findViewById(R.id.session_indicator);
		sessionDuration = (TextView) view.findViewById(R.id.session_duration);
	    sessionBlinker = new BlinkerManager(sessionIndicator);
	   
		
	    String clockBtnFont = getActivity().getResources().getString(R.string.clock_button_font);
        Typeface tf = Typeface.createFromAsset(getActivity().getAssets(), clockBtnFont);
        sessionDuration.setTypeface(tf);
	    durationTimer = new TimerManager(sessionDuration);
	      
	    if (tcId < 0 ) {
	    	showTop("Update View = ");
	    	updateView();
	    } else {	    
	    	showTop("populateModelById " + tcId);
	       populateModelById(tcId);
	    }
	    
	    sessionCntrBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				/** if timecard is ended allow to start new session*/ 
				if (model.getTimecardStatus() == TimecardModel.TIMESHEET_STATUS_ENDED){	
				  controller.handleMessage(TimecardController.MESSAGE_START_NEW_SESSION);
				}
			}
		});
	    
		clockInWidget.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
			    entryTimerManager.startTimer();
				controller.handleMessage(TimecardController.MESSAGE_CLOCK_IN);	
				
			
			}
		});
		
		clockOutWidget.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				entryTimerManager.stopTimer();
				controller.handleMessage(TimecardController.MESSAGE_CLOCK_OUT);	
			}
		});	
		
		addEntryButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				controller.handleMessage(TimecardController.MESSAGE_ADD_ENTRY);
			}
		});
	 	
		
		/** initiate swipe view list */ 

	    swipeListView = (SwipeListView) view.findViewById(R.id.entries_swipe_list);

	    if (Build.VERSION.SDK_INT >= 11) {
	         swipeListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
	    }
	    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
	         swipeListView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {

	                @Override
	                public void onItemCheckedStateChanged(ActionMode mode, int position,
	                                                      long id, boolean checked) {
	                    mode.setTitle("Selected (" + swipeListView.getCountSelected() + ")");
	                }

	                @Override
	                public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
	                    int id = item.getItemId();
	                    if (id == R.id.menu_delete) {
	                        swipeListView.dismissSelected();
	                        return true;
	                    }
	                    return false;
	                }

	                @Override
	                public boolean onCreateActionMode(ActionMode mode, Menu menu) {
	                    MenuInflater inflater = mode.getMenuInflater();
	                    inflater.inflate(R.menu.menu_choice_items, menu);
	                    return true;
	                }

	                @Override
	                public void onDestroyActionMode(ActionMode mode) {
	                    swipeListView.unselectedChoiceStates();
	                }

	                @Override
	                public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
	                    return false;
	                }
	            });
	    }

	    swipeListView.setSwipeListViewListener(new BaseSwipeListViewListener() {
	            @Override
	            public void onOpened(int position, boolean toRight) {
	            }

	            @Override
	            public void onClosed(int position, boolean fromRight) {
	            }

	            @Override
	            public void onListChanged() {
	            }

	            @Override
	            public void onMove(int position, float x) {
	            }

	            @Override
	            public void onStartOpen(int position, int action, boolean right) {
	                Log.d("swipe", String.format("onStartOpen %d - action %d", position, action));
	            }

	            @Override
	            public void onStartClose(int position, boolean right) {
	                Log.d("swipe", String.format("onStartClose %d", position));
	            }

	            @Override
	            public void onClickFrontView(int position) {
	                Log.d("swipe", String.format("onClickFrontView %d", position));
	            }

	            @Override
	            public void onClickBackView(int position) {
	                Log.d("swipe", String.format("onClickBackView %d", position));
	            }

	            @Override
	            public void onDismiss(int[] reverseSortedPositions) {
	                for (int position : reverseSortedPositions) {
	                    entries.remove(position);
	                }
	                entriesAdapter.notifyDataSetChanged();
	            }

	   }); 

	        swipeListView.setAdapter(entriesAdapter);
	        entryTimerManager = new EntryTimerManager(getActivity(), entries, swipeListView);

	       /* new ListAppTask().execute();

	        progressDialog = new ProgressDialog(getActivity());
	        progressDialog.setMessage(getString(R.string.loading));
	        progressDialog.setCancelable(false);
	        progressDialog.show();
	       */
		
	}
	
	/**
	 * Since a change has occurred we need to update our widgets/sub-views. Remember, widgets must be 
	 * modified on the UI Thread. Since we don't know what thread called onChange, we need to switch over 
	 * to the UI thread before making an modification which is done by calling the runOnUiThread.
	 */
	
	@Override
	public void onChange(TimecardModel model) {		
		getActivity().runOnUiThread(new Runnable() {

			@Override
			public void run() {
				updateView();
			}
		});

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		/**persist current timecard*/
		SessionDBParams sessionParam = new SessionDBParams(TimecardDao.V_PARAM_ACTIVE_TIMECARD_MODEL_ID, String.valueOf(model.getId()));
		controller.handleMessage(TimecardController.MESSAGE_SET_SESSION_PARAMETER, sessionParam);
		
		controller.dispose();
		entryTimerManager.stopTimer();
		durationTimer.stopTimer();
		//if (saveDialog != null && saveDialog.isShowing()) saveDialog.dismiss();
	}
	
	@Override
	public void onPause() {
		super.onPause();
		/**persist current timecard*/
		SessionDBParams sessionParam = new SessionDBParams(TimecardDao.V_PARAM_ACTIVE_TIMECARD_MODEL_ID, String.valueOf(model.getId()));
		controller.handleMessage(TimecardController.MESSAGE_SET_SESSION_PARAMETER, sessionParam);
		
		entryTimerManager.stopTimer();
		durationTimer.stopTimer();
		//if (saveDialog != null && saveDialog.isShowing()) saveDialog.dismiss();
	}	
	
	public void populateModelById(int id) {
		Log.d(TAG, "class.TimecardDetailsFragment method.populateModelById id " + id );
        /*progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.setCancelable(false);
        progressDialog.show();*/
		controller.handleMessage(TimecardController.MESSAGE_POPULATE_MODEL_BY_ID, id);
	}
	
	public void saveNewModel(TimecardModel model) {
		this.model = model;
		controller.handleMessage(TimecardController.MESSAGE_CLOCK_IN);		
	}
	
	public void updateView() {
		
		titleTV.setText(model.getTitleBarString());
		clockInWidget.setValueText(model.getClockInTimeString());
		clockInWidget.setLabelText(model.getClockInLabel());
		clockOutWidget.setValueText(model.getClockOutTimeString());
		clockOutWidget.setLabelText(model.getClockOutLabel());
		mTcIdTV.setText("Timecard ID " + Integer.valueOf(model.getId()));
		
		switch (model.getTimecardStatus()) {
		case TimecardModel.TIMESHEET_STATUS_NOTSTARTED:
			//titleBar.setBackgroundColor((getActivity().getResources()
			//		.getColor(R.color.tc_titlebar_not_started)));
			sessionIndicator.setVisibility(sessionIndicator.INVISIBLE);
			sessionCntrBtn.setVisibility(sessionCntrBtn.INVISIBLE);
			break;
		case TimecardModel.TIMESHEET_STATUS_STARTED:
			//titleBar.setBackgroundColor(getActivity().getResources()
			//		.getColor(R.color.tc_titlebar_started));
			sessionCntrBtn.setVisibility(sessionCntrBtn.INVISIBLE);
			sessionIndicator.setVisibility(sessionIndicator.VISIBLE);
			sessionBlinker.startBlinking();
			entryTimerManager.startTimer();
			durationTimer.startTimer(model.getClockInTimeInMillis());
			break;
		case TimecardModel.TIMESHEET_STATUS_ENDED:
			//titleBar.setBackgroundColor(getActivity().getResources()
			//		.getColor(R.color.tc_titlebar_ended));		
			sessionCntrBtn.setVisibility(sessionCntrBtn.VISIBLE);
			sessionIndicator.setVisibility(sessionIndicator.INVISIBLE);
			sessionBlinker.stopBlinking();
			durationTimer.stopTimer();
			break;
			
		default:
			//titleBar.setBackgroundColor(getActivity().getResources()
			//		.getColor(R.color.tc_titlebar_not_started));

		}
		
		entries = model.getTimecardEntryList();
		entriesAdapter.notifyDataSetChanged();
				
	}

	/** 
	 * Remove visual hint container when new activity is added or CLOCKIN event occur.
	 * this can also perhaps be moved under ListView.Empty logic in future
	 */

	private void handleEmptyContainer() {
		log("Entering class.TimecardFragment method.handleEmptyContainer");
		View emptyContainer = (View) view.findViewById(R.id.empty_container);
		if (emptyContainer != null && entries != null) {
			if (entries.size() > 0) {
				ViewGroup parent = (ViewGroup) emptyContainer.getParent();
				parent.removeView((View) emptyContainer);
			}
		}
	}
		

	/** 
	 * handle messages from controller 
	 */
	
	@Override
	public boolean handleMessage(final Message msg) { 
		log("Entering class.TimecardFragment method.handleMessage");
		switch (msg.what) {
		case TimecardController.MESSAGE_SHOW_TOAST:
			getActivity().runOnUiThread(new Runnable() {
				@Override
				public void run() {
					showTop(getActivity().getResources().getString(msg.arg1));
				}
			});
			return true;	
		case TimecardController.MESSAGE_SAVE_COMPLETE:
			getActivity().runOnUiThread(new Runnable() {
				@Override
				public void run() {
				    mCallback.onTimecardDetailSaved(model);
				    Log.d(TAG, "after MESSAGE_SAVE_COMPLETE model_id = " + model.getId());
				    populateModelById(model.getId());
					
				}
			});
			return true;			
		case TimecardController.MESSAGE_SAVE_NOW:
			getActivity().runOnUiThread(new Runnable() {
				@Override
				public void run() {			
					
					controller.handleMessage(TimecardController.MESSAGE_SAVE_MODEL);
					//show("Handle message MESSAGE_UPDATE_VEW 1" + model.getTimecardEntryList().get(model.getTimecardEntryList().size()-1).getTimeEndString());
				    
					//globalState.setTimecardModel(model);
					//entries = model.getTimecardEntryList();
					//entriesAdapter.notifyDataSetChanged();
				    Log.d(TAG, "after MESSAGE_SAVE_NOW ");
					//handleEmptyContainer();
					//updateView();
					
				}
			});
			return true;
		case TimecardController.MESSAGE_UPDATE_VIEW:
			getActivity().runOnUiThread(new Runnable() {
				@Override
				public void run() {
				    handleEmptyContainer();
					updateView();
					Log.d(TAG, "after MESSAGE_UPDATE_VIEW ");

					// Make view visible once data is loaded. 
					if (view.getVisibility() == view.INVISIBLE) {
						view.setVisibility(view.VISIBLE);
					}
					
			        if (progressDialog != null) {
			            progressDialog.dismiss();
			            progressDialog = null;
			        }
				}
			});
			return true;
		case TimecardController.MESSAGE_LOADING_NOT_REQUIRED:
			getActivity().runOnUiThread(new Runnable() {
				@Override
				public void run() {
					
					if (view.getVisibility() == view.INVISIBLE) {
						view.setVisibility(view.VISIBLE);
					}
					
			        if (progressDialog != null) {
			            progressDialog.dismiss();
			            progressDialog = null;
			        }
				}
			});
			return true;				
		case TimecardController.MESSAGE_TIMECARD_DELETED:
			getActivity().runOnUiThread(new Runnable() {
				@Override
				public void run() {			
					
					showTop(getActivity().getResources().getString(R.string.timecard_deleted));
				    mCallback.onTimecardDeleted();
					
				}
			});
			return true;
		case TimecardController.MESSAGE_READY_FOR_NEW_SESSION:
			getActivity().runOnUiThread(new Runnable() {
				@Override
				public void run() {			
				   onStartInit();
				}
			});
			return true;
		}
		return false;
	}

	public void animateLeftToRight () {
		Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.left_to_right);
		view.findViewById(R.id.fragment_timecard_container).startAnimation(animation); 
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		// Save the current article selection in case we need to recreate the
		// fragment
		 outState.putInt(TC_ID, model.getId());
		 
	}

    // The container Activity must implement this interface so the frag can deliver messages
    public interface OnAddActivitySelectedListener {
        /** Called by HeadlinesFragment when a list item is selected */
        public void OnAddActivitySelected();
    }
    
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
		// This makes sure that the container activity has implemented
		// the callback interface. If not, it throws an exception.
		try {
			mCallback = (TimecardsDetailManualListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement TimecardsDetailListener");
		}
	}


	@Override
	public void onDestroyView() {
		super.onDestroyView();
        
		Log.d("MOWO", "TimecardFragment onDestroyView ");
		/*
		// we need to destroy nested fragments , otherwise error is caused when we return back to in
		// onCreateView inflate section of this code. 
		try {
			FragmentTransaction transaction = getActivity()
					.getSupportFragmentManager().beginTransaction();

			TimecardClockingFragment timecardClockingFragment = (TimecardClockingFragment) getActivity()
					.getSupportFragmentManager().findFragmentById(
							R.id.timecardclockingfragment);
			transaction.remove(timecardClockingFragment);

			TimecardActivitiesFragment timecardActivitiesFragment = (TimecardActivitiesFragment) getActivity()
					.getSupportFragmentManager().findFragmentById(
							R.id.timecardactivityfragment);

			transaction.remove(timecardActivitiesFragment);

			transaction.commit();
		} catch (Exception e) {
		}	
		*/
	} 
	
	
	/**
	 * Set the callback to null so we don't accidentally leak the Activity
	 * instance.
	 */
	@Override
	public void onDetach() {
		// TODO Auto-generated method stub
		super.onDetach();
		mCallback = null;
	}

	public TimecardModel getModel() {
		return this.model;
	}
	
	public void deleteTimecard(int selectedModelId) {	
		if (selectedModelId == model.getId()) {
			controller.handleMessage(TimecardController.MESSAGE_DELETE_TIMECARD, model.getId());			
		}
	}

	private void show(String param) {
    	Toast.makeText(getActivity(),param, Toast.LENGTH_SHORT).show();
    }
    
    private void showTop(String param) {
		Toast toast= Toast.makeText(getActivity(), param, Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.TOP|Gravity.CENTER, 0, 0);
		toast.show();
    }    

    private void log(String str) {
    	Log.d(MainActivity.DEBUGTAG, str);
    }



}
