package com.bbbd.treasurehunt;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.Vibrator;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

/**
 * Created by dabbe on 13 Apr.
 */
public class DigActivity extends Activity implements SensorEventListener {

    private Float azimut, pitch, roll;  // View to draw a compass
    private CheckBox digg1, digg2, digg3;
    private boolean firstDigg, secondDigg, down, down2, down3, showFinishDiag;

    private SensorManager mSensorManager;
    private Sensor accelerometer;
    private Sensor magnetometer;
    private Dialog startDialog = null;
    private PowerManager.WakeLock mWakeLock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dig);
        digg1 = (CheckBox) findViewById(R.id.Digg1);
        digg2 = (CheckBox) findViewById(R.id.Digg2);
        digg3 = (CheckBox) findViewById(R.id.Digg3);
        firstDigg = false;
        secondDigg = false;
        down = false;
        down2 = false;
        down3 = false;
        showFinishDiag = true;

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "NoiseAlert");

        createDialog();

    }

    @Override
    public void onStop() {
        super.onStop();
        if (mWakeLock.isHeld()) {
            mWakeLock.release();
        }
    }

    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI);
        if (!mWakeLock.isHeld()) {
            mWakeLock.acquire();
        }
    }

    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }


    private void createDialog() {
        startDialog = new Dialog(this);
        startDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        startDialog.setContentView(R.layout.dialog_dig);
        startDialog.setCanceledOnTouchOutside(false);
        TextView text = (TextView) startDialog.findViewById(R.id.descript_dig);
        Button buttonOk = (Button) startDialog.findViewById(R.id.button_ok_dig);
        buttonOk.setOnClickListener(new View.OnClickListener() {
            public void onClick(View View3) {
                startDialog.dismiss();
            }
        });
        startDialog.show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //Handle the back button

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //Ask the user if they want to quit
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_menu_info_details)
                    .setTitle("Vill du avsluta spelet?")
                    .setMessage("\u00C4r du s\u00E4ker p\u00E5 att du vill avsluta nuvarande spel? \n\nAlla dina skatter \u00E4r sparade, men inte den nuvarande")
                    .setPositiveButton("Ja", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent homeScreen = new Intent(DigActivity.this, StartActivity.class);
                            homeScreen.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(homeScreen);
                            //Stop the activity
                            finish();
                        }

                    })
                    .setNegativeButton("Nej", null)
                    .show();

            return true;
        }else {
            return super.onKeyDown(keyCode, event);
        }
    }

    private void finishDialog() {
        showFinishDiag = false;
        final Dialog dialogFinish =  new Dialog(this);
        dialogFinish.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogFinish.setContentView(R.layout.dialog_dig_finish);
        dialogFinish.setCanceledOnTouchOutside(false);
        dialogFinish.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    //Toast.makeText(getApplicationContext(),"Backpressed", Toast.LENGTH_SHORT).show();
                    return true;
                }
                return false;
            }
        });
        Button buttonOk = (Button) dialogFinish.findViewById(R.id.button_ok_dig);
        buttonOk.setOnClickListener(new View.OnClickListener() {
            public void onClick(View View3) {
                dialogFinish.dismiss();
                startActivity(new Intent(DigActivity.this, BlowActivity.class));
                finish();
            }
        });
        dialogFinish.show();
    }

    private void digVibrate() {
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(250);
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    float[] mGravity;
    float[] mGeomagnetic;

    public void onSensorChanged(SensorEvent event) {
        if(digg3.isChecked() && showFinishDiag){
            finishDialog();
        }
        double y_axis;
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
            mGravity = event.values;
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
            mGeomagnetic = event.values;
        if (mGravity != null && mGeomagnetic != null) {
            float R[] = new float[9];
            float I[] = new float[9];
            boolean success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);
            if (success && !startDialog.isShowing()) {
                float orientation[] = new float[3];
                SensorManager.getOrientation(R, orientation);
                azimut = orientation[0]; // orientation contains: azimut, pitch and roll
                pitch = orientation[1];
                roll = orientation[2];
                y_axis = Math.toDegrees(pitch);
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
