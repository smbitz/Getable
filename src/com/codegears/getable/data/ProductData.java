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
			brand = new BrandData( (JSONObject) jsonObject.get("brand") );
			category = new CategoryData( (JSONObject) jsonObject.get("category") );
			store = new StoreData( (JSONObject) jsonObject.get("store") );
			picture = new ProductPicture( (JSONObject) jsonObject.get("picture") );
			gender = new ProductGender( (JSONObject) jsonObject.get("gender") );
			price = jsonObject.getString("price");
			keywords = jsonObject.getString("keywords");
			description = jsonObject.getString("description");
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
