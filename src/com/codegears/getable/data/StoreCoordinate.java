package com.codegears.getable.data;

import org.json.JSONException;
import org.json.JSONObject;

public class StoreCoordinate {
	private String latitude;
	private String longitude;
	
	public StoreCoordinate(JSONObject jsonObject) {
		latitude = jsonObject.optString("latitude");
		longitude = jsonObject.optString("longitude");
	}
	
	public String getLat(){
		return latitude;
	}
	
	public String getLng(){
		return longitude;
	}
	
}
