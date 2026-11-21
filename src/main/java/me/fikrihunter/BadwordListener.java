package me.fikrihunter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class BadwordListener implements Listener {

    /** Thread-safe because this is read from the async chat thread. */
    private final Set<String> badWordMatrix = ConcurrentHashMap.newKeySet();

    private final JavaPlugin plugin;
    private final File file;
    private FileConfiguration fileConfiguration;

    /** Cached & volatile: never touch FileConfiguration from the chat thread. */
    private volatile String badwordMessage;
    private volatile String badwordChar;

    public BadwordListener(JavaPlugin plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "badwords.yml");
        this.fileConfiguration = YamlConfiguration.loadConfiguration(this.file);
        this.setupBadWordMessage();
        this.setupDefaultBadWords();
    }

    private void saveFile() {
        try {
            this.fileConfiguration.save(this.file);
        } catch (IOException error) {
            error.printStackTrace();
        }
    }

    private void setupBadWordMessage() {
        if (this.fileConfiguration.getString("badword-message") == null) {
            this.fileConfiguration.set("badword-message", "&4Please do not use badwords!");
        }
        if (this.fileConfiguration.get("badword-char") == null) {
            this.fileConfiguration.set("badword-char", "#");
        }

        // Cache the resolved values once — safe to read from async thread later.
        this.badwordMessage = ChatColor.translateAlternateColorCodes('&',
                this.fileConfiguration.getString("badword-message", "&4Please do not use badwords!"));
        this.badwordChar    = this.fileConfiguration.getString("badword-char", "#");

        this.saveFile();
    }

    private void setupDefaultBadWords() {
        if (this.fileConfiguration.getStringList("badwords").isEmpty()) {
            List<String> defaults = List.of(
                    "Fuck", "Shit", "Bitch", "Damm", "Cunt", "Asshole", "Cock",
                    "Penis", "Piss", "Twat", "Suck", "motherfucker", "prick",
                    "Ass", "Slut", "Whore", "horseshit", "arse", "crap", "cocksucker");

            this.badWordMatrix.addAll(defaults);
            this.fileConfiguration.set("badwords", new ArrayList<>(defaults));
            this.saveFile();
        } else {
            for (String word : this.fileConfiguration.getStringList("badwords")) {
                this.badWordMatrix.add(word);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        String[] words = e.getMessage().split(" ");
        StringBuilder message = new StringBuilder(e.getMessage().length());
        boolean badwordUsed = false;
        String maskChar = this.badwordChar;

        // Iterate by index so spaces are handled correctly (fixes "hello hello" bug).
        for (int i = 0; i < words.length; i++) {
            if (i > 0) message.append(' ');
            String word = words[i];

            boolean replaced = false;
            for (String badword : this.badWordMatrix) {
                if (word.toLowerCase().contains(badword.toLowerCase())) {
                    replaced = true;
                    break;
                }
            }

            if (replaced) {
                badwordUsed = true;
                for (int j = 0; j < word.length(); j++) {
                    message.append(maskChar);
                }
            } else {
                message.append(word);
            }
        }

        e.setMessage(message.toString());

        if (badwordUsed) {
            // Folia-safe: schedule the message onto the player's region thread.
            // Works on plain Paper too — EntityScheduler is a compatibility wrapper there.
            final String msg = this.badwordMessage;
            e.getPlayer().getScheduler().run(plugin,
                    task -> e.getPlayer().sendMessage(msg), null);
        }
    }
}