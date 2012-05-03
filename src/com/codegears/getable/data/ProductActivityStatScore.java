package com.codegears.getable.data;

import org.json.JSONException;
import org.json.JSONObject;

public class ProductActivityStatScore {
	private int active;
	private int allTime;
	
	public ProductActivityStatScore(JSONObject jsonObject) {
		active = jsonObject.optInt("active");
		allTime = jsonObject.optInt("allTime");
	}
}
