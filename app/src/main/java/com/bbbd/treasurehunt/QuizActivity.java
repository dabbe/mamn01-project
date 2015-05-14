package com.bbbd.treasurehunt;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
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
    final private String first_half = "Du har ";
    final private String second_half = " försök kvar ";
    final private String correctAnswerFirst = "Du får ";
    final private String correctAnswerLast = " poäng!";

    private String typeQuestion;

    private PowerManager.WakeLock mWakeLock;


    MediaPlayer mp = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        //typeQuestion = getIntent().getExtras().getString("namn");
        // Ska ändras när de andra är implementerat
        typeQuestion = "math3";

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
        ((ImageView) findViewById(R.id.chest_01)).setImageResource(R.drawable.heart);
        ((ImageView) findViewById(R.id.chest_02)).setImageResource(R.drawable.heart);
        ((ImageView) findViewById(R.id.chest_03)).setImageResource(R.drawable.heart);
        ((TextView) findViewById(R.id.text_tries_left)).setText(first_half + nbr_tries + second_half);

        createJsonQuestion();
        //makeMathQuestion();

        button1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (button1.getText().equals(correct)) {
                    correctAnswers();
                } else {
                    wrongAnswers();
                }
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (button2.getText().equals(correct)) {
                    correctAnswers();
                } else {
                    wrongAnswers();
                }
            }
        });


        button3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (button3.getText().equals(correct)) {
                    correctAnswers();
                } else {
                    wrongAnswers();
                }
            }
        });

        button4.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (button4.getText().equals(correct)) {
                    correctAnswers();
                } else {
                    wrongAnswers();
                }
            }
        });
        createDialogStart();
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "NoiseAlert");
    }

    @Override
    protected void onResume() {
        super.onResume();
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
           /* if(dialogFinish.isShowing()){
                return true;
            } **/
            //Ask the user if they want to quit
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_menu_info_details)
                    .setTitle("Vill du avsluta spelet?")
                    .setMessage("Är du säker på att du vill avsluta nuvarande spel? \n\nAlla dina skatter är sparade,  men inte den nuvarande")
                    .setPositiveButton("Ja", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent homeScreen = new Intent(QuizActivity.this, StartActivity.class);
                            homeScreen.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(homeScreen);
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

    private void createDialogStart() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_quiz);
        dialog.setCanceledOnTouchOutside(false);

        //dialog.setTitle("Dialog Box");
        TextView text = (TextView) dialog.findViewById(R.id.descript_quiz);
        Button buttonOk = (Button) dialog.findViewById(R.id.buttonOK);
        //image.setImageResource(R.drawable.dialog2_bg);
        buttonOk.setOnClickListener(new View.OnClickListener() {
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
            JSONArray jArry = obj.getJSONArray(typeQuestion);
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

    private void wrongVibrate() {
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        long[] pattern = {0, 100, 100, 100, 100, 100, 100, 100, 100, 500};
        v.vibrate(pattern, -1);

    }

    private void correctSound() {
        mp = MediaPlayer.create(this, R.raw.correctsound);
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.release();
            }

        });
        mp.start();
    }

    private void wrongSound() {
        mp = MediaPlayer.create(this, R.raw.wrongsound);
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.release();
            }

        });
        mp.start();
    }

    private void wrongAnswers() {
        if (nbr_tries > 1) {
            Toast.makeText(getApplicationContext(), "Du har " + (nbr_tries - 1) + " försök kvar!", Toast.LENGTH_LONG).show();
            createJsonQuestion();
            nbr_tries--;
            ((TextView) findViewById(R.id.text_tries_left)).setText(first_half + nbr_tries + second_half);
            chests.get(nbr_tries).setImageResource(R.drawable.heart_b_w);
        } else {
            //Toast.makeText(getApplicationContext(), "Du klarade inte att öppna skatten, leta upp en ny!", Toast.LENGTH_SHORT).show();
            nbr_tries--;
            ((TextView) findViewById(R.id.text_tries_left)).setText(first_half + nbr_tries + second_half);
            chests.get(nbr_tries).setImageResource(R.drawable.heart_b_w);
            final Dialog dialogFinish =  new Dialog(this);
            dialogFinish.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialogFinish.setContentView(R.layout.dialog_quiz);
            dialogFinish.setCanceledOnTouchOutside(false);
            dialogFinish.setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        //Toast.makeText(getApplicationContext(),"Backpressed", Toast.LENGTH_SHORT).show();
                        return true;
                    }
                    return false;
                }
            });
            //dialog.setTitle("Dialog Box");
            TextView text = (TextView) dialogFinish.findViewById(R.id.descript_quiz);
            text.setText("Du svarade fel 3 gånger! \n\n" + "Rätt på senaste frågan är " + correct + "\n\n Du får får leta upp en ny skatt!" );
            Button buttonOk = (Button) dialogFinish.findViewById(R.id.buttonOK);
            //image.setImageResource(R.drawable.dialog2_bg);
            buttonOk.setOnClickListener(new View.OnClickListener() {
                public void onClick(View View3) {
                    dialogFinish.dismiss();
                    finish();
                }
            });
            dialogFinish.show();

        }
        wrongSound();
        wrongVibrate();
    }

    //Måste ändras med poängen sedan
    private void correctAnswers() {
        //Toast.makeText(getApplicationContext(), correctAnswerFirst + "100" + correctAnswerLast, Toast.LENGTH_SHORT).show();
        correctVibrate();
        correctSound();
        saveScore(100);

        //Dialog before enter next screen
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_quiz);
        dialog.setCanceledOnTouchOutside(false);

        //dialog.setTitle("Dialog Box");
        TextView text = (TextView) dialog.findViewById(R.id.descript_quiz);
        text.setText("Du svarade rätt och har nu öppnat skatten! \n\n" + correctAnswerFirst + "100" + correctAnswerLast);
        Button buttonOk = (Button) dialog.findViewById(R.id.buttonOK);
        //image.setImageResource(R.drawable.dialog2_bg);
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    //Toast.makeText(getApplicationContext(),"Backpressed", Toast.LENGTH_SHORT).show();
                    return true;
                }
                return false;
            }
        });
        buttonOk.setOnClickListener(new View.OnClickListener() {
            public void onClick(View View3) {
                dialog.dismiss();
                finish();
            }
        });
        dialog.show();

    }

    //Not in use, but nice to have
    private void makeMathQuestion() {

        Random rn = new Random();
        int answer1 = rn.nextInt(10) + 1;
        int answer2 = rn.nextInt(10) + 1;
        question.setText(answer1 + " + " + answer2 + " = ");
        int tempCorrect = answer1 + answer2;

        for (int i = 0; i < 4; i++) {
            int nbr = rn.nextInt(tempCorrect * 2);
            if (nbr != tempCorrect) {
                buttons.get(i).setText(Integer.toString(nbr));
            }
        }
        buttons.get(rn.nextInt(4)).setText(Integer.toString(tempCorrect));
        correct = String.valueOf(tempCorrect);
    }

    private void saveScore(int newScore) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        int highScore = sharedPref.getInt("high", 0);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("high",highScore+newScore);
        editor.commit();
    }
}
