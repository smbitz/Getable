package com.codegears.getable.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ProductActivityType {
	private String id;
	private String name;
	
	public ProductActivityType(JSONObject jsonObject) {
		try {
			id = jsonObject.getString("id");
			name = jsonObject.getString("name");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
