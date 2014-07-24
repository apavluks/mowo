package com.cummins.mowo.activity;

import java.util.ArrayList;
import java.util.List;

import com.cummins.mowo.R;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar.Tab;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MotionEvent;
import android.widget.Toast;

public class TabsAdapter extends FragmentPagerAdapter implements
		ActionBar.TabListener, 
		ViewPager.OnPageChangeListener {
	
	private final Context mContext;
	private final ActionBar mActionBar;
	private final ViewPager mViewPager;
	private final ArrayList<TabInfo> mTabs = new ArrayList<TabInfo>();
	private FragmentManager fm;
	private Fragment[] frags;
	private Menu mMenu;

	static final class TabInfo {
		private final Class<?> clss;
		private final Bundle args;

		TabInfo(Class<?> _class, Bundle _args) {
			clss = _class;
			args = _args;
		}
		
		public Bundle getBundle(){
			return args;
		}
	}

	public TabsAdapter(ActionBarActivity activity, ViewPager pager) {
		super(activity.getSupportFragmentManager());
		
		fm = activity.getSupportFragmentManager(); 
		mContext = activity;
		mActionBar = activity.getSupportActionBar();
		mViewPager = pager;
		mViewPager.setAdapter(this);
		mViewPager.setOnPageChangeListener(this);
	}

	public void addTab(ActionBar.Tab tab, Class<?> clss, Bundle args) {
		TabInfo info = new TabInfo(clss, args);
		tab.setTag(info);
		tab.setTabListener(this);
		mTabs.add(info);
		mActionBar.addTab(tab);
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return mTabs.size();
	}

	@Override
	public Fragment getItem(int position) {
		TabInfo info = mTabs.get(position);
		Fragment fragment = Fragment.instantiate(mContext, info.clss.getName(), info.args);
		
		if(position==1){
            //ft.add(mViewPager.getId(), fragment, "companies");
            
        }
		
		return fragment;
	}

	@Override
	public void onPageScrolled(int position, float positionOffset,
			int positionOffsetPixels) {
	}

	@Override
	public void onPageSelected(int position) {
		mActionBar.setSelectedNavigationItem(position);
	}

	@Override
	public void onPageScrollStateChanged(int state) {
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		Object tag = tab.getTag();
		for (int i = 0; i < mTabs.size(); i++) {
			if (mTabs.get(i) == tag) {
				mViewPager.setCurrentItem(i); 
				TabInfo tabInfo = (TabInfo) tag;
				int tabId = ((Bundle) tabInfo.args).getInt("TAB_ID");
				((MainActivity) mContext).changeMenuOnTabChange(tabId);	
				
			}
		}
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
	}
	
    private void show(String param) {
		Toast toast= Toast.makeText(mContext, param, Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER|Gravity.CENTER, 0, 0);
		toast.show();
    }

	public void setMenu(Menu m) {
		this.mMenu = m;
	}     
	
    

}