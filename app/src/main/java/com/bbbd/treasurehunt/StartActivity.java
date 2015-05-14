package com.bbbd.treasurehunt;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import static android.content.Context.MODE_WORLD_READABLE;

/**
 * Created by dabbe on 13 Apr.
 * mod 14 apr by christian
 */
public class StartActivity extends Activity {


    private PowerManager.WakeLock mWakeLock;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
      /*  SharedPreferences prefs = this.getSharedPreferences("myPrefsKey", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("key", 0);
        editor.commit();
**/
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "NoiseAlert");
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        int highScore = sharedPref.getInt("high", 0);
        ((TextView) findViewById(R.id.textView3)).setText("Totalt antal poäng: " + highScore);
        if (!mWakeLock.isHeld()) {
            mWakeLock.acquire();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mWakeLock.isHeld()) {
            mWakeLock.release();
        }
    }

    //Onbackkey pressed, comes alert dialog
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //Handle the back button

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //Ask the user if they want to quit
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_menu_info_details)
                    .setTitle("Vill du stänga av spelet?")
                    .setMessage("Är du säker på att du vill stänga av spelet? \n\n Alla dina skatter är sparade, och finns kvar här nästa gång du vill leta efter fler")
                    .setPositiveButton("Jag är säker", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {

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

    public void startHunting(View view) {
        startActivity(new Intent(this, CompassActivity.class));
        //finish();
    }
}
