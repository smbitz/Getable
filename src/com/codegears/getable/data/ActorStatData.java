package com.codegears.getable.data;

import org.json.JSONException;
import org.json.JSONObject;

public class ActorStatData {
	private int numberOfProducts;
	private int numberOfComments;
	private int numberOfReceivedComments;
	private int numberOfCommentedProducts;
	private int numberOfFollowers;
	private int numberOfFollowings;
	private int numberOfLikes;
	private int numberOfReceivedLikes;
	private int numberOfSharings;
	private int numberOfWishlists;
	private ActorStatScore score;
	
	public ActorStatData(JSONObject jsonObject) {
		try {
			numberOfProducts = jsonObject.optInt("numberOfProducts");
			numberOfComments = jsonObject.optInt("numberOfComments");
			numberOfReceivedComments = jsonObject.optInt("numberOfReceivedComments");
			numberOfCommentedProducts = jsonObject.optInt("numberOfCommentedProducts");
			numberOfFollowers = jsonObject.optInt("numberOfFollowers");
			numberOfFollowings = jsonObject.optInt("numberOfFollowings");
			numberOfLikes = jsonObject.optInt("numberOfLikes");
			numberOfReceivedLikes = jsonObject.optInt("numberOfReceivedLikes");
			numberOfSharings = jsonObject.optInt("numberOfSharings");
			numberOfWishlists = jsonObject.optInt("numberOfWishlists");
			score = new ActorStatScore( jsonObject.getJSONObject("score") );
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
