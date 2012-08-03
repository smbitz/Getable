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
	private MyRelationData myRelation;
	private boolean followButtonToggleState;
	
	public ProductActorFollowData( JSONObject setObject ) {
		followButtonToggleState = false;
		try {
			id = setObject.optString("id");
			actor = new ActorData( setObject.getJSONObject("actor") );
			statistic = new ProductActivityStat( setObject.getJSONObject("statistic") );
			type = new ProductActivityType( setObject.getJSONObject("type") );
			activityTime = setObject.optString("activityTime");
			url = setObject.optString("url");
			followedUser = new ActorData( setObject.getJSONObject("followedUser") );
			
			if( setObject.optJSONObject("myRelation") != null ){
				myRelation = new MyRelationData( setObject.optJSONObject("myRelation") );
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void setClickedCheck(boolean check){
		followButtonToggleState = check;
	}
	
	public void setFollowedUser( ActorData setFollowedUser ){
		followedUser = setFollowedUser;
	}
	
	public void setActorData( ActorData setActorData ){
		actor = setActorData;
	}
	
	public boolean getClickedCheck(){
		return followButtonToggleState;
	}
	
	public ActorData getFollowedUser(){
		return followedUser;
	}
	
	public ActorData getActor(){
		return actor;
	}
	
}
