package com.codegears.getable.data;

import org.json.JSONException;
import org.json.JSONObject;

public class ActorSocailFoursquare {
	private boolean status;
	
	public ActorSocailFoursquare(JSONObject jsonObject) {
		status = jsonObject.optBoolean("status");
	}
}
