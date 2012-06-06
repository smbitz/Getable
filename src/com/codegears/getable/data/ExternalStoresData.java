package com.codegears.getable.data;

import org.json.JSONArray;
import org.json.JSONObject;

public class ExternalStoresData {
	
	private ProductActivityType type;
	private JSONArray stores;
	
	public ExternalStoresData(JSONObject jsonObject) {
		if( jsonObject.optJSONObject( "type" ) != null ){
			type = new ProductActivityType( jsonObject.optJSONObject( "type" ) );
		}
		
		if( jsonObject.optJSONArray( "stores" ) != null ){
			stores = jsonObject.optJSONArray( "stores" );
		}
	}
	
	public JSONArray getStores(){
		return stores;
	}
	
}
