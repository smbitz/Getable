package com.codegears.getable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;

import com.codegears.getable.util.NetworkThreadUtil;
import com.codegears.getable.util.NetworkThreadUtil.NetworkThreadListener;
import com.codegears.getable.util.NetworkUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class LoginActivity extends Activity implements NetworkThreadListener {
	
	private MyApp app;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView( R.layout.loginactivity );
		
		app = (MyApp) this.getApplication();
		
		//------- Login
        Map< String, String > newMapData = new HashMap<String, String>();
        newMapData.put( "email", "benz_shimo@hotmail.com" );
        newMapData.put( "password", "codegears" );
        newMapData.put( "_a", "logIn" );
        String postData = NetworkUtil.createPostData( newMapData );
        NetworkThreadUtil.getRawData( "http://wongnaimedia.dyndns.org/getable/guest.json?_f=logIn", postData, this );
	}

	@Override
	public void onNetworkDocSuccess(String urlString, Document document) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onNetworkRawSuccess(String urlString, String result) {
		app.setAppCookie( NetworkUtil.lastCookie );
		
		Intent intent = new Intent( this, MainActivity.class );
		this.startActivity( intent );
		
		/*List<String> lastCookie = NetworkUtil.lastCookie;
		System.out.println("cookie test : " + lastCookie.size());
		for (String cookie : lastCookie) {
			System.out.println("cookie : " + cookie);
		}
		NetworkThreadUtil.getRawDataWithCookie( "http://wongnaimedia.dyndns.org/getable/me/feed.json", null, lastCookie, new NetworkThreadListener(){
			@Override
			public void onNetworkDocSuccess( String urlString, Document document ) {
			}
			@Override
			public void onNetworkRawSuccess( String urlString, String result ) {
				System.out.println("feed result : " + result);
			}
			@Override
			public void onNetworkFail( String urlString ) {
			}} );*/
	}

	@Override
	public void onNetworkFail(String urlString) {
		// TODO Auto-generated method stub
		
	}
	
}
