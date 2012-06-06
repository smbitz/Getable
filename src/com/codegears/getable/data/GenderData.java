package com.codegears.getable.data;

public class GenderData {
	
	private String id;
	private String name;
	
	public GenderData(String genderName, String getId) {
		id = getId;
		name = genderName;
	}
	
	public String getId(){
		return id;
	}
	
	public String getName(){
		return name;
	}
	
}
