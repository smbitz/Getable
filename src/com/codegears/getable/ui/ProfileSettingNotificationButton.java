package com.codegears.getable.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageButton;

public class ProfileSettingNotificationButton extends ImageButton {
	
	public static final int NOTIFICATION_STATUS_ON = 0;
	public static final int NOTIFICATION_STATUS_OFF = 1;
	
	private int buttonStatus;
	
	public ProfileSettingNotificationButton(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public void setButtonStatus( int setStatusValue ){
		buttonStatus = setStatusValue;
	}
	
	public int getButtonStatus(){
		return buttonStatus;
	}

}
