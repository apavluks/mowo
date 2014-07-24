package com.cummins.mowo.utils;

import java.util.ArrayList;


public abstract class MowoList extends ArrayList<Object> implements Idable {

	private int id;

	public MowoList() {
		super();
		this.id = IdGenerator.generate();
	}

	public int getObjectId() {
		return id;
	}
}
