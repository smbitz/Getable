package com.codegears.getable.data;

import org.json.JSONException;
import org.json.JSONObject;

public class StoreData {
	private String name;
	private String id;
	private String url;
	private String streetAddress;
	private String city;
	private String state;
	private String postalCode;
	private StoreCoordinate coordinate;
	private ExternalStoresReferenceData externalReference;
	
	public StoreData(JSONObject jsonObject) {
		name = jsonObject.optString("name");
		id = jsonObject.optString("id");
		url = jsonObject.optString("url");
		streetAddress = jsonObject.optString("streetAddress");
		city = jsonObject.optString("city");
		state = jsonObject.optString("state");
		postalCode = jsonObject.optString("postalCode");
		
		if( jsonObject.optJSONObject("coordinate") != null ){
			coordinate = new StoreCoordinate( jsonObject.optJSONObject("coordinate") );
		}
		
		if( jsonObject.optJSONObject("externalReference") != null ){
			externalReference = new ExternalStoresReferenceData( jsonObject.optJSONObject("externalReference") );
		}
	}
	
	public String getName(){
		return name;
	}
	
	public String getStreetAddress(){
		return streetAddress;
	}
	
	public String getId(){
		return id;
	}
	
	public String getPostalCode(){
		return postalCode;
	}
	
	public ExternalStoresReferenceData getExternalReference(){
		return externalReference;
	}
	
	public StoreCoordinate getCoondinate(){
		return coordinate;
	}
	
}
