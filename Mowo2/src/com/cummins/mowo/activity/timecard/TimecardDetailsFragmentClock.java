package com.cummins.mowo.activity.timecard;


import java.util.List;
import java.util.Stack;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.SpannableString;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.cummins.mowo.R;
import com.cummins.mowo.activity.MainActivity;
import com.cummins.mowo.activity.timecard.TimecardCurrentActivityFragment.TimecardCurrentActivityFragmentListener;
import com.cummins.mowo.adapters.TimecardEntriesAdapter;
import com.cummins.mowo.adapters.TimecardEntriesAdapterNew;
import com.cummins.mowo.conrollers.timecard.TimecardController;
import com.cummins.mowo.daos.TimecardDao;
import com.cummins.mowo.functions.fDATE;
import com.cummins.mowo.model.GlobalState;
import com.cummins.mowo.model.TimecardModel;
import com.cummins.mowo.utils.MowoListEntries;
import com.cummins.mowo.vos.OnChangeListener;
import com.cummins.mowo.vos.PunchData;
import com.cummins.mowo.vos.SessionDBParams;
import com.cummins.mowo.vos.TimecardEntry;
import com.cummins.mowo.widgets.BlinkerManager;
import com.cummins.mowo.widgets.ClockButtonWidget;
import com.cummins.mowo.widgets.ClockControlDialog;
import com.cummins.mowo.widgets.EntryTimerManager;
import com.cummins.mowo.widgets.SlidingActivityOptions;
import com.cummins.mowo.widgets.TimerManager;
import com.cummins.mowo.widgets.kenburnsview.AlphaForegroundColorSpan;

public class TimecardDetailsFragmentClock extends Fragment implements OnChangeListener<TimecardModel>
                                                                    , Handler.Callback{

	private static final String TAG = TimecardDetailsFragmentClock.class.getSimpleName();
	private static final String RUNNING_FRAGMENT_TAG = "mRunningFragment";
	private static final String WHAT_FRAGMENT_TAG = "mWhatFragment";
    private static GlobalState globalState;
    private TimecardController controller;
	private TimecardsDetailClockListener mCallback;
	private TextView titleTV;
	private TextView mCompletedHeaderLabel;
    private TimecardModel model;
    private TimecardEntriesAdapterNew entriesAdapter;
    private View view;
    private RelativeLayout titleBar;
    private ClockButtonWidget clockInWidget;
    private ClockButtonWidget clockOutWidget;
    private List<TimecardEntry> entries;
    public static final String TC_ID = "tcId";
    private int savedInstanceTcId;
    private int  tcId;
    private BlinkerManager sessionBlinker;
    private View sessionIndicator;
    private TextView sessionDuration;
    private TimerManager durationTimer;
    private TimecardDao dao;
    private FrameLayout mActivitiesFrameLayout;
    private boolean mSliderEnabled = true;
    private ListView mListView;
    private ProgressDialog progressDialog;
    private int mHeaderHeight;
    private int mMinFooterTranslation;
    private View mListFooter;
    private View mPlaceHolderViewFooter;
    public Stack<String> mFragmentStack;
    public Object slideFooterCallerObj = null;
    public boolean scrolledToEnd = false;  
    private TimecardWhatActivityFragment mWhatfragment;
    private TimecardCurrentActivityFragment mRunningFragment;
    private RelativeLayout mFragmentHolder;
    private View emptyContainer;
    
    public interface TimecardsDetailClockListener {
		public void onTimecardDetailSaved(TimecardModel model);
		public void onTimecardDeleted();
		public void onTimecardDetailFragmentClockCreated(TimecardDetailsFragmentClock frag);
	}    
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (savedInstanceState != null) {
        	savedInstanceTcId = savedInstanceState.getInt(TC_ID);
        	showTop("Timecard restored from state TC_ID = " + savedInstanceTcId);
        } else {
        	showTop("Timecard savedInstanceState is null ");
        	savedInstanceTcId = -1;
        }
		this.view = inflater.inflate(R.layout.fragment_timecard_session_clock, container,false);		
		// Make view invisible until data is laoded. 
		this.view.setVisibility(view.INVISIBLE);
		mCallback.onTimecardDetailFragmentClockCreated(this);
		return view;
	}
	
    @Override
    public void onActivityCreated(Bundle saved) { 
        super.onActivityCreated(saved);
      /*  progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.setCancelable(false);
        progressDialog.show();
	  */	
        onStartInit();	
    }

	@Override
	public void onStart() {
		super.onStart();
	}

	public void onStartInit() {
		globalState = (GlobalState) getActivity().getApplication();
		
	 	if(globalState.getTimecardModel() != null) {
	 		model = globalState.getTimecardModel();
	 	} else {
	 		model = new TimecardModel();	
	 	    globalState.setTimecardModel(model);
	 	}
	 	
	    controller = new TimecardController(model);
	    controller.addOutboxHandler(new Handler(this));	

		// initialize UI components 
		titleTV = (TextView) view.findViewById(R.id.title);
		titleBar = (RelativeLayout) view.findViewById(R.id.title_layout);
		clockInWidget = (ClockButtonWidget) view.findViewById(R.id.clock_in_widget);
		clockInWidget.setImageSrc(clockInWidget.DRAWABLE_CLOCK_IN);
		clockOutWidget = (ClockButtonWidget) view.findViewById(R.id.clock_out_widget);	
		clockOutWidget.setImageSrc(clockInWidget.DRAWABLE_CLOCK_OUT);
		mActivitiesFrameLayout = (FrameLayout) view.findViewById(R.id.framelayout_activites_container);
		mCompletedHeaderLabel = (TextView) view.findViewById(R.id.completed_activities_container_label);
		sessionIndicator = (View) view.findViewById(R.id.session_indicator);
		sessionDuration = (TextView) view.findViewById(R.id.session_duration);
	    sessionBlinker = new BlinkerManager(sessionIndicator);
	    mFragmentHolder = (RelativeLayout) view.findViewById(R.id.current_activity_fragment_holder);
	    String clockBtnFont = getActivity().getResources().getString(R.string.clock_button_font);
        Typeface tf = Typeface.createFromAsset(getActivity().getAssets(), clockBtnFont);
        sessionDuration.setTypeface(tf);
	    durationTimer = new TimerManager(sessionDuration);
	    
		//attached footer fragments 
		FragmentTransaction ft = getChildFragmentManager().beginTransaction();
		if (getChildFragmentManager().findFragmentByTag(WHAT_FRAGMENT_TAG) != null) {
			mWhatfragment = (TimecardWhatActivityFragment) getChildFragmentManager().findFragmentByTag(WHAT_FRAGMENT_TAG);
		} else {
	  	    mWhatfragment = new TimecardWhatActivityFragment();
		    ft.add(R.id.current_activity_fragment_holder, mWhatfragment, WHAT_FRAGMENT_TAG);
		}
		ft.hide(mWhatfragment);
		if (getChildFragmentManager().findFragmentByTag(RUNNING_FRAGMENT_TAG) != null) {
			mRunningFragment = (TimecardCurrentActivityFragment) getChildFragmentManager().findFragmentByTag(RUNNING_FRAGMENT_TAG);
		} else {
			mRunningFragment = new TimecardCurrentActivityFragment();
			ft.add(R.id.current_activity_fragment_holder, mRunningFragment, RUNNING_FRAGMENT_TAG);
		}
        ft.hide(mRunningFragment); 
        
        ft.commit();
        getChildFragmentManager().executePendingTransactions();
	    
		setupEntriesList(); 
	    manageInitialViewUpdate();
	    
		clockInWidget.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				
				ClockControlDialog clockDialog = new ClockControlDialog(
						TimecardDetailsFragmentClock.this.getActivity(),
						new ClockControlDialog.ClockControlListener() {
							@Override
							public void onBadgeSwipe(PunchData punchData) {
								model.setPunchInData(punchData);
								controller.handleMessage(TimecardController.MESSAGE_CLOCK_IN);
								//mWhatActivityBtn.setText(getActivity().getResources().getString(R.string.new_activity_question));
								//mSliderOptionsWidget.unselect();
							}
						});		
				/* when clock in is null show auto clock in dialog */
				if (!model.isClockInTimeSet()) {
					clockDialog.handleAutoPunch("Clock In");
				} else {
					clockDialog.handlePunchEdit("Clock In", model.getPunchInData(), model.getClockInTimeString());
				}
			
			}
		});
		
		clockOutWidget.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ClockControlDialog clockDialog = new ClockControlDialog(
						TimecardDetailsFragmentClock.this.getActivity(),
						new ClockControlDialog.ClockControlListener() {
							@Override
							public void onBadgeSwipe(PunchData punchData) {
								//entryTimerManager.stopTimer();
								model.setPunchOutData(punchData);
								controller.handleMessage(TimecardController.MESSAGE_CLOCK_OUT);
							}
						});
				//when clock in is null show auto clock in dialog 
				if (!model.isClockOutTimeSet()) {
					clockDialog.handleAutoPunch("Clock Out");
				} else {
					clockDialog.handlePunchEdit("Clock Out",model.getPunchOutData(),model.getClockOutTimeString());
				}
			}
		});
        

        //setup and display completed activities list
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
		//persist current model
		SessionDBParams sessionParam = new SessionDBParams(TimecardDao.V_PARAM_ACTIVE_TIMECARD_MODEL_ID, String.valueOf(model.getId()));
		controller.handleMessage(TimecardController.MESSAGE_SET_SESSION_PARAMETER, sessionParam);
		//other tasks
		controller.dispose();
		durationTimer.stopTimer();
	}
	
	@Override
	public void onPause() {
		super.onPause();
		//persist current model
		SessionDBParams sessionParam = new SessionDBParams(TimecardDao.V_PARAM_ACTIVE_TIMECARD_MODEL_ID, String.valueOf(model.getId()));
		//other tasks
		controller.handleMessage(TimecardController.MESSAGE_SET_SESSION_PARAMETER, sessionParam);
		durationTimer.stopTimer();
	}	
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		// Save the current article selection in case we need to recreate the
		 outState.putInt(TC_ID, model.getId());
		 
	}
    
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mCallback = (TimecardsDetailClockListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement TimecardsDetailListener");
		}	
	}

	@Override
	public void onResume() {
		super.onResume();
		updateView();
	}
	
	public void startNewSession() {
		//if timecard is ended allow to start new session
		if (model.getTimecardStatus() == TimecardModel.TIMESHEET_STATUS_ENDED){	
		  controller.handleMessage(TimecardController.MESSAGE_START_NEW_SESSION);
		  
		} else if (model.getTimecardStatus() == TimecardModel.TIMESHEET_STATUS_STARTED) {
			showTop("Unable to create new session. Current timecard is active. Clock out first");
		} else if (model.getTimecardStatus() == TimecardModel.TIMESHEET_STATUS_NOTSTARTED) {
			showTop("New session already created. Clock In using button below");
		}
	}
	
	/**
	 * Setup entries list and set on the listener to resize the current activity 
	 * area when the users are interacting with the list. 
	 */
	public void setupEntriesList() {
		mHeaderHeight = getResources().getDimensionPixelSize(R.dimen.entry_header);
        mMinFooterTranslation = mHeaderHeight - 100;
	    mListView = (ListView) view.findViewById(R.id.list_for_entries);
	    mListFooter = (View) this.view.findViewById(R.id.entry_list_footer);
        entries = model.getTimecardEntryList();
		entriesAdapter = new TimecardEntriesAdapterNew(getActivity(), entries, controller);
        mPlaceHolderViewFooter = getActivity().getLayoutInflater().inflate(R.layout.view_footer_entries_list_placeholder, mListView, false);
        if (mListView.getFooterViewsCount() == 0 ) { 
          mListView.addFooterView(mPlaceHolderViewFooter);
        }
        mListView.setAdapter(entriesAdapter);
        //set scroll listener to perform various tasks 
        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {  
            	if (scrollState == 0 && scrolledToEnd) {
            		if (slideFooterCallerObj instanceof TimecardWhatActivityFragment) { 
            		    ((TimecardWhatActivityFragment) slideFooterCallerObj).displayActivityTypeDialog();
            	    	slideFooterCallerObj = new Object();
            		} else if (slideFooterCallerObj instanceof TimecardCurrentActivityFragment) {
            			((TimecardCurrentActivityFragment) slideFooterCallerObj).displayActivityTypeDialog();
            	    	slideFooterCallerObj = new Object();
            		}
            	}
            	
            }
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {            	
            	    //Log.d(TAG, "inside ListView setOnScrollListener llingEnabled = " + mSliderEnabled);
            	    if (mSliderEnabled) {
					  int scrollFooterY = getScrollFooterY();
					  mListFooter.setTranslationY(Math.min(scrollFooterY,mMinFooterTranslation));
            	    }     	    
            	    if (mListView.getLastVisiblePosition() == mListView.getAdapter().getCount()-1
            	        && mListView.getChildAt(mListView.getChildCount()-1).getBottom() <= mListView.getHeight()) {
            	    	scrolledToEnd = true;
            	    } else {
            	    	scrolledToEnd = false;
            	    }
            }
        });
	}

	/** 
	 * Return the height of the invisible area of the ListView that was scrolled down below 
	 * the current activity frame
	 */ 
    public int getScrollFooterY() {
    	int listCount = mListView.getCount();
        View c = mListView.getChildAt(mListView.getChildCount()-1);
        if (c == null) {
            return 0;
        }
        // lastVisiblePosition is the last position in adapter (not to be mistaken with lastPosition on screen)
        int lastVisiblePosition = mListView.getLastVisiblePosition();
        int bottom = mListView.getHeight() - c.getBottom();
        int headerHeight = 0;
        // if footer is no longer visible then add it to the height of the invisible area 
        if (lastVisiblePosition <= listCount-1) {
            headerHeight =getResources().getDimensionPixelSize(R.dimen.entry_header); 
        }
        int invisibleTaleItemCount = (listCount-1)-lastVisiblePosition-1;
        return -bottom + invisibleTaleItemCount * c.getHeight() + headerHeight;
    }    
	
	public void populateModelById(int id) {
		controller.handleMessage(TimecardController.MESSAGE_POPULATE_MODEL_BY_ID, id);
	}
		
	public void updateView() {
		Log.d(TAG, "Entering updateview ");
		titleTV.setText(model.getTitleBarString());
		clockInWidget.setValueText(model.getClockInTimeString());
		clockInWidget.setLabelText(model.getClockInLabel());
		clockOutWidget.setValueText(model.getClockOutTimeString());
		clockOutWidget.setLabelText(model.getClockOutLabel());
		switch (model.getTimecardStatus()) {
		case TimecardModel.TIMESHEET_STATUS_NOTSTARTED:
			sessionIndicator.setVisibility(sessionIndicator.INVISIBLE);
			mActivitiesFrameLayout.setVisibility(View.INVISIBLE);
			mCompletedHeaderLabel.setVisibility(View.INVISIBLE);
			sessionDuration.setText("00h 00m");
			togleFooterFragment(false);
			break;
		case TimecardModel.TIMESHEET_STATUS_STARTED:
			sessionIndicator.setVisibility(sessionIndicator.VISIBLE);
			sessionBlinker.startBlinking();
			mActivitiesFrameLayout.setVisibility(View.VISIBLE);		
			if (entriesAdapter.getCount() > 0) { mCompletedHeaderLabel.setVisibility(View.VISIBLE); } else {  mCompletedHeaderLabel.setVisibility(View.INVISIBLE); };
			durationTimer.startTimer(model.getClockInTimeInMillis());
			togleFooterFragment(true);
			break;
		case TimecardModel.TIMESHEET_STATUS_ENDED:	
			sessionIndicator.setVisibility(sessionIndicator.INVISIBLE);
			mActivitiesFrameLayout.setVisibility(View.VISIBLE);
			sessionBlinker.stopBlinking();
			durationTimer.stopTimer();
			if (entriesAdapter.getCount() > 0) { mCompletedHeaderLabel.setVisibility(View.VISIBLE); } else {  mCompletedHeaderLabel.setVisibility(View.INVISIBLE); };
			togleFooterFragment(false);
			break;			
		default:
		}
		entries = model.getTimecardEntryList();
		entriesAdapter.notifyDataSetChanged();

	}
	
	/**
	 * manage current activity view state
	 */
	
	public void togleFooterFragment(boolean isTimecardRunning) {
		Log.d(TAG, "insde toglefragment ...");
		FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.alpha_anim_long, R.anim.alpha_anim_reverse_long);
		if (isTimecardRunning) {
			if (model.getActiveEntry() != null) {
				ft.hide(getChildFragmentManager().findFragmentByTag(WHAT_FRAGMENT_TAG));
				ft.show(getChildFragmentManager().findFragmentByTag(RUNNING_FRAGMENT_TAG));
				mRunningFragment.setEntry(model.getActiveEntry());
				mRunningFragment.updateRunningView();
				mRunningFragment.startRunning();
			} else {
				ft.hide(getChildFragmentManager().findFragmentByTag(RUNNING_FRAGMENT_TAG));
				mRunningFragment.stopRunning();
				ft.show(getChildFragmentManager().findFragmentByTag(WHAT_FRAGMENT_TAG));
			}
		} else {
			ft.hide(getChildFragmentManager().findFragmentByTag(RUNNING_FRAGMENT_TAG));
			ft.hide(getChildFragmentManager().findFragmentByTag(WHAT_FRAGMENT_TAG));
			mRunningFragment.stopRunning();
		}
		ft.commit();
		getChildFragmentManager().executePendingTransactions();
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
					showCenter(getActivity().getResources().getString(msg.arg1));
				}
			});
			return true;	
		case TimecardController.MESSAGE_SAVE_COMPLETE:
			getActivity().runOnUiThread(new Runnable() {
				@Override
				public void run() {
				    globalState.setTimecardModel(model);
				    mCallback.onTimecardDetailSaved(model);
				    Log.d(TAG, "after MESSAGE_SAVE_COMPLETE model_id = " + model.getId());
				    populateModelById(model.getId());
					
				}
			});
			return true;		
		case TimecardController.MESSAGE_CURRENT_ENTRY_STOPED:
			getActivity().runOnUiThread(new Runnable() {
				@Override
				public void run() {
				    globalState.setTimecardModel(model);
				    mCallback.onTimecardDetailSaved(model);
				    Log.d(TAG, "after MESSAGE_CURRENT_ENTRY_STOPED model_id = " + model.getId());
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
					Log.d(TAG, "run MESSAGE_UPDATE_VIEW ");
				    handleEmptyContainer();
				    Log.d(TAG, "after MESSAGE_UPDATE_VIEW ");
					updateView();
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
		case TimecardController.MESSAGE_CONTINUE_ENTRY_COMPLETED:
			getActivity().runOnUiThread(new Runnable() {
				@Override
				public void run() {
					Log.d(TAG, "run MESSAGE_CONTINUE_ENTRY_COMPLETED ");
					Log.d(TAG, "after MESSAGE_UPDATE_VIEW ");
			        
					final Handler handler = new Handler();
					handler.postDelayed(new Runnable() {
						@Override
						public void run() {
							mRunningFragment.resetWhatSelection();
							mRunningFragment.showRunning();
							model.setContinueEntry(null);
					        updateView();

						}
					}, 3000);

				}
			});
			return true;			
		case TimecardController.MESSAGE_LOADING_NOT_REQUIRED:
			getActivity().runOnUiThread(new Runnable() {
				@Override
				public void run() {
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
				   globalState.setTimecardModel(null);
				   onStartInit();
				   handleEmptyContainer();
				}
			});
			return true;
		}
		return false;
	}
	
	/** 
	 * Remove visual hint container when new activity is added or CLOCKIN event occur.
	 * this can also perhaps be moved under ListView.Empty logic in future
	 */
	
	private void handleEmptyContainer() {
		log("Entering class.TimecardFragment method.handleEmptyContainer");
	    View emptyContainer = (View) view.findViewById(R.id.empty_container);
		if (emptyContainer != null && model.isClockedIn()) {
			  
				ViewGroup parent = (ViewGroup) emptyContainer.getParent();
				parent.removeView((View) emptyContainer);
			
		} else if (emptyContainer == null && model.getTimecardStatus() == TimecardModel.TIMESHEET_STATUS_NOTSTARTED) {
			LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(getActivity().LAYOUT_INFLATER_SERVICE);
			ViewGroup parent = (ViewGroup) view.findViewById(R.id.entries_list_container);
		    View emptyC = inflater.inflate(R.layout.container_empty, parent, true);
		}
	}

	public void deleteTimecard(int selectedModelId) {	
		if (selectedModelId == model.getId()) {
			controller.handleMessage(TimecardController.MESSAGE_DELETE_TIMECARD, model.getId());			
		}
	}
        
    public void insertEntryBeforePosition(int beforePosition) {
    	TimecardEntry entryAfter = model.getTimecardEntryList().get(beforePosition);
    	TimecardEntry entryBefore = model.getTimecardEntryList().get(beforePosition-1);
    	TimecardEntry entryNew = new TimecardEntry(null, null, null, entryBefore.getTimeEnd(), entryAfter.getTimeStart());
    	model.getTimecardEntryList().add(entryNew);
    	controller.handleMessage(TimecardController.MESSAGE_SAVE_MODEL);	
    }

	public void setAnimateLastItem(boolean b) {
		entriesAdapter.setAnimateLastItem(b);
		
	}

	/** 
	 * 
	 * This method is called when Start New Activity Button is pressed 
	 */
	public void onStartRunningActivity(TimecardEntry entry) {		
		controller.handleMessage(TimecardController.MESSAGE_ADD_ENTRY,entry);
		scrollFotterUp ();
		
	}
	
	public void onStopRunningActivity() {
		setAnimateLastItem(true);
		model.stopActiveEntry ();
        controller.handleMessage(TimecardController.MESSAGE_STOP_CURRENT_ENTRY);

		FragmentTransaction ft = getChildFragmentManager().beginTransaction();
		ft.setCustomAnimations(R.anim.alpha_anim_long, R.anim.alpha_anim_reverse_long);
		mRunningFragment.stopRunning();
		mWhatfragment.resetWhatSelection();
        ft.hide(mRunningFragment); 
        ft.show(mWhatfragment);
        ft.commit();
	}

	/**
	 * OnContinueRunningActivity is called from running activity fragment to create
	 * continuation of the activity on the current. Current activity is stopped 
	 * and after smooth transition next activity is started with the previous 
	 * activity sliding up into list of completed activities. 
	 * @param entry
	 */
	public void onContinueRunningActivity(final TimecardEntry entry) {
		// stop timer on current activity fragment  
		mRunningFragment.stopRunning();
		//update fragment with the running activity
		TimecardEntry runningEntry = model.getActiveEntry();
		mRunningFragment.setEntry(runningEntry);
		// stop running activity in model 
		model.stopActiveEntry ();
		
		// set animation flag that is used by the list to decide if last item should be animated
		setAnimateLastItem(true);
		
		// delay the start of the new activity to ensure list has time to scroll up. 
		final Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				// show "starting" fragment for half a sec between transitions 
				mRunningFragment.showLoading();
		        model.setContinueEntry(entry); 
		        entry.setTimeStart(fDATE.getTimeNow());
		        // save currently running activity and create new in database
				controller.handleMessage(TimecardController.MESSAGE_CONTINUE_CURRENT_ENTRY);  
			}
		}, 500);
		
	}
	
	public void animatCurentActivityOnContinue() {
		
		scrollFotterUp ();
		
	}
	
	/** 
	 * 
	 * This method is called from a callback function to evaluate if 
	 * list view shall be scrolled up before WHAT ACTIVITY dialog is displayed. 
	 */
	
	public void onWhatNextActivityClicked(Object sourceObj) {

		slideFooterCallerObj = sourceObj;
        View c = mListView.getChildAt(mListView.getChildCount()-1);
        if (c == null) {
            return;
        }
        int [] locArray = new int [2];   
        c.getLocationOnScreen(locArray);
        int lastListViewItemY = locArray[1];
		Rect rDisplay = new Rect();
		Display display = getActivity().getWindowManager().getDefaultDisplay();
		display.getRectSize(rDisplay);
        int footerHeight =getResources().getDimensionPixelSize(R.dimen.entry_header);
		// if footer is reaching to close display size scroll it up.
		if (rDisplay.bottom - lastListViewItemY < footerHeight - 20) {
			Log.d(TAG, "Need Scroll " + " rDisplay.bottom " + rDisplay.bottom + " lastListViewItemY " + lastListViewItemY );
			scrollFotterUp ();
		} else {
			if (slideFooterCallerObj instanceof TimecardWhatActivityFragment) {
			    ((TimecardWhatActivityFragment) slideFooterCallerObj).displayActivityTypeDialog();
			} else if (slideFooterCallerObj instanceof TimecardCurrentActivityFragment) {
				((TimecardCurrentActivityFragment) slideFooterCallerObj).displayActivityTypeDialog();				
			}
		    slideFooterCallerObj = new Object();
		}
		
	}
	
	public void scrollFotterUp () {
		mListView.smoothScrollToPosition(entriesAdapter.getCount());
	}

	/**
	 * is called when this fragment is first initiated
	 */
	private void manageInitialViewUpdate() {
	    tcId = getActivity().getIntent().getIntExtra(TC_ID, globalState.getTimecardModel().getId() );

	    /** get persisted TC ID*/
	    dao = new TimecardDao();
	    if (tcId < 0) {
		   tcId = Integer.valueOf(dao.getDBParameter(TimecardDao.V_PARAM_ACTIVE_TIMECARD_MODEL_ID));
	    }
	    
	    /** determine to load either persisted model or create new*/
	    if (tcId < 0 ) {
	    	updateView();
			// Make view visible 
			if (view.getVisibility() == view.INVISIBLE) {
				view.setVisibility(view.VISIBLE);
			}
	    } else {	    
	       populateModelById(tcId);
	    }		
	}
	
    private void showTop(String param) {
		Toast toast= Toast.makeText(getActivity(), param, Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.TOP|Gravity.CENTER, 0, 0);
		toast.show();
    }    
    
    private void showCenter(String param) {
		Toast toast= Toast.makeText(getActivity(), param, Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER|Gravity.CENTER, 0, 0);
		toast.show();
    }       

    private void log(String str) {
    	Log.d(MainActivity.DEBUGTAG, str);
    }

}
