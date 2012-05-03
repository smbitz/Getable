package com.codegears.getable.data;

import org.json.JSONException;
import org.json.JSONObject;

public class ProductActivityStatScore {
	private int active;
	private int allTime;
	
	public ProductActivityStatScore(JSONObject jsonObject) {
		try {
			active = jsonObject.getInt("active");
			allTime = jsonObject.getInt("allTime");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
