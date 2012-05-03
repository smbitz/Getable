package com.codegears.getable.data;

import org.json.JSONException;
import org.json.JSONObject;

public class ActorSocialFacebook {
	private boolean status;
	
	public ActorSocialFacebook(JSONObject jsonObject) {
		status = jsonObject.optBoolean("status");
	}
}
