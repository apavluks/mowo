package com.cummins.mowo.utils;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class CustomAdapter extends ArrayAdapter {
	
	private Context context;
	private int textViewResourceId;
	private String[] objects;
	public boolean flag = false;

	public CustomAdapter(Context context, int textViewResourceId,
			String[] objects) {
		super(context, textViewResourceId, objects);
		this.context = context;
		this.textViewResourceId = textViewResourceId;
		this.objects = objects;
	}
	
    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent)
    {
        View v = null;

        // If this is the initial dummy entry, make it hidden
        if (position == 0) {
            TextView tv = new TextView(getContext());
            tv.setHeight(0);
            tv.setVisibility(View.GONE);
            v = tv;
        }
        else {
            // Pass convertView as null to prevent reuse of special case views
            v = super.getDropDownView(position, null, parent);
        }

        // Hide scroll bar because it appears sometimes unnecessarily, this does not prevent scrolling 
        parent.setVerticalScrollBarEnabled(false);
        return v;
    }
	
	/*
	private Context context;
	private int textViewResourceId;
	private String[] objects;
	public boolean flag = false;

	public CustomAdapter(Context context, int textViewResourceId,
			String[] objects) {
		super(context, textViewResourceId, objects);
		this.context = context;
		this.textViewResourceId = textViewResourceId;
		this.objects = objects;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null)
			convertView = View.inflate(context, textViewResourceId, null);
		if (flag != false) {
			TextView tv = (TextView) convertView;
			tv.setText(objects[position]);
		}
		return convertView;
	}

	public boolean isFlag() {
		return flag;
	}

	public void setFlag(boolean flag) {
		this.flag = flag;
	}
	
	*/
	
}
