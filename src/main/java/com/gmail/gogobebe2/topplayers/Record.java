package com.gmail.gogobebe2.topplayers;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Record {
    private static Set<Record> records = new HashSet<>();

    private long startTime;
    private UUID playerUUID;
    private UUID worldUUID;
    private TopPlayers plugin;

    protected Record(Player player, TopPlayers plugin) {
        this(player, player.getWorld(), plugin);
    }

    protected Record(Player player, World world, TopPlayers plugin) {
        this.playerUUID = player.getUniqueId();
        this.worldUUID = world.getUID();
        this.plugin = plugin;
        this.startTime = System.currentTimeMillis();
        records.add(this);
    }

    protected void saveRecord(UUID worldUUID) {
        plugin.getConfig().set("players." + playerUUID.toString() + "." + worldUUID, this.getTotalTime());
        plugin.saveConfig();
    }

    protected long getTotalTime() {
        String path = "players." + playerUUID.toString() + "." + worldUUID.toString();
        return (plugin.getConfig().isSet(path) ? plugin.getConfig().getLong(path) : 0)
                + System.currentTimeMillis() - startTime;
    }

    protected UUID getPlayerUUID() {
        return this.playerUUID;
    }

    public UUID getWorldUUID() {
        return this.worldUUID;
    }

    protected static Record getRecord(UUID playerUUID, UUID worldUUD) {
        for (Record record : records) {
            if (record.getPlayerUUID().equals(playerUUID)
                    && worldUUD.equals(record.getWorldUUID())) {
                return record;
            }
        }
        return null;
    }

    protected static Record getRecord(int placement, UUID worldUUID) {
        Record[] top = new Record[placement];
        for (Record record : records) {
            if (record.getWorldUUID().equals(worldUUID)) {
                for (int i = 0; i < top.length; i++) {
                    if (top[i] == null || record.getTotalTime() > top[i].getTotalTime()) {
                        top[i] = record;
                        break;
                    }
                }
            }
        }
        return top[top.length - 1];
    }

    protected static void loadRecords(TopPlayers plugin) {
        if (plugin.getConfig().isSet("players")) {
            for (String uuid : plugin.getConfig().getConfigurationSection("players").getKeys(false)) {
                for (String worldUUID : plugin.getConfig().getConfigurationSection("players." + uuid).getKeys(false)) {
                    records.add(new Record(Bukkit.getPlayer(UUID.fromString(uuid)),
                            Bukkit.getWorld(UUID.fromString(worldUUID)), plugin));
                }
            }
        }
    }
}
