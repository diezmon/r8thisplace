/**
 * 
 */
package com.diezmon.r8thisplace.util;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;

import com.diezmon.r8thisplace.PlaceActivity;

/**
 * @author g041338
 *
 */
public class GetPlaceRatingsTask extends AsyncTask<Object, Void, JSONObject> {
	
	private PlaceActivity callerActivity;
	@Override
	protected JSONObject doInBackground(Object... params) {
		// TODO Auto-generated method stub
		
		this.callerActivity = (PlaceActivity) params[1];
		JSONObject ratingResult =  JSONParser.getJSONFromUrl((String)params[0]);
		return ratingResult;
		
	}
	
	@Override
	protected void onPostExecute(JSONObject result) {
		// TODO Auto-generated method stub
//		super.onPostExecute(result);
		try {
			this.callerActivity.populateData(result);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
