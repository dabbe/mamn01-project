package com.bbbd.treasurehunt;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.LinearLayout;

import com.bbbd.treasurehunt.compass.Compass;
import com.bbbd.treasurehunt.compass.CompassView;

/**
 * Created by dabbe on 13 Apr.
 */
public class CompassActivity extends Activity {
    Compass compass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compass);
        compass = new Compass(this);
        ((LinearLayout) findViewById(R.id.layout)).addView(new CompassView(this, compass));
        new Thread() {
            @Override
            public void run() {
                while (true) {
                    test();
                    try {
                        sleep(400);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    void test() {
        Location loc;   //Will hold lastknown location
        Location wptLoc = new Location("");    // Waypoint location
        float dist = -1;
        float bearing = 0;
        float heading = 0;
        float arrow_rotation = 0;

        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        loc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        if (loc == null) {   //No recent GPS fix
            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_FINE);
            criteria.setAltitudeRequired(false);
            criteria.setBearingRequired(true);
            criteria.setCostAllowed(true);
            criteria.setSpeedRequired(false);
            loc = lm.getLastKnownLocation(lm.getBestProvider(criteria, true));
        }

        if (loc != null) {
            wptLoc.setLatitude(55.722920f);
            wptLoc.setLongitude(13.215821f);
            dist = loc.distanceTo(wptLoc);
            bearing = loc.bearingTo(wptLoc);    // -180 to 180
            heading = loc.getBearing();         // 0 to 360
            // *** Code to calculate where the arrow should point ***
            arrow_rotation = (360 + ((bearing + 360) % 360) - heading) % 360;

            Log.d("CompassActivity", "Dist: " + dist);
            Log.d("CompassActivity", "Bearing: " + bearing);
            Log.d("CompassActivity", "Heading: " + heading);
            Log.d("CompassActivity", "Arrow rotation: " + arrow_rotation);
            Log.d("CompassActivity", "Should point at: " + (bearing - compass.getDegrees()));
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN)
            startActivity(new Intent(this, DigActivity.class));
        return true;
    }
}
