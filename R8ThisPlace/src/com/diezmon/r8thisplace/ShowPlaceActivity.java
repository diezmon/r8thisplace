package com.diezmon.r8thisplace;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.Intent;
import android.os.AsyncTask;
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
	ProgressDialog pd;
	
	public static final String GOOGLE_RESULTS_KEY = "googleResults";
	public static final String RATING_RESULTS_KEY = "ratingResults";
	  
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
        
        try {
            String s = getIntent().getExtras().getString("title");
            if (s != null && s.length() > 0) {
                this.setTitle(s);
            }
            latitude = getIntent().getExtras().getDouble("latitude");
      	  	longitude = getIntent().getExtras().getDouble("longitude");
      	  	googleReference = getIntent().getExtras().getString("reference");
      	  	
      	  	String dataUrl = JSONParser.getPlaceDetailsUrl(googleReference);
      	  	String ratingUrl = JSONParser.getR8ItDetailsUrl(latitude, longitude);
      	  	PlaceDetailsTask detailsTask = new PlaceDetailsTask(this);
      	  	detailsTask.execute(dataUrl, ratingUrl);
      	  	
        } 
        catch (Exception e) 
        {
        	e.printStackTrace();
        }
        
    }
    
    public void onGetResults(Map<String, JSONObject> placeInformationMap)
    {
    
    	PlaceDetail placeDetail = null;
		
    	try {
			placeDetail = new PlaceDetail(placeInformationMap.get(ShowPlaceActivity.GOOGLE_RESULTS_KEY));

			placeDetail.ratingInfoJson = placeInformationMap.get(ShowPlaceActivity.RATING_RESULTS_KEY);

			this.setTitle(placeDetail.name);

		} catch (Exception e) {
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
		ratingsTab.setIndicator(getResources()
				.getString(R.string.ratingDetails));
		Intent showRatingsIntent = new Intent(this, PlaceRatingsActivity.class);
		showRatingsIntent.putExtra(PlaceDetail.PLACE_DETAIL_KEY, placeDetail);
		ratingsTab.setContent(showRatingsIntent);

		tabHost.addTab(detailsTab);
		tabHost.addTab(ratingsTab);
		if (pd != null) this.pd.hide();
		
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
    
    private class PlaceDetailsTask extends AsyncTask<String, Void, Map<String, JSONObject>> {

    	ShowPlaceActivity callerActivity;
    	
    	public PlaceDetailsTask(ShowPlaceActivity spa) {
    		this.callerActivity = spa;
    		pd = new ProgressDialog( ShowPlaceActivity.this); 
    		pd.setView(ShowPlaceActivity.this.findViewById(R.layout.progressbar));
			pd.setMessage(ShowPlaceActivity.this.getResources().getText(R.string.loadingPlace));
			pd.show();
    	}

    	@Override
    	protected Map doInBackground(String... params) {		
			
    		// TODO Auto-generated method stub	 
			JSONObject googleResult =  JSONParser.getJSONFromUrl(params[0]);  
			JSONObject ratingResult =  JSONParser.getJSONFromUrl(params[1]);
			Map<String, JSONObject> resultsMap = new HashMap<String, JSONObject>();
			
			resultsMap.put(ShowPlaceActivity.GOOGLE_RESULTS_KEY, googleResult);
			resultsMap.put(ShowPlaceActivity.RATING_RESULTS_KEY, ratingResult);
	
    		return resultsMap;
    	}

		@Override
		protected void onPostExecute(Map<String, JSONObject> result) {
			// TODO Auto-generated method stub
//			super.onPostExecute(result);
			this.callerActivity.onGetResults(result);
		}

		@Override
		protected void onCancelled() {
			// TODO Auto-generated method stub
			super.onCancelled();
			if (pd != null)
			{
				pd.cancel();
			}
		}
    	
    	
    	

    }


}
