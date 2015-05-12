package com.bbbd.treasurehunt;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.TextView;

import static android.content.Context.MODE_WORLD_READABLE;

/**
 * Created by dabbe on 13 Apr.
 * mod 14 apr by christian
 */
public class StartActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
      /*  SharedPreferences prefs = this.getSharedPreferences("myPrefsKey", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("key", 0);
        editor.commit();
**/
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        int highScore = sharedPref.getInt("high", 0);
        ((TextView) findViewById(R.id.textView3)).setText("Totalt antal poäng: " + highScore);

    }

    public void startHunting(View view) {
        startActivity(new Intent(this, CompassActivity.class));
        //finish();
    }
}
