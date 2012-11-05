package com.diezmon.r8thisplace.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import org.json.JSONObject;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import com.diezmon.r8thisplace.App;
import com.diezmon.r8thisplace.R;

public class R8Util {
	
	
	public static Resources getResources()
	{
		return  App.getContext().getResources();
	}
	
	public static Context getContext()
	{
		return  App.getContext();
	}
	
	public static JSONObject addRating(double latitude, double longitude, int rating, String comments, String userName)
	{		
		
		String dateString = String.valueOf(Calendar.getInstance().getTimeInMillis());
    	String timezoneId = TimeZone.getDefault().getID();
    	
		return addRating(latitude, longitude, rating, comments, userName, dateString, timezoneId);
		
	}
	
	public static JSONObject addRating(double latitude, double longitude, int rating, String comments, String userName, String date, String timezoneId)
	{		
		
		StringBuffer url = 
    			new StringBuffer(getResources().getString(R.string.r8PutUrl))
    	.append(String.valueOf(latitude)).append("/")
    	.append(String.valueOf(longitude)).append("/")
    	.append("?rating=")
    	.append(String.valueOf(rating))
    	.append("&comment=").append(URLEncoder.encode(comments))
    	.append("&user=").append(userName)
		.append("&dt=").append(date)
		.append("&tz=").append(timezoneId);
    	
		return JSONParser.getJSONFromUrl(url.toString());
		//return JSONParser.doPut(url.toString());
		
	}

	public static ImageView getImageViewFromUrl(String imageUrl, Activity activity, int viewId) {

		try {
			ImageView i = (ImageView) activity.findViewById(viewId);
			Bitmap bitmap = BitmapFactory.decodeStream((InputStream) new URL(
					imageUrl).getContent());
			i.setImageBitmap(bitmap);
			return i;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;

	}
	
	public static void savePreferenceBool(String key, Boolean value)
	{
		SharedPreferences sharedPreferences = App.getContext().getSharedPreferences("APP", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPreferences.edit();
	    editor.putBoolean(key, value);
	    editor.commit();
		
	}

	public static boolean getPreferenceBool(String key)
	{
		SharedPreferences sharedPreferences = App.getContext().getSharedPreferences("APP", Context.MODE_PRIVATE);
		return sharedPreferences.getBoolean(key, Boolean.FALSE);
		
	}
	
	public static void savePreferenceStr(String key, String value)
	{
		SharedPreferences sharedPreferences = App.getContext().getSharedPreferences("APP", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPreferences.edit();
	    editor.putString(key, value);
	    editor.commit();
		
	}

	public static String getPreferenceStr(String key)
	{
		SharedPreferences sharedPreferences = App.getContext().getSharedPreferences("APP", Context.MODE_PRIVATE);
		return sharedPreferences.getString(key, null);
		
	}
	
	public static boolean isNull(String str)
	{
		return str == null;
	}
	
	public static boolean isEmpty(String str)
	{
		return ( isNull(str) || str.length() == 0);
	}
	
	public static boolean isEmptyTrimmed(String str)
	{
		return ( isNull(str) || str.trim().length() == 0);
		
	}
	
	public static List getUserNames()
	{
		AccountManager am = AccountManager.get(getContext());
		
		List<String> usernames = new ArrayList<String>();
		usernames.add(getResources().getString(R.string.anonymous));
		
		for (Account acct: am.getAccountsByType("com.google"))
		{
			usernames.add(nameFromEmail(acct.name));
		}
		return usernames;
		
	}
	
	public static String nameFromEmail(String email)
	{
		
		if (isEmptyTrimmed(email) || !email.contains("@"))
		{
			return email;
		}
		
		int atIndex = email.indexOf("@");
		
		return email.substring(0, atIndex);
		
		
	}
}
