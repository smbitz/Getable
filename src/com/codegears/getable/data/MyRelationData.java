package com.codegears.getable.data;

import org.json.JSONArray;
import org.json.JSONObject;

public class MyRelationData {
	
	private Boolean me;
	private ProductActivityData followActivity;
	private Boolean followed;
	private String liked;
	private JSONArray wishlists;
	
	public MyRelationData( JSONObject jsonObject ){
		me = jsonObject.optBoolean( "me" );
		
		if( jsonObject.optJSONObject( "followActivity" ) != null ){
			followActivity = new ProductActivityData( jsonObject.optJSONObject( "followActivity" ) );
		}
		
		if( jsonObject.optJSONArray( "wishlists" ) != null ){
			wishlists = jsonObject.optJSONArray( "wishlists" );
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
	
	public JSONArray getArrayWishlistId(){
		return wishlists;
	}
	
}
