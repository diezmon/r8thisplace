package com.diezmon.r8thisplace;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.diezmon.r8thisplace.util.R8Util;

public class AddRatingDialog extends DialogFragment {

	EditText comment;
	RatingBar ratingBar;
	Spinner ratingUser;
	
	RatingDialogListener mListener;
	
	
	public interface RatingDialogListener {
        public void onDialogPositiveClick(AddRatingDialog dialog);
        public void onDialogNegativeClick(AddRatingDialog dialog);
    }
	
	// Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (RatingDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement RatingDialogListener");
        }
    }
	
	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View dialogView = inflater.inflate(R.layout.activity_rate_it, null);
        
        //setContentView(R.layout.activity_rate_it);
        builder.setView(dialogView);
        builder.setTitle(getResources().getString(R.string.title_activity_rate_it));
        
        ratingUser = (Spinner) dialogView.findViewById(R.id.ratingUser);
        
        List<String> users = R8Util.getUserNames();
        
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(R8Util.getContext(),
        		android.R.layout.simple_spinner_item, users);
        
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ratingUser.setSelection(0, true);
        ratingUser.setAdapter(dataAdapter);
           
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            	if (ratingBar.getRating() > 0 )
            	{
            		Toast.makeText( App.getContext(), 
    					App.getContext().getResources().getString(R.string.addingRating), Toast.LENGTH_LONG).show();
            		mListener.onDialogPositiveClick(AddRatingDialog.this);
            	}
            }
        });
        
        
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            	mListener.onDialogNegativeClick(AddRatingDialog.this);
//            	AddRatingDialog.this.getDialog().cancel();
            }
        });
        
       
        ratingBar = (RatingBar) dialogView.findViewById(R.id.ratingBar);
        comment = (EditText) dialogView.findViewById(R.id.commentText);
        
        	
    	Dialog d = builder.create();
    	
    	return d;
    	
	}
	
	
//	@Override
//    public void onActivityCreated(Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//
//        Button b = (Button)this.getView().findViewById(R.id.okButton);
//
//        b.setClickable(false);
//	}
	
	

}
