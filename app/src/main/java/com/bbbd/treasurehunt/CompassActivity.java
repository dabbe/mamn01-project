package com.bbbd.treasurehunt;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;

/**
 * Created by dabbe on 13 Apr.
 */
public class CompassActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compass);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        startActivity(new Intent(this, DigActivity.class));
        return true;
    }
}
