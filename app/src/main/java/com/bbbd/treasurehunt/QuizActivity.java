package com.bbbd.treasurehunt;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.Toast;

/**
 * Created by dabbe on 13 Apr.
 */
public class QuizActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {

        Toast.makeText(this, "trololol", Toast.LENGTH_SHORT).show();
        return true;
    }
}
