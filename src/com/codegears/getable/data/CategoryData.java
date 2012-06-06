package com.codegears.getable.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CategoryData {
	private String id;
	private String name;
	private String tags;
	private JSONArray subCategories;
	
	public CategoryData(JSONObject jsonObject) {
		id = jsonObject.optString("id");
		name = jsonObject.optString("name");
		tags = jsonObject.optString("tags");
		
		if( jsonObject.optJSONArray("subCategories") != null ){
			subCategories = jsonObject.optJSONArray("subCategories");
		}
	}
	
	public String getName(){
		return name;
	}
	
	public JSONArray getSubCategories(){
		return subCategories;
	}
	
	public String getId(){
		return id;
	}
	
}
