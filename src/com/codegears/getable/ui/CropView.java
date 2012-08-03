package com.codegears.getable.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

public class CropView extends ImageView {

	private static final float MIN_CROP_WIDTH = 0.1f;
	private static final float MIN_CROP_HEIGHT = 0.1f;

	private RectF cropArea;
	private Bitmap upButton;
	private Bitmap downButton;
	private Bitmap leftButton;
	private Bitmap rightButton;
	private Bitmap imageBitmap;
	private float cropRatio;

	private static final int DRAG_NOT = 0;
	private static final int DRAG_DRAG = 1;
	private static final int DRAG_TOP = 2;
	private static final int DRAG_BOTTOM = 3;
	private static final int DRAG_LEFT = 4;
	private static final int DRAG_RIGHT = 5;

	private int dragState;
	private float dragX;
	private float dragY;

	public CropView( Context context, AttributeSet attrs ) {
		super( context, attrs );
		dragState = DRAG_NOT;
		cropArea = new RectF( 0f, 0f, 0.5f, 0.5f );
	}

	/*
	 * Set xy ratio of crop area
	 */
	public void setCropRatio( float xyRatio ) {
		float xyRatioView = (float) this.getWidth() / this.getHeight();
		cropRatio = xyRatio;
		cropArea.left = 0;
		cropArea.top = 0;
//		cropArea.right = 1f * xyRatio / xyRatioView;
		cropArea.right = 1f;
		cropArea.bottom = 1f;
	}
	
	public void alignCenter(){
		if(imageBitmap == null){
			return;
		}
		float xyRatioView = (float) this.getWidth() / this.getHeight();
		float xyRatioImage = (float) imageBitmap.getWidth() / imageBitmap.getHeight();
		if(xyRatioView >= xyRatioImage){
			float adjustedImageWidth = xyRatioImage * this.getHeight();
			float imageWidth = adjustedImageWidth / this.getWidth();
			float imageHeight = imageWidth / cropRatio * xyRatioView;
			cropArea.left = 0.5f - imageWidth / 2;
			cropArea.right = 0.5f + imageWidth / 2;
			cropArea.top = 0.5f - imageHeight / 2;
			cropArea.bottom = 0.5f + imageHeight / 2;
		} else {
			float adjustedImageHeight = this.getWidth() / xyRatioImage;
			float imageHeight = adjustedImageHeight / this.getHeight();
			float imageWidth = imageHeight * cropRatio / xyRatioView;
			cropArea.left = 0.5f - imageWidth / 2;
			cropArea.right = 0.5f + imageWidth / 2;
			cropArea.top = 0.5f - imageHeight / 2;
			cropArea.bottom = 0.5f + imageHeight / 2;
		}
		// Unexpected case, some bug appear above, so cropArea.width() > 1 occur some time.
		if(cropArea.width() > 1){
			float width = cropArea.width();
			float height = cropArea.height();
			cropArea.left = 0;
			cropArea.right = 1;
			cropArea.top = 0.5f - (height / width) / 2;
			cropArea.bottom = 0.5f + (height / width) / 2;
		}
	}
	
	@Override
	public void onWindowFocusChanged(boolean hasWindowFocus){
		super.onWindowFocusChanged(hasWindowFocus);
		float xyRatioView = (float) this.getWidth() / this.getHeight();
		this.setCropRatio(cropRatio);	//reset CropRatio due to the problem of view measure
		adjustCropArea();				//readjust CropArea due to the problem of view measure
		alignCenter();
	}

	/*
	 * set directional button image
	 */
	public void setButton( Bitmap up, Bitmap down, Bitmap left, Bitmap right ) {
		upButton = up;
		downButton = down;
		leftButton = left;
		rightButton = right;
	}

	/*
	 * set Bitmap image using for cropping data
	 */
	public void setBitmap( Bitmap bitmap ) {
		imageBitmap = bitmap;
		adjustCropArea();
	}

	private void adjustCropArea() {
		if ( imageBitmap == null ) {
			return;
		}
		float xyRatioView = (float) this.getWidth() / this.getHeight();
		float xyRatioImage = (float) imageBitmap.getWidth() / imageBitmap.getHeight();
		if ( xyRatioView >= xyRatioImage ) {
			float adjustedImageWidth = xyRatioImage * this.getHeight();
			float space = (this.getWidth() - adjustedImageWidth) / 2;
			float spaceRatio = space / this.getWidth();
			float imageWidth = adjustedImageWidth / this.getWidth();
			if ( cropArea.width() > imageWidth ) {
				float imageHeight = imageWidth / cropRatio * xyRatioView;
				cropArea.right = cropArea.left + imageWidth;
				cropArea.bottom = cropArea.top + imageHeight;
			}
			if ( cropArea.height() > 1 ) {
				float expectedWidth = cropRatio;
				float divWidth = Math.abs( expectedWidth - cropArea.width() );
				cropArea.top = 0;
				cropArea.bottom = 1;
				cropArea.right = cropArea.left + divWidth;
			}
			if ( cropArea.left < spaceRatio ) {
				cropArea.right = cropArea.right + spaceRatio - cropArea.left;
				cropArea.left = spaceRatio;
			} else if ( cropArea.right > 1 - spaceRatio ) {
				cropArea.left = cropArea.left + 1 - spaceRatio - cropArea.right;
				cropArea.right = 1 - spaceRatio;
			}
			if ( cropArea.top < 0 ) {
				cropArea.bottom -= cropArea.top;
				cropArea.top = 0;
			}
			if ( cropArea.bottom > 1 ) {
				cropArea.top -= cropArea.bottom - 1;
				cropArea.bottom = 1;
			}
		} else if ( xyRatioView < xyRatioImage ) {		
			float adjustedImageHeight = this.getWidth() / xyRatioImage;
			float space = (this.getHeight() - adjustedImageHeight) / 2;
			float spaceRatio = space / this.getHeight();
			float imageHeight = adjustedImageHeight / this.getHeight();
			if ( cropArea.height() > imageHeight ) {
				float imageWidth = imageHeight * cropRatio / xyRatioView;
				cropArea.bottom = cropArea.top + imageHeight;
				cropArea.right = cropArea.left + imageWidth;
			}
			if ( cropArea.width() > 1 ) {
				float width = cropArea.width();
				cropArea.left = 0;
				cropArea.right = 1;
				cropArea.bottom = cropArea.top + cropArea.height() / width;
			}
			if ( cropArea.top < spaceRatio ) {
				cropArea.bottom = cropArea.bottom + spaceRatio - cropArea.top;
				cropArea.top = spaceRatio;
			} else if ( cropArea.bottom > 1 - spaceRatio ) {
				cropArea.top = cropArea.top + 1 - spaceRatio - cropArea.bottom;
				cropArea.bottom = 1 - spaceRatio;
			}
			if ( cropArea.left < 0 ) {
				cropArea.right -= cropArea.left;
				cropArea.left = 0;
			}
			if ( cropArea.right > 1 ) {
				cropArea.left -= cropArea.right - 1;
				cropArea.right = 1;
			}
		}
	}

	/*
	 * Retrieve cropped Image as Bitmap
	 */
	public Bitmap getCropImage() {
		float cropTop = 0.0f;
		float cropBottom = 0.0f;
		float cropLeft = 0.0f;
		float cropRight = 0.0f;

		float xyRatioView = (float) this.getWidth() / this.getHeight();
		float xyRatioImage = (float) imageBitmap.getWidth() / imageBitmap.getHeight();
		if ( xyRatioView >= xyRatioImage ) {
			cropTop = cropArea.top * imageBitmap.getHeight();
			cropBottom = cropArea.bottom * imageBitmap.getHeight();
			
			float adjustedImageWidth = xyRatioImage * this.getHeight();
			float space = (this.getWidth() - adjustedImageWidth) / 2;
			float spaceRatio = space / this.getWidth();
			cropLeft = cropArea.left - spaceRatio;
			cropLeft = cropLeft * imageBitmap.getWidth() / (1 - spaceRatio * 2);
			cropRight = cropArea.right - spaceRatio;
			cropRight = cropRight * imageBitmap.getWidth() / (1 - spaceRatio * 2);
		} else if(xyRatioView < xyRatioImage){
			cropLeft = cropArea.left * imageBitmap.getWidth();
			cropRight = cropArea.right * imageBitmap.getWidth();
			
			float adjustedImageHeight = this.getWidth() / xyRatioImage;
			float space = (this.getHeight() - adjustedImageHeight) / 2;
			float spaceRatio = space / this.getHeight();
			cropTop = cropArea.top - spaceRatio;
			cropTop = cropTop * imageBitmap.getHeight() / (1 - spaceRatio * 2);
			cropBottom = cropArea.bottom - spaceRatio;
			cropBottom = cropBottom * imageBitmap.getHeight() / (1 - spaceRatio * 2);
		}
		return Bitmap.createBitmap( imageBitmap, (int) cropLeft, (int) cropTop,
						(int) (cropRight - cropLeft), (int) (cropBottom - cropTop) );
	}

	@Override
	public void onDraw( Canvas canvas ) {
		super.onDraw( canvas );
		Paint borderPaint = new Paint();
		borderPaint.setColor( 0xff0000ff );
		Paint outterAreaPaint = new Paint();
		outterAreaPaint.setColor( 0x7F000000 );
		float cropTop = cropArea.top * this.getHeight();
		float cropBottom = cropArea.bottom * this.getHeight();
		float cropLeft = cropArea.left * this.getWidth();
		float cropRight = cropArea.right * this.getWidth();
		canvas.drawRect( 0, 0, this.getWidth(), cropTop, outterAreaPaint );
		canvas.drawRect( 0, cropTop, cropLeft, cropBottom, outterAreaPaint );
		canvas.drawRect( cropRight, cropTop, this.getWidth(), cropBottom, outterAreaPaint );
		canvas.drawRect( 0, cropBottom, this.getWidth(), this.getHeight(), outterAreaPaint );
		canvas.drawLine( cropLeft, cropTop, cropRight, cropTop, borderPaint );
		canvas.drawLine( cropRight, cropTop, cropRight, cropBottom, borderPaint );
		canvas.drawLine( cropRight, cropBottom, cropLeft, cropBottom, borderPaint );
		canvas.drawLine( cropLeft, cropBottom, cropLeft, cropTop, borderPaint );
		if ( upButton != null ) {
			float upButtonHalfHeight = upButton.getHeight() / 2;
			canvas.drawBitmap( upButton, (cropLeft + cropRight - upButton.getWidth()) / 2, cropTop
							- upButtonHalfHeight, borderPaint );
		}
		if ( downButton != null ) {
			float downButtonHalfHeight = downButton.getHeight() / 2;
			canvas.drawBitmap( downButton, (cropLeft + cropRight - downButton.getWidth()) / 2, cropBottom
							- downButtonHalfHeight, borderPaint );
		}
		if ( leftButton != null ) {
			float leftButtonHalfWidth = leftButton.getWidth() / 2;
			canvas.drawBitmap( leftButton, cropLeft - leftButtonHalfWidth,
							(cropTop + cropBottom - leftButton.getHeight()) / 2, borderPaint );
		}
		if ( rightButton != null ) {
			float rightButtonHalfWidth = rightButton.getWidth() / 2;
			canvas.drawBitmap( rightButton, cropRight - rightButtonHalfWidth,
							(cropTop + cropBottom - rightButton.getHeight()) / 2, borderPaint );
		}
	}

	private boolean isTouchInCropRect( float x, float y ) {
		float ratioX = x / this.getWidth();
		float ratioY = y / this.getHeight();
		return cropArea.contains( ratioX, ratioY );
	}

	private boolean isTouchInLeftArrow( float x, float y ) {
		float pixelYCropArea = (cropArea.bottom + cropArea.top) / 2 * this.getHeight();
		float pixelXCropArea = cropArea.left * this.getWidth();
		if ( (pixelYCropArea - 25 < y) && (y < pixelYCropArea + 25) ) {
			if ( (pixelXCropArea - 25 < x) && (x < pixelXCropArea + 25) ) {
				return true;
			}
		}
		return false;
	}

	private boolean isTouchInRightArrow( float x, float y ) {
		float pixelYCropArea = (cropArea.bottom + cropArea.top) / 2 * this.getHeight();
		float pixelXCropArea = cropArea.right * this.getWidth();
		if ( (pixelYCropArea - 25 < y) && (y < pixelYCropArea + 25) ) {
			if ( (pixelXCropArea - 25 < x) && (x < pixelXCropArea + 25) ) {
				return true;
			}
		}
		return false;
	}

	private boolean isTouchInTopArrow( float x, float y ) {
		float pixelYCropArea = cropArea.top * this.getHeight();
		float pixelXCropArea = (cropArea.left + cropArea.right) / 2 * this.getWidth();
		if ( (pixelYCropArea - 25 < y) && (y < pixelYCropArea + 25) ) {
			if ( (pixelXCropArea - 25 < x) && (x < pixelXCropArea + 25) ) {
				return true;
			}
		}
		return false;
	}

	private boolean isTouchInBottomArrow( float x, float y ) {
		float pixelYCropArea = cropArea.bottom * this.getHeight();
		float pixelXCropArea = (cropArea.left + cropArea.right) / 2 * this.getWidth();
		if ( (pixelYCropArea - 25 < y) && (y < pixelYCropArea + 25) ) {
			if ( (pixelXCropArea - 25 < x) && (x < pixelXCropArea + 25) ) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean onTouchEvent( MotionEvent event ) {
		if ( event.getAction() == MotionEvent.ACTION_DOWN ) {
			if ( isTouchInLeftArrow( event.getX(), event.getY() ) ) {
				dragState = DRAG_LEFT;
				dragX = event.getX();
				dragY = event.getY();
			} else if ( isTouchInRightArrow( event.getX(), event.getY() ) ) {
				dragState = DRAG_RIGHT;
				dragX = event.getX();
				dragY = event.getY();
			} else if ( isTouchInTopArrow( event.getX(), event.getY() ) ) {
				dragState = DRAG_TOP;
				dragX = event.getX();
				dragY = event.getY();
			} else if ( isTouchInBottomArrow( event.getX(), event.getY() ) ) {
				dragState = DRAG_BOTTOM;
				dragX = event.getX();
				dragY = event.getY();
			} else if ( isTouchInCropRect( event.getX(), event.getY() ) ) {
				dragState = DRAG_DRAG;
				dragX = event.getX();
				dragY = event.getY();
			}
		} else if ( event.getAction() == MotionEvent.ACTION_UP ) {
			dragState = DRAG_NOT;
		} else if ( dragState == DRAG_DRAG ) {
			float divX = event.getX() - dragX;
			float divY = event.getY() - dragY;
			float divXRatio = divX / this.getWidth();
			float divYRatio = divY / this.getHeight();
			cropArea.top += divYRatio;
			cropArea.bottom += divYRatio;
			cropArea.left += divXRatio;
			cropArea.right += divXRatio;
			dragX = event.getX();
			dragY = event.getY();
			adjustCropArea();
			this.invalidate();
		} else if ( dragState == DRAG_TOP ) {
			float xyRatioView = (float) this.getWidth() / this.getHeight();
			float divY = event.getY() - dragY;
			float divYRatio = divY / this.getHeight();
			cropArea.top += divYRatio;
			if ( cropArea.bottom - cropArea.top < MIN_CROP_HEIGHT ) {
				cropArea.top = cropArea.bottom - MIN_CROP_HEIGHT;
			}
			float areaHeight = cropArea.bottom - cropArea.top;
			float areaWidth = areaHeight * cropRatio / xyRatioView;
			float divWidth = (areaWidth - (cropArea.right - cropArea.left)) / 2;
			cropArea.left -= divWidth;
			cropArea.right += divWidth;
			dragX = event.getX();
			dragY = event.getY();
			adjustCropArea();
			this.invalidate();
		} else if ( dragState == DRAG_BOTTOM ) {
			float xyRatioView = (float) this.getWidth() / this.getHeight();
			float divY = event.getY() - dragY;
			float divYRatio = divY / this.getHeight();
			cropArea.bottom += divYRatio;
			if ( cropArea.bottom - cropArea.top < MIN_CROP_HEIGHT ) {
				cropArea.bottom = cropArea.top + MIN_CROP_HEIGHT;
			}
			float areaHeight = cropArea.bottom - cropArea.top;
			float areaWidth = areaHeight * cropRatio / xyRatioView;
			float divWidth = (areaWidth - (cropArea.right - cropArea.left)) / 2;
			cropArea.left -= divWidth;
			cropArea.right += divWidth;
			dragX = event.getX();
			dragY = event.getY();
			adjustCropArea();
			this.invalidate();
		} else if ( dragState == DRAG_LEFT ) {
			float xyRatioView = (float) this.getWidth() / this.getHeight();
			float divX = event.getX() - dragX;
			float divXRatio = divX / this.getWidth();
			cropArea.left += divXRatio;
			if ( cropArea.right - cropArea.left < MIN_CROP_WIDTH ) {
				cropArea.right = cropArea.left + MIN_CROP_WIDTH;
			}
			float areaWidth = cropArea.right - cropArea.left;
			float areaHeight = areaWidth / cropRatio * xyRatioView;
			float divHeight = (areaHeight - (cropArea.bottom - cropArea.top)) / 2;
			cropArea.top -= divHeight;
			cropArea.bottom += divHeight;
			dragX = event.getX();
			dragY = event.getY();
			adjustCropArea();
			this.invalidate();
		} else if ( dragState == DRAG_RIGHT ) {
			float xyRatioView = (float) this.getWidth() / this.getHeight();
			float divX = event.getX() - dragX;
			float divXRatio = divX / this.getWidth();
			cropArea.right += divXRatio;
			if ( cropArea.right - cropArea.left < MIN_CROP_WIDTH ) {
				cropArea.right = cropArea.left + MIN_CROP_WIDTH;
			}
			float areaWidth = cropArea.right - cropArea.left;
			float areaHeight = areaWidth / cropRatio * xyRatioView;
			float divHeight = (areaHeight - (cropArea.bottom - cropArea.top)) / 2;
			cropArea.top -= divHeight;
			cropArea.bottom += divHeight;
			dragX = event.getX();
			dragY = event.getY();
			adjustCropArea();
			this.invalidate();
		}
		return true;
	}

}