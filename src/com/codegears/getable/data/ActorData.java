package com.codegears.getable.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ActorData {
	private String id;
	private String url;
	private String name;
	private String firstName;
	private String lastName;
	private String joinTime;
	private ActorStatData statistic;
	private ActorNotificationSettings notificationSettings;
	private ActorSocialConnections socialConnections;
	private String phoneNumber;
	private ActorPicture picture;
	private Boolean firstLogin;
	
	public ActorData(JSONObject jsonObject) {
		try {
			id = jsonObject.getString("id");
			url = jsonObject.getString("url");
			name = jsonObject.getString("name");
			firstName = jsonObject.getString("firstName");
			lastName = jsonObject.getString("lastName");
			joinTime = jsonObject.getString("joinTime");
			statistic = new ActorStatData( jsonObject.getJSONObject("statistic") );
			notificationSettings = new ActorNotificationSettings( jsonObject.getJSONObject("notificationSettings") );
			socialConnections = new ActorSocialConnections( jsonObject.getJSONObject("socialConnections") );
			phoneNumber = jsonObject.getString("phoneNumber");
			picture = new ActorPicture( jsonObject.getJSONObject("picture") );
			firstLogin = jsonObject.getBoolean("firstLogin");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String getName(){
		return name;
	}
	
	public ActorPicture getPicture(){
		return picture;
	}
	
	public String getId(){
		return id;
	}
}
