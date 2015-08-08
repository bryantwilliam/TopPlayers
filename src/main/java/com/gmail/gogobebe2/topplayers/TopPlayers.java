package com.gmail.gogobebe2.topplayers;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.UUID;

public class TopPlayers extends JavaPlugin implements Listener {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (label.equalsIgnoreCase("topplayer") && sender.hasPermission("topplayer.admin")) {
            if (args[0].equalsIgnoreCase("reload")) {
                reloadConfig();
                saveConfig();
                sender.sendMessage(ChatColor.GREEN + "Config reloaded!");
                return true;
            }
            sender.sendMessage(ChatColor.RED + "Incorrect usage! Type /topplayer reload");
            return true;
        }
        return false;
    }

    SignUpdater signUpdater;
    @Override
    public void onEnable() {
        getLogger().info("Starting up TopPlayers. If you need me to update this plugin, email at gogobebe2@gmail.com");

        saveDefaultConfig();

        Record.loadRecords(this);

        // Incase the server was reloaded and no players were kicked.
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (Record.getRecord(player.getUniqueId(), player.getWorld().getUID()) == null) {
                new Record(player, this);
            }
        }

        Bukkit.getPluginManager().registerEvents(this, this);

        this.signUpdater = new SignUpdater(this);
        BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
        scheduler.scheduleSyncRepeatingTask(this, signUpdater,
                0L, getConfig().getLong("sign update rate (in ticks)"));
    }

    @Override
    public void onDisable() {
        getLogger().info("Disabling TopPlayers. If you need me to update this plugin, email at gogobebe2@gmail.com");
    }

    @EventHandler
    protected void onPlayerJoin(PlayerJoinEvent event) {
        new Record(event.getPlayer(), this);
    }

    @EventHandler
    protected void onPlayerQuite(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        saveRecord(player.getUniqueId(), player.getWorld().getUID());
    }

    private void saveRecord(UUID playerUUID, UUID worldUUID) {
        Record record = Record.getRecord(playerUUID, worldUUID);
        if (record != null) {
            record.saveRecord();
        }
        else {
            getLogger().severe(ChatColor.RED + "An error occurred while trying to find "
                    + Bukkit.getPlayer(playerUUID).getName() + "'s record. This should never happen.");
        }
    }

    @EventHandler
    protected void onPlayerChangeWorld(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        saveRecord(player.getUniqueId(), event.getFrom().getUID());
        new Record(player, this);
    }

    @EventHandler
    protected void onSignChange(SignChangeEvent event) {
        if (event.isCancelled()) return;
        String[] lines = event.getLines();
        if (lines[0].equalsIgnoreCase("[top player]")) {
            Player player = event.getPlayer();
            if (player.hasPermission("topsign.create")) {
                int placement;
                try {
                    placement = Integer.parseInt(lines[1]);
                    if (placement <= 0) {
                        throw new NumberFormatException();
                    }
                }
                catch (NumberFormatException eee) {
                    player.sendMessage(ChatColor.RED + "Error! line 2 does not have a placement number in it or it's below 1!");
                    event.setCancelled(true);
                    return;
                }
                Block sign = event.getBlock();
                new LocationData(sign.getLocation(), this).saveToConfig("signs." + placement + "." + UUID.randomUUID().toString());
                signUpdater.updateSigns();
                player.sendMessage(ChatColor.GREEN + "Top " + placement + " sign has been created!");
            } else {
                player.sendMessage(ChatColor.RED + "Error! You do not have permission to create head signs!");
            }
        }
    }
}
