package com.codegears.getable.data;

import org.json.JSONException;
import org.json.JSONObject;

public class StoreCoondinate {
	private String latitude;
	private String longitude;
	
	public StoreCoondinate(JSONObject jsonObject) {
		latitude = jsonObject.optString("latitude");
		longitude = jsonObject.optString("longitude");
	}
}
