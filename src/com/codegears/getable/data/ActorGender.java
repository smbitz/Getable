package com.codegears.getable.data;

import org.json.JSONObject;

public class ActorGender {
	
	private String id;
	private String name;
	
	public ActorGender( JSONObject jsonObject ) {
		id = jsonObject.optString( "id" );
		name = jsonObject.optString( "name" );
	}

	public String getId() {
		return id;
	}
	
}
