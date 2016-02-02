package org.jim.bukkit.audit.entitylimit;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class EntityCheck {

	public void checkEntity(CommandSender sender, Player onLinePlayer,
			Integer radius) {
		Location location = onLinePlayer.getLocation();
		Map<EntityType, EntityCount> map = new HashMap<>();
		int total = 0;
		for (Entity entity : onLinePlayer.getNearbyEntities(radius, 255d,
				radius)) {
			EntityCount count = map.get(entity.getType());
			if (count == null) {
				count = new EntityCount(entity.getType());
			}
			count.add();
			total++;
			map.put(entity.getType(), count);
		}
		Set<EntityCount> set = new TreeSet<>(map.values());
		StringBuffer m = new StringBuffer();
		m.append("------ Entity Info ------\n");
		m.append("Player: " + ChatColor.BLUE + onLinePlayer.getName()
				+ ChatColor.WHITE + "\n");
		m.append(String.format("Location: %s, %d, %d, %d\n", location
				.getWorld().getName(), location.getBlockX(), location
				.getBlockY(), location.getBlockZ()));
		m.append("Radius: " + radius + "\n");
		for (EntityCount c : set) {
			m.append("Entity: " + ChatColor.BLUE + "#"
					+ c.getType().getTypeId() + "(" + c.getType() + ")"
					+ ChatColor.WHITE + " Count: " + ChatColor.RED
					+ c.getCount() + ChatColor.WHITE + "\n");
		}
		m.append("------    Total Entity: " + ChatColor.UNDERLINE + total
				+ ChatColor.WHITE + "    ------\n");
		sender.sendMessage(m.toString());
	}

	public void checkAll(CommandSender sender, Integer radius) {
		StringBuffer m = new StringBuffer();
		m.append("------ Entity Info (Radius:" + radius + ") ------\n");
		for (Player player : Bukkit.getOnlinePlayers()) {
			m.append("Player: " + ChatColor.BLUE + player.getName()
					+ ChatColor.WHITE + " ");
			m.append("EntityCount: " + ChatColor.BLUE
					+ player.getNearbyEntities(radius, 255d, radius).size()
					+ ChatColor.WHITE);
			m.append("\n");
		}
		sender.sendMessage(m.toString());
	}

	public void checkAll2(CommandSender sender, Integer radius) {
		StringBuffer m = new StringBuffer();
		m.append("------ Entity Info (Radius:" + radius + ") ------\n");
		// Map<Player,PlayerEntity> map = new HashMap<>();
		Set<PlayerEntity> entitys = new TreeSet<>(new Comparator<PlayerEntity>() {

			@Override
			public int compare(PlayerEntity o1, PlayerEntity o2) {
				return o2.count-o1.count;
			}
		});
		for (Player player : Bukkit.getOnlinePlayers()) {
			entitys.add(new PlayerEntity(player, player.getNearbyEntities(
					radius, 255d, radius).size()));
		}
		for (PlayerEntity pe : entitys) {
			m.append("Player: " + ChatColor.BLUE + pe.player.getName()
					+ ChatColor.WHITE + " ");
			m.append("EntityCount: " + ChatColor.BLUE + pe.count
					+ ChatColor.WHITE);
			m.append("\n");
		}
		sender.sendMessage(m.toString());
	}

	class PlayerEntity {
		Player player;
		Integer count = 0;

		public PlayerEntity(Player player, Integer count) {
			super();
			this.player = player;
			this.count = count;
		}

	}
}
