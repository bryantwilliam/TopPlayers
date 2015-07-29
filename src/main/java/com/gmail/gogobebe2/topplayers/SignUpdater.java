package com.gmail.gogobebe2.topplayers;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;

import java.util.concurrent.TimeUnit;

public class SignUpdater implements Runnable {
    TopPlayers plugin;

    protected SignUpdater(TopPlayers plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        if (plugin.getConfig().isSet("signs")) {
            for (String placement : plugin.getConfig().getConfigurationSection("signs").getKeys(false)) {
                for (String signID : plugin.getConfig().getConfigurationSection("signs." + placement).getKeys(false)) {
                    String path = "signs." + placement + "." + signID;
                    BlockState state = new LocationData(path, plugin).getLocation().getBlock().getState();
                    if (state instanceof Sign) {
                        updateSign((Sign) state, Integer.parseInt(placement));
                    }
                    else {
                        plugin.getConfig().set(path, null);
                    }
                }
            }
        }
    }

    protected static void updateSign(Sign sign, int placement) {
        Record record = Record.getRecord(placement, sign.getWorld().getUID());
        String name;
        long time;
        if (record == null) {
            name = "null";
            time = -1;
        }
        else {
            name = Bukkit.getOfflinePlayer(record.getPlayerUUID()).getName();
            time = TimeUnit.MILLISECONDS.toHours(record.getTotalTime());
        }

        sign.setLine(0, ChatColor.DARK_BLUE + "Top Player:");
        sign.setLine(1, ChatColor.GOLD + name);
        sign.setLine(2, ChatColor.BLUE + "Placed at: " + placement);
        sign.setLine(3, ChatColor.GREEN + "With " + time + " hours");
        sign.update();
    }
}
