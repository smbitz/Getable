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
	private ProductCommentData comment;
	
	public ProductActivityData(JSONObject setObject) {
		try {
			id = setObject.getString("id");
			url = setObject.getString("url");
			activityTime = setObject.getString("activityTime");
			type = new ProductActivityType( setObject.getJSONObject("type") );
			actor = new ActorData( setObject.getJSONObject("actor") );
			statistic = new ProductActivityStat( setObject.getJSONObject("statistic") );
			product = new ProductData( setObject.getJSONObject("product") );
			comment = new ProductCommentData( setObject.getJSONObject("comment") );
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
	
	public ProductCommentData getComment(){
		return comment;
	}
	
	public ActorData getActor(){
		return actor;
	}
}