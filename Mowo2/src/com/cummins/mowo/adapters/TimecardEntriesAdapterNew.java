/*
 * Copyright (C) 2013 47 Degrees, LLC
 *  http://47deg.com
 *  hello@47deg.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.cummins.mowo.adapters;

import java.util.List;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.format.Time;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Space;
import android.widget.TextView;
import android.view.Window;
import android.graphics.drawable.ColorDrawable;

import com.cummins.mowo.R;
import com.cummins.mowo.activity.MainActivity;
import com.cummins.mowo.activity.timecardentry.TimecardEntryActivity;
import com.cummins.mowo.conrollers.timecard.TimecardController;
import com.cummins.mowo.functions.fDATE;
import com.cummins.mowo.model.JobsModel;
import com.cummins.mowo.model.TimecardModel;
import com.cummins.mowo.vos.TimecardEntry;
import com.cummins.mowo.widgets.ClockControlActivityDialog;
import com.cummins.mowo.widgets.EntryTimerManager;
import com.cummins.mowo.widgets.NewActivityDialog;
import com.cummins.mowo.widgets.TimePickerDialog;
import com.doomonafireball.betterpickers.datepicker.DatePickerBuilder;
import com.doomonafireball.betterpickers.radialtimepicker.RadialPickerLayout;
import com.doomonafireball.betterpickers.radialtimepicker.RadialTimePickerDialog;

public class TimecardEntriesAdapterNew extends BaseAdapter {

	private final static String TAG = TimecardEntriesAdapterNew.class.getSimpleName();
	
	private final static int SET_START_FIELD = 1;
	private final static int SET_END_FIELD = 2;
    private List<TimecardEntry> data;
    public Context context;
    private EntryTimerManager tm;
    private TimecardController controller;
    private Time newTime;
    private boolean animateLastItem = false;
    
    private int selected = 0; 
    private int buffKey = 0; // add buffer value
    
    private int mLastPosition;

    public TimecardEntriesAdapterNew(Context context, List<TimecardEntry> data, TimecardController controller) {
        this.context = context;
        this.data = data;
        this.tm = tm;
        this.controller = controller;
    }

    @Override
    public int getCount() {
    	return data.size();
    }

    @Override
    public TimecardEntry getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

//    @Override
//    public boolean isEnabled(int position) {
//        if (position == 2) {
//            return false;
//        } else {
//            return true;
//        }
//    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
    	
    	Log.d(TAG, "Entering TimecardEntriesAdapterNew");
    	Log.d(TAG, "Data size " + data.size() );
    	
    	int maxPosition = data.size() - 1;
    	
        final TimecardEntry item = getItem(position);
        ViewHolder holder;
        if (convertView == null) {
            LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = li.inflate(R.layout.fragment_timecard_swipelist_item, parent, false);

            holder = new ViewHolder();
            holder.ivImage = (ImageView) convertView.findViewById(R.id.list_image);
            holder.jobId = (TextView) convertView.findViewById(R.id.swiperow_activity_job_id);
            holder.activityTypeContainer = (RelativeLayout) convertView.findViewById(R.id.container_for_activity_type);
            holder.activityType = (TextView) convertView.findViewById(R.id.swiperow_activity_type);
            
           // holder.actTitle = (TextView) convertView.findViewById(R.id.activity_type_title);
            holder.actClockIn = (TextView) convertView.findViewById(R.id.activity_clock_in_time);
            holder.actClockOut = (TextView) convertView.findViewById(R.id.activity_clock_out_time);
            holder.actDuration = (TextView) convertView.findViewById(R.id.activity_duration);
            holder.actGap = (TextView) convertView.findViewById(R.id.swiperow_activity_gap);
            holder.actComment = (TextView) convertView.findViewById(R.id.swiperow_comments);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.actClockIn.setText(item.getTimeStartString());
        holder.actClockOut.setText(item.getTimeEndString());
        
        String gapSymbol = "";
        if (item.getGapType() == TimecardEntry.GAP_APART) {
        	gapSymbol = "+ ";
        } else if (item.getGapType() == TimecardEntry.GAP_OVERLAP) {
        	gapSymbol = "- ";
        }
        
        holder.actGap.setText(gapSymbol + fDATE.formatElapsedTime(item.getGapInMillis()));
        holder.activityType.setText(item.getActivityTypeString());
        holder.actComment.setText(item.getCommentString());
        holder.actDuration.setText(item.getDurationString());
        
        holder.ivImage.setImageDrawable(context.getResources().getDrawable(item.getActivityType().getListIcon()));
        //Log.d(TAG, "GetGapMill " + String.valueOf(item.getGapInMillis()) + " sec " + item.getGapInMillis() / 1000 / 60);
        
        //if gap between activiites is more than minute
        if (item.getGapInMillis() / 1000 / 60 >= 1 ) {
            holder.actGap.setVisibility(holder.actGap.VISIBLE);
            holder.actGap.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					showAlertDialogGap(fDATE.formatElapsedTime(item.getGapInMillis()), position);
					
				}
            	
            });
        } else {
        	holder.actGap.setVisibility(holder.actGap.INVISIBLE);
        }          
        
        if (item.getJobNumberString() != null) {
         	String job = "Job: " + item.getJobNumberString();
            holder.jobId.setText(job);
        } else {
        	holder.jobId.setText("");
        }
        /** setTags on these fiels so entryTimerManager class that is called from the main fragment 
         *  can find and updates these views dynamically based on current tiem - start time logic 
         *  */
           holder.actClockOut.setTag(EntryTimerManager.FIELD_END_TIME + String.valueOf(item.getId()));
           holder.actDuration.setTag(EntryTimerManager.FIELD_DURATION + String.valueOf(item.getId()));
         
           // This tells the view where to start based on the direction of the scroll.
           // If the last position to be loaded is <= the current position, we want
           // the views to start below their ending point (500f further down).
           // Otherwise, we start above the ending point.
        
		if (position == maxPosition && animateLastItem) {
			// float initialTranslation = (mLastPosition <= position ? 100f : -100f);
			float initialTranslation = 90f;

			convertView.setTranslationY(initialTranslation);
			convertView
					.animate()
					.setInterpolator(new DecelerateInterpolator(1.0f))
					.translationY(0f)
					.setDuration(1000l)
					.setListener(new InnerAnimatorListener(convertView, position, maxPosition, (MainActivity) context   )).start();

			// Keep track of the last position we loaded
			//mLastPosition = position;
		}

		Log.d(TAG, "Leaving TimecardEntriesAdapterNew getview()" + position);
        return convertView;
    }

    static class ViewHolder {
        ImageView ivImage;
        TextView activityType;
        TextView actTitle;
        TextView actClockIn;
        TextView actClockOut;
        TextView actDuration;
        TextView actComment;
        TextView actGap;
        TextView actId;
        TextView jobId;
        RelativeLayout activityTypeContainer;
        Button bAction1;
        Button bAction2;
        Button bAction3;
    }
    
	public int getItemPosition(int id)
	{
	    for (int position=0; position<data.size(); position++)
	        if (data.get(position).getId() == id)
	            return position;
	    return 0;
	}
	
	
	/**
	 * disable list items
	 */
	@Override
	public boolean isEnabled(int position) {
		// TODO Auto-generated method stub
		return false;
	}

	public void showAlertDialogGap(String gap, final int beforePosition) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				context);

		// set title
		alertDialogBuilder.setTitle("Unallocated time between activities");

		// set dialog message
		alertDialogBuilder
				.setMessage("Allocate " + gap + " to a new activity?")
				.setCancelable(false)
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								// if this button is clicked replace gap with new activity
								MainActivity ma = (MainActivity) context;
								ma.insertEntryBeforePosition(beforePosition);
							}
						})
				.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								// if this button is clicked, just close
								// the dialog box and do nothing
								dialog.cancel();
							}
						});

		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();

		// show it
		alertDialog.show();
	}
	
	private void showDialogButtonClick() {
	    Log.i(TAG, "show Dialog ButtonClick");
	    AlertDialog.Builder builder = 
	        new AlertDialog.Builder(context);
	    builder.setTitle("Show dialog");
	     
	    final CharSequence[] choiceList = 
	    {"Coke", "Pepsi" , "Sprite" , "Seven Up" };

	    
	    builder.setSingleChoiceItems(
	            choiceList, 
	            selected, 
	            new DialogInterface.OnClickListener() {
	         
	        @Override
	        public void onClick(
	                DialogInterface dialog, 
	                int which) {
	            //set to buffKey instead of selected 
	            //(when cancel not save to selected)
	            buffKey = which;
	        }
	    })
	    .setCancelable(false)
	    .setPositiveButton("Save", 
	        new DialogInterface.OnClickListener() 
	        {
	            @Override
	            public void onClick(DialogInterface dialog, 
	                    int which) {
	                Log.d(TAG,"Which value="+which);
	                Log.d(TAG,"Selected value="+buffKey);
	                Log.d(TAG,"Select "+choiceList[buffKey]);
	                //set buff to selected
	                selected = buffKey;
	            }
	        }
	    )
	    .setNegativeButton("Canel", 
	        new DialogInterface.OnClickListener() 
	        {
	            @Override
	            public void onClick(DialogInterface dialog, 
	                    int which) {
	            	Log.d(TAG,"Cancel click");
	            }
	        }
	    );
	     
	    AlertDialog alert = builder.create();
	    alert.show();
	}
	
	public void selectNewTime(final TimecardEntry item, final int field) {
		
		Log.d(TAG, "Entering method setTime");
		
		// decide if we are dealing with start or end time
		switch(field) {
		case SET_START_FIELD:
			// there should always be a start time in the model
			newTime = item.getTimeStart();
			Log.d(TAG, "Existing start time " + newTime.HOUR + ":" + newTime.MINUTE + " stirngval " + item.getTimeStartString());
			break;
		case SET_END_FIELD:
		    // if end time is null that means activity is running to set it to the current time.
			if (item.getTimeEndString() == null) {
				newTime.setToNow();
			} else {
				newTime = item.getTimeEnd();
			}
			Log.d(TAG, "Existing end time " + newTime.HOUR + ":" + newTime.MINUTE + " stirngval " + item.getTimeEndString());
			break;
		}
		
		// instantiate timePicker class
		TimePickerDialog timePicker = new TimePickerDialog();
		
		//set initial position of the time picker 
		timePicker.setStartTime(newTime.hour, newTime.minute);
		
		// set call back listener on what happens when time is set 
		TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {

			@Override
			public void onTimeSet(RadialPickerLayout view, int hourOfDay,
					int minute) {
				
				// set new time 
				newTime.set(0, minute, hourOfDay,  newTime.monthDay, newTime.month, newTime.year);
				
				// set field value 
				if (field == SET_START_FIELD) {
				    item.setTimeStart(newTime);
				} else if (field == SET_END_FIELD) {
					item.setTimeEnd(newTime);
				}
				
				// save model to apply time change
				controller.handleMessage(TimecardController.MESSAGE_SAVE_MODEL);
				
			}
		};
		
		timePicker.setOnTimeSetListener(onTimeSetListener);
		
		
		// show time picker dialog
		timePicker.show(((MainActivity) context).getSupportFragmentManager(), "test");
		
	}
	
    public boolean isAnimateLastItem() {
		return animateLastItem;
	}

	public void setAnimateLastItem(boolean animateLastItem) {
		this.animateLastItem = animateLastItem;
	}

	/**
     * Animator listener enables scrolling of the footer when last item's animation is complete 
     *
     */
	static class InnerAnimatorListener implements AnimatorListener {

	    private View v;

	    private int layerType;
	    private int position;
	    private int maxposition;
	    private MainActivity ma;

	    public InnerAnimatorListener(View v, int position, int maxposition, MainActivity ma) {
	        this.v = v;
	        this.position = position;
	        this.maxposition = maxposition;
	        this.ma = ma;
	    }

	    @Override
	    public void onAnimationStart(Animator animation) {
	        layerType = v.getLayerType();
	        v.setLayerType(View.LAYER_TYPE_HARDWARE, null);
	    }

	    @Override
	    public void onAnimationEnd(Animator animation) {
	        v.setLayerType(layerType, null);
	        if (position == maxposition) {
	          Log.d(TAG, "Animation ended pos " + position + " max " + maxposition);
	          ma.setAnimateLastItem(false);
	          ma.animatCurentActivityOnContinue();
	        }
	    }

		@Override
		public void onAnimationCancel(Animator animation) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onAnimationRepeat(Animator animation) {
			// TODO Auto-generated method stub
			
		}
	}
}
