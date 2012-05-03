package com.codegears.getable.data;

import org.json.JSONException;
import org.json.JSONObject;

public class ProductGender {
	private String id;
	private String name;
	
	public ProductGender(JSONObject jsonObject) {
		try {
			id = jsonObject.getString("id");
			name  = jsonObject.getString("name");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
