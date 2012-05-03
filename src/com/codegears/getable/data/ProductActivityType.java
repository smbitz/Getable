package com.codegears.getable.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ProductActivityType {
	private String id;
	private String name;
	
	public ProductActivityType(JSONObject jsonObject) {
		id = jsonObject.optString("id");
		name = jsonObject.optString("name");
	}
}
