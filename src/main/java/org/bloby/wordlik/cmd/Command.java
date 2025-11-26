package org.bloby.wordlik.commands;

import org.bloby.wordlik.Wordlik;
import org.bloby.wordlik.game.GameManager;
import org.bloby.wordlik.game.GuessResult;
import org.bloby.wordlik.game.WordleGame;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

public class Command implements CommandExecutor {
    
    private final GameManager gameManager;
    private final Wordlik plugin;
    
    public Command(GameManager gameManager, Wordlik plugin) {
        this.gameManager = gameManager;
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Tento příkaz může používat pouze hráč!");
            return true;
        }
        
        Player player = (Player) sender;
        
        if (args.length == 0) {
            sendHelp(player);
            return true;
        }
        
        String subCommand = args[0].toLowerCase();
        
        switch (subCommand) {
            case "start":
            case "nova":
            case "new":
                handleStart(player);
                break;
                
            case "stop":
            case "konec":
            case "end":
                handleStop(player);
                break;
                
            case "stats":
            case "statistiky":
                handleStats(player);
                break;
                
            case "napoveda":
            case "help":
            case "pomoc":
                sendHelp(player);
                break;
                
            case "daily":
            case "denni":
                handleDaily(player);
                break;
                
            case "top":
            case "leaderboard":
            case "zebricek":
                handleLeaderboard(player);
                break;
                
            case "hint":
            case "napovida":
                handleHint(player);
                break;
                
            case "reload":
                handleReload(player);
                break;
                
            default:
                if (args[0].length() == 5) {
                    handleGuess(player, args[0]);
                } else {
                    player.sendMessage(ChatColor.RED + "Slovo musí mít přesně 5 písmen!");
                }
                break;
        }
        
        return true;
    }
    
    private void handleStart(Player player) {
        if (gameManager.hasActiveGame(player)) {
            player.sendMessage(ChatColor.YELLOW + "Už máš aktivní hru! Použij /wordlik stop pro ukončení.");
            return;
        }
        
        gameManager.startGame(player);
        
        player.sendMessage("");
        player.sendMessage(ChatColor.GREEN + "═══════════════════════════════════");
        player.sendMessage(ChatColor.GOLD + ChatColor.BOLD.toString() + "       WORDLIK - České Wordle");
        player.sendMessage(ChatColor.GREEN + "═══════════════════════════════════");
        player.sendMessage(ChatColor.WHITE + "Hádej " + ChatColor.YELLOW + "5písmenné" + ChatColor.WHITE + " české slovo!");
        player.sendMessage(ChatColor.WHITE + "Máš " + ChatColor.YELLOW + ChatColor.BOLD + "6 pokusů" + ChatColor.WHITE + ".");
        player.sendMessage("");
        player.sendMessage(ChatColor.GRAY + "Barvy písmen:");
        player.sendMessage(ChatColor.GREEN + "■ " + ChatColor.WHITE + "Písmeno je na správném místě");
        player.sendMessage(ChatColor.YELLOW + "■ " + ChatColor.WHITE + "Písmeno je ve slově, ale jinde");
        player.sendMessage(ChatColor.DARK_GRAY + "■ " + ChatColor.WHITE + "Písmeno není ve slově");
        player.sendMessage("");
        player.sendMessage(ChatColor.AQUA + "Použij: " + ChatColor.WHITE + "/wordlik " + ChatColor.YELLOW + "<slovo>");
        player.sendMessage(ChatColor.AQUA + "Nápověda: " + ChatColor.WHITE + "/wordlik hint");
        player.sendMessage(ChatColor.GREEN + "═══════════════════════════════════");
        player.sendMessage("");
        
        if (plugin.areSoundsEnabled()) {
            player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
        }
    }
    
    private void handleStop(Player player) {
        WordleGame game = gameManager.getGame(player);
        if (game == null) {
            player.sendMessage(ChatColor.RED + "Nemáš aktivní hru!");
            return;
        }
        
        player.sendMessage("");
        player.sendMessage(ChatColor.YELLOW + "Hra byla ukončena.");
        player.sendMessage(ChatColor.WHITE + "Správné slovo bylo: " + 
                          ChatColor.GOLD + ChatColor.BOLD + game.getTargetWord().toUpperCase());
        player.sendMessage("");
        
        gameManager.endGame(player);
        
        if (plugin.areSoundsEnabled()) {
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
        }
    }
    
    private void handleGuess(Player player, String guess) {
        WordleGame game = gameManager.getGame(player);
        if (game == null) {
            player.sendMessage(ChatColor.RED + "Nemáš aktivní hru! Použij " + ChatColor.YELLOW + "/wordlik start");
            return;
        }
        
        GuessResult result = game.makeGuess(guess.toLowerCase());
        
        switch (result.getStatus()) {
            case INVALID_LENGTH:
                player.sendMessage(ChatColor.RED + "Slovo musí mít přesně 5 písmen!");
                if (plugin.areSoundsEnabled()) {
                    player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 0.5f, 0.8f);
                }
                break;
                
            case INVALID_WORD:
                player.sendMessage(ChatColor.RED + "Toto slovo není v seznamu!");
                if (plugin.areSoundsEnabled()) {
                    player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 0.5f, 0.8f);
                }
                break;
                
            case CORRECT:
                displayGuess(player, result);
                player.sendMessage("");
                player.sendMessage(ChatColor.GREEN + ChatColor.BOLD.toString() + "╔════════════════════════╗");
                player.sendMessage(ChatColor.GREEN + ChatColor.BOLD.toString() + "║   🎉 VÝHRA! 🎉        ║");
                player.sendMessage(ChatColor.GREEN + ChatColor.BOLD.toString() + "╚════════════════════════╝");
                player.sendMessage(ChatColor.WHITE + "Uhodl jsi slovo " + ChatColor.GOLD + ChatColor.BOLD + 
                                 game.getTargetWord().toUpperCase() + ChatColor.WHITE + 
                                 " na " + ChatColor.YELLOW + "" + game.getAttempts() + ". pokus" + ChatColor.WHITE + "!");
                player.sendMessage("");
                
                Wordlik.PlayerStats stats = plugin.getPlayerStats(player.getUniqueId());
                long playTime = System.currentTimeMillis() - game.getStartTime();
                stats.recordWin(game.getAttempts(), playTime);
                
                player.sendMessage(ChatColor.GRAY + "Tvoje statistiky: " + ChatColor.YELLOW + "" + 
                                 stats.getGamesWon() + " výher / " + 
                                 stats.getGamesPlayed() + " her");
                
                gameManager.endGame(player);
                
                if (plugin.areSoundsEnabled()) {
                    player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.0f, 1.0f);
                }
                break;
                
            case WRONG:
                displayGuess(player, result);
                int remaining = game.getMaxAttempts() - game.getAttempts();
                if (remaining > 0) {
                    player.sendMessage(ChatColor.GRAY + "Zbývá pokusů: " + ChatColor.YELLOW + "" + remaining);
                }
                
                if (plugin.areSoundsEnabled()) {
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_HAT, 0.5f, 1.0f);
                }
                break;
                
            case GAME_OVER:
                displayGuess(player, result);
                player.sendMessage("");
                player.sendMessage(ChatColor.RED + ChatColor.BOLD.toString() + "╔════════════════════════╗");
                player.sendMessage(ChatColor.RED + ChatColor.BOLD.toString() + "║     PROHRA!           ║");
                player.sendMessage(ChatColor.RED + ChatColor.BOLD.toString() + "╚════════════════════════╝");
                player.sendMessage(ChatColor.WHITE + "Správné slovo bylo: " + ChatColor.GOLD + ChatColor.BOLD + 
                                 game.getTargetWord().toUpperCase());
                player.sendMessage("");
                
                Wordlik.PlayerStats stats2 = plugin.getPlayerStats(player.getUniqueId());
                long playTime2 = System.currentTimeMillis() - game.getStartTime();
                stats2.recordLoss(playTime2);
                
                gameManager.endGame(player);
                
                if (plugin.areSoundsEnabled()) {
                    player.playSound(player.getLocation(), Sound.ENTITY_WITHER_DEATH, 0.5f, 0.5f);
                }
                break;
        }
    }
    
    private void displayGuess(Player player, GuessResult result) {
        StringBuilder display = new StringBuilder(ChatColor.WHITE + "");
        char[] letters = result.getGuess().toUpperCase().toCharArray();
        
        for (int i = 0; i < letters.length; i++) {
            switch (result.getFeedback()[i]) {
                case CORRECT:
                    display.append(ChatColor.GREEN).append(ChatColor.BOLD).append(" ").append(letters[i]).append(" ");
                    break;
                case PRESENT:
                    display.append(ChatColor.YELLOW).append(ChatColor.BOLD).append(" ").append(letters[i]).append(" ");
                    break;
                case ABSENT:
                    display.append(ChatColor.DARK_GRAY).append(ChatColor.BOLD).append(" ").append(letters[i]).append(" ");
                    break;
            }
        }
        
        player.sendMessage(display.toString());
    }
    
    private void handleStats(Player player) {
        Wordlik.PlayerStats stats = plugin.getPlayerStats(player.getUniqueId());
        
        player.sendMessage("");
        player.sendMessage(ChatColor.GOLD + "═══════ Tvoje Statistiky ═══════");
        player.sendMessage(ChatColor.YELLOW + "Odehráno her: " + ChatColor.WHITE + "" + stats.getGamesPlayed());
        player.sendMessage(ChatColor.YELLOW + "Výher: " + ChatColor.WHITE + "" + stats.getGamesWon());
        player.sendMessage(ChatColor.YELLOW + "Úspěšnost: " + ChatColor.WHITE + 
                         String.format("%.1f%%", stats.getWinRate()));
        player.sendMessage(ChatColor.YELLOW + "Aktuální série: " + ChatColor.WHITE + "" + stats.getCurrentStreak());
        player.sendMessage(ChatColor.YELLOW + "Nejlepší série: " + ChatColor.WHITE + "" + stats.getBestStreak());
        
        if (stats.getGamesWon() > 0) {
            player.sendMessage(ChatColor.YELLOW + "Průměr pokusů: " + ChatColor.WHITE + 
                             String.format("%.2f", stats.getAverageAttempts()));
        }
        
        player.sendMessage("");
        player.sendMessage(ChatColor.GOLD + "Distribuce pokusů:");
        Map<Integer, Integer> dist = stats.getAttemptsDistribution();
        for (int i = 1; i <= 6; i++) {
            int count = dist.getOrDefault(i, 0);
            String bar = createBar(count, stats.getGamesWon());
            player.sendMessage(ChatColor.GRAY + "" + i + ": " + ChatColor.GREEN + bar + ChatColor.WHITE + " " + count);
        }
        player.sendMessage(ChatColor.GOLD + "════════════════════════════════");
        player.sendMessage("");
    }
    
    private String createBar(int value, int max) {
        if (max == 0) return "";
        int length = (int) Math.ceil((value * 20.0) / max);
        StringBuilder bar = new StringBuilder();
        for (int i = 0; i < length; i++) {
            bar.append("█");
        }
        return bar.toString();
    }
    
    private void handleDaily(Player player) {
        player.sendMessage(ChatColor.GOLD + "Denní výzva bude brzy k dispozici!");
    }
    
    private void handleLeaderboard(Player player) {
        player.sendMessage(ChatColor.GOLD + "Žebříček bude brzy k dispozici!");
    }
    
    private void handleHint(Player player) {
        WordleGame game = gameManager.getGame(player);
        if (game == null) {
            player.sendMessage(ChatColor.RED + "Nemáš aktivní hru!");
            return;
        }
        
        player.sendMessage(ChatColor.YELLOW + "Nápověda: První písmeno je " + 
                         ChatColor.GOLD + ChatColor.BOLD + 
                         game.getTargetWord().toUpperCase().charAt(0));
        
        plugin.getPlayerStats(player.getUniqueId()).addHintUsed();
    }
    
    private void handleReload(Player player) {
        if (!player.hasPermission("wordlik.admin")) {
            player.sendMessage(ChatColor.RED + "Nemáš oprávnění!");
            return;
        }
        
        plugin.reloadConfig();
        player.sendMessage(ChatColor.GREEN + "Konfigurace byla znovu načtena!");
    }
    
    private void sendHelp(Player player) {
        player.sendMessage("");
        player.sendMessage(ChatColor.GOLD + "═══════ Wordlik - Nápověda ═══════");
        player.sendMessage(ChatColor.YELLOW + "/wordlik start" + ChatColor.WHITE + " - Začít novou hru");
        player.sendMessage(ChatColor.YELLOW + "/wordlik <slovo>" + ChatColor.WHITE + " - Hádat slovo");
        player.sendMessage(ChatColor.YELLOW + "/wordlik hint" + ChatColor.WHITE + " - Získat nápovědu");
        player.sendMessage(ChatColor.YELLOW + "/wordlik stop" + ChatColor.WHITE + " - Ukončit hru");
        player.sendMessage(ChatColor.YELLOW + "/wordlik stats" + ChatColor.WHITE + " - Zobrazit statistiky");
        player.sendMessage(ChatColor.YELLOW + "/wordlik help" + ChatColor.WHITE + " - Zobrazit nápovědu");
        player.sendMessage(ChatColor.GOLD + "══════════════════════════════════");
        player.sendMessage("");
    }
}
