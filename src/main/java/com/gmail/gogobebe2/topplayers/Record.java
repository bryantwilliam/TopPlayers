package com.gmail.gogobebe2.topplayers;

import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Record {
    private static Set<Record> records = new HashSet<>();

    private long startTime;
    private UUID uuid;
    private TopPlayers plugin;

    protected Record(UUID uuid, TopPlayers plugin) {
        this.uuid = uuid;
        this.plugin = plugin;
        this.startTime = System.currentTimeMillis();
    }

    protected void saveRecord() {
        plugin.getConfig().set(uuid.toString(), this.getTotalTime());
        plugin.saveConfig();
    }

    protected long getTotalTime() {
        String entry = uuid.toString();
        return (plugin.getConfig().isSet(entry) ? plugin.getConfig().getLong(entry) : 0)
                + System.currentTimeMillis() - startTime;
    }

    protected UUID getUUID() {
        return this.uuid;
    }

    protected static Record getRecord(Player player) {
        for (Record record : records) {
            if (record.getUUID().equals(player.getUniqueId())) {
                return record;
            }
        }
        return null;
    }

    protected static Record getRecord(int placement) {
        Record[] top = new Record[placement];
        for (Record record : records) {
            for (int i = 0; i < top.length; i++) {
                if (top[i] == null || record.getTotalTime() > top[i].getTotalTime()) {
                    top[i] = record;
                    break;
                }
            }
        }
        return top[top.length - 1];
    }

    protected static void loadRecords(TopPlayers plugin) {
        if (plugin.getConfig().isSet("players")) {
            for (String uuid : plugin.getConfig().getConfigurationSection("players").getKeys(false)) {
                records.add(new Record(UUID.fromString(uuid), plugin));
            }
        }
    }
}
