package com.diezmon.r8thisplace;

import org.json.JSONObject;

import android.app.Activity;
import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

import com.diezmon.r8thisplace.model.PlaceDetail;
import com.diezmon.r8thisplace.util.JSONParser;

public class ShowPlaceActivity extends TabActivity  {

	public static final String DATA_UPDATED_KEY = "dataUpdatedKey";
	public static final String JSON_PLACE_RATINGS_KEY = "jsonPlaceRatingsKey";
	double latitude;
	double longitude;
	String googleReference;
	  
    /*
     * (non-Javadoc)
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.place_detail_dialog);
        
        
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND,
                WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
        
        PlaceDetail placeDetail = null;
        JSONObject ratingJson = null;
        
        // title
        try {
            String s = getIntent().getExtras().getString("title");
            if (s != null && s.length() > 0) {
                this.setTitle(s);
            }
            latitude = getIntent().getExtras().getDouble("latitude");
      	  	longitude = getIntent().getExtras().getDouble("longitude");
      	  	googleReference = getIntent().getExtras().getString("reference");
      	  	
      	  	JSONObject googleInfo = JSONParser.getPlaceDetails(googleReference);
      	  	
      	  	placeDetail = new PlaceDetail(googleInfo);
      	  	
      	  	placeDetail.ratingInfoJson = JSONParser.getR8ItDetails(latitude, longitude);
      	  	
      	  	this.setTitle(placeDetail.name);
      	  	
        } 
        catch (Exception e) 
        {
        	e.printStackTrace();
        }
        
        TabHost tabHost = getTabHost();
        
        TabSpec detailsTab = tabHost.newTabSpec("Info");
        // setting Title and Icon for the Tab
        detailsTab.setIndicator(getResources().getString(R.string.general));
        Intent placeDetIntent = new Intent(this, PlaceDetailActivity.class);
        placeDetIntent.putExtra(PlaceDetail.PLACE_DETAIL_KEY, placeDetail);
        
        detailsTab.setContent(placeDetIntent);
        
        TabSpec ratingsTab = tabHost.newTabSpec("Ratings");
        // setting Title and Icon for the Tab
        ratingsTab.setIndicator(getResources().getString(R.string.ratingDetails));
        Intent showRatingsIntent = new Intent(this, PlaceRatingsActivity.class);
        showRatingsIntent.putExtra(PlaceDetail.PLACE_DETAIL_KEY, placeDetail);   
        ratingsTab.setContent(showRatingsIntent);

        tabHost.addTab(detailsTab);
        tabHost.addTab(ratingsTab);
        
    }

    /* (non-Javadoc)
     * @see android.app.Activity#onBackPressed()
     */
    @Override
    public void onBackPressed() {
        executeDone();
        super.onBackPressed();
    }

    /**
     *
     */
    private void executeDone() {
        Intent resultIntent = new Intent();
        //resultIntent.putExtra("searchValue", ShowPlaceActivity.this.et.getText().toString());
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }

}
