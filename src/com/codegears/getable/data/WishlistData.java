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
		try {
			id = jsonObject.optString("id");
			name = jsonObject.optString("name");
			statisic = new WishlistStat( jsonObject.getJSONObject( "statistic" ) );
			picture = new WishlistsPicture( jsonObject.getJSONObject( "picture" ) );
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
