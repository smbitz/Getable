package com.codegears.getable.ui;

import com.codegears.getable.BodyLayoutStackListener;
import com.codegears.getable.R;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

public class UserProfileLayout extends LinearLayout implements OnClickListener {

	public static final int LAYOUTCHANGE_BADGE = 3;
	
	private Button badgeButton;
	private BodyLayoutStackListener listener;
	
	public UserProfileLayout( Context context ) {
		super( context );
		View.inflate( context, R.layout.userprofilelayout, this );
		badgeButton = (Button)this.findViewById( R.id.BadgeButton );
		badgeButton.setOnClickListener( this );
	}
	
	public void setBodyLayoutChangeListener(BodyLayoutStackListener listener){
		this.listener = listener;
	}

	@Override
	public void onClick( View view ) {
		if(badgeButton.equals( view )){
			if(listener != null){
				listener.onRequestBodyLayoutStack( LAYOUTCHANGE_BADGE );
			}
		}
	}

}
