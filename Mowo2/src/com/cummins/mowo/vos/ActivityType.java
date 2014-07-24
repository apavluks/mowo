package com.cummins.mowo.vos;

import android.graphics.Bitmap;

public class ActivityType {

	private String title;
	private int icon;
	private int listIcon;
	private boolean chargable; 
	private Integer code;
	
	public ActivityType(Integer listIcon, int icon, String title, boolean chargable, Integer code) {
		super();
		this.title = title;
		this.icon = icon;
		this.chargable = chargable;
		this.code = code;
		this.listIcon = listIcon;
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

	public boolean isChargable() {
		return chargable;
	}

	public void setChargable(boolean chargable) {
		this.chargable = chargable;
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public int getListIcon() {
		return listIcon;
	}

	public void setListIcon(int listIcon) {
		this.listIcon = listIcon;
	}
	
	
}
