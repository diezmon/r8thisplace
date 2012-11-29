package com.diezmon.r8thisplace;

import java.util.ArrayList;

import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.widget.Toast;

import com.diezmon.r8thisplace.util.R8Util;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

public class R8MapOverlay extends ItemizedOverlay {
	
	private OverlayItem mOverlays = null;//OverlayItem();
	MainActivity mContext;
	String googleReference;
	String iconUrl;
	boolean allowTap = true;


	public void addOverlay(OverlayItem overlay) {
	    mOverlays = overlay;
	    populate();
	}
	
	public R8MapOverlay(Drawable defaultMarker, MainActivity context, boolean allowTapping) {
		  super(boundCenterBottom(defaultMarker));
		  this.mContext = context;
		  this.allowTap = allowTapping;
	}
	
	public R8MapOverlay(Drawable defaultMarker, MainActivity context, String googleRef, String icon) {
		  super(boundCenterBottom(defaultMarker));
		  this.mContext = context;
		  this.googleReference = googleRef;
		  this.iconUrl = icon;
	}
	
	private class ShowPlaceTask extends AsyncTask<Void,Void,Void>{
		
		String reference;
	    double latitude;
		double longitude;
		MainActivity actContext;
	    
	    ProgressDialog pd;

	    public ShowPlaceTask(double latitude,
				double longitude, String googleReference, MainActivity obj) {
	    	
			super();
			this.latitude = latitude;
			this.longitude = longitude;
			this.reference = googleReference;
			this.actContext = obj;
		}

		@Override
		protected Void doInBackground(Void... params) {
			Intent infoIntent = new Intent(this.actContext, ShowPlaceActivity.class);
			infoIntent.putExtra("latitude", latitude);
			infoIntent.putExtra("longitude", longitude);
			infoIntent.putExtra("reference", googleReference);		  
			actContext.startActivity(infoIntent);
			return null;
		}
		
		
		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
//			pd.cancel();
		}		
		
	}
	
	@Override
	protected boolean onTap(int index) {
		  
	  if (allowTap)
	  {
//		  Toast.makeText( R8Util.getContext(), R8Util.getResources().getText(R.string.loadingPlace), Toast.LENGTH_LONG ).show();
		  OverlayItem item = mOverlays;//.get(index);
		  double latitude = item.getPoint().getLatitudeE6() / 1E6;
		  double longitude = item.getPoint().getLongitudeE6() / 1E6;
		  
//		  Intent infoIntent = new Intent(this.mContext, ShowPlaceActivity.class);
//		  infoIntent.putExtra("latitude", latitude);
//		  infoIntent.putExtra("longitude", longitude);
//		  infoIntent.putExtra("reference", googleReference);
//		  
//		  mContext.startActivity(infoIntent);
		  
		  ShowPlaceTask spt = new ShowPlaceTask(latitude, longitude, googleReference, mContext);
		  spt.execute((Void[])null);
		  
		 		
	  }
	  return true;
	}
	
	@Override
	protected OverlayItem createItem(int arg0) {
		// TODO Auto-generated method stub
		return mOverlays;//.get(arg0);
	}

	@Override
	public int size() {
		return 1;//mOverlays.size();
	}
}
