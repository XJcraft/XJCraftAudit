package org.jim.bukkit.audit.apply;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.jim.bukkit.audit.AuditPlugin;
import org.jim.bukkit.audit.Status;
import org.jim.bukkit.audit.cmds.ICmd;

public class Give extends ICmd {

	public Give() {
		super("give", "[player] [code]", "设置玩家状态");
	}

	@Override
	public boolean onCommand(CommandSender sender, String[] args) {
		if (args.length == 0)
			return false;
		OfflinePlayer argePlayer = Bukkit.getOfflinePlayer(args[0]);
		if (argePlayer == null) {
			sender.sendMessage(ChatColor.RED + "该玩家不存在或未在线");
			return true;
		}
		Status s = null;
		try {
			s = (args != null && args.length > 1)
					? Status.get(Integer.valueOf(args[1])) : Status.UNAPPLIED;
		} catch (NumberFormatException e) {
			// e.printStackTrace();
		}
		if (s == null) {
			sender.sendMessage(ChatColor.RED + "Status is not found.");
			return true;
		}
		AuditPlugin.getPlugin().helper.setStatus(args[0], s);
		sender.sendMessage("设置" + argePlayer.getName() + "状态为" + s);
		if (!sender.getName().equals(argePlayer.getName())
				&& argePlayer.isOnline())
			argePlayer.getPlayer().sendMessage("你的状态更新为" + s);
		return true;
	}

	@Override
	public String permission() {
		return "xjcraft.give";
	}

}
