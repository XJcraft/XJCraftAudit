package org.jim.bukkit.audit;

import java.util.LinkedHashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

public class LocationRef implements ConfigurationSerializable {

    private String world;
    private double x;
    private double y;
    private double z;

    private Location ref;

    public LocationRef(String world, double x, double y, double z) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public LocationRef(Location location) {
        this(location.getWorld().getName(), location.getX(), location.getY(),
                location.getZ());
    }

    public String getWorld() {
        return world;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public Location getRef() {
        World w = Bukkit.getWorld(world);
        if (w == null) {
            return null;
        }
        if (ref == null) {
            ref = new Location(w, x, y, z);
        }
        return ref;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> result = new LinkedHashMap<String, Object>();
        result.put("world", world);
        result.put("x", x);
        result.put("y", y);
        result.put("z", z);
        return result;
    }

    public static LocationRef deserialize(Map<String, Object> args) {
        String world = args.get("world").toString();
        double x = ((Number) args.get("x")).doubleValue();
        double y = ((Number) args.get("y")).doubleValue();
        double z = ((Number) args.get("z")).doubleValue();
        return new LocationRef(world, x, y, z);
    }

    @Override
    public String toString() {
        return ChatColor.AQUA + "[" + world == null ? "null" : world + "(" + x + ", " + y + ", " + z + ")]";
    }
}
