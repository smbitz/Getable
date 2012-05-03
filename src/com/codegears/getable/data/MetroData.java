package com.codegears.getable.data;

import org.json.JSONException;
import org.json.JSONObject;

public class MetroData {
	private String id;
	private String name;
	
	public MetroData(JSONObject jsonObject) {
		try {
			id = jsonObject.getString("id");
			name = jsonObject.getString("name");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String getName(){
		return this.name;
	}
	
	public String getId(){
		return this.id;
	}
}
