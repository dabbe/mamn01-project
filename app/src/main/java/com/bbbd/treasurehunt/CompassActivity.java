package com.bbbd.treasurehunt;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.bbbd.treasurehunt.location.Compass;
import com.bbbd.treasurehunt.location.CompassView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

/**
 * Created by dabbe on 13 Apr.
 */
public class CompassActivity extends Activity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener, View.OnClickListener {

    //malmö
    private static final float lat = 55.602177f;
    private static final float lon = 13.002601f;

    private String TAG = "CompassActivity.java";
    private Compass compass;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Location lastLocation;
    private Location targetLocation;
    private CompassView compassView;
    private VibrationThread t;

    private long distanceFactor = 1000;
    private int distanceColor = 0xFFFF0000;

    private int closeColor = 0x00FF00;
    private int farColor = 0x0B2403;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compass);
        initializeGUI();
        initialize();
    }

    private void initialize() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    private void initializeGUI() {
        targetLocation = new Location("");
        targetLocation.setLatitude(lat);
        targetLocation.setLongitude(lon);
        compass = new Compass(this, targetLocation);
        compassView = new CompassView(this, compass);
        compassView.setOnClickListener(this);
        ((LinearLayout) findViewById(R.id.layout)).addView(compassView);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(1000);
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

        lastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (lastLocation != null) {
            System.out.println(lastLocation);
            //mLatitudeText.setText(String.valueOf(lastLocation.getLatitude()));
            //mLongitudeText.setText(String.valueOf(lastLocation.getLongitude()));
        } else {
            System.out.println("Location unavailable :(");
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "GoogleApiClient connection has been suspend");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setTitle("Error");
        b.setMessage("The Google Api Client connection could not be established.");
        b.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //finish();
            }
        });
        b.setCancelable(false);
        b.show();
    }

    @Override
    public void onLocationChanged(Location location) {
        lastLocation = location;
        float meters = lastLocation.distanceTo(targetLocation);
        float pause;
        if (meters >= 100) {
            pause = 1000;
        } else {
            pause = 9f * meters + 100f;
        }
        this.distanceFactor = (long) pause;

        float distance = Math.min(100, meters);
        float distanceFactor = distance / 100f;

        int alpha = 255 << 24;
        int red = (int) (distanceFactor * (((farColor & 0xFF0000) >> 16) - ((closeColor & 0xFF0000) >> 16)) + ((closeColor & 0xFF0000) >> 16)) << 16;
        int green = (int) (distanceFactor * (((farColor & 0x00FF00) >> 8) - ((closeColor & 0x00FF00) >> 8)) + ((closeColor & 0x00FF00) >> 8)) << 8;
        int blue = (int) (distanceFactor * ((farColor & 0x0000FF) - (closeColor & 0x0000FF)) + (closeColor & 0x0000FF));
        distanceColor = alpha + red + green + blue;
    }

    public Location getLastLocation() {
        if (lastLocation != null) return lastLocation;
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(true);
        criteria.setCostAllowed(true);
        criteria.setSpeedRequired(false);
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        lastLocation = lm.getLastKnownLocation(lm.getBestProvider(criteria, true));
        return lastLocation;
    }

    @Override
    public void onClick(View v) {
        startActivity(new Intent(this, DigActivity.class));
    }

    //stänga av location osv + vibration thread
    @Override
    protected void onPause() {
        super.onPause();
        if (t != null) t.setInterrupted(true);
    }

    //sätta på location osv
    @Override
    protected void onResume() {
        super.onResume();
        if (t != null)
            t.setInterrupted(true);
        t = new VibrationThread(this);
        t.start();

    }

    /**
     * Returns the color indicator for the compassView arrow depending on distance from target location
     *
     * @return
     */
    public synchronized int getDistanceColor() {
        return distanceColor;
    }

    /**
     * Returns the distance factor, i.e. the time the vibration thread should be paused
     *
     * @return
     */
    public synchronized long getDistanceFactor() {
        return distanceFactor;
    }

    private static class VibrationThread extends Thread {

        private Vibrator vibrator;
        private CompassActivity activity;
        private boolean interrupted = false;

        public VibrationThread(CompassActivity activity) {
            this.activity = activity;
            vibrator = (Vibrator) activity.getSystemService(Context.VIBRATOR_SERVICE);
        }

        public void setInterrupted(boolean b) {
            this.interrupted = b;
        }

        @Override
        public void run() {
            while (!interrupted) {
                long pause = activity.getDistanceFactor();
                vibrator.vibrate(120);
                //   Log.d("VibrationThread", "Pause: " + pause);
                try {
                    sleep(pause + 120);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
