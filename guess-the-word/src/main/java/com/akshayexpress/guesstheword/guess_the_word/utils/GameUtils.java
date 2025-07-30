package com.akshayexpress.guesstheword.guess_the_word.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Service;

import com.akshayexpress.guesstheword.guess_the_word.services.GameService;

@Service
public class GameUtils {

    @Autowired
    ConfigurableApplicationContext applicationContext;

    private static final int MAX_TRIES = 5; // Maximum number of tries
    private int triesRemaining;

    public GameUtils() {
        resetTries(); // Initialize tries when the object is created
    }

    // Reduce tries but ensure it does not go below zero
    public int reduceTry() {
        if (triesRemaining > 0) {
            triesRemaining -= 1;
        }
        return triesRemaining;
    }

    // Get the number of tries remaining
    public int getTriesRemaining() {
        return triesRemaining;
    }

    // Check if there are tries left
    public boolean hasTriesLeft() {
        return triesRemaining > 0;
    }

    // Reset tries to the maximum allowed value
    public void resetTries() {
        triesRemaining = MAX_TRIES;
    }

    // Reload the GameService bean
    public GameService reload() {
        return applicationContext.getBean(GameService.class);
    }
}
