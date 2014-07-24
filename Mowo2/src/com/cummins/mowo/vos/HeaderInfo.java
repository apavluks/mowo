package com.cummins.mowo.vos;

import java.util.ArrayList;
import java.util.Date;

import com.cummins.mowo.model.TimecardModel;

public class HeaderInfo {

	private Date date;
	private ArrayList<TimecardModel> timecardList = new ArrayList<TimecardModel>();
	
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public ArrayList<TimecardModel> getTimecardList() {
		return timecardList;
	}
	public void setTimecardList(ArrayList<TimecardModel> timecardList) {
		this.timecardList = timecardList;
	}
	
	
}
