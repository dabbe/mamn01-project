package com.bbbd.treasurehunt;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
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

    //mmö->simris->trellan->stlm
    private static final float lat[] = {55.602177f, 55.557194f, 55.376107f, 59.329119f};
    private static final float lon[] = {13.002601f, 14.348759f, 13.157209f, 18.065136f};

    private String TAG = "CompassActivity.java";
    private Compass compass;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Location lastLocation;
    private CompassView compassView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compass);
        initButtons();
        compass = new Compass(this);

        Location target = new Location("");
        target.setLatitude(lat[0]);
        target.setLongitude(lon[0]);

        compassView = new CompassView(this, compass, target);
        compassView.setOnClickListener(this);
        ((LinearLayout) findViewById(R.id.layout)).addView(compassView);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    private void initButtons() {
        findViewById(R.id.btn_malmo).setOnClickListener(this);
        findViewById(R.id.btn_simrishamn).setOnClickListener(this);
        findViewById(R.id.btn_sthlm).setOnClickListener(this);
        findViewById(R.id.btn_tbg).setOnClickListener(this);
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

    @Override
    public void onClick(View v) {
        Location l = new Location("");
        switch (v.getId()) {
            case R.id.btn_malmo:
                l.setLatitude(lat[0]);
                l.setLongitude(lon[0]);
                break;
            case R.id.btn_simrishamn:
                l.setLatitude(lat[1]);
                l.setLongitude(lon[1]);
                break;
            case R.id.btn_tbg:
                l.setLatitude(lat[2]);
                l.setLongitude(lon[2]);
                break;
            case R.id.btn_sthlm:
                l.setLatitude(lat[3]);
                l.setLongitude(lon[3]);
                break;
            default:
                startActivity(new Intent(this, DigActivity.class));
                return;
        }
        compassView.setTargetLocation(l);
    }

    //stänga av location osv
    @Override
    protected void onPause() {
        super.onPause();
    }

    //sätta på location osv
    @Override
    protected void onResume() {
        super.onResume();
    }
}
