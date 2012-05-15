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
		try {
			brand = new BrandData( jsonObject.getJSONObject("brand") );
			category = new CategoryData( jsonObject.getJSONObject("category") );
			store = new StoreData( jsonObject.getJSONObject("store") );
			picture = new ProductPicture( jsonObject.getJSONObject("picture") );
			gender = new ProductGender( jsonObject.getJSONObject("gender") );
			price = jsonObject.optString("price");
			keywords = jsonObject.optString("keywords");
			description = jsonObject.optString("description");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//keywords
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
