package com.cummins.mowo.activity;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.ListFragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.cummins.mowo.activity.FragmentPagerSupport.ArrayListFragment;
import com.cummins.mowo.activity.FragmentPagerSupport.MyAdapter;
import com.cummins.mowo.R;

public class CountingFragment extends FragmentActivity {
	    static final int NUM_ITEMS = 10;

	    MyAdapter mAdapter;

	    ViewPager mPager;

	    @Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.fragment_pager);
	        
	        Log.d(MainActivity.DEBUGTAG, "Inside Counting Fragment top class");

	        mAdapter = new MyAdapter(getSupportFragmentManager());

	        mPager = (ViewPager)findViewById(R.id.pager);
	        mPager.setAdapter(mAdapter);

	        // Watch for button clicks.
	        Button button = (Button)findViewById(R.id.goto_first);
	        button.setOnClickListener(new OnClickListener() {
	            public void onClick(View v) {
	                mPager.setCurrentItem(0);
	            }
	        });
	        button = (Button)findViewById(R.id.goto_last);
	        button.setOnClickListener(new OnClickListener() {
	            public void onClick(View v) {
	                mPager.setCurrentItem(NUM_ITEMS-1);
	            }
	        });
	    }

	    public static class MyAdapter extends FragmentPagerAdapter {
	        public MyAdapter(FragmentManager fm) {
	            super(fm);
	        }

	        @Override
	        public int getCount() {
	            return NUM_ITEMS;
	        }

	        @Override
	        public Fragment getItem(int position) {
	            return ArrayListFragment.newInstance(position);
	        }
	    }

	    public static class ArrayListFragment extends ListFragment {
	        int mNum;

	        /**
	         * Create a new instance of CountingFragment, providing "num"
	         * as an argument.
	         */
	        static ArrayListFragment newInstance(int num) {
	            ArrayListFragment f = new ArrayListFragment();

	            // Supply num input as an argument.
	            Bundle args = new Bundle();
	            args.putInt("num", num);
	            f.setArguments(args);
	            
	            Log.d(MainActivity.DEBUGTAG, "Inside Array List");
	            Log.i("MOWO","Inside Array List");
	            
	            return f;
	        }

	        /**
	         * When creating, retrieve this instance's number from its arguments.
	         */
	        @Override
	        public void onCreate(Bundle savedInstanceState) {
	            super.onCreate(savedInstanceState);
	            mNum = getArguments() != null ? getArguments().getInt("num") : 1;
	        }

	        /**
	         * The Fragment's UI is just a simple text view showing its
	         * instance number.
	         */
	        @Override
	        public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                Bundle savedInstanceState) {
	            View v = inflater.inflate(R.layout.fragment_pager_list, container, false);
	            View tv = v.findViewById(R.id.text);
	            ((TextView)tv).setText("Fragment #" + mNum);
	            return v;
	        }

	        @Override
	        public void onActivityCreated(Bundle savedInstanceState) {
	            super.onActivityCreated(savedInstanceState);

	   
	 	    String[] values = new String[] { "Test1", "Test2", "WindowsMobile",
	 	        "Blackberry", "WebOS", "Ubuntu", "Windows7", "Max OS X",
	 	        "Linux", "OS/2", "Ubuntu", "Windows7", "Max OS X", "Linux",
	 	        "OS/2", "Ubuntu", "Windows7", "Max OS X", "Linux", "OS/2",
	 	        "Android", "iPhone", "WindowsMobile", "Test1", "Test2" };

	 	    final ArrayList<String> list = new ArrayList<String>();
	 	    for (int i = 0; i < values.length; ++i) {
	 	      list.add(values[i]);
	 	    }
	            
	            
	            setListAdapter(new ArrayAdapter<String>(getActivity(),
	                    android.R.layout.simple_list_item_1, list));
	        }

	        @Override
	        public void onListItemClick(ListView l, View v, int position, long id) {
	            Log.i("MOWO", "Item clicked: " + id);
	        }
	    }
}
