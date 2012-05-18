package com.codegears.getable;

import java.util.List;

import android.app.Application;

public class MyApp extends Application {
	
	public static final String URL_DEFAULT = "URL_DEFAULT";
	public static final String DEFAULT_URL_VAR_1 = "?page.number=1&page.size=32";
	
	private List<String> appCookie;
	private String userId;
	
	public void setAppCookie( List<String> setValue ){
		appCookie = setValue;
	}

	public void setUserId(String string) {
		userId = string;
	}
	
	public List<String> getAppCookie(){
		return appCookie;
	}
	
	public String getUserId(){
		return userId;
	}
	
}
