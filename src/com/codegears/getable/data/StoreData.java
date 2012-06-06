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
	private StoreCoondinate coondinate;
	private ExternalStoresReferenceData externalReference;
	
	public StoreData(JSONObject jsonObject) {
		try {
			name = jsonObject.optString("name");
			id = jsonObject.optString("id");
			url = jsonObject.optString("url");
			streetAddress = jsonObject.optString("streetAddress");
			city = jsonObject.optString("city");
			state = jsonObject.optString("state");
			postalCode = jsonObject.optString("postalCode");
			
			if( jsonObject.getJSONObject("coondinate") != null ){
				coondinate = new StoreCoondinate( jsonObject.getJSONObject("coondinate") );
			}
			
			if( jsonObject.getJSONObject("externalReference") != null ){
				externalReference = new ExternalStoresReferenceData( jsonObject.getJSONObject("externalReference") );
			}
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
	
}
