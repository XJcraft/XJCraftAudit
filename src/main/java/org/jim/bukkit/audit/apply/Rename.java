package org.jim.bukkit.audit.apply;

import java.util.Arrays;
import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.jim.bukkit.audit.cmds.ICmd;

public class Rename extends ICmd {

	public Rename() {
		super("rename", "[name] <lore[,]>", "消耗一根羽毛，给一张纸改名");
		minParam = 1;
	}

	@Override
	public boolean onCommand(CommandSender sender, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("该命令只能玩家使用");
			return true;
		}
		Player player = (Player) sender;
		String name = args[0];
		String lore = null;
		if (args.length > 1)
			lore = args[1];
		rename(player, name, lore);
		return true;
	}

	public static void rename(Player player, String name, String lore) {
		if (!player.getInventory().contains(Material.PAPER)) {
			player.sendMessage(ChatColor.RED + "你手里的厕纸呢!?");
			return;
		}
		if (!player.getInventory().contains(Material.FEATHER)) {
			player.sendMessage(ChatColor.RED + "没有羽毛了!");
			return;
		}
		ItemStack feather = new ItemStack(Material.FEATHER, 1);
		ItemStack paper = new ItemStack(Material.PAPER, 1);
		ItemStack newPaper = new ItemStack(Material.PAPER, 1);
		ItemMeta meta = paper.getItemMeta();
		meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
		if (lore != null) {
			meta.setLore(Arrays.asList(ChatColor
					.translateAlternateColorCodes('&', lore).split(",")));
		}
		newPaper.setItemMeta(meta);
		PlayerInventory inventory = player.getInventory();
		HashMap<Integer, ItemStack> map =
				player.getInventory().removeItem(feather, paper);
		if (!map.isEmpty()) {
			if (!map.containsValue(feather))
				inventory.addItem(feather);
			if (!map.containsValue(paper))
				inventory.addItem(paper);
			player.sendMessage(ChatColor.AQUA + "命名失败,不包含未命名的纸!");
			return;
		}
		player.getInventory().addItem(newPaper);
		player.sendMessage(ChatColor.AQUA + "命名成功!");
	}

	@Override
	public String permission() {
		return "xjcraft.rename";
	}

}
