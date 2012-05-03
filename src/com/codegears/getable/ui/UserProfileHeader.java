package com.codegears.getable.ui;

import com.codegears.getable.R;
import com.codegears.getable.data.ActorData;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class UserProfileHeader extends LinearLayout {
	
	private ImageView userImage;
	private TextView userName;
	private ActorData userData;
	
	public UserProfileHeader(Context context) {
		super(context);
		View.inflate(context, R.layout.userprofileheader, this);
		
		userImage = (ImageView) findViewById( R.id.userProfileImage );
		userName = (TextView) findViewById( R.id.userProfileName );
	}

	public void setName(String setUserName) {
		userName.setText( setUserName );
	}

	public void setImage(Bitmap setUserImage) {
		userImage.setImageBitmap( setUserImage );
	}

	public void setData(ActorData setUserData) {
		userData = setUserData;
	}
	
	public ActorData getUserData(){
		return userData;
	}

}
