package com.cummins.mowo.activity;

import com.cummins.mowo.R;
import com.cummins.mowo.activity.timecard.TimecardDetailFragmentManual;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class ShowGPSActivity extends Activity {

	private static final String TAG = ShowGPSActivity.class.getSimpleName();
	
	private Button show;
	private double current_location_latitude = 0.0;
	private double current_location_longtitude = 0.0;
	Location location;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.test_gps);
		show = (Button) findViewById(R.id.btn_show);

		
		LocationManager locationManager = (LocationManager) this
				.getSystemService(Context.LOCATION_SERVICE);
		
		Criteria criteria = new Criteria();

		String provider = locationManager.getBestProvider(criteria, false);
		
		
		location = locationManager.getLastKnownLocation(provider);
		
			
		if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && location != null) {
			current_location_latitude = location.getLatitude();
			current_location_longtitude = location.getLongitude();
		} else {
			AlertDialog.Builder alertDialog = new AlertDialog.Builder(ShowGPSActivity.this);
			alertDialog.setTitle("GPS is settings");
			alertDialog.setMessage("GPS is not enabled. Do you want to enable it?");

			alertDialog.setPositiveButton("Yes",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							try {
								Intent callGPSSettingIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
								startActivity(callGPSSettingIntent);
								current_location_latitude = location.getLatitude();
								current_location_longtitude = location.getLongitude();
							} catch (Exception e) {
								current_location_latitude = 0.0;
								current_location_longtitude = 0.0;
							}
						}
					});

			// on pressing cancel button
			alertDialog.setNegativeButton("Cancel",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();
							finish();
						}
					});

			// Showing Alert Message
			alertDialog.show();

		}
		
		
	      // Get a handle to the Map Fragment
        GoogleMap map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        
        
        LatLng clocklocation = new LatLng(current_location_latitude, current_location_longtitude);

        map.setMyLocationEnabled(true);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(clocklocation, 13));

        map.addMarker(new MarkerOptions()
                .title("Clock In")
                .snippet("Location at the time of session clock in")
                .position(clocklocation))
                .isVisible();
        
        show.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Toast.makeText(ShowGPSActivity.this, "Your location is"+"\n"+"Latitude: "+current_location_latitude+"\n"+"Longitude"+current_location_longtitude, Toast.LENGTH_LONG).show();
				
			}
		}); 
		
	}

}
