package com.petcare.model;

import java.sql.Timestamp;

public class Activity {
	private String user;
	private String activity;
	private Timestamp activityDate;
	
	public Activity(String user, String activity, Timestamp activityDate) {
		this.user = user;
		this.activity = activity;
		this.activityDate = activityDate;
	}
	
	public String getUser() {return user;}
	public String getActivity() {return activity;}
	public Timestamp getActivityDate() {return activityDate;}
}
