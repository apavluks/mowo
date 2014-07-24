package com.cummins.mowo.activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.cummins.mowo.activity.FragmentPagerSupport.ArrayListFragment;
import com.cummins.mowo.activity.FragmentPagerSupport.MyAdapter;
import com.cummins.mowo.R;

public class MyFragmentActivity extends FragmentActivity {
	static final int NUM_ITEMS = 10;

	MyAdapter mAdapter;

	ViewPager mPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_pager);

		mAdapter = new MyAdapter(getSupportFragmentManager());

		mPager = (ViewPager) findViewById(R.id.pager);
		mPager.setAdapter(mAdapter);

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

	public static class MyFragment extends Fragment {

		public static final String EXTRA_MESSAGE = "EXTRA_MESSAGE";
		int mNum;

		public static final MyFragment newInstance(int num)

		{

			MyFragment f = new MyFragment();
			Bundle args = new Bundle();
            args.putInt("num", num);
			f.setArguments(args);
			return f;

		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,

		Bundle savedInstanceState) {
			int num = getArguments().getInt("num");
			View v = inflater.inflate(R.layout.myfragment_layout, container,
					false);

			TextView messageTextView = (TextView) v.findViewById(R.id.textView);
			String fragnum = messageTextView.getText() + String.valueOf(num); 
			messageTextView.setText(fragnum);
			return v;

		}

	}
}