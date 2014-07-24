package com.cummins.mowo.widgets;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.text.format.Time;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.FrameLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ViewFlipper;
import android.widget.ViewSwitcher;
import android.widget.FrameLayout;
import android.widget.TabHost.TabSpec;
import android.widget.TabHost.TabContentFactory;

import com.cummins.mowo.R;
import com.cummins.mowo.activity.timecard.TimecardDetailsFragmentClock.TimecardsDetailClockListener;
import com.cummins.mowo.vos.PunchData;

public class ClockControlActivityDialog extends Dialog {

	
	private static final String TAG = ClockControlActivityDialog.class.getSimpleName();
	public static final String AUTOPUNCH_YES = "Y";
	public static final String AUTOPUNCH_NO = "N";
	
	private SetTimeActivityListener mCallback;
	private SeekBar mBadgeControl;
	private Context context; 
	private LocationManager locationManager;
	private Location location;
	private double latitude = 0.0;
	private double longtitude = 0.0;
	private Address address;
	private String addressText;
	private PunchData punchData;
    private ViewFlipper flippy;
    private TextView mSetManualTimeBtn;
    private TextView mLinkWithPrevBtn;
    private Time time;
    private LayoutInflater inflater;
    
    private DatePicker datePicker;
    private TimePicker timePicker;
    private TextView setTimeBtn;
    private TextView timeBefore;
    private TextView timeAfter;
    private TextView dateAfter;
	
	public interface SetTimeActivityListener {
		public void onManualTimeSet(Time time);
	}
	
	public ClockControlActivityDialog(Context context, SetTimeActivityListener mCallback , Time time, String title) {
		super(context, R.style.DialogSlideAnim);
		setWindowSize();
        //this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		//this.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		this.context = context;
		this.mCallback = mCallback;
		this.time = time;
		this.setTitle(title);
	    this.inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
		this.setContentView(R.layout.dialog_clock_control_activity);
	}
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
	
		
	    datePicker = (DatePicker) this.findViewById(R.id.datePicker1);
	    timePicker = (TimePicker) this.findViewById(R.id.timePicker1);

		datePicker.setCalendarViewShown(false);
	    setTimeBtn = (TextView) this.findViewById(R.id.dialog_set_button);
		timeAfter = (TextView) this.findViewById(R.id.set_time_after_time);
		dateAfter = (TextView) this.findViewById(R.id.set_time_after_date);
		
		TabHost tabHost = (TabHost) findViewById(android.R.id.tabhost);
		tabHost.setup();
		
		final TabWidget tabWidget = tabHost.getTabWidget();
		final FrameLayout tabContent = tabHost.getTabContentView();
		
		// Get the original tab textviews and remove them from the viewgroup.
		TextView[] originalTextViews = new TextView[tabWidget.getTabCount()];
		for (int index = 0; index < tabWidget.getTabCount(); index++) {
			originalTextViews[index] = (TextView) tabWidget.getChildTabViewAt(index);
		}
		tabWidget.removeAllViews();
		
		
		// Ensure that all tab content childs are not visible at startup.
		for (int index = 0; index < tabContent.getChildCount(); index++) {
			tabContent.getChildAt(index).setVisibility(View.GONE);
		}
		
		// Create the tabspec based on the textview childs in the xml file.
		// Or create simple tabspec instances in any other way...
		for (int index = 0; index < originalTextViews.length; index++) {
			final TextView tabWidgetTextView = originalTextViews[index];
			final View tabContentView = tabContent.getChildAt(index);
			TabSpec tabSpec = tabHost.newTabSpec((String) tabWidgetTextView.getTag());
			tabSpec.setContent(new TabContentFactory() {
				@Override
				public View createTabContent(String tag) {
					return tabContentView;
				}
			});
			
			int tabText = 0;
			if (index == 0 ) {
				tabText = R.string.tab_pick_time;
			} else if (index == 1) {
				tabText = R.string.tab_pick_date;
			}
			
			tabSpec.setIndicator(getTabIndicator(tabHost.getContext(), tabText, android.R.drawable.star_on)); // new function to inject our own tab layout
			tabHost.addTab(tabSpec);
			
		}
		
		tabHost.setCurrentTab(0);
		
        Log.d(TAG, "OnCreate is called");
    }
    
    
	
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();

		timeAfter.setText(time.format("%I:%M %p"));
		dateAfter.setText(time.format("%d.%h.%y"));

		// set current time into timepicker
		timePicker.setCurrentHour(time.hour);
		timePicker.setCurrentMinute(time.minute);
		
	    // initialize datePicker and set listener  
		datePicker.init(time.year, time.month, time.monthDay, new OnDateChangedListener() {

			@Override
			public void onDateChanged(DatePicker view, int year,
					int monthOfYear, int dayOfMonth) {
				time.set(0, time.minute, time.hour, dayOfMonth, monthOfYear, year);
				dateAfter.setText(time.format("%d.%h.%y"));
				
			}
			
		});
		
		// listen to timer change 
		timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
			
			@Override
			public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
				time.set(0, minute, hourOfDay, time.monthDay, time.month, time.year);
				timeAfter.setText(time.format("%I:%M %p" ));
			}
		});
			
		
		setTimeBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Log.d(TAG, "Passing data back to parent class from clock activity time change widget");
				ClockControlActivityDialog.this.dismiss();
				mCallback.onManualTimeSet(time);
			}
		});
	}
	
    private View getTabIndicator(Context context, int title, int drawable) {
    	
        View view = inflater.from(context).inflate(R.layout.tab_layout, null);
        TextView tv = (TextView) view.findViewById(R.id.textView);
        tv.setText(title);
        return view;
    }
    
	private void setWindowSize() {
        //set the size of the dialog
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindow().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        getWindow().setGravity(Gravity.TOP); 
        double sizeDialog = 0.30;
        int width = (int) (displaymetrics.widthPixels * 1);
        int height = (int) (displaymetrics.heightPixels * 1);
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = width;
        params.height = height;
        getWindow().setAttributes(params);
	}

	
}
