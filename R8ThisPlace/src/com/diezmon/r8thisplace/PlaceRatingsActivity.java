package com.diezmon.r8thisplace;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.diezmon.r8thisplace.model.PlaceDetail;
import com.diezmon.r8thisplace.util.GetRemoteImageTask;
import com.diezmon.r8thisplace.util.JSONParser;
import com.diezmon.r8thisplace.util.R8Util;

public class PlaceRatingsActivity extends FragmentActivity implements PlaceActivity, AddRatingDialog.RatingDialogListener {

	LinearLayout ratingLayout = null;
	
	ScrollView ratingsScrollView;
	LinearLayout ratingsScrollerLayout;
	
	PlaceDetail placeDetail;
	
	
	@Override
	protected void onNewIntent(Intent intent)
	{
		super.onResume();
		boolean dirty = R8Util.getPreferenceBool(ShowPlaceActivity.DATA_UPDATED_KEY);
		if (dirty)
		{			
			try {
				String jsonDataString = R8Util.getPreferenceStr(ShowPlaceActivity.JSON_PLACE_RATINGS_KEY);
				if ( !R8Util.isEmptyTrimmed(jsonDataString) )
				{
					placeDetail.ratingInfoJson = new JSONObject(jsonDataString);
					this.populateData(placeDetail.ratingInfoJson);
				}
				
				R8Util.savePreferenceBool(ShowPlaceActivity.DATA_UPDATED_KEY, false);	
				R8Util.savePreferenceStr(ShowPlaceActivity.JSON_PLACE_RATINGS_KEY, null);
				
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Toast.makeText( this, "Update failed!" , Toast.LENGTH_LONG ).show();
			}
		}
		
		//Toast.makeText( this, "Rating window onNewIntent " + dirty , Toast.LENGTH_LONG ).show();
	}
	
    /*
     * (non-Javadoc)
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.place_ratings);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND,
                WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
        
        try {
        	
        	      	
            placeDetail = (PlaceDetail) getIntent().getExtras().getSerializable(PlaceDetail.PLACE_DETAIL_KEY);

            ImageView placeIconImageView = (ImageView) this.findViewById(R.id.placeIconRatings);
            
            new GetRemoteImageTask().execute(placeDetail.icon, placeIconImageView);
            
            TextView placeName = (TextView) this.findViewById(R.id.placeNameRatings);
            placeName.setText(placeDetail.name);
            
            ratingsScrollView = (ScrollView) this.findViewById(R.id.ratingsScrollView);
            this.ratingsScrollerLayout = (LinearLayout) this.findViewById(R.id.ratingsScrollerLayout);
            this.ratingsScrollerLayout.setOrientation(LinearLayout.VERTICAL);
            
            
            this.populateData(placeDetail.ratingInfoJson);
           
        }
        catch (Exception e)
        {
        	e.printStackTrace();
        	
        }
        
    }
    
    public void populateData(JSONObject jObj) throws JSONException
    {

    	this.ratingsScrollerLayout.removeAllViews();
    	
    	if (!jObj.has("ratings") || jObj.getJSONArray("ratings").length() == 0)
    	{
    		LinearLayout ratingHolderLayout = new LinearLayout(this.ratingsScrollView.getContext() );
			ratingHolderLayout.setOrientation(LinearLayout.VERTICAL);
			ratingHolderLayout.setGravity(Gravity.CENTER_HORIZONTAL);
			TextView txtView = new TextView(ratingHolderLayout.getContext());
			txtView.setText(getResources().getText(R.string.noratings));
			txtView.setPadding(2, 2, 0, 0);
			ratingHolderLayout.addView(txtView);
			this.ratingsScrollerLayout.addView(ratingHolderLayout);		
    		return;
    	}
    	   	
    	JSONArray jArray = jObj.getJSONArray("ratings");
    	
    	for(int i=0;i<jArray.length();i++)
		{
			JSONObject currentItem = jArray.getJSONObject(i);
			JSONObject properties = currentItem.getJSONObject("properties");
			String comment = properties.getString("comment");
			String user = properties.getString("user");
			int rating = properties.getInt("rating");
			String theDate = properties.getString("date");
			
			LinearLayout ratingHolderLayout = new LinearLayout(this.ratingsScrollView.getContext() );
			ratingHolderLayout.setOrientation(LinearLayout.VERTICAL);
			ratingHolderLayout.setGravity(Gravity.LEFT);
			
			LinearLayout imageHolderlayout = new LinearLayout(this.ratingsScrollView.getContext() );
			imageHolderlayout.setGravity(Gravity.LEFT);
			imageHolderlayout.addView(getImageViewForRating(rating));
			
			LinearLayout userHolderLayout = new LinearLayout(this.ratingsScrollView.getContext() );
			userHolderLayout.setGravity(Gravity.LEFT);
			
			TextView userTxtView = new TextView(userHolderLayout.getContext());
			userTxtView.setTypeface(Typeface.DEFAULT_BOLD);
			userTxtView.setText(user);
			userTxtView.setPadding(2, 2, 0, 0);
			userHolderLayout.addView(userTxtView);
			
			LinearLayout commentHolderLayout = new LinearLayout(this.ratingsScrollView.getContext() );
			commentHolderLayout.setGravity(Gravity.LEFT);
			
			TextView txtView = new TextView(commentHolderLayout.getContext());
			txtView.setText(comment);
			txtView.setPadding(4, 2, 0, 0);
			commentHolderLayout.addView(txtView);
			
			ratingHolderLayout.addView(imageHolderlayout);
			ratingHolderLayout.addView(userHolderLayout);
			ratingHolderLayout.addView(commentHolderLayout);
						
			this.ratingsScrollerLayout.addView(ratingHolderLayout);		
			
		}
    	
    	
    	
    }
    
    private ImageView getImageViewForRating(int rating)
    {
    	ImageView iv = new ImageView(ratingsScrollerLayout.getContext());
    	iv.setPadding(0, 10, 0, 2);
    	iv.setAdjustViewBounds(true);
    	iv.setMaxHeight(60);
    	
    	switch (rating)
		{
		case 1:
			iv.setImageResource(R.drawable.ic_1_thumb);
			break;
		case 2:
			iv.setImageResource(R.drawable.ic_2_thumbs);
			break;
		case 3:
			iv.setImageResource(R.drawable.ic_3_thumbs);
			break;
		case 4:
			iv.setImageResource(R.drawable.ic_4_thumbs);
			break;
		case 5:
			iv.setImageResource(R.drawable.ic_5_thumbs);
			break;
		default:
			iv.setImageResource(R.drawable.ic_1_thumb);
			break;
		}
    	
    	return iv;
    	
    }

	public void onDialogPositiveClick(AddRatingDialog dialog) {
		// TODO Auto-generated method stub
		Toast.makeText( this, "OK CLICK IN PlaceRatingActivity", Toast.LENGTH_LONG ).show();
	}

	public void onDialogNegativeClick(AddRatingDialog dialog) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onPostResume() {
		// TODO Auto-generated method stub
		super.onPostResume();
//		String ratingUrl;
//		try {
//			ratingUrl = JSONParser.getR8ItDetailsUrl(placeDetail.latitude, placeDetail.longitude);
//			new GetRemoteImageTask().execute(ratingUrl, this);
//		} catch (JSONException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
        
	}
	
	

}
