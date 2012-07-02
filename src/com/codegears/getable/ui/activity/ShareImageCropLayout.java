package com.codegears.getable.ui.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.codegears.getable.BodyLayoutStackListener;
import com.codegears.getable.MainActivity;
import com.codegears.getable.R;
import com.codegears.getable.ui.AbstractViewLayout;
import com.codegears.getable.ui.CropView;
import android.view.View.OnClickListener;

public class ShareImageCropLayout extends AbstractViewLayout implements OnClickListener {
	
	public static final String TEMP_IMAGE_FILE_NAME = "shareImageTmp.PNG";
	public static final String TEMP_IMAGE_DIRECTORY_NAME = "/getable/"; 
	
	private CropView cropImageView;
	private Button doneButton;
	private ImageView cropImage;
	private BodyLayoutStackListener listener;
	private String extStorageDirectory;
	private Activity activity;
	
	public ShareImageCropLayout(Activity activity) {
		super(activity);
		this.activity = activity;
		View.inflate( this.getContext(), R.layout.shareimagecroplayout,  this );
		
		cropImageView = (CropView) findViewById( R.id.shareImageCropImageView );
		doneButton = (Button) findViewById( R.id.shareImageCropDoneButton );
		Bitmap arrow = BitmapFactory.decodeResource( this.getResources(), R.drawable.crop_image_button );
		cropImageView.setButton( arrow, arrow, arrow, arrow );
		
		extStorageDirectory = Environment.getExternalStorageDirectory().toString()+TEMP_IMAGE_DIRECTORY_NAME;
		
		File tempDirectory = new File( extStorageDirectory );
		if( !(tempDirectory.exists()) ){
			tempDirectory.mkdir();
		}
		
		doneButton.setOnClickListener( this );
	}
	
	public void setImage(final Bitmap bitmap ){
				cropImageView.setCropRatio( 1 );
				cropImageView.setImageBitmap( bitmap );
				cropImageView.setBitmap(bitmap);			
	}

	@Override
	public void refreshView(Intent getData) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void refreshView() {
		// TODO Auto-generated method stub
		
	}
	
	public void setBodyLayoutChangeListener(BodyLayoutStackListener listener){
		this.listener = listener;
	}

	@Override
	public void onClick(View v) {
		if(listener != null){
			if( v.equals( doneButton ) ){
				//Delete old value.
				SharedPreferences myPref = this.getActivity().getSharedPreferences( ShareImageDetailLayout.SHARE_PREF_DETAIL_VALUE, this.getActivity().MODE_PRIVATE );
				SharedPreferences.Editor prefsEditor = myPref.edit();
				prefsEditor.putString( ShareImageDetailLayout.SHARE_PREF_KEY_CATEGORY_NAME, null );
				prefsEditor.putString( ShareImageDetailLayout.SHARE_PREF_KEY_CATEGORY_ID, null );
				prefsEditor.putString( ShareImageDetailLayout.SHARE_PREF_KEY_BRAND_NAME, null );
				prefsEditor.putString( ShareImageDetailLayout.SHARE_PREF_KEY_STORES_NAME, null );
				prefsEditor.putString( ShareImageDetailLayout.SHARE_PREF_KEY_STORES_ID, null );
				prefsEditor.putString( ShareImageDetailLayout.SHARE_PREF_KEY_GENDER_ID, null );
				prefsEditor.putString( ShareImageDetailLayout.SHARE_PREF_KEY_STORES_EXTERNAL_ID, null );
				prefsEditor.putString( ShareImageDetailLayout.SHARE_PREF_KEY_STORES_EXTERNAL_TYPE_ID, null );
		        prefsEditor.commit();
				
		        OutputStream outStream = null;
		        File file = new File(extStorageDirectory, TEMP_IMAGE_FILE_NAME);
		        
		        try {
		        	Bitmap bitmap = cropImageView.getCropImage();
		        	
					outStream = new FileOutputStream(file);
					bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
				    outStream.flush();
				    outStream.close();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		        
				listener.onRequestBodyLayoutStack( MainActivity.LAYOUTCHANGE_SHARE_IMAGE_DETAIL );
			}
		}
	}

}
