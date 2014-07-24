package com.cummins.mowo.widgets;

import java.io.IOException;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.cummins.mowo.R;
import com.cummins.mowo.activity.timecard.TimecardDetailsFragmentClock.TimecardsDetailClockListener;
import com.cummins.mowo.vos.PunchData;

public class ClockControlDialog extends Dialog {

	private static final String TAG = ClockControlDialog.class.getSimpleName();
	public static final String AUTOPUNCH_YES = "Y";
	public static final String AUTOPUNCH_NO = "N";
	
	private ClockControlListener mCallback;
	private SeekBar mBadgeControl;
	private Context context; 
	private LocationManager locationManager;
	private Location location;
	private double latitude = 0.0;
	private double longtitude = 0.0;
	private Address address;
	private String addressText;
	private PunchData punchData;
    private ViewSwitcher switcher;
	
	public interface ClockControlListener {
		public void onBadgeSwipe(PunchData punchData);
	}
	
	public ClockControlDialog(Context context, ClockControlListener mCallback ) {
		super(context);
		this.context = context;
		this.mCallback = mCallback;
		this.setContentView(R.layout.dialog_clock_control);
		switcher = (ViewSwitcher) findViewById(R.id.ViewSwitcher1);
	}
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "OnCreate is called");
    }
	
	/** call this method to handle Clock In */
	public void handleAutoPunch(String title) {
		initGPSLocation();
		this.setTitle(title);
        TextView latitudeTextView = (TextView) this.findViewById(R.id.gps_latitute);
        TextView longtitudeTextView = (TextView) this.findViewById(R.id.gps_longtitude);
        TextView addressTextView = (TextView) this.findViewById(R.id.gps_address);
        final TextView swipeProgressTextView = (TextView) this.findViewById(R.id.time_setter_swipe_progress);
        
        latitudeTextView.setText(String.valueOf(latitude));
		longtitudeTextView.setText(String.valueOf(longtitude));
		addressTextView.setText(addressText);
		
        mBadgeControl = (SeekBar) this.findViewById(R.id.time_setter_badge_control);	       
        mBadgeControl.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            int progressChanged = 0;
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
            	if ( progress == 0){
            		swipeProgressTextView.setText("Swipe virtual badge to clock");
				} else {
            	    swipeProgressTextView.setText(String.valueOf(progress)+ "%");
				}
                progressChanged = progress;
            }
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }
            public void onStopTrackingTouch(SeekBar seekBar) {
                Toast.makeText(context,"seek bar progress:"+progressChanged + "%", 
                        Toast.LENGTH_SHORT).show();
				if (progressChanged == 100) {
					ClockControlDialog.this.dismiss();
					punchData = new PunchData();
					punchData.setLatitute(latitude);
					punchData.setLongtitute(longtitude);
					punchData.setAddress(addressText);
					punchData.setAutopanch(AUTOPUNCH_YES);
					mCallback.onBadgeSwipe(punchData); 
					
				} 
				
				
            }
        });
        
        this.show();
        
	}
 
	/** call this method to handle Clock In */
	public void handlePunchEdit(String title, PunchData punchData, String clockTimeString) {
		switcher.showNext();
		this.setTitle(title);
        TextView latitudeTextView = (TextView) this.findViewById(R.id.gps_latitute1);
        TextView longtitudeTextView = (TextView) this.findViewById(R.id.gps_longtitude1);
        TextView addressTextView = (TextView) this.findViewById(R.id.gps_address1);
        TextView clockTime = (TextView) this.findViewById(R.id.clock_time);
        
        latitudeTextView.setText(String.valueOf(punchData.getLatitute()));
		longtitudeTextView.setText(String.valueOf(punchData.getLongtitute()));
		addressTextView.setText(punchData.getAddress());
		clockTime.setText(clockTimeString);
		
        this.show();
        
	}
	
	
	public void showGPSSettingsDialog () {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
		alertDialog.setTitle("GPS settings");
		alertDialog.setMessage("GPS is not enabled. Do you want to enable it?");
		alertDialog.setPositiveButton("Yes",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						try {
							Intent callGPSSettingIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
							context.startActivity(callGPSSettingIntent);
						} catch (Exception e) {
							latitude = 0.0;
							longtitude = 0.0;
						}
					}
				});
		// on pressing cancel button
		alertDialog.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
						
					}
				});
		alertDialog.show();
	}
	
	public void initGPSLocation() {
		
	    /* instantiate location manager*/
		locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		
		Criteria criteria = new Criteria();
		String provider = locationManager.getBestProvider(criteria, false);
		location = locationManager.getLastKnownLocation(provider);
		if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && location != null) {
			latitude = location.getLatitude();
			longtitude = location.getLongitude();
			try {
				// Ensure that a Geocoder services is available
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD
						&& Geocoder.isPresent()) {
					Geocoder geocoder = new Geocoder(context);
					List<Address> addresses;

					addresses = geocoder.getFromLocation(latitude, longtitude,1);
					// If the reverse geocode returned an address
					if (addresses != null && addresses.size() > 0) {
						address = addresses.get(0);
						addressText = String.format(
								"%s, %s, %s, %s",
								// If there's a street address, add it
								address.getMaxAddressLineIndex() > 0 ? address
										.getAddressLine(0) : "",
								// Locality is usually a city
								address.getLocality(),
								// The country of the address
								address.getCountryName(),
								// Get postal code
								address.getPostalCode());
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				Log.e(TAG, "Geocoder Error", e);
			}			
		} else {
		    showGPSSettingsDialog();
		}
	}
}
