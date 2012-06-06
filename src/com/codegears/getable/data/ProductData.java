package com.codegears.getable.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ProductData {
	private BrandData brand;
	private CategoryData category;
	private StoreData store;
	private ProductPicture picture;
	private ProductGender gender;
	private String price;
	private String keywords;
	private String description;
	
	public ProductData(JSONObject jsonObject) {
		if( jsonObject.optJSONObject("brand") != null ){
			brand = new BrandData( jsonObject.optJSONObject("brand") );
		}
		
		if( jsonObject.optJSONObject("category") != null ){
			category = new CategoryData( jsonObject.optJSONObject("category") );
		}
		
		if( jsonObject.optJSONObject("store") != null ){
			store = new StoreData( jsonObject.optJSONObject("store") );
		}
		
		if( jsonObject.optJSONObject("picture") != null ){
			picture = new ProductPicture( jsonObject.optJSONObject("picture") );
		}
		
		if( jsonObject.optJSONObject("gender") != null ){
			gender = new ProductGender( jsonObject.optJSONObject("gender") );
		}

		price = jsonObject.optString("price");
		keywords = jsonObject.optString("keywords");
		description = jsonObject.optString("description");
	}
	
	public ProductPicture getProductPicture(){
		return picture;
	}
	
	public BrandData getBrand(){
		return brand;
	}
	
	public String getDescription(){
		return description;
	}
	
	public String getPrice(){
		return price;
	}
	
	public StoreData getStore(){
		return store;
	}
}
