package com.bbbd.treasurehunt;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.content.Context;
import android.os.Handler;
import android.os.PowerManager;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.bbbd.treasurehunt.sound.*;

/**
 * Created by Jacob Arvidsson on 17 April 2015.
 */
public class BlowActivity extends Activity{
    /* constants */
    private static final int POLL_INTERVAL = 300;
    private static final int threshold = 5;

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
            mStatusView.setText("" + progress);

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
        mStatusView = (TextView) findViewById(R.id.soundMeter);

        mSensor = new SoundMeter();
        progress = 0;
        sand = (ImageView) findViewById(R.id.sandImage);

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "NoiseAlert");
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
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN)
            startActivity(new Intent(this, QuizActivity.class));
        return true;
    }
}
