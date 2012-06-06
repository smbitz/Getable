package com.codegears.getable.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.xmlpull.v1.XmlPullParserException;

import com.codegears.getable.R;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;

public class GetGender {
	private Map<String, Object> data;
	
	public GetGender( Context context ) {
		data = new HashMap<String, Object>();
		
		Resources res = context.getResources();
		XmlResourceParser xmlParser = res.getXml( R.xml.gender );
		
		try {
			while( xmlParser.getEventType() != XmlResourceParser.END_DOCUMENT ){
				if( xmlParser.getEventType() == XmlResourceParser.START_TAG ){
					String xmlTag = xmlParser.getName();
					
					if( xmlTag.equals("string") ){
						String name = xmlParser.getAttributeValue(null, "name");
						String value = xmlParser.getAttributeValue(null, "value");
						data.put(name, value);
					} else if (xmlTag.equals("int")) {
						String name = xmlParser.getAttributeValue(null, "name");
						int value = Integer.parseInt(xmlParser.getAttributeValue(null, "value"));
						data.put(name, value);
					} else if (xmlTag.equals("float")) {
						String name = xmlParser.getAttributeValue(null, "name");
						float value = Float.parseFloat(xmlParser.getAttributeValue(null, "value"));
						data.put(name, value);
					}
				} else if (xmlParser.getEventType() == XmlResourceParser.END_TAG) {
				} else if (xmlParser.getEventType() == XmlResourceParser.TEXT) {
				}
				xmlParser.next();
			}
			xmlParser.close();
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Object get( String dataName ){
		return data.get( dataName );
	}
	
	public Map<String, Object> getGenderData(){
		return data;
	}
}
