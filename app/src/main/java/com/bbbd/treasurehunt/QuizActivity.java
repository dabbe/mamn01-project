package com.bbbd.treasurehunt;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by dabbe on 13 Apr.
 */
public class QuizActivity extends Activity {
    private Button button1;
    private Button button2;
    private Button button3;
    private Button button4;

    private TextView question;

    private ArrayList<Button> buttons;
    private int correct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        // Assign the buttons
        button1 = (Button) findViewById(R.id.b_ans1);
        button2 = (Button) findViewById(R.id.b_ans2);
        button3 = (Button) findViewById(R.id.b_ans3);
        button4 = (Button) findViewById(R.id.b_ans4);

        //Assign texview with the question
        question = (TextView) findViewById(R.id.question);

        //Add all buttons to arraylist
        buttons = new ArrayList<Button>();
        buttons.add(button1);
        buttons.add(button2);
        buttons.add(button3);
        buttons.add(button4);


        makeMathQuestion();
/*
        button1.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    changeBackColorButton(0);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (button1.getText().equals(Integer.toString(correct))) {
                        Toast.makeText(getApplicationContext(), "correct", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "wrong", Toast.LENGTH_SHORT).show();
                    }
                }
                return false;
            }
        });
**/

        button1.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                if (button1.getText().equals(Integer.toString(correct))) {
                    Toast.makeText(getApplicationContext(), "correct", Toast.LENGTH_SHORT).show();
                    correctAwnsers();
                } else
                    Toast.makeText(getApplicationContext(), "wrong", Toast.LENGTH_SHORT).show();
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (button2.getText().equals(Integer.toString(correct))) {
                    Toast.makeText(getApplicationContext(), "correct", Toast.LENGTH_SHORT).show();
                    correctAwnsers();
                } else
                    Toast.makeText(getApplicationContext(), "wrong", Toast.LENGTH_SHORT).show();
            }
        });


        button3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (button3.getText().equals(Integer.toString(correct))) {
                    Toast.makeText(getApplicationContext(), "correct", Toast.LENGTH_SHORT).show();
                    correctAwnsers();
                } else
                    Toast.makeText(getApplicationContext(), "wrong", Toast.LENGTH_SHORT).show();
            }
        });

        button4.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (button4.getText().equals(Integer.toString(correct))) {
                    Toast.makeText(getApplicationContext(), "correct", Toast.LENGTH_SHORT).show();
                    correctAwnsers();
                } else
                    Toast.makeText(getApplicationContext(), "wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void correctAwnsers() {
        startActivity(new Intent(this, CompassActivity.class));
    }

    /*
    private void changeBackColorButton(int i) {
        for (int j = 0; j < buttons.size(); j++ ){
            if(i == j) {
                buttons.get(j).setBackgroundColor(Color.BLUE);
            } else {
                buttons.get(j).setBackgroundColor(Color.LTGRAY);
            }
        }

    }
    **/

    private void makeMathQuestion(){

        Random rn = new Random();
        int answer1 = rn.nextInt(10) + 1;
        int answer2 = rn.nextInt(10) + 1;
        question.setText(answer1 + " + " + answer2 + " = ");
        correct = answer1 + answer2;
        Log.d("Quiz","Size vector:" + buttons.size());
        Log.d("Quiz", buttons.get(0).getText().toString());

        int i = 0;
        while(i < 4){
            int nbr = rn.nextInt(correct*2);
            if(nbr != correct){
                buttons.get(i).setText(Integer.toString(nbr));
                i++;
            }
        }
        buttons.get(rn.nextInt(4)).setText(Integer.toString(correct));

    }
}
