package com.bbbd.treasurehunt;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
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
    private String correct;

    private ArrayList<ImageView> chests;
    private int nbr_tries;
    final private int TRIES = 3;

    MediaPlayer mp = null;

    final Context context = this;

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

        nbr_tries = TRIES;

        chests = new ArrayList<ImageView>();
        chests.add((ImageView) findViewById(R.id.chest_01));
        chests.add((ImageView) findViewById(R.id.chest_02));
        chests.add((ImageView) findViewById(R.id.chest_03));

        createJsonQuestion();
        //makeMathQuestion();

        button1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (button1.getText().equals(correct)) {
                    Toast.makeText(getApplicationContext(), "correct", Toast.LENGTH_SHORT).show();
                    correctAnswers();
                } else {
                    Toast.makeText(getApplicationContext(), "wrong", Toast.LENGTH_SHORT).show();
                    wrongAnswers();
                }
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (button2.getText().equals(correct)) {
                    Toast.makeText(getApplicationContext(), "correct", Toast.LENGTH_SHORT).show();
                    correctAnswers();
                } else{
                    Toast.makeText(getApplicationContext(), "wrong", Toast.LENGTH_SHORT).show();
                    wrongAnswers();
                }
            }
        });


        button3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (button3.getText().equals(correct)) {
                    Toast.makeText(getApplicationContext(), "correct", Toast.LENGTH_SHORT).show();
                    correctAnswers();
                } else {
                    Toast.makeText(getApplicationContext(), "wrong", Toast.LENGTH_SHORT).show();
                    wrongAnswers();
                }
            }
        });

        button4.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (button4.getText().equals(correct)) {
                    Toast.makeText(getApplicationContext(), "correct", Toast.LENGTH_SHORT).show();
                    correctAnswers();
                } else {
                    Toast.makeText(getApplicationContext(), "wrong", Toast.LENGTH_SHORT).show();
                    wrongAnswers();
                }
            }
        });

        createDialog();
    }

    private void createDialog() {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_quiz);
        dialog.setCanceledOnTouchOutside(false);
        //dialog.setTitle("Dialog Box");
        ImageView image = (ImageView) dialog.findViewById(R.id.imageView_dialog_quiz);
        image.setImageResource(R.drawable.dialog2_bg);
        image.setOnClickListener(new View.OnClickListener() {
            public void onClick(View View3) {
                dialog.dismiss();
            }
        });
        dialog.show();
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
            String[] answers = new String[4];
            answers[0] = firstObj.getString("false1");
            answers[1] = firstObj.getString("false2");
            answers[2] = firstObj.getString("false3");
            answers[3] = firstObj.getString("correct");
            correct = answers[3];
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

   /* @Override
    public void onBackPressed() {
        //Nothing will happen when you push back_buttom
    }
 **/
    private void correctVibrate() {
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        long[] pattern = {0, 250, 400, 1050};
        v.vibrate(pattern, -1);
    }

    private void wrongVibrate(){
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        long[] pattern = {0, 100, 100, 100, 100, 100, 100, 100, 100, 500};
        v.vibrate(pattern, -1);

    }

    private void correctSound(){
        mp = MediaPlayer.create(this, R.raw.correctsound);
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.release();
            }

        });
        mp.start();
    }

    private void wrongSound(){
        mp = MediaPlayer.create(this, R.raw.wrongsound);
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.release();
            }

        });
        mp.start();
    }

    private void wrongAnswers(){
        if(nbr_tries > 1) {
            createJsonQuestion();
            nbr_tries--;
            chests.get(nbr_tries).setImageResource(R.drawable.little_chest_b_w);
        } else {
            nbr_tries--;
            chests.get(nbr_tries).setImageResource(R.drawable.little_chest_b_w);
            startActivity(new Intent(this, CompassActivity.class));
            finish();
        }
        wrongSound();
        wrongVibrate();
    }

    private void correctAnswers() {
        correctVibrate();
        correctSound();
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
        int tempCorrect = answer1 + answer2;

        for(int i = 0; i < 4; i++){
            int nbr = rn.nextInt(tempCorrect*2);
            if(nbr != tempCorrect){
                buttons.get(i).setText(Integer.toString(nbr));
            }
        }
        buttons.get(rn.nextInt(4)).setText(Integer.toString(tempCorrect));
        correct = String.valueOf(tempCorrect);
    }
}
