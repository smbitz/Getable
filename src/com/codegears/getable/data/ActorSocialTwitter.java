package com.codegears.getable.data;

import org.json.JSONException;
import org.json.JSONObject;

public class ActorSocialTwitter {
	private boolean status;
	private String id;
	private String name;
	
	public ActorSocialTwitter(JSONObject jsonObject) {
		status = jsonObject.optBoolean("status");
		try {
			id = jsonObject.getString("id");
			name = jsonObject.getString("name");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Boolean getStatus(){
		return status;
	}
	
	public String getId(){
		return id;
	}
	
	public String getName(){
		return name;
	}
	
}
