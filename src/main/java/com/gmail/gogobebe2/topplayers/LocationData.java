package com.gmail.gogobebe2.topplayers;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class LocationData {
    private Location location;
    private TopPlayers plugin;

    public LocationData(Location location, TopPlayers plugin) {
        this(plugin);
        this.location = location;
    }

    public LocationData(String path, TopPlayers plugin) {
        this(plugin);
        World world = Bukkit.getWorld(plugin.getConfig().getString(path + ".world"));
        double x = plugin.getConfig().getDouble(path + ".x");
        double y = plugin.getConfig().getDouble(path + ".y");
        double z = plugin.getConfig().getDouble(path + ".z");
        this.location = new Location(world, x, y, z);
    }

    private LocationData(TopPlayers plugin) {
        this.plugin = plugin;
    }

    public void saveToConfig(String path) {
        plugin.getConfig().set(path + ".world", location.getWorld().getName());
        plugin.getConfig().set(path + ".x", location.getX());
        plugin.getConfig().set(path + ".y", location.getY());
        plugin.getConfig().set(path + ".z", location.getZ());
        plugin.saveConfig();
    }

    public Location getLocation() {
        return this.location;
    }
}
