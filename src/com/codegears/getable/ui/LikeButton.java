package com.codegears.getable.ui;

import android.content.Context;
import android.widget.Button;

public class LikeButton extends Button {
	
	public static final int BUTTON_STATUS_UNLIKE = 0;
	public static final int BUTTON_STATUS_LIKE = 1;
	
	public LikeButton(Context context) {
		super(context);
		this.setText( "Like" );
	}
	
}
