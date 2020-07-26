package com.morrisonlive.firstabc;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.DataSetObserver;
import android.media.MediaRecorder;
import android.media.audiofx.NoiseSuppressor;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NavUtils;
import androidx.core.content.ContextCompat;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class RecordLettersActivity extends AppCompatActivity {

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private View mContentView;
    private Spinner voiceOptions;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
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


    private MediaRecorder mRecorder;

    private Button aButton;
    public static char[] letters = {'a', 'b', 'c', 'd', 'e', 'f', 'g',
            'h', 'i', 'j', 'k', 'l', 'm', 'n',
            'o', 'p', 'q', 'r', 's', 't', 'u',
            'v', 'w', 'x', 'y', 'z'};

    public static String[] phrases = {"Find the letter", "That's correct!", "Nice Try!"};

    protected static final String LOG_TAG = "RecordLettersActivity";
    private boolean recording;
    private TextView instructions;
    private TextView letter;
    private int characterIndex;
    private int phraseIndex;
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_record_letters);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mVisible = true;
        mControlsView = findViewById(R.id.fullscreen_content_controls);
        mContentView = findViewById(R.id.recordContainer);
        voiceOptions = findViewById(R.id.selectedVoice);


        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item);
        adapter.add("New Voice");
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        voiceOptions.setAdapter(adapter);


        mRecorder = new MediaRecorder();


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

        instructions = this.findViewById(R.id.instructions);
        letter	 =  this.findViewById(R.id.letter);
        letter.setVisibility(View.INVISIBLE);

        aButton = findViewById(R.id.record_button);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {



            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_RECORD_AUDIO_PERMISSION );

        } else {
            setupRecordButtons();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions, @NotNull int[] grantResults){
        setupRecordButtons();
    }

    private void setupRecordButtons() {

        aButton.setOnClickListener(new Button.OnClickListener(){

            @Override
            public void onClick(View v) {
                aButton.setEnabled(false);
                instructions.setVisibility(View.INVISIBLE);
                letter.setVisibility(View.VISIBLE);
                recording = false;
                characterIndex = 0;

                new CountDownTimer(1000 * (letters.length+1), 1000) {

                    @Override
                    public void onTick(long elapsed) {

                        try {
                            if (recording) {
                                stopRecording();
                            }

                            char c = letters[characterIndex];
                            letter.setText(String.valueOf(c).toUpperCase());
                            ++characterIndex;

                            startRecording(c);
                        } catch (Exception ex) {
                            //ignore
                        }

                        //don't do anything yet, will probably make it do something with a recording indicator
                    }

                    @Override
                    public void onFinish() {
                        try {
                            stopRecording();
                        } catch (Exception ex) {
                            //ignore
                        }
                        phraseIndex = 0;
                        new CountDownTimer(2000 * (phrases.length+1), 2000) {

                            @Override
                            public void onTick(long elapses) {
                                try {
                                    if (recording) {
                                        stopRecording();
                                    }

                                    letter.setTextSize(TypedValue.COMPLEX_UNIT_SP, 40);
                                    letter.setText(phrases[phraseIndex]);
                                    startRecording(Character.forDigit(phraseIndex, 10));
                                    ++phraseIndex;
                                } catch (Exception ex) {
                                    //ignore
                                }
                            }

                            @Override
                            public void onFinish() {

                                try {
                                    stopRecording();
                                } catch(Exception ignored) {

                                }

                                instructions.setVisibility(View.VISIBLE);
                                letter.setVisibility(View.INVISIBLE);
                                letter.setTextSize(TypedValue.COMPLEX_UNIT_SP, 200);
                                characterIndex = 0;
                                letter.setText("");
                                aButton.setEnabled(true);

                            }
                        }.start();

                    }


                }.start();

            }

        });
    }

    private void startRecording(char c) {
        recording = true;
        mRecorder.setAudioSource(MediaRecorder.AudioSource.VOICE_COMMUNICATION);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);

        try{
            //the voice that is being recorded
            String selectedVoice = "default";
            File selectedVoiceDir = this.getDir(selectedVoice, this.MODE_PRIVATE);
            mRecorder.setOutputFile(selectedVoiceDir.getPath() +"letter_"+c+".3gp");
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mRecorder.prepare();
            mRecorder.start();
        } catch(IOException e) {
            Log.e(LOG_TAG, "prepare() failed: "+e.getMessage());
        }
    }

    private void stopRecording() {
        recording = false;
        mRecorder.stop();
        mRecorder.reset();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button.
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRecorder.release();
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
    private void delayedHide() {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, 100);
    }
}
