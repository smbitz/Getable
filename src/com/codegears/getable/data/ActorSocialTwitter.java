package com.codegears.getable.data;

import org.json.JSONException;
import org.json.JSONObject;

public class ActorSocialTwitter {
	private boolean status;
	
	public ActorSocialTwitter(JSONObject jsonObject) {
		status = jsonObject.optBoolean("status");
	}
}
