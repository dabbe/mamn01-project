package com.bbbd.treasurehunt;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.CheckBox;
import android.widget.TextView;

/**
 * Created by dabbe on 13 Apr.
 */
public class DigActivity extends Activity implements SensorEventListener{

    Float azimut, pitch, roll;  // View to draw a compass
    TextView x, y, z;
    CheckBox digg1, digg2;
    boolean firstDigg, down, down2;
    int y_zero;

    private SensorManager mSensorManager;
    Sensor accelerometer;
    Sensor magnetometer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dig);
        x = (TextView) findViewById(R.id.x);
        y = (TextView) findViewById(R.id.y);
        z = (TextView) findViewById(R.id.z);
        digg1 = (CheckBox) findViewById(R.id.Digg1);
        digg2 = (CheckBox) findViewById(R.id.Digg2);
        firstDigg = false;
        down = false;
        down2 = false;

        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN)
            startActivity(new Intent(this, BlowActivity.class));
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

    public void onAccuracyChanged(Sensor sensor, int accuracy) {  }

    float[] mGravity;
    float[] mGeomagnetic;
    public void onSensorChanged(SensorEvent event) {
        double y_axis = 0;
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
                x.setText("X: " + Double.toString(Math.toDegrees(azimut)));
                y.setText("Y: " + y_axis);
                z.setText("Z: " + Double.toString(Math.toDegrees(roll)));
                if(y_axis > 30 && y_axis < 50 && !firstDigg){
                    down = true;
                }
                else if(y_axis < -30 && y_axis > -50 && down){
                    digg1.setChecked(true);
                    firstDigg = true;
                }
                if(firstDigg){
                    if(y_axis > 30 && y_axis < 50){
                        down2 = true;
                    }
                    else if(y_axis < -30 && y_axis > -50 && down2){
                        digg2.setChecked(true);
                    }
                }

            }
        }
    }
}
