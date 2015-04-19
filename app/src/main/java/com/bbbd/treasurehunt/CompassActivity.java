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
        LocationListener {

    private String TAG = "CompassActivity.java";
    private Compass compass;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Location lastLocation;
    private CompassView compassView;

    int tmpCounter = 0;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            switch (tmpCounter++) {
                case 0: {
                    Location l = new Location(""); //trelleborg
                    l.setLatitude(55.376107);
                    l.setLongitude(13.157209);
                    compassView.setTargetLocation(l);
                    break;
                }
                case 1: {
                    Location l = new Location(""); //malmö
                    l.setLatitude(55.602177);
                    l.setLongitude(13.002601);
                    compassView.setTargetLocation(l);
                    break;
                }
                case 3:
                    startActivity(new Intent(this, DigActivity.class));
                    break;
            }
        }
        return true;
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compass);
        compass = new Compass(this);

        Location target = new Location(""); //  simrishamn
        target.setLatitude(55.557194f);
        target.setLongitude(14.348759f);

        compassView = new CompassView(this, compass, target);
        ((LinearLayout) findViewById(R.id.layout)).addView(compassView);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
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
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "GoogleApiClient connection has been suspend");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(TAG, "GoogleApiClient connection has failed");
    }

    @Override
    public void onLocationChanged(Location location) {
        lastLocation = location;
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
}
