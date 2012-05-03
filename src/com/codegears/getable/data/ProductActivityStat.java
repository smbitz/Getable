package com.codegears.getable.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ProductActivityStat {
	private int numberOfComments;
	private int numberOfLikes;
	private ProductActivityStatScore score;
	
	public ProductActivityStat(JSONObject jsonObject) {
		try {
			numberOfComments = jsonObject.getInt("numberOfComments");
			numberOfLikes = jsonObject.getInt("numberOfLikes");
			score = new ProductActivityStatScore( jsonObject.getJSONObject("score") );
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public int getNumberOfComments(){
		return numberOfComments;
	}
	
	public int getNumberOfLikes(){
		return numberOfLikes;
	}
}
