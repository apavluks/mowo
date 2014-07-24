package com.cummins.mowo.vos;

public class SessionDBParams {
	
	private String param;
	private String value;
	
	public SessionDBParams(String param, String value) {
	   this.param = param;
	   this.value = value;
	}

	public String getParam() {
		return param;
	}

	public void setParam(String param) {
		this.param = param;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	
}
