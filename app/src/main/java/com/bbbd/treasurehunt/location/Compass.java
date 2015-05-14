package com.bbbd.treasurehunt.location;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;

/**
 * Created by Daniel on 2015-03-25.
 */
public class Compass implements SensorEventListener {
    private SensorManager sensorManager;
    private Sensor accelerometer, magnetometer;
    private float azimut;
    private float[] gravity, geomagnetic;
    private Location target;

    public Compass(Context c, Location target) {
        this.target = target;
        sensorManager = (SensorManager) c.getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        start();
    }


    public void setTargetLocation(Location location) {
        this.target = location;
    }

    public void stop() {
        sensorManager.unregisterListener(this);
    }

    public void start() {
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI);
    }

    /* Taken from: http://blog.thomnichols.org/2011/08/smoothing-sensor-data-with-a-low-pass-filter
     * time smoothing constant for low-pass filter
     * 0 ≤ alpha ≤ 1 ; a smaller value basically means more smoothing
     * See: http://en.wikipedia.org/wiki/Low-pass_filter#Discrete-time_realization
     */
    static final float ALPHA = 0.15f;

    /**
     * Taken from: http://blog.thomnichols.org/2011/08/smoothing-sensor-data-with-a-low-pass-filter
     *
     * @see http://en.wikipedia.org/wiki/Low-pass_filter#Algorithmic_implementation
     * @see http://developer.android.com/reference/android/hardware/SensorEvent.html#values
     */
    private float[] lowPass(float[] input, float[] output) {
        if (output == null) return input;

        for (int i = 0; i < input.length; i++) {
            output[i] = output[i] + ALPHA * (input[i] - output[i]);
        }
        return output;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        switch (event.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                gravity = lowPass(event.values.clone(), gravity);
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                geomagnetic = lowPass(event.values.clone(), geomagnetic);
                break;
        }

        if (gravity != null && geomagnetic != null) {
            float R[] = new float[9];
            float I[] = new float[9];

            boolean success = SensorManager.getRotationMatrix(R, I, gravity, geomagnetic);
            if (success) {
                float[] orientation = new float[3];
                SensorManager.getOrientation(R, orientation);
                azimut = orientation[0];
            }
        }

    }

    public float getDegrees(Location locationFrom) {
        float f = (float) Math.toDegrees(azimut);
        return locationFrom.bearingTo(target) - (f < 0.0f ? f + 360.0f : f);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}
