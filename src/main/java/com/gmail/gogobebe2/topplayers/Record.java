package com.gmail.gogobebe2.topplayers;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.*;

public class Record implements Comparable<Record> {
    private static Set<Record> openRecords = new HashSet<>();

    private UUID playerUUID;
    private UUID worldUUID;
    private TopPlayers plugin;
    private long accumulatedTime = 0;
    private long serverSessionInitialTime = 0;

    protected Record(UUID playerUUID, UUID worldUUID, TopPlayers plugin) {
        this.playerUUID = playerUUID;
        this.worldUUID = worldUUID;
        this.plugin = plugin;
        if (plugin.getConfig().isSet("players." + playerUUID + "." + worldUUID)) {
            this.accumulatedTime = plugin.getConfig().getLong("players." + playerUUID + "." + worldUUID);
        }
        if (getPlayer().isOnline()) this.serverSessionInitialTime = System.currentTimeMillis();
        openRecords.add(this);
        plugin.getLogger().info("Opened " + getPlayer().getName() + "'s record.");
    }

    protected void closeAndSaveRecord() {
        if (getPlayer().isOnline()) {
            plugin.getConfig().set("players." + playerUUID + "." + worldUUID, getAccumulatedTime());
            plugin.saveConfig();
        }
    }

    private OfflinePlayer getPlayer() {
        return Bukkit.getOfflinePlayer(playerUUID);
    }

    protected void startRecording() {
        if (getPlayer().isOnline()) {
            this.serverSessionInitialTime = System.currentTimeMillis();
        }
    }

    protected long getAccumulatedTime() {
        long currentTime = System.currentTimeMillis();
        this.accumulatedTime += currentTime - serverSessionInitialTime;
        this.serverSessionInitialTime = currentTime;
        return this.accumulatedTime;
    }

    protected UUID getPlayerUUID() {
        return this.playerUUID;
    }

    public UUID getWorldUUID() {
        return this.worldUUID;
    }

    protected static Record getRecord(UUID playerUUID, UUID worldUUD) {
        for (Record record : openRecords) {
            if (record.getPlayerUUID().equals(playerUUID)
                    && worldUUD.equals(record.getWorldUUID())) {
                return record;
            }
        }
        return null;
    }

    protected static Record getRecord(int placement, UUID worldUUID) {
        if (placement > openRecords.size()) return null;
        List<Record> sortedRecords = new ArrayList<>();
        for (Record record : openRecords) if (record.getWorldUUID().equals(worldUUID)) sortedRecords.add(record);
        Collections.sort(sortedRecords);
        return sortedRecords.get(placement - 1);
    }

    protected static void loadRecords(TopPlayers plugin) {
        if (plugin.getConfig().isSet("players")) {
            for (String uuid : plugin.getConfig().getConfigurationSection("players").getKeys(false)) {
                for (String worldUUID : plugin.getConfig().getConfigurationSection("players." + uuid).getKeys(false)) {
                    plugin.getLogger().info("Loaded "
                            + new Record(UUID.fromString(uuid),
                            UUID.fromString(worldUUID), plugin).getPlayer().getName() + "'s record.");
                }
            }
        }
    }

    @Override
    public int compareTo(Record other) {
        return Long.compare(this.getAccumulatedTime(), other.getAccumulatedTime());
    }
}
