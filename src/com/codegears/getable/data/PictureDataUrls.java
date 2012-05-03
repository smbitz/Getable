package com.codegears.getable.data;

import org.json.JSONException;
import org.json.JSONObject;

public class PictureDataUrls {
	private String t;
	private String s;
	private String l;
	
	public PictureDataUrls(JSONObject jsonObject) {
		t = jsonObject.optString("t");
		s = jsonObject.optString("s");
		l = jsonObject.optString("l");
	}
	
	public String getImageURLT(){
		return t;
	}
	
	public String getImageURLS(){
		return s;
	}
	
	public String getImageURLL(){
		return l;
	}
}
