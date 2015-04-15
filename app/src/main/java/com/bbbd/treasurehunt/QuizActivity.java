package com.bbbd.treasurehunt;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        final Button button1 = (Button) findViewById(R.id.b_ans1);
        final Button button2 = (Button) findViewById(R.id.b_ans2);
        final Button button3 = (Button) findViewById(R.id.b_ans3);
        final Button button4 = (Button) findViewById(R.id.b_ans4);
        final TextView question = (TextView) findViewById(R.id.question);
        ArrayList<Button> buttons = new ArrayList<Button>();
        buttons.add(button1);
        buttons.add(button2);
        buttons.add(button3);
        buttons.add(button4);

        Random rn = new Random();
        int answer1 = rn.nextInt(10) + 1;
        int answer2 = rn.nextInt(10) + 1;
        question.setText(answer1 + " + " + answer2 + " = ");
        final int correct = answer1 + answer2;
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

        button1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (button1.getText().equals(Integer.toString(correct))) {
                    Toast.makeText(getApplicationContext(), "correct", Toast.LENGTH_SHORT).show();
                } else Toast.makeText(getApplicationContext(), "wrong", Toast.LENGTH_SHORT).show();
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(button2.getText().equals(Integer.toString(correct))){
                    Toast.makeText(getApplicationContext(), "correct", Toast.LENGTH_SHORT).show();
                }
                else Toast.makeText(getApplicationContext(), "wrong", Toast.LENGTH_SHORT).show();
            }
        });

        button3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(button3.getText().equals(Integer.toString(correct))){
                    Toast.makeText(getApplicationContext(), "correct", Toast.LENGTH_SHORT).show();
                }
                else Toast.makeText(getApplicationContext(), "wrong", Toast.LENGTH_SHORT).show();
            }
        });

        button4.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(button4.getText().equals(Integer.toString(correct))){
                    Toast.makeText(getApplicationContext(), "correct", Toast.LENGTH_SHORT).show();
                }
                else Toast.makeText(getApplicationContext(), "wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
