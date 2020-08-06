package com.morrisonlive.firstabc;

import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;


public class LetterRecognitionListener implements RecognitionListener {

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

    }

    @Override
    public void onResults(Bundle results) {
        ArrayList<String> words = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

        String letter = null;
        for(int i=0; i<words.size(); ++i) {
            if(letterMap.containsKey(words.get(i))){
                letter = letterMap.get(words.get(i));
                break;
            }
        }

    }

    @Override
    public void onPartialResults(Bundle partialResults) {
    }

    @Override
    public void onEvent(int eventType, Bundle params) {

    }
}
