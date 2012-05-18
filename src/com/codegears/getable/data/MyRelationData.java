package com.codegears.getable.data;

import org.json.JSONObject;

public class MyRelationData {
	
	private Boolean me;
	private ProductActivityData followActivity;
	private Boolean followed;
	private String liked;
	
	public MyRelationData( JSONObject jsonObject ){
		me = jsonObject.optBoolean( "me" );
		
		if( jsonObject.optJSONObject( "followActivity" ) != null ){
			followActivity = new ProductActivityData( jsonObject.optJSONObject( "followActivity" ) );
		}
		
		followed = jsonObject.optBoolean( "followed" );
		liked = jsonObject.optString( "liked" );
	}
	
	public ProductActivityData getFollowActivity(){
		return followActivity;
	}
	
	public String getLike(){
		return liked;
	}
	
}
