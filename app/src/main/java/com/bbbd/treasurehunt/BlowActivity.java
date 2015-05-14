package com.bbbd.treasurehunt;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.content.Context;
import android.os.Handler;
import android.os.PowerManager;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.bbbd.treasurehunt.sound.*;

/**
 * Created by Jacob Arvidsson on 17 April 2015.
 */
public class BlowActivity extends Activity{
    /* constants */
    private static final int POLL_INTERVAL = 300;
    private static final int threshold = 6;

    /** running state **/
    private boolean mRunning = false;
    /** config state **/
    private PowerManager.WakeLock mWakeLock;
    private Handler mHandler = new Handler();
    /* References to view elements */
    private TextView mStatusView;
    /* data source */
    private SoundMeter mSensor;
    private float progress;
    private ImageView sand;

    private Dialog startDialog = null;

    /****************** Define runnable thread again and again detect noise *********/

    private Runnable mSleepTask = new Runnable() {
        public void run() {
            start();
        }
    };

    // Create runnable thread to Monitor Voice
    private Runnable mPollTask = new Runnable() {
        public void run() {
            double amp = mSensor.getAmplitude();
            if ((amp > threshold) && !startDialog.isShowing()){
                progress += amp;
            }

            // Runnable(mPollTask) will again execute after POLL_INTERVAL
            mHandler.postDelayed(mPollTask, POLL_INTERVAL);

            if(progress>150){
                transitionToNextScene();
            }
            sand.setAlpha(1-(progress/150));
        }
    };

    /*********************************************************/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blow);
        mSensor = new SoundMeter();
        progress = 0;
        sand = (ImageView) findViewById(R.id.sandImage);
        createDialog();

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "NoiseAlert");
    }
    @Override
    public void onResume() {
        super.onResume();
        if (!mWakeLock.isHeld()) {
            mWakeLock.acquire();
        }
        if (!mRunning) {
            mRunning = true;
            start();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mWakeLock.isHeld()) {
            mWakeLock.release();
        }
        //Stop noise monitoring
        stop();
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //Handle the back button

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //Ask the user if they want to quit
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_menu_info_details)
                    .setTitle("Vill du avsluta spelet?")
                    .setMessage("Är du säker på att du vill avsluta nuvarande spel? \n\nAlla dina skatter är sparade, men inte den nuvarande")
                    .setPositiveButton("Ja", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent homeScreen = new Intent(BlowActivity.this, StartActivity.class);
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

    private void start() {
        mSensor.start();

        //Noise monitoring start
        // Runnable(mPollTask) will execute after POLL_INTERVAL
        mHandler.postDelayed(mPollTask, POLL_INTERVAL);
    }

    private void stop() {
        mHandler.removeCallbacks(mSleepTask);
        mHandler.removeCallbacks(mPollTask);
        mSensor.stop();
        mRunning = false;

    }

    private void transitionToNextScene(){
        stop();
        finishDialog();
        //startActivity(new Intent(this, QuizActivity.class));
        //finish();
    }

    private void finishDialog() {
        final Dialog dialogFinish =  new Dialog(this);
        dialogFinish.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogFinish.setContentView(R.layout.dialog_quiz);
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
        //dialog.setTitle("Dialog Box");
        TextView text = (TextView) dialogFinish.findViewById(R.id.descript_quiz);
        text.setText("Du blåste bort sanden, men kistan är låst!" );
        Button buttonOk = (Button) dialogFinish.findViewById(R.id.buttonOK);
        buttonOk.setOnClickListener(new View.OnClickListener() {
            public void onClick(View View3) {
                dialogFinish.dismiss();
                startActivity(new Intent(BlowActivity.this, QuizActivity.class));
                finish();
            }
        });
        dialogFinish.show();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            startActivity(new Intent(this, QuizActivity.class));
            finish();
        }
        return true;
    }
    private void createDialog() {
        startDialog = new Dialog(this);
        startDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        startDialog.setContentView(R.layout.dialog_blow);
        startDialog.setCanceledOnTouchOutside(false);
        TextView text = (TextView) startDialog.findViewById(R.id.descript_blow);
        Button buttonOk = (Button) startDialog.findViewById(R.id.button_ok_blow);
        buttonOk.setOnClickListener(new View.OnClickListener() {
            public void onClick(View View3) {
                startDialog.dismiss();
            }
        });
        startDialog.show();
    }
}
