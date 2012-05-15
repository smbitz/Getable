package com.codegears.getable.data;

import org.json.JSONException;
import org.json.JSONObject;

public class ActorLikeActivity {
	
	private ProductActivityData activityData;
	
	public ActorLikeActivity(JSONObject setObject) {
		try {
			activityData = new ProductActivityData( setObject.getJSONObject("activity") );
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public ProductActivityData getActivityData(){
		return this.activityData;
	}
	
}
