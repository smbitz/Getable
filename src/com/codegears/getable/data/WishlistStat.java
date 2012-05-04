package com.codegears.getable.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class WishlistStat {
	private int numberOfActivities;
	
	public WishlistStat(JSONObject jsonObject) {
		numberOfActivities = jsonObject.optInt("numberOfActivities");
	}
	
	public int getNumberOfActivities(){
		return numberOfActivities;
	}
}
