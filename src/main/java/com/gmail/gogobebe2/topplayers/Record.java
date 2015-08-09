package com.gmail.gogobebe2.topplayers;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.*;

public class Record implements Comparable<Record> {
    private static Set<Record> records = new HashSet<>();

    private UUID playerUUID;
    private UUID worldUUID;
    private TopPlayers plugin;
    private long accumulatedTime;
    private long serverSessionInitialTime;
    private boolean active;

    protected Record(UUID playerUUID, UUID worldUUID, boolean active, TopPlayers plugin) {
        this.playerUUID = playerUUID;
        this.worldUUID = worldUUID;
        this.plugin = plugin;
        this.active = active;
        this.accumulatedTime = 0;
        if (plugin.getConfig().isSet("players." + playerUUID + "." + worldUUID)) {
            this.accumulatedTime = plugin.getConfig().getLong("players." + playerUUID + "." + worldUUID);
        }
        if (active) this.serverSessionInitialTime = System.currentTimeMillis();
        records.add(this);
        plugin.getLogger().info("Opened " + Bukkit.getOfflinePlayer(playerUUID).getName() + "'s record.");
    }

    protected void closeAndSaveRecord() {
        if (this.active) {
            plugin.getConfig().set("players." + playerUUID + "." + worldUUID, getNewAccumulatedTime());
            plugin.saveConfig();
            this.active = false;
        }
    }

    protected void startRecording() {
        if (!active) {
            this.serverSessionInitialTime = System.currentTimeMillis();
            active = true;
        }
    }

    protected long getNewAccumulatedTime() {
        if (active) return accumulatedTime + System.currentTimeMillis() - serverSessionInitialTime;
        else return accumulatedTime;
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
        if (placement > records.size()) return null;
        List<Record> sortedRecords = new ArrayList<>();
        for (Record record : records) if (record.getWorldUUID().equals(worldUUID)) sortedRecords.add(record);
        Collections.sort(sortedRecords);
        return sortedRecords.get(placement - 1);
    }

    protected static void loadRecords(TopPlayers plugin) {
        if (plugin.getConfig().isSet("players")) {
            for (String uuid : plugin.getConfig().getConfigurationSection("players").getKeys(false)) {
                for (String worldUUID : plugin.getConfig().getConfigurationSection("players." + uuid).getKeys(false)) {
                    OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(uuid));
                    new Record(player.getUniqueId(), UUID.fromString(worldUUID), player.isOnline(), plugin);
                    plugin.getLogger().info("Loaded " + player.getName() + "'s record.");
                }
            }
        }
    }

    @Override
    public int compareTo(Record other) {
        return Long.compare(this.getNewAccumulatedTime(), other.getNewAccumulatedTime());
    }
}
