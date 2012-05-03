package com.codegears.getable.data;

import org.json.JSONException;
import org.json.JSONObject;

public class ProductCommentData {
	private ProductActivityData activity;
	private String text;
	
	public ProductCommentData( JSONObject getJsonObject ) {
		try {
			activity = new ProductActivityData( getJsonObject.getJSONObject("activity") );
			text = getJsonObject.optString("text");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String getCommentText(){
		return text;
	}
	
	public ProductActivityData getProductActivityData(){
		return activity;
	}
}
