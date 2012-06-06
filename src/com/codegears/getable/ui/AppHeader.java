package com.codegears.getable.ui;

import com.codegears.getable.R;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class AppHeader extends LinearLayout {

	public AppHeader(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.inflate(context, R.layout.appheader, this);
	}

}
