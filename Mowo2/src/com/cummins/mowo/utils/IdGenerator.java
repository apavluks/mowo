package com.cummins.mowo.utils;

public class IdGenerator {
	private static int id = 0;

	public static synchronized int generate() {
		return id++;
	}
}
