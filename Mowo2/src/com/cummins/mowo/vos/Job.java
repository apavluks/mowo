package com.cummins.mowo.vos;

public class Job {

	private String jobSubjectString;
	private String jobNumberString;
	private int id; 
	
	public Job() {
		this(-1, null, null); 
	}

	public Job(int id, String jobSubjectString, String jobNumberString) {
		super();
		this.jobSubjectString = jobSubjectString;
		this.jobNumberString = jobNumberString;
		this.id = id;
	}

	public String getJobSubjectString() {
		return jobSubjectString;
	}

	public void setJobSubjectString(String jobSubjectString) {
		this.jobSubjectString = jobSubjectString;
	}

	public String getJobNumberString() {
		return jobNumberString;
	}

	public void setJobNumberString(String jobNumberString) {
		this.jobNumberString = jobNumberString;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	
}
