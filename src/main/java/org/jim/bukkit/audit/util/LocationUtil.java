package org.jim.bukkit.audit.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.MemorySection;
import org.jim.bukkit.audit.LocationRef;

public class LocationUtil {

	public static void putLocation(MemorySection yaml, Location loc,
			String prefix) {
		if (loc == null)
			return;
		yaml.set(prefix + ".world", loc.getWorld().getName());
		yaml.set(prefix + ".x", loc.getBlockX());
		yaml.set(prefix + ".y", loc.getBlockY());
		yaml.set(prefix + ".z", loc.getBlockZ());
		yaml.set(prefix + ".pitch", loc.getPitch());
		yaml.set(prefix + ".yaw", loc.getYaw());
	}

	public static void putLocation(MemorySection yaml, LocationRef loc,
			String prefix) {
		if (loc == null)
			return;
		yaml.set(prefix + ".world", loc.getWorld());
		yaml.set(prefix + ".x", loc.getX());
		yaml.set(prefix + ".y", loc.getY());
		yaml.set(prefix + ".z", loc.getZ());
	}

	public static Location getLocation(MemorySection yaml, String prefix) {
		String worldName = (String) yaml.get(prefix + ".world");
		if (worldName == null) {
			return null;
		}
		World world = Bukkit.getWorld(worldName);
		int x = yaml.getInt(prefix + ".x");
		int y = yaml.getInt(prefix + ".y");
		int z = yaml.getInt(prefix + ".z");
		float pitch = (float) yaml.getDouble(prefix + ".pitch");
		float yaw = (float) yaml.getDouble(prefix + ".yaw");
		return new Location(world, x, y, z, yaw, pitch);
	}

	public static LocationRef getLocationRef(MemorySection yaml,
			String prefix) {
		String worldName = (String) yaml.get(prefix + ".world");
		// if (worldName == null) {
		// return null;
		// }
		// World world = Bukkit.getWorld(worldName);
		double x = yaml.getDouble(prefix + ".x");
		double y = yaml.getDouble(prefix + ".y");
		double z = yaml.getDouble(prefix + ".z");
		return new LocationRef(worldName, x, y, z);
	}

	public static String toString(Location location) {
		if (location == null) {
			return "[null Location]";
		}
		return toString(new LocationRef(location));
	}

	public static String toString(LocationRef location) {
		if (location == null) {
			return "[null Location]";
		}
		return location.toString();
	}
}
