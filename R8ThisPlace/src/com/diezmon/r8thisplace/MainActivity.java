package com.diezmon.r8thisplace;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.diezmon.r8thisplace.util.JSONParser;
import com.diezmon.r8thisplace.util.LastLocationFinder;
import com.diezmon.r8thisplace.util.RestLocationTask;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class MainActivity extends MapActivity implements LocationListener {

    String searchValue = "";
    
    private static final String TAG = "R8ThisPlace";
     private LocationManager myLocationManager;
     
     private MapView myMapView;
     
     private MapController myMapController;
     
     private LastLocationFinder lastLocationFinder;
     
     public static ProgressDialog pd;
     
     boolean appStartedWithLink;
     R8MapOverlay overlayToClick = null;
     double paramLat = 0;
     double paramLng = 0;
     String paramKey;

     public void centerLocation(GeoPoint centerGeoPoint)
     {
                 
         if (appStartedWithLink)
         {
        	 Uri appStartUri = this.getIntent().getData();
        	 paramLat = Double.parseDouble(appStartUri.getQueryParameter("lat")); // "str" is set
        	 paramLng = Double.parseDouble(appStartUri.getQueryParameter("lng")); // "string" is set
        	 searchValue = appStartUri.getQueryParameter("key"); 
             
             centerGeoPoint = new GeoPoint(
                     (int) (paramLat * 1E6),
                     (int) (paramLng * 1E6));             
         }
         
         myMapController.animateTo(centerGeoPoint);  
         
         List<Overlay> mapOverlays = myMapView.getOverlays();
         Drawable drawable = this.getResources().getDrawable(R.drawable.ic_marker);
         R8MapOverlay itemizedoverlay = new R8MapOverlay(drawable, this, appStartedWithLink);
          
         OverlayItem overlayitem = new OverlayItem(centerGeoPoint, "Center!", "I'm right here!");
         itemizedoverlay.addOverlay(overlayitem);
          
         mapOverlays.add(itemizedoverlay);
         
         if (appStartedWithLink)
         {
        	 processSearchBackground(searchValue);
        	 
         }
          
      
     }
     
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

        myMapController = myMapView.getController();
        myMapController.setZoom(12); // Fixed Zoom Level
        myMapView.setBuiltInZoomControls(true);

        myLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        
        lastLocationFinder = new LastLocationFinder(this);
        lastLocationFinder.setChangedLocationListener(this);
        
        initLocation();

    }
    
    protected void onActivityResult(int requestCode, int resultCode,
            Intent data) {
        
        switch (requestCode)
        {
        case 99:
            if (resultCode == RESULT_OK || resultCode == RESULT_CANCELED) {
                // back from settings/gps screen
                initLocation();
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
    
    private void initLocation()
    {
        
        if(!myLocationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER ))
        {
            Log.d(TAG, "GPS OFF ");
            Toast.makeText( this, getResources().getString(R.string.turnOnGps), Toast.LENGTH_SHORT ).show();
            Intent enableGPSIntent = new Intent( Settings.ACTION_SECURITY_SETTINGS );
            startActivityForResult(enableGPSIntent, 99);
           
        }
        else
        {
            Log.d(TAG, "GPS ON ");
            Location lastLocation = myLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (lastLocation == null)
            {
                lastLocation = myLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);    
            }
            if (lastLocation == null)
            {
                this.initLocaleRestFul();    
            }
            else
            {
                Log.d(TAG, "lastLocation " + lastLocation);
//                Toast.makeText( this, lastLocation.getLatitude()*1E6 + "," + lastLocation.getLongitude()*1E6, Toast.LENGTH_LONG ).show();
                GeoPoint myGeoPoint = new GeoPoint(
                        (int) (lastLocation.getLatitude() * 1E6),
                        (int) (lastLocation.getLongitude() * 1E6));
                
                
                Log.d(TAG, "geoPoint " + myGeoPoint);
                centerLocation(myGeoPoint);
            }
            
        }    
        
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
        
        this.myMapView.postInvalidate();
          
     }
    
    private void initLocaleRestFul()
    {
   	
    	RestLocationTask st = new RestLocationTask(this);
        st.execute((Void[])null);
            
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
        myLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 1, this);
        super.onResume();
    }

    @Override
    public void onPause() {
        myLocationManager.removeUpdates(this);
        super.onPause();
    }

    public void onLocationChanged(Location argLocation) {
        // TODO Auto-generated method stub
        Log.d(TAG, "argLocation " + argLocation);
        
        GeoPoint myGeoPoint = new GeoPoint(
                (int) (argLocation.getLatitude() * 1000000),
                (int) (argLocation.getLongitude() * 1000000));

        centerLocation(myGeoPoint);
    }

    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub
        
    }

    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub
        
    }

    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub
        
    }
    
    /** Called when the user touches the button */
    public void showSearchDialog(View view) {
        // Do something in response to button click
        Intent searchIntent = new Intent(this, TextEntryActivity.class);
        searchIntent.putExtra("value", this.searchValue);
        this.startActivityForResult(searchIntent, 66);
    }
    
}
