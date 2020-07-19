package com.morrisonlive.firstabc;

import androidx.lifecycle.ViewModel;

import java.util.Random;

public class FindLetterViewModel extends ViewModel {
    public char secretLetter;
    public Boolean autoPlayed;
    private Random rand = new Random();

    public FindLetterViewModel() {
        super();
        pickLetter();
    }

    public void pickLetter() {
        autoPlayed = false;
        int i = rand.nextInt(26);
        secretLetter = RecordLettersActivity.letters[i];
    }

}
