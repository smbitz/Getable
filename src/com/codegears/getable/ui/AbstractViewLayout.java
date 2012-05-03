package com.codegears.getable.ui;

import android.app.Activity;
import android.content.Intent;
import android.widget.LinearLayout;

public abstract class AbstractViewLayout extends LinearLayout {
	
	private Activity activity;
	
	public AbstractViewLayout(Activity activity) {
		super(activity);
		this.activity = activity;
	}
	
	public Activity getActivity(){
		return this.activity;
	}
	
	public abstract void refreshView( Intent getData );
}
