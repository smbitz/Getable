package com.codegears.getable.data;

import org.json.JSONException;
import org.json.JSONObject;

public class CategoryData {
	private String id;
	private String name;
	private String tags;
	private String subCategories;
	
	public CategoryData(JSONObject jsonObject) {
		id = jsonObject.optString("id");
		name = jsonObject.optString("name");
		tags = jsonObject.optString("tags");
		subCategories = jsonObject.optString("subCategories");
	}
}
