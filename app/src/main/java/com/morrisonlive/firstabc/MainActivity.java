package com.morrisonlive.firstabc;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;



public class MainActivity extends AppCompatActivity {

    String selectedVoiceDir = null;
    SharedPreferences sharedPref = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupLetterButtons();
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadPrefrences();
    }

    private void loadPrefrences() {

        sharedPref = getSharedPreferences(getString(R.string.preference_file_key), this.MODE_PRIVATE);
        String selectedVoice = sharedPref.getString(getString(R.string.preference_selected_voice), null);

        if(selectedVoice == null) {
            Log.println(Log.INFO, "Selected Voice", "null");
        } else {
            Log.println(Log.INFO, "Selected Voice", selectedVoice);
        }


        if(selectedVoice != null && !selectedVoice.equals("Default Voice")) {
            selectedVoiceDir = getFilesDir()+File.separator+selectedVoice;
        } else {
            selectedVoiceDir = null;
        }

    }

    private void setupLetterButtons(){

        final Animation rotate = AnimationUtils.loadAnimation(this, R.anim.rotate);
        final Animation grow = AnimationUtils.loadAnimation(this, R.anim.grow);

        TableRow[] rows = new TableRow[7];
        rows[0] = findViewById(R.id.row1);
        rows[1] = findViewById(R.id.row2);
        rows[2] = findViewById(R.id.row3);
        rows[3] = findViewById(R.id.row4);
        rows[4] = findViewById(R.id.row5);
        rows[5] = findViewById(R.id.row6);
        rows[6] = findViewById(R.id.row7);

        TextView[] letters = new TextView[26];

        int index = 0;
        int columns;
        int fontsize;

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            columns = 7;
            fontsize = 40;
        } else {
            columns = 4;
            fontsize = 50;
        }

        for(char c: RecordLettersActivity.letters){
            String upper = String.valueOf(c).toUpperCase();
            final char mychar = c;
            final TextView letter = new TextView(getApplicationContext());

            letter.setText(upper);
            letter.setBackgroundColor(getResources().getColor(R.color.transparent));
            letter.setTextColor(getResources().getColor(R.color.purple));
            letter.setTextSize(TypedValue.COMPLEX_UNIT_DIP, fontsize);
            letter.setGravity(Gravity.CENTER);

            letter.setOnClickListener(new TextView.OnClickListener(){
                @Override
                public void onClick(View arg0){

                    double value = Math.random();
                    rotate.setZAdjustment(Animation.ZORDER_TOP);
                    grow.setZAdjustment(Animation.ZORDER_TOP);

                    if(value > 0.5) {
                        letter.startAnimation(grow);
                    } else {
                        letter.startAnimation(rotate);
                    }

                    if(String.valueOf(letter.getText()).equals(String.valueOf(letter.getText()).toUpperCase())) {
                        letter.setText(String.valueOf(letter.getText()).toLowerCase());
                    } else {
                        letter.setText(String.valueOf(letter.getText()).toUpperCase());
                    }


                    int red = (int)Math.floor(Math.random()*255);
                    int green = (int)Math.floor(Math.random() * 255);
                    int blue = (int)Math.floor(Math.random()*255);

                    letter.setTextColor(Color.argb(255, red, green, blue));
                    AudioUtil.playBackLetter(mychar, selectedVoiceDir, getApplicationContext());

                }
            });
            letters[index] = letter;
            ++index;
        }

        for(int i=0; i<26; ++i) {
            int row = (int) Math.floor((double)i/(double)columns);
            rows[row].addView(letters[i]);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.find_letters:
                startFindLettersActivity();
                return true;
            case R.id.record_letters:
                startRecordActivity();
                return true;
            case R.id.name_letters:
                startNameTheLetter();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void startSettingsActivity() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }



    private void startFindLettersActivity() {
        Intent intent = new Intent(this, FindLettersActivity.class);
        startActivity(intent);
    }


    private void startRecordActivity() {
        Intent intent = new Intent(this, RecordLettersActivity.class);
        startActivity(intent);
    }

    private void startNameTheLetter() {
        Intent intent = new Intent(this, NameTheLetter.class);
        startActivity(intent);
    }

}
