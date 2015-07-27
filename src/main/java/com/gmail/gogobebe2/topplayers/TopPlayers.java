package com.gmail.gogobebe2.topplayers;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.TimeUnit;

public class TopPlayers extends JavaPlugin implements Listener {
    @Override
    public void onEnable() {
        getLogger().info("Starting up TopPlayers. If you need me to update this plugin, email at gogobebe2@gmail.com");

        saveDefaultConfig();

        // Incase the server was reloaded and no players were kicked.
        for (Player player : Bukkit.getOnlinePlayers()) {
            new Record(player.getUniqueId(), this);
        }

        Record.loadRecords(this);

        // TODO: create a sign updater Bukkit Runnable.
    }

    @Override
    public void onDisable() {
        getLogger().info("Disabling TopPlayers. If you need me to update this plugin, email at gogobebe2@gmail.com");
    }

    @EventHandler
    protected void onPlayerJoin(PlayerJoinEvent event) {
        new Record(event.getPlayer().getUniqueId(), this);
    }

    @EventHandler
    protected void onPlayerQuite(PlayerQuitEvent event) {
        Record record = Record.getRecord(event.getPlayer());
        if (record != null) {
            record.saveRecord();
        }
        else {
            getLogger().severe(ChatColor.RED + "An error occurred while trying to find " + event.getPlayer().getName()
                    + "'s record. This should never happen.");
        }
    }

    @EventHandler
    protected void onSignChange(SignChangeEvent event) {
        if (event.isCancelled()) return;
        String[] lines = event.getLines();
        if (lines[0].equalsIgnoreCase("[top player]")) {
            Player player = event.getPlayer();
            if (player.hasPermission("topplayers.create")) {
                int placement;
                try {
                    placement = Integer.parseInt(lines[1]);
                    if (placement >= 0) {
                        throw new NumberFormatException();
                    }
                }
                catch (NumberFormatException eee) {
                    player.sendMessage(ChatColor.RED + "Error! line 2 does not have a placement number in it or it's below 1s!");
                    event.setCancelled(true);
                    return;
                }
                Record record = Record.getRecord(placement);
                String name = Bukkit.getPlayer(record.getUUID()).getName();
                long time = TimeUnit.MILLISECONDS.toHours(record.getTotalTime());

                event.setLine(0, ChatColor.DARK_BLUE + "Placement " + placement + ":");
                event.setLine(1, ChatColor.BLUE + "" + placement);
                event.setLine(2, ChatColor.GOLD + name);
                event.setLine(3, ChatColor.GREEN + "" + time + "hours");
                player.sendMessage(ChatColor.GREEN + "Top Player sign has been created for " + name + " as the top "
                        + placement + " with a time of " + time);

                // TODO: save position of sign in config and create a sign updater.
            } else {
                player.sendMessage(ChatColor.RED + "Error! You do not have permission to create head signs!");
            }
        }
    }
}
