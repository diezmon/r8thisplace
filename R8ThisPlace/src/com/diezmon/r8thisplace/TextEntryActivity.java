package com.diezmon.r8thisplace;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

public class TextEntryActivity extends Activity {
    private EditText et;

    /*
     * (non-Javadoc)
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.dialog_search);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND,
                WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
        // title
        try {
            String value = getIntent().getExtras().getString("value");
            if (value.length() > 0) {
                //this.setTitle(s);
            }
            et = ((EditText) findViewById(R.id.searchText));
            et.setText(value);
        } catch (Exception e) {
        }
        // value

        try {
           
        } catch (Exception e) {
        }
        // button
        ((Button) findViewById(R.id.okButton)).setOnClickListener(new OnClickListener() {
           
            public void onClick(View v) {
                executeDone();
            }
        });
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
        resultIntent.putExtra("searchValue", TextEntryActivity.this.et.getText().toString().trim());
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }


}