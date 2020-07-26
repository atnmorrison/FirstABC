package com.morrisonlive.firstabc;

import android.media.MediaPlayer;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class AudioUtil {

    private static MediaPlayer mPlayer;
    private static ArrayList<String> files = new ArrayList<>();

    public static void playFindLetter(char c, String filesDir) {

        String selectedVoice = "default";
        String findClip = filesDir+"letter_0.3gp";
        final String clipName = filesDir+"letter_"+c+".3gp";

        MediaPlayer playFindLetter = new MediaPlayer();
        try {
            playFindLetter.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    Log.e("Audio Util", "starting play");
                    mp.start();
                }

            });

            playFindLetter.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer mp) {
                    mp.release();

                    try {
                        if(mPlayer == null) {
                            initLetterPlayer();
                        } else {
                            mPlayer.stop();
                            mPlayer.reset();
                        }
                        mPlayer.setDataSource(clipName);
                        mPlayer.prepareAsync();
                    } catch (Exception ex) {
                        Log.e("Audio Util", "peropare failed", ex);
                    }

                }


            });


            playFindLetter.setDataSource(findClip);
            playFindLetter.prepareAsync();

        } catch (Exception ex) {
            Log.e("Audio Util", "peropare failed", ex);
        }
    }


    public static void initLetterPlayer() {
        mPlayer = new MediaPlayer();
        mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mPlayer.start();
            }
        });

        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if(files.size() > 0) {
                    String nextClip = files.remove(0);
                    try {
                        mPlayer.stop();
                        mPlayer.reset();
                        mPlayer.setDataSource(nextClip);
                        mPlayer.prepareAsync();
                    } catch(Exception e) {
                        Log.e("Audio Util", "peropare failed", e);
                    }

                }
            }
        });
    }



    public static void playBackLetter(char c, String filesDir) {

        String clipName = filesDir+"letter_"+c+".3gp";

        if(mPlayer == null) {
            initLetterPlayer();
        } else {
            if(mPlayer.isPlaying()){
                files.add(clipName);
            } else {
                try {
                    mPlayer.reset();
                    mPlayer.setDataSource(clipName);
                    mPlayer.prepareAsync();
                } catch (IOException e) {
                    Log.e("Audio Util", "peropare failed", e);
                }
            }
        }


    }



}
