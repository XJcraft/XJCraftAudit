package org.jim.bukkit.audit.entitylimit;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class EntityCheck {
    public void checkEntity(CommandSender sender, Player onlinePlayer, Integer radius) {
        Location location = onlinePlayer.getLocation();
        Map<EntityType, EntityCount> map = new HashMap<>();
        int total = 0;
        for (Entity entity : onlinePlayer.getNearbyEntities(radius, 255.0, radius)) {
            EntityCount count = map.get(entity.getType());
            if (count == null) {
                count = new EntityCount(entity.getType());
            }
            count.add();
            total++;
            map.put(entity.getType(), count);
        }
        Set<EntityCount> set = new TreeSet<>(map.values());
        StringBuilder m = new StringBuilder();
        m.append("------ Entity Info ------\n");
        m.append("Player: ").append(ChatColor.BLUE).append(onlinePlayer.getName())
                .append(ChatColor.WHITE).append('\n');
        m.append(String.format("Location: %s, %d, %d, %d\n",
                location.getWorld().getName(), location.getBlockX(),
                location.getBlockY(), location.getBlockZ()));
        m.append("Radius: ").append(radius).append('\n');
        for (EntityCount c : set) {
            m.append("Entity: ").append(ChatColor.BLUE).append(c.getType().getName())
                    .append(ChatColor.WHITE).append(" Count: ")
                    .append(ChatColor.RED).append(c.getCount()).append(ChatColor.WHITE).append('\n');
        }
        m.append("------    Total Entity: ").append(ChatColor.UNDERLINE).append(total)
                .append(ChatColor.WHITE).append("    ------\n");
        sender.sendMessage(m.toString());
    }

    public void checkAll2(CommandSender sender, Integer radius) {
        StringBuilder m = new StringBuilder();
        m.append("------ Entity Info (Radius:").append(radius).append(") ------\n");
        Set<PlayerEntity> entitys = new TreeSet<>((o1, o2) -> o2.count - o1.count);
        for (Player player : Bukkit.getOnlinePlayers()) {
            entitys.add(new PlayerEntity(player,
                    player.getNearbyEntities(radius, 255.0, radius).size()));
        }
        for (PlayerEntity pe : entitys) {
            m.append("Player: ").append(ChatColor.BLUE).append(pe.player.getName())
                    .append(ChatColor.WHITE).append(" ");
            m.append("EntityCount: ").append(ChatColor.BLUE).append(pe.count)
                    .append(ChatColor.WHITE);
            m.append('\n');
        }
        sender.sendMessage(m.toString());
    }

    static class PlayerEntity {
        Player player;
        Integer count;

        public PlayerEntity(Player player, Integer count) {
            super();
            this.player = player;
            this.count = count;
        }
    }
}
