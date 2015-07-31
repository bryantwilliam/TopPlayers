package com.gmail.gogobebe2.topplayers;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.block.Skull;

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

    protected void updateSign(Sign sign, int placement) {
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
        sign.setLine(2, ChatColor.BLUE + "Placed " + ordinal(placement));
        sign.setLine(3, ChatColor.GREEN + "With " + time + " hours");
        sign.update();

        int radius = plugin.getConfig().getInt("head identification block radius");
        for (int x = sign.getX() - radius; x < sign.getX() + radius; x++) {
            for (int y = sign.getY() - radius; y < sign.getY() + radius; y++) {
                for (int z = sign.getZ() - radius; z < sign.getZ() + radius; z++) {
                    Block block = sign.getWorld().getBlockAt(x, y, z);
                    if (block.getType() == Material.SKULL) {
                        Skull skull = (Skull) block;
                        skull.setSkullType(SkullType.PLAYER);
                        skull.setOwner(name);
                        skull.update();
                    }
                }
            }
        }
    }

    private String ordinal(int i) {
        int mod100 = i % 100;
        int mod10 = i % 10;
        if(mod10 == 1 && mod100 != 11) {
            return i + "st";
        } else if(mod10 == 2 && mod100 != 12) {
            return i + "nd";
        } else if(mod10 == 3 && mod100 != 13) {
            return i + "rd";
        } else {
            return i + "th";
        }
    }
}
