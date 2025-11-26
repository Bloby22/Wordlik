package org.bloby.wordlik.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GuessResult {
    
    public enum Status {
        CORRECT,
        WRONG,
        GAME_OVER,
        INVALID_LENGTH,
        INVALID_WORD,
        ALREADY_GUESSED,
        GAME_PAUSED,
        NO_ACTIVE_GAME
    }
    
    public enum LetterStatus {
        CORRECT,
        PRESENT,
        ABSENT,
        UNKNOWN
    }
    
    private final String guess;
    private final Status status;
    private final LetterStatus[] feedback;
    private final int attemptNumber;
    private final int remainingAttempts;
    private final long timestamp;
    private final Map<Character, LetterStatus> letterSummary;
    private final List<Integer> correctPositions;
    private final List<Integer> presentPositions;
    private final List<Integer> absentPositions;
    private final String targetWord;
    private final boolean isWinningGuess;
    private final double accuracy;
    
    public GuessResult(String guess, Status status, LetterStatus[] feedback) {
        this(guess, status, feedback, 0, 0, null);
    }
    
    public GuessResult(String guess, Status status, LetterStatus[] feedback, int attemptNumber, int remainingAttempts, String targetWord) {
        this.guess = guess;
        this.status = status;
        this.feedback = feedback;
        this.attemptNumber = attemptNumber;
        this.remainingAttempts = remainingAttempts;
        this.timestamp = System.currentTimeMillis();
        this.targetWord = targetWord;
        this.isWinningGuess = status == Status.CORRECT;
        this.letterSummary = new HashMap<>();
        this.correctPositions = new ArrayList<>();
        this.presentPositions = new ArrayList<>();
        this.absentPositions = new ArrayList<>();
        
        if (feedback != null) {
            analyzeFeedback();
            this.accuracy = calculateAccuracy();
        } else {
            this.accuracy = 0.0;
        }
    }
    
    private void analyzeFeedback() {
        if (feedback == null || guess == null) {
            return;
        }
        
        char[] letters = guess.toCharArray();
        
        for (int i = 0; i < feedback.length && i < letters.length; i++) {
            char letter = letters[i];
            LetterStatus currentStatus = feedback[i];
            
            LetterStatus existingStatus = letterSummary.get(letter);
            if (existingStatus == null || shouldUpdateStatus(existingStatus, currentStatus)) {
                letterSummary.put(letter, currentStatus);
            }
            
            switch (currentStatus) {
                case CORRECT:
                    correctPositions.add(i);
                    break;
                case PRESENT:
                    presentPositions.add(i);
                    break;
                case ABSENT:
                    absentPositions.add(i);
                    break;
            }
        }
    }
    
    private boolean shouldUpdateStatus(LetterStatus existing, LetterStatus newStatus) {
        if (newStatus == LetterStatus.CORRECT) {
            return true;
        }
        if (newStatus == LetterStatus.PRESENT && existing != LetterStatus.CORRECT) {
            return true;
        }
        return false;
    }
    
    private double calculateAccuracy() {
        if (feedback == null || feedback.length == 0) {
            return 0.0;
        }
        
        int correctCount = 0;
        int presentCount = 0;
        
        for (LetterStatus status : feedback) {
            if (status == LetterStatus.CORRECT) {
                correctCount++;
            } else if (status == LetterStatus.PRESENT) {
                presentCount++;
            }
        }
        
        return ((correctCount * 1.0) + (presentCount * 0.5)) / feedback.length * 100;
    }
    
    public String getGuess() {
        return guess;
    }
    
    public Status getStatus() {
        return status;
    }
    
    public LetterStatus[] getFeedback() {
        return feedback;
    }
    
    public int getAttemptNumber() {
        return attemptNumber;
    }
    
    public int getRemainingAttempts() {
        return remainingAttempts;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    
    public Map<Character, LetterStatus> getLetterSummary() {
        return new HashMap<>(letterSummary);
    }
    
    public List<Integer> getCorrectPositions() {
        return new ArrayList<>(correctPositions);
    }
    
    public List<Integer> getPresentPositions() {
        return new ArrayList<>(presentPositions);
    }
    
    public List<Integer> getAbsentPositions() {
        return new ArrayList<>(absentPositions);
    }
    
    public String getTargetWord() {
        return targetWord;
    }
    
    public boolean isWinningGuess() {
        return isWinningGuess;
    }
    
    public double getAccuracy() {
        return accuracy;
    }
    
    public int getCorrectLetterCount() {
        return correctPositions.size();
    }
    
    public int getPresentLetterCount() {
        return presentPositions.size();
    }
    
    public int getAbsentLetterCount() {
        return absentPositions.size();
    }
    
    public boolean isValidGuess() {
        return status != Status.INVALID_LENGTH && 
               status != Status.INVALID_WORD && 
               status != Status.ALREADY_GUESSED &&
               status != Status.NO_ACTIVE_GAME &&
               status != Status.GAME_PAUSED;
    }
    
    public boolean isGameEnding() {
        return status == Status.CORRECT || status == Status.GAME_OVER;
    }
    
    public String getFormattedGuess() {
        if (guess == null) {
            return "";
        }
        return guess.toUpperCase();
    }
    
    public String getColoredGuess() {
        if (feedback == null || guess == null) {
            return guess;
        }
        
        StringBuilder colored = new StringBuilder();
        char[] letters = guess.toUpperCase().toCharArray();
        
        for (int i = 0; i < letters.length && i < feedback.length; i++) {
            switch (feedback[i]) {
                case CORRECT:
                    colored.append("§a").append(letters[i]);
                    break;
                case PRESENT:
                    colored.append("§e").append(letters[i]);
                    break;
                case ABSENT:
                    colored.append("§8").append(letters[i]);
                    break;
                default:
                    colored.append("§7").append(letters[i]);
                    break;
            }
        }
        
        return colored.toString();
    }
    
    public List<Character> getCorrectLetters() {
        List<Character> correct = new ArrayList<>();
        if (guess == null || feedback == null) {
            return correct;
        }
        
        char[] letters = guess.toCharArray();
        for (int i = 0; i < letters.length && i < feedback.length; i++) {
            if (feedback[i] == LetterStatus.CORRECT) {
                correct.add(letters[i]);
            }
        }
        return correct;
    }
    
    public List<Character> getPresentLetters() {
        List<Character> present = new ArrayList<>();
        if (guess == null || feedback == null) {
            return present;
        }
        
        char[] letters = guess.toCharArray();
        for (int i = 0; i < letters.length && i < feedback.length; i++) {
            if (feedback[i] == LetterStatus.PRESENT) {
                present.add(letters[i]);
            }
        }
        return present;
    }
    
    public List<Character> getAbsentLetters() {
        List<Character> absent = new ArrayList<>();
        if (guess == null || feedback == null) {
            return absent;
        }
        
        char[] letters = guess.toCharArray();
        for (int i = 0; i < letters.length && i < feedback.length; i++) {
            if (feedback[i] == LetterStatus.ABSENT) {
                absent.add(letters[i]);
            }
        }
        return absent;
    }
    
    public String getStatusMessage() {
        switch (status) {
            case CORRECT:
                return "§a§lVÝHRA! Správné slovo!";
            case WRONG:
                return "§eŠpatně, zkus to znovu!";
            case GAME_OVER:
                return "§c§lPROHRA! Vyčerpal jsi všechny pokusy!";
            case INVALID_LENGTH:
                return "§cSlovo musí mít přesně 5 písmen!";
            case INVALID_WORD:
                return "§cToto slovo není v seznamu!";
            case ALREADY_GUESSED:
                return "§cToto slovo jsi už hádal!";
            case GAME_PAUSED:
                return "§eHra je pozastavena!";
            case NO_ACTIVE_GAME:
                return "§cNemáš aktivní hru!";
            default:
                return "§7Neznámý stav";
        }
    }
    
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("guess", guess);
        map.put("status", status.name());
        map.put("attemptNumber", attemptNumber);
        map.put("remainingAttempts", remainingAttempts);
        map.put("timestamp", timestamp);
        map.put("isWinning", isWinningGuess);
        map.put("accuracy", accuracy);
        map.put("correctCount", getCorrectLetterCount());
        map.put("presentCount", getPresentLetterCount());
        map.put("absentCount", getAbsentLetterCount());
        return map;
    }
    
    @Override
    public String toString() {
        return "Result{" +
                "guess='" + guess + '\'' +
                ", status=" + status +
                ", attemptNumber=" + attemptNumber +
                ", remainingAttempts=" + remainingAttempts +
                ", accuracy=" + String.format("%.1f", accuracy) + "%" +
                ", correctLetters=" + getCorrectLetterCount() +
                '}';
    }
    
    public boolean hasCorrectLetters() {
        return !correctPositions.isEmpty();
    }
    
    public boolean hasPresentLetters() {
        return !presentPositions.isEmpty();
    }
    
    public boolean hasAbsentLetters() {
        return !absentPositions.isEmpty();
    }
    
    public int getTotalHints() {
        return correctPositions.size() + presentPositions.size();
    }
    
    public String getProgressBar() {
        if (feedback == null) {
            return "§7[-----]";
        }
        
        StringBuilder bar = new StringBuilder("§7[");
        for (LetterStatus status : feedback) {
            switch (status) {
                case CORRECT:
                    bar.append("§a█");
                    break;
                case PRESENT:
                    bar.append("§e█");
                    break;
                case ABSENT:
                    bar.append("§8█");
                    break;
                default:
                    bar.append("§7-");
                    break;
            }
        }
        bar.append("§7]");
        return bar.toString();
    }
}
