package org.bloby.wordlik;

import org.bloby.wordlik.commands.Command;
import org.bloby.wordlik.game.GameManager;
import org.bloby.wordlik.utils.WordList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public class Wordlik extends JavaPlugin {
    
    private GameManager gameManager;
    private WordList wordList;
    private Map<UUID, PlayerStats> playerStats;
    private File statsFile;
    private FileConfiguration statsConfig;
    private boolean soundsEnabled;
    private boolean debugMode;
    
    @Override
    public void onEnable() {
        getLogger().info("═══════════════════════════════════");
        getLogger().info("  Wordlik Plugin - Zapínání...");
        getLogger().info("═══════════════════════════════════");
        
        saveDefaultConfig();
        loadConfiguration();
        
        getLogger().info("Načítání slovního seznamu...");
        wordList = new WordList();
        getLogger().info("Načteno " + wordList.getWordCount() + " slov");
        
        getLogger().info("Inicializace Game Manageru...");
        gameManager = new GameManager(this, wordList);
        
        getLogger().info("Načítání statistik hráčů...");
        playerStats = new ConcurrentHashMap<>();
        loadStats();
        
        getLogger().info("Registrace příkazů...");
        Command commandExecutor = new Command(gameManager, this);
        Objects.requireNonNull(getCommand("wordlik")).setExecutor(commandExecutor);
        
        startAutoSave();
        
        getLogger().info("═══════════════════════════════════");
        getLogger().info("  Wordlik Plugin byl úspěšně zapnut!");
        getLogger().info("  Verze: " + getDescription().getVersion());
        getLogger().info("═══════════════════════════════════");
    }
    
    @Override
    public void onDisable() {
        getLogger().info("═══════════════════════════════════");
        getLogger().info("  Wordlik Plugin - Vypínání...");
        getLogger().info("═══════════════════════════════════");
        
        if (gameManager != null) {
            int activeGames = gameManager.getActiveGamesCount();
            if (activeGames > 0) {
                getLogger().info("Ukončování " + activeGames + " aktivních her...");
                
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (gameManager.hasActiveGame(player)) {
                        player.sendMessage(ChatColor.YELLOW + "Tvoje hra byla ukončena kvůli restartu serveru.");
                        gameManager.endGame(player);
                    }
                }
            }
            gameManager.clearAllGames();
        }
        
        getLogger().info("Ukládání statistik hráčů...");
        saveStats();
        
        getLogger().info("═══════════════════════════════════");
        getLogger().info("  Wordlik Plugin byl vypnut!");
        getLogger().info("═══════════════════════════════════");
    }
    
    private void loadConfiguration() {
        FileConfiguration config = getConfig();
        
        soundsEnabled = config.getBoolean("sounds.enabled", true);
        debugMode = config.getBoolean("debug-mode", false);
        
        config.addDefault("sounds.enabled", true);
        config.addDefault("debug-mode", false);
        config.addDefault("auto-save-interval", 300);
        config.options().copyDefaults(true);
        saveConfig();
        
        if (debugMode) {
            getLogger().info("Debug režim je ZAPNUTÝ");
        }
    }
    
    private void loadStats() {
        statsFile = new File(getDataFolder(), "stats.yml");
        
        if (!statsFile.exists()) {
            try {
                statsFile.getParentFile().mkdirs();
                statsFile.createNewFile();
                getLogger().info("Vytvořen nový soubor statistik");
            } catch (IOException e) {
                getLogger().log(Level.SEVERE, "Nelze vytvořit soubor statistik!", e);
                return;
            }
        }
        
        statsConfig = YamlConfiguration.loadConfiguration(statsFile);
        
        for (String key : statsConfig.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(key);
                PlayerStats stats = new PlayerStats();
                
                stats.gamesPlayed = statsConfig.getInt(key + ".gamesPlayed", 0);
                stats.gamesWon = statsConfig.getInt(key + ".gamesWon", 0);
                stats.currentStreak = statsConfig.getInt(key + ".currentStreak", 0);
                stats.bestStreak = statsConfig.getInt(key + ".bestStreak", 0);
                stats.totalAttempts = statsConfig.getInt(key + ".totalAttempts", 0);
                stats.totalPlayTime = statsConfig.getLong(key + ".totalPlayTime", 0);
                stats.hintsUsed = statsConfig.getInt(key + ".hintsUsed", 0);
                
                if (statsConfig.contains(key + ".attemptsDistribution")) {
                    for (String attempt : statsConfig.getConfigurationSection(key + ".attemptsDistribution").getKeys(false)) {
                        int attemptNum = Integer.parseInt(attempt);
                        int count = statsConfig.getInt(key + ".attemptsDistribution." + attempt);
                        stats.attemptsDistribution.put(attemptNum, count);
                    }
                }
                
                playerStats.put(uuid, stats);
            } catch (Exception e) {
                getLogger().warning("Chyba při načítání statistik pro: " + key);
            }
        }
        
        getLogger().info("Načteny statistiky pro " + playerStats.size() + " hráčů");
    }
    
    private void saveStats() {
        if (statsConfig == null || statsFile == null) {
            return;
        }
        
        for (Map.Entry<UUID, PlayerStats> entry : playerStats.entrySet()) {
            String key = entry.getKey().toString();
            PlayerStats stats = entry.getValue();
            
            statsConfig.set(key + ".gamesPlayed", stats.gamesPlayed);
            statsConfig.set(key + ".gamesWon", stats.gamesWon);
            statsConfig.set(key + ".currentStreak", stats.currentStreak);
            statsConfig.set(key + ".bestStreak", stats.bestStreak);
            statsConfig.set(key + ".totalAttempts", stats.totalAttempts);
            statsConfig.set(key + ".totalPlayTime", stats.totalPlayTime);
            statsConfig.set(key + ".hintsUsed", stats.hintsUsed);
            
            for (Map.Entry<Integer, Integer> dist : stats.attemptsDistribution.entrySet()) {
                statsConfig.set(key + ".attemptsDistribution." + dist.getKey(), dist.getValue());
            }
        }
        
        try {
            statsConfig.save(statsFile);
            if (debugMode) {
                getLogger().info("Statistiky uloženy");
            }
        } catch (IOException e) {
            getLogger().log(Level.SEVERE, "Nelze uložit statistiky!", e);
        }
    }
    
    private void startAutoSave() {
        int interval = getConfig().getInt("auto-save-interval", 300);
        
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> {
            saveStats();
            if (debugMode) {
                getLogger().info("Auto-save: Statistiky uloženy");
            }
        }, 20L * interval, 20L * interval);
    }
    
    public PlayerStats getPlayerStats(UUID uuid) {
        return playerStats.computeIfAbsent(uuid, k -> new PlayerStats());
    }
    
    public boolean areSoundsEnabled() {
        return soundsEnabled;
    }
    
    public boolean isDebugMode() {
        return debugMode;
    }
    
    public GameManager getGameManager() {
        return gameManager;
    }
    
    public WordList getWordList() {
        return wordList;
    }
    
    public static class PlayerStats {
        private int gamesPlayed;
        private int gamesWon;
        private int currentStreak;
        private int bestStreak;
        private int totalAttempts;
        private long totalPlayTime;
        private int hintsUsed;
        private Map<Integer, Integer> attemptsDistribution;
        
        public PlayerStats() {
            this.gamesPlayed = 0;
            this.gamesWon = 0;
            this.currentStreak = 0;
            this.bestStreak = 0;
            this.totalAttempts = 0;
            this.totalPlayTime = 0;
            this.hintsUsed = 0;
            this.attemptsDistribution = new HashMap<>();
        }
        
        public void recordWin(int attempts, long playTime) {
            gamesPlayed++;
            gamesWon++;
            currentStreak++;
            totalAttempts += attempts;
            totalPlayTime += playTime;
            
            if (currentStreak > bestStreak) {
                bestStreak = currentStreak;
            }
            
            attemptsDistribution.put(attempts, attemptsDistribution.getOrDefault(attempts, 0) + 1);
        }
        
        public void recordLoss(long playTime) {
            gamesPlayed++;
            currentStreak = 0;
            totalPlayTime += playTime;
        }
        
        public void addHintUsed() {
            hintsUsed++;
        }
        
        public int getGamesPlayed() {
            return gamesPlayed;
        }
        
        public int getGamesWon() {
            return gamesWon;
        }
        
        public int getCurrentStreak() {
            return currentStreak;
        }
        
        public int getBestStreak() {
            return bestStreak;
        }
        
        public double getWinRate() {
            return gamesPlayed > 0 ? (double) gamesWon / gamesPlayed * 100 : 0.0;
        }
        
        public double getAverageAttempts() {
            return gamesWon > 0 ? (double) totalAttempts / gamesWon : 0.0;
        }
        
        public long getAveragePlayTime() {
            return gamesPlayed > 0 ? totalPlayTime / gamesPlayed : 0;
        }
        
        public Map<Integer, Integer> getAttemptsDistribution() {
            return new HashMap<>(attemptsDistribution);
        }
        
        public int getHintsUsed() {
            return hintsUsed;
        }
    }
}
