package com.codegears.getable.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.codegears.getable.BodyLayoutStackListener;
import com.codegears.getable.MainActivity;
import com.codegears.getable.R;
import com.codegears.getable.ui.AbstractViewLayout;
import com.codegears.getable.ui.CropView;
import android.view.View.OnClickListener;

public class ShareImageCropLayout extends AbstractViewLayout implements OnClickListener {
	
	private CropView cropImageView;
	private Button doneButton;
	private ImageView cropImage;
	private BodyLayoutStackListener listener;
	
	public ShareImageCropLayout(Activity activity) {
		super(activity);
		View.inflate( this.getContext(), R.layout.shareimagecroplayout,  this );
		
		cropImageView = (CropView) findViewById( R.id.shareImageCropImageView );
		doneButton = (Button) findViewById( R.id.shareImageCropDoneButton );
		Bitmap arrow = BitmapFactory.decodeResource( this.getResources(), R.drawable.ic_launcher );
		cropImageView.setButton( arrow, arrow, arrow, arrow );
		
		doneButton.setOnClickListener( this );
	}
	
	public void setImage( Bitmap bitmap ){
		cropImageView.setCropRatio( 1 );
		cropImageView.setImageBitmap( bitmap );
		cropImageView.setBitmap(bitmap);
	}

	@Override
	public void refreshView(Intent getData) {
		// TODO Auto-generated method stub
		
	}
	
	public void setBodyLayoutChangeListener(BodyLayoutStackListener listener){
		this.listener = listener;
	}

	@Override
	public void onClick(View v) {
		if(listener != null){
			if( v.equals( doneButton ) ){
				listener.onRequestBodyLayoutStack( MainActivity.LAYOUTCHANGE_SHARE_IMAGE_DETAIL );
			}
		}
	}

}
