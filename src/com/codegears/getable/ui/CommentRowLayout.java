package com.codegears.getable.ui;

import com.codegears.getable.R;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

public class CommentRowLayout extends LinearLayout {

	public CommentRowLayout(Context context) {
		super(context);
		View.inflate(context, R.layout.commentrowlayout, this);
	}

}
