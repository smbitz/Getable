package com.codegears.getable.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;

public class RotateAndScaleImage {
	
	public static Bitmap getMatrixRotateAndScaleImageFromCamera( Context context, Float defaultUploadHeightSize, Bitmap bitmap, Uri selectedImageUri, RotateImage rotateImage ){
		//Check for rotate and scale down image
		Matrix matrix = new Matrix();
		if( bitmap.getHeight() > defaultUploadHeightSize ){
			float scaleValue = (defaultUploadHeightSize/bitmap.getHeight());
			
			int newWidth = (int) ((int) bitmap.getWidth()*scaleValue);//(int) (photo.getWidth()*scaleValue);
			int newHeight = defaultUploadHeightSize.intValue();//(int) (photo.getHeight()*scaleValue);
			
			bitmap = Bitmap.createScaledBitmap( bitmap, newWidth, newHeight, false );
		}
		int rotation = rotateImage.getCameraPhotoOrientation( context, selectedImageUri, selectedImageUri.getPath() );
		if (rotation != 0f) {
		     matrix.preRotate(rotation);
		}
		
		Bitmap.createBitmap( bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		
		return bitmap;
	}
	
	public static Matrix getMatrixRotateAndScaleImageFromGallery( Context context, Float defaultUploadHeightSize, Bitmap bitmap, Uri selectedImageUri, RotateImage rotateImage ){
		//Check for rotate and scale down image
		Matrix matrix = new Matrix();
		if( bitmap.getHeight() > defaultUploadHeightSize ){
			float scaleValue = (defaultUploadHeightSize/bitmap.getHeight());
			
			int newWidth = (int) ((int) bitmap.getWidth()*scaleValue);//(int) (photo.getWidth()*scaleValue);
			int newHeight = defaultUploadHeightSize.intValue();//(int) (photo.getHeight()*scaleValue);
			
			bitmap = Bitmap.createScaledBitmap( bitmap, newWidth, newHeight, false );
		}
		int rotation = rotateImage.getGalleryPhotoOrientation( context, selectedImageUri);
		if (rotation != 0f) {
		     matrix.preRotate(rotation);
		}
		
		return matrix;
	}
	
}
