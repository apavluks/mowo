package com.cummins.mowo.vos;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.cummins.mowo.R;
import com.cummins.mowo.vos.ActivityType;

public class ActivityJobOptionHolder {

	private ArrayList<ActivityJobOption> buttonArray = new ArrayList<ActivityJobOption>();
    private Context mContext;
	
    public ActivityJobOptionHolder(Context context) {
        this.mContext = context;
		
	    buttonArray.add(new ActivityJobOption(R.drawable.ic_arrow_continue_48,"Continue", true));
		buttonArray.add(new ActivityJobOption(R.drawable.ic_job_list_48,"From list", false));
		buttonArray.add(new ActivityJobOption(R.drawable.ic_blank_48,"Blank", false));
	}

	public ArrayList<ActivityJobOption> getButtonArray() {
		return buttonArray;
	}
		
}
