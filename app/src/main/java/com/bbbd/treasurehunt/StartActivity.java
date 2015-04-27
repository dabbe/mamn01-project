package com.bbbd.treasurehunt;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

/**
 * Created by dabbe on 13 Apr.
 * mod 14 apr by christian
 */
public class StartActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        int highScore = sharedPref.getInt(getString(R.string.saved_high_score), 0);
        ((TextView) findViewById(R.id.textView3)).setText("Totalt antal poäng: " + highScore);

    }

    public void startHunting(View view) {
        startActivity(new Intent(this, CompassActivity.class));
        //finish();
    }
}
