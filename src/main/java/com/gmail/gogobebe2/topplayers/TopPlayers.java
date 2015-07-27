package com.gmail.gogobebe2.topplayers;

import org.bukkit.plugin.java.JavaPlugin;

public class TopPlayers extends JavaPlugin {
    @Override
    public void onEnable() {
        getLogger().info("Starting up TopPlayers. If you need me to update this plugin, email at gogobebe2@gmail.com");
    }

    @Override
    public void onDisable() {
        getLogger().info("Disabling TopPlayers. If you need me to update this plugin, email at gogobebe2@gmail.com");
    }
}
