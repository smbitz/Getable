package com.codegears.getable.ui;

import com.codegears.getable.R;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;

public class BadgeListItem extends LinearLayout {


	public BadgeListItem( Context context ) {
		super( context );
		View.inflate( context, R.layout.badgelistitem, this );
	}
}
