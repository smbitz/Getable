package com.codegears.getable.data;

import org.json.JSONException;
import org.json.JSONObject;

public class ProductActorLikeData {
	private String id;
	private ActorData actor;
	private ActorLikeActivity like;
	private ProductActivityStat statistic;
	private ProductActivityType type;
	private String activityTime;
	private String url;
	private MyRelationData myRelation;
	
	public ProductActorLikeData( JSONObject setObject ) {
		try {
			id = setObject.optString("id");
			actor = new ActorData( setObject.getJSONObject("actor") );
			like = new ActorLikeActivity( setObject.getJSONObject("like") );
			statistic = new ProductActivityStat( setObject.getJSONObject("statistic") );
			type = new ProductActivityType( setObject.getJSONObject("type") );
			activityTime = setObject.optString("activityTime");
			url = setObject.optString("url");
			
			if( setObject.optJSONObject("myRelation") != null ){
				myRelation = new MyRelationData( setObject.optJSONObject("myRelation") );
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public ActorLikeActivity getLike(){
		return this.like;
	}
	
	public ActorData getActor(){
		return this.actor;
	}
	
	public MyRelationData getMyRelation(){
		return this.myRelation;
	}
	
}
