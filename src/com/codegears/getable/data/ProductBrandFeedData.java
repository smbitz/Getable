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
	
	public ProductBrandFeedData(JSONObject setObject) {
		try {
			id = setObject.optString("id");
			url = setObject.optString("url");
			activityTime = setObject.optString("activityTime");
			type = new ProductActivityType( setObject.getJSONObject("type") );
			actor = new ActorData( setObject.getJSONObject("actor") );
			statistic = new ProductActivityStat( setObject.getJSONObject("statistic") );
			product = new ProductData( setObject.getJSONObject("product") );
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
	
	public ActorData getActor(){
		return actor;
	}
}