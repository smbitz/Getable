package com.codegears.getable.ui.activity;

import java.util.List;

import org.apache.http.client.CookieStore;
import org.apache.http.cookie.Cookie;
import org.apache.http.util.EncodingUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageButton;

import com.codegears.getable.BodyLayoutStackListener;
import com.codegears.getable.MainActivity;
import com.codegears.getable.MyApp;
import com.codegears.getable.R;
import com.codegears.getable.ui.AbstractViewLayout;
import com.codegears.getable.util.Config;
import com.loopj.android.http.AsyncHttpClient;

import android.view.View.OnClickListener;

public class ProfileLayoutWebView extends AbstractViewLayout implements
		OnClickListener {

	public static final String SHARE_PREF_WEB_VIEW_TYPE = "SHARE_PREF_WEB_VIEW_TYPE";
	public static final String WEB_VIEW_TYPE = "WEB_VIEW_TYPE";
	public static final String WEB_VIEW_TYPE_TERMS = "WEB_VIEW_TYPE_TERMS";
	public static final String WEB_VIEW_TYPE_POLICY = "WEB_VIEW_TYPE_POLICY";
	public static final String WEB_VIEW_TYPE_CONNECT_TWITTER = "WEB_VIEW_TYPE_CONNECT_TWITTER";

	private static final String WEB_VIEW_TERMS_URL = "http://www.getableapp.com/terms";
	private static final String WEB_VIEW_POLICY_URL = "http://www.getableapp.com/privacy";
	
	public static final String CONNECT_TWITTER_FROM = "CONNECT_TWITTER_FROM";
	public static final int CONNECT_TWITTER_FROM_SETTINGS = 1;
	public static final int CONNECT_TWITTER_FROM_SHARE_PRODUCT = 2;
	public static final int CONNECT_TWITTER_FROM_PRODUCT_DETAIL = 3;
	public static final int CONNECT_TWITTER_FROM_CHANGESETTING_CHANGE_IMAGE = 4;
	public static final int CONNECT_TWITTER_FROM_FIND_FRIEND = 5;

	private Button openBrowserButton;
	private WebView webView;
	private String webViewType;
	private int connectTwitterType;
	private MyApp app;
	private AsyncHttpClient asyncHttpClient;
	private ImageButton backButton;
	private BodyLayoutStackListener listener;
	private Config config;
	private String getableURL;
	private String connectTwitterURL;
	private String connectSameTwitterAccountURL;
	private AlertDialog alertDialog;

	public ProfileLayoutWebView(Activity activity) {
		super(activity);
		View.inflate(this.getContext(), R.layout.profilelayoutwebview, this);

		SharedPreferences myPrefs = this.getActivity().getSharedPreferences(SHARE_PREF_WEB_VIEW_TYPE, this.getActivity().MODE_PRIVATE);
		webViewType = myPrefs.getString(WEB_VIEW_TYPE, null);
		connectTwitterType = myPrefs.getInt(CONNECT_TWITTER_FROM, 0);

		app = (MyApp) this.getActivity().getApplication();
		asyncHttpClient = app.getAsyncHttpClient();
		config = new Config( this.getContext() );
		alertDialog = new AlertDialog.Builder( this.getContext() ).create();

		openBrowserButton = (Button) findViewById(R.id.profileLayoutWebViewOpenBrowserButton);
		webView = (WebView) findViewById(R.id.profileLayoutWebViewLayout);
		backButton = (ImageButton) findViewById(R.id.profileLayoutWebViewBackButton);

		// Set font
		openBrowserButton.setTypeface(Typeface.createFromAsset(this
				.getContext().getAssets(), MyApp.APP_FONT_PATH_2));

		openBrowserButton.setOnClickListener(this);
		backButton.setOnClickListener(this);
		
		getableURL = config.get( MyApp.URL_DEFAULT ).toString();
		connectTwitterURL = config.get( MyApp.URL_DEFAULT ).toString()+"connect/twitter.json";
		connectSameTwitterAccountURL = config.get( MyApp.URL_DEFAULT ).toString()+"main/connect/twitter.json";

		webView.getSettings().setDefaultTextEncodingName("utf-8");
		webView.getSettings().setJavaScriptEnabled(true);
		if (webViewType.equals(WEB_VIEW_TYPE_TERMS)) {
			webView.loadUrl(WEB_VIEW_TERMS_URL);
		} else if (webViewType.equals(WEB_VIEW_TYPE_POLICY)) {
			webView.loadUrl(WEB_VIEW_POLICY_URL);
		} else if (webViewType.equals(WEB_VIEW_TYPE_CONNECT_TWITTER)) {
			openBrowserButton.setVisibility( View.INVISIBLE );
			setCookies( connectTwitterURL );
		}
		
		webView.setWebViewClient(new WebViewLayoutClient());
	}

	private String convertCookieToString(Cookie cookie) {
		return String.format("%s=%s;", cookie.getName(), cookie.getValue());
	}

	@Override
	public void refreshView(Intent getData) {
		// TODO Auto-generated method stub

	}

	@Override
	public void refreshView() {
		// TODO Auto-generated method stub

	}

	public void setBodyLayoutChangeListener(BodyLayoutStackListener setListener) {
		this.listener = setListener;
	}

	@Override
	public void onClick(View v) {
		if (v.equals(openBrowserButton)) {
			if (webViewType.equals(WEB_VIEW_TYPE_TERMS)) {
				Intent browserIntent = new Intent(Intent.ACTION_VIEW,
						Uri.parse(WEB_VIEW_TERMS_URL));
				this.getActivity().startActivity(browserIntent);
			} else if (webViewType.equals(WEB_VIEW_TYPE_POLICY)) {
				Intent browserIntent = new Intent(Intent.ACTION_VIEW,
						Uri.parse(WEB_VIEW_POLICY_URL));
				this.getActivity().startActivity(browserIntent);
			}
		} else if (v.equals(backButton)) {
			if (listener != null) {
				listener.onRequestBodyLayoutStack(MainActivity.LAYOUTCHANGE_BACK_BUTTON);
			}
		}
	}

	private class WebViewLayoutClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url);
			return true;
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
			if ( url.equals( connectTwitterURL ) ||
				 url.startsWith( connectSameTwitterAccountURL ) ) {
				System.out.println("ConnectTwitterFinish : "+connectTwitterURL);
				if (listener != null) {
					if( connectTwitterType == CONNECT_TWITTER_FROM_SETTINGS ){
						listener.onRequestBodyLayoutStack(MainActivity.LAYOUTCHANGE_PROFILE_SETTINGS_BACK_REFRESH);
					}else if( connectTwitterType == CONNECT_TWITTER_FROM_SHARE_PRODUCT ){
						listener.onRequestBodyLayoutStack(MainActivity.LAYOUTCHANGE_SHARE_PRODUCT_LAYOUT_BACK_REFRESH_SOCIAL);
					}else if( connectTwitterType == CONNECT_TWITTER_FROM_PRODUCT_DETAIL ){
						listener.onRequestBodyLayoutStack(MainActivity.LAYOUTCHANGE_SETUP_SHARE_SOCIAL_LAYOUT_BACK_REFRESH);
					}else if( connectTwitterType == CONNECT_TWITTER_FROM_CHANGESETTING_CHANGE_IMAGE ){
						listener.onRequestBodyLayoutStack(MainActivity.LAYOUTCHANGE_PROFILE_SETTINGS_BACK_CHANGE_IMAGE_FROM_TWITTER);
					}else if( connectTwitterType == CONNECT_TWITTER_FROM_FIND_FRIEND ){
						listener.onRequestBodyLayoutStack(MainActivity.LAYOUTCHANGE_FIND_FRIEND_BACK_REFRESH);
					}
				}
			}else{
				System.out.println("ConnectTwitter : "+url);
				/*alertDialog.setTitle( "Error" );
				alertDialog.setMessage( "Sign in using Twitter fail." );
				alertDialog.setButton( "ok", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						alertDialog.dismiss();
						if (listener != null) {
							if( connectTwitterType == CONNECT_TWITTER_FROM_SETTINGS ){
								listener.onRequestBodyLayoutStack(MainActivity.LAYOUTCHANGE_PROFILE_SETTINGS_BACK_REFRESH);
							}else if( connectTwitterType == CONNECT_TWITTER_FROM_SHARE_PRODUCT ){
								listener.onRequestBodyLayoutStack(MainActivity.LAYOUTCHANGE_SHARE_PRODUCT_LAYOUT_BACK_REFRESH_SOCIAL);
							}else if( connectTwitterType == CONNECT_TWITTER_FROM_PRODUCT_DETAIL ){
								listener.onRequestBodyLayoutStack(MainActivity.LAYOUTCHANGE_SETUP_SHARE_SOCIAL_LAYOUT_BACK_REFRESH);
							}
						}
					}
				});
				alertDialog.show();*/
			}
		}
	}

	private void setCookies(final String setUrl) {
		(new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				CookieSyncManager
						.createInstance(ProfileLayoutWebView.this.getContext());
				CookieManager.getInstance().removeSessionCookie();
				SystemClock.sleep(1000);
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				super.onPostExecute(result);
				CookieStore cs = app.getAppCookie();
				List<Cookie> cookies = cs.getCookies();
				StringBuilder sb = new StringBuilder();
				for (Cookie c : cookies) {
					sb.append(convertCookieToString(c));
				}

				CookieManager.getInstance().setCookie( getableURL,
						sb.toString());
				CookieSyncManager.getInstance().sync();

				webView.postUrl(setUrl, null);
				webView.requestFocus(View.FOCUS_DOWN);
			}

		}).execute();
	}

}
