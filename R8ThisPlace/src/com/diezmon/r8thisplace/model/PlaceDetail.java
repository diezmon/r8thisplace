package com.diezmon.r8thisplace.model;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

public class PlaceDetail implements Serializable {
	   
	public static String PLACE_DETAIL_KEY = "placeDetail";
	
	private static final long serialVersionUID = 1L;

	public String id;

	public String name;

	public String reference;

	public String icon;

	public String url;

	public String webSite = null;

	public String formatted_address;

	public String formatted_phone_number;

	public double latitude;
	public double longitude;
	
	public JSONObject ratingInfoJson;

	public String toString() {
		return name + " - " + id + " - " + reference;
	}

	public PlaceDetail(JSONObject jsonObj) throws JSONException {

		JSONObject result = jsonObj.getJSONObject("result");

		JSONObject location = result.getJSONObject("geometry").getJSONObject(
				"location");
		this.latitude = location.getDouble("lat");
		this.longitude = location.getDouble("lng");

		this.icon = result.getString("icon");
		this.id = result.getString("id");
		this.name = result.getString("name");
		this.formatted_phone_number = result
				.getString("formatted_phone_number");
		this.formatted_address = result.getString("formatted_address");
		this.reference = result.getString("reference");
		this.url = result.getString("url");
		
		if (result.has("website"))
		{
			this.webSite = result.getString("website");
		}

	}
	 

}
