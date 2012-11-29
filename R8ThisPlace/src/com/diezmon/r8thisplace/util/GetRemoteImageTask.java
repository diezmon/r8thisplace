package com.diezmon.r8thisplace.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

/**
 * method to populate an ImageView object from a url. 
 * 
 * Takes a set of objects as params.  With the expected following
 * 
 * param1 String: image url
 * param2 ImageView: the ImageView to put the remote image into 
 * 
 * @author g041338
 *
 */
public class GetRemoteImageTask extends AsyncTask<Object, Void, ImageView> {

	//Activity mainActivity;
	Bitmap bitmap;

	@Override
	protected ImageView doInBackground(Object... params) 
	{
		String imageUrl = (String) params[0];
		ImageView imageView = (ImageView)params[1];
		
		try {
  			bitmap = BitmapFactory.decodeStream((InputStream) new URL(
  					imageUrl).getContent());
  			
  		} catch (MalformedURLException e) {
  			e.printStackTrace();
  		} catch (IOException e) {
  			e.printStackTrace();
  		}
		return imageView;
	}
     
	@Override
	protected void onPostExecute(ImageView result) {
		result.setImageBitmap(bitmap);
		result.refreshDrawableState();
    }

}
