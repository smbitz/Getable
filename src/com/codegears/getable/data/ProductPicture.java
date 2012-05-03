package com.codegears.getable.data;

import org.json.JSONException;
import org.json.JSONObject;

public class ProductPicture {
	private PictureDataUrls urls;
	private String description;
	private String uploadTime;
	//private Bitmap image;
	
	public ProductPicture(JSONObject jsonObject) {
		try {
			urls = new PictureDataUrls( (JSONObject) jsonObject.get("urls") );
			description = jsonObject.optString("description");
			uploadTime  = jsonObject.optString("uploadTime");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
