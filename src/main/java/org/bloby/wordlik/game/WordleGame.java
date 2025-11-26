package org.bloby.wordlik.game;

import org.bloby.wordlik.utils.WordList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class WordleGame {
    
    private final String targetWord;
    private final WordList wordList;
    private final int maxAttempts;
    private final List<String> guesses;
    private final List<GuessResult> results;
    private final Set<String> guessedWords;
    private final Map<Character, GuessResult.LetterStatus> letterStatuses;
    private int attempts;
    private boolean finished;
    private long startTime;
    private boolean won;
    
    public WordleGame(String targetWord, WordList wordList) {
        this(targetWord, wordList, 6);
    }
    
    public WordleGame(String targetWord, WordList wordList, int maxAttempts) {
        this.targetWord = targetWord.toLowerCase();
        this.wordList = wordList;
        this.maxAttempts = maxAttempts;
        this.guesses = new ArrayList<>();
        this.results = new ArrayList<>();
        this.guessedWords = new HashSet<>();
        this.letterStatuses = new HashMap<>();
        this.attempts = 0;
        this.finished = false;
        this.startTime = System.currentTimeMillis();
        this.won = false;
    }
    
    public GuessResult makeGuess(String guess) {
        guess = guess.toLowerCase();
        
        if (finished) {
            return new GuessResult(guess, GuessResult.Status.GAME_OVER, null);
        }
        
        if (guess.length() != 5) {
            return new GuessResult(guess, GuessResult.Status.INVALID_LENGTH, null);
        }
        
        if (!wordList.isValidWord(guess)) {
            return new GuessResult(guess, GuessResult.Status.INVALID_WORD, null);
        }
        
        if (guessedWords.contains(guess)) {
            return new GuessResult(guess, GuessResult.Status.ALREADY_GUESSED, null);
        }
        
        attempts++;
        guesses.add(guess);
        guessedWords.add(guess);
        
        GuessResult.LetterStatus[] feedback = evaluateGuess(guess);
        updateLetterStatuses(guess, feedback);
        
        GuessResult.Status status;
        if (guess.equals(targetWord)) {
            status = GuessResult.Status.CORRECT;
            finished = true;
            won = true;
        } else if (attempts >= maxAttempts) {
            status = GuessResult.Status.GAME_OVER;
            finished = true;
        } else {
            status = GuessResult.Status.WRONG;
        }
        
        GuessResult result = new GuessResult(guess, status, feedback, attempts, maxAttempts - attempts, targetWord);
        results.add(result);
        
        return result;
    }
    
    private GuessResult.LetterStatus[] evaluateGuess(String guess) {
        GuessResult.LetterStatus[] feedback = new GuessResult.LetterStatus[5];
        char[] targetChars = targetWord.toCharArray();
        char[] guessChars = guess.toCharArray();
        boolean[] targetUsed = new boolean[5];
        boolean[] guessUsed = new boolean[5];
        
        for (int i = 0; i < 5; i++) {
            if (guessChars[i] == targetChars[i]) {
                feedback[i] = GuessResult.LetterStatus.CORRECT;
                targetUsed[i] = true;
                guessUsed[i] = true;
            }
        }
        
        for (int i = 0; i < 5; i++) {
            if (guessUsed[i]) {
                continue;
            }
            
            boolean found = false;
            for (int j = 0; j < 5; j++) {
                if (!targetUsed[j] && guessChars[i] == targetChars[j]) {
                    feedback[i] = GuessResult.LetterStatus.PRESENT;
                    targetUsed[j] = true;
                    found = true;
                    break;
                }
            }
            
            if (!found) {
                feedback[i] = GuessResult.LetterStatus.ABSENT;
            }
        }
        
        return feedback;
    }
    
    private void updateLetterStatuses(String guess, GuessResult.LetterStatus[] feedback) {
        for (int i = 0; i < guess.length(); i++) {
            char letter = guess.charAt(i);
            GuessResult.LetterStatus currentStatus = letterStatuses.get(letter);
            GuessResult.LetterStatus newStatus = feedback[i];
            
            if (currentStatus == null || shouldUpdateStatus(currentStatus, newStatus)) {
                letterStatuses.put(letter, newStatus);
            }
        }
    }
    
    private boolean shouldUpdateStatus(GuessResult.LetterStatus existing, GuessResult.LetterStatus newStatus) {
        if (newStatus == GuessResult.LetterStatus.CORRECT) {
            return true;
        }
        if (newStatus == GuessResult.LetterStatus.PRESENT && existing != GuessResult.LetterStatus.CORRECT) {
            return true;
        }
        return false;
    }
    
    public String getTargetWord() {
        return targetWord;
    }
    
    public int getAttempts() {
        return attempts;
    }
    
    public int getMaxAttempts() {
        return maxAttempts;
    }
    
    public boolean isFinished() {
        return finished;
    }
    
    public boolean hasWon() {
        return won;
    }
    
    public long getStartTime() {
        return startTime;
    }
    
    public List<String> getGuesses() {
        return new ArrayList<>(guesses);
    }
    
    public List<GuessResult> getResults() {
        return new ArrayList<>(results);
    }
    
    public Map<Character, GuessResult.LetterStatus> getLetterStatuses() {
        return new HashMap<>(letterStatuses);
    }
    
    public int getRemainingAttempts() {
        return maxAttempts - attempts;
    }
    
    public long getPlayTime() {
        return System.currentTimeMillis() - startTime;
    }
    
    public boolean hasGuessed(String word) {
        return guessedWords.contains(word.toLowerCase());
    }
}
