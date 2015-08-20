package com.gmail.gogobebe2.topplayers.record;

import com.gmail.gogobebe2.topplayers.TopPlayers;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OfflineRecord extends Record implements Comparable<OfflineRecord> {

    public OfflineRecord(OfflinePlayer player, World world, TopPlayers plugin) {
        super(player, world, plugin);
    }

    @Override
    public int compareTo(OfflineRecord other) {
        return Long.compare(getAccumulatedTime(other.getPlayer(), getWorld(), getPlugin()),
                getAccumulatedTime(getPlayer(), getWorld(), getPlugin()));
    }

    public static OfflineRecord getAtPlacement(int placement, World world, TopPlayers plugin) {
        List<OfflineRecord> sortedRecords = new ArrayList<>();
        for (OfflinePlayer player : Bukkit.getOfflinePlayers())
            sortedRecords.add(new OfflineRecord(player, world, plugin));
        Collections.sort(sortedRecords);
        return sortedRecords.get(placement - 1);
    }
}
