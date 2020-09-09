package com.morrisonlive.firstabc;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class AudioUtil {

    private static MediaPlayer mPlayer;
    private static MediaPlayer playFindLetter;
    private static ArrayList<String> files = new ArrayList<>();
    private static String clipName;
    private static Uri clipNameURI;
    private static Context context;

    public static void playFindLetter(char c, String filesDir, Context appContext) {

        context = appContext;

        String findClip = null;
        clipName = null;
        Uri findClipURI = null;
        clipNameURI = null;
        if(filesDir == null) {
            findClipURI = Uri.parse("android.resource://com.morrisonlive.firstabc/raw/letter_0_default");
            clipNameURI = Uri.parse("android.resource://com.morrisonlive.firstabc/raw/letter_"+c+"_default");
        } else {
            findClip = filesDir+File.separator+"letter_0.3gp";
            clipName = filesDir+File.separator+"letter_"+c+".3gp";
        }

        try {
            if(playFindLetter == null) {
                playFindLetter = new MediaPlayer();
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
                        try {
                            if (mPlayer == null) {
                                initLetterPlayer();
                            } else {
                                mPlayer.stop();
                                mPlayer.reset();
                            }
                            if(clipName != null) {
                                mPlayer.setDataSource(clipName);
                            } else {
                                mPlayer.setDataSource(context, clipNameURI);
                            }
                            mPlayer.prepareAsync();
                        } catch (Exception ex) {
                            Log.e("Audio Util", "peropare failed", ex);
                        }

                    }


                });
            } else {
                playFindLetter.stop();
                playFindLetter.reset();
            }

            if(findClip != null) {
                playFindLetter.setDataSource(findClip);
            } else {

                Log.println(Log.INFO, "context", context.toString());
                Log.println(Log.INFO, "clip", findClipURI.toString());

                playFindLetter.setDataSource(context, findClipURI);
            }
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

    public static void playBackLetter(char c, String filesDir, Context context) {

        String clipName = null;
        Uri clipNameURI = null;
        if (filesDir == null) {
            clipNameURI = Uri.parse("android.resource://com.morrisonlive.firstabc/raw/letter_"+c+"_default");
        } else {
            clipName = filesDir + File.separator + "letter_" + c + ".3gp";
        }

        if(mPlayer == null) {
            initLetterPlayer();
        }

        if(mPlayer.isPlaying()){
            mPlayer.stop();
        }

        try {
            mPlayer.reset();
            if(clipName != null) {
                mPlayer.setDataSource(clipName);
            } else {
                mPlayer.setDataSource(context, clipNameURI);
            }
            mPlayer.prepareAsync();
        } catch (IOException e) {
            Log.e("Audio Util", "peropare failed", e);
        }
    }

}
