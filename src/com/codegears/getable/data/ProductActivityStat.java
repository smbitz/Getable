package com.codegears.getable.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ProductActivityStat {
	private int numberOfComments;
	private int numberOfLikes;
	private ProductActivityStatScore score;
	
	public ProductActivityStat(JSONObject jsonObject) {
		numberOfComments = jsonObject.optInt("numberOfComments");
		numberOfLikes = jsonObject.optInt("numberOfLikes");
		
		if( jsonObject.optJSONObject("score") != null ){
			score = new ProductActivityStatScore( jsonObject.optJSONObject("score") );
		}
	}
	
	public void setNumberOfLikes( int setValue ){
		numberOfLikes = setValue;
	}
	
	public int getNumberOfComments(){
		return numberOfComments;
	}
	
	public int getNumberOfLikes(){
		return numberOfLikes;
	}
}
