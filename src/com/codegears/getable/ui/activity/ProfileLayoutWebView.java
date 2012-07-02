package com.codegears.getable.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import com.codegears.getable.R;
import com.codegears.getable.ui.AbstractViewLayout;
import android.view.View.OnClickListener;

public class ProfileLayoutWebView extends AbstractViewLayout implements OnClickListener {
	
	public static final String SHARE_PREF_WEB_VIEW_TYPE = "SHARE_PREF_WEB_VIEW_TYPE";
	public static final String WEB_VIEW_TYPE = "WEB_VIEW_TYPE";
	public static final String WEB_VIEW_TYPE_TERMS = "WEB_VIEW_TYPE_TERMS";
	public static final String WEB_VIEW_TYPE_POLICY = "WEB_VIEW_TYPE_POLICY";
	
	private static final String WEB_VIEW_TERMS_URL = "http://www.getableapp.com/terms";
	private static final String WEB_VIEW_POLICY_URL = "http://www.getableapp.com/privacy";
	
	private Button openBrowserButton;
	private WebView webView;
	private String webViewType;
	
	public ProfileLayoutWebView(Activity activity) {
		super(activity);
		View.inflate( this.getContext(), R.layout.profilelayoutwebview, this );
		
		SharedPreferences myPrefs = this.getActivity().getSharedPreferences( SHARE_PREF_WEB_VIEW_TYPE, this.getActivity().MODE_PRIVATE );
		webViewType = myPrefs.getString( WEB_VIEW_TYPE, null );
		
		openBrowserButton = (Button) findViewById( R.id.profileLayoutWebViewOpenBrowserButton );
		webView = (WebView) findViewById( R.id.profileLayoutWebViewLayout );
		
		openBrowserButton.setOnClickListener( this );
		
		webView.getSettings().setDefaultTextEncodingName("utf-8");
		webView.getSettings().setJavaScriptEnabled(true);
		if( webViewType.equals( WEB_VIEW_TYPE_TERMS ) ){
			webView.loadUrl( WEB_VIEW_TERMS_URL );
		}else if( webViewType.equals( WEB_VIEW_TYPE_POLICY ) ){
			webView.loadUrl( WEB_VIEW_POLICY_URL );
		}
		
		webView.setWebViewClient( new WebViewLayoutClient() );
	}
	
	@Override
	public void refreshView(Intent getData) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void refreshView() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onClick(View v) {
		if( v.equals( openBrowserButton ) ){
			if( webViewType.equals( WEB_VIEW_TYPE_TERMS ) ){
				Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse( WEB_VIEW_TERMS_URL ));
				this.getActivity().startActivity(browserIntent);
			}else if( webViewType.equals( WEB_VIEW_TYPE_POLICY ) ){
				Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse( WEB_VIEW_POLICY_URL ));
				this.getActivity().startActivity(browserIntent);
			}
		}
	}
	
	private class WebViewLayoutClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url);
    		return true;
		}
	}

}
