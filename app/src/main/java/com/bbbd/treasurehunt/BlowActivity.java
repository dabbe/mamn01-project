package com.bbbd.treasurehunt;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.content.Context;
import android.os.Handler;
import android.os.PowerManager;
import android.view.View;
import android.view.Window;
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
            if ((amp > threshold)) progress += amp;

            // Runnable(mPollTask) will again execute after POLL_INTERVAL
            mHandler.postDelayed(mPollTask, POLL_INTERVAL);

            if(progress>50){
                transitionToNextScene();
            }
            sand.setAlpha(1-(progress/50));
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
        if (!mRunning) {
            mRunning = true;
            start();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        //Stop noise monitoring
        stop();
    }

    private void start() {
        mSensor.start();
        if (!mWakeLock.isHeld()) {
            mWakeLock.acquire();
        }
        //Noise monitoring start
        // Runnable(mPollTask) will execute after POLL_INTERVAL
        mHandler.postDelayed(mPollTask, POLL_INTERVAL);
    }

    private void stop() {
        if (mWakeLock.isHeld()) {
            mWakeLock.release();
        }
        mHandler.removeCallbacks(mSleepTask);
        mHandler.removeCallbacks(mPollTask);
        mSensor.stop();
        mRunning = false;

    }

    private void transitionToNextScene(){
        stop();
        startActivity(new Intent(this, QuizActivity.class));
        finish();
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
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_blow);
        dialog.setCanceledOnTouchOutside(false);
        TextView text = (TextView) dialog.findViewById(R.id.descript_blow);
        text.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}
