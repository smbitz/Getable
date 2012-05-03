package com.codegears.getable.data;

import org.json.JSONException;
import org.json.JSONObject;

public class ActorSocialConnections {
	ActorSocialTwitter twitter;
	ActorSocialFacebook facebook;
	ActorSocailFoursquare foursqure;
	
	public ActorSocialConnections(JSONObject jsonObject) {
		try {
			twitter = new ActorSocialTwitter( jsonObject.getJSONObject("twitter") );
			facebook = new ActorSocialFacebook( jsonObject.getJSONObject("facebook") );
			foursqure = new ActorSocailFoursquare( jsonObject.getJSONObject("foursqure") );
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
