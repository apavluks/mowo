package com.cummins.mowo.vos;

import android.graphics.Bitmap;

public class ActivityJobOption {

	private String title;
	private int icon;
	private boolean showJobList; 
	
	public ActivityJobOption(int icon, String title, boolean showJobList) {
		super();
		this.title = title;
		this.icon = icon;
		this.showJobList = showJobList;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getIcon() {
		return icon;
	}

	public void setIcon(int icon) {
		this.icon = icon;
	}

	public boolean getShowJobList() {
		return showJobList;
	}

	public void setShowJobList(boolean showJobList) {
		this.showJobList = showJobList;
	}
	
	
}
