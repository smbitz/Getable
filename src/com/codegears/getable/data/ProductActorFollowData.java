package com.codegears.getable.data;

import org.json.JSONException;
import org.json.JSONObject;

public class ProductActorFollowData {
	private String id;
	private ActorData actor;
	private ProductActivityStat statistic;
	private ProductActivityType type;
	private String activityTime;
	private String url;
	private ActorData followedUser;
	
	public ProductActorFollowData( JSONObject setObject ) {
		try {
			id = setObject.optString("id");
			actor = new ActorData( setObject.getJSONObject("actor") );
			statistic = new ProductActivityStat( setObject.getJSONObject("statistic") );
			type = new ProductActivityType( setObject.getJSONObject("type") );
			activityTime = setObject.optString("activityTime");
			url = setObject.optString("url");
			followedUser = new ActorData( setObject.getJSONObject("followedUser") );
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public ActorData getFollowedUser(){
		return followedUser;
	}
	
}