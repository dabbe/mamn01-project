package com.bbbd.treasurehunt;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.content.Context;
import android.os.Handler;
import android.os.PowerManager;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import com.bbbd.treasurehunt.sound.*;

/**
 * Created by Jacob Arvidsson on 17 April 2015.
 */
public class BlowActivity extends Activity{
    /* constants */
    private static final int POLL_INTERVAL = 300;
    /** running state **/
    private boolean mRunning = false;
    /** config state **/
    private static final int threshold = 5;
    private PowerManager.WakeLock mWakeLock;
    private Handler mHandler = new Handler();
    /* References to view elements */
    private TextView mStatusView;
    /* data source */
    private SoundMeter mSensor;

    /****************** Define runnable thread again and again detect noise *********/

    private Runnable mSleepTask = new Runnable() {
        public void run() {
            //Log.i("Noise", "runnable mSleepTask");
            start();
        }
    };

    // Create runnable thread to Monitor Voice
    private Runnable mPollTask = new Runnable() {
        public void run() {

            double amp = mSensor.getAmplitude();
            //Log.i("Noise", "runnable mPollTask");
            mStatusView.setText("Monitoring" + amp);

            if ((amp > threshold)) {
                mStatusView.setText("Done");
                //Log.i("Noise", "==== onCreate ===");

            }

            // Runnable(mPollTask) will again execute after POLL_INTERVAL
            mHandler.postDelayed(mPollTask, POLL_INTERVAL);

        }
    };

    /*********************************************************/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blow);
        mStatusView = (TextView) findViewById(R.id.soundMeter);

        // Used to record voice
        mSensor = new SoundMeter();

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "NoiseAlert");
    }
    @Override
    public void onResume() {
        super.onResume();
        //Log.i("Noise", "==== onResume ===");

        if (!mRunning) {
            mRunning = true;
            start();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        // Log.i("Noise", "==== onStop ===");

        //Stop noise monitoring
        stop();

    }

    private void start() {
        //Log.i("Noise", "==== start ===");

        mSensor.start();
        if (!mWakeLock.isHeld()) {
            mWakeLock.acquire();
        }

        //Noise monitoring start
        // Runnable(mPollTask) will execute after POLL_INTERVAL
        mHandler.postDelayed(mPollTask, POLL_INTERVAL);
    }

    private void stop() {
        Log.i("Noise", "==== Stop Noise Monitoring===");
        if (mWakeLock.isHeld()) {
            mWakeLock.release();
        }
        mHandler.removeCallbacks(mSleepTask);
        mHandler.removeCallbacks(mPollTask);
        mSensor.stop();
        mStatusView.setText("Stopped!");
        mRunning = false;

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN)
            startActivity(new Intent(this, QuizActivity.class));
        return true;
    }
}
