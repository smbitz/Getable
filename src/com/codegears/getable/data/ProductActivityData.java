package com.codegears.getable.data;

import org.json.JSONException;
import org.json.JSONObject;

public class ProductActivityData {
	private String id;
	private String url;
	private String activityTime;
	private ProductActivityType type;
	private ActorData actor;
	private ProductActivityStat statistic;
	private ProductData product;
	private MyRelationData myRelation;
	private ActorData followedUser;
	private ActorLikeActivity like;
	private ProductCommentData comment;
	
	public ProductActivityData(JSONObject setObject) {
		id = setObject.optString("id");
		url = setObject.optString("url");
		activityTime = setObject.optString("activityTime");
		
		if( setObject.optJSONObject("type") != null ){
			type = new ProductActivityType( setObject.optJSONObject("type") );
		}
		
		if( setObject.optJSONObject("actor") != null ){
			actor = new ActorData( setObject.optJSONObject("actor") );
		}
		
		if( setObject.optJSONObject("statistic") != null ){
			statistic = new ProductActivityStat( setObject.optJSONObject("statistic") );
		}
		
		if( setObject.optJSONObject("product") != null ){
			product = new ProductData( setObject.optJSONObject("product") );
		}
		
		if( setObject.optJSONObject("myRelation") != null ){
			myRelation = new MyRelationData( setObject.optJSONObject("myRelation") );
		}
		
		if( setObject.optJSONObject("followedUser") != null ){
			followedUser = new ActorData( setObject.optJSONObject("followedUser") );
		}
		
		if( setObject.optJSONObject("like") != null ){
			like = new ActorLikeActivity( setObject.optJSONObject("like") );
		}
		
		if( setObject.optJSONObject("comment") != null ){
			comment = new ProductCommentData( setObject.optJSONObject("comment") );
		}
		
	}
	
	public ProductData getProduct(){
		return product;
	}
	
	public String getId(){
		return id;
	}
	
	public ProductActivityStat getStatisitc(){
		return statistic;
	}
	
	public ActorData getActor(){
		return actor;
	}
	
	public String getURL(){
		return url;
	}
	
	public MyRelationData getMyRelation(){
		return myRelation;
	}
	
	public ActorData getFollowedUser(){
		return followedUser;
	}
	
	public ActorLikeActivity getLike(){
		return this.like;
	}
	
	public ProductCommentData getComment(){
		return comment;
	}
	
	public ProductActivityType getType(){
		return type;
	}
	
}
