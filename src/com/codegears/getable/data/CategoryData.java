package com.codegears.getable.data;

import org.json.JSONException;
import org.json.JSONObject;

public class CategoryData {
	private String id;
	private String name;
	private String tags;
	private String subCategories;
	
	public CategoryData(JSONObject jsonObject) {
		try {
			id = jsonObject.getString("id");
			name = jsonObject.getString("name");
			tags = jsonObject.getString("tags");
			subCategories = jsonObject.getString("subCategories");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
