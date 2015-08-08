package com.gmail.gogobebe2.topplayers;

import org.bukkit.entity.Player;

import java.util.*;

public class Record implements Comparable<Record> {
    private static Set<Record> records = new HashSet<>();

    private UUID playerUUID;
    private UUID worldUUID;
    private TopPlayers plugin;
    private long accumulatedTime;
    private long serverSessionInitialTime;

    protected Record(Player player, TopPlayers plugin) {
        this(player.getUniqueId(), player.getWorld().getUID(), 0, plugin);
    }

    protected Record(UUID playerUUID, UUID worldUUID, long accumulatedTime, TopPlayers plugin) {
        this.playerUUID = playerUUID;
        this.worldUUID = worldUUID;
        this.plugin = plugin;
        this.accumulatedTime = accumulatedTime;
        this.serverSessionInitialTime = System.currentTimeMillis();
        records.add(this);
    }

    protected void saveRecord() {
        plugin.getConfig().set("players." + playerUUID.toString() + "." + worldUUID, getNewAccumulatedTime());
        plugin.saveConfig();
    }

    protected long getNewAccumulatedTime() {
        return accumulatedTime + System.currentTimeMillis() - serverSessionInitialTime;
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
                    long accumulatedTime = plugin.getConfig().getLong("players." + uuid + "." + worldUUID);
                    records.add(new Record(UUID.fromString(uuid),
                            UUID.fromString(worldUUID), accumulatedTime, plugin));
                }
            }
        }
    }

    protected static Set<Record> getRecords() {
        return records;
    }

    @Override
    public int compareTo(Record other) {
        return Long.compare(this.getNewAccumulatedTime(), other.getNewAccumulatedTime());
    }
}
