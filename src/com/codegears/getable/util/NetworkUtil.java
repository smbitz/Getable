package com.codegears.getable.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkUtil {
	
	public static List<String> lastCookie;
	
	public static Document getXml( String urlString, String postData ) {
		try {
			URL url = new URL( urlString );
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoInput( true );
			connection.setUseCaches( false );
			if ( postData != null ) {
				connection.setRequestMethod( "POST" );
				connection.setDoOutput( true );
				DataOutputStream wr = new DataOutputStream( connection.getOutputStream() );
				wr.writeBytes( postData );
				wr.flush();
				wr.close();
			}
			connection.connect();
			Document d = DocumentBuilderFactory.newInstance().newDocumentBuilder()
							.parse( connection.getInputStream() );
			connection.disconnect();
			return d;
		} catch ( MalformedURLException e ) {
			e.printStackTrace();
		} catch ( IOException e ) {
			e.printStackTrace();
		} catch ( SAXException e ) {
			e.printStackTrace();
		} catch ( ParserConfigurationException e ) {
			e.printStackTrace();
		}
		return null;
	}

	public static Document getXml( String url ) {
		return getXml( url, null );
	}

	public static String getRawData( String urlString, String postData ) {
		String rawData = "";
		try {
			URL url = new URL( urlString );
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoInput( true );
			connection.setUseCaches( false );
			if ( postData != null ) {
				connection.setRequestMethod( "POST" );
				connection.setDoOutput( true );
				DataOutputStream wr = new DataOutputStream( connection.getOutputStream() );
				wr.writeBytes( postData );
				wr.flush();
				wr.close();
			}
			connection.connect();
			lastCookie = connection.getHeaderFields().get("Set-Cookie");
			InputStream i = connection.getInputStream();
			BufferedReader bReader = new BufferedReader( new InputStreamReader( i ) );
			//Read bReader
			/*char[] buffer = new char[2048];
			while loop
			bReader.read(buffer, 0, 1024);
			bReader.read(buffer, 1024, 1024);
			...
			add to raw data*/
			String line = bReader.readLine();
			while ( line != null ) {
				rawData += line;
				line = bReader.readLine();
			}
			/*char[] buffer = new char[2048];
			int defaultOffsetSize = 0;
			int loop = 0;
			while( (bReader.read( buffer, defaultOffsetSize, 1024 )) > 0 ){
				String s = String.valueOf(buffer);
				System.out.println("STRING : "+s);
				rawData += s;
				
				//System.out.println("In buffer Data : "+buffer[0]+buffer[1]+buffer[2]+", "+loop);
				loop++;
			}*/
			/*StringBuilder totalString = new StringBuilder();
			String line;
			while ((line = bReader.readLine()) != null) {
				totalString.append(line);
			}
			rawData = totalString.toString();*/
			//System.out.println("In Raw Data : "+rawData.length());
		} catch ( MalformedURLException e ) {
			e.printStackTrace();
		} catch ( IOException e ) {
			e.printStackTrace();
		}
		
		return rawData;
	}

	public static String createPostData( Map< String, String > data ) {
		String returnVal = "";
		Set< String > keySet = data.keySet();
		for ( String key : keySet ) {
			String value = data.get( key );
			returnVal += key + "=" + URLEncoder.encode( value ) + "&";
		}
		returnVal = returnVal.substring( 0, returnVal.length() - 1 );
		return returnVal;
	}

	public static boolean isInternetConnection( Context context ) {
		ConnectivityManager cManager = (ConnectivityManager) context
						.getSystemService( Context.CONNECTIVITY_SERVICE );
		NetworkInfo info = cManager.getActiveNetworkInfo();
		if ( info == null ) {
			return false;
		}
		if ( info.isAvailable() && info.isConnected() ) {
			return true;
		}
		return false;
	}

	public static String getRawData( String urlString, String postData, List< String > cookies ) {
		String rawData = "";
		try {
			URL url = new URL( urlString );
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoInput( true );
			connection.setUseCaches( false );
			for (String cookie : cookies) {
				connection.addRequestProperty("Cookie", cookie.split(";", 2)[0]);
			}
			if ( postData != null ) {
				connection.setRequestMethod( "POST" );
				connection.setDoOutput( true );
				DataOutputStream wr = new DataOutputStream( connection.getOutputStream() );
				wr.writeBytes( postData );
				wr.flush();
				wr.close();
			}
			connection.connect();
			InputStream i = connection.getInputStream();
			BufferedReader bReader = new BufferedReader( new InputStreamReader( i ) );
			String line = bReader.readLine();
			while ( line != null ) {
				rawData += line;
				line = bReader.readLine();
			}
		} catch ( MalformedURLException e ) {
			e.printStackTrace();
		} catch ( IOException e ) {
			e.printStackTrace();
		}
		return rawData;
	}

	public static String getRawData(String urlString,
			MultipartEntity reqEntity, BasicHttpContext appBasicHttpContext) {

		HttpClient httpClient = new DefaultHttpClient();
        HttpPost postRequest = new HttpPost( urlString );
        
        postRequest.setEntity( reqEntity );
        HttpResponse res;
        String rawData = "";
		try {
			res = httpClient.execute( postRequest, appBasicHttpContext );
			BufferedReader reader = new BufferedReader(new InputStreamReader(res.getEntity().getContent(), "UTF-8"));
			String line = reader.readLine();
			while ( line != null ) {
				rawData += line;
				line = reader.readLine();
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return rawData;
	}

}
