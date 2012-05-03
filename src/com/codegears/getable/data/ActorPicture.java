package com.codegears.getable.data;

import org.json.JSONException;
import org.json.JSONObject;

public class ActorPicture {
	private PictureDataUrls urls;
	private String description;
	private String uploadTime;
	
	public ActorPicture(JSONObject jsonObject) {
		try {
			urls = new PictureDataUrls( jsonObject.getJSONObject("urls") );
			description = jsonObject.optString("description");
			uploadTime = jsonObject.optString("uploadTime");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public PictureDataUrls getImageUrls(){
		return urls;
	}
	
}
