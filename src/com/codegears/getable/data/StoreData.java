package com.codegears.getable.data;

import org.json.JSONException;
import org.json.JSONObject;

public class StoreData {
	private String name;
	private String id;
	private String url;
	private String streetAddress;
	private String postalCode;
	private StoreCoondinate coondinate;
	
	public StoreData(JSONObject jsonObject) {
		try {
			name = jsonObject.optString("name");
			id = jsonObject.optString("id");
			url = jsonObject.optString("url");
			streetAddress = jsonObject.optString("streetAddress");
			postalCode = jsonObject.optString("postalCode");
			coondinate = new StoreCoondinate( jsonObject.getJSONObject("coondinate") );
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String getName(){
		return name;
	}
	
	public String getStreetAddress(){
		return streetAddress;
	}
}
