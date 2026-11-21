package me.fikrihunter;

import java.util.logging.Logger;
import org.bukkit.plugin.java.JavaPlugin;

public class BadwordMain extends JavaPlugin {
    public static BadwordMain instance;
    public static Logger log;

    @Override
    public void onLoad() {
        instance = this;
        log = getLogger();          // plugin logger — works on Folia too
        log.info("Loading badwords");
    }

    @Override
    public void onEnable() {
        log.info("Starting up badwords");
        this.getServer().getPluginManager().registerEvents(new BadwordListener(this), this);
    }

    @Override
    public void onDisable() {
        log.info("Shutting down badwords");
    }
}