package com.bbbd.treasurehunt;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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

        //Assign textView with the question
        question = (TextView) findViewById(R.id.question);

        //Add all buttons to Arraylist
        buttons = new ArrayList<Button>();
        buttons.add(button1);
        buttons.add(button2);
        buttons.add(button3);
        buttons.add(button4);



        createJsonQuestion();
        //makeMathQuestion();

        button1.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                if (button1.getText().equals(Integer.toString(correct))) {
                    Toast.makeText(getApplicationContext(), "correct", Toast.LENGTH_SHORT).show();
                    correctAnswers();
                } else
                    Toast.makeText(getApplicationContext(), "wrong", Toast.LENGTH_SHORT).show();
                createJsonQuestion();
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (button2.getText().equals(Integer.toString(correct))) {
                    Toast.makeText(getApplicationContext(), "correct", Toast.LENGTH_SHORT).show();
                    correctAnswers();
                } else
                    Toast.makeText(getApplicationContext(), "wrong", Toast.LENGTH_SHORT).show();
                    createJsonQuestion();
            }
        });


        button3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (button3.getText().equals(Integer.toString(correct))) {
                    Toast.makeText(getApplicationContext(), "correct", Toast.LENGTH_SHORT).show();
                    correctAnswers();
                } else
                    Toast.makeText(getApplicationContext(), "wrong", Toast.LENGTH_SHORT).show();
                    createJsonQuestion();
            }
        });

        button4.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (button4.getText().equals(Integer.toString(correct))) {
                    Toast.makeText(getApplicationContext(), "correct", Toast.LENGTH_SHORT).show();
                    correctAnswers();
                } else
                    Toast.makeText(getApplicationContext(), "wrong", Toast.LENGTH_SHORT).show();
                    createJsonQuestion();
            }
        });
    }

    private void createJsonQuestion() {
        String s = loadJSONFromAsset();
        try {
            //Take out information of the random JSon Object with given name
            JSONObject obj = new JSONObject(s);
            JSONArray jArry = obj.getJSONArray("math1");
            Random rand = new Random();
            JSONObject firstObj = jArry.getJSONObject(rand.nextInt(jArry.length()));
            question.setText(firstObj.getString("question"));

            //Take out the answers and shuffle
            String [] answers = new String[4];
            answers[0] = firstObj.getString("false1");
            answers[1] = firstObj.getString("false2");
            answers[2] = firstObj.getString("false3");
            answers[3] = firstObj.getString("correct");
            correct = Integer.valueOf(answers[3]);
            Collections.shuffle(Arrays.asList(answers));

            //Assign the answers
            button1.setText(answers[0]);
            button2.setText(answers[1]);
            button3.setText(answers[2]);
            button4.setText(answers[3]);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = getResources().openRawResource(R.raw.document);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    @Override
    public void onBackPressed() {
        //Nothing will happen when you push back_buttom
    }


    private void correctAnswers() {
        startActivity(new Intent(this, CompassActivity.class));
        finish();
        //Will be changed later on
    }

    //Not in use, but nice to have
    private void makeMathQuestion(){
        Random rn = new Random();
        int answer1 = rn.nextInt(10) + 1;
        int answer2 = rn.nextInt(10) + 1;
        question.setText(answer1 + " + " + answer2 + " = ");
        correct = answer1 + answer2;

        for(int i = 0; i < 4; i++){
            int nbr = rn.nextInt(correct*2);
            if(nbr != correct){
                buttons.get(i).setText(Integer.toString(nbr));
            }
        }
        buttons.get(rn.nextInt(4)).setText(Integer.toString(correct));

    }
}
