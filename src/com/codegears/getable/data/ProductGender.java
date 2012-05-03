package com.codegears.getable.data;

import org.json.JSONException;
import org.json.JSONObject;

public class ProductGender {
	private String id;
	private String name;
	
	public ProductGender(JSONObject jsonObject) {
		id = jsonObject.optString("id");
		name  = jsonObject.optString("name");
	}
}
