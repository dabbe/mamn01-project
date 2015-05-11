package com.bbbd.treasurehunt;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bbbd.treasurehunt.location.Compass;
import com.bbbd.treasurehunt.location.CompassView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by dabbe on 13 Apr.
 */
public class CompassActivity extends Activity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener, View.OnClickListener {
    //malmö,
    private static final float lat = 55.598821f;
    private static final float lon = 12.993877f;

    private ProgressDialog loadingDialog;

    private String TAG = "CompassActivity.java";
    private Compass compass;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Location lastLocation;
    private Location targetLocation;
    private VibrationThread t;

    private ArrayList<Treasure> treasures;
    private Random rnd;

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
        loadTreasures();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        rnd = new Random();
    }

    private void initializeGUI() {
        targetLocation = new Location("");
        targetLocation.setLatitude(lat);
        targetLocation.setLongitude(lon);
        compass = new Compass(this, targetLocation);
        CompassView compassView = new CompassView(this, compass);
        compassView.setOnClickListener(this);
        ((LinearLayout) findViewById(R.id.layout)).addView(compassView);
        loadingDialog = new ProgressDialog(this);
        loadingDialog.setCancelable(false);
        loadingDialog.setMessage("Laddar..");
        loadingDialog.show();
        createDialog();

    }


    private void createDialog() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean b = prefs.getBoolean("first", true);
        if (b) {
            final Dialog dialog = new Dialog(this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_compass);
            dialog.setCanceledOnTouchOutside(false);
            TextView text = (TextView) dialog.findViewById(R.id.descript_compass);
            text.setOnClickListener(new View.OnClickListener() {
                public void onClick(View View3) {
                    dialog.dismiss();
                }
            });
            dialog.show();
            prefs.edit().putBoolean("first", false).apply();
        }
    }


    private void navigateToNextTreasure() {
        //update gui showing remaining treasures
        if (treasures.size() == 0) {
            //klar med spelet - dialog elr någonting?
            finish();
        } else {
            //borde inte ta bort, borde loopa igenom alla o kolla om man har samlat ihop dom
            int rand = rnd.nextInt(treasures.size());
            Treasure t1 = treasures.get(rand);
            compass.setTargetLocation(t1.getLocation());
            targetLocation.setLatitude(t1.getLocation().getLatitude());
            targetLocation.setLongitude(t1.getLocation().getLongitude());
            treasures.remove(rand);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(1000);
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        lastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (lastLocation == null) {
            //Stänga av elr någonting? varning?
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
        loadingDialog.cancel();
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
        if (distance <= 5) {
            startActivity(new Intent(this, DigActivity.class));
        } else {
            float distanceFactor = distance / 100f;
            int alpha = 255 << 24;
            int red = (int) (distanceFactor * (((farColor & 0xFF0000) >> 16) - ((closeColor & 0xFF0000) >> 16)) + ((closeColor & 0xFF0000) >> 16)) << 16;
            int green = (int) (distanceFactor * (((farColor & 0x00FF00) >> 8) - ((closeColor & 0x00FF00) >> 8)) + ((closeColor & 0x00FF00) >> 8)) << 8;
            int blue = (int) (distanceFactor * ((farColor & 0x0000FF) - (closeColor & 0x0000FF)) + (closeColor & 0x0000FF));
            distanceColor = alpha + red + green + blue;
        }
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

    @Override
    protected void onPause() {
        mGoogleApiClient.disconnect();
        super.onPause();
        if (t != null) t.setInterrupted(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
        if (t != null) {
            t.setInterrupted(true);
        }
        t = new VibrationThread(this);
        t.start();
        navigateToNextTreasure();
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

    private void loadTreasures() {
        String json = null;
        try {
            InputStream is = getResources().openRawResource(R.raw.treasures);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
            Gson gson = new Gson();
            Type type = new TypeToken<List<Treasure>>() {
            }.getType();
            treasures = gson.fromJson(json, type);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
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
                try {
                    sleep(pause + 120);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
