package com.codegears.getable.ui;

import com.codegears.getable.data.BrandData;
import com.codegears.getable.data.StoreData;

import android.content.Context;
import android.widget.TextView;

public class ProductStoreAddress extends TextView {
	
	private StoreData storeData;
	
	public ProductStoreAddress(Context context) {
		super(context);
	}
	
	public void setStoreData( StoreData setData ){
		storeData = setData;
	}
	
	public StoreData getStoreData(){
		return storeData;
	}
	
}
