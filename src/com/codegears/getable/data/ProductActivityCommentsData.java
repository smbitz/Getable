package com.codegears.getable.data;

import org.json.JSONException;
import org.json.JSONObject;

public class ProductActivityCommentsData {
	private String id;
	private String url;
	private String activityTime;
	private ProductActivityType type;
	private ActorData actor;
	private ProductActivityStat statistic;
	private ProductCommentData comment;
	
	public ProductActivityCommentsData(JSONObject setObject) {
		try {
			id = setObject.optString("id");
			url = setObject.optString("url");
			activityTime = setObject.optString("activityTime");
			type = new ProductActivityType( setObject.getJSONObject("type") );
			actor = new ActorData( setObject.getJSONObject("actor") );
			statistic = new ProductActivityStat( setObject.getJSONObject("statistic") );
			comment = new ProductCommentData( setObject.getJSONObject("comment") );
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
