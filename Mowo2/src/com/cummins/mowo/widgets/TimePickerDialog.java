package com.cummins.mowo.widgets;

import com.doomonafireball.betterpickers.radialtimepicker.RadialTimePickerDialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.Gravity;

@SuppressLint("ValidFragment")
public class TimePickerDialog extends RadialTimePickerDialog {

	private final static String TAG = TimePickerDialog.class.getSimpleName();
	private Dialog dialog;
	
	public TimePickerDialog() {
		super();
		// TODO Auto-generated constructor stub
	}

	public TimePickerDialog(Context context, int theme,
			OnTimeSetListener callback, int hourOfDay, int minute,
			boolean is24HourMode) {
		
		super(context, theme, callback, hourOfDay, minute, is24HourMode);
		dialog = super.getDialog();
		// TODO Auto-generated constructor stub
	
	}
	

}
