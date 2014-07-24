package com.cummins.mowo.vos;

public class PunchData {
	
	private double latitute;
	private double longtitute;
	private String address;
	private String autopanch;
	
	public PunchData() {
		this.latitute = 0.0;
		this.longtitute = 0.0;
		this.address = null;
		this.autopanch = null;	
	}

	public double getLatitute() {
		return latitute;
	}

	public void setLatitute(double latitute) {
		this.latitute = latitute;
	}

	public double getLongtitute() {
		return longtitute;
	}

	public void setLongtitute(double longtitute) {
		this.longtitute = longtitute;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getAutopanch() {
		return autopanch;
	}

	public void setAutopanch(String autopanch) {
		this.autopanch = autopanch;
	}

}
