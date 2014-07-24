package com.cummins.mowo.conrollers;

public interface ControllerState {
	
	boolean handleMessage(int what);
	boolean handleMessage(int what, Object data);
	void dispose();
}
