package com.morrisonlive.firstabc;

import android.Manifest;
import android.annotation.SuppressLint;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.icu.text.Collator;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class NameTheLetter extends AppCompatActivity implements RecognitionListener {

    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private FindLetterViewModel model;
    private SpeechRecognizer recognizer;
    private TextView nameLetterText;
    private String selectedVoiceDir = null; //null means play the default audio shippped with the app
    private SharedPreferences sharedPref = null;
    private final static HashMap<String, String> letterMap = new HashMap<String, String> (){{
        put("hay","a");
        put("hey","a");
        put("hey hey", "a");
        put("a", "a");
        put("b", "b");
        put("be", "b");
        put("bee", "b");
        put("b", "b");
        put("c", "c");
        put("see", "c");
        put("sea", "c");
        put("ce", "c");
        put("cc", "c");
        put("d","d");
        put("di","d");
        put("dee","d");
        put("dd","d");
        put("e", "e");
        put("he","e");
        put("f","f");
        put("g","g");
        put("gee","g");
        put("gg","g");
        put("gigi","g");
        put("h","h");
        put("i","i");
        put("jay","j");
        put("j","j");
        put("je","j");
        put("jae","j");
        put("k","k");
        put("kay","k");
        put("cay","k");
        put("kaye","k");
        put("l","l");
        put("el","l");
        put("elle","l");
        put("m","m");
        put("em","m");
        put("n","n");
        put("o","o");
        put("oh","o");
        put("p","p");
        put("pee","p");
        put("pea","p");
        put("q","q");
        put("cue","q");
        put("queue","q");
        put("que","q");
        put("r","r");
        put("ar","r");
        put("are","r");
        put("s","s");
        put("es","s");
        put("t","t");
        put("tea","t");
        put("tee","t");
        put("u","u");
        put("you","u");
        put("v","v");
        put("vee","v");
        put("vie","v");
        put("vii","v");
        put("w","w");
        put("x","x");
        put("ex","x");
        put("y","y");
        put("why","y");
        put("z","z");
        put("zedd","z");
        put("zed","z");
        put("zee","z");
        put("zi","z");
    }};

    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private View mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (AUTO_HIDE) {
                        delayedHide(AUTO_HIDE_DELAY_MILLIS);
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    view.performClick();
                    break;
                default:
                    break;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_name_the_letter);
        nameLetterText = findViewById(R.id.nameLetterText);

        mVisible = true;
        mControlsView = findViewById(R.id.fullscreen_content_controls);
        mContentView = findViewById(R.id.fullscreen_content);

        // Set up the user interaction to manually show or hide the system UI.
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.

        model = new ViewModelProvider(this.getViewModelStore(), ViewModelProvider.AndroidViewModelFactory.getInstance(this.getApplication())).get(FindLetterViewModel.class);
        model.pickLetter();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_RECORD_AUDIO_PERMISSION );
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions, @NotNull int[] grantResults){
        if(grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
            finish();
        }
    }

    @Override
    protected void onStart(){
        super.onStart();

        if(!SpeechRecognizer.isRecognitionAvailable(this)) {
            Toast.makeText(this, "Speech Recognition is unavailable", Toast.LENGTH_LONG);
            finish();
        }

        if(recognizer == null) {
            recognizer = SpeechRecognizer.createSpeechRecognizer(this);
            recognizer.setRecognitionListener(this);
        }

        nameLetterText.setText(String.valueOf(model.secretLetter).toUpperCase());
        sharedPref = getSharedPreferences(getString(R.string.preference_file_key), this.MODE_PRIVATE);
        String selectedVoice = sharedPref.getString(getString(R.string.preference_selected_voice), null);

        if(selectedVoice != null && !selectedVoice.equals("Default Voice")) { //if they've selected default voice in record activity or haven't recorded anything use the shipped voice
            selectedVoiceDir = getFilesDir()+ File.separator+selectedVoice;
        }

        promptGuess();
    }

    protected void promptGuess() {
        Intent intent = RecognizerIntent.getVoiceDetailsIntent(this);
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 50);
        intent.putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE, true);
        recognizer.startListening(intent);
    }

    @Override
    protected void onStop() {
        super.onStop();
        recognizer.stopListening();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        recognizer.destroy();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(300);
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in delay milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }


    //recognition listener methods

    @Override
    public void onReadyForSpeech(Bundle params) {
        Log.println(Log.INFO, "Speech Listener", "Started Listening");
    }

    @Override
    public void onBeginningOfSpeech() {

    }

    @Override
    public void onRmsChanged(float rmsdB) {

    }

    @Override
    public void onBufferReceived(byte[] buffer) {

    }

    @Override
    public void onEndOfSpeech() {

    }

    @Override
    public void onError(int error) {
        Log.println(Log.INFO,"Error", String.valueOf(error));

        if(error == SpeechRecognizer.ERROR_RECOGNIZER_BUSY) {
            recognizer.cancel();
            playWrongGuess();
        } else if ( error == SpeechRecognizer.ERROR_NO_MATCH) {
            playWrongGuess();
        }
    }

    @Override
    public void onResults(Bundle results) {
        ArrayList<String> words = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        recognizer.stopListening();
        String letter = null;
        for(int i=0; i<words.size(); ++i) {
            Log.println(Log.INFO,"Word", words.get(i));
            if(letterMap.containsKey(words.get(i).toLowerCase())){
                letter = letterMap.get(words.get(i).toLowerCase());
                break;
            }
        }

        Log.println(Log.INFO,"Secret", String.valueOf(model.secretLetter));
        Log.println(Log.INFO,"Letter", String.valueOf(letter));

        if(letter != null) {
            int rand = (int)Math.floor(Math.random()*3);
            if(letter.equals(String.valueOf(model.secretLetter))) {
                AudioUtil.playBackLetter(Character.forDigit(rand+1,10), selectedVoiceDir, getApplicationContext());
                Intent intent = new Intent(this, CorrectAnswer.class);
                model.pickLetter();
                startActivity(intent);
            } else {
                playWrongGuess();
            }
        } else {
            playWrongGuess();
        }

    }

    private void playWrongGuess(){
        int rand = (int)Math.floor(Math.random()*3);
        MediaPlayer mPlayer = new MediaPlayer();
        mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
            }
        });

        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                promptGuess();
            }
        });

        String clipName = null;
        Uri clipNameURI = null;
        Character clipNumber = Character.forDigit(rand+4,10);
        if (selectedVoiceDir == null) {
            clipNameURI = Uri.parse("android.resource://com.morrisonlive.firstabc/raw/letter_"+clipNumber+"_default");
            try {
                mPlayer.setDataSource(getApplicationContext(), clipNameURI);
            } catch (Exception ex) {
                //do nothing
            }
        } else {
            clipName = selectedVoiceDir + File.separator + "letter_" + clipNumber + ".3gp";
            try {
                mPlayer.setDataSource(clipName);
            } catch (Exception ex){
                //do nothing
            }
        }
        mPlayer.prepareAsync();
    }


    @Override
    public void onPartialResults(Bundle partialResults) {
    }

    @Override
    public void onEvent(int eventType, Bundle params) {

    }


}