package com.codegears.getable.data;

import org.json.JSONException;
import org.json.JSONObject;

public class ActorNotificationEmail {
	private boolean followed;
	private boolean commented;
	private boolean liked;
	
	public ActorNotificationEmail(JSONObject jsonObject) {
		followed = jsonObject.optBoolean("followed");
		commented = jsonObject.optBoolean("commented");
		liked = jsonObject.optBoolean("liked");
	}
}
