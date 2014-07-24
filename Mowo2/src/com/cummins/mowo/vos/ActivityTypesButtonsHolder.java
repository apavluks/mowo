package com.cummins.mowo.vos;

import java.util.ArrayList;
import java.util.HashMap;

import android.util.SparseArray;

import com.cummins.mowo.R;
import com.cummins.mowo.vos.ActivityType;

public class ActivityTypesButtonsHolder {

	public static final Integer ACTIVITY_TYPE_UNDEFINED = 1;
	public static final Integer ACTIVITY_TYPE_TRAVEL_TO = 100;
	public static final Integer ACTIVITY_TYPE_WORK_IN = 200;
	public static final Integer ACTIVITY_TYPE_TRAVEL_FROM = 110;
	public static final Integer ACTIVITY_TYPE_INTERNAL = 300;
	public static final Integer ACTIVITY_TYPE_BREAK = 400;
	public static ActivityType travelTo = new ActivityType(R.drawable.ic_truck_out_dark_48, R.drawable.ic_truck_out_48,"Travel To", true, ACTIVITY_TYPE_TRAVEL_TO);
	public static ActivityType workIn = new ActivityType(R.drawable.ic_work_billable_dark_48, R.drawable.ic_work_billable_48,"Work In", true, ACTIVITY_TYPE_WORK_IN);;
	public static ActivityType travelFrom = new ActivityType(R.drawable.ic_truck_back_dark_48, R.drawable.ic_truck_back_48,"Travel Back", true, ACTIVITY_TYPE_TRAVEL_FROM);;
	public static ActivityType internal = new ActivityType(R.drawable.ic_internalwork_48, R.drawable.ic_internalwork_48,"Internal", false, ACTIVITY_TYPE_INTERNAL);;
	public static ActivityType breakYay = new ActivityType(R.drawable.ic_coffee_white_48, R.drawable.ic_coffee_white_48,"Break", false, ACTIVITY_TYPE_BREAK);;
	public static ActivityType undefined = new ActivityType(R.drawable.ic_coffee_white_48, R.drawable.ic_coffee_white_48,"Break", false, ACTIVITY_TYPE_UNDEFINED);;
	private ArrayList<ActivityType> buttonArray = new ArrayList<ActivityType>();
    public static SparseArray<ActivityType> mActivityTypesMap;
	
    public ActivityTypesButtonsHolder() {

    	buttonArray.add(travelTo);
		buttonArray.add(workIn);
		buttonArray.add(travelFrom);
		buttonArray.add(internal);
		buttonArray.add(breakYay);
		//buttonArray.add(undefined);
		
	}
    
    static
    {
    	mActivityTypesMap = new SparseArray<ActivityType>();
    	mActivityTypesMap.put(ACTIVITY_TYPE_TRAVEL_TO, travelTo);
    	mActivityTypesMap.put(ACTIVITY_TYPE_WORK_IN, workIn);
    	mActivityTypesMap.put(ACTIVITY_TYPE_TRAVEL_FROM, travelFrom);
    	mActivityTypesMap.put(ACTIVITY_TYPE_INTERNAL, internal);
    	mActivityTypesMap.put(ACTIVITY_TYPE_BREAK, breakYay);    	
    	mActivityTypesMap.put(ACTIVITY_TYPE_UNDEFINED, undefined);    
    }

	public ArrayList<ActivityType> getButtonArray() {
		return buttonArray;
	}
		
}
