package com.codegears.getable.util;

import java.util.List;

import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.protocol.BasicHttpContext;
import org.w3c.dom.Document;

public class NetworkThreadUtil {

	public static void getXml(final String urlString, final String postData,
			final NetworkThreadListener listener) {
		new Thread() {
			@Override
			public void run() {
				Document doc = NetworkUtil.getXml(urlString, postData);
				if (doc != null) {
					listener.onNetworkDocSuccess(urlString, doc);
				} else {
					listener.onNetworkFail(urlString);
				}
			}
		}.start();
	}

	public static void getXml(final String urlString,
			final NetworkThreadListener listener) {
		new Thread() {
			@Override
			public void run() {
				Document doc = NetworkUtil.getXml(urlString, null);
				if (doc != null) {
					listener.onNetworkDocSuccess(urlString, doc);
				} else {
					listener.onNetworkFail(urlString);
				}
			}
		}.start();
	}

	public static void getRawDataWithCookie(final String urlString, final String postData,final List<String> cookie, final NetworkThreadListener listener){
		new Thread() {
			@Override
			public void run() {
				String raw = NetworkUtil.getRawData(urlString, postData, cookie);
				if (!raw.equals("")) {
					listener.onNetworkRawSuccess(urlString, raw);
				} else {
					listener.onNetworkFail(urlString);
				}
			}
		}.start();
	}
	
	public static void getRawData(final String urlString,
			final String postData, final NetworkThreadListener listener) {
		new Thread() {
			@Override
			public void run() {
				String raw = NetworkUtil.getRawData(urlString, postData);
				if (!raw.equals("")) {
					listener.onNetworkRawSuccess(urlString, raw);
				} else {
					listener.onNetworkFail(urlString);
				}
			}
		}.start();
	}

	public static interface NetworkThreadListener {
		public void onNetworkDocSuccess(String urlString, Document document);

		public void onNetworkRawSuccess(String urlString, String result);

		public void onNetworkFail(String urlString);
	}

	public static void getRawDataMultiPartWithCookie(final String urlString,
			final MultipartEntity reqEntity,
			final BasicHttpContext appBasicHttpContext,
			final NetworkThreadListener listener) {
		new Thread() {
			@Override
			public void run() {
				String raw = NetworkUtil.getRawData(urlString, reqEntity, appBasicHttpContext);
				if (!raw.equals("")) {
					listener.onNetworkRawSuccess(urlString, raw);
				} else {
					listener.onNetworkFail(urlString);
				}
			}
		}.start();
	}

}
