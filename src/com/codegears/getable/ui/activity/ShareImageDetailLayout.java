package com.codegears.getable.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import com.codegears.getable.R;
import com.codegears.getable.ui.AbstractViewLayout;

public class ShareImageDetailLayout extends AbstractViewLayout {

	public ShareImageDetailLayout(Activity activity) {
		super(activity);
		View.inflate( this.getContext(), R.layout.shareimagedetaillayout, this );
	}

	@Override
	public void refreshView(Intent getData) {
		// TODO Auto-generated method stub
		
	}

}
