package org.jim.bukkit.audit.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.jim.bukkit.audit.AuditPlugin;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class Lang {

	public static boolean isEmpty(String s) {
		return s == null || "".equals(s.trim());
	}

	public static boolean contains(String[] array, String str) {
		if (array != null)
			for (String s : array) {
				if (str.equalsIgnoreCase(s))
					return true;
			}
		return false;
	}

	public static Block getNearAir(Block block, Integer radio) {
		if (isAir(block))
			return block;
		Location location = block.getLocation().clone();
		/*
		 * for(int i=1;i<10;i++){ // up down if(isAir(location.add(0, i, 0))) return }
		 */
		if (isAir(location.add(0, 1, 0)))
			return location.getBlock();
		return null;
	}

	public static boolean isAir(Block block) {
		return block.getType() == Material.AIR;
	}

	public static boolean isBlockType(Block block, Material m) {
		return block != null && block.getType() == m;
	}

	public static boolean isMaterialType(ItemStack item, Material m) {
		return item != null && item.getType() == m;
	}

	public static boolean isAir(Location location) {
		return isAir(location.getBlock());
	}

	public static Player getRealPlayer(CommandSender sender, String[] args) {
		Player p = (sender instanceof Player) ? (Player) sender : null;
		return (args != null && args.length > 0) ? Bukkit.getPlayer(args[0])
				: p;
	}

	public static int floor(double value) {
		int i = (int) value;
		return value < i ? i - 1 : i;
	}

	/**
	 * 判断一个材料是否是告示牌(插在地上的、或物品)
	 * @param material 被判断的材料
	 * @return 这个材料是否是告示牌
	 */
	public static boolean isSign(Material material) {
		return
				material == Material.ACACIA_SIGN ||
				material == Material.BIRCH_SIGN ||
				material == Material.DARK_OAK_SIGN ||
				material == Material.JUNGLE_SIGN ||
				material == Material.OAK_SIGN ||
				material == Material.SPRUCE_SIGN;
	}

	/**
	 * 判断一个材料是否是告示牌(放在墙上的)
	 * @param material 被判断的材料
	 * @return 这个材料是否是告示牌
	 */
	public static boolean isWallSign(Material material) {
		return
				material == Material.ACACIA_WALL_SIGN ||
				material == Material.BIRCH_WALL_SIGN ||
				material == Material.DARK_OAK_WALL_SIGN ||
				material == Material.JUNGLE_WALL_SIGN ||
				material == Material.OAK_WALL_SIGN ||
				material == Material.SPRUCE_WALL_SIGN;
	}

	public static Block getSignDep(Block sign) {
		if (!isWallSign(sign.getType()))
			return null;
		BlockFace signFace = ((org.bukkit.block.data.type.WallSign) sign.getBlockData()).getFacing();
		return sign.getRelative(signFace.getOppositeFace());
	}

	public static List<Entity> getEntities(World world, Entity ignore,
			double xmin, double ymin, double zmin, double xmax, double ymax,
			double zmax) {
		List<Entity> list = new ArrayList<>();
		for (Entity entity : world.getEntities()) {
			if (ignore.equals(entity))
				continue;
			Location loc = entity.getLocation();
			if (loc.getBlockX() < xmin || loc.getBlockY() < ymin
					|| loc.getBlockZ() < zmin)
				continue;
			if (loc.getBlockX() > xmax || loc.getBlockY() > ymax
					|| loc.getBlockZ() > zmax)
				continue;
			list.add(entity);
		}
		return list;
	}

	public static String getMetaData(Block block, String key) {
		String name = AuditPlugin.getPlugin().getName();
		for (MetadataValue m : block.getMetadata(key)) {
			if (m.getOwningPlugin().getName().equals(name))
				return m.asString();
		}
		return null;
	}

	public static void setMetaData(Block block, String key, String value) {
		if (value == null)
			block.removeMetadata(key, AuditPlugin.getPlugin());
		else
			block.setMetadata(key,
					new FixedMetadataValue(AuditPlugin.getPlugin(), value));
	}

	public static Integer parseInt(String s, Integer def) {
		try {
			return Integer.parseInt(s);
		} catch (NumberFormatException e) {
			return def;
		}
	}

	public static Double parseDouble(String s) {
		try {
			return Double.parseDouble(s);
		} catch (NumberFormatException e) {
			return 0d;
		}
	}

	public static Object first(Object obj) {
		if (null == obj)
			return obj;

		if (obj instanceof Collection<?>) {
			Iterator<?> it = ((Collection<?>) obj).iterator();
			return it.hasNext() ? it.next() : null;
		}

		if (obj.getClass().isArray())
			return Array.getLength(obj) > 0 ? Array.get(obj, 0) : null;

		return obj;
	}

}
