package com.cummins.mowo.activity.timecard;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.cummins.mowo.R;
import com.cummins.mowo.model.TimecardModel;
import com.cummins.mowo.vos.OnChangeListener;


public class TimecardDayFragment extends Fragment implements OnChangeListener<TimecardModel>, Handler.Callback {

	private static final String TAG = TimecardDayFragment.class.getSimpleName();
	
	private TimecardsDetailManualDayListener mCallback;
    private View view;
	private TextView mDayDateHolder;
    
   	public interface TimecardsDetailManualDayListener {

	}    
   	
	public void initDate(String date) {
		mDayDateHolder.setText(date);
	}
	
    public void setData(String date) {
    	mDayDateHolder.setText(date);
        // The reload fragment code here !
        if (this.isDetached()) {
            getFragmentManager().beginTransaction()
               .detach(this)
               .attach(this)
               .commit();
        }
    }	
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
	        
		view = inflater.inflate(R.layout.fragment_timecard_day, container,false);				
		return view;
	}

	@Override
	public void onStart() {
		super.onStart();
	    mDayDateHolder = (TextView) view.findViewById(R.id.day_date_holder);
	}
	
	@Override
	public void onChange(TimecardModel model) {		
		
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	
	@Override
	public void onPause() {
		super.onPause();
	}	
    
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
		// This makes sure that the container activity has implemented
		// the callback interface. If not, it throws an exception.
		try {
			mCallback = (TimecardsDetailManualDayListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement TimecardsDetailManualDayListener");
		}
	}


	@Override
	public void onDestroyView() {
		super.onDestroyView();
	} 
    
    private void showTop(String param) {
		Toast toast= Toast.makeText(getActivity(), param, Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.TOP|Gravity.CENTER, 0, 0);
		toast.show();
    }    

    private void log(String str) {
    	Log.d(TAG, str);
    }

	@Override
	public boolean handleMessage(Message arg0) {
		// TODO Auto-generated method stub
		return false;
	}



}
