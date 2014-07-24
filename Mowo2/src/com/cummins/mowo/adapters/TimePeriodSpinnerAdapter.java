package com.cummins.mowo.adapters;

import com.cummins.mowo.R;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class TimePeriodSpinnerAdapter extends ArrayAdapter {

	private Context context;
	private int textViewResourceId;
	private String[] spinnerValues;

	public TimePeriodSpinnerAdapter(Context context, int textViewResourceId,
			String[] spinnerValues) {
		super(context, textViewResourceId, spinnerValues);
		this.context = context;
		this.textViewResourceId = textViewResourceId;
		this.spinnerValues = spinnerValues;
	}

	
    @Override
    public int getCount() {
        return spinnerValues.length;
    }

    @Override
    public String getItem(int position) {
        return spinnerValues[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent)
    {
        View v = super.getDropDownView(position, null, parent);

        v = setViewValues(v, position);

        // Hide scroll bar because it appears sometimes unnecessarily, this does not prevent scrolling 
        parent.setVerticalScrollBarEnabled(false);
        return v;
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
    	
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.fragment_time_period_spinner_item, null);
		view = setViewValues(view, position);
		
        return view;
    }
    
    public View setViewValues( View v , int position) {
    
    	View view = v; 

  		String spinnerValue = spinnerValues[position];
  		String[] splittedString = spinnerValue.split("-");
  		String key = splittedString[0];
  		String value = splittedString[1];
  	
		TextView timePeriod = (TextView) view.findViewById(R.id.time_period_textview);
		
		timePeriod.setText(value);
		timePeriod.setTag(key);
    	  
    	return view;
    }
}
