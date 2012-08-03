package com.codegears.getable.ui;

import com.codegears.getable.MyApp;
import com.codegears.getable.R;
import com.codegears.getable.data.BrandData;
import com.codegears.getable.data.CategoryData;
import com.codegears.getable.data.StoreData;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ShareProductSearchItem extends LinearLayout {
	
	private TextView name;
	private CategoryData categoryData;
	private BrandData brandData;
	private StoreData storeData;
	private String genderId;
	private ImageView arrowRight;
	
	public ShareProductSearchItem(Context context) {
		super(context);
		View.inflate(context, R.layout.shareproductsearchitem, this);
		
		name = (TextView) findViewById( R.id.shareProductSearchItemName );
		arrowRight = (ImageView) findViewById( R.id.shareProductSearchItemArrowRightImage );
		
		//Set font
		name.setTypeface( Typeface.createFromAsset( this.getContext().getAssets(), MyApp.APP_FONT_PATH) );
	}
	
	public void setName( String setValue ){
		name.setText( setValue ); 
	}

	public void setCategoryData( CategoryData setValue ) {
		categoryData = setValue;
	}
	
	public void setBrandData( BrandData setValue ) {
		brandData = setValue;
	}
	
	public void setStoreData(StoreData setValue) {
		storeData = setValue;
	}
	
	public void setGenderId(String setValue) {
		genderId = setValue;
	}
	
	public void setArrowVisibility(int visibility){
		arrowRight.setVisibility(visibility);
	}
	
	public CategoryData getCategoryData() {
		return categoryData;
	}
	
	public BrandData getBrandData(){
		return brandData;
	}
	
	public StoreData getStoreData(){
		return storeData;
	}
	
	public String getGenderId(){
		return genderId;
	}
	
}
