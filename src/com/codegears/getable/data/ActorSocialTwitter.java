package com.codegears.getable.data;

import org.json.JSONException;
import org.json.JSONObject;

public class ActorSocialTwitter {
	private boolean status;
	
	public ActorSocialTwitter(JSONObject jsonObject) {
		try {
			status = jsonObject.getBoolean("status");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
