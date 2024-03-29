package com.diezmon.r8thisplace;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.diezmon.r8thisplace.model.PlaceDetail;
import com.diezmon.r8thisplace.util.GetRemoteImageTask;
import com.diezmon.r8thisplace.util.JSONParser;
import com.diezmon.r8thisplace.util.R8Util;

public class PlaceDetailActivity extends FragmentActivity implements PlaceActivity, AddRatingDialog.RatingDialogListener {

	LinearLayout ratingLayout = null;
	
	PlaceDetail placeDetail;
	
	TextView ratingsLabel;
	TextView overallRatingLabel;
	ImageView ratingOverall;
	
    /*
     * (non-Javadoc)
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.place_detail);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND,
                WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
        
        try {
        	
        	
            placeDetail = (PlaceDetail) getIntent().getExtras().getSerializable(PlaceDetail.PLACE_DETAIL_KEY);
            
            ImageView placeIconImageView = (ImageView) this.findViewById(R.id.placeIcon);
            
            new GetRemoteImageTask().execute(placeDetail.icon, placeIconImageView);
            
            TextView placeName = (TextView) this.findViewById(R.id.placeName);
            placeName.setText(placeDetail.name);
            
            TextView placeInfo = (TextView) this.findViewById(R.id.placeInfo);
            
            String webSite = (R8Util.isEmptyTrimmed(placeDetail.url))?"":placeDetail.url;
            
            if (!R8Util.isEmptyTrimmed(webSite))
            {
            	placeInfo.setText(placeDetail.formatted_address + "\n" + getResources().getText(R.string.openSite) );
            	
            	placeInfo.setOnClickListener(new OnClickListener() {
                    public void onClick(View v) {
                    	Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(placeDetail.url));
                    	startActivity(browserIntent);
                    }
                });
            	
            }
            else
            {
            	placeInfo.setText(placeDetail.formatted_address + "\n" );
            }
            
            TextView ratingBarLabel = (TextView) this.findViewById(R.id.ratingBarLabel);
            ratingBarLabel.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                   showRatingDialog(v);
                }
            });
            
            ImageView ratingBar = (ImageView) this.findViewById(R.id.ratingBarImg);
            
            ratingBar.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                   showRatingDialog(v);
                }
            });
            
            ratingsLabel = (TextView) this.findViewById(R.id.ratingsLabel);
            overallRatingLabel = (TextView) this.findViewById(R.id.overallRatingLabel);
            ratingOverall = (ImageView) this.findViewById(R.id.overallRatingImg);
            
            this.populateData(placeDetail.ratingInfoJson);
           
        }
        catch (Exception e)
        {
//        	Toast.makeText( this, getResources().getString(R.string.errorGeneral), Toast.LENGTH_LONG ).show();
        	Toast.makeText( this, e.getMessage(), Toast.LENGTH_LONG ).show();
//        	e.printStackTrace();
        	StringWriter sw = new StringWriter();
        	PrintWriter pw = new PrintWriter(sw);
        	e.printStackTrace(pw);
        	R8Util.sendEmail(this, "Error from r8thisplace " + this.getLocalClassName(), e.getMessage() + "\n" + sw.toString());
        	
        }
        
    }
    
    
    public void populateData(JSONObject jObj)
    {
    	
    	this.placeDetail.ratingInfoJson = jObj;

    	int overallRating = 0;
    	
    	try
    	{
    		overallRating = Integer.parseInt( String.valueOf(jObj.get("overallRating")) );
    	}
    	catch (JSONException je)
    	{
    		//je.printStackTrace();
    	}
    	
    	String theDate = "";
		try {
			theDate = jObj.getString("theDate");
			theDate = theDate.split(":")[0];
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
        ratingsLabel.setText(getResources().getString(R.string.ratings) + theDate);
        
        overallRatingLabel.setText(getResources().getText(R.string.overallRating) + String.valueOf(overallRating));
    	
    	if (overallRating == 0)
    	{
    		ratingOverall.setImageResource(R.drawable.ic_thumb_none);
    		overallRatingLabel.setText( getResources().getText(R.string.overallRating) + " " +  getResources().getText(R.string.none));
    	}
    	else if (overallRating < 3)
    	{
    		ratingOverall.setImageResource(R.drawable.ic_thumb_down);
    	}
    	else if (overallRating == 3)
    	{
    		ratingOverall.setImageResource(R.drawable.ic_thumb_average);
    	}
    	else if (overallRating > 3 )
    	{
    		ratingOverall.setImageResource(R.drawable.ic_thumb_up);
    	}	
    	R8Util.savePreferenceStr(ShowPlaceActivity.JSON_PLACE_RATINGS_KEY, jObj.toString());
    }
    
    private void showRatingDialog(View view)
    {
    	
    	try
    	{
    		DialogFragment dialog = new AddRatingDialog();
	        dialog.show(getSupportFragmentManager(), "AddRatingDialog");
    	}
    	catch (Exception e)
    	{
//    		Toast.makeText( this, getResources().getString(R.string.errorGeneral), Toast.LENGTH_LONG ).show();
    		Toast.makeText( this, e.getMessage(), Toast.LENGTH_LONG ).show();
    	}
    	
    }

	public void onDialogPositiveClick(AddRatingDialog dialog) {
		
		
		if (dialog.ratingBar.getRating() < 1 )
		{
			Toast.makeText( App.getContext(), 
					App.getContext().getResources().getString(R.string.pleaseSelectRating), Toast.LENGTH_LONG ).show();
		}
		else
		{
		
			String updateUrl = R8Util.addRatingUrl(placeDetail.latitude, placeDetail.longitude, (int)dialog.ratingBar.getRating(), dialog.comment.getText().toString(), 
					String.valueOf(dialog.ratingUser.getSelectedItem()));
			PlaceDetailsUpdateTask updateRatings = new PlaceDetailsUpdateTask(this);
			updateRatings.execute(updateUrl);
			
			R8Util.savePreferenceBool(ShowPlaceActivity.DATA_UPDATED_KEY, true);
							
		}
		
	}


	public void onDialogNegativeClick(AddRatingDialog dialog) {
		// TODO Auto-generated method stub
//		Toast.makeText( this, "resultCode: " + resultCode, Toast.LENGTH_LONG ).show();
	}
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
       getMenuInflater().inflate(R.menu.detail_menu, menu);
       String callNumber = getResources().getString(R.string.call);
       menu.findItem(R.id.callPlace).setTitle( String.format(callNumber, placeDetail.name));
       return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menuEmail:
              //String to = textTo.getText().toString();
  			  String subject = getResources().getString(R.string.emailSubject);
  			  String message = getResources().getString(R.string.emailMessage);
   
  			  Intent email = new Intent(Intent.ACTION_SEND);
	
  			  email.putExtra(Intent.EXTRA_SUBJECT, String.format(subject, this.placeDetail.name));
  			 
  			  email.putExtra(Intent.EXTRA_TEXT, String.format(message,  this.placeDetail.name, this.placeDetail.url));
   
  			  //need this to prompts email client only
  			  email.setType("message/rfc822");
    			
  			  startActivity(Intent.createChooser(email, getResources().getString(R.string.chooseEmailClient)));
  			  
                return true;
            case R.id.menuText:
            	Intent sms = new Intent(Intent.ACTION_VIEW);
            	
    			String txtBody = getResources().getString(R.string.txtMessage);
            	sms.setData(Uri.parse("sms:"));
            	sms.putExtra("sms_body", String.format(txtBody, this.placeDetail.name, this.placeDetail.url) ); 
            	startActivity(sms);
            	
            	return true;
            	
            case R.id.callPlace:
            	Intent phone = new Intent(Intent.ACTION_VIEW);
            	phone.setData(Uri.parse("tel:" + this.placeDetail.formatted_phone_number));
            	startActivity(phone);
            	return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    private class PlaceDetailsUpdateTask extends AsyncTask<String, Void, JSONObject> {

    	PlaceDetailActivity callerActivity;
    	
    	public PlaceDetailsUpdateTask(PlaceDetailActivity pda) {
    		this.callerActivity = pda;
    	}

    	@Override
    	protected JSONObject doInBackground(String... params) {
    			return JSONParser.getJSONFromUrl(params[0]);      			
    	}

		@Override
		protected void onPostExecute(JSONObject result) {
			// TODO Auto-generated method stub
//			super.onPostExecute(result);
			this.callerActivity.populateData(result);
		}

		@Override
		protected void onCancelled() {
			// TODO Auto-generated method stub
			super.onCancelled();
		}    	

    }
    
	

}
