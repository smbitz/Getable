package com.codegears.getable.data;

import org.json.JSONException;
import org.json.JSONObject;

public class ProductBrandFeedData {
	private String id;
	private String url;
	private String activityTime;
	private ProductActivityType type;
	private ActorData actor;
	private ProductActivityStat statistic;
	private ProductData product;
	private MyRelationData myRelation;
	
	public ProductBrandFeedData(JSONObject setObject) {
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
	}
	
	public void setMyRelation( MyRelationData setMyRelationData ){
		myRelation = setMyRelationData;
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
	
	public String getActivityTime(){
		return activityTime;
	}
	
	public MyRelationData getMyRelation(){
		return myRelation;
	}
}
