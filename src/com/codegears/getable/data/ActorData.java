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
	private MyRelationData myRelation;
	private ActorGender gender;
	
	public ActorData(JSONObject jsonObject) {
		try {
			id = jsonObject.optString("id");
			url = jsonObject.optString("url");
			name = jsonObject.optString("name");
			firstName = jsonObject.optString("firstName");
			lastName = jsonObject.optString("lastName");
			joinTime = jsonObject.optString("joinTime");
			statistic = new ActorStatData( jsonObject.getJSONObject("statistic") );
			notificationSettings = new ActorNotificationSettings( jsonObject.getJSONObject("notificationSettings") );
			socialConnections = new ActorSocialConnections( jsonObject.getJSONObject("socialConnections") );
			phoneNumber = jsonObject.optString("phoneNumber");
			picture = new ActorPicture( jsonObject.getJSONObject("picture") );
			firstLogin = jsonObject.optBoolean("firstLogin");
			
			if( jsonObject.optJSONObject("myRelation") != null ){
				myRelation = new MyRelationData( jsonObject.optJSONObject("myRelation") );
			}
			
			if( jsonObject.optJSONObject("gender") != null ){
				gender = new ActorGender( jsonObject.optJSONObject("gender") );
			}
			
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
	
	public ActorStatData getStatistic(){
		return statistic;
	}
	
	public String getPhone(){
		return phoneNumber;
	}
	
	public MyRelationData getMyRelation(){
		return myRelation;
	}
	
	public String getFirstName(){
		return firstName;
	}
	
	public String getLastName(){
		return lastName;
	}
	
	public ActorGender getGender(){
		return gender;
	}
	
	public ActorNotificationSettings getNotificationSettings(){
		return notificationSettings;
	}
	
	public ActorSocialConnections getSocialConnections(){
		return socialConnections;
	}
	
}
