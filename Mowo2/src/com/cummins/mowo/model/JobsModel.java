package com.cummins.mowo.model;

import java.util.ArrayList;
import java.util.List;

import com.cummins.mowo.vos.Job;

public class JobsModel {
	private List<Job> jobsList;

	public JobsModel() {
		super();
		
		// retrieve these messages from somewhere 
		this.jobsList = new ArrayList<Job>();
		
		this.jobsList.add(new Job(1, "Fix Engine", "123424"));
		this.jobsList.add(new Job(2, "Clean Engine", "232111"));
		this.jobsList.add(new Job(3, "Replace something", "323231"));
		this.jobsList.add(new Job(1, "Fix Engine", "123424"));
		this.jobsList.add(new Job(2, "Clean Engine", "232111"));
		this.jobsList.add(new Job(3, "Replace something", "323231"));		
		this.jobsList.add(new Job(1, "Fix Engine", "123424"));
		this.jobsList.add(new Job(2, "Clean Engine", "232111"));
		this.jobsList.add(new Job(3, "Replace something", "323231"));	
		this.jobsList.add(new Job(1, "Fix Engine", "123424"));
		this.jobsList.add(new Job(2, "Clean Engine", "232111"));
		this.jobsList.add(new Job(3, "Replace something", "323231"));
		this.jobsList.add(new Job(1, "Fix Engine", "123424"));
		this.jobsList.add(new Job(2, "Clean Engine", "232111"));
		this.jobsList.add(new Job(3, "Replace something", "323231"));		
		this.jobsList.add(new Job(1, "Fix Engine", "123424"));
		this.jobsList.add(new Job(2, "Clean Engine", "232111"));
		this.jobsList.add(new Job(3, "Replace something", "323231"));	
		
	}

	public List<Job> getJobsList() {
		return jobsList;
	}

	public void setJobsList(List<Job> jobsList) {
		this.jobsList = jobsList;
	}

}
