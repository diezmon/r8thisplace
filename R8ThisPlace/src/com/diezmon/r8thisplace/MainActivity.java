package com.diezmon.r8thisplace;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.diezmon.r8thisplace.util.JSONParser;
import com.diezmon.r8thisplace.util.LastLocationFinder;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class MainActivity extends MapActivity  
{

	 String searchValue = "";
	 
	 private int gpsWarningCount = 0;
	    
	 public static final String TAG = "R8ThisPlace";
	 private LocationManager myLocationManager;
	 
	 private MapView myMapView;
	 
	 private MyLocationOverlay mMyLocationOverlay;
	 
	 private MapController myMapController;
	 
	 private LastLocationFinder lastLocationFinder;
	 
	 public static ProgressDialog pd;
	 
	 boolean appStartedWithLink;
	 R8MapOverlay overlayToClick = null;
	 double paramLat = 0;
	 double paramLng = 0;
	 String paramKey;

     
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Intent thisIntent = this.getIntent();
        //we're loaded from a url
        if (Intent.ACTION_VIEW.equals(thisIntent.getAction())) 
        {
        	appStartedWithLink = true;
              // may be some test here with your custom uri
        }
        else
        {
        	appStartedWithLink = false;
        }

        setContentView(R.layout.activity_main);
        ImageButton button = (ImageButton) findViewById(R.id.buttonPrompt);
        myMapView = (MapView) findViewById(R.id.mapview);
        
        showLocation();

    }
    
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        
        switch (requestCode)
        {
        case 99:
            if (resultCode == RESULT_OK || resultCode == RESULT_CANCELED) {
                // back from settings/gps screen
//                initLocation();
            }
        case 66:
            if (resultCode == RESULT_OK) {
                // back from search input screen
                String value = data.getStringExtra("searchValue").trim();
                if (value != null && value.length() > 0) {
                    this.searchValue = value;
                    processSearchBackground(searchValue);
                    
                }
                
            }
        }
        
        
    }
    
    private void showLocation()
    {
    	if (mMyLocationOverlay == null)
    	{
    		mMyLocationOverlay = new MyLocationOverlay(this, myMapView);
    	}
    	mMyLocationOverlay.runOnFirstFix(new Runnable() { public void run() {
        	myMapView.getController().setZoom(12);
        	myMapView.getController().animateTo(mMyLocationOverlay.getMyLocation());
        }});
         
        myMapView.getOverlays().add(mMyLocationOverlay);

        myMapView.setBuiltInZoomControls(true);

    }
    
    private class SearchTask extends AsyncTask<Double,Double,JSONObject>{
        String searchString;
        double latitude;
        double longitude; 
        MainActivity obj;
        boolean hasResults = true;
        
        ProgressDialog pd;

        public SearchTask(String searchString, double latitude,
                double longitude, MainActivity obj) {
            super();
            this.searchString = searchString;
            this.latitude = latitude;
            this.longitude = longitude;
            this.obj = obj;
            pd = new ProgressDialog(obj);
            pd.setView(obj.findViewById(R.layout.progressbar));
            String callNumber = getResources().getString(R.string.searching);             
            pd.setMessage(String.format(callNumber, searchString));
            pd.show();
        }
        
        @Override
        protected void onPostExecute(JSONObject result) {            
            pd.cancel();
            if (!hasResults)
            {
                Toast.makeText( obj.getBaseContext(), obj.getResources().getString(R.string.noresults), Toast.LENGTH_LONG ).show();
            }
            if (obj.appStartedWithLink)
            {
            	try
            	{
            		obj.overlayToClick.onTap(0);
            	}
            	catch (Exception e)
            	{
            		
            	}
            }
        }

        @Override
        protected JSONObject doInBackground(Double... params) {
            
            JSONObject jObj = JSONParser.getSearchResults(searchString, latitude , longitude);
            try {
                obj.showSearchOverlays(jObj);                
            } catch (Exception e) {
                hasResults = false;
                e.printStackTrace();
            }
            return null;
        }
        
    }
    
    protected void processSearchBackground(String searchString)
    {
                
        GeoPoint currentCenterPoint = myMapView.getMapCenter();
        
        double latitude = currentCenterPoint.getLatitudeE6() / 1E6;
        double longitude = currentCenterPoint.getLongitudeE6() / 1E6;
        
        SearchTask st = new SearchTask(searchString, latitude, longitude, this);
        st.execute((Double[])null);
          
    }
    
    
    /*
     {"id":"6b62f059ed16202142ea676cafedc7728bc64ed1",
"icon":"http:\/\/maps.gstatic.com\/mapfiles\/place_api\/icons\/restaurant-71.png",
"vicinity":"6030 50th St N, Oakdale",
"name":"Randys Premier Pizza",
"rating":4.2,
"types":["restaurant","food","establishment"],
"reference":"CoQBcwAAAG72jWPgGdN_WgNZc-Pvdvhy5aQ4DZBEUCnxUhrHMZJNkEmiZX389imU9os20c9srlA_agb_DLPzYsA2M79EBAWM8VaD_8z51q9S4rfWj9y8w-0zm1oPWAUChtvAEf_Us9HYgua7EwvODpTvx2I1bETHrYbaZHVNvb-vi_sIW5qrEhCYTE2YHb2K_5jMcJXKKqD2GhSsJhVpHcLnbqXtVWrzAJL2Vm5Gvg",
"opening_hours":{"open_now":true},
"geometry":{"location":{"lng":-92.984334,"lat":45.021493}} 
     */
    private void showSearchOverlays(JSONObject jObj) throws Exception
    {
        //{"results":[],"html_attributions":[],"status":"ZERO_RESULTS"}
        JSONArray jArray = jObj.getJSONArray("results");
        List<Overlay> mapOverlays = myMapView.getOverlays();
        mapOverlays.clear();
        
        if (jArray == null || jArray.length() < 1 || jObj.getString("status").equalsIgnoreCase("ZERO_RESULTS"))
        {
            Toast.makeText( this, getResources().getString(R.string.noresults), Toast.LENGTH_LONG ).show();
            return;
        }
        
        Drawable drawable = this.getResources().getDrawable(R.drawable.ic_pin);
        
        
        for(int i=0;i<jArray.length();i++)
        {
            JSONObject currentItem = jArray.getJSONObject(i);
            String imageUrl = currentItem.getString("icon");
            String googleRef = (String) currentItem.get("reference");
            R8MapOverlay itemizedoverlay = new R8MapOverlay(drawable, MainActivity.this, googleRef, imageUrl);
            JSONObject location = currentItem.getJSONObject("geometry").getJSONObject("location");
            double latitude = location.getDouble("lat");
            double longitude = location.getDouble("lng");
            
            if (latitude == this.paramLat && longitude == this.paramLng)
            {
            	overlayToClick = itemizedoverlay;
            }
            
            GeoPoint currentGeo = new GeoPoint(
                    (int) (latitude * 1000000),
                    (int) (longitude * 1000000));
            
            OverlayItem overlayitem = new OverlayItem(currentGeo, "Center!", "I'm right here!");
            itemizedoverlay.addOverlay(overlayitem);
            mapOverlays.add(itemizedoverlay);
        }
        
        myMapView.getController().setCenter(mMyLocationOverlay.getMyLocation());
          
     }
      
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    @Override
    protected boolean isRouteDisplayed() {
        return false;
    }
    
    @Override 
    public void onResume() {
        Log.d(TAG, "onResume " );
        super.onResume();
        mMyLocationOverlay.enableMyLocation();
        this.showLocation();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMyLocationOverlay.disableMyLocation();
    }

    
    /** Called when the user touches the button */
    public void showSearchDialog(View view) {
        // Do something in response to button click
        Intent searchIntent = new Intent(this, TextEntryActivity.class);
        searchIntent.putExtra("value", this.searchValue);
        this.startActivityForResult(searchIntent, 66);
    }
    
    @Override
    public void onBackPressed() 
    {
    	this.finish();
    }
    
}
