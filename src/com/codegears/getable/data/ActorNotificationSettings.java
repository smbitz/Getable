package com.codegears.getable.data;

import org.json.JSONException;
import org.json.JSONObject;

public class ActorNotificationSettings {
	private ActorNotificationEmail email;
	private ActorNotificationPush push;
	
	public ActorNotificationSettings(JSONObject jsonObject) {
		try {
			email = new ActorNotificationEmail( jsonObject.getJSONObject("email") );
			push = new ActorNotificationPush( jsonObject.getJSONObject("push") );
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
