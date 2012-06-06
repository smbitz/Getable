package com.codegears.getable.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class BrandData {
	private String id;
	private String url;
	private String name;
	private JSONArray tags;
	
	public BrandData( JSONObject jsonObject ) {
		id = jsonObject.optString("id");
		url = jsonObject.optString("url");
		name = jsonObject.optString("name");
		
		if( jsonObject.optJSONArray("tags") != null ){
			tags = jsonObject.optJSONArray("tags");
		}
	}
	
	public String getName(){
		return name;
	}
	
	public String getId(){
		return id;
	}
	
}
