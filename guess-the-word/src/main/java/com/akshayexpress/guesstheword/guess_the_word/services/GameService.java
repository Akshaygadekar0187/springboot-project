package com.akshayexpress.guesstheword.guess_the_word.services;
import java.util.Random;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("prototype")
public class GameService {
	

    private String randomlyChosenWord; // The word to guess
    private char[] guessedCharacters; // Array to track guessed letters
    private int score; // Track the player's score
    private Random random = new Random();

    public GameService() {
        startNewGame();
    }

    private String pickRandomWord() {
        String[] randomWords = { "father", "mother", "sister", "brother", "hello", "home", "technology", "science" };
        return randomWords[random.nextInt(randomWords.length)];
    }

    public void startNewGame() {
        randomlyChosenWord = pickRandomWord(); // Pick a new random word
        guessedCharacters = new char[randomlyChosenWord.length()]; // Reset guessed characters
        score = 0; // Reset the score
        System.out.println("New word: " + randomlyChosenWord); // Debugging
    }

    public String getMaskedWord() {
        StringBuilder maskedWord = new StringBuilder();

        for (int i = 0; i < guessedCharacters.length; i++) {
            if (guessedCharacters[i] == '\u0000') { // If not guessed, show underscore
                maskedWord.append("_");
            } else {
                maskedWord.append(guessedCharacters[i]); // Show the guessed character
            }
            maskedWord.append(" "); // Add space for readability
        }

        return maskedWord.toString().trim();
    }

    public boolean guessCharacter(char guessedChar) {
        boolean isCorrect = false;

        for (int i = 0; i < randomlyChosenWord.length(); i++) {
            if (randomlyChosenWord.charAt(i) == guessedChar) {
                guessedCharacters[i] = guessedChar;
                isCorrect = true;
            }
        }

        if (isCorrect) {
            score += 10; // Increase score for correct guesses
        } else {
            score -= 2; // Penalize wrong guesses
        }

        return isCorrect;
    }

    public boolean isWordComplete() {
        for (char c : guessedCharacters) {
            if (c == '\u0000') {
                return false;
            }
        }
        return true;
    }

    public int getScore() {
        return score;
    }

    public String getActualWord() {
        return randomlyChosenWord;
    }

    public String provideHint() {
        for (int i = 0; i < guessedCharacters.length; i++) {
            if (guessedCharacters[i] == '\u0000') {
                guessedCharacters[i] = randomlyChosenWord.charAt(i); // Reveal one letter
                return "Hint: The letter '" + randomlyChosenWord.charAt(i) + "' was revealed!";
            }
        }
        return "No more hints available!";
    }
}
