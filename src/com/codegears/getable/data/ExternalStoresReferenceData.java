package com.codegears.getable.data;

import org.json.JSONObject;

public class ExternalStoresReferenceData {
	
	private String id;
	private ProductActivityType type;
	
	public ExternalStoresReferenceData(JSONObject jsonObject) {
		id= jsonObject.optString("id");
		
		if( jsonObject.optJSONObject( "type" ) != null ){
			type = new ProductActivityType( jsonObject.optJSONObject( "type" ) );
		}
	}
	
	public String getId(){
		return id;
	}
	
	public ProductActivityType getType(){
		return type;
	}
	
}
