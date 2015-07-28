package com.gmail.gogobebe2.topplayers;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
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
                    Block block = new LocationData(path, plugin).getLocation().getBlock();
                    if (block instanceof Sign) {
                        updateSign((Sign) block, Integer.parseInt(placement));
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
        String name = Bukkit.getPlayer(record.getPlayerUUID()).getName();
        long time = TimeUnit.MILLISECONDS.toHours(record.getTotalTime());
        sign.setLine(0, ChatColor.DARK_BLUE + "Top Player:");
        sign.setLine(1, ChatColor.GOLD + name);
        sign.setLine(2, ChatColor.BLUE + "Placed at:" + placement);
        sign.setLine(3, ChatColor.GREEN + "With " + time + " hours");
        sign.update();
    }
}
