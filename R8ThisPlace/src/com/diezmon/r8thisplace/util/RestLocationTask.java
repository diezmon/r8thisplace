package com.diezmon.r8thisplace.util;

import org.json.JSONObject;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.diezmon.r8thisplace.MainActivity;
import com.diezmon.r8thisplace.R;
import com.google.android.maps.GeoPoint;

public class RestLocationTask extends AsyncTask<Void, Void, JSONObject> {

	 MainActivity mainActivity;
     
     ProgressDialog pd;

     public RestLocationTask(MainActivity mainActivity) {
         super();
         this.mainActivity = mainActivity;
         pd = new ProgressDialog(mainActivity);
         pd.setView(mainActivity.findViewById(R.layout.progressbar));
         pd.setMessage(R8Util.getResources().getString(R.string.doLocationWebSearch));
         pd.show();
     }
     
     @Override
     protected void onPostExecute(JSONObject result) {            
         pd.cancel();
     }

     @Override
     protected JSONObject doInBackground(Void... params) {
             	
         try {
         	JSONObject jObj = JSONParser.getJSONFromUrl("http://freegeoip.net/json/");
         	Log.d(MainActivity.TAG, "freegeoip:  " + jObj);
         	GeoPoint currentPoint = new GeoPoint(
                     (int) (jObj.getInt("latitude") * 1E6),
                     (int) (jObj.getInt("longitude") * 1E6));
         	
//         	mainActivity.centerLocation(currentPoint);
         	
         } catch (Exception e) {
         	Toast.makeText( mainActivity, R8Util.getResources().getText(R.string.noLocation), Toast.LENGTH_LONG ).show();
         }
         return null;
     }

}
