package com.codegears.getable.data;

import org.json.JSONException;
import org.json.JSONObject;

public class ActorSocialConnections {
	ActorSocialTwitter twitter;
	ActorSocialFacebook facebook;
	ActorSocailFoursquare foursquare;
	
	public ActorSocialConnections(JSONObject jsonObject) {
		try {
			twitter = new ActorSocialTwitter( jsonObject.getJSONObject("twitter") );
			facebook = new ActorSocialFacebook( jsonObject.getJSONObject("facebook") );
			foursquare = new ActorSocailFoursquare( jsonObject.getJSONObject("foursquare") );
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public ActorSocialFacebook getFacebook(){
		return facebook;
	}
	
	public ActorSocialTwitter getTwitter(){
		return twitter;
	}
	
}
