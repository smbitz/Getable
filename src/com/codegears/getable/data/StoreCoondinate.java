package com.codegears.getable.data;

import org.json.JSONException;
import org.json.JSONObject;

public class StoreCoondinate {
	private String latitude;
	private String longitude;
	
	public StoreCoondinate(JSONObject jsonObject) {
		try {
			latitude = jsonObject.getString("latitude");
			longitude = jsonObject.getString("longitude");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
