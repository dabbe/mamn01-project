package com.bbbd.treasurehunt;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.LinearLayout;

import com.bbbd.treasurehunt.compass.Compass;
import com.bbbd.treasurehunt.compass.CompassView;

/**
 * Created by dabbe on 13 Apr.
 */
public class CompassActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compass);
        ((LinearLayout) findViewById(R.id.layout)).addView(new CompassView(this, new Compass(this)));
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN)
            startActivity(new Intent(this, DigActivity.class));
        return true;
    }
}
