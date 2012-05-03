package com.codegears.getable.data;

import org.json.JSONException;
import org.json.JSONObject;

public class PictureDataUrls {
	private String t;
	private String s;
	private String l;
	
	public PictureDataUrls(JSONObject jsonObject) {
		try {
			t = jsonObject.getString("t");
			s = jsonObject.getString("s");
			l = jsonObject.getString("l");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
