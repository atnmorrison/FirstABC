package com.morrisonlive.firstabc;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import java.util.Objects;

public class FindLettersActivity extends AppCompatActivity {

    Button playButton;
    FindLetterViewModel model;


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_letters);

        model = new ViewModelProvider(this.getViewModelStore(), ViewModelProvider.AndroidViewModelFactory.getInstance(this.getApplication())).get(FindLetterViewModel.class);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        playButton = findViewById(R.id.letterPlayButton);

        playButton.setOnClickListener(new Button.OnClickListener(){

            @Override
            public void onClick(View arg0) {
                Log.println(Log.DEBUG, "Find Letters", "Playing Letter "+model.secretLetter);
                AudioUtil.playFindLetter(model.secretLetter,getFilesDir());
            }

        });

        setupLetterButtons();
    }

    @Override
    protected  void onStart() {
        super.onStart();
        if(!model.autoPlayed) {
            AudioUtil.playFindLetter(model.secretLetter, getFilesDir());
            model.autoPlayed = true;
        }
    }

    private void setupLetterButtons(){

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

        final FindLettersActivity self = this;
        for(char c: RecordLettersActivity.letters){
            String upper = String.valueOf(c).toUpperCase();
            final TextView letter = new TextView(getApplicationContext());

            letter.setText(upper);
            letter.setTextColor(getResources().getColor(R.color.purple));
            letter.setTextSize(TypedValue.COMPLEX_UNIT_DIP, fontsize);
            letter.setGravity(Gravity.CENTER);
            letter.setOnClickListener(new TextView.OnClickListener(){
                @Override
                public void onClick(View arg0){
                    grow.setZAdjustment(Animation.ZORDER_TOP);
                    if(letter.getText().equals(String.valueOf(model.secretLetter).toUpperCase())) {
                        letter.setTextColor(getResources().getColor(R.color.green));
                        AudioUtil.playBackLetter('1', getFilesDir());
                        Intent intent = new Intent(self, CorrectAnswer.class);
                        startActivity(intent);
                    } else {
                        letter.setTextColor(getResources().getColor(R.color.red));
                        letter.startAnimation(grow);
                        AudioUtil.playBackLetter('2', getFilesDir());
                        model.pickLetter();
                    }
                }
            });
            letters[index] = letter;
            ++index;
        }

        for(int i=0; i<26; ++i) {
            int row;
            if(i == 0) {
                row = 0;
            } else {
                row = (int) Math.floor((double) i / (double) columns);
            }
            rows[row].addView(letters[i]);
        }

    }



}
