package com.diezmon.r8thisplace.util;
 
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.TimeZone;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import com.diezmon.r8thisplace.App;
import com.diezmon.r8thisplace.R;
import com.diezmon.r8thisplace.model.PlaceDetail;

import android.content.res.Resources;
import android.util.Log;
 
public class JSONParser {
    
    //place details. 
    //https://maps.googleapis.com/maps/api/place/details/json?reference=CoQBcwAAAG72jWPgGdN_WgNZc-Pvdvhy5aQ4DZBEUCnxUhrHMZJNkEmiZX389imU9os20c9srlA_agb_DLPzYsA2M79EBAWM8VaD_8z51q9S4rfWj9y8w-0zm1oPWAUChtvAEf_Us9HYgua7EwvODpTvx2I1bETHrYbaZHVNvb-vi_sIW5qrEhCYTE2YHb2K_5jMcJXKKqD2GhSsJhVpHcLnbqXtVWrzAJL2Vm5Gvg&sensor=true&key=AIzaSyA3neLk5bHTeQi1bD7WpfM0-EXUJKduVsQ
    public JSONParser() {
 
    }
    
    private static Resources getResources()
	{
		return  App.getContext().getResources();
	}
    
    public static String getPlaceDetailsUrl(String reference)
    {
    	
    	StringBuffer searchUrl = 
    			new StringBuffer( "https://maps.googleapis.com/maps/api/place/details/json?reference=")  
    	.append(reference)
    	.append("&key=AIzaSyA3neLk5bHTeQi1bD7WpfM0-EXUJKduVsQ&sensor=false");
    	return searchUrl.toString();
    	
    }
    
    public static String getR8ItDetailsUrl(double lat, double lng) throws JSONException
    {
    	
    	String dateString = String.valueOf(Calendar.getInstance().getTimeInMillis());
    	String timezoneId = TimeZone.getDefault().getID();
    	
    	return getR8ItDetailsUrl(lat, lng, dateString, timezoneId);
    	
    	
    }
    
    public static String getR8ItDetailsUrl(double lat, double lng, String dateString, String timezoneId) throws JSONException
    {
    	StringBuffer url = 
    			new StringBuffer(getResources().getString(R.string.r8Url))
    	.append(String.valueOf(lat)).append("/")
    	.append(String.valueOf(lng)).append("/?")
    	.append("tz=").append(timezoneId)
    	.append("&dt=").append(dateString);

    	return url.toString();
    } 
   
    public static JSONObject getSearchResults(String keyword, double lat, double lng) {
    
    	StringBuffer searchUrl = 
    			new StringBuffer( "https://maps.googleapis.com/maps/api/place/search/json?keyword=")  
    	.append(keyword)
    	.append("&location=")
    	.append(String.valueOf(lat)).append(",").append(String.valueOf(lng))
    	.append("&radius=20000&sensor=false&key=AIzaSyA3neLk5bHTeQi1bD7WpfM0-EXUJKduVsQ");
    	return getJSONFromUrl(searchUrl.toString());
    }
    
    public static JSONObject getJSONFromUrl(String inputUrl) {
 
    	JSONObject jsonObj = null;
    	String jsonString = null;
    	InputStream is = null;
        // Making HTTP request
        try {
            // defaultHttpClient
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(inputUrl);
 
            HttpResponse httpResponse = httpClient.execute(httpGet);
            HttpEntity httpEntity = httpResponse.getEntity();
            is = httpEntity.getContent();           
 
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            jsonString = sb.toString();
        } catch (Exception e) {
            Log.e("Buffer Error", "Error converting result " + e.toString());
            jsonString = "{\"results\":[],\"status\":\"ZERO_RESULTS\"}";
        }
 
        // try parse the string to a JSON object
        try {
        	jsonObj = new JSONObject(jsonString);
        } catch (Exception e) {
            Log.e("JSON Parser", "Error parsing data " + e.toString());
        }
 
        // return JSON String
        return jsonObj;
 
    }
    
    public static boolean doPut(String inputUrl) {
    	 
        try {
            // defaultHttpClient
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(inputUrl);
 
            HttpResponse httpResponse = httpClient.execute(httpGet);
            return (httpResponse.getStatusLine().getStatusCode() == 200);
 
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
 
        return false;
 
    }
}