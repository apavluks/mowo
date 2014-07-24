package com.cummins.mowo.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cummins.mowo.R;

public class CurrentTimecardFragment extends Fragment {

	 public static final String EXTRA_MESSAGE = "EXTRA_MESSAGE";

	 

	 public static final CurrentTimecardFragment newInstance(String message)

	 {

	   CurrentTimecardFragment f = new CurrentTimecardFragment();

	   Bundle bdl = new Bundle(1);

	   bdl.putString(EXTRA_MESSAGE, message);

	   f.setArguments(bdl);

	   return f;

	 }

	 

	 @Override

	 public View onCreateView(LayoutInflater inflater, ViewGroup container,

	   Bundle savedInstanceState) {

	   //String message = getArguments().getString(EXTRA_MESSAGE);

	   View v = inflater.inflate(R.layout.myfragment_layout, container, false);

	   TextView messageTextView = (TextView)v.findViewById(R.id.textView);

	   //messageTextView.setText(message);

	 

	   return v;

	 }

	}
