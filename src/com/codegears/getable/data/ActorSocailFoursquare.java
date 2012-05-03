package com.codegears.getable.data;

import org.json.JSONException;
import org.json.JSONObject;

public class ActorSocailFoursquare {
	private boolean status;
	
	public ActorSocailFoursquare(JSONObject jsonObject) {
		try {
			status = jsonObject.getBoolean("status");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
