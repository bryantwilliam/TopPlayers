package com.gmail.gogobebe2.topplayers.record;

import com.gmail.gogobebe2.topplayers.TopPlayers;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;

public abstract class Record {
    private World world;
    private TopPlayers plugin;
    private OfflinePlayer player;

    protected Record(OfflinePlayer player, World world, TopPlayers plugin) {
        this.world = world;
        this.plugin = plugin;
        this.player = player;
    }

    protected World getWorld() {
        return this.world;
    }

    protected TopPlayers getPlugin() {
        return this.plugin;
    }

    public OfflinePlayer getPlayer() {
        return this.player;
    }

    public long getAccumulatedTime() {
        return getAccumulatedTime(player, world, plugin);
    }

    protected static long getAccumulatedTime(OfflinePlayer player, World world, TopPlayers plugin) {
        long accumulatedTime = 0;
        String path = "players." + player.getUniqueId() + "." + world.getUID();
        if (plugin.getConfig().isSet(path)) {
            accumulatedTime = plugin.getConfig().getLong(path);
        }
        if (player.isOnline()) {
            accumulatedTime += System.currentTimeMillis() - OnlineRecord.getOnlineRecord((Player) player, world, plugin).getSessionStartTime();
        }
        return accumulatedTime;
    }
}
