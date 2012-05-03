package com.codegears.getable.data;

import org.json.JSONException;
import org.json.JSONObject;

public class ActorStatScore {
	private int active;
	private int allTime;
	
	public ActorStatScore(JSONObject jsonObject) {
		active = jsonObject.optInt("active");
		allTime = jsonObject.optInt("allTime");
	}
}
