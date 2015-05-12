package com.bbbd.treasurehunt;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.TextView;

/**
 * Created by dabbe on 13 Apr.
 */
public class DigActivity extends Activity implements SensorEventListener {

    Float azimut, pitch, roll;  // View to draw a compass
    //TextView x, y, z;
    CheckBox digg1, digg2, digg3;
    boolean firstDigg, secondDigg, down, down2, down3;

    private SensorManager mSensorManager;
    Sensor accelerometer;
    Sensor magnetometer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dig);
//        x = (TextView) findViewById(R.id.x);
//        y = (TextView) findViewById(R.id.y);
//        z = (TextView) findViewById(R.id.z);
        digg1 = (CheckBox) findViewById(R.id.Digg1);
        digg2 = (CheckBox) findViewById(R.id.Digg2);
        digg3 = (CheckBox) findViewById(R.id.Digg3);
        firstDigg = false;
        secondDigg = false;
        down = false;
        down2 = false;
        down3 = false;

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        createDialog();

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN)
            startActivity(new Intent(this, BlowActivity.class));
        finish();
        return true;
    }

    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI);
    }

    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    private void createDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_dig);
        dialog.setCanceledOnTouchOutside(false);
        //dialog.setTitle("Dialog Box");
        TextView text = (TextView) dialog.findViewById(R.id.descript_dig);
        //image.setImageResource(R.drawable.dialog2_bg);
        text.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

//If the Hint only should be shown the first time the game is started
/*    private boolean isFirstTime()
    {
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        boolean ranBefore = preferences.getBoolean("RanBefore", false);
        if (!ranBefore) {
            // first time
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("RanBefore", true);
            editor.apply();
        }
        return !ranBefore;
    }*/

    private void digVibrate() {
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(250);
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    float[] mGravity;
    float[] mGeomagnetic;

    public void onSensorChanged(SensorEvent event) {
        double y_axis;
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
            mGravity = event.values;
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
            mGeomagnetic = event.values;
        if (mGravity != null && mGeomagnetic != null) {
            float R[] = new float[9];
            float I[] = new float[9];
            boolean success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);
            if (success) {
                float orientation[] = new float[3];
                SensorManager.getOrientation(R, orientation);
                azimut = orientation[0]; // orientation contains: azimut, pitch and roll
                pitch = orientation[1];
                roll = orientation[2];
                y_axis = Math.toDegrees(pitch);
//                x.setText("X: " + Double.toString(Math.toDegrees(azimut)));
//                y.setText("Y: " + y_axis);
//                z.setText("Z: " + Double.toString(Math.toDegrees(roll)));
                if (y_axis > 30 && y_axis < 50 && !firstDigg) {
                    if (!down) {
                        digVibrate();
                    }
                    down = true;

                } else if (y_axis < -30 && y_axis > -50 && down) {
                    digg1.setChecked(true);
                    firstDigg = true;
                }
                if (firstDigg) {
                    if (y_axis > 30 && y_axis < 50) {
                        if (!down2) {
                            digVibrate();
                        }
                        down2 = true;
                    } else if (y_axis < -30 && y_axis > -50 && down2) {
                        digg2.setChecked(true);
                        secondDigg = true;
                    }
                }
                if (firstDigg && secondDigg) {
                    if (y_axis > 30 && y_axis < 50) {
                        if (!down3) {
                            digVibrate();
                        }
                        down3 = true;
                    } else if (y_axis < -30 && y_axis > -50 && down3) {
                        digg3.setChecked(true);
                    }
                }

            }
        }
    }
}
