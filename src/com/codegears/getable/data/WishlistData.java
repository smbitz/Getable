package com.codegears.getable.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class WishlistData {
	private String id;
	private String name;
	private WishlistStat statisic;
	private WishlistsPicture picture;
	
	public WishlistData(JSONObject jsonObject) {
		id = jsonObject.optString("id");
		name = jsonObject.optString("name");
		
		if( jsonObject.optJSONObject( "statistic" ) != null ){
			statisic = new WishlistStat( jsonObject.optJSONObject( "statistic" ) );
		}
		
		if( jsonObject.optJSONObject( "picture" ) != null ){
			picture = new WishlistsPicture( jsonObject.optJSONObject( "picture" ) );
		}
		
	}
	
	public WishlistsPicture getPicture(){
		return picture;
	}
	
	public String getName(){
		return name;
	}
	
	public WishlistStat getStatistic(){
		return statisic;
	}
	
	public String getId(){
		return id;
	}
	
}
