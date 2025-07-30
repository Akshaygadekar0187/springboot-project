package com.akshayexpress.guesstheword.guess_the_word.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;


import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.akshayexpress.guesstheword.guess_the_word.services.GameService;
import com.akshayexpress.guesstheword.guess_the_word.utils.GameUtils;

@Controller
public class GameController {

    @Autowired
    private GameService gameService;

    @Autowired
    private GameUtils gameUtils;

    @GetMapping("/game-home")
    public String showGameHomePage(Model model) {
        initializeGame(model);
        return "game-home-page";
    }

    @GetMapping("/guess")
    public String handleGuess(@RequestParam("letter") char letter, Model model) {
        boolean isCorrect = gameService.guessCharacter(letter); // Check the guessed letter
        boolean isComplete = gameService.isWordComplete(); // Check if the word is complete

        if (isComplete) {
            model.addAttribute("wordToDisplay", gameService.getMaskedWord());
            model.addAttribute("guessResult", "🎉 Congratulations! You guessed the word!");
            model.addAttribute("isComplete", true);
            model.addAttribute("remainingTries", gameUtils.getTriesRemaining());
            gameService.startNewGame(); // Reset game with a new word
            gameUtils.resetTries(); // Reset tries for the new word
            return "game-home-page";
        }

        if (!isCorrect) {
            gameUtils.reduceTry(); // Deduct a try for incorrect guesses
        }

        if (!gameUtils.hasTriesLeft()) {
            model.addAttribute("wordToDisplay", gameService.getMaskedWord());
            model.addAttribute("guessResult", "❌ Out of tries! The word was: " + gameService.getActualWord());
            model.addAttribute("isComplete", true);
            gameService.startNewGame(); // Reset game with a new word
            gameUtils.resetTries(); // Reset tries for the new word
            return "game-home-page";
        }

        // Update model for ongoing game
        updateModelForOngoingGame(model, isCorrect);
        return "game-home-page";
    }
    
    @GetMapping("/hint")
    public String provideHint(Model model) {
        String hint = gameService.provideHint();
        gameUtils.reduceTry(); // Reduce tries as a penalty for using a hint

        model.addAttribute("wordToDisplay", gameService.getMaskedWord());
        model.addAttribute("guessResult", hint);
        model.addAttribute("remainingTries", gameUtils.getTriesRemaining());
        model.addAttribute("isComplete", gameService.isWordComplete());

        return "game-home-page";
    }
 
    

    private void initializeGame(Model model) {
        gameService.startNewGame(); // Reset the word and guessed characters
        gameUtils.resetTries(); // Reset the number of tries
        model.addAttribute("wordToDisplay", gameService.getMaskedWord());
        model.addAttribute("guessResult", ""); // No guess result initially
        model.addAttribute("isComplete", false); // Reset completion status
        model.addAttribute("remainingTries", gameUtils.getTriesRemaining());
    }

    private void updateModelForOngoingGame(Model model, boolean isCorrect) {
        model.addAttribute("wordToDisplay", gameService.getMaskedWord());
        model.addAttribute("guessResult", isCorrect ? "Correct guess!" : "Wrong guess!");
        model.addAttribute("isComplete", false);
        model.addAttribute("remainingTries", gameUtils.getTriesRemaining());
    }
    
    @GetMapping("/reload")
    public String reloadGame() {
    	
    	gameService = gameUtils.reload();
    	
    	gameUtils.resetTries();
    	
    	return "redirect:/game-home";
    }
}
