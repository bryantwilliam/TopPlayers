package com.gmail.gogobebe2.topplayers.record;

import com.gmail.gogobebe2.topplayers.TopPlayers;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashSet;
import java.util.Set;

public class OnlineRecord extends Record {
    private long sessionStartTime;

    private static Set<OnlineRecord> onlineRecords = new HashSet<>();

    private static Listener listener = null;

    protected OnlineRecord(Player player, World world, TopPlayers plugin) {
        super(player, world, plugin);
        sessionStartTime = System.currentTimeMillis();
    }

    private void closeAndSave() {
        getPlugin().getConfig().set("players." + getPlayer().getUniqueId() + "." + getWorld().getUID(), getAccumulatedTime());
        onlineRecords.remove(this);
    }

    protected long getSessionStartTime() {
        return this.sessionStartTime;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof OnlineRecord) {
            OnlineRecord other = (OnlineRecord) obj;
            if (this.getPlayer().getUniqueId().equals(other.getPlayer().getUniqueId())) {
                if (this.getWorld().getUID().equals(other.getWorld().getUID())) {
                    return true;
                }
            }
        }
        return false;
    }

    protected static OnlineRecord getOnlineRecord(Player player, World world, TopPlayers plugin) throws NullPointerException {
        for (OnlineRecord record : onlineRecords) {
            if (record.equals(new OnlineRecord(player, world, plugin))) {
                return record;
            }
        }
        throw new NullPointerException("No such record!");
    }

    public static Listener getListener(TopPlayers plugin) {
        if (listener == null) return new LISTENER(plugin);
        return listener;
    }

    private final static class LISTENER implements Listener {
        TopPlayers plugin;

        private LISTENER(TopPlayers plugin) {
            this.plugin = plugin;
        }

        @EventHandler
        protected void onPlayerJoin(PlayerJoinEvent event) {
            Player player = event.getPlayer();
            onlineRecords.add(new OnlineRecord(player, player.getWorld(), plugin));
        }

        @EventHandler
        protected void onPlayerQuit(PlayerQuitEvent event) {
            Player player = event.getPlayer();
            getOnlineRecord(player, player.getWorld(), plugin);
        }

        @EventHandler
        protected void onPlayerChangeWorld(PlayerChangedWorldEvent event) {
            Player player = event.getPlayer();
            getOnlineRecord(player, event.getFrom(), plugin).closeAndSave();
            onlineRecords.add(new OnlineRecord(player, player.getWorld(), plugin));
        }
    }
}
