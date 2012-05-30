package com.codegears.getable.data;

import org.json.JSONException;
import org.json.JSONObject;

public class WishlistsPicture {
	private PictureDataUrls urls;
	private String description;
	private String uploadTime;
	//private Bitmap image;
	
	public WishlistsPicture(JSONObject jsonObject) {
		if( jsonObject.optJSONObject("urls") != null ){
			urls = new PictureDataUrls( jsonObject.optJSONObject("urls") );
		}
		
		description = jsonObject.optString("description");
		uploadTime  = jsonObject.optString("uploadTime");
	}
	/*
	public void loadImage(){
		//load image
	}
	
	public Bitmap getImage(){
		//if image isn't load??
		
		return image;
	}*/
	public PictureDataUrls getImageUrls(){
		return urls;
	}
}
