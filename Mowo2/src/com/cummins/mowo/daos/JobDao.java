package com.cummins.mowo.daos;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.format.Time;
import android.util.Log;

import com.cummins.mowo.database.DatabaseHelper;
import com.cummins.mowo.functions.fDATE;
import com.cummins.mowo.model.TimecardModel;
import com.cummins.mowo.vos.Job;
import com.cummins.mowo.vos.TimecardEntry;

public class JobDao {

	private static final String TAG = JobDao.class.getSimpleName();
	
	public static final String JOB_TABLE = "JOBS";
	public static final String JOB_ID = "ID";
	public static final String JOB_NUMBER = "JOB_NUMBER";
	public static final String JOB_DESCRIPTION = "DESCRIPTION";

	
	public JobDao() {
		//
	}
	
	public Job getJob(int id) {
		Log.d(TAG, " class.JobDao entering method.getJob id = " + id);
		SQLiteDatabase db = new DatabaseHelper().getWritableDatabase();
		
		Cursor cursor = db.query(JOB_TABLE, null, JOB_ID+"=?", new String[] {Integer.toString(id)}, null, null, null);
		Job job = null;
		if (cursor.moveToFirst()) {
			
			job =  new Job();
			job.setId(cursor.getInt(cursor.getColumnIndex(JOB_ID)));
			job.setJobNumberString(cursor.getString(cursor.getColumnIndex(JOB_NUMBER)));
			job.setJobSubjectString(cursor.getString(cursor.getColumnIndex(JOB_DESCRIPTION)));
			
		}
		
		cursor.close();
		db.close();
		return job;
	}	
	
	public int insert(Job job) {
		
		SQLiteDatabase db = new DatabaseHelper().getWritableDatabase();
		ContentValues values = new ContentValues();
		if (job.getId() > 0) values.put(JOB_ID, job.getId());
		values.put(JOB_NUMBER, job.getJobNumberString());
		values.put(JOB_DESCRIPTION, job.getJobSubjectString());
		
		int num = (int) db.insert(JOB_TABLE, null, values);
		
		Log.d(TAG, " class.JobDao new job saved id = " + num);

		db.close();
		return num;
	}	
	
	public int update(Job job) {
		SQLiteDatabase db = new DatabaseHelper().getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(JOB_ID, job.getId());
		values.put(JOB_NUMBER, job.getJobNumberString());
		values.put(JOB_DESCRIPTION, String.valueOf(job.getJobSubjectString()));
		
		Log.d(TAG, " class.JobDoa before updating jobid = " + job.getId());
		
		int num = db.update(JOB_TABLE, values, JOB_ID + "=?", new String[]{Integer.toString(job.getId())});
		
		Log.d(TAG, " class.JobDao after job updated id = " + num);
		
		db.close();
		return num;
	}
	
    public List<Job> getJobs() {
		
		Log.d(TAG, "Starting method jobDao");

		List<Job> list = new ArrayList<Job>();		
		SQLiteDatabase db = new DatabaseHelper().getWritableDatabase();
		
		Cursor cursor = db.query(JOB_TABLE, null, null, null, null, null, null, null);
		
		while (cursor.moveToNext()) {
			
			Job job = new Job();
			job.setId(cursor.getInt(cursor.getColumnIndex(JOB_ID)));
			job.setJobNumberString(cursor.getString(cursor.getColumnIndex(JOB_NUMBER)));
			job.setJobSubjectString(cursor.getString(cursor.getColumnIndex(JOB_DESCRIPTION)));
			
			list.add(job);
		}
		cursor.close();
		db.close();
		
		Log.d(TAG, "Inside method.getJobs output List Size " + list.size());
		
		return list;
	}	
	
	public int deleteJob (int id) {
		Log.d(TAG, " class.JobDao entering method.deleteJob id = " + id);
		SQLiteDatabase db = new DatabaseHelper().getWritableDatabase();
		
		/** operation return 0 if error and 1 if success*/
		int status = db.delete(JOB_TABLE, JOB_ID+"=?", new String[] {Integer.toString(id)});

		db.close();
		return status;
	}
}
