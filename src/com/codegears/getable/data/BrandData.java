package com.codegears.getable.data;

import org.json.JSONException;
import org.json.JSONObject;

public class BrandData {
	private String id;
	private String url;
	private String name;
	private String tags;
	
	public BrandData( JSONObject jsonObject ) {
		try {
			id = jsonObject.getString("id");
			url = jsonObject.getString("url");
			name = jsonObject.getString("name");
			tags = jsonObject.getString("tags");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String getName(){
		return name;
	}
}
