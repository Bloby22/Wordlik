package org.bloby.wordlik.game;

import org.bloby.wordlik.Wordlik;
import org.bloby.wordlik.utils.WordList;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class GameManager {
    
    private final Wordlik plugin;
    private final WordList wordList;
    private final Map<UUID, WordleGame> activeGames;
    private final Map<UUID, Long> gameStartTimes;
    private final Map<UUID, Integer> playerAttempts;
    private final Set<UUID> pausedGames;
    private final Map<UUID, List<String>> gameHistory;
    private int totalGamesPlayed;
    private int totalGamesWon;
    private long totalGameTime;
    private final Map<String, Integer> wordFrequency;
    private final Random random;
    
    public GameManager(Wordlik plugin, WordList wordList) {
        this.plugin = plugin;
        this.wordList = wordList;
        this.activeGames = new ConcurrentHashMap<>();
        this.gameStartTimes = new ConcurrentHashMap<>();
        this.playerAttempts = new ConcurrentHashMap<>();
        this.pausedGames = ConcurrentHashMap.newKeySet();
        this.gameHistory = new ConcurrentHashMap<>();
        this.totalGamesPlayed = 0;
        this.totalGamesWon = 0;
        this.totalGameTime = 0;
        this.wordFrequency = new ConcurrentHashMap<>();
        this.random = new Random();
        
        startGameMonitor();
        startStatisticsLogger();
    }
    
    public void startGame(Player player) {
        UUID uuid = player.getUniqueId();
        
        if (activeGames.containsKey(uuid)) {
            return;
        }
        
        String targetWord = selectTargetWord(player);
        WordleGame game = new WordleGame(targetWord, wordList);
        
        activeGames.put(uuid, game);
        gameStartTimes.put(uuid, System.currentTimeMillis());
        playerAttempts.put(uuid, 0);
        
        if (!gameHistory.containsKey(uuid)) {
            gameHistory.put(uuid, new ArrayList<>());
        }
        gameHistory.get(uuid).add(targetWord);
        
        totalGamesPlayed++;
        wordFrequency.put(targetWord, wordFrequency.getOrDefault(targetWord, 0) + 1);
        
        if (plugin.isDebugMode()) {
            plugin.getLogger().info("Hráč " + player.getName() + " začal novou hru. Slovo: " + targetWord);
        }
    }
    
    public void endGame(Player player) {
        UUID uuid = player.getUniqueId();
        
        WordleGame game = activeGames.get(uuid);
        if (game != null) {
            Long startTime = gameStartTimes.get(uuid);
            if (startTime != null) {
                long duration = System.currentTimeMillis() - startTime;
                totalGameTime += duration;
                
                if (game.isFinished() && game.getAttempts() <= game.getMaxAttempts()) {
                    totalGamesWon++;
                }
            }
        }
        
        activeGames.remove(uuid);
        gameStartTimes.remove(uuid);
        playerAttempts.remove(uuid);
        pausedGames.remove(uuid);
        
        if (plugin.isDebugMode()) {
            plugin.getLogger().info("Hráč " + player.getName() + " ukončil hru");
        }
    }
    
    public WordleGame getGame(Player player) {
        return activeGames.get(player.getUniqueId());
    }
    
    public boolean hasActiveGame(Player player) {
        return activeGames.containsKey(player.getUniqueId());
    }
    
    public void pauseGame(Player player) {
        UUID uuid = player.getUniqueId();
        if (activeGames.containsKey(uuid)) {
            pausedGames.add(uuid);
        }
    }
    
    public void resumeGame(Player player) {
        pausedGames.remove(player.getUniqueId());
    }
    
    public boolean isGamePaused(Player player) {
        return pausedGames.contains(player.getUniqueId());
    }
    
    public void clearAllGames() {
        if (plugin.isDebugMode()) {
            plugin.getLogger().info("Ukončování všech aktivních her: " + activeGames.size());
        }
        
        activeGames.clear();
        gameStartTimes.clear();
        playerAttempts.clear();
        pausedGames.clear();
    }
    
    public int getActiveGamesCount() {
        return activeGames.size();
    }
    
    public long getGameDuration(Player player) {
        Long startTime = gameStartTimes.get(player.getUniqueId());
        if (startTime == null) {
            return 0;
        }
        return System.currentTimeMillis() - startTime;
    }
    
    public List<String> getPlayerHistory(Player player) {
        return gameHistory.getOrDefault(player.getUniqueId(), new ArrayList<>());
    }
    
    public int getTotalGamesPlayed() {
        return totalGamesPlayed;
    }
    
    public int getTotalGamesWon() {
        return totalGamesWon;
    }
    
    public double getGlobalWinRate() {
        return totalGamesPlayed > 0 ? (double) totalGamesWon / totalGamesPlayed * 100 : 0.0;
    }
    
    public long getAverageGameTime() {
        return totalGamesPlayed > 0 ? totalGameTime / totalGamesPlayed : 0;
    }
    
    public Map<String, Integer> getWordFrequency() {
        return new HashMap<>(wordFrequency);
    }
    
    public List<UUID> getActivePlayers() {
        return new ArrayList<>(activeGames.keySet());
    }
    
    public void recordAttempt(Player player) {
        UUID uuid = player.getUniqueId();
        playerAttempts.put(uuid, playerAttempts.getOrDefault(uuid, 0) + 1);
    }
    
    public int getPlayerAttempts(Player player) {
        return playerAttempts.getOrDefault(player.getUniqueId(), 0);
    }
    
    private String selectTargetWord(Player player) {
        List<String> recentWords = getPlayerHistory(player);
        String word;
        int attempts = 0;
        int maxAttempts = 50;
        
        do {
            word = wordList.getRandomWord();
            attempts++;
        } while (recentWords.contains(word) && attempts < maxAttempts);
        
        return word;
    }
    
    private void startGameMonitor() {
        new BukkitRunnable() {
            @Override
            public void run() {
                long currentTime = System.currentTimeMillis();
                List<UUID> toRemove = new ArrayList<>();
                
                for (Map.Entry<UUID, Long> entry : gameStartTimes.entrySet()) {
                    long duration = currentTime - entry.getValue();
                    
                    if (duration > 3600000) {
                        toRemove.add(entry.getKey());
                    }
                }
                
                for (UUID uuid : toRemove) {
                    Player player = Bukkit.getPlayer(uuid);
                    if (player != null) {
                        endGame(player);
                        player.sendMessage("§cTvoje hra byla automaticky ukončena po 1 hodině nečinnosti.");
                    } else {
                        activeGames.remove(uuid);
                        gameStartTimes.remove(uuid);
                        playerAttempts.remove(uuid);
                        pausedGames.remove(uuid);
                    }
                }
                
                if (plugin.isDebugMode() && !toRemove.isEmpty()) {
                    plugin.getLogger().info("Automaticky ukončeno " + toRemove.size() + " neaktivních her");
                }
            }
        }.runTaskTimer(plugin, 20L * 60, 20L * 60);
    }
    
    private void startStatisticsLogger() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (plugin.isDebugMode()) {
                    plugin.getLogger().info("=== Statistiky Game Manageru ===");
                    plugin.getLogger().info("Aktivní hry: " + activeGames.size());
                    plugin.getLogger().info("Celkem her: " + totalGamesPlayed);
                    plugin.getLogger().info("Výher: " + totalGamesWon);
                    plugin.getLogger().info("Win rate: " + String.format("%.2f%%", getGlobalWinRate()));
                    plugin.getLogger().info("Průměrný čas hry: " + (getAverageGameTime() / 1000) + "s");
                    plugin.getLogger().info("================================");
                }
            }
        }.runTaskTimerAsynchronously(plugin, 20L * 300, 20L * 300);
    }
    
    public void resetStatistics() {
        totalGamesPlayed = 0;
        totalGamesWon = 0;
        totalGameTime = 0;
        wordFrequency.clear();
        
        plugin.getLogger().info("Globální statistiky byly resetovány");
    }
    
    public Map<String, Object> getGameInfo(Player player) {
        Map<String, Object> info = new HashMap<>();
        UUID uuid = player.getUniqueId();
        
        info.put("hasActiveGame", hasActiveGame(player));
        info.put("isPaused", isGamePaused(player));
        info.put("duration", getGameDuration(player));
        info.put("attempts", getPlayerAttempts(player));
        
        WordleGame game = getGame(player);
        if (game != null) {
            info.put("maxAttempts", game.getMaxAttempts());
            info.put("remainingAttempts", game.getMaxAttempts() - game.getAttempts());
            info.put("guesses", game.getGuesses());
            info.put("isFinished", game.isFinished());
        }
        
        return info;
    }
    
    public boolean canStartGame(Player player) {
        if (hasActiveGame(player)) {
            return false;
        }
        
        if (activeGames.size() >= 1000) {
            return false;
        }
        
        return true;
    }
    
    public String getRandomUnusedWord(Player player) {
        List<String> history = getPlayerHistory(player);
        List<String> allWords = wordList.getAllWords();
        List<String> unused = new ArrayList<>();
        
        for (String word : allWords) {
            if (!history.contains(word)) {
                unused.add(word);
            }
        }
        
        if (unused.isEmpty()) {
            return wordList.getRandomWord();
        }
        
        return unused.get(random.nextInt(unused.size()));
    }
    
    public void cleanupPlayer(UUID uuid) {
        activeGames.remove(uuid);
        gameStartTimes.remove(uuid);
        playerAttempts.remove(uuid);
        pausedGames.remove(uuid);
    }
    
    public int countGamesInProgress() {
        int count = 0;
        for (WordleGame game : activeGames.values()) {
            if (!game.isFinished()) {
                count++;
            }
        }
        return count;
    }
    
    public int countFinishedGames() {
        int count = 0;
        for (WordleGame game : activeGames.values()) {
            if (game.isFinished()) {
                count++;
            }
        }
        return count;
    }
    
    public List<Map.Entry<String, Integer>> getMostPlayedWords(int limit) {
        return wordFrequency.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(limit)
                .toList();
    }
}
