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
			numberOfProducts = jsonObject.getInt("numberOfProducts");
			numberOfComments = jsonObject.getInt("numberOfComments");
			numberOfReceivedComments = jsonObject.getInt("numberOfReceivedComments");
			numberOfCommentedProducts = jsonObject.getInt("numberOfCommentedProducts");
			numberOfFollowers = jsonObject.getInt("numberOfFollowers");
			numberOfFollowings = jsonObject.getInt("numberOfFollowings");
			numberOfLikes = jsonObject.getInt("numberOfLikes");
			numberOfReceivedLikes = jsonObject.getInt("numberOfReceivedLikes");
			numberOfSharings = jsonObject.getInt("numberOfSharings");
			numberOfWishlists = jsonObject.getInt("numberOfWishlists");
			score = new ActorStatScore( jsonObject.getJSONObject("score") );
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
