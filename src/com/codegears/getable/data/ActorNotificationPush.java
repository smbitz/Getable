package com.codegears.getable.data;

import org.json.JSONException;
import org.json.JSONObject;

public class ActorNotificationPush {
	private boolean followed;
	private boolean commented;
	private boolean liked;
	
	public ActorNotificationPush(JSONObject jsonObject) {
		try {
			followed = jsonObject.getBoolean("followed");
			commented = jsonObject.getBoolean("commented");
			liked = jsonObject.getBoolean("liked");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
